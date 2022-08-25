package com.mokasong.service.user;

import com.mokasong.domain.user.User;
import com.mokasong.dto.user.LoginDto;
import com.mokasong.response.BaseResponse;

public interface UserLoginService {
    BaseResponse login(LoginDto loginDto) throws Exception;
    BaseResponse logout() throws Exception;
}
