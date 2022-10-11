package com.mokasong.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INTERNAL_SERVER_ERROR(-2),
    UNAUTHORIZED(-1),
    UNPROCESSABLE_ENTITY(0);

    private final Integer errorCode;
}
