package ru.vladimir.web.impl;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladimir.config.properties.WebProperties;
import ru.vladimir.logic.ActivityService;
import ru.vladimir.logic.SynchronizeService;
import ru.vladimir.logic.error.LogicAwareException;
import spark.Request;

import javax.annotation.PostConstruct;
import javax.servlet.MultipartConfigElement;
import java.io.InputStream;

import static spark.Spark.*;

@Service
@Log4j2
public class SynchronizationHandlers {

    @Autowired
    SynchronizeService syncService;
    @Autowired
    WebProperties webProperties;
    @Autowired
    ActivityService activityService;

    @PostConstruct
    public void init() {
        initHandlers();
        initErrorHandlers();
    }

    private void initHandlers() {
        get("/sync/:id", (request, res) -> syncService.checkedGetSynchronizationRawData(request.params(":id")));
        post("/sync/:id", (request, res) -> {
            String json = readMultipartSync(request);
            syncService.updateSynchronization(request.params(":id"), json);
            return syncService.checkedGetSynchronizationRawData(request.params(":id"));
        });

        get("/stats/new", (request, res) ->
                syncService.getNewPerCountry(longP(request, "from"), longP(request, "to")
                ));
        get("/stats/top", (request, res) -> syncService.getTopByMoney(intP(request, "count")));

        get("/activity/:id", (request, res) -> activityService.getActivity(request.params(":id"),
                longP(request, "from"), longP(request, "to")
        ));
        post("/activity/:id", (request, res) -> {
            activityService.saveActivity(request.params(":id"), longP(request, "value"));
            return "OK";
        });
    }

    private void initErrorHandlers() {
        exception(LogicAwareException.class, (exception, request, response) -> {
            log.error("Error while handling request " + request.queryString(), exception);
            response.status(exception.getCode().getCode());
            response.body(exception.getMessage());
        });
    }

    @SneakyThrows
    private String readMultipartSync(Request request) {
        String json;
        MultipartConfigElement config = new MultipartConfigElement("temp",
                webProperties.getMaxSyncSize(), webProperties.getMaxSyncSize(),
                9999999 // we don't wan anything on the disk, so set this to large value
        );
        request.attribute("org.eclipse.jetty.multipartConfig", config);
        try (InputStream is = request.raw().getPart("sync_data").getInputStream()) {
            json = IOUtils.toString(is, "UTF8");
        }
        return json;
    }

    private long longP(Request request, String name) {
        return Long.parseLong(request.queryParams(name));
    }

    private int intP(Request request, String name) {
        return Integer.parseInt(request.queryParams(name));
    }
}
