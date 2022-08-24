package com.mokasong.exception.custom;

import com.mokasong.exception.CustomExceptionList;
import com.mokasong.exception.NonCriticalException;

/**
 *  휴대전화번호 인증 시, 인증번호가 일치하지 않는 경우
 */

public class VerificationCodeException extends NonCriticalException {
    public VerificationCodeException(CustomExceptionList customExceptionlist) {
        super(customExceptionlist.getMessage(), customExceptionlist.getErrorCode(), customExceptionlist.getHttpStatusCode());
    }
}

