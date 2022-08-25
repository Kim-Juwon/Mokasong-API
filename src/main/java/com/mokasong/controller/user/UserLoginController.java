package com.mokasong.controller.user;

import com.mokasong.annotation.Auth;
import com.mokasong.annotation.NonAuth;
import com.mokasong.annotation.ValidationGroups;
import com.mokasong.annotation.XssPrevent;
import com.mokasong.domain.user.User;
import com.mokasong.response.BaseResponse;
import com.mokasong.service.user.UserLoginService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.mokasong.state.Authority.ADMIN;
import static com.mokasong.state.Authority.REGULAR;

@RestController
public class UserLoginController {
    private final UserLoginService userLoginService;

    @Autowired
    public UserLoginController(UserLoginService userLoginService) {
        this.userLoginService = userLoginService;
    }

    @NonAuth
    @XssPrevent
    @PostMapping("/user/login")
    @ApiOperation(value = "로그인", notes = "로그인합니다.")
    public ResponseEntity<BaseResponse> login(@RequestBody @Validated(ValidationGroups.Login.class) User user) throws Exception {
        return new ResponseEntity<>(userLoginService.login(user), HttpStatus.OK);
    }

    @Auth({REGULAR, ADMIN})
    @XssPrevent
    @PostMapping("/user/logout")
    @ApiOperation(value = "로그아웃", notes = "로그아웃", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<BaseResponse> logout() throws Exception {
        return new ResponseEntity<>(userLoginService.logout(), HttpStatus.OK);
    }

}
