package com.beteam.willu.common.dataSource;


import lombok.extern.slf4j.Slf4j;
import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Profile("production")
@Slf4j
@EnableJpaRepositories(
        basePackages = "com.beteam.willu"
)

@Configuration
public class RoutingDataSourceConfig {

    private final String ROUTING_DATA_SOURCE = "routingDataSource";
    private final String MAIN_DATA_SOURCE = "mainDataSource";
    private final String REPLICA_DATA_SOURCE = "replicaDataSource";
    private final String DATA_SOURCE = "dataSource";
    private final String NOROUTE_DATA_SOURCE = "norouteDataSource";

    @Value("${spring.jpa.properties.hibernate.format_sql}")
    String formatSQL;
    @Value("${spring.jpa.properties.hibernate.show_sql}")
    String showSQL;
    @Value("${spring.jpa.hibernate.ddl-auto}")
    String ddl;

    @Profile("production")
    @Bean(ROUTING_DATA_SOURCE)
    public DataSource routingDataSource(
            @Qualifier(MAIN_DATA_SOURCE) final DataSource mainDataSource,
            @Qualifier(REPLICA_DATA_SOURCE) final DataSource replicaDataSource) {

        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> dataSourceMap = new HashMap<>();
        dataSourceMap.put(DataSourceType.MAIN, mainDataSource);
        dataSourceMap.put(DataSourceType.REPLICA, replicaDataSource);

        routingDataSource.setTargetDataSources(dataSourceMap);
        routingDataSource.setDefaultTargetDataSource(mainDataSource);

        return routingDataSource;
    }

    @Profile("dev")
    @Bean("dataSource")
    public DataSource noroutingDataSource(
            @Qualifier(MAIN_DATA_SOURCE) final DataSource devDataSource) {
        return devDataSource;
    }

    @Profile("production")
    @Bean(DATA_SOURCE)
    public DataSource dataSource(
            @Qualifier(ROUTING_DATA_SOURCE) DataSource routingDataSource) {
        return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Bean("entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
            @Qualifier(DATA_SOURCE) DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory
                = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource);
        entityManagerFactory.setPackagesToScan("com.beteam.willu");
        entityManagerFactory.setJpaVendorAdapter(this.jpaVendorAdapter());
        entityManagerFactory.setPersistenceUnitName("entityManager");
        entityManagerFactory.setJpaProperties(additionalProperties());
        return entityManagerFactory;
    }

    private JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQLDialect");
        return hibernateJpaVendorAdapter;
    }

    @Bean("transactionManager")
    public PlatformTransactionManager platformTransactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        return jpaTransactionManager;
    }

    private Properties additionalProperties() {
        Properties properties = new Properties();
        properties.setProperty(AvailableSettings.DIALECT, org.hibernate.dialect.MySQLDialect.class.getName());
        properties.setProperty(AvailableSettings.SHOW_SQL, showSQL);
        properties.setProperty(AvailableSettings.FORMAT_SQL, formatSQL);
        properties.setProperty(AvailableSettings.HBM2DDL_AUTO, ddl);
        // Naming Strategy 설정 (여기서는 ImprovedNamingStrategy 사용)
        properties.setProperty(AvailableSettings.PHYSICAL_NAMING_STRATEGY, org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy.class.getName());

        //암시적 전략은 default가 jpa properties.setProperty(AvailableSettings.IMPLICIT_NAMING_STRATEGY, org.hibernate.boot.model.naming.implicitnamingst"org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy");
        return properties;
    }
}