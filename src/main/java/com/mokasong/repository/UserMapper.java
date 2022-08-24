package com.mokasong.repository;

import com.mokasong.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    void createUser(User user);
    User getUserById(Long userId);
    User getUserByEmail(String email);
    User getUserByPhoneNumber(String phoneNumber);
    User getUserByName(String name);
    User getUserByNameAndPhoneNumber(String name, String phoneNumber);
    User getUserByNameAndEmail(String name, String email);
    User getUserByRegisterToken(String registerToken);
    Long getUserIdByEmail(String email);
    void updateUser(User user);
    void deleteUser(Long userId);
}
