package com.mokasong.domain.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mokasong.annotation.ValidationGroups;
import com.mokasong.state.Authority;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.mindrot.jbcrypt.BCrypt;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Getter @ToString @Setter
public class User {
    @ApiModelProperty(hidden = true)
    private Long user_id;

    @Email(groups = {ValidationGroups.ChangeToStandingByRegister.class,
            ValidationGroups.Login.class}, message = "이메일 형식이어야 합니다.")
    @NotBlank(groups = ValidationGroups.ChangeToStandingByRegister.class, message = "이메일은 필수입니다.")
    private String email;

    @NotBlank(groups = {ValidationGroups.ChangeToStandingByRegister.class,
            ValidationGroups.Login.class}, message = "비밀번호는 필수입니다.")
    private String password;

    @NotBlank(groups = {ValidationGroups.ChangeToStandingByRegister.class}, message = "휴대전화번호는 필수입니다.")
    @Pattern(groups = {ValidationGroups.ChangeToStandingByRegister.class},
            regexp = "^010\\d{7,8}$", message = "휴대전화번호 형식이어야 합니다. (010xxxxxxx 또는 010xxxxxxxx)")
    private String phone_number;

    @ApiModelProperty(hidden = true)
    @NotBlank(message = "주소는 필수로 입력하여야 합니다.")
    private String address;

    @ApiModelProperty(hidden = true)
    private Authority authority;

    @NotBlank(groups = {ValidationGroups.ChangeToStandingByRegister.class}, message = "이름은 필수입니다.")
    @Pattern(groups = {ValidationGroups.ChangeToStandingByRegister.class},
            regexp = "^[가-힣|a-z|A-Z]+$", message = "이름이 유효하지 않습니다.")
    private String name;

    @ApiModelProperty(hidden = true)
    private Date last_login_time;

    @ApiModelProperty(hidden = true)
    private Date last_logout_time;

    @ApiModelProperty(hidden = true)
    private String register_token;

    @ApiModelProperty(hidden = true)
    private Boolean is_deleted;

    @ApiModelProperty(hidden = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date created_at;

    @ApiModelProperty(hidden = true)
    private Date updated_at;

    // 회원가입 대기 상태로 전환
    public void initializeForStandingByRegister(String registerToken) {
        this.password = BCrypt.hashpw(this.password, BCrypt.gensalt());
        this.authority = Authority.STAND_BY_REGISTER;
        this.register_token = registerToken;
        this.is_deleted = false;
        this.created_at = new Date();
        this.updated_at = new Date();
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
