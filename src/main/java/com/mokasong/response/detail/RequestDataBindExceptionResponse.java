package com.mokasong.response.detail;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mokasong.response.ExceptionResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class RequestDataBindExceptionResponse extends ExceptionResponse {
    @JsonProperty("validation_error_messages")
    private List<String> validationErrorMessages;

    public RequestDataBindExceptionResponse(String message, Integer errorCode, List<String> validationErrorMessages) {
        super(message, errorCode);
        this.validationErrorMessages = validationErrorMessages;
    }
}
