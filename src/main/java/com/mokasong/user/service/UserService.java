package com.mokasong.user.service;

import com.mokasong.common.response.BaseResponse;
import com.mokasong.common.response.NormalResponse;
import com.mokasong.common.state.RedisCategory;
import com.mokasong.common.util.*;
import com.mokasong.user.domain.User;
import com.mokasong.user.dto.LoginDto;
import com.mokasong.user.dto.UserVerifyDto;
import com.mokasong.user.dto.UserRegisterDto;
import com.mokasong.user.dto.VerificationCodeCheckDto;
import com.mokasong.user.exception.*;
import com.mokasong.user.repository.UserMapper;
import com.mokasong.user.state.Authority;
import com.mokasong.user.validation.UserDataValidationGroups.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.HashMap;

import static com.mokasong.common.exception.CustomExceptionList.*;

@Service
@Validated
public class UserService {
    private final UserMapper userMapper;
    private final JwtHandler jwtHandler;
    private final AwsSes awsSes;
    private final MessageSender messageSender;
    private final RedisClient redisClient;

    @Autowired
    public UserService(UserMapper userMapper, JwtHandler jwtHandler, AwsSes awsSes,
                       MessageSender messageSender, RedisClient redisClient) {
        this.userMapper = userMapper;
        this.jwtHandler = jwtHandler;
        this.awsSes = awsSes;
        this.messageSender = messageSender;
        this.redisClient = redisClient;
    }

    @Transactional
    public BaseResponse login(LoginDto dto) throws Exception {
        User user = userMapper.getUserByEmail(dto.getEmail());

        // 유저가 조회되지 않는다면
        if (user == null) {
            throw new LoginFailException(USER_NOT_EXIST);
        }
        // 유저가 조회는 되지만, 탈퇴했거나 비밀번호가 다르다면
        if ((user.getIs_deleted()) || (!this.passwordValid(dto.getPassword(), user.getPassword()))) {
            throw new LoginFailException(USER_NOT_EXIST);
        }
        // 회원가입 대기 상태라면 (정식 회원이 아니라면)
        if (user.getAuthority() == Authority.STAND_BY_REGISTER) {
            throw new LoginFailException(USER_NOT_REGULAR);
        }

        // 1시간의 유효시간을 가지는 access token 발급
        String accessToken = jwtHandler.generateToken(user.getUser_id(), 1);

        user.changeLastLoginTimeToNow();
        userMapper.updateUser(user);

        return new NormalResponse("로그인 되었습니다.", new HashMap<>() {{
            put("success", true);
            put("access_token", accessToken);
        }});
    }

    @Transactional
    public BaseResponse logout() throws Exception {
        String accessToken = jwtHandler.getTokenInHttpHeader();
        Long userId = jwtHandler.discoverUserId(accessToken);
        User user = userMapper.getUserById(userId);

        // access token에 담긴 user id로 유저를 조회가 안된다면
        if (user == null) {
            throw new LogoutFailException(USER_NOT_EXIST);
        }

        user.changeLastLogoutTimeToNow();
        userMapper.updateUser(user);

        return new NormalResponse("로그아웃 되었습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional(readOnly = true)
    public BaseResponse getExistenceOfEmail(String email) throws Exception {
        if (this.emailExist(email)) {
            return new NormalResponse("이미 회원정보에 존재하는 이메일입니다.", new HashMap<>() {{
                put("email_existence", true);
            }});
        }

        return new NormalResponse("등록이 가능한 이메일입니다.", new HashMap<>() {{
            put("email_existence", false);
        }});
    }

    @Transactional(readOnly = true)
    public BaseResponse getExistenceOfCellphone(String phoneNumber) throws Exception {
        if (this.phoneNumberExist(phoneNumber)) {
            return new NormalResponse("이미 회원정보에 존재하는 휴대폰번호입니다.", new HashMap<>() {{
                put("phone_number_existence", true);
            }});
        }

        return new NormalResponse("등록이 가능한 휴대폰 번호입니다.", new HashMap<>() {{
            put("phone_number_existence", false);
        }});
    }

    @Transactional(readOnly = true)
    @Validated(RegisterCellPhone.class)
    public BaseResponse sendCodeForRegisterCellphone(UserVerifyDto dto) throws Exception {
        // 휴대폰 번호가 이미 회원 정보에 존재한다면
        if (this.phoneNumberExist(dto.getPhone_number())) {
            throw new VerificationCodeSendException(PHONE_NUMBER_ALREADY_EXIST);
        }

        // 인증번호 생성
        String verificationCode = RandomStringUtils.randomNumeric(6);

        // 인증번호의 유효시간은 3분
        redisClient.setString(RedisCategory.REGISTER_CELLPHONE, dto.getPhone_number(), verificationCode, 3);

        // 인증 방법이 휴대폰이라면 Coolsms로 문자 메시지 전송
        if (dto.getWay().equals("CELLPHONE")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.VERIFY_CELLPHONE_NUMBER, phoneNumber, verificationCode);
            System.out.println(verificationCode);
        }
        // way에 EMAIL 또는 엉뚱한 값이 들어있을 경우
        else {
            throw new VerificationCodeSendException(INVALID_REQUEST_DATA);
        }

        return new NormalResponse("인증번호를 전송하였습니다. 3분안에 인증해주세요.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional(readOnly = true)
    @Validated(FindEmail.class)
    public BaseResponse sendCodeForFindEmail(@Valid UserVerifyDto dto) throws Exception {
        User selectedUser = userMapper.getUserByNameAndPhoneNumber(dto.getName(), dto.getPhone_number());

        // 유저 정보가 없거나 탈퇴했다면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new VerificationCodeSendException(USER_NOT_EXIST);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);

        // 인증번호의 유효 시간은 3분
        redisClient.setString(RedisCategory.FIND_EMAIL, dto.getPhone_number(), verificationCode, 3);

        // 인증방법이 휴대폰이라면 Coolsms로 문자 메시지 전송
        if (dto.getWay().equals("CELLPHONE")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.FIND_EMAIL, emailFindDto.getPhone_number(), verificationCode);
            System.out.println(verificationCode);
        }
        // way에 EMAIL 또는 엉뚱한 값이 들어있을 경우
        else {
            throw new VerificationCodeSendException(INVALID_REQUEST_DATA);
        }

        return new NormalResponse("인증번호를 전송하였습니다. 3분안에 인증해주세요.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional(readOnly = true)
    @Validated(FindPassword.class)
    public BaseResponse sendCodeForFindPassword(@Valid UserVerifyDto dto) throws Exception {
        User selectedUser = userMapper.getUserByNameAndEmail(dto.getName(), dto.getEmail());

        // 유저 정보가 없거나 탈퇴했다면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new VerificationCodeSendException(USER_NOT_EXIST);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);

        // 인증번호의 유효시간은 5분
        redisClient.setString(RedisCategory.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode, 5);

        // 인증 방법이 이메일이라면 AWS SES로 메일 전송
        if (dto.getWay().equals("EMAIL")) {
            // TODO: 실제 배포시에는 html로 바꿀것
            awsSes.sendEmail(dto.getEmail(), "[Mokasong] 비밀번호 찾기를 위한 인증번호입니다.", verificationCode);
        }
        // 인증방법이 휴대폰이라면 Coolsms로 문자 메시지 전송
        else if (dto.getWay().equals("CELLPHONE")) {
            // TODO: 다시 살리기
            // messageSender.sendMessageToOne(MessageSendPurpose.FIND_PASSWORD, selectedUser.getPhone_number(), verificationCode);
            System.out.println(verificationCode);
        }
        // way에 EMAIL, CELLPHONE 외의 엉뚱한 값이 들어있을 경우
        else {
            throw new VerificationCodeSendException(INVALID_REQUEST_DATA);
        }

        return new NormalResponse("인증번호를 전송하였습니다. 5분안에 인증해주세요.", new HashMap<>() {{
            put("success", true);
        }});
    }

    public BaseResponse checkCodeForRegisterCellphone(VerificationCodeCheckDto dto) throws Exception {
        String codeInRedis = redisClient.getString(RedisCategory.REGISTER_CELLPHONE, dto.getPhone_number());

        // redis server에 인증번호가 없으면 인증시간 만료로 간주
        if (codeInRedis == null) {
            throw new VerificationCodeCheckException(VERIFICATION_TIME_EXPIRE);
        }
        // redis server에 있는 인증번호와 다른 경우
        if (!codeInRedis.equals(dto.getCode())) {
            throw new VerificationCodeCheckException(VERIFICATION_CODE_NOT_EQUAL);
        }

        // 회원가입 대기상태 전환 API 요청 시 휴대폰 번호 조작 방지용 토큰 생성
        String randomString = RandomStringUtils.randomAlphanumeric(100);
        redisClient.setString(RedisCategory.CHANGE_TO_STAND_BY_REGULAR, dto.getPhone_number(), randomString, 3);

        redisClient.deleteKey(RedisCategory.REGISTER_CELLPHONE, dto.getPhone_number());

        return new NormalResponse("인증이 완료되었습니다.", new HashMap<>() {{
            put("success", true);
            put("verification_token", randomString);
        }});
    }

    @Transactional(readOnly = true)
    public BaseResponse checkCodeForFindEmail(@Valid VerificationCodeCheckDto dto) throws Exception {
        User selectedUser = userMapper.getUserByPhoneNumber(dto.getPhone_number());

        // 휴대폰 번호로 조회한 유저 정보가 없거나 탈퇴했다면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new VerificationCodeCheckException(USER_NOT_EXIST);
        }

        String codeInRedis = redisClient.getString(RedisCategory.FIND_EMAIL, dto.getPhone_number());

        // redis server에 인증번호가 없으면 인증시간 만료로 간주
        if (codeInRedis == null) {
            throw new VerificationCodeCheckException(VERIFICATION_TIME_EXPIRE);
        }
        // redis server에 있는 인증번호와 다른 경우
        if (!codeInRedis.equals(dto.getCode())) {
            throw new VerificationCodeCheckException(VERIFICATION_CODE_NOT_EQUAL);
        }

        redisClient.deleteKey(RedisCategory.FIND_EMAIL, dto.getPhone_number());

        return new NormalResponse("인증되었습니다. 이메일 주소를 확인해주세요.", new HashMap<>() {{
            put("success", true);
            put("email", selectedUser.getEmail());
        }});
    }

    @Transactional
    @Validated(FindPassword.class)
    public BaseResponse checkCodeFindPassword(@Valid VerificationCodeCheckDto dto) throws Exception {
        User selectedUser = userMapper.getUserByPhoneNumber(dto.getPhone_number());

        // 휴대폰 번호로 조회한 유저 정보가 없거나 탈퇴했다면
        if ((selectedUser == null) || (selectedUser.getIs_deleted())) {
            throw new VerificationCodeCheckException(USER_NOT_EXIST);
        }

        String codeInRedis = redisClient.getString(RedisCategory.FIND_PASSWORD, dto.getPhone_number());

        // redis server에 인증번호가 없으면 인증시간 만료로 간주
        if (codeInRedis == null) {
            throw new VerificationCodeCheckException(VERIFICATION_TIME_EXPIRE);
        }
        // redis server에 있는 인증번호와 다른 경우
        if (!codeInRedis.equals(dto.getCode())) {
            throw new VerificationCodeCheckException(VERIFICATION_CODE_NOT_EQUAL);
        }

        selectedUser.changeToNewPassword(dto.getNew_password());
        userMapper.updateUser(selectedUser);

        redisClient.deleteKey(RedisCategory.FIND_PASSWORD, dto.getPhone_number());

        return new NormalResponse("비밀번호가 성공적으로 변경되었습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional
    public BaseResponse changeToStandingByRegister(UserRegisterDto dto) throws Exception {
        String codeInRedis = redisClient.getString(RedisCategory.CHANGE_TO_STAND_BY_REGULAR, dto.getPhone_number());

        // redis server에 휴대폰 번호 조작 방지용 토큰이 없으면 인증시간 만료로 간주
        if (codeInRedis == null) {
            throw new UserRegisterFailException(REQUEST_TIME_EXPIRE_FOR_USER_REGISTER);
        }
        // redis server에 있는 휴대폰 번호 조작 방지용 토큰과 요청 토큰이 다를 경우
        if (!codeInRedis.equals(dto.getVerification_token())) {
            throw new UserRegisterFailException(VERIFICATION_TOKEN_NOT_EQUAL);
        }

        User selectedUserByPhoneNumber = userMapper.getUserByPhoneNumber(dto.getPhone_number());
        // 휴대폰 번호로 유저 조회가 되었을때
        if (selectedUserByPhoneNumber != null) {
            // 탈퇴한 유저가 아니라면 휴대폰 번호가 회원정보에 이미 존재한다고 알려준다.
            if (!selectedUserByPhoneNumber.getIs_deleted()) {
                throw new UserRegisterFailException(PHONE_NUMBER_ALREADY_EXIST);
            }
            // 탈퇴한 유저라면 해당 레코드 삭제 (users 테이블의 phone_number 컬럼의 unique 제약조건 때문)
            else {
                userMapper.deleteUserById(selectedUserByPhoneNumber.getUser_id());
            }
        }

        User selectedUserByEmail = userMapper.getUserByEmail(dto.getEmail());
        // 이메일로 유저 조회가 되었을 때
        if (selectedUserByEmail != null) {
            // 탈퇴한 유저가 아니라면 유저가 이미 존재한다고 알려준다.
            if (!selectedUserByEmail.getIs_deleted()) {
                throw new UserRegisterFailException(EMAIL_ALREADY_EXIST);
            }
            // 탈퇴한 유저라면 해당 레코드 삭제 (users 테이블의 email 컬럼의 unique 제약조건 때문)
            else {
                userMapper.deleteUserById(selectedUserByEmail.getUser_id());
            }
        }

        String registerToken;
        User selectedUserByRegisterToken;

        /* '회원가입 대기 상태'에서 '정식 회원'으로 전환할때 사용할 토큰을 회원 DB에 함께 저장한다.
            가입 토큰은 악의적으로 사용될 일이 없기 때문에 만료 시간을 설정하지 않는다. */
        do {
            registerToken = RandomStringUtils.randomAlphanumeric(200);
            selectedUserByRegisterToken = userMapper.getUserByRegisterToken(registerToken);
        } while (selectedUserByRegisterToken != null);

        User user = new User();
        user.initializeForStandingByRegister(dto, registerToken);
        userMapper.createUser(user);

        // TODO: 실제 배포시에는 real host로 바꿀것
        String verificationUrl = "http://localhost:8080/user/register/" + registerToken;

        // TODO: 실제 배포시에는 html로 만들어 전송할것
        awsSes.sendEmail(user.getEmail(), "[Mokasong] 회원가입을 위해 이메일을 인증해주세요.", verificationUrl);

        redisClient.deleteKey(RedisCategory.CHANGE_TO_STAND_BY_REGULAR, dto.getPhone_number());

        return new NormalResponse("입력하신 이메일 주소로 인증 메일을 전송하였습니다. 인증을 완료하셔야 로그인하실 수 있습니다.", new HashMap<>() {{
            put("success", true);
        }});
    }

    @Transactional
    public void register(String registerToken) throws Exception {
        User user = userMapper.getUserByRegisterToken(registerToken);

        // 가입 토큰으로 유저 조회가 안된다면
        if (user == null) {
            throw new UserRegisterFailException(INVALID_ACCESS);
        }
        // 유저 조회는 되지만, 탈퇴했거나 권한이 이미 정식 회원이거나 어드민인경우
        if ((user.getIs_deleted()) || (user.getAuthority() != Authority.STAND_BY_REGISTER)) {
            throw new UserRegisterFailException(INVALID_ACCESS);
        }

        // 정식 회원 전환
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

    private boolean passwordValid(String passwordInRequest, String passwordInDatabase) {
        return BCrypt.checkpw(passwordInRequest, passwordInDatabase);
    }
}
