package ru.vladimir.config.spring;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.pool.HikariPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.vladimir.config.properties.PersistenceProperties;
import ru.vladimir.storage.ClickhouseConnectionPool;
import ru.vladimir.storage.entity.SynchronizationEntity;
import ru.vladimir.storage.impl.ClickhouseConnectionPoolImpl;
import ru.vladimir.storage.repository.RepositorySynchronizationEntity;
import ru.yandex.clickhouse.ClickHouseDataSource;

@Configuration
@EntityScan(basePackageClasses = {SynchronizationEntity.class})
@EnableJpaRepositories(basePackageClasses = {RepositorySynchronizationEntity.class})
@EnableTransactionManagement
public class PersistenceConfig {

    @Autowired
    PersistenceProperties config;

    @Bean
    public ClickhouseConnectionPool getDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getClickhouseUrl());
        hikariConfig.setDataSourceClassName("ru.yandex.clickhouse.ClickHouseDataSource");
        hikariConfig.setDataSource(new ClickHouseDataSource(config.getClickhouseUrl()));
        hikariConfig.setDriverClassName("ru.yandex.clickhouse.ClickHouseDriver");
        hikariConfig.getDataSource();
        hikariConfig.setMaximumPoolSize(100);
        return new ClickhouseConnectionPoolImpl(new HikariPool(hikariConfig));
    }
}
