package com.mokasong.common.interceptor;

import com.mokasong.user.repository.UserMapper;
import com.mokasong.common.util.JwtHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        return true;
    }
}
