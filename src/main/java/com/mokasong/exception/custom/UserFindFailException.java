package com.mokasong.exception.custom;

import com.mokasong.exception.CustomExceptionList;
import com.mokasong.exception.NonCriticalException;
import org.springframework.http.HttpStatus;

public class UserFindFailException extends NonCriticalException {
    public UserFindFailException(CustomExceptionList customExceptionList) {
        super(customExceptionList.getMessage(), customExceptionList.getErrorCode(), customExceptionList.getHttpStatusCode());
    }
}
