package ru.vladimir.storage.util;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;

public class TimestampToLongConverter implements AttributeConverter<Long, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(Long attribute) {
        return new Timestamp(attribute);
    }

    @Override
    public Long convertToEntityAttribute(Timestamp dbData) {
        return dbData.getTime();
    }
}
