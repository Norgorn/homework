package ru.vladimir.logic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SynchronizationDTO implements Serializable {

    private long money;
    private String country;
}
