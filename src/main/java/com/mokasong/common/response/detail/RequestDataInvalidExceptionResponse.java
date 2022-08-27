package com.mokasong.common.response.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokasong.common.response.ExceptionResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestDataInvalidExceptionResponse extends ExceptionResponse {
    @JsonProperty("validation_error_messages")
    private List<String> validationErrorMessages;

    public RequestDataInvalidExceptionResponse(String message, Integer errorCode, List<String> validationErrorMessages) {
        super(message, errorCode);
        this.validationErrorMessages = validationErrorMessages;
    }
}