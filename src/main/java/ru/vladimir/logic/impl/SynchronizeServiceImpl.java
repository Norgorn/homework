package ru.vladimir.logic.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladimir.logic.error.LogicAwareException;
import ru.vladimir.logic.error.LogicErrorCode;
import ru.vladimir.logic.model.dto.CountPerCountryDTO;
import ru.vladimir.logic.model.dto.SynchronizationDTO;
import ru.vladimir.logic.model.dto.UserWithMoneyDTO;
import ru.vladimir.storage.StorageService;
import ru.vladimir.storage.entity.SynchronizationEntity;
import ru.vladimir.web.JsonService;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import static ru.vladimir.logic.error.LogicErrorCode.BAD_REQUEST;
import static ru.vladimir.logic.error.LogicErrorCode.NOT_FOUND;

@Service
public class SynchronizeServiceImpl implements ru.vladimir.logic.SynchronizeService {

    private final Predicate<String> uuidCheck =
            Pattern.compile("[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}").asPredicate();

    @Autowired
    StorageService storage;
    @Autowired
    JsonService json;

    @Override
    public String checkedGetSynchronizationRawData(String userId) {
        checkValidUUID(userId);

        return storage.readSynchronization(userId)
                .map(SynchronizationEntity::getRawJson)
                .orElseThrow(() -> new LogicAwareException(NOT_FOUND, userId));
    }

    @Override
    public void updateSynchronization(String userId, String rawJson) {
        SynchronizationDTO dto = json.parseSynchronization(rawJson);
        if (dto.getMoney() < 1) {
            throw new LogicAwareException(LogicErrorCode.BAD_REQUEST, "wrong money %s", dto.getMoney());
        }
        if (StringUtils.isBlank(dto.getCountry())) {
            throw new LogicAwareException(LogicErrorCode.BAD_REQUEST, "wrong country %s", dto.getCountry());
        }
        storage.doWithSynchronization(userId, SynchronizationEntity::new,
                syncEntity -> updateSynchronization(rawJson, dto, syncEntity));
    }

    @Override
    public Map<String, List<UserWithMoneyDTO>> getTopByMoney(int count) {
        return storage.getTopSynchronisationsByMoney(count);
    }

    @Override
    public List<CountPerCountryDTO> getNewPerCountry(long from, long to) {
        return storage.getNewSynchronizationsPerCountry(from, to);
    }

    void checkValidUUID(String userId) {
        if (!uuidCheck.test(userId)) {
            throw new LogicAwareException(BAD_REQUEST, "Not a valid uuid: %s", userId);
        }
    }

    SynchronizationEntity updateSynchronization(String rawJson, SynchronizationDTO dto, SynchronizationEntity syncEntity) {
        syncEntity.setMoney(dto.getMoney());
        syncEntity.setCountryCode(dto.getCountry());
        syncEntity.setRawJson(rawJson);
        return syncEntity;
    }
}
