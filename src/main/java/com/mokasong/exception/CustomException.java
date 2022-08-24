package com.mokasong.exception;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter @ToString
public class CustomException extends RuntimeException {
    private Integer errorCode;
    private HttpStatus httpStatusCode;

    public CustomException(String message, Integer errorCode, HttpStatus httpStatusCode) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatusCode = httpStatusCode;
    }
}
