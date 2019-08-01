package ru.vladimir.web;

import ru.vladimir.logic.model.dto.SynchronizationDTO;

public interface JsonService {
    SynchronizationDTO parseSynchronization(String json);
}
