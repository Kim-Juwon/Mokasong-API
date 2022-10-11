package com.mokasong.user.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter @Builder
@ToString
@Schema(description = "로그인 DTO", required = true)
public class LoginRequest {
    @Schema(description = "이메일", required = true, nullable = false, example = "damiano102777@naver.com")
    @Email(message = "이메일 형식이어야 합니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    private String email;

    @Schema(description = "비밀번호", required = true, nullable = false)
    @Size(max = 64, message = "비밀번호 해시값은 최대 64자입니다.")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    public String toJson() throws Exception {
        return new ObjectMapper().writeValueAsString(this);
    }
}
