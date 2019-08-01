package ru.vladimir.logic.model.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class UserWithMoneyDTO implements Serializable {

    String userId;
    long money;
}
