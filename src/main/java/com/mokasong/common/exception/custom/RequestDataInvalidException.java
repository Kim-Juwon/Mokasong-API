package com.mokasong.common.exception.custom;

public class RequestDataInvalidException extends RuntimeException {
    public RequestDataInvalidException(String message) {
        super(message);
    }
}
