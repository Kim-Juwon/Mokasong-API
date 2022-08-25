package com.mokasong.service.user;

import com.mokasong.domain.user.User;
import com.mokasong.response.BaseResponse;

public interface UserLoginService {
    BaseResponse login(User user) throws Exception;
    BaseResponse logout() throws Exception;
}
