package com.mokasong.user.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.user.validation.UserDataValidationGroups;
import com.mokasong.common.response.BaseResponse;
import com.mokasong.user.dto.LoginDto;
import com.mokasong.user.service.UserLoginService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.mokasong.user.state.Authority.REGULAR;

@RestController
@Tag(name = "로그인/로그아웃", description = "로그인/로그아웃 API")
public class UserLoginController {
    private final UserLoginService userLoginService;

    @Autowired
    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @Tag(name = "로그인/로그아웃")
    @PostMapping("/user/login")
    @ApiOperation(value = "로그인", notes = "로그인합니다.")
    public ResponseEntity<BaseResponse> login(@RequestBody @Validated(UserDataValidationGroups.Login.class) LoginDto loginDto) throws Exception {
        return new ResponseEntity<>(userLoginService.login(loginDto), HttpStatus.OK);
    }

    @Login(REGULAR)
    @Tag(name = "로그인/로그아웃")
    @PostMapping("/user/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<BaseResponse> logout() throws Exception {
        return new ResponseEntity<>(userLoginService.logout(), HttpStatus.OK);
    }
}
