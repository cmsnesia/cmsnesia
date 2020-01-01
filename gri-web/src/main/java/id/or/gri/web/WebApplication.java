package id.or.gri.web;

import id.or.gri.model.AuthDto;
import id.or.gri.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import java.util.Arrays;
import java.util.stream.Collectors;

@SpringBootApplication(scanBasePackages = "id.or.gri")
@EntityScan("id.or.gri")
@EnableReactiveMongoRepositories("id.or.gri")
public class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }

}