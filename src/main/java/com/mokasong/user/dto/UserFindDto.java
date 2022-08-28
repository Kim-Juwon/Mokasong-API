package com.mokasong.user.dto;

import com.mokasong.user.validation.UserDataValidationGroups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;

@Getter
public class UserFindDto {
    @Schema(description = "이름", required = true, nullable = false, example = "김주원")
    @NotBlank(groups = {FindEmail.class, FindPassword.class}, message = "이름은 필수입니다.")
    @Pattern(groups = {FindEmail.class, FindPassword.class}, regexp = "^[가-힣|a-z|A-Z]{2,20}$", message = "이름이 유효하지 않습니다.")
    private String name;

    @Schema(description = "휴대폰 번호", required = true, nullable = false, example = "01023911319")
    @NotBlank(groups = FindEmail.class, message = "휴대폰 번호는 필수입니다.")
    @Pattern(groups = FindEmail.class, regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phone_number;

    @Schema(description = "이메일", required = true, nullable = false, example = "damiano102777@naver.com")
    @NotBlank(groups = FindPassword.class, message = "이메일은 필수입니다.")
    @Email(groups = FindPassword.class, message = "이메일 형식이어야 합니다.")
    private String email;

    @Schema(description = "찾으려는 대상", required = true, nullable = false, example = "EMAIL")
    @NotBlank(groups = Default.class, message = "찾으려는 대상은 필수입니다.")
    @Pattern(groups = Default.class, regexp = "^(EMAIL|PASSWORD)$", message = "찾으려는 대상은 EMAIL 또는 PASSWORD 입니다.")
    private String purpose;

    @Schema(description = "인증 방법 (인증번호를 보내는 곳)", required = true, nullable = false, example = "CELLPHONE")
    @NotBlank(groups = {FindEmail.class, FindPassword.class}, message = "인증 방법은 필수입니다.")
    @Pattern(groups = {FindEmail.class, FindPassword.class}, regexp = "^(EMAIL|CELLPHONE)$", message = "인증 방법은 EMAIL 또는 CELLPHONE 입니다.")
    private String way;
}
