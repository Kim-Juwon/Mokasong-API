package com.mokasong.exception.custom;

import com.mokasong.exception.CustomExceptionList;
import com.mokasong.exception.NonCriticalException;

/**
 *  회원가입중 발생하는 문제와 관련있 Exception
 */

public class UserRegisterFailException extends NonCriticalException {
    public UserRegisterFailException(CustomExceptionList exception) {
        super(exception.getMessage(), exception.getErrorCode(), exception.getHttpStatusCode());
    }
}
