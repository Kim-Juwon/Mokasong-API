package com.mokasong.user.controller;
/*
import com.mokasong.annotation.ValidationGroups;
import com.mokasong.domain.user.UserForVerification;
import com.mokasong.response.BaseResponse;
import com.mokasong.service.user.UserFindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/user/find")
public class UserFindController {
    @Autowired
    private UserFindService userFindService;

    @PostMapping("/email")
    public ResponseEntity<BaseResponse> sendVerificationCodeForFindEmail(
            @RequestBody
            @Validated(ValidationGroups.SendVerificationCodeForFindEmail.class)
            UserForVerification userForVerification) throws Exception {
        return new ResponseEntity<>(userFindService.sendVerificationCodeForFindEmail(userForVerification), HttpStatus.OK);
    }

    @PostMapping("/verification-code/check")
    public ResponseEntity<BaseResponse> checkVerificationCodeForFindEmail(
            @RequestBody
            @Validated(ValidationGroups.CheckVerificationCodeForFindEmail.class)
            UserForVerification userForVerification) throws Exception {
        return new ResponseEntity<>(userFindService.checkVerificationCodeForFindEmail(userForVerification), HttpStatus.OK);
    }

    @PostMapping("/password")
    public ResponseEntity<BaseResponse> sendVerificationCodeForFindPassword(
            @RequestBody
            @Validated(ValidationGroups.SendVerificationCodeForFindPassword.class)
            UserForVerification userForVerification) throws Exception {
        return new ResponseEntity<>(null, HttpStatus.OK);
    }
}


 */