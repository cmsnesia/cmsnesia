package id.or.gri.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import id.or.gri.model.AuthDto;
import id.or.gri.model.request.RefreshTokenRequest;
import id.or.gri.model.request.TokenRequest;
import id.or.gri.model.response.TokenResponse;
import id.or.gri.service.AuthService;
import id.or.gri.service.TokenService;
import id.or.gri.service.util.Crypto;
import id.or.gri.service.util.Json;
import id.or.gri.service.util.TokenInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

    private static final String TOKEN_TYPE = "Bearer";
    private static final String USERNAME = "username";
    private static final String SESSION_ATTRIBUTE = "sa";
    private static final String SESSION_ID = "sid";
    private static final String ESID = "esid";

    private final AuthService authService;
    private final TokenInfo tokenInfo;
    private final Json json;
    private final Crypto crypto;

    public TokenServiceImpl(AuthService authService, TokenInfo tokenInfo, Json json, Crypto crypto) {
        this.authService = authService;
        this.tokenInfo = tokenInfo;
        this.json = json;
        this.crypto = crypto;
    }

    @Override
    public Mono<AuthDto> decode(TokenResponse tokenResponse) {
        String token = tokenResponse.getAccessToken();
        if (token == null) {
            return Mono.empty();
        }
        Claims claims = getClaims(token);
        if (claims == null) {
            return Mono.empty();
        }

        if (claims.containsKey(USERNAME)) {
            log.info("You are using refresh token, please provide access token instead.");
            return Mono.empty();
        }

        try {
            String base64Json = claims.get(SESSION_ATTRIBUTE, String.class);
            String jsonString = new String(Base64.getDecoder().decode(base64Json));
            AuthDto authDto = json.readValue(jsonString, new TypeReference<AuthDto>() {
            });
            return Mono.just(authDto);
        } catch (Exception e) {
            return Mono.empty();
        }
    }

    @Override
    public Mono<TokenResponse> encode(TokenRequest tokenRequest) {
        return authService.findByUsername(tokenRequest.getUsername()).map(authDto -> {
            return doEncode(authDto);
        }).defaultIfEmpty(new TokenResponse("", "", ""));
    }

    @Override
    public Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse) {
        String token = tokenResponse.getRefreshToken();
        if (token == null) {
            return Mono.empty();
        }

        Claims claims = getClaims(token);
        if (claims == null) {
            return Mono.empty();
        }

        if (!claims.containsKey(USERNAME)) {
            log.info("Invalid refresh token, maybe access token.");
            return Mono.empty();
        }

        return encode(new TokenRequest(claims.get(USERNAME, String.class), ""));
    }

    private TokenResponse doEncode(AuthDto authDto) {
        try {
            String base64Json = Base64.getEncoder().encodeToString(json.writeValueAsString(authDto).getBytes("UTF-8"));
            String sessionId = Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes("UTF-8"));
            String esid = Base64.getEncoder().encodeToString(crypto.encrypt(sessionId));

            Date creationTime = new Date();
            String accessToken = Jwts.builder()
                    .setIssuer(tokenInfo.getIssuer())
                    .setIssuedAt(creationTime)
                    .setExpiration(new Date(creationTime.getTime() + tokenInfo.getAccessTokenExparation()))
                    .signWith(SignatureAlgorithm.HS512, tokenInfo.getSecret())
                    .claim(SESSION_ID, sessionId)
                    .claim(ESID, esid)
                    .claim(SESSION_ATTRIBUTE, base64Json)
                    .compact();

            String refreshToken = Jwts.builder()
                    .setIssuer(tokenInfo.getIssuer())
                    .setIssuedAt(creationTime)
                    .setExpiration(new Date(creationTime.getTime() + tokenInfo.getRefreshTokenExparation()))
                    .signWith(SignatureAlgorithm.HS512, tokenInfo.getSecret())
                    .claim(USERNAME, authDto.getUsername())
                    .claim(SESSION_ID, sessionId)
                    .claim(ESID, esid)
                    .compact();
            return new TokenResponse(accessToken, refreshToken, TOKEN_TYPE);
        } catch (Exception e) {
            return new TokenResponse("", "", "");
        }
    }

    private Claims getClaims(String token) {
        if (token == null) {
            return null;
        }
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(tokenInfo.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.info("Invalid token, couldn't extract token: {}", e);
            return null;
        }

        if (claims == null) {
            log.info("Invalid token: couldn't extract token");
            return null;
        }

        String sessionId = claims.get(SESSION_ID, String.class);
        String esid;

        try {
            esid = crypto.decrypt(Base64.getDecoder().decode(claims.get(ESID, String.class)));
        } catch (Exception e) {
            log.info("Couldn't decrypt token session ID.");
            return null;
        }

        if (!sessionId.equals(esid)) {
            log.info("Invalid token session ID.");
            return null;
        }

        String clientIssuer = claims.getIssuer();
        if (!clientIssuer.equals(tokenInfo.getIssuer())) {
            log.info("Invalid issuer: {}", clientIssuer);
            return null;
        }

        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        Date now = new Date();

        if (expiration.after(now) && expiration.before(issuedAt)) {
            log.info("Token expired at {} ", expiration);
            return null;
        }
        return claims;
    }
}
