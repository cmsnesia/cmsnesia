package com.cmsnesia.web.controller;

import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.CategoryDto;
import com.cmsnesia.model.EmailDto;
import com.cmsnesia.model.TagDto;
import com.cmsnesia.model.request.RefreshTokenRequest;
import com.cmsnesia.model.request.TokenRequest;
import com.cmsnesia.model.response.TokenResponse;
import com.cmsnesia.service.AuthService;
import com.cmsnesia.service.CategoryService;
import com.cmsnesia.service.TagService;
import com.cmsnesia.service.TokenService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "token")
@Api(value = "Token API", tags = {"Token"})
@Slf4j
public class TokenController {

    private final AuthService authService;
    private final TokenService tokenService;
    private final PasswordEncoder passwordEncoder;

    public TokenController(AuthService authService, TokenService tokenService, PasswordEncoder passwordEncoder) {
        this.authService = authService;
        this.tokenService = tokenService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/request")
    public Mono<ResponseEntity<?>> request(@RequestBody TokenRequest request) {
        return authService.findByUsername(request.getUsername()).map((userDetails) -> {
            if (passwordEncoder.matches(request.getPasssword(), userDetails.getPassword())) {
                return ResponseEntity.ok(tokenService.encode(request));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @PutMapping("/refresh")
    public Mono<ResponseEntity<?>> refresh(@RequestBody RefreshTokenRequest request) {
        return Mono.just(ResponseEntity.ok(tokenService.refresh(request)));
    }

    @PutMapping("/destroy")
    public Mono<ResponseEntity<?>> destroy(@RequestBody TokenResponse tokenResponse) {
        return Mono.just(ResponseEntity.ok(tokenService.destroy(tokenResponse)));
    }


    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TagService tagService;

    @PostMapping("/dummy")
    public Mono<AuthDto> dummy() {
        AuthDto dto = AuthDto.builder()
                .username("ardikars")
                .password(passwordEncoder.encode("123456"))
                .fullName("Ardika Rommy Sanjaya")
                .emails(Arrays.asList(EmailDto.builder()
                                .address("ardikars@gmail.com")
                                .status("VERIFIED")
                                .types(Arrays.asList("NOTIFICATION", "PROMOTION").stream().collect(Collectors.toSet()))
                                .build(),
                        EmailDto.builder()
                                .address("contact@ardikars.com")
                                .status("VERIFIED")
                                .types(Arrays.asList("PRIMARY").stream().collect(Collectors.toSet()))
                                .build()
                ).stream().collect(Collectors.toSet()))
                .roles(Arrays.asList("ADMIN").stream().collect(Collectors.toSet()))
                .build();

        categoryService.add(dto, CategoryDto.builder()
                .name("Inspirasi")
                .build()).block();
        tagService.add(dto, TagDto.builder()
                .name("Hidup")
                .build()).block();
        return authService.add(dto, dto);
    }

}