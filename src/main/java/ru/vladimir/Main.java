package ru.vladimir;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import ru.vladimir.config.properties.WebProperties;
import ru.vladimir.config.spring.PersistenceConfig;
import ru.vladimir.logic.impl.SynchronizeServiceImpl;
import ru.vladimir.storage.impl.StorageServiceImpl;
import ru.vladimir.web.impl.SynchronizationHandlers;


@SpringBootApplication(scanBasePackageClasses = {
        WebProperties.class,

        StorageServiceImpl.class,
        SynchronizeServiceImpl.class,

        SynchronizationHandlers.class
})
@EnableAutoConfiguration()
@EnableJpaAuditing
@Import({PersistenceConfig.class})
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
