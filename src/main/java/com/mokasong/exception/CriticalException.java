package com.mokasong.exception;

import org.springframework.http.HttpStatus;

public class CriticalException extends CustomException {
    public CriticalException(String message, Integer errorCode, HttpStatus httpStatusCode) {
        super(message, errorCode, httpStatusCode);
    }
}
