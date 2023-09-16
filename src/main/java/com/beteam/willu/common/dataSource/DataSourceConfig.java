package com.beteam.willu.common.dataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Slf4j
@Configuration
public class DataSourceConfig {

    private final String MASTER_DATA_SOURCE = "masterDataSource";
    private final String SLAVE_DATA_SOURCE = "slaveDataSource";

    @Primary
    @Bean(MASTER_DATA_SOURCE)
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource masterDataSource() {
        System.out.println("MASTER_DATA_SOURCE 연결 WRITE 작업");
        return DataSourceBuilder
                .create()
                .build();
    }

    @Bean(SLAVE_DATA_SOURCE)
    @ConfigurationProperties(prefix = "replica")
    public DataSource slaveDataSource() {
        System.out.println("SLAVE_DATA_SOURCE 연결 READ 작업");
        return DataSourceBuilder
                .create()
                .build();
    }
}
