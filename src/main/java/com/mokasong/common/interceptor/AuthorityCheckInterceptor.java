package com.mokasong.common.interceptor;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.exception.custom.ForbiddenException;
import com.mokasong.common.exception.custom.UnauthorizedException;
import com.mokasong.user.entity.User;
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

        Auth authInClass = handlerMethod.getMethod().getDeclaringClass().getDeclaredAnnotation(Auth.class);
        Auth authInMethod = handlerMethod.getMethod().getDeclaredAnnotation(Auth.class);

        User user;

        if (authInClass == null) {
            // 클래스, 메소드에 전부 선언 안되어있는 경우
            if (authInMethod == null) {
                return true;
            }

            // 메소드에만 선언되어있는 경우
            user = getUser(request);

            if (authorized(authInMethod, user.getAuthority())) {
                request.setAttribute("user", user);
                return true;
            }
        } else {
            user = getUser(request);

            // 클래스에만 선언되어 있는 경우
            if (authInMethod == null) {
                if (authorized(authInClass, user.getAuthority())) {
                    request.setAttribute("user", user);
                    return true;
                }
            }

            // 클래스, 메소드에 전부 선언되어있는 경우
            else {
                // 메소드에 있는 정보를 기준으로 한다.
                if (authorized(authInMethod, user.getAuthority())) {
                    request.setAttribute("user", user);
                    return true;
                }
            }
        }

        throw new ForbiddenException("권한이 없습니다.", -1);
    }

    private User getUser(HttpServletRequest request) throws Exception {
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

        return user;
    }

    private boolean authorized(Auth auth, Authority userAuthority) throws Exception {
        Authority[] authorities = auth.value();

        if (authorities.length == 0) {
            return true;
        }

        for (Authority authority : authorities) {
            if (userAuthority == authority) {
                return true;
            }
        }

        return false;
    }
}
