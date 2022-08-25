package com.mokasong.controller.user;

import com.mokasong.annotation.NonAuth;
import com.mokasong.annotation.ValidationGroups.*;
import com.mokasong.annotation.XssPrevent;
import com.mokasong.dto.user.UserRegisterDto;
import com.mokasong.dto.user.VerificationCodeCheckDto;
import com.mokasong.response.BaseResponse;
import com.mokasong.service.user.UserRegisterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@RestController
@Validated
@Tag(name = "회원가입", description = "회원가입 API")
public class UserRegisterController {
    private UserRegisterService userRegisterService;

    @Autowired
    public UserRegisterController(UserRegisterService userRegisterService) {
        this.userRegisterService = userRegisterService;
    }

    @NonAuth
    @Tag(name = "회원가입")
    @GetMapping("/existence/email/{email}")
    @ApiOperation(value = "이메일 중복 확인", notes = "이메일이 회원 정보에 이미 존재하는지 확인합니다.")
    public ResponseEntity<BaseResponse> getExistenceOfEmail(
            @PathVariable("email")
            @Email(message = "이메일 형식이어야 합니다.")
            @NotBlank(message = "이메일은 필수입니다.")
            String email) throws Exception {
        return new ResponseEntity<>(userRegisterService.getExistenceOfEmail(email), HttpStatus.OK);
    }

    @NonAuth
    @Tag(name = "회원가입")
    @GetMapping("/existence/phone-number/{phone-number}")
    @ApiOperation(value = "휴대전화번호 중복 확인", notes = "휴대전화번호가 회원 정보에 이미 존재하는지 확인합니다.")
    public ResponseEntity<BaseResponse> getExistenceOfPhoneNumber(
            @PathVariable("phone-number")
            @NotBlank(message = "휴대전화번호는 필수입니다.")
            @Pattern(regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
            String phoneNumber) throws Exception {
        return new ResponseEntity<>(userRegisterService.getExistenceOfPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    @NonAuth
    @XssPrevent
    @Tag(name = "회원가입")
    @PostMapping("/user/phone-number/verification-code/send")
    @ApiOperation(value = "휴대전화 인증번호 발송", notes = "휴대전화 인증번호를 발송합니다.")
    public ResponseEntity<BaseResponse> sendVerificationCodeForPhoneNumber(
            @RequestParam("phone-number")
            @NotBlank(message = "휴대전화번호는 필수입니다.")
            @Pattern(regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
            String phoneNumber) throws Exception {
        return new ResponseEntity<>(userRegisterService.sendVerificationCodeForPhoneNumber(phoneNumber), HttpStatus.OK);
    }

    @NonAuth
    @XssPrevent
    @Tag(name = "회원가입")
    @PostMapping("/user/phone-number/verification-code/check")
    @ApiOperation(value = "휴대전화 인증번호 확인", notes = "휴대전화 인증번호를 확인합니다.")
    public ResponseEntity<BaseResponse> checkVerificationCodeForPhoneNumber(
            @RequestBody
            @Validated(CheckVerificationCodeForPhoneNumber.class)
            VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.checkVerificationCodeForPhoneNumber(verificationCodeCheckDto), HttpStatus.OK);
    }

    @NonAuth
    @XssPrevent
    @Tag(name = "회원가입")
    @PostMapping("/user/register/stand-by")
    @ApiOperation(value = "회원가입 대기 상태로 전환", notes = "회원가입 대기 상태로 전환합니다.")
    public ResponseEntity<BaseResponse> changeToStandingByRegister(
            @RequestBody
            @Validated(ChangeToStandingByRegister.class)
            UserRegisterDto userRegisterDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.changeToStandingByRegister(userRegisterDto), HttpStatus.OK);
    }
}
