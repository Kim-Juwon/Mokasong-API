package com.mokasong.domain.user;

import com.mokasong.annotation.ValidationGroups;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter @ToString
public class UserForVerification {
    @NotBlank(groups = {ValidationGroups.SendVerificationCodeForFindEmail.class,
                        ValidationGroups.CheckVerificationCodeForFindEmail.class,
                        ValidationGroups.SendVerificationCodeForFindPassword.class},
                        message = "이름은 필수입니다.")
    @Pattern(groups = {ValidationGroups.SendVerificationCodeForFindEmail.class,
                       ValidationGroups.CheckVerificationCodeForFindEmail.class,
                       ValidationGroups.SendVerificationCodeForFindPassword.class},
                       regexp = "^[가-힣|a-z|A-Z]+$", message = "이름이 유효하지 않습니다.")
    private String name;

    @NotBlank(groups = {ValidationGroups.SendVerificationCodeForFindEmail.class,
                        ValidationGroups.CheckVerificationCodeForFindEmail.class,
                        ValidationGroups.CheckVerificationCodeForPhoneNumber.class,
                        ValidationGroups.SendVerificationCodeForPhoneNumber.class},
                        message = "휴대전화번호는 필수입니다.")
    @Pattern(groups = {ValidationGroups.SendVerificationCodeForFindEmail.class,
                       ValidationGroups.CheckVerificationCodeForFindEmail.class,
                       ValidationGroups.CheckVerificationCodeForPhoneNumber.class,
                       ValidationGroups.SendVerificationCodeForPhoneNumber.class},
                       regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phone_number;

    @NotBlank(groups = {ValidationGroups.CheckVerificationCodeForFindEmail.class,
                        ValidationGroups.CheckVerificationCodeForPhoneNumber.class},
                        message = "인증번호는 필수입니다.")
    @Pattern(groups = {ValidationGroups.CheckVerificationCodeForFindEmail.class,
                       ValidationGroups.CheckVerificationCodeForPhoneNumber.class},
                       regexp = "^\\d{6}$", message = "인증번호는 한자리의 수가 6자리로 이루어져있어야 합니다.")
    private String verification_code;

    @NotBlank(message = "토큰은 비워둘 수 없습니다.")
    @Pattern(regexp = "^[a-z|A-Z|0-1]{50}$", message = "토큰의 형식이 아닙니다.")
    private String token;

    @Email(groups = {ValidationGroups.ChangeToStandingByRegister.class,
                     ValidationGroups.SendVerificationCodeForFindPassword.class,
                     ValidationGroups.SendVerificationCodeForPhoneNumber.class},
                     message = "이메일 형식이어야 합니다.")
    @NotBlank(groups = {ValidationGroups.ChangeToStandingByRegister.class,
                        ValidationGroups.SendVerificationCodeForFindPassword.class,
                        ValidationGroups.SendVerificationCodeForPhoneNumber.class},
                        message = "이메일은 필수입니다.")
    private String email;

    /**
     *  [way]
     *  인증 방법
     *  - EMAIL: 이메일
     *  - CELLPHONE: 휴대전화
     */
    @Pattern(groups = {ValidationGroups.SendVerificationCodeForFindPassword.class}, regexp = "EMAIL|CELLPHONE", message = "인증 방법은 EMAIL 또는 CELLPHONE 이어야 합니다.")
    private String way;
}
