package com.mokasong.exception.custom;

import com.mokasong.exception.CustomExceptionList;
import com.mokasong.exception.NonCriticalException;

public class JWTPreconditionException extends NonCriticalException {
    public JWTPreconditionException(CustomExceptionList customExceptionList) {
        super(customExceptionList.getMessage(), customExceptionList.getErrorCode(), customExceptionList.getHttpStatusCode());
    }
}
