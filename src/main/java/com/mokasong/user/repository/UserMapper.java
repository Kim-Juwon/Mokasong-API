package com.mokasong.user.repository;

import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void createUser(User user);
    User getUserById(Long userId);
    User getUserByEmail(String email);
    User getUserByPhoneNumber(String phoneNumber);
    User getUserByNameAndPhoneNumber(String name, String phoneNumber);
    User getUserByNameAndEmail(String name, String email);
    User getUserByRegisterToken(String registerToken);
    void updateUser(User user);
    UserResponse getUserByIgnoreDeletion(Long userId);
}
