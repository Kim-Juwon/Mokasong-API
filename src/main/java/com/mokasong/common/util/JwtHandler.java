package com.mokasong.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mokasong.common.exception.custom.JWTPreconditionException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.Date;

import static com.mokasong.common.exception.CustomExceptionList.*;

@Component
public class JwtHandler {
    private final String secretKey;

    public JwtHandler() {
        secretKey = RandomStringUtils.randomAlphanumeric(300);
    }

    public String generateToken(Long userId, int hour) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);

        return JWT.create()
                .withExpiresAt(getExpirationTime(Calendar.HOUR, hour))
                .withClaim("userId", userId)
                .sign(algorithm);
    }

    public Long discoverUserId(String token) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(this.secretKey);
        DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(token);

        Long userId = decodedJWT.getClaim("userId").asLong();

        return userId;
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

        // 헤더에 Authorization에 대한 값이 없을 경우
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
