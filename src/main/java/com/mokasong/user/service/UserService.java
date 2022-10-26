package com.mokasong.user.service;

import com.mokasong.common.dto.response.DuplicateCheckResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.user.dto.request.LoginRequest;
import com.mokasong.user.dto.request.RegisterRequest;
import com.mokasong.user.dto.request.UserVerifyRequest;
import com.mokasong.user.dto.request.CodeCheckRequest;
import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.dto.response.normal.EmailFindSuccessResponse;
import com.mokasong.user.dto.response.normal.LoginSuccessResponse;
import com.mokasong.user.dto.response.normal.VerificationCodeSendResponse;

import javax.validation.Valid;

public interface UserService {
    LoginSuccessResponse login(LoginRequest requestBody) throws Exception;
    SuccessfulResponse logout() throws Exception;
    DuplicateCheckResponse getDuplicateStatusOfEmail(String email) throws Exception;
    DuplicateCheckResponse getDuplicateStatusOfPhoneNumber(String phoneNumber) throws Exception;
    VerificationCodeSendResponse sendCodeForRegisterCellphone(@Valid UserVerifyRequest requestBody) throws Exception;
    VerificationCodeSendResponse sendCodeForFindEmail(@Valid UserVerifyRequest requestBody) throws Exception;
    VerificationCodeSendResponse sendCodeForFindPassword(@Valid UserVerifyRequest requestBody) throws Exception;
    SuccessfulResponse checkCodeForRegisterCellphone(CodeCheckRequest requestBody) throws Exception;
    EmailFindSuccessResponse checkCodeForFindEmail(CodeCheckRequest requestBody) throws Exception;
    SuccessfulResponse checkCodeFindPassword(@Valid CodeCheckRequest requestBody) throws Exception;
    SuccessfulResponse changeToStandingByRegister(RegisterRequest requestBody) throws Exception;
    void register(String registerToken) throws Exception;
    UserResponse getUserForAdmin(Long userId) throws Exception;
    SuccessfulResponse deleteUserForAdmin(Long userId) throws Exception;
    SuccessfulResponse undeleteUserForAdmin(Long userId) throws Exception;
}
