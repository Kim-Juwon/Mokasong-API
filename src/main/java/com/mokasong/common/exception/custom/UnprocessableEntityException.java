package com.mokasong.common.exception.custom;

import lombok.Getter;

@Getter
public class UnprocessableEntityException extends RuntimeException {
    private final String field;
    private final Object invalidValue;

    public UnprocessableEntityException(String message, String field, Object invalidValue) {
        super(message);
        this.field = field;
        this.invalidValue = invalidValue;
    }
}
