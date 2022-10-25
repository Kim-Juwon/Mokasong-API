package com.mokasong.user.dto.request;

import com.mokasong.user.validation.UserDataValidationGroups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;

@Getter
@Schema(description = "인증번호 검증 DTO", required = true)
public class CodeCheckRequest {
    @Schema(description = "휴대폰 번호", required = true, nullable = false, example = "01023911319")
    @NotBlank(groups = Default.class, message = "휴대전화번호는 필수입니다.")
    @Pattern(groups = Default.class, regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phoneNumber;

    @Schema(description = "인증번호", required = true, nullable = false, example = "129837")
    @NotBlank(groups = Default.class, message = "인증번호는 필수입니다.")
    private String code;

    @Schema(description = "새 비밀번호", required = true, nullable = false)
    @Size(groups = FindPassword.class, max = 64, message = "비밀번호 해시값은 64자 이하입니다.")
    @NotBlank(groups = FindPassword.class, message = "새 비밀번호는 필수입니다.")
    private String newPassword;

    @Schema(description = "인증번호를 검증하려는 목적", required = true, nullable = false, example = "FIND_EMAIL")
    @NotBlank(groups = Default.class, message = "인증번호를 검증하려는 목적은 필수입니다.")
    @Pattern(groups = Default.class, regexp = "^(REGISTER_CELLPHONE|FIND_EMAIL|FIND_PASSWORD)$", message = "인증번호를 검증하려는 목적은 REGISTER_CELLPHONE 또는 FIND_EMAIL 또는 FIND_PASSWORD 입니다.")
    private String purpose;
}
