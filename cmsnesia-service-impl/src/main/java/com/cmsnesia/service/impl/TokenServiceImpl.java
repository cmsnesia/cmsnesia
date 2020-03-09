package com.cmsnesia.service.impl;

import com.cmsnesia.assembler.AuthAssembler;
import com.cmsnesia.domain.Auth;
import com.cmsnesia.domain.model.Token;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import com.cmsnesia.service.AuthService;
import com.cmsnesia.service.TokenService;
import com.cmsnesia.service.repository.AuthRepo;
import com.cmsnesia.service.util.Crypto;
import com.cmsnesia.service.util.Json;
import com.cmsnesia.service.util.TokenInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Slf4j
@Component
public class TokenServiceImpl implements TokenService {

  private static final String TOKEN_TYPE = "Bearer";
  private static final String USERNAME = "username";
  private static final String SESSION_ATTRIBUTE = "sa";
  private static final String SESSION_ID = "sid";
  private static final String ESID = "esid";

  private final AuthAssembler authAssembler;
  private final AuthRepo authRepo;
  private final AuthService authService;
  private final TokenInfo tokenInfo;
  private final Json json;
  private final Crypto crypto;

  @Override
  public Mono<AuthDto> validate(TokenResponse tokenResponse) {
    String token = tokenResponse.getAccessToken();
    if (token == null) {
      return Mono.empty();
    }
    return getAccessTokenClaims(token)
        .flatMap(
            claims -> {
              if (claims != null) {
                if (claims.containsKey(USERNAME)) {
                  log.info("You are using refresh token, please provide access token instead.");
                  return Mono.empty();
                }
                try {
                  String base64Json = claims.get(SESSION_ATTRIBUTE, String.class);
                  String jsonString = new String(Base64.getDecoder().decode(base64Json));
                  AuthDto authDto = json.readValue(jsonString, new TypeReference<AuthDto>() {});
                  return Mono.just(authDto);
                } catch (Exception e) {
                  return Mono.empty();
                }
              }
              return Mono.empty();
            });
  }

  @Override
  public Mono<TokenResponse> request(TokenRequest tokenRequest) {
    return authRepo
        .findByUsername(tokenRequest.getUsername())
        .flatMap(
            auth -> {
              if (tokenInfo.getMax() != null && tokenInfo.getMax() > 0) {
                if (auth.getTokens() == null || auth.getTokens().size() < tokenInfo.getMax()) {
                  return doEncode(authAssembler.fromEntity(auth))
                      .flatMap(
                          tokenResponse -> {
                            if (auth.getTokens() == null) {
                              auth.setTokens(new HashSet<>());
                            }
                            auth.getTokens()
                                .add(
                                    new Token(
                                        tokenResponse.getAccessToken(),
                                        tokenResponse.getRefreshToken(),
                                        tokenResponse.getTokenType()));
                            return authRepo
                                .save(auth)
                                .map(
                                    response -> {
                                      return tokenResponse;
                                    });
                          });
                } else {
                  return Mono.empty();
                }
              } else {
                return doEncode(authAssembler.fromEntity(auth));
              }
            });
  }

  @Override
  public Mono<TokenResponse> refresh(RefreshTokenRequest tokenResponse) {
    String token = tokenResponse.getRefreshToken();
    if (token == null) {
      return Mono.empty();
    }
    return getRefreshTokenClaims(token)
        .flatMap(
            claims -> {
              if (claims != null) {
                if (!claims.containsKey(USERNAME)) {
                  log.info("Invalid refresh token, maybe access token.");
                  return Mono.empty();
                }
                if (tokenInfo.getMax() != null && tokenInfo.getMax() > 0) {
                  return authRepo
                      .findByRefreshTokenAndTokenType(AuthDto.builder().build(), token, TOKEN_TYPE)
                      .flatMap(
                          auth -> {
                            auth.getTokens()
                                .removeIf(
                                    o -> {
                                      return o.getRefreshToken().equals(token);
                                    });
                            return authRepo
                                .save(auth)
                                .flatMap(
                                    saved -> {
                                      return request(
                                          new TokenRequest(claims.get(USERNAME, String.class), ""));
                                    });
                          });
                } else {
                  return request(new TokenRequest(claims.get(USERNAME, String.class), ""));
                }
              } else {
                return Mono.empty();
              }
            });
  }

  @Override
  public Mono<String> destroy(TokenResponse tokenResponse) {
    if (tokenInfo.getMax() != null && tokenInfo.getMax() > 0) {
      return authRepo
          .findByAccessTokenAndRefreshTokenAndTokenType(AuthDto.builder().build(), tokenResponse)
          .flatMap(
              auth -> {
                auth.getTokens()
                    .removeIf(
                        token -> {
                          if (token.getAccessToken().equals(tokenResponse.getAccessToken())
                              && token.getRefreshToken().equals(tokenResponse.getRefreshToken())
                              && token.getTokenType().equals(tokenResponse.getTokenType())) {
                            return true;
                          } else {
                            return false;
                          }
                        });
                return authRepo.save(auth).map(saved -> "Success");
              })
          .onErrorReturn("Failed");
    } else {
      return Mono.just("Not supported");
    }
  }

  private Mono<TokenResponse> doEncode(AuthDto authDto) {
    try {
      String base64Json =
          Base64.getEncoder().encodeToString(json.writeValueAsString(authDto).getBytes("UTF-8"));
      String sessionId =
          Base64.getEncoder().encodeToString(UUID.randomUUID().toString().getBytes("UTF-8"));
      String esid = Base64.getEncoder().encodeToString(crypto.encrypt(sessionId));

      Date creationTime = new Date();
      String accessToken =
          Jwts.builder()
              .setIssuer(tokenInfo.getIssuer())
              .setIssuedAt(creationTime)
              .setExpiration(
                  new Date(creationTime.getTime() + tokenInfo.getAccessTokenExparation()))
              .signWith(SignatureAlgorithm.HS512, tokenInfo.getSecret())
              .claim(SESSION_ID, sessionId)
              .claim(ESID, esid)
              .claim(SESSION_ATTRIBUTE, base64Json)
              .compact();

      String refreshToken =
          Jwts.builder()
              .setIssuer(tokenInfo.getIssuer())
              .setIssuedAt(creationTime)
              .setExpiration(
                  new Date(creationTime.getTime() + tokenInfo.getRefreshTokenExparation()))
              .signWith(SignatureAlgorithm.HS512, tokenInfo.getSecret())
              .claim(USERNAME, authDto.getUsername())
              .claim(SESSION_ID, sessionId)
              .claim(ESID, esid)
              .compact();
      return Mono.just(new TokenResponse(accessToken, refreshToken, TOKEN_TYPE));
    } catch (Exception e) {
      return Mono.empty();
    }
  }

  private Mono<Claims> getAccessTokenClaims(String token) {
    if (token == null) {
      return null;
    }
    if (tokenInfo.getMax() != null && tokenInfo.getMax() > 0) {
      return validateAccessTokenState(token);
    }
    return validate(token);
  }

  private Mono<Claims> getRefreshTokenClaims(String token) {
    if (token == null) {
      return null;
    }
    if (tokenInfo.getMax() != null && tokenInfo.getMax() > 0) {
      return validateRefreshTokenState(token);
    }
    return validate(token);
  }

  private Mono<Claims> validate(String token) {
    if (token == null) {
      return Mono.empty();
    }
    Claims claims;
    try {
      claims = Jwts.parser().setSigningKey(tokenInfo.getSecret()).parseClaimsJws(token).getBody();
    } catch (Exception e) {
      log.info("Invalid token, couldn't extract token: {}", e);
      return Mono.empty();
    }

    if (claims == null) {
      log.info("Invalid token: couldn't extract token");
      return Mono.empty();
    }

    String sessionId = claims.get(SESSION_ID, String.class);
    String esid;

    try {
      esid = crypto.decrypt(Base64.getDecoder().decode(claims.get(ESID, String.class)));
    } catch (Exception e) {
      log.info("Couldn't decrypt token session ID.");
      return Mono.empty();
    }

    if (!sessionId.equals(esid)) {
      log.info("Invalid token session ID.");
      return Mono.empty();
    }

    String clientIssuer = claims.getIssuer();
    if (!clientIssuer.equals(tokenInfo.getIssuer())) {
      log.info("Invalid issuer: {}", clientIssuer);
      return Mono.empty();
    }

    Date issuedAt = claims.getIssuedAt();
    Date expiration = claims.getExpiration();
    Date now = new Date();

    if (expiration.after(now) && expiration.before(issuedAt)) {
      log.info("Token expired at {} ", expiration);
      return Mono.empty();
    }
    return Mono.just(claims);
  }

  private Mono<Claims> validateAccessTokenState(String accessToken) {
    return authRepo
        .findByAccessTokenAndType(AuthDto.builder().build(), accessToken, TOKEN_TYPE)
        .flatMap(
            auth -> {
              Set<Token> tokens =
                  auth.getTokens().stream()
                      .filter(token -> validate(token.getAccessToken()) != null)
                      .collect(Collectors.toSet());
              if (auth.getTokens().size() == tokens.size()) {
                return validate(accessToken);
              }
              auth.setTokens(tokens);
              return authRepo
                  .save(auth)
                  .map(Auth::getTokens)
                  .flatMap(token -> validate(accessToken));
            });
  }

  private Mono<Claims> validateRefreshTokenState(String accessToken) {
    return authRepo
        .findByRefreshTokenAndTokenType(AuthDto.builder().build(), accessToken, TOKEN_TYPE)
        .flatMap(
            auth -> {
              Set<Token> tokens =
                  auth.getTokens().stream()
                      .filter(token -> validate(token.getAccessToken()) != null)
                      .collect(Collectors.toSet());
              if (auth.getTokens().size() == tokens.size()) {
                return validate(accessToken);
              }
              auth.setTokens(tokens);
              return authRepo
                  .save(auth)
                  .map(Auth::getTokens)
                  .flatMap(token -> validate(accessToken));
            });
  }
}
