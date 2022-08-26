package com.mokasong.dto.user;

import com.mokasong.annotation.ValidationGroups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Schema(description = "회원가입 대기상태 전환 DTO", required = true)
public class UserRegisterDto {
    @Schema(description = "이메일", required = true, nullable = false, example = "damiano102777@naver.com")
    @NotBlank(groups = ChangeToStandingByRegister.class, message = "이메일은 필수입니다.")
    @Email(groups = ChangeToStandingByRegister.class, message = "이메일 형식이어야 합니다.")
    private String email;

    @Schema(description = "비밀번호", required = true, nullable = false)
    // TODO: password pattern 정의하기
    @NotBlank(groups = ChangeToStandingByRegister.class, message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "휴대폰 번호", required = true, nullable = false, example = "01023911319")
    @NotBlank(groups = ChangeToStandingByRegister.class, message = "휴대전화번호는 필수입니다.")
    @Pattern(groups = ChangeToStandingByRegister.class, regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phone_number;

    @Schema(description = "이름", required = true, nullable = false, example = "김주원")
    // TODO: name pattern 한번 더 고민하기
    @NotBlank(groups = ChangeToStandingByRegister.class, message = "이름은 필수입니다.")
    @Pattern(groups = ChangeToStandingByRegister.class, regexp = "^[가-힣|a-z|A-Z]+$", message = "이름이 유효하지 않습니다.")
    private String name;

    // TODO: verification_code pattern 고민해보기
    @Schema(description = "휴대폰 번호 인증 증명 토큰", required = true, nullable = false)
    private String verification_token;
}
