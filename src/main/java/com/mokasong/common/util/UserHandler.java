package com.mokasong.common.util;

import com.mokasong.user.entity.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class UserHandler {
    /** interceptor에서 request에 담아준 User 객체를 리턴한다.
     *  이 메소드의 장점은 다음과 같다.
     *  - service layer에서 이 메소드 한번 호출만으로 편리하게 유저의 정보를 받아올 수 있어 서비스 로직에만 집중할 수 있게 해준다.
     *  - interceptor에서 이미 쿼리를 실행하기 때문에, 더이상 쿼리를 실행할 필요가 없다.
     */
    public static User getLoggedInUser() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return (User) request.getAttribute("user");
    }
}
