package ru.vladimir.storage.impl;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import ru.vladimir.logic.model.dto.CountPerCountryDTO;
import ru.vladimir.logic.model.dto.UserActivityDTO;
import ru.vladimir.logic.model.dto.UserWithMoneyDTO;
import ru.vladimir.storage.ClickhouseConnectionPool;
import ru.vladimir.storage.entity.SynchronizationEntity;
import ru.vladimir.storage.repository.RepositorySynchronizationEntity;
import ru.vladimir.util.ThrowingConsumer;
import ru.vladimir.util.ThrowingFunction;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static javax.transaction.Transactional.TxType.REQUIRES_NEW;

@Service
@Log4j2
public class StorageServiceImpl implements ru.vladimir.storage.StorageService {

    private final static int RETRY_NUMBER = 3;

    @Autowired
    RepositorySynchronizationEntity synchronizationRepository;

    @Autowired
    EntityManager entityManager;

    @Autowired
    ClickhouseConnectionPool clickhouse;

    /**
     * Reads user synchronization data
     *
     * @return DETACHED synchronisation entity or Optional.empty()
     */
    @Override
    @Transactional(REQUIRES_NEW)
    public Optional<SynchronizationEntity> readSynchronization(String id) {
        return synchronizationRepository.findById(id);
    }

    @Override
    public <T> T doWithSynchronization(String id, Function<String, SynchronizationEntity> entityFactory,
                                       Function<SynchronizationEntity, T> cleanFunction) {
        return doWithEntity(synchronizationRepository, id, entityFactory, cleanFunction);
    }


    @Override
    public Map<String, List<UserWithMoneyDTO>> getTopSynchronisationsByMoney(int count) {
        return synchronizationRepository.getTopByCountries(count).stream()
                .map($ -> Pair.of($[0].toString(), new UserWithMoneyDTO($[1].toString(), bigIntRes($, 2))))
                .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));
    }

    @Override
    public List<CountPerCountryDTO> getNewSynchronizationsPerCountry(long from, long to) {
        return synchronizationRepository.findNew(new Date(from), new Date(to)).stream()
                .map($ -> new CountPerCountryDTO($[0].toString(), bigIntRes($, 1)))
                .collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public List<UserActivityDTO> getActivity(String userId, long from, long to) {
        return clickhouse(connection -> {
            String query = "SELECT activity, date " +
                    "FROM home_db.activity " +
                    "WHERE user_id = ? AND date BETWEEN ? AND ? " +
                    // "WHERE user_id = '?' AND date BETWEEN " + from + " AND " + to +
                    "ORDER BY date DESC";
            PreparedStatement statement = connection.prepareStatement(query);
            int i = 1;
            statement.setString(i++, userId);
            statement.setLong(i++, from);
            statement.setLong(i++, to);
            log.debug("Requesting activity for user {} with query {}", userId, statement.toString());
            ResultSet resultSet = statement.executeQuery();
            List<UserActivityDTO> result = new ArrayList<>();
            while (resultSet.next()) {
                UserActivityDTO activity = new UserActivityDTO(resultSet.getLong("activity"), resultSet.getLong("date"));
                result.add(activity);
            }
            return result;
        });
    }

    @Override
    @SneakyThrows
    public void saveActivity(String userId, long activity) {
        clickhouse(connection -> {
            String query = "INSERT INTO home_db.activity(user_id, activity) VALUES (?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, userId);
            statement.setLong(2, activity);
            log.debug("user#{} save activity {} with query {}", userId, activity, statement.toString());
            statement.executeUpdate();
        });
    }

    <K, E, T> T doWithEntity(JpaRepository<E, K> repository,
                             K id, Function<K, E> entityFactory,
                             Function<E, T> cleanFunction) {
        int tryCount = 0;
        OptimisticEntityLockException error = null;
        while (tryCount++ < RETRY_NUMBER) {
            try {
                return doWithEntityOnce(repository, id, entityFactory, cleanFunction);
            } catch (OptimisticEntityLockException e) {
                if (error == null) {error = e; }
            }
        }
        //noinspection ConstantConditions
        throw error; // is always not null to this point
    }

    private long bigIntRes(Object[] array, int i) {
        return ((BigInteger) array[i]).longValue();
    }

    private void clickhouse(ThrowingConsumer<Connection> action) {
        clickhouse(c -> {
            action.accept(c);
            return null;
        });
    }

    @SneakyThrows
    private <T> T clickhouse(ThrowingFunction<Connection, T> action) {
        try (Connection connection = clickhouse.getConnection()) {
            return action.apply(connection);
        }
    }


    @Transactional(REQUIRES_NEW)
    <K, E, T> T doWithEntityOnce(JpaRepository<E, K> repository,
                                 K id, Function<K, E> entityFactory,
                                 Function<E, T> cleanFunction) {
        E entity = repository.findById(id).orElseGet(() -> entityFactory.apply(id));
        T result = cleanFunction.apply(entity);
        repository.save(entity);
        return result;
    }
}
