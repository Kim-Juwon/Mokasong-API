package com.mokasong.user.controller;

import com.mokasong.common.annotation.LoginRequired;
import com.mokasong.common.response.BaseResponse;
import com.mokasong.user.dto.LoginDto;
import com.mokasong.user.dto.UserVerifyDto;
import com.mokasong.user.dto.UserRegisterDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.exception.UserInformationReadException;
import com.mokasong.user.exception.VerificationCodeCheckException;
import com.mokasong.user.exception.VerificationCodeSendException;
import com.mokasong.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.groups.Default;

import static com.mokasong.common.exception.CustomExceptionList.INVALID_REQUEST_DATA;
import static com.mokasong.common.exception.CustomExceptionList.REQUIRED_TYPE_EMAIL_OR_PHONENUMBER;
import static com.mokasong.common.util.StringHandler.isEmailPattern;
import static com.mokasong.common.util.StringHandler.isPhoneNumberPattern;
import static com.mokasong.user.state.Authority.*;

@RestController
@RequestMapping("/user")
@Validated
@Tag(name = "User API", description = "유저 관련 API")
public class UserController {
    private UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Tag(name = "User API")
    @GetMapping("/existence")
    @ApiOperation(value = "이메일/휴대폰번호 중복 확인", notes = "이메일/휴대폰번호가 회원 정보에 이미 존재하는지 확인합니다.")
    public ResponseEntity<BaseResponse> getExistence(@RequestParam("data") @NotBlank(message = "이메일 or 휴대폰번호는 필수입니다.") String data) throws Exception {
        BaseResponse response;

        // 이메일 형식이라면
        if (isEmailPattern(data)) {
            response = userService.getExistenceOfEmail(data);
        }
        // 휴대폰번호 형식이라면
        else if (isPhoneNumberPattern(data)) {
            response = userService.getExistenceOfCellphone(data);
        }
        // 그 외 형식이라면
        else {
            throw new UserInformationReadException(REQUIRED_TYPE_EMAIL_OR_PHONENUMBER);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "User API")
    @PostMapping("/verification-code/send")
    @ApiOperation(value = "인증번호 전송", notes = "인증번호를 전송합니다.")
    public ResponseEntity<BaseResponse> sendCode(@RequestBody @Validated(Default.class) UserVerifyDto dto) throws Exception {
        BaseResponse response;

        // 휴대폰 번호 등록이라면 (회원가입시의 휴대폰 번호 인증 or 회원 정보 수정시 휴대폰 번호 인증)
        if (dto.getPurpose().equals("REGISTER_CELLPHONE")) {
            response = userService.sendCodeForRegisterCellphone(dto);
        }
        // 이메일 찾기라면
        else if (dto.getPurpose().equals("FIND_EMAIL")) {
            response = userService.sendCodeForFindEmail(dto);
        }
        // 비밀번호 찾기라면
        else if (dto.getPurpose().equals("FIND_PASSWORD")) {
            response = userService.sendCodeForFindPassword(dto);
        }
        // purpose 값에 엉뚱한 값이 들어왔다면 (@Validated로 이미 검증하지만, 확실한 검증을 하기 위함)
        else {
            throw new VerificationCodeSendException(INVALID_REQUEST_DATA);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "User API")
    @PostMapping("/verification-code/check")
    @ApiOperation(value = "인증번호 확인", notes = "이메일/휴대폰으로 전송했던 인증번호를 확인합니다.")
    public ResponseEntity<BaseResponse> checkCode(@RequestBody @Validated(Default.class) VerificationCodeCheckDto dto) throws Exception {
        BaseResponse response;

        // 휴대폰 번호 등록이라면 (회원가입시의 휴대폰 번호 인증 or 회원 정보 수정시 휴대폰 번호 수정할때)
        if (dto.getPurpose().equals("REGISTER_CELLPHONE")) {
            response = userService.checkCodeForRegisterCellphone(dto);
        }
        // 이메일 찾기 인증번호 확인이라면
        else if (dto.getPurpose().equals("FIND_EMAIL")) {
            response = userService.checkCodeForFindEmail(dto);
        }
        // 비밀번호 찾기 인증번호 확인(동시에 비밀번호도 변경)이라면
        else if (dto.getPurpose().equals("FIND_PASSWORD")) {
            response = userService.checkCodeFindPassword(dto);
        }
        // purpose 값에 엉뚱한 값이 들어왔다면 (@Validated로 이미 검증하지만, 확실한 검증을 하기 위함)
        else {
            throw new VerificationCodeCheckException(INVALID_REQUEST_DATA);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Tag(name = "User API")
    @PostMapping("/register/standby")
    @ApiOperation(value = "회원가입 대기 상태로 전환", notes = "회원가입 대기 상태로 전환합니다.")
    public ResponseEntity<BaseResponse> changeToStandingByRegister(@RequestBody @Valid UserRegisterDto dto) throws Exception {
        return new ResponseEntity<>(userService.changeToStandingByRegister(dto), HttpStatus.OK);
    }

    @Tag(name = "User API")
    @PostMapping("/login")
    @ApiOperation(value = "로그인", notes = "로그인합니다.")
    public ResponseEntity<BaseResponse> login(@RequestBody @Valid LoginDto dto) throws Exception {
        return new ResponseEntity<>(userService.login(dto), HttpStatus.OK);
    }

    @LoginRequired({REGULAR, ADMIN})
    @Tag(name = "User API")
    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃합니다.", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<BaseResponse> logout() throws Exception {
        return new ResponseEntity<>(userService.logout(), HttpStatus.OK);
    }
}
