package ru.vladimir.logic.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LogicErrorCode {

    BAD_REQUEST(400), NOT_FOUND(404);

    private final int code;
}
