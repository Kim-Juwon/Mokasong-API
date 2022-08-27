package com.mokasong.common.exception.custom;

import com.mokasong.common.exception.CustomExceptionList;
import com.mokasong.common.exception.NonCriticalException;

public class JWTPreconditionException extends NonCriticalException {
    public JWTPreconditionException(CustomExceptionList exception) {
        super(exception.getMessage(), exception.getErrorCode(), exception.getHttpStatusCode());
    }
}
