package com.mokasong.user.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mokasong.user.state.Authority;
import lombok.Getter;

import java.util.Date;
import java.util.List;

@Getter
public class UserResponse {
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

    @Getter
    @JsonIgnoreProperties
    private static class DeliveryAddress {
        private String address;
        private Boolean isDefault;
    }
}
