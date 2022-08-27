package com.mokasong.common.response;

import lombok.Getter;

import java.util.Map;

@Getter
public class NormalResponse extends BaseResponse {
    private Map<String, Object> informations;

    public NormalResponse(String message) {
        super(message);
    }
    public NormalResponse(String message, Map<String, Object> informations) {
        super(message);
        this.informations = informations;
    }
}
