package com.mokasong.user.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.user.dto.response.admin.UserResponse;
import com.mokasong.user.entity.User;
import com.mokasong.user.repository.AdminUserMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserServiceImpl implements AdminUserService {
    private final AdminUserMapper adminUserMapper;

    public AdminUserServiceImpl(AdminUserMapper adminUserMapper) {
        this.adminUserMapper = adminUserMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) throws Exception {
        UserResponse.AdminPageUser user = adminUserMapper.getUserForAdminPage(userId);

        if (user == null) {
            throw new NotFoundException("존재하지 않는 회원입니다.", 1);
        }

        return UserResponse.builder()
                .user(user)
                .build();
    }

    public SuccessfulResponse deleteUser(Long userId) throws Exception {
        User user = checkUserExistsForAdmin(userId, 1);

        if (user.getIsDeleted()) {
            throw new ConflictException("이미 soft delete 되어있는 회원입니다.", 2);
        }

        // Soft delete
        adminUserMapper.deleteUser(userId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    public SuccessfulResponse undeleteUser(Long userId) throws Exception {
        User user = checkUserExistsForAdmin(userId, 1);

        if (!user.getIsDeleted()) {
            throw new ConflictException("soft delete 되어있는 회원이 아닙니다.", 2);
        }

        // Soft delete 해제
        adminUserMapper.undeleteUser(userId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private User checkUserExistsForAdmin(Long userId, Integer errorCode) {
        User user = adminUserMapper.getUser(userId);

        if (user == null) {
            throw new NotFoundException("없는 회원입니다.", errorCode);
        }

        return user;
    }
}
