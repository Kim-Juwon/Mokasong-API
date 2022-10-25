package com.mokasong.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @SuperBuilder
public class RequestDataInvalidExceptionResponse extends ExceptionResponse {
    private List<String> errorMessages;
}
