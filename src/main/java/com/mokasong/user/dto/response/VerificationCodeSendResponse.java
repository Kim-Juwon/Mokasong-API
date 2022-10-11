package com.mokasong.user.dto.response;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mokasong.common.dto.response.SuccessfulResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VerificationCodeSendResponse extends SuccessfulResponse {
    private final Integer effectiveMinute;
}
