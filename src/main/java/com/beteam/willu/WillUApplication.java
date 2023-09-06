package com.beteam.willu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableScheduling  // 스케줄러 기능 활성화
@SpringBootApplication
@EnableWebSecurity
@EnableJpaAuditing
@EnableAsync
public class WillUApplication {
    public static void main(String[] args) {
        SpringApplication.run(WillUApplication.class, args);
    }

}
