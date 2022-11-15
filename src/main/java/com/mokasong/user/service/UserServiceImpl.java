package com.mokasong.user.service;

import com.mokasong.common.exception.custom.*;
import com.mokasong.common.dto.response.DuplicateCheckResponse;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.util.*;
import com.mokasong.user.entity.User;
import com.mokasong.user.dto.request.LoginRequest;
import com.mokasong.user.dto.request.UserVerifyRequest;
import com.mokasong.user.dto.request.RegisterRequest;
import com.mokasong.user.dto.request.CodeCheckRequest;
import com.mokasong.user.dto.response.normal.EmailFindSuccessResponse;
import com.mokasong.user.dto.response.normal.LoginSuccessResponse;
import com.mokasong.user.dto.response.normal.VerificationCodeSendResponse;
import com.mokasong.user.repository.AdminUserMapper;
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
import static com.mokasong.common.util.UserHandler.getLoggedInUser;

@Service
@Validated
public class UserServiceImpl implements UserService {
    private final AdminUserMapper adminUserMapper;
    private final UserMapper userMapper;
    private final JwtHandler jwtHandler;
    private final AwsSes awsSes;
    private final MessageSender messageSender;
    private final RedisClient redisClient;

    @Autowired
    public UserServiceImpl(AdminUserMapper adminUserMapper, UserMapper userMapper, JwtHandler jwtHandler,
                           AwsSes awsSes, MessageSender messageSender, RedisClient redisClient) {
        this.adminUserMapper = adminUserMapper;
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
            throw new NotFoundException("회원이 존재하지 않습니다.", 1);
        }
        if (user.getAuthority() == Authority.STAND_BY_REGISTER) {
            throw new ForbiddenException("정식 회원이 아닙니다.", 2);
        }

        user.changeLastLoginTimeToNow();
        userMapper.updateUser(user);

        return LoginSuccessResponse.builder()
                .success(true)
                .accessToken(jwtHandler.generateToken(user.getUserId(), user.getSecretKey(), 72))
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse logout() throws Exception {
        User user = getLoggedInUser();

        user.changeLastLogoutTimeToNow();
        userMapper.updateUser(user);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
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
            throw new ConflictException("이미 회원정보에 존재하는 전화번호입니다.", 1);
        }

        String code = RandomStringUtils.randomNumeric(6);
        redisClient.setString(REGISTER_CELLPHONE, requestBody.getPhoneNumber(), code, 3);

        if (requestBody.getWay().equals("CELLPHONE")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.VERIFY_CELLPHONE_NUMBER, phoneNumber, verificationCode);
            System.out.println(code);
        }
        else {
            throw new UnprocessableEntityException("way가 유효하지 않습니다.", "way", requestBody.getWay());
        }

        return VerificationCodeSendResponse.builder()
                .success(true)
                .effectiveMinute(3)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Validated(FindEmail.class)
    public VerificationCodeSendResponse sendCodeForFindEmail(@Valid UserVerifyRequest requestBody) throws Exception {
        if (userMapper.getUserByNameAndPhoneNumber(requestBody.getName(), requestBody.getPhoneNumber()) == null) {
            throw new NotFoundException("유저가 존재하지 않습니다.", 1);
        }

        String code = RandomStringUtils.randomNumeric(6);
        redisClient.setString(FIND_EMAIL, requestBody.getPhoneNumber(), code, 3);

        switch (requestBody.getWay()) {
            case "CELLPHONE":
                // TODO: 다시 살리기
                // messageSender.sendMessageToOne(MessageSendPurpose.FIND_EMAIL, emailFindDto.getPhone_number(), verificationCode);
                System.out.println(code);
                break;
            default:
                throw new UnprocessableEntityException("way가 유효하지 않습니다.", "way", requestBody.getWay());
        }

        return VerificationCodeSendResponse.builder()
                .success(true)
                .effectiveMinute(3)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    @Validated(FindPassword.class)
    public VerificationCodeSendResponse sendCodeForFindPassword(@Valid UserVerifyRequest requestBody) throws Exception {
        User user = userMapper.getUserByNameAndEmail(requestBody.getName(), requestBody.getEmail());

        if (user == null) {
            throw new NotFoundException("유저가 존재하지 않습니다.", 1);
        }

        String code = RandomStringUtils.randomNumeric(6);
        redisClient.setString(FIND_PASSWORD, user.getPhoneNumber(), code, 5);

        switch (requestBody.getWay()) {
            case "EMAIL":
                Context context = new Context();
                context.setVariable("code", code);
                String htmlBody = new SpringTemplateEngine().process("register_verification", context);
                awsSes.sendEmail(requestBody.getEmail(), "[Mokasong] 비밀번호 찾기를 위한 인증번호입니다.", htmlBody);
                break;
            case "CELLPHONE":
                // TODO: 다시 살리기
                // messageSender.sendMessageToOne(MessageSendPurpose.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode);
                System.out.println(code);
                break;
            default:
                throw new UnprocessableEntityException("way에 올바르지 않은 값이 들어있습니다.", "way", requestBody.getWay());
        }

        return VerificationCodeSendResponse.builder()
                .success(true)
                .effectiveMinute(5)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse checkCodeForRegisterCellphone(CodeCheckRequest requestBody) throws Exception {
        if (userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber()) != null) {
            throw new ConflictException("이미 회원정보에 존재하는 전화번호입니다.", 1);
        }

        String codeInRedis = redisClient.getString(REGISTER_CELLPHONE, requestBody.getPhoneNumber());

        if (codeInRedis == null) {
            throw new PreconditionFailedException("인증 가능 시간이 만료되었습니다.", 2);
        }
        if (!codeInRedis.equals(requestBody.getCode())) {
            throw new ForbiddenException("인증번호가 다릅니다.", 3);
        }

        redisClient.deleteKey(REGISTER_CELLPHONE, requestBody.getPhoneNumber());

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public EmailFindSuccessResponse checkCodeForFindEmail(CodeCheckRequest requestBody) throws Exception {
        User user = userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber());
        if (user == null) {
            throw new NotFoundException("회원이 존재하지 않습니다.", 1);
        }

        String codeInRedis = redisClient.getString(FIND_EMAIL, requestBody.getPhoneNumber());

        if (codeInRedis == null) {
            throw new PreconditionFailedException("인증 가능 시간이 만료되었습니다.", 2);
        }
        if (!codeInRedis.equals(requestBody.getCode())) {
            throw new ForbiddenException("인증번호가 다릅니다.", 3);
        }

        redisClient.deleteKey(FIND_EMAIL, requestBody.getPhoneNumber());

        return EmailFindSuccessResponse.builder()
                .success(true)
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    @Validated(FindPassword.class)
    public SuccessfulResponse checkCodeFindPassword(@Valid CodeCheckRequest requestBody) throws Exception {
        User user = userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber());
        if (user == null) {
            throw new NotFoundException("회원이 존재하지 않습니다.", 1);
        }

        String codeInRedis = redisClient.getString(FIND_PASSWORD, requestBody.getPhoneNumber());

        if (codeInRedis == null) {
            throw new PreconditionFailedException("인증 가능 시간이 만료되었습니다.", 2);
        }
        if (!codeInRedis.equals(requestBody.getCode())) {
            throw new ForbiddenException("인증번호가 다릅니다.", 3);
        }

        userMapper.updateUser(user.changeToNewPassword(requestBody.getNewPassword()));

        redisClient.deleteKey(FIND_PASSWORD, requestBody.getPhoneNumber());

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse changeToStandingByRegister(RegisterRequest requestBody) throws Exception {
        if (userMapper.getUserByEmail(requestBody.getEmail()) != null) {
            throw new ConflictException("이메일이 중복됩니다.", 1);
        }
        if (userMapper.getUserByPhoneNumber(requestBody.getPhoneNumber()) != null) {
            throw new ConflictException("휴대폰 번호가 중복됩니다.", 2);
        }

        /* '회원가입 대기 상태'에서 '정식 회원'으로 전환할때 사용할 토큰을 회원 DB에 함께 저장한다.
            가입 토큰은 악의적으로 사용될 일이 없기 때문에 만료 시간을 설정하지 않는다. */
        String registerToken;
        do {
            registerToken = RandomStringUtils.randomAlphanumeric(255);
        } while (userMapper.getUserByRegisterToken(registerToken) != null);

        userMapper.createUser(new User(requestBody, registerToken));

        // TODO: 실제 배포시에는 real host로 바꿀것
        String url = "http://localhost:8080/user/register/" + registerToken;

        // TODO: 실제 배포시에는 html로 만들어 전송할것
        Context context = new Context();
        context.setVariable("register_token", registerToken);
        String htmlBody = new SpringTemplateEngine().process("register_verification", context);
        awsSes.sendEmail(requestBody.getEmail(), "[Mokasong] 회원가입을 위해 이메일을 인증해주세요.", htmlBody);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public void register(String registerToken) throws Exception {
        User user = userMapper.getUserByRegisterToken(registerToken);

        if (user == null || user.getAuthority() != Authority.STAND_BY_REGISTER) {
            throw new UnauthorizedException("유효하지 않은 요청입니다.", 1);
        }

        userMapper.updateUser(user.changeToRegular());
    }
}
