package ru.vladimir.storage;

import ru.vladimir.logic.model.dto.CountPerCountryDTO;
import ru.vladimir.logic.model.dto.UserActivityDTO;
import ru.vladimir.logic.model.dto.UserWithMoneyDTO;
import ru.vladimir.storage.entity.SynchronizationEntity;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

public interface StorageService {
    @Transactional(REQUIRES_NEW)
    Optional<SynchronizationEntity> readSynchronization(String id);

    <T> T doWithSynchronization(String id, Function<String, SynchronizationEntity> entityFactory,
                                Function<SynchronizationEntity, T> cleanFunction);

    Map<String, List<UserWithMoneyDTO>> getTopSynchronisationsByMoney(int count);

    List<CountPerCountryDTO> getNewSynchronizationsPerCountry(long from, long to);

    List<UserActivityDTO> getActivity(String userId, long from, long to);

    void saveActivity(String userId, long activity);
}
