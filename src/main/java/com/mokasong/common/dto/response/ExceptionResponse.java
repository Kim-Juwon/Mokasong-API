package com.mokasong.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class ExceptionResponse {
    private final String message;
    private final Integer errorCode;
}
