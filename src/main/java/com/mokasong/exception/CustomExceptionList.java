package com.mokasong.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 *  1xx: 유저 관련 Exception
 *
 *  9xx: 기타 Exception
 */

@Getter
public enum CustomExceptionList {

    EMAIL_ALREADY_EXIST(100, "이미 회원 정보에 존재하는 이메일입니다.", HttpStatus.FORBIDDEN),

    PHONE_NUMBER_ALREADY_EXIST(101, "이미 회원 정보에 존재하는 휴대전화번호입니다.", HttpStatus.FORBIDDEN),

    VERIFICATION_TIME_EXPIRE(102, "인증 시간이 만료되었습니다.", HttpStatus.FORBIDDEN),

    REQUEST_TIME_EXPIRE_OR_DATA_COUNTERFEIT_DETECTED(103, "요청 가능 시간이 만료되었거나 데이터 조작이 감지되었습니다.", HttpStatus.FORBIDDEN),

    VERIFICATION_CODE_NOT_EQUAL(104, "인증 번호가 일치하지 않습니다.", HttpStatus.FORBIDDEN),

    VERIFICATION_TOKEN_NOT_EQUAL(105, "인증 토큰이 일치하지 않습니다.", HttpStatus.FORBIDDEN),



    INVALID_ACCESS(900, "유효하지 않은 접근입니다.", HttpStatus.FORBIDDEN),

    // ----------------------------------------- 유저 관련 Exception -----------------------------------------

    // REQUIRED_SEND_VERIFICATION_CODE(100, "인증번호 전송이 필요합니다.", HttpStatus.FORBIDDEN),

    // NOT_EQUAL_VERIFICATION_CODE(101, "인증번호가 일치하지 않습니다.", HttpStatus.FORBIDDEN),

    // ALREADY_EXIST_USER_BY_EMAIL(102, "이미 회원가입 되어있는 이메일입니다.", HttpStatus.FORBIDDEN),

    // REQUIRED_INPUT_REGISTER_INFORMATION(103, "회원가입 정보 입력을 해주세요.", HttpStatus.FORBIDDEN),

    // ALREADY_EXIST_USER_BY_PHONE_NUMBER(104, "이미 회원정보에 존재하는 휴대전화번호입니다.", HttpStatus.FORBIDDEN),

    TOKEN_EXPIRED(105, "토큰의 유효시간이 만료되었습니다.", HttpStatus.UNAUTHORIZED),

    // TOKEN_ISSUER_NOT_VERIFIED(106, "토큰의 발급자 정보가 유효하지 않습니다,", HttpStatus.UNAUTHORIZED),

    TOKEN_CREATION_HAS_PROBLEM(107, "토큰 발급 과정에서 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    TOKEN_DETECTED_DIRTY(108, "토큰 변경이 감지되었습니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_NOT_EXIST_IN_REQUEST(109, "요청 헤더에 토큰이 없습니다.", HttpStatus.UNAUTHORIZED),

    TOKEN_NOT_CONTAIN_BEARER(110, "토큰은 Bearer +token 형태로 전송해야합니다.", HttpStatus.UNAUTHORIZED),

    // REGISTRATION_PRECONDITION_NOT_SATISFIED(111, "회원가입할 수 없습니다.", HttpStatus.UNAUTHORIZED),

    // IMPOSSIBLE_FIND_EMAIL_BY_PHONE_NUMBER(112, "이름과 휴대전화번호가 회원 정보와 일치하지 않거나, 회원 정보가 없습니다.", HttpStatus.FORBIDDEN),

    // IMPOSSIBLE_VERIFY_CODE(113, "예기치 않은 에러로 인증이 불가능합니다.", HttpStatus.FORBIDDEN),

    // VERIFICATION_EXPIRED(114, "인증 가능 시간이 만료되었습니다.", HttpStatus.FORBIDDEN),

    // ---------------------------------------------- 기타 Exception ----------------------------------------------

    INVALID_REQUEST_DATA(900, "요청에 올바르지 않은 데이터가 있습니다.", HttpStatus.BAD_REQUEST),

    MESSAGE_SEND_HAS_PROBLEM(901, "메시지 발송중 문제가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    UNPREDICTABLE(999, "예기치 못한 에러가 발생하였습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

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
