package com.mokasong.user.repository;

import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserMapper {
    UserResponse.AdminPageUser getUserForAdminPage(Long userId);

    User getUser(Long userId);

    void deleteUser(Long userId);

    void undeleteUser(Long userId);
}
