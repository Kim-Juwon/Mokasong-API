package com.mokasong.user.service;

import com.mokasong.common.exception.CustomExceptionList;
import com.mokasong.common.response.BaseResponse;
import com.mokasong.common.response.NormalResponse;
import com.mokasong.common.state.RedisCategory;
import com.mokasong.common.util.*;
import com.mokasong.user.domain.User;
import com.mokasong.user.dto.UserRegisterDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.exception.UserRegisterFailException;
import com.mokasong.user.exception.VerificationCodeException;
import com.mokasong.user.repository.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static com.mokasong.common.exception.CustomExceptionList.*;

@Service
public class UserRegisterService {
    private final UserMapper userMapper;
    private final JwtHandler jwtHandler;
    private final AwsSes awsSes;
    private final MessageSender messageSender;
    private final RedisClient redisClient;

    @Autowired
    public UserRegisterService(
            UserMapper userMapper, JwtHandler jwtHandler, AwsSes awsSes,
            MessageSender messageSender, RedisClient redisClient) {
        this.userMapper = userMapper;
        this.jwtHandler = jwtHandler;
        this.awsSes = awsSes;
        this.messageSender = messageSender;
        this.redisClient = redisClient;
    }

    public BaseResponse getExistenceOfEmail(String email) throws Exception {
        if (this.emailExist(email)) {
            return new NormalResponse("이미 회원정보에 존재하는 이메일입니다.", new HashMap<>() {{
                put("email_existence", true);
            }});
        }

        return new NormalResponse("가입이 가능한 이메일입니다.", new HashMap<>() {{
            put("email_existence", false);
        }});
    }

    public BaseResponse getExistenceOfPhoneNumber(String phoneNumber) throws Exception {
        if (this.phoneNumberExist(phoneNumber)) {
            return new NormalResponse("이미 회원정보에 존재하는 휴대폰번호입니다.", new HashMap<>() {{
                put("phone_number_existence", true);
            }});
        }

        return new NormalResponse("가입이 가능한 휴대폰 번호입니다.", new HashMap<>() {{
            put("phone_number_existence", false);
        }});
    }

    public BaseResponse sendVerificationCodeForPhoneNumber(String phoneNumber) throws Exception {
        // 휴대폰 번호 중복 2차 검사
        if (this.phoneNumberExist(phoneNumber)) {
            User user = userMapper.getUserByPhoneNumber(phoneNumber);

            CustomExceptionList phoneNumberAlreadyExist = PHONE_NUMBER_ALREADY_EXIST
                    .setMessage(String.format("%s 이메일로 이미 가입된 계정의 전화번호입니다.", StringHandler.hideIdOfEmail(user.getEmail())));

            throw new UserRegisterFailException(phoneNumberAlreadyExist);
        }

        // 인증번호 생성
        String verificationCode = RandomStringUtils.randomNumeric(6);

        // redis server에 인증번호 3분간 임시 저장
        redisClient.setString(RedisCategory.REGISTER_CELLPHONE, phoneNumber, verificationCode, 3);

        // 인증번호 전송
        // TODO: 다시 살리기
        // messageSender.sendMessageToOne(MessageSendPurpose.VERIFY_CELLPHONE_NUMBER, phoneNumber, verificationCode);
        System.out.println(verificationCode);

        return new NormalResponse("인증번호를 전송하였습니다. 3분안에 인증해주세요.", new HashMap<>() {{
            put("success", true);
        }});
    }

    // TODO: 메소드명 축약하기 (클래스명에서 이미 어떤 기능들이 있을지 암시하기 때문)
    public BaseResponse checkVerificationCodeForPhoneNumber(VerificationCodeCheckDto verificationCodeCheckDto) throws Exception {
        String phoneNumber = verificationCodeCheckDto.getPhone_number();
        String verificationCode = verificationCodeCheckDto.getCode();

        String verificationCodeInRedisServer = redisClient.getString(RedisCategory.REGISTER_CELLPHONE, phoneNumber);

        // redis server에 인증번호가 없으면 인증시간 만료로 간주
        if (verificationCodeInRedisServer == null) {
            throw new VerificationCodeException(VERIFICATION_TIME_EXPIRE);
        }
        // redis server에 있는 인증번호가 다른 경우
        if (!verificationCodeInRedisServer.equals(verificationCode)) {
            throw new VerificationCodeException(VERIFICATION_CODE_NOT_EQUAL);
        }

        // 회원가입 대기상태 전환 API 요청 시 휴대폰 번호 조작 방지용 토큰 생성
        String randomString = RandomStringUtils.randomAlphanumeric(100);
        redisClient.setString(RedisCategory.CHANGE_TO_STAND_BY_REGULAR, phoneNumber, randomString, 3);

        redisClient.deleteKey(RedisCategory.REGISTER_CELLPHONE, phoneNumber);

        return new NormalResponse("인증이 완료되었습니다.", new HashMap<>() {{
            put("success", true);
            put("verification_token", randomString);
        }});
    }

    @Transactional
    public BaseResponse changeToStandingByRegister(UserRegisterDto userRegisterDto) throws Exception {
        String phoneNumber = userRegisterDto.getPhone_number();
        String email = userRegisterDto.getEmail();

        String verificationTokenInRedisServer = redisClient.getString(RedisCategory.CHANGE_TO_STAND_BY_REGULAR, phoneNumber);

        // redis server에 휴대폰 번호 저작 방지용 토큰이 없으면 인증시간 만료로 간주
        if (verificationTokenInRedisServer == null) {
            throw new UserRegisterFailException(REQUEST_TIME_EXPIRE_FOR_USER_REGISTER);
        }
        // redis server에 있는 토큰이 다른 경우
        if (!verificationTokenInRedisServer.equals(userRegisterDto.getVerification_token())) {
            throw new UserRegisterFailException(VERIFICATION_TOKEN_NOT_EQUAL);
        }

        User selectedUserByPhoneNumber = userMapper.getUserByPhoneNumber(phoneNumber);
        // 휴대폰 번호로 유저 조회가 되었을때
        if (selectedUserByPhoneNumber != null) {
            // 탈퇴(soft delete)한 유저가 아니라면 유저가 이미 존재한다고 알려줌
            if (!selectedUserByPhoneNumber.getIs_deleted()) {
                throw new UserRegisterFailException(PHONE_NUMBER_ALREADY_EXIST);
            }
            // 탈퇴(soft delete)한 유저라면 해당 레코드 삭제
            else {
                userMapper.deleteUserById(selectedUserByPhoneNumber.getUser_id());
            }
        }

        User selectedUserByEmail = userMapper.getUserByEmail(email);
        // 이메일로 유저 조회가 되었을 때
        if (selectedUserByEmail != null) {
            // 탈퇴(soft delete)한 유저가 아니라면 유저가 이미 존재한다고 알려줌
            if (!selectedUserByEmail.getIs_deleted()) {
                throw new UserRegisterFailException(EMAIL_ALREADY_EXIST);
            }
            // 탈퇴(soft delete)한 유저라면 해당 레코드 삭제
            else {
                userMapper.deleteUserById(selectedUserByEmail.getUser_id());
            }
        }

        String registerToken;
        User selectedUserByRegisterToken;

        // 회원가입 대기 상태 -> 정식 회원으로 전환할때 사용할 토큰을 회원 db에 함께 저장. 가입 토큰은 악의적으로 요청될 일이 없기 때문에 만료 시간이 없다.
        do {
            registerToken = RandomStringUtils.randomAlphanumeric(200);
            selectedUserByRegisterToken = userMapper.getUserByRegisterToken(registerToken);
        } while (selectedUserByRegisterToken != null);

        User user = new User();
        user.initializeForStandingByRegister(userRegisterDto, registerToken);
        userMapper.createUser(user);

        // TODO: 실제 배포시에는 real host로 바꿀것
        String url = "http://localhost:8080/user/register/" + registerToken;

        // TODO: 실제 배포시에는 html로 만들어 전송할것
        awsSes.sendEmail(user.getEmail(), "[Mokasong] 회원가입을 위해 이메일을 인증해주세요.", url);

        redisClient.deleteKey(RedisCategory.CHANGE_TO_STAND_BY_REGULAR, phoneNumber);

        return new NormalResponse("회원가입 대기 상태로 전환되었습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional
    public void register(String registerToken) throws Exception {
        User user = userMapper.getUserByRegisterToken(registerToken);

        if (user == null) {
            throw new UserRegisterFailException(INVALID_ACCESS);
        }

        // 가입 토큰이 유효하다면 정식 회원 전환
        user.changeAuthorityToRegular();
        userMapper.updateUser(user);
    }

    private boolean emailExist(String email) {
        User user = userMapper.getUserByEmail(email);

        if ((user == null) || (user.getIs_deleted())) {
            return false;
        }

        return true;
    }

    private boolean phoneNumberExist(String phoneNumber) {
        User user = userMapper.getUserByPhoneNumber(phoneNumber);

        if ((user == null) || (user.getIs_deleted())) {
            return false;
        }

        return true;
    }
}
