package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.NonCriticalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotFoundException extends NonCriticalException {
    public NotFoundException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.NOT_FOUND);
    }
}
