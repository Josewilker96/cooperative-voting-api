package com.sicred.votacao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class VotacaoApplication {
    public static void main(String[] args) {
        SpringApplication.run(VotacaoApplication.class, args);
    }
}
