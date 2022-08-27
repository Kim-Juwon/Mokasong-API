package com.mokasong.user.repository;

import com.mokasong.user.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void createUser(User user);
    User getUserById(Long userId);
    User getUserByEmail(String email);
    User getUserByPhoneNumber(String phoneNumber);
    User getUserByRegisterToken(String registerToken);
    void updateUser(User user);
    void deleteUserById(Long userId);
}
