package com.mokasong.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NonCriticalException extends CustomException {
    public NonCriticalException(String message, Integer errorCode, HttpStatus httpStatusCode) {
        super(message, errorCode, httpStatusCode);
    }
}
