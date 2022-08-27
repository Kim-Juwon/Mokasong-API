package com.mokasong.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CriticalException extends CustomException {
    public CriticalException(String message, Integer errorCode, HttpStatus httpStatusCode) {
        super(message, errorCode, httpStatusCode);
    }
}
