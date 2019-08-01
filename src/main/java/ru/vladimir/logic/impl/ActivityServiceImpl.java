package ru.vladimir.logic.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladimir.logic.model.dto.UserActivityDTO;
import ru.vladimir.storage.StorageService;

import java.util.List;

@Service
public class ActivityServiceImpl implements ru.vladimir.logic.ActivityService {

    @Autowired
    StorageService storage;

    @Override
    public List<UserActivityDTO> getActivity(String userId, long from, long to) {
        return storage.getActivity(userId, from, to);
    }

    @Override
    public void saveActivity(String userId, long activity) {
        storage.saveActivity(userId, activity);
    }
}
