package ru.vladimir.logic;

import ru.vladimir.logic.model.dto.CountPerCountryDTO;
import ru.vladimir.logic.model.dto.UserWithMoneyDTO;

import java.util.List;
import java.util.Map;

public interface SynchronizeService {
    String checkedGetSynchronizationRawData(String userId);

    void updateSynchronization(String userId, String rawJson);

    Map<String, List<UserWithMoneyDTO>> getTopByMoney(int count);

    List<CountPerCountryDTO> getNewPerCountry(long from, long to);
}
