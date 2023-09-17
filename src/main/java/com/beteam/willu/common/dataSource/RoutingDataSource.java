package com.beteam.willu.common.dataSource;

import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

@Profile("production")
public class RoutingDataSource extends AbstractRoutingDataSource {
    @Override
    protected Object determineCurrentLookupKey() {
        DataSourceType type;
        if (TransactionSynchronizationManager.isCurrentTransactionReadOnly()) {
            type = DataSourceType.REPLICA;
        } else {
            type = DataSourceType.MAIN;
        }
        System.out.println("type = " + type.name());
        return type;

    }
}
