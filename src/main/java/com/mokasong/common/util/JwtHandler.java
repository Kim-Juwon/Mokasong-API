package com.mokasong.common.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mokasong.common.exception.custom.ForbiddenException;
import com.mokasong.common.exception.custom.InternalServerErrorException;
import com.mokasong.common.exception.custom.UnauthorizedException;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.mokasong.common.exception.ErrorCode.*;

@Component
public class JwtHandler {
    public String generateToken(Long userId, String secretKey, int hour) throws Exception {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        String token;
        try {
            token = JWT.create()
                    .withExpiresAt(this.getExpirationTime(Calendar.HOUR, hour))
                    .withClaim("userId", userId)
                    .sign(algorithm);

        } catch (Exception e) {
            throw new InternalServerErrorException("토큰 발급중 문제가 발생하였습니다.", INTERNAL_SERVER_ERROR.getErrorCode());
        }

        return token;
    }

    public Long discoverUserId(String accessToken) throws Exception {
        String[] divided = accessToken.split("\\.");
        if (divided.length != 3) {
            throw new UnauthorizedException("토큰 변경이 감지되었습니다.", UNAUTHORIZED.getErrorCode());
        }

        Long userId;
        try {
            String encodedPayload = divided[1];
            String decodedPayload = new String(Base64.getDecoder().decode(encodedPayload));

            Map<String, Object> decodedPayloadMap = new ObjectMapper().readValue(decodedPayload, HashMap.class);

            Object userIdObj = decodedPayloadMap.get("userId");
            Class<?> intType = userIdObj.getClass();

            userId = intType.equals(Integer.class) ? ((Integer) userIdObj).longValue() : (Long) userIdObj;

        } catch (Exception e) {
            throw new UnauthorizedException("토큰 변경이 감지되었습니다.", UNAUTHORIZED.getErrorCode());
        }

        return userId;
    }

    public void validateToken(String accessToken, String secretKey) throws Exception {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secretKey);
            JWT.require(algorithm).build().verify(accessToken);
        } catch (TokenExpiredException e) {
            throw new ForbiddenException("토큰 유효 시간이 만료되었습니다.", UNAUTHORIZED.getErrorCode());
        }
        catch (Exception e) {
            throw new UnauthorizedException("토큰 변경이 감지되었습니다.", UNAUTHORIZED.getErrorCode());
        }
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

    /*
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
     */
}
