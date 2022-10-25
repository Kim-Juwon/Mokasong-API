package com.mokasong.user.entity;

import com.mokasong.user.dto.request.RegisterRequest;
import com.mokasong.user.state.Authority;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Date;

@Getter
@NoArgsConstructor
public class User {
    private Long userId;
    private String email;
    private String password;
    private String phoneNumber;
    private Authority authority;
    private String name;
    private Date lastLoginTime;
    private Date lastLogoutTime;
    private String registerToken;
    private String secretKey;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public User(RegisterRequest requestBody, String registerToken) {
        this.email = requestBody.getEmail();
        this.password = BCrypt.hashpw(requestBody.getPassword(), BCrypt.gensalt());
        this.phoneNumber = requestBody.getPhoneNumber();
        this.authority = Authority.STAND_BY_REGISTER;
        this.name = requestBody.getName();
        this.registerToken = registerToken;
        this.secretKey = RandomStringUtils.randomAlphanumeric(255);
    }

    public User changeToRegular() {
        this.authority = Authority.REGULAR;
        return this;
    }

    public void changeLastLoginTimeToNow() {
        this.lastLoginTime = new Date();
    }

    public void changeLastLogoutTimeToNow() {
        this.lastLogoutTime = new Date();
    }

    public User changeToNewPassword(String newPassword) {
        this.password = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        return this;
    }
}
