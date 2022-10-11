package com.mokasong.common.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RequestDataInvalidExceptionResponse extends ExceptionResponse {
    private List<String> errorMessages;
}
