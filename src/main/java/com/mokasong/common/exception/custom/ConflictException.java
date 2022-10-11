package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.NonCriticalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ConflictException extends NonCriticalException {
    public ConflictException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.CONFLICT);
    }
}
