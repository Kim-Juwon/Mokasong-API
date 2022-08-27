package com.mokasong.user.service;

/*
@Service
public class UserFindServiceImpl implements UserFindService {
    private final UserMapper userMapper;
    private final AwsSes awsSes;
    private final Coolsms coolsms;
    private final ValueOperations<String, String> redisClient;

    @Autowired
    public UserFindServiceImpl(
        UserMapper userMapper, AwsSes awsSes,
        Coolsms coolsms, RedisTemplate<String, String> redisTemplate) {
        this.userMapper = userMapper;
        this.awsSes = awsSes;
        this.coolsms = coolsms;
        this.redisClient = redisTemplate.opsForValue();
    }

    @Override
    @Transactional
    public BaseResponse sendVerificationCodeForFindEmail(UserForVerification userForVerification) throws Exception {
        User selectedUser = userMapper.getUserByNameAndPhoneNumber(userForVerification.getName(), userForVerification.getPhone_number());

        if ((selectedUser == null) || (selectedUser.getAuthority() == Authority.STAND_BY_REGISTER)) {
            throw new UserFindFailException(IMPOSSIBLE_FIND_EMAIL_BY_PHONE_NUMBER);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);
        String messageText = String.format("[Mokasong](이메일 찾기 인증) 인증번호는 [%s]입니다.", verificationCode);

        // TODO: 다시 살리기
        // coolsms.sendMessageToOne(phoneNumber, messageText);
        System.out.println(verificationCode);

        String key = String.format("find-email/verification/code/%s", userForVerification.getPhone_number());
        redisClient.set(key, verificationCode);

        return new NormalResponse("인증번호가 전송되었습니다.", new HashMap<>() {{
            put("is_sent", true);
        }});
    }

    // TODO: 인증번호 만료시간 어떻게 설정할것인지 정하기
    @Override
    @Transactional
    public BaseResponse checkVerificationCodeForFindEmail(UserForVerification userForVerification) throws Exception {
        User selectedUser = userMapper.getUserByNameAndPhoneNumber(userForVerification.getName(), userForVerification.getPhone_number());

        // 이름과 전화번호가 매핑되는 회원 정보가 없는 경우는 인증 프로세스 실행 불가능
        if (selectedUser == null) {
            throw new UserFindFailException(IMPOSSIBLE_VERIFY_CODE);
        }

        String key = String.format("find-email/verification/code/%s", userForVerification.getPhone_number());
        String verificationCodeInRedis = redisClient.get(key);

        // 인증번호가 redis-server에 없을때
        if (verificationCodeInRedis == null) {
            throw new VerificationCodeException(REQUIRED_SEND_VERIFICATION_CODE);
        }

        String verificationCode = userForVerification.getVerification_code();
        // 인증번호가 불일치할때
        if (!verificationCodeInRedis.equals(verificationCode)) {
            throw new VerificationCodeException(NOT_EQUAL_VERIFICATION_CODE);
        }

        redisClient.getAndDelete(key);

        return new NormalResponse("인증이 완료되었습니다.", new HashMap<>(){{
            put("verification", true);
            put("user", new HashMap<String, Object>() {{
                put("email", selectedUser.getEmail());
                put("name", selectedUser.getName());
                put("registration_date", selectedUser.getCreated_at()); // 가입일
            }});
        }});
    }

    // TODO: 인증번호 만료시간 어떻게 설정할것인지 정하기
    /*@Override
    @Transactional
    public BaseResponse sendVerificationCodeForFindPassword(UserForFind userForFind) throws Exception {
        User selectedUser = userMapper.getUserByNameAndPhoneNumber(userForFind.getName(), userForFind.getEmail());

        // 이름과 전화번호가 매칭되는 회원 정보가 없는 경우는 인증 프로세스 실행 불가능
        if (selectedUser == null) {
            throw new UserFindFailException(IMPOSSIBLE_VERIFY_CODE);
        }

        String verificationCode = RandomStringUtils.randomNumeric(6);
        String key;

        String way = userForFind.getWay();
        switch (way) {
            case "EMAIL":
                awsSes.sendEmail(selectedUser.getEmail(), "[Mokasong] 비밀번호 찾기 인증번호입니다.", verificationCode);
                key =
                break;
            case "CELLPHONE":
                coolsms.sendMessageToOne(selectedUser.getPhone_number(),
                        String.format("[Mokasong](비밀번호 찾기 인증) 인증번호는 [%s]입니다.", verificationCode));
                break;
            default:
                throw new UserFindFailException(INVALID_REQUEST_DATA);
                break;
        }

        String key = String.format("find-password/verification/code/%s", userForFind.get);
    }

    @Override
    public BaseResponse checkVerificationCodeForFindPassword(UserForFind userForFind) throws Exception {
        return null;
    }
}

 */
