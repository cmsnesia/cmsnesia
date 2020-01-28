package com.cmsnesia.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.cmsnesia")
public class ServiceApplicationTest {

  public static void main(String[] args) {
    SpringApplication.run(ServiceApplicationTest.class, args);
  }
}
