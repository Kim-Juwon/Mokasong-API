package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.NonCriticalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerErrorException extends NonCriticalException {
    public InternalServerErrorException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
