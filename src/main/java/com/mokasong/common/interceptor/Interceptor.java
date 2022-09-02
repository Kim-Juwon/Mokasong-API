package com.mokasong.common.interceptor;

import com.mokasong.common.annotation.AccessibleOnly;
import com.mokasong.common.exception.custom.JWTPreconditionException;
import com.mokasong.user.domain.User;
import com.mokasong.user.exception.UnauthorizedException;
import com.mokasong.user.repository.UserMapper;
import com.mokasong.common.util.JwtHandler;
import com.mokasong.user.state.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.mokasong.common.exception.CustomExceptionList.*;
import static com.mokasong.user.state.Authority.*;

@Component
public class Interceptor implements HandlerInterceptor {
    private UserMapper userMapper;
    private JwtHandler jwtHandler;

    @Autowired
    public Interceptor(UserMapper userMapper, JwtHandler jwtHandler) {
        this.userMapper = userMapper;
        this.jwtHandler = jwtHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        AccessibleOnly accessibleOnly = handlerMethod.getMethod().getDeclaredAnnotation(AccessibleOnly.class);

        // 권한이 필요없다면
        if (accessibleOnly == null) {
            return true;
        }

        Authority[] authorities = accessibleOnly.value();
        if (authorities.length == 0) {
            return true;
        }

        String accessToken = request.getHeader("Authorization");

        // 헤더에 access token이 없을 경우
        if (accessToken == null) {
            throw new JWTPreconditionException(TOKEN_NOT_EXIST_IN_REQUEST);
        }

        int tokenLength = accessToken.length();
        // 앞 7글자가 "Bearer " 가 아닌 경우
        if ((tokenLength < 7) || (!accessToken.substring(0, 7).equals("Bearer "))) {
            throw new JWTPreconditionException(TOKEN_NOT_CONTAIN_BEARER);
        }

        accessToken = accessToken.substring(7, tokenLength);
        Long userId = jwtHandler.discoverUserId(accessToken);
        User user = userMapper.getUserById(userId);

        // 유저가 조회되지 않는다면
        if (user == null) {
            throw new UnauthorizedException(USER_NOT_EXIST);
        }
        // 유저가 회원가입 대기상태라면
        if (user.getAuthority() == STAND_BY_REGISTER) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        for (Authority authority : authorities) {
            if (user.getAuthority() == authority) {
                request.setAttribute("user", user);
                return true;
            }
        }
        return false;
    }
}
