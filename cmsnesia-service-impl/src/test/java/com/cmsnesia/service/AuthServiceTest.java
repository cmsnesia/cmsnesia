package com.cmsnesia.service;

import com.cmsnesia.assembler.AuthAssembler;
import com.cmsnesia.domain.Auth;
import com.cmsnesia.model.AuthDto;
import com.cmsnesia.model.EmailDto;
import com.cmsnesia.service.repository.AuthRepo;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = ServiceApplicationTest.class)
public class AuthServiceTest {

  @Autowired private AuthService authService;

  @Autowired private AuthAssembler authAssembler;

  @MockBean private AuthRepo authRepo;

  private AuthDto dto;

  @Before
  public void init() {
    this.dto =
        AuthDto.builder()
            .id(UUID.randomUUID().toString())
            .username("ardikars")
            .password("123456")
            .fullName("Ardika Rommy Sanjaya")
            .emails(
                Arrays.asList(
                        EmailDto.builder()
                            .address("ardikars@gmail.com")
                            .status("VERIFIED")
                            .types(
                                Arrays.asList("NOTIFICATION", "PROMOTION").stream()
                                    .collect(Collectors.toSet()))
                            .build(),
                        EmailDto.builder()
                            .address("contact@ardikars.com")
                            .status("VERIFIED")
                            .types(Arrays.asList("PRIMARY").stream().collect(Collectors.toSet()))
                            .build())
                    .stream()
                    .collect(Collectors.toSet()))
            .roles(Arrays.asList("ADMIN").stream().collect(Collectors.toSet()))
            .build();
  }

  @Test
  public void addTest() {
    Auth entity = authAssembler.fromDto(dto);
    Mockito.when(authRepo.save(Mockito.any())).thenReturn(Mono.just(entity));
    authService.add(dto, dto);
  }

  @Test
  public void editTest() {
    Auth entity = authAssembler.fromDto(dto);
    Mockito.when(authRepo.findById(Mockito.anyString())).thenReturn(Mono.just(entity));
    Mockito.when(authRepo.save(Mockito.any())).thenReturn(Mono.just(entity));
    authService.edit(dto, dto);
  }

  @Test
  public void deleteTest() {
    Auth entity = authAssembler.fromDto(dto);
    Mockito.when(authRepo.findById(Mockito.anyString())).thenReturn(Mono.just(entity));
    Mockito.when(authRepo.save(Mockito.any())).thenReturn(Mono.just(entity));
    authService.delete(dto, dto);
  }

  @Test
  public void findTest() {
    Auth entity = authAssembler.fromDto(dto);
    Pageable pageable = PageRequest.of(0, 1);
    Mockito.when(authRepo.find(Mockito.any(), Mockito.any(), Mockito.any()))
        .thenReturn(Flux.just(entity));
    authService.find(dto, dto, pageable);
  }

  @Test
  public void findByUsername() {
    Auth entity = authAssembler.fromDto(dto);
    Mockito.when(authRepo.findByUsername("ardikars")).thenReturn(Mono.just(entity));
    authService.findByUsername("ardikars");
  }
}
