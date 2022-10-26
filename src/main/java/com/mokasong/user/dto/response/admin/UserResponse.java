package com.mokasong.user.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mokasong.user.state.Authority;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter @Builder
public class UserResponse {
    private AdminPageUser user;

    @Getter
    public static class AdminPageUser {
        private Long userId;
        private String email;
        private String phoneNumber;
        private Authority authority;
        private String name;
        private List<DeliveryAddress> deliveryAddresses;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date lastLoginTime;
        private Boolean isDeleted;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date createdAt;
    }

    @Getter
    private static class DeliveryAddress {
        private String address;
        private Boolean isDefault;
    }
}
