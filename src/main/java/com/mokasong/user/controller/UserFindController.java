package com.mokasong.user.controller;

import com.mokasong.common.response.BaseResponse;
import com.mokasong.user.dto.UserFindDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.exception.UserFindFailException;
import com.mokasong.user.service.UserFindService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

import static com.mokasong.common.exception.CustomExceptionList.INVALID_REQUEST_DATA;

@RestController
@RequestMapping("/user/find")
@Tag(name = "아이디/비밀번호 찾기", description = "아이디/비밀번호 찾기 API")
public class UserFindController {
    private UserFindService userFindService;

    @Autowired
    public UserFindController(UserFindService userFindService) {
        this.userFindService = userFindService;
    }

    @Tag(name = "아이디/비밀번호 찾기")
    @PostMapping("/code/send")
    @ApiOperation(value = "인증번호 전송", notes = "아이디/비밀번호 찾기를 위해 인증번호를 전송합니다.")
    public ResponseEntity<BaseResponse> sendCode(@RequestBody @Validated(Default.class) UserFindDto userFindDto) throws Exception {
        BaseResponse response;

        // 이메일 찾기라면
        if (userFindDto.getPurpose().equals("FIND_EMAIL")) {
            response = userFindService.sendCodeForFindEmail(userFindDto);
        }
        // 비밀번호 찾기라면
        else if (userFindDto.getPurpose().equals("FIND_PASSWORD")) {
            response = userFindService.sendCodeForFindPassword(userFindDto);
        }
        // purpose 값에 엉뚱한 값이 들어왔다면 (@Validated로 이미 검증하지만, 확실한 검증을 하기 위함)
        else {
            throw new UserFindFailException(INVALID_REQUEST_DATA);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     *  아이디(이메일)/비밀번호 찾기 인증번호 확인 API
     */

    @Tag(name = "아이디/비밀번호 찾기")
    @PostMapping("/code/check")
    @ApiOperation(value = "인증번호 확인", notes = "아이디/비밀번호 찾기를 위해 전송했던 인증번호를 확인합니다.")
    public ResponseEntity<BaseResponse> checkCode(@RequestBody @Validated(Default.class) VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        BaseResponse response;

        // 이메일 찾기 인증번호 확인이라면
        if (verificationCodeCheckDto.getPurpose().equals("FIND_EMAIL")) {
            response = userFindService.checkCodeForFindEmail(verificationCodeCheckDto);
        }
        // 비밀번호 찾기 인증번호 확인(동시에 비밀번호도 변경)이라면
        else if (verificationCodeCheckDto.getPurpose().equals("FIND_PASSWORD")) {
            response = userFindService.checkCodeFindPassword(verificationCodeCheckDto);
        }
        // purpose 값에 엉뚱한 값이 들어왔다면 (@Validated로 이미 검증하지만, 확실한 검증을 하기 위함)
        else {
            throw new UserFindFailException(INVALID_REQUEST_DATA);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}