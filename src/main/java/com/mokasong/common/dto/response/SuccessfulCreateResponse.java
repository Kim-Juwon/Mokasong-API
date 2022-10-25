package com.mokasong.common.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class SuccessfulCreateResponse extends SuccessfulResponse {
    @JsonProperty("id")
    private Long entityId;
}
