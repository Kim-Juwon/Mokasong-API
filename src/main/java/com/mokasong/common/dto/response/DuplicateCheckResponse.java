package com.mokasong.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class DuplicateCheckResponse {
    private final Boolean duplicateStatus;
}
