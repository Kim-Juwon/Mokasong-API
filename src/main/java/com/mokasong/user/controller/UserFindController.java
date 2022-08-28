package com.mokasong.user.controller;

import com.mokasong.common.response.BaseResponse;
import com.mokasong.user.dto.UserFindDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.exception.UserFindFailException;
import com.mokasong.user.service.UserFindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

import static com.mokasong.common.exception.CustomExceptionList.INVALID_REQUEST_DATA;

@RestController
@RequestMapping("/user/find")
public class UserFindController {
    private UserFindService userFindService;

    @Autowired
    public UserFindController(UserFindService userFindService) {
        this.userFindService = userFindService;
    }

    /**
     *  아이디(이메일)/비밀번호 찾기 인증번호 발송 API
     */
    @PostMapping("/code/send")
    public ResponseEntity<BaseResponse> sendCode(@RequestBody @Validated(Default.class) UserFindDto userFindDto) throws Exception {
        BaseResponse response;

        // 이메일 찾기라면
        if (userFindDto.getPurpose().equals("EMAIL")) {
            response = userFindService.sendCodeForFindEmail(userFindDto);
        }
        // 비밀번호 찾기라면
        else if (userFindDto.getPurpose().equals("PASSWORD")) {
            response = userFindService.sendCodeForFindPassword(userFindDto);
        }
        // purpose 값에 엉뚱한 값이 들어왔다면 (@Validated로 이미 검증하지만, 확실한 검증을 하기 위함)
        else {
            throw new UserFindFailException(INVALID_REQUEST_DATA);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
    @PostMapping("/code/check")
    public ResponseEntity<BaseResponse> checkCode(@RequestBody @Validated(Default.class) VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        BaseResponse response;

        // 이메일 찾기 인증번호 확인이라면
        if (verificationCodeCheckDto.getPurpose().equals("FIND_EMAIL")) {

        }
        else if (verificationCodeCheckDto.getPurpose().equals("FIND_PASSWORD") {
        }
    }
*/
}