package ru.vladimir.logic.impl;

import com.google.gson.JsonSyntaxException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import ru.vladimir.logic.error.LogicAwareException;
import ru.vladimir.logic.model.dto.SynchronizationDTO;
import ru.vladimir.storage.StorageService;
import ru.vladimir.storage.entity.SynchronizationEntity;
import ru.vladimir.web.impl.JsonServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@SuppressWarnings("unused")
@RunWith(MockitoJUnitRunner.class)
public class SynchronizeServiceImplTest {

    @Mock
    StorageService storage;

    @Spy
    JsonServiceImpl json = new JsonServiceImpl();

    @InjectMocks
    SynchronizeServiceImpl sut = new SynchronizeServiceImpl();

    @Test(expected = LogicAwareException.class)
    public void updateSynchronization_whenWrongUUID_thenException() {
        sut.updateSynchronization("not-uuid", "{}");
    }

    @Test(expected = JsonSyntaxException.class)
    public void updateSynchronization_whenJson_thenException() {
        sut.updateSynchronization("651368ac-9c9b-4e5b-97f8-490fc315e4a9", "{sgdfgdfgdf}");
    }

    @Test(expected = LogicAwareException.class)
    public void updateSynchronization_whenNegativeMoney_thenException() {
        sut.updateSynchronization("651368ac-9c9b-4e5b-97f8-490fc315e4a9", "{\"money\":-10}");
    }

    @Test(expected = LogicAwareException.class)
    public void updateSynchronization_whenNoMoney_thenException() {
        sut.updateSynchronization("651368ac-9c9b-4e5b-97f8-490fc315e4a9", "{}");
    }

    @Test(expected = LogicAwareException.class)
    public void updateSynchronization_whenNoCountry_thenException() {
        sut.updateSynchronization("651368ac-9c9b-4e5b-97f8-490fc315e4a9", "{\"money\":10, \"somekey\":1235465}");
    }

    @Test
    public void updateSynchronization_whenCorrect_thenSave() {
        String userId = "651368ac-9c9b-4e5b-97f8-490fc315e4a9";
        sut.updateSynchronization(userId,
                "{\"money\":10, \"country\":\"ru\", \"somekey\":1235465}");
        verify(storage).doWithSynchronization(eq(userId), any(), any());
    }

    @Test
    public void updateSynchronization_whenPassedEntity_thenUpdate() {
        String rawJson = "{can_be_anything}";
        SynchronizationDTO dto = new SynchronizationDTO(456, "some_country_code");
        SynchronizationEntity entity = new SynchronizationEntity("123");

        sut.updateSynchronization(rawJson, dto, entity);

        assertEquals(dto.getMoney(), entity.getMoney());
        assertEquals(dto.getCountry(), entity.getCountryCode());
        assertEquals(rawJson, entity.getRawJson());
    }

    @Test(expected = LogicAwareException.class)
    public void checkValidUUID_whenWrong_thanException() {
        sut.checkValidUUID("adsdsd");
    }

    @Test
    public void checkValidUUID_whenCorrect_thenNoException() {
        sut.checkValidUUID("651368ac-9c9b-4e5b-97f8-490fc315e4a9");
    }
}