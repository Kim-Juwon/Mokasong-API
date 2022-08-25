package com.mokasong.dto.user;

import com.mokasong.annotation.ValidationGroups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Schema(description = "휴대폰 번호 인증 DTO", required = true)
public class VerificationCodeCheckDto {
    @Schema(description = "휴대폰 번호", required = true, nullable = false, example = "01023911319")
    @NotBlank(groups = CheckVerificationCodeForPhoneNumber.class, message = "휴대전화번호는 필수입니다.")
    @Pattern(groups = CheckVerificationCodeForPhoneNumber.class, regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phone_number;

    @Schema(description = "인증번호", required = true, nullable = false, example = "129837")
    @NotBlank(groups = CheckVerificationCodeForPhoneNumber.class, message = "인증번호는 필수입니다.")
    @Pattern(groups = CheckVerificationCodeForPhoneNumber.class, regexp = "^\\d{6}$", message = "인증번호는 한자리의 수가 6자리로 이루어져있어야 합니다.")
    private String verification_code;
}
