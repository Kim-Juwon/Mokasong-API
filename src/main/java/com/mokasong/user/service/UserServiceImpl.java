package com.mokasong.user.service;

import com.mokasong.common.exception.custom.*;
import com.mokasong.common.dto.response.DuplicateCheckResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.util.*;
import com.mokasong.user.domain.User;
import com.mokasong.user.dto.request.LoginRequest;
import com.mokasong.user.dto.request.UserVerifyRequest;
import com.mokasong.user.dto.request.RegisterRequest;
import com.mokasong.user.dto.request.CodeCheckRequest;
import com.mokasong.user.dto.response.EmailFindSuccessResponse;
import com.mokasong.user.dto.response.LoginSuccessResponse;
import com.mokasong.user.dto.response.VerificationCodeSendResponse;
import com.mokasong.user.repository.UserMapper;
import com.mokasong.user.state.Authority;
import com.mokasong.user.validation.UserDataValidationGroups.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.validation.Valid;

import static com.mokasong.common.state.RedisCategory.*;
import static com.mokasong.common.util.UserHandler.getUser;

@Service
@Validated
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final JwtHandler jwtHandler;
    private final AwsSes awsSes;
    private final MessageSender messageSender;
    private final RedisClient redisClient;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, JwtHandler jwtHandler, AwsSes awsSes,
                           MessageSender messageSender, RedisClient redisClient) {
        this.userMapper = userMapper;
        this.jwtHandler = jwtHandler;
        this.awsSes = awsSes;
        this.messageSender = messageSender;
        this.redisClient = redisClient;
    }

    @Override
    @Transactional
    public LoginSuccessResponse login(LoginRequest requestBody) throws Exception {
        User user = userMapper.getUserByEmail(requestBody.getEmail());

        if (user == null || !BCrypt.checkpw(requestBody.getPassword(), user.getPassword())) {
            throw new NotFoundException("????????? ???????????? ????????????.", 1);
        }
        if (user.getAuthority() == Authority.STAND_BY_REGISTER) {
            throw new ForbiddenException("?????? ????????? ????????????.", 2);
        }

        user.changeLastLoginTimeToNow();
        userMapper.updateUser(user);

        return LoginSuccessResponse.builder()
                .accessToken(jwtHandler.generateToken(user.getUserId(), user.getSecretKey(), 1))
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse logout() throws Exception {
        User user = getUser();

        user.changeLastLogoutTimeToNow();
        userMapper.updateUser(user);

        return new SuccessfulResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateCheckResponse getDuplicateStatusOfEmail(String email) throws Exception {
        return DuplicateCheckResponse.builder()
                .duplicateStatus(userMapper.getUserByEmail(email) != null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DuplicateCheckResponse getDuplicateStatusOfPhoneNumber(String phoneNumber) throws Exception {
        return DuplicateCheckResponse.builder()
                .duplicateStatus(userMapper.getUserByPhoneNumber(phoneNumber) != null)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Validated(RegisterCellPhone.class)
    public VerificationCodeSendResponse sendCodeForRegisterCellphone(@Valid UserVerifyRequest requestBody) throws Exception {
        if (userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber()) != null) {
            throw new ConflictException("?????? ??????????????? ???????????? ?????????????????????.", 1);
        }

        String code = RandomStringUtils.randomNumeric(6);
        redisClient.setString(REGISTER_CELLPHONE, requestBody.getPhoneNumber(), code, 3);

        if (requestBody.getWay().equals("CELLPHONE")) {
            // TODO: ?????? ?????????
            // messageSender.sendMessageToOne(MessageSendPurpose.VERIFY_CELLPHONE_NUMBER, phoneNumber, verificationCode);
            System.out.println(code);
        }
        else {
            throw new UnprocessableEntityException("way??? ???????????? ????????????.", 0);
        }

        return VerificationCodeSendResponse.builder()
                .effectiveMinute(3)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Validated(FindEmail.class)
    public VerificationCodeSendResponse sendCodeForFindEmail(@Valid UserVerifyRequest requestBody) throws Exception {
        if (userMapper.getUserByNameAndPhoneNumber(requestBody.getName(), requestBody.getPhoneNumber()) == null) {
            throw new NotFoundException("????????? ???????????? ????????????.", 1);
        }

        String code = RandomStringUtils.randomNumeric(6);
        redisClient.setString(FIND_EMAIL, requestBody.getPhoneNumber(), code, 3);

        switch (requestBody.getWay()) {
            case "CELLPHONE":
                // TODO: ?????? ?????????
                // messageSender.sendMessageToOne(MessageSendPurpose.FIND_EMAIL, emailFindDto.getPhone_number(), verificationCode);
                System.out.println(code);
                break;
            default:
                throw new UnprocessableEntityException("way??? ???????????? ????????????.", 0);
        }

        return VerificationCodeSendResponse.builder()
                .effectiveMinute(3)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Validated(FindPassword.class)
    public VerificationCodeSendResponse sendCodeForFindPassword(@Valid UserVerifyRequest requestBody) throws Exception {
        User user = userMapper.getUserByNameAndEmail(requestBody.getName(), requestBody.getEmail());

        if (user == null) {
            throw new NotFoundException("????????? ???????????? ????????????.", 1);
        }

        String code = RandomStringUtils.randomNumeric(6);
        redisClient.setString(FIND_PASSWORD, user.getPhoneNumber(), code, 5);

        switch (requestBody.getWay()) {
            case "EMAIL":
                Context context = new Context();
                context.setVariable("code", code);
                String htmlBody = new SpringTemplateEngine().process("register_verification", context);
                awsSes.sendEmail(requestBody.getEmail(), "[Mokasong] ???????????? ????????? ?????? ?????????????????????.", htmlBody);
                break;
            case "CELLPHONE":
                // TODO: ?????? ?????????
                // messageSender.sendMessageToOne(MessageSendPurpose.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode);
                System.out.println(code);
                break;
            default:
                throw new UnprocessableEntityException("way??? ???????????? ?????? ?????? ??????????????????.", 0);
        }

        return VerificationCodeSendResponse.builder()
                .effectiveMinute(5)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse checkCodeForRegisterCellphone(CodeCheckRequest requestBody) throws Exception {
        if (userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber()) != null) {
            throw new ConflictException("?????? ??????????????? ???????????? ?????????????????????.", 1);
        }

        String codeInRedis = redisClient.getString(REGISTER_CELLPHONE, requestBody.getPhoneNumber());

        if (codeInRedis == null) {
            throw new PreconditionFailedException("?????? ?????? ????????? ?????????????????????.", 2);
        }
        if (!codeInRedis.equals(requestBody.getCode())) {
            throw new ForbiddenException("??????????????? ????????????.", 3);
        }

        redisClient.deleteKey(REGISTER_CELLPHONE, requestBody.getPhoneNumber());

        return new SuccessfulResponse();
    }

    @Override
    @Transactional(readOnly = true)
    public EmailFindSuccessResponse checkCodeForFindEmail(CodeCheckRequest requestBody) throws Exception {
        User user = userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber());
        if (user == null) {
            throw new NotFoundException("????????? ???????????? ????????????.", 1);
        }

        String codeInRedis = redisClient.getString(FIND_EMAIL, requestBody.getPhoneNumber());

        if (codeInRedis == null) {
            throw new PreconditionFailedException("?????? ?????? ????????? ?????????????????????.", 2);
        }
        if (!codeInRedis.equals(requestBody.getCode())) {
            throw new ForbiddenException("??????????????? ????????????.", 3);
        }

        redisClient.deleteKey(FIND_EMAIL, requestBody.getPhoneNumber());

        return EmailFindSuccessResponse.builder()
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    @Validated(FindPassword.class)
    public SuccessfulResponse checkCodeFindPassword(@Valid CodeCheckRequest requestBody) throws Exception {
        User user = userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber());
        if (user == null) {
            throw new NotFoundException("????????? ???????????? ????????????.", 1);
        }

        String codeInRedis = redisClient.getString(FIND_PASSWORD, requestBody.getPhoneNumber());

        if (codeInRedis == null) {
            throw new PreconditionFailedException("?????? ?????? ????????? ?????????????????????.", 2);
        }
        if (!codeInRedis.equals(requestBody.getCode())) {
            throw new ForbiddenException("??????????????? ????????????.", 3);
        }

        userMapper.updateUser(user.changeToNewPassword(requestBody.getNewPassword()));

        redisClient.deleteKey(FIND_PASSWORD, requestBody.getPhoneNumber());

        return new SuccessfulResponse();
    }

    @Override
    @Transactional
    public SuccessfulResponse changeToStandingByRegister(RegisterRequest requestBody) throws Exception {
        if (userMapper.getUserByEmail(requestBody.getEmail()) != null) {
            throw new ConflictException("???????????? ???????????????.", 1);
        }
        if (userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber()) != null) {
            throw new ConflictException("????????? ????????? ???????????????.", 2);
        }

        /* '???????????? ?????? ??????'?????? '?????? ??????'?????? ???????????? ????????? ????????? ?????? DB??? ?????? ????????????.
            ?????? ????????? ??????????????? ????????? ?????? ?????? ????????? ?????? ????????? ???????????? ?????????. */
        String registerToken;
        do {
            registerToken = RandomStringUtils.randomAlphanumeric(255);
        } while (userMapper.getUserByRegisterToken(registerToken) != null);

        userMapper.createUser(new User(requestBody, registerToken));

        // TODO: ?????? ??????????????? real host??? ?????????
        String url = "http://localhost:8080/user/register/" + registerToken;

        // TODO: ?????? ??????????????? html??? ????????? ????????????
        Context context = new Context();
        context.setVariable("register_token", registerToken);
        String htmlBody = new SpringTemplateEngine().process("register_verification", context);
        awsSes.sendEmail(requestBody.getEmail(), "[Mokasong] ??????????????? ?????? ???????????? ??????????????????.", htmlBody);

        return new SuccessfulResponse();
    }

    @Override
    @Transactional
    public void register(String registerToken) throws Exception {
        User user = userMapper.getUserByRegisterToken(registerToken);

        if (user == null || user.getAuthority() != Authority.STAND_BY_REGISTER) {
            throw new UnauthorizedException("???????????? ?????? ???????????????.", 1);
        }

        userMapper.updateUser(user.changeToRegular());
    }
}
