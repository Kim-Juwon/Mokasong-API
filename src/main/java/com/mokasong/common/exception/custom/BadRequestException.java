package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.CustomException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BadRequestException extends CustomException {
    public BadRequestException(String message, Integer errorCode) {
        super(message, errorCode, HttpStatus.BAD_REQUEST);
    }
}
