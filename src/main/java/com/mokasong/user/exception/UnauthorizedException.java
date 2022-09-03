package com.mokasong.user.exception;

import com.mokasong.common.exception.CustomExceptionList;
import com.mokasong.common.exception.NonCriticalException;

public class UnauthorizedException extends NonCriticalException {
    public UnauthorizedException(CustomExceptionList exception) {
        super(exception.getMessage(), exception.getErrorCode(), exception.getHttpStatusCode());
    }
}
