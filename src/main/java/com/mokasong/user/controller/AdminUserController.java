package com.mokasong.user.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.service.UserService;
import com.mokasong.user.state.Authority;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mokasong.user.state.Authority.ADMIN;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
    private UserService userService;

    @Value("${application.url.current}")
    private String host;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    @Login(ADMIN)
    @GetMapping("/{id}")
    @ApiOperation(value = "회원 조회", notes = "회원을 조회합니다.", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long userId) throws Exception {
        UserResponse responseBody = userService.getUserForAdmin(userId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @DeleteMapping("/{id}")
    @ApiOperation(value = "회원 삭제", notes = "회원을 soft delete 합니다.", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<SuccessfulResponse> deleteUser(@PathVariable("id") Long userId) throws Exception {
        SuccessfulResponse responseBody = userService.deleteUserForAdmin(userId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @PatchMapping("/{id}/undelete")
    @ApiOperation(value = "회원 삭제 해제", notes = "회원의 soft delete 상태를 해제합니다.", authorizations = @Authorization(value = "Authorization"))
    public ResponseEntity<SuccessfulResponse> undeleteUser(@PathVariable("id") Long userId) throws Exception {
        SuccessfulResponse responseBody = userService.undeleteUserForAdmin(userId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
