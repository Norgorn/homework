package ru.vladimir.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class PersistenceProperties {

    @Value("${data.clickhouse.url}")
    private String clickhouseUrl;
}
