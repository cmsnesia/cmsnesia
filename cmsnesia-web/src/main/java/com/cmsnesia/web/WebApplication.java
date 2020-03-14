package com.cmsnesia.web;

import ch.sbb.esta.openshift.gracefullshutdown.GracefulshutdownSpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication(scanBasePackages = "com.cmsnesia")
@EntityScan("com.cmsnesia")
@EnableReactiveMongoRepositories("com.cmsnesia")
public class WebApplication {

  public static void main(String[] args) {
    GracefulshutdownSpringApplication.run(WebApplication.class, args);
  }
}
