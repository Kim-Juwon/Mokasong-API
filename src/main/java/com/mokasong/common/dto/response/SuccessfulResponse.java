package com.mokasong.common.dto.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@NoArgsConstructor
public class SuccessfulResponse {
    private Boolean success;
}
