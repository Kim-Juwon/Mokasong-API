package com.mokasong.domain.user;

import com.mokasong.dto.user.UserRegisterDto;
import com.mokasong.state.Authority;
import lombok.Getter;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

@Getter
public class User {
    private Long user_id;
    private String email;
    private String password;
    private String phone_number;
    private String address;
    private Authority authority;
    private String name;
    private Date last_login_time;
    private Date last_logout_time;
    private String register_token;
    private Boolean is_deleted;
    private Date created_at;
    private Date updated_at;

    // 회원가입 대기 상태로 전환
    public void initializeForStandingByRegister(UserRegisterDto userRegisterDto, String registerToken) {
        this.email = userRegisterDto.getEmail();
        this.password = BCrypt.hashpw(userRegisterDto.getPassword(), BCrypt.gensalt()); // bcrypt 암호화
        this.phone_number = userRegisterDto.getPhone_number();
        this.authority = Authority.STAND_BY_REGISTER;
        this.name = userRegisterDto.getName();
        this.register_token = registerToken;
        this.is_deleted = false;
        this.created_at = new Date();
    }

    // 회원가입 대기 상태에서 정식 회원으로 전환
    public void changeAuthorityToRegular() {
        this.authority = Authority.REGULAR;
        this.updated_at = new Date();
    }

    public void changeLastLoginTime() {
        this.last_login_time = new Date();
    }

    public void changeLastLogoutTime() {
        this.last_logout_time = new Date();
    }
}
