package com.mokasong.user.exception;

import com.mokasong.common.exception.CustomExceptionList;
import com.mokasong.common.exception.NonCriticalException;

public class UserLogoutException extends NonCriticalException {
    public UserLogoutException(CustomExceptionList exception) {
        super(exception.getMessage(), exception.getErrorCode(), exception.getHttpStatusCode());
    }
}
