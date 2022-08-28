package com.mokasong.user.service;

import com.mokasong.common.response.BaseResponse;
import com.mokasong.common.response.NormalResponse;
import com.mokasong.common.state.RedisCategory;
import com.mokasong.common.util.AwsSes;
import com.mokasong.common.util.MessageSender;
import com.mokasong.common.util.RedisClient;
import com.mokasong.user.domain.User;
import com.mokasong.user.dto.UserFindDto;
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

import static com.mokasong.common.exception.CustomExceptionList.USER_NOT_EXIST;

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

        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new UserFindFailException(USER_NOT_EXIST);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);

        redisClient.setString(RedisCategory.FIND_EMAIL, userFindDto.getPhone_number(), verificationCode, 60);

        // TODO: 다시 살리기
        // messageSender.sendMessageToOne(MessageSendPurpose.FIND_EMAIL, emailFindDto.getPhone_number(), verificationCode);
        System.out.println(verificationCode);

        return new NormalResponse("인증번호를 전송하였습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional(readOnly = true)
    @Validated(FindPassword.class)
    public BaseResponse sendCodeForFindPassword(@Valid UserFindDto userFindDto) throws Exception {
        User selectedUser = userMapper.getUserByNameAndEmail(userFindDto.getName(), userFindDto.getEmail());

        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new UserFindFailException(USER_NOT_EXIST);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);

        redisClient.setString(RedisCategory.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode, 60);

        // 인증 방법이 이메일이라면 AWS SES 사용
        if (userFindDto.getWay().equals("email")) {
            // TODO: 실제 배포시에는 html로 바꿀것
            awsSes.sendEmail(userFindDto.getEmail(), "[Mokasong] 비밀번호 찾기를 위한 인증번호입니다.", verificationCode);
        }
        // 인증방법이 휴대폰이라면 Coolsms 사용
        if (userFindDto.getWay().equals("cellphone")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode);
            System.out.println(verificationCode);
        }

        return new NormalResponse("인증번호를 전송하였습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }
}