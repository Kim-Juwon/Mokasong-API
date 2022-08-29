package com.mokasong.user.exception;

import com.mokasong.common.exception.CustomExceptionList;
import com.mokasong.common.exception.NonCriticalException;

public class VerificationCodeSendException extends NonCriticalException {
    public VerificationCodeSendException(CustomExceptionList exception) {
        super(exception.getMessage(), exception.getErrorCode(), exception.getHttpStatusCode());
    }
}
