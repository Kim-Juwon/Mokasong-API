package com.mokasong.user.dto;

import com.mokasong.user.validation.UserDataValidationGroups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

@Getter
@Schema(description = "인증번호 검증 DTO", required = true)
public class VerificationCodeCheckDto {
    @Schema(description = "휴대폰 번호", required = true, nullable = false, example = "01023911319")
    @NotBlank(groups = {VerifyPhoneNumber.class, FindEmail.class, FindPassword.class}, message = "휴대전화번호는 필수입니다.")
    @Pattern(groups = {VerifyPhoneNumber.class, FindEmail.class, FindPassword.class}, regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phone_number;

    @Schema(description = "인증번호", required = true, nullable = false, example = "129837")
    @NotBlank(groups = {VerifyPhoneNumber.class, FindEmail.class, FindPassword.class}, message = "인증번호는 필수입니다.")
    @Pattern(groups = {VerifyPhoneNumber.class, FindEmail.class, FindPassword.class}, regexp = "^\\d{6}$", message = "인증번호는 한자리의 수가 6자리로 이루어져있어야 합니다.")
    private String code;

    @Schema(description = "새 비밀번호", required = true, nullable = false)
    // TODO: password pattern 정의하기
    @NotBlank(groups = FindPassword.class)
    private String new_password;

    @Schema(description = "인증번호를 검증하려는 목적", required = true, nullable = false, example = "email")
    @NotBlank(groups = Default.class, message = "인증번호를 검증하려는 목적은 필수입니다.")
    @Pattern(groups = Default.class, regexp = "^(FIND_EMAIL|FIND_PASSWORD)$", message = "인증번호를 검증하려는 목적은 FIND_EMAIL 또는 FIND_PASSWORD 입니다.")
    private String purpose;
}
