package com.mokasong.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mokasong.exception.custom.JWTPreconditionException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

import static com.mokasong.exception.CustomExceptionList.TOKEN_NOT_CONTAIN_BEARER;
import static com.mokasong.exception.CustomExceptionList.TOKEN_NOT_EXIST_IN_REQUEST;

@Component
public class JwtHandler {
    @Value("${jwt.user-authorization-secret-key}")
    private String USER_AUTHORIZATION_SECRET_KEY;

    @Value("${jwt.user-registration-precondition-check-secret-key}")
    private String USER_REGISTRATION_PRECONDITION_CHECK_SECRET_KEY;

    @Value("${jwt.message-service-secret-key}")
    private String MESSAGE_SERVICE_SECRET_KEY;

    /**
     *  휴대전화번호 인증시, 인증번호가 맞는지 판별하는데에 사용되는 JWT 생성
     */
    public String generateTokenForPhoneNumberVerification(String phoneNumber, int expirationMinute) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(MESSAGE_SERVICE_SECRET_KEY);

        return JWT.create()
                .withExpiresAt(getExpirationTime(Calendar.MINUTE, expirationMinute))
                .withClaim("phone_number", phoneNumber)
                .sign(algorithm);
    }

    /**
     *  generateTokenForPhoneNumberVerification()에서 발급된 토큰이 유효한지 판별하고 decondig된 내용(DecodedJWT 객체)을 리턴
     */
    public DecodedJWT getDecodedTokenForPhoneNumberVerification(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(MESSAGE_SERVICE_SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();

        return verifier.verify(token);
    }

    /**
     *  회원가입 버튼 누를 시, 그 전에 전화번호 인증이 확인되었는지 판별하는데 사용되는 JWT 생성
     */
    public String generateTokenForRegistrationPreconditionCheck(String registrationPreconditionCode) {
        Algorithm algorithm = Algorithm.HMAC256(USER_REGISTRATION_PRECONDITION_CHECK_SECRET_KEY);

        return JWT.create()
                .withClaim("precondition_registration_code", registrationPreconditionCode)
                .sign(algorithm);
    }

    /**
     *  generateTokenForPreconditionRegisterCheck()에서 발급된 토큰이 유효한지 판별하고 decondig된 내용(DecodedJWT 객체)을 리턴
     */
    public DecodedJWT getDecodedTokenForPreconditionRegistrationCheck(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(USER_REGISTRATION_PRECONDITION_CHECK_SECRET_KEY);
        JWTVerifier verifier = JWT.require(algorithm).build();

        return verifier.verify(token);
    }

    /**
     *
     * @param criteria 기준 (초, 분, 시간) -> (Calendar.SECOND, Calendar.MINUTE, Calendar.HOUR)
     * @param extendedTime 더해질 시간
     * @return 현재 시간에 extendedTime이 더해져 만들어진 시간
     */
    private Date getExpirationTime(int criteria, int extendedTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(criteria, extendedTime);
        return calendar.getTime();
    }

    public String getTokenInHttpHeader() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        // 헤더에 Authorization에 대한 값이 없을 경우 Exception을 throw
        if (token == null) {
            throw new JWTPreconditionException(TOKEN_NOT_EXIST_IN_REQUEST);
        }

        int tokenLength = token.length();
        // 앞 7글자가 "Bearer " 가 아닌 경우 Exception을 throw
        if ((tokenLength < 7) || (!token.substring(0, 7).equals("Bearer "))) {
            throw new JWTPreconditionException(TOKEN_NOT_CONTAIN_BEARER);
        }

        // "Bearer " 이후의 문자열(진짜 토큰)을 리턴
        // 토큰에 문제가 있는 경우는 JwtHandler에서 처리된다.
        return token.substring(7, tokenLength);
    }
}
