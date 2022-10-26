package com.mokasong.user.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.service.AdminUserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mokasong.user.state.Authority.ADMIN;

@RestController
@RequestMapping("/admin/users")
@Tag(name = "Admin User API", description = "회원 API - 어드민")
public class AdminUserController {
    private final AdminUserService adminUserService;

    @Value("${application.schema-and-host.current}")
    private String schemaAndHost;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @Login(ADMIN)
    @Tag(name = "Admin User API")
    @GetMapping("/{id}")
    @ApiOperation(value = "회원 조회", notes = "회원을 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<UserResponse> getUser(@PathVariable("id") Long userId) throws Exception {
        UserResponse responseBody = adminUserService.getUser(userId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @Tag(name = "Admin User API")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "회원 삭제", notes = "회원 soft delete", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteUser(@PathVariable("id") Long userId) throws Exception {
        SuccessfulResponse responseBody = adminUserService.deleteUser(userId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @Tag(name = "Admin User API")
    @PatchMapping("/{id}/undelete")
    @ApiOperation(value = "회원 삭제 해제", notes = "회원의 soft delete 해제", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> undeleteUser(@PathVariable("id") Long userId) throws Exception {
        SuccessfulResponse responseBody = adminUserService.undeleteUser(userId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
