package com.mokasong.service.user;

import com.mokasong.domain.user.User;
import com.mokasong.domain.user.UserForVerification;
import com.mokasong.response.BaseResponse;

public interface UserRegisterService {
    BaseResponse getExistenceOfEmail(String email) throws Exception;
    BaseResponse getExistenceOfPhoneNumber(String phoneNumber) throws Exception;
    BaseResponse sendVerificationCodeForPhoneNumber(String phoneNumber) throws Exception;
    BaseResponse checkVerificationCodeForPhoneNumber(UserForVerification userInRequest) throws Exception;

    BaseResponse changeToStandingByRegister(User user, String verificationToken) throws Exception;
    void register(String registerToken) throws Exception;
}
