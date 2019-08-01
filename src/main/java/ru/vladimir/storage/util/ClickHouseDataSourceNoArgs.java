package ru.vladimir.storage.util;


import ru.yandex.clickhouse.ClickHouseDataSource;

public class ClickHouseDataSourceNoArgs extends ClickHouseDataSource {

    public ClickHouseDataSourceNoArgs() {
        super("");
    }
}
