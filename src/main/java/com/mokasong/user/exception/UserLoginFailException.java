package com.mokasong.user.exception;

import com.mokasong.common.exception.CustomExceptionList;
import com.mokasong.common.exception.NonCriticalException;

public class UserLoginFailException extends NonCriticalException {
    public UserLoginFailException(CustomExceptionList exception) {
        super(exception.getMessage(), exception.getErrorCode(), exception.getHttpStatusCode());
    }
}
