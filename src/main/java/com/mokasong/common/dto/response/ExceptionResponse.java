package com.mokasong.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ExceptionResponse {
    private final String message;
    private final Integer errorCode;
}
