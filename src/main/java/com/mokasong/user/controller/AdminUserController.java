package com.mokasong.user.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.service.UserService;
import com.mokasong.user.state.Authority;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long userId) throws Exception {
        return ResponseEntity
                .ok()
                .body(userService.getUserForAdmin(userId));
    }
}
