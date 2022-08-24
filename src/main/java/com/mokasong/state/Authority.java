package com.mokasong.state;

public enum Authority {
    ADMIN, // 관리자
    REGULAR, // 일반회원
    STAND_BY_REGISTER // 회원가입 대기 (이메일 인증만 남은 상태)
}
