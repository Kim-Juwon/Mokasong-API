package com.mokasong.common.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ExceptionResponse extends BaseResponse {
    @JsonProperty("error_code")
    private final Integer errorCode;

    public ExceptionResponse(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
