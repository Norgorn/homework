package ru.vladimir.logic;

import ru.vladimir.logic.model.dto.UserActivityDTO;

import java.util.List;

public interface ActivityService {
    List<UserActivityDTO> getActivity(String userId, long from, long to);

    void saveActivity(String userId, long activity);
}
