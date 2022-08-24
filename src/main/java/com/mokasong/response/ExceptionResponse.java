package com.mokasong.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter @ToString
public class ExceptionResponse extends BaseResponse {
    @JsonProperty("error_code")
    private final Integer errorCode;

    public ExceptionResponse(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
