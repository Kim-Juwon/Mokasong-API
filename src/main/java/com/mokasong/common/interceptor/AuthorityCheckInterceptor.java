package com.mokasong.common.interceptor;

import com.mokasong.common.annotation.Login;
import com.mokasong.common.exception.custom.ForbiddenException;
import com.mokasong.common.exception.custom.UnauthorizedException;
import com.mokasong.user.entity.User;
import com.mokasong.user.repository.AdminUserMapper;
import com.mokasong.user.repository.UserMapper;
import com.mokasong.common.util.JwtHandler;
import com.mokasong.user.state.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.mokasong.common.exception.ErrorCode.*;
import static com.mokasong.user.state.Authority.*;

@Component
public class AuthorityCheckInterceptor implements HandlerInterceptor {
    private final UserMapper userMapper;
    private final JwtHandler jwtHandler;

    @Autowired
    public AuthorityCheckInterceptor(UserMapper userMapper, JwtHandler jwtHandler) {
        this.userMapper = userMapper;
        this.jwtHandler = jwtHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) handler;

        Login login = handlerMethod.getMethod().getDeclaredAnnotation(Login.class);

        if (login == null) {
            return true;
        }

        Authority[] authorities = login.value();
        if (authorities.length == 0) {
            return true;
        }

        String accessToken = request.getHeader("Access-Token");

        if (accessToken == null) {
            throw new UnauthorizedException("헤더에 토큰이 없습니다.", UNAUTHORIZED.getErrorCode());
        }

        int tokenLength = accessToken.length();
        if ((tokenLength < 7) || (!accessToken.substring(0, 7).equals("Bearer "))) {
            throw new UnauthorizedException("토큰 조작이 감지되었습니다.", UNAUTHORIZED.getErrorCode());
        }

        accessToken = accessToken.substring(7, tokenLength);
        Long userId = jwtHandler.discoverUserId(accessToken);

        User user = userMapper.getUserById(userId);

        if (user == null) {
            throw new UnauthorizedException("토큰 조작이 감지되었습니다.", UNAUTHORIZED.getErrorCode());
        }

        jwtHandler.validateToken(accessToken, user.getSecretKey());

        if (user.getAuthority() == STAND_BY_REGISTER) {
            throw new UnauthorizedException("아직 정식 회원이 아닙니다.", -1);
        }

        for (Authority authority : authorities) {
            if (user.getAuthority().equals(authority)) {
                request.setAttribute("user", user);
                return true;
            }
        }

        throw new ForbiddenException("권한이 없습니다.", -1);
    }
}
