package ru.vladimir.logic.model.dto;

import lombok.Value;

import java.io.Serializable;

@Value
public class UserActivityDTO implements Serializable {

    long activity;
    long date;
}
