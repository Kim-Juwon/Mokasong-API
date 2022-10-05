package com.mokasong.common.interceptor;

import com.mokasong.common.annotation.Login;
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
public class AuthorityCheckInterceptor implements HandlerInterceptor {
    private UserMapper userMapper;
    private JwtHandler jwtHandler;

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

        // 권한이 필요없다면
        if (login == null) {
            return true;
        }

        Authority[] authorities = login.value();
        if (authorities.length == 0) {
            return true;
        }

        String accessToken = request.getHeader("Authorization");

        if (accessToken == null) {
            throw new JWTPreconditionException(TOKEN_NOT_EXIST_IN_REQUEST);
        }

        int tokenLength = accessToken.length();
        if ((tokenLength < 7) || (!accessToken.substring(0, 7).equals("Bearer "))) {
            throw new JWTPreconditionException(TOKEN_NOT_CONTAIN_BEARER);
        }

        accessToken = accessToken.substring(7, tokenLength);
        Long userId = jwtHandler.discoverUserId(accessToken);
        User user = userMapper.getUserById(userId);

        if (user == null) {
            throw new UnauthorizedException(USER_NOT_EXIST);
        }
        if (user.getAuthority() == STAND_BY_REGISTER) {
            throw new UnauthorizedException(UNAUTHORIZED);
        }

        for (Authority authority : authorities) {
            if (user.getAuthority().equals(authority)) {
                request.setAttribute("user", user);
                return true;
            }
        }

        return false;
    }
}
