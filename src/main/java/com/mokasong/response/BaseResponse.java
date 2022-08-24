package com.mokasong.response;

import lombok.Getter;
import lombok.ToString;


@Getter @ToString
public class BaseResponse {
    private final String message;

    public BaseResponse(String message) {
        this.message = message;
    }
}
