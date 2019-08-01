package ru.vladimir.storage.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.vladimir.storage.entity.SynchronizationEntity;

import java.util.Date;
import java.util.List;

@Repository
public interface RepositorySynchronizationEntity extends JpaRepository<SynchronizationEntity, String> {

    @Query(nativeQuery = true,
           value = "WITH c AS (\n" +
                   "        SELECT DISTINCT (country_code) code\n" +
                   "        FROM homework.public.sync\n" +
                   "     )\n" +
                   " SELECT c.code, res.id, res.money\n" +
                   "    FROM c,\n" +
                   "         LATERAL (SELECT * FROM homework.public.sync t WHERE t.country_code = c.code ORDER BY t.money DESC " +
                   "LIMIT :count) res\n;"
    )
    List<Object[]> getTopByCountries(@Param("count") int countPerCountry);

    @Query(nativeQuery = true,
           value = "SELECT t.country_code, COUNT(*)\n" +
                   "FROM homework.public.sync t\n" +
                   "WHERE created_at BETWEEN :from AND :to\n" +
                   "GROUP BY country_code\n;"
    )
    List<Object[]> findNew(@Param("from") Date from, @Param("to") Date to);
}
