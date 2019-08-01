package ru.vladimir.storage.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import ru.vladimir.storage.util.TimestampToLongConverter;

import javax.persistence.*;


@Data
@NoArgsConstructor
@Entity(name = "sync")
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(callSuper = false)
@TypeDef(
        name = "jsonb",
        typeClass = JsonBinaryType.class
)
public class SynchronizationEntity {

    @Id
    private String id;

    private long money;

    @Column(nullable = false)
    private String countryCode;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String rawJson;

    @Column(nullable = false, updatable = false)
    @CreatedDate
    @Convert(converter = TimestampToLongConverter.class)
    private long createdAt;

    @Column(nullable = false)
    @LastModifiedDate
    @Convert(converter = TimestampToLongConverter.class)
    private long updatedAt;

    public SynchronizationEntity(String id) {
        this.id = id;
    }
}
