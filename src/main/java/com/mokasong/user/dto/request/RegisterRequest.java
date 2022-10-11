package com.mokasong.user.dto.request;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "회원가입 대기상태 전환 DTO", required = true)
public class RegisterRequest {
    @Schema(description = "이메일", required = true, nullable = false, example = "damiano102777@naver.com")
    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "이메일 형식이어야 합니다.")
    private String email;

    @Schema(description = "비밀번호", required = true, nullable = false)
    @Size(max = 64, message = "비밀번호 해시값은 64자 이하입니다.")
    @NotBlank(message = "비밀번호는 필수입니다.")
    private String password;

    @Schema(description = "휴대폰 번호", required = true, nullable = false, example = "01023911319")
    @NotBlank(message = "휴대전화번호는 필수입니다.")
    @Pattern(regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phoneNumber;

    @Schema(description = "이름", required = true, nullable = false, example = "김주원")
    @NotBlank(message = "이름은 필수입니다.")
    @Pattern(regexp = "^[가-힣|a-z|A-Z]{2,20}$", message = "이름이 유효하지 않습니다.")
    private String name;
}
