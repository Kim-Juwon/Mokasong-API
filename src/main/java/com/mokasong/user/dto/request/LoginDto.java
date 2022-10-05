package com.mokasong.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Schema(description = "로그인 DTO", required = true)
public class LoginDto {
    @Schema(description = "이메일", required = true, nullable = false, example = "damiano102777@naver.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @Schema(description = "비밀번호", required = true, nullable = false)
    // TODO: password pattern 정의하기
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;
}
