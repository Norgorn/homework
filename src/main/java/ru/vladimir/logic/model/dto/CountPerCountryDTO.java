package ru.vladimir.logic.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CountPerCountryDTO implements Serializable {

    String country;
    long count;
}
