package com.mokasong.user.controller;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.exception.custom.UnprocessableEntityException;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.user.dto.request.LoginRequest;
import com.mokasong.user.dto.request.UserVerifyRequest;
import com.mokasong.user.dto.request.RegisterRequest;
import com.mokasong.user.dto.request.CodeCheckRequest;
import com.mokasong.common.dto.response.DuplicateCheckResponse;
import com.mokasong.user.dto.response.normal.LoginSuccessResponse;
import com.mokasong.user.dto.response.normal.VerificationCodeSendResponse;
import com.mokasong.user.service.UserService;
import com.mokasong.user.service.UserServiceImpl;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

import static com.mokasong.common.util.StringHandler.isEmailPattern;
import static com.mokasong.common.util.StringHandler.isPhoneNumberPattern;
import static com.mokasong.user.state.Authority.*;

@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "User API", description = "회원 API - 일반")
public class UserController {
    private final UserService userService;

    public UserController(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Tag(name = "User API")
    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "로그인합니다.")
    public ResponseEntity<LoginSuccessResponse> login(@RequestBody @Valid LoginRequest requestBody) throws Exception {
        return ResponseEntity
                .ok()
                .body(userService.login(requestBody));
    }

    @Auth({REGULAR, ADMIN})
    @Tag(name = "User API")
    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃합니다.", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<SuccessfulResponse> logout() throws Exception {
        return ResponseEntity
                .ok()
                .body(userService.logout());
    }

    @Tag(name = "User API")
    @GetMapping("/duplicate-status")
    @ApiOperation(value = "이메일/휴대폰번호 중복 확인", notes = "이메일/휴대폰번호가 회원 정보에 이미 존재하는지 확인합니다.")
    public ResponseEntity<DuplicateCheckResponse> checkDuplicateStatus(
            @RequestParam("data") @NotBlank(message = "이메일 or 휴대폰번호는 필수입니다.") String data) throws Exception {
        DuplicateCheckResponse response;

        if (isEmailPattern(data)) {
            response = userService.getDuplicateStatusOfEmail(data);
        }
        else if (isPhoneNumberPattern(data)) {
            response = userService.getDuplicateStatusOfPhoneNumber(data);
        }
        else {
            throw new UnprocessableEntityException("data가 유효하지 않습니다.", "data", data);
        }

        return ResponseEntity
                .ok()
                .body(response);
    }

    @Tag(name = "User API")
    @PostMapping("/verification-code/send")
    @ApiOperation(value = "인증번호 발송", notes = "인증번호를 발송합니다.")
    public ResponseEntity<VerificationCodeSendResponse> sendCode(
            @RequestBody @Validated(Default.class) UserVerifyRequest requestBody) throws Exception {
        VerificationCodeSendResponse response;

        switch (requestBody.getPurpose()) {
            case "REGISTER_CELLPHONE":
                response = userService.sendCodeForRegisterCellphone(requestBody);
                break;
            case "FIND_EMAIL":
                response = userService.sendCodeForFindEmail(requestBody);
                break;
            case "FIND_PASSWORD":
                response = userService.sendCodeForFindPassword(requestBody);
                break;
            default:
                throw new UnprocessableEntityException("purpose가 유효하지 않습니다.", "purpose", requestBody.getPurpose());
        }

        return ResponseEntity
                .ok()
                .body(response);
    }

    @Tag(name = "User API")
    @PostMapping("/verification-code/check")
    @ApiOperation(value = "인증번호 확인", notes = "이메일/휴대폰으로 전송했던 인증번호를 확인합니다.")
    public ResponseEntity<SuccessfulResponse> checkCode(
            @RequestBody @Validated(Default.class) CodeCheckRequest requestBody) throws Exception {
        SuccessfulResponse response;

        switch (requestBody.getPurpose()) {
            // 휴대폰 번호 등록이라면 (회원가입시의 휴대폰 번호 인증 or 회원 정보 수정시 휴대폰 번호 수정할때)
            case "REGISTER_CELLPHONE":
                response = userService.checkCodeForRegisterCellphone(requestBody);
                break;
            // 이메일 찾기 인증번호 확인이라면
            case "FIND_EMAIL":
                response = userService.checkCodeForFindEmail(requestBody);
                break;
            // 비밀번호 찾기 인증번호 확인(동시에 비밀번호도 변경)이라면
            case "FIND_PASSWORD":
                response = userService.checkCodeFindPassword(requestBody);
                break;
            default:
                throw new UnprocessableEntityException("purpose가 유효하지 않습니다.", "purpose", requestBody.getPurpose());
        }

        return ResponseEntity
                .ok()
                .body(response);
    }

    @Tag(name = "User API")
    @PostMapping("/register/standby")
    @ApiOperation(value = "회원가입 대기 상태로 전환", notes = "회원가입 대기 상태로 전환합니다.")
    public ResponseEntity<SuccessfulResponse> changeToStandingByRegister(
            @RequestBody @Valid RegisterRequest requestBody) throws Exception {
        return ResponseEntity
                .ok()
                .body(userService.changeToStandingByRegister(requestBody));
    }
}