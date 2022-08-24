package com.mokasong.exception;

import org.springframework.http.HttpStatus;

public class NonCriticalException extends CustomException {
    public NonCriticalException(String message, Integer errorCode, HttpStatus httpStatusCode) {
        super(message, errorCode, httpStatusCode);
    }
}
