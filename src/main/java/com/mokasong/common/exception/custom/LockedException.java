package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.NonCriticalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class LockedException extends NonCriticalException {
    public LockedException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.LOCKED);
    }
}
