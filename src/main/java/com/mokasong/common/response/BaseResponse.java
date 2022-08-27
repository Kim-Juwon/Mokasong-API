package com.mokasong.common.response;

import lombok.Getter;

@Getter
public class BaseResponse {
    private final String message;

    public BaseResponse(String message) {
        this.message = message;
    }
}
