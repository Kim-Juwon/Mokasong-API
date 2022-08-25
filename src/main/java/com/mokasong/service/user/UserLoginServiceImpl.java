package com.mokasong.service.user;

import com.mokasong.domain.user.User;
import com.mokasong.exception.custom.UserLoginFailException;
import com.mokasong.exception.custom.UserLogoutException;
import com.mokasong.repository.UserMapper;
import com.mokasong.response.BaseResponse;
import com.mokasong.response.NormalResponse;
import com.mokasong.state.Authority;
import com.mokasong.util.JwtHandler;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

import static com.mokasong.exception.CustomExceptionList.UNPREDICTABLE;
import static com.mokasong.exception.CustomExceptionList.USER_NOT_EXIST;

@Service
public class UserLoginServiceImpl implements UserLoginService {
    private UserMapper userMapper;
    private JwtHandler jwtHandler;

    @Autowired
    public UserLoginServiceImpl(UserMapper userMapper, JwtHandler jwtHandler) {
        this.userMapper = userMapper;
        this.jwtHandler = jwtHandler;
    }
    @Override
    @Transactional
    public BaseResponse login(User user) throws Exception {
        User selectedUser = userMapper.getUserByEmail(user.getEmail());

        if (selectedUser == null) {
            throw new UserLoginFailException(USER_NOT_EXIST);
        }
        if ((selectedUser.getIs_deleted().equals(true)) ||
                (selectedUser.getAuthority() == Authority.STAND_BY_REGISTER) ||
                !this.passwordValid(user.getPassword(), selectedUser.getPassword())) {
            throw new UserLoginFailException(USER_NOT_EXIST);
        }

        String accessToken = jwtHandler.generateToken(selectedUser.getUser_id(), 1);

        selectedUser.changeLastLoginTime();
        userMapper.updateUser(selectedUser);

        return new NormalResponse("로그인 되었습니다.", new HashMap<>() {{
            put("login", true);
            put("access_token", accessToken);
        }});
    }

    @Override
    @Transactional
    public BaseResponse logout() throws Exception {
        String accessToken = jwtHandler.getTokenInHttpHeader();
        Long userId = jwtHandler.discoverUserId(accessToken);
        User user = userMapper.getUserById(userId);

        if (user == null) {
            throw new UserLogoutException(UNPREDICTABLE);
        }

        user.changeLastLogoutTime();
        userMapper.updateUser(user);

        return new NormalResponse("로그아웃 되었습니다.", new HashMap<>() {{
            put("logout", true);
        }});
    }

    private boolean passwordValid(String passwordInRequest, String passwordInDatabase) {
        return BCrypt.checkpw(passwordInRequest, passwordInDatabase);
    }
}
