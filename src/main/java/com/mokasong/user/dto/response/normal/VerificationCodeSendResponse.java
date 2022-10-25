package com.mokasong.user.dto.response.normal;

import com.mokasong.common.dto.response.SuccessfulResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter @SuperBuilder
public class VerificationCodeSendResponse extends SuccessfulResponse {
    private final Integer effectiveMinute;
}
