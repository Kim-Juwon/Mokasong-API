package com.mokasong.user.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.user.dto.response.admin.UserResponse;

public interface AdminUserService {
    UserResponse getUser(Long userId) throws Exception;
    SuccessfulResponse deleteUser(Long userId) throws Exception;
    SuccessfulResponse undeleteUser(Long userId) throws Exception;
}
