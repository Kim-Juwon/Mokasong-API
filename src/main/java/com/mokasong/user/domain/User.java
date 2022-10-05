package com.mokasong.user.domain;

import com.mokasong.user.dto.request.UserRegisterDto;
import com.mokasong.user.state.Authority;
import lombok.Getter;
import lombok.ToString;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

@Getter @ToString
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
    public void initializeForStandingByRegister(UserRegisterDto dto, String registerToken) {
        this.email = dto.getEmail();
        this.password = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt()); // bcrypt 암호화
        this.phone_number = dto.getPhone_number();
        this.authority = Authority.STAND_BY_REGISTER;
        this.name = dto.getName();
        this.register_token = registerToken;
        this.is_deleted = false;
        this.created_at = new Date();
    }

    // 회원가입 대기 상태에서 정식 회원으로 전환
    public void changeAuthorityToRegular() {
        this.authority = Authority.REGULAR;
        this.updated_at = new Date();
    }

    public void changeLastLoginTimeToNow() {
        this.last_login_time = new Date();
        this.updated_at = new Date();
    }

    public void changeLastLogoutTimeToNow() {
        this.last_logout_time = new Date();
        this.updated_at = new Date();
    }

    public void changeToNewPassword(String newPassword) {
        this.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        this.updated_at = new Date();
    }
}
