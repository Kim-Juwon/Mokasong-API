package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.NonCriticalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class UnprocessableEntityException extends NonCriticalException {
    public UnprocessableEntityException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
