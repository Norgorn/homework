package ru.vladimir.config.properties;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class WebProperties {

    @Value("${web.max-sync-data}")
    private int maxSyncSize;
}
