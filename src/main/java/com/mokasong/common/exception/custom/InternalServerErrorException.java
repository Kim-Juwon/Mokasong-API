package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.CriticalException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InternalServerErrorException extends CriticalException {
    public InternalServerErrorException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
