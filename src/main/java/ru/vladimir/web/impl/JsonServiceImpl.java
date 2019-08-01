package ru.vladimir.web.impl;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import ru.vladimir.logic.model.dto.SynchronizationDTO;
import ru.vladimir.web.JsonService;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
public class JsonServiceImpl implements JsonService {

    private final Gson gson = new Gson();

    @Override
    public SynchronizationDTO parseSynchronization(String json) {
        return checkNotNull(gson.fromJson(json, SynchronizationDTO.class), "wrong json %s", json);
    }
}
