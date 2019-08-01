package ru.vladimir.storage;

import java.sql.Connection;

public interface ClickhouseConnectionPool {

    Connection getConnection();
}
