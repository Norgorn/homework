package ru.vladimir.storage.impl;

import com.zaxxer.hikari.pool.HikariPool;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import ru.vladimir.storage.ClickhouseConnectionPool;

import java.sql.Connection;

@AllArgsConstructor
public class ClickhouseConnectionPoolImpl implements ClickhouseConnectionPool {


    HikariPool pool;

    @Override
    @SneakyThrows
    public Connection getConnection() {
        return pool.getConnection();
    }
}
