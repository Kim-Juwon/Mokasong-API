package com.mokasong.user.controller;

import com.mokasong.user.validation.UserDataValidationGroups.*;
import com.mokasong.common.response.BaseResponse;
import com.mokasong.user.dto.UserRegisterDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.service.UserRegisterService;
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

    // TODO: email과 phone number를 url에 넣어 요청하면 안된다.
    // TODO: email과 phone number에 대한 중복 확인 API를 통합하는 것 고려하기 (확장성을 생각해서)
    // TODO: request body로 보내는 것 고려해보기
    // TODO: UserLoginController와 UserRegisterController 합치기 or DDD 적용하기
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

    @Tag(name = "회원가입")
    @PostMapping("/user/phone-number/verification-code/check")
    @ApiOperation(value = "휴대전화 인증번호 확인", notes = "휴대전화 인증번호를 확인합니다.")
    public ResponseEntity<BaseResponse> checkVerificationCodeForPhoneNumber(
            @RequestBody
            @Validated(VerifyPhoneNumber.class)
            VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.checkVerificationCodeForPhoneNumber(verificationCodeCheckDto), HttpStatus.OK);
    }

    @Tag(name = "회원가입")
    @PostMapping("/user/register/stand-by")
    @ApiOperation(value = "회원가입 대기 상태로 전환", notes = "회원가입 대기 상태로 전환합니다.")
    public ResponseEntity<BaseResponse> changeToStandingByRegister(
            @RequestBody
            @Validated(Register.class)
            UserRegisterDto userRegisterDto) throws Exception {
        return new ResponseEntity<>(userRegisterService.changeToStandingByRegister(userRegisterDto), HttpStatus.OK);
    }
}
