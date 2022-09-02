package com.mokasong.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 *  1xx: 유저 관련 Exception
 *
 *  9xx: 기타 Exception
 */

@Getter
public enum CustomExceptionList {

    // ------------------------------------------------------ 1xx: 유저 관련 Exception ---------------------------------------------------------

    EMAIL_ALREADY_EXIST(100, "이미 회원 정보에 존재하는 이메일입니다.", HttpStatus.FORBIDDEN),

    PHONE_NUMBER_ALREADY_EXIST(101, "이미 회원 정보에 존재하는 휴대전화번호입니다.", HttpStatus.FORBIDDEN),

    VERIFICATION_TIME_EXPIRE(102, "인증 시간이 만료되었습니다.", HttpStatus.FORBIDDEN),

    REQUIRED_TYPE_EMAIL_OR_PHONENUMBER(103, "데이터 형식이 이메일 또는 휴대폰번호가 아닙니다.", HttpStatus.FORBIDDEN),

    REQUEST_TIME_EXPIRE_FOR_USER_REGISTER(104, "회원가입 요청 가능 시간이 만료되었습니다. 회원가입을 다시 진행해주세요.", HttpStatus.FORBIDDEN),

    VERIFICATION_CODE_NOT_EQUAL(105, "인증 번호가 일치하지 않습니다.", HttpStatus.FORBIDDEN),

    VERIFICATION_TOKEN_NOT_EQUAL(106, "인증 토큰이 일치하지 않습니다.", HttpStatus.FORBIDDEN),

    USER_NOT_EXIST(107, "회원 정보가 없습니다.", HttpStatus.FORBIDDEN),

    USER_NOT_REGULAR(108, "회원 정보는 있으나 이메일 인증이 남은 상태입니다. 회원가입시 입력했던 이메일로 가서 인증링크를 클릭하고 다시 로그인해주세요.", HttpStatus.FORBIDDEN),

    TOKEN_EXPIRED(109, "토큰의 유효시간이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_CREATION_HAS_PROBLEM(110, "토큰 발급 과정에서 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    TOKEN_DIRTY_DETECTED(111, "토큰 변경이 감지되었습니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_NOT_EXIST_IN_REQUEST(112, "요청 헤더에 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_NOT_CONTAIN_BEARER(113, "토큰은 Bearer +token 형태로 전송해야합니다.", HttpStatus.UNAUTHORIZED),

    UNAUTHORIZED(114, "권한이 없습니다.", HttpStatus.UNAUTHORIZED),

    // ---------------------------------------------------------------------------------------------------------------------------------------

    // --------------------------------------------------------9xx: 기타 Exception ------------------------------------------------------------

    INVALID_ACCESS(900, "유효하지 않은 접근입니다.", HttpStatus.FORBIDDEN),

    INVALID_REQUEST_DATA(901, "요청에 형식에 맞지 않는 데이터가 있습니다.", HttpStatus.BAD_REQUEST),

    MESSAGE_SEND_HAS_PROBLEM(902, "메시지 발송중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    UNPREDICTABLE(999, "예기치 못한 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    // ---------------------------------------------------------------------------------------------------------------------------------------

    private Integer errorCode;
    private String message;
    private HttpStatus httpStatusCode;

    CustomExceptionList(Integer errorCode, String message, HttpStatus httpStatusCode) {
        this.errorCode = errorCode;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }

    public CustomExceptionList setMessage(String message) {
        this.message = message;
        return this;
    }
}
