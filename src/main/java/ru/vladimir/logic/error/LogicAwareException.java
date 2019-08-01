package ru.vladimir.logic.error;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class LogicAwareException extends RuntimeException {

    private final LogicErrorCode code;

    public LogicAwareException(LogicErrorCode code) {
        this.code = code;
    }

    public LogicAwareException(LogicErrorCode code, String pattern, Object... patternArguments) {
        super(String.format(pattern, patternArguments));
        this.code = code;
    }

    public LogicAwareException(String s, Throwable throwable, LogicErrorCode code) {
        super(s, throwable);
        this.code = code;
    }

    public LogicAwareException(Throwable throwable, LogicErrorCode code) {
        super(throwable);
        this.code = code;
    }

    public LogicAwareException(String s, Throwable throwable, boolean b, boolean b1, LogicErrorCode code) {
        super(s, throwable, b, b1);
        this.code = code;
    }
}
