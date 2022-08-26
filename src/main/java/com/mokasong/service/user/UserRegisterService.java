package com.mokasong.service.user;

import com.mokasong.dto.user.UserRegisterDto;
import com.mokasong.dto.user.VerificationCodeCheckDto;
import com.mokasong.response.BaseResponse;

public interface UserRegisterService {
    BaseResponse getExistenceOfEmail(String email) throws Exception;
    BaseResponse getExistenceOfPhoneNumber(String phoneNumber) throws Exception;
    BaseResponse sendVerificationCodeForPhoneNumber(String phoneNumber) throws Exception;
    BaseResponse checkVerificationCodeForPhoneNumber(VerificationCodeCheckDto verificationCodeCheckDto) throws Exception;

    BaseResponse changeToStandingByRegister(UserRegisterDto userRegisterDto) throws Exception;
    void register(String registerToken) throws Exception;
}
