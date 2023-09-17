package com.beteam.willu.common.dataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Slf4j
@Profile("production")
@Configuration
public class DataSourceConfig {

    private final String MAIN_DATA_SOURCE = "mainDataSource";
    private final String REPLICA_DATA_SOURCE = "replicaDataSource";

    @Primary
    @Bean(MAIN_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource mainDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }

    @Profile("production")
    @Bean(REPLICA_DATA_SOURCE)
    @ConfigurationProperties(prefix = "replica")
    public DataSource replicaDataSource() {
        return DataSourceBuilder
                .create()
                .build();
    }
}
