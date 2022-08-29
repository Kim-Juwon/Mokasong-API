package com.mokasong.common.annotation;

import com.mokasong.user.state.Authority;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  controller method에 Login annotation이 선언되어있다면
 *  해당 API는 로그인이 필요하다는 것을 알려줍니다.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LoginRequired {
    Authority[] value();
}
