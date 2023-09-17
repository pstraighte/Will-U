package com.beteam.willu.common.dataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@EnableAutoConfiguration
@Configuration
public class DevConfig {
}
