package com.mokasong.user.service;

import com.mokasong.common.response.BaseResponse;
import com.mokasong.common.response.NormalResponse;
import com.mokasong.common.state.RedisCategory;
import com.mokasong.common.util.AwsSes;
import com.mokasong.common.util.MessageSender;
import com.mokasong.common.util.RedisClient;
import com.mokasong.user.domain.User;
import com.mokasong.user.dto.UserFindDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.exception.UserFindFailException;
import com.mokasong.user.repository.UserMapper;
import com.mokasong.user.validation.UserDataValidationGroups.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;

import static com.mokasong.common.exception.CustomExceptionList.*;

@Service
@Validated
public class UserFindService {
    private final UserMapper userMapper;
    private final AwsSes awsSes;
    private final MessageSender messageSender;
    private final RedisClient redisClient;

    @Autowired
    public UserFindService(
            UserMapper userMapper, AwsSes awsSes,
            MessageSender messageSender, RedisClient redisClient) {
        this.userMapper = userMapper;
        this.awsSes = awsSes;
        this.messageSender = messageSender;
        this.redisClient = redisClient;
    }

    @Transactional(readOnly = true)
    @Validated(FindEmail.class)
    public BaseResponse sendCodeForFindEmail(@Valid UserFindDto userFindDto) throws Exception {
        User selectedUser = userMapper.getUserByNameAndPhoneNumber(userFindDto.getName(), userFindDto.getPhone_number());

        // DB에 회원 정보가 없거나 탈퇴한 회원이라면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new UserFindFailException(USER_NOT_EXIST);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);

        // 인증번호의 유효 시간은 10분
        redisClient.setString(RedisCategory.FIND_EMAIL, userFindDto.getPhone_number(), verificationCode, 10);

        // 인증방법이 휴대폰이라면 Coolsms로 문자 메시지 전송
        if (userFindDto.getWay().equals("CELLPHONE")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.FIND_EMAIL, emailFindDto.getPhone_number(), verificationCode);
            System.out.println(verificationCode);
        }
        // way에 EMAIL 또는 엉뚱한 값이 들어있을 경우
        else {
            throw new UserFindFailException(INVALID_REQUEST_DATA);
        }

        return new NormalResponse("인증번호를 전송하였습니다. 인증번호의 유효시간은 10분입니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional(readOnly = true)
    @Validated(FindPassword.class)
    public BaseResponse sendCodeForFindPassword(@Valid UserFindDto userFindDto) throws Exception {
        User selectedUser = userMapper.getUserByNameAndEmail(userFindDto.getName(), userFindDto.getEmail());

        // DB에 회원 정보가 없거나 탈퇴한 회원이라면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new UserFindFailException(USER_NOT_EXIST);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);

        // 인증번호의 유효시간은 10분
        redisClient.setString(RedisCategory.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode, 10);

        // 인증 방법이 이메일이라면 AWS SES로 메일 전송
        if (userFindDto.getWay().equals("EMAIL")) {
            // TODO: 실제 배포시에는 html로 바꿀것
            awsSes.sendEmail(userFindDto.getEmail(), "[Mokasong] 비밀번호 찾기를 위한 인증번호입니다.", verificationCode);
        }
        // 인증방법이 휴대폰이라면 Coolsms로 문자 메시지 전송
        else if (userFindDto.getWay().equals("CELLPHONE")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode);
            System.out.println(verificationCode);
        }
        // way에 EMAIL, CELLPHONE 외의 엉뚱한 값이 들어있을 경우
        else {
            throw new UserFindFailException(INVALID_REQUEST_DATA);
        }

        return new NormalResponse("인증번호를 전송하였습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional(readOnly = true)
    @Validated(FindEmail.class)
    public BaseResponse checkCodeForFindEmail(@Valid VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        User selectedUser = userMapper.getUserByPhoneNumber(verificationCodeCheckDto.getPhone_number());

        // DB에 회원 정보가 없거나 탈퇴한 회원이라면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new UserFindFailException(USER_NOT_EXIST);
        }

        String codeInRedis = redisClient.getString(RedisCategory.FIND_EMAIL, verificationCodeCheckDto.getPhone_number());

        // redis server에 인증번호가 없으면 인증시간 만료로 간주
        if (codeInRedis == null) {
            throw new UserFindFailException(VERIFICATION_TIME_EXPIRE);
        }
        // redis server에 있는 인증번호 인증번호와 다른 경우
        if (!codeInRedis.equals(verificationCodeCheckDto.getCode())) {
            throw new UserFindFailException(VERIFICATION_CODE_NOT_EQUAL);
        }

        redisClient.deleteKey(RedisCategory.FIND_EMAIL, verificationCodeCheckDto.getPhone_number());

        return new NormalResponse("인증되었습니다. 이메일 주소를 확인해주세요.", new HashMap<>() {{
            put("success", true);
            put("email", selectedUser.getEmail());
        }});
    }

    @Transactional
    @Validated(FindPassword.class)
    public BaseResponse checkCodeFindPassword(@Valid VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        User selectedUser = userMapper.getUserByPhoneNumber(verificationCodeCheckDto.getPhone_number());

        // DB에 회원 정보가 없거나 탈퇴한 회원이라면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new UserFindFailException(USER_NOT_EXIST);
        }

        String codeInRedis = redisClient.getString(RedisCategory.FIND_PASSWORD, verificationCodeCheckDto.getPhone_number());

        // redis server에 인증번호가 없으면 인증시간 만료로 간주
        if (codeInRedis == null) {
            throw new UserFindFailException(VERIFICATION_TIME_EXPIRE);
        }
        // redis server에 있는 인증번호 인증번호와 다른 경우
        if (!codeInRedis.equals(verificationCodeCheckDto.getCode())) {
            throw new UserFindFailException(VERIFICATION_CODE_NOT_EQUAL);
        }

        selectedUser.changeToNewPassword(verificationCodeCheckDto.getNew_password());
        userMapper.updateUser(selectedUser);

        redisClient.deleteKey(RedisCategory.FIND_PASSWORD, verificationCodeCheckDto.getPhone_number());

        return new NormalResponse("비밀번호가 성공적으로 변경되었습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }
}