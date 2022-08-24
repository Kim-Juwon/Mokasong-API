package com.mokasong.exception.handler;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.mokasong.exception.CustomException;
import com.mokasong.response.ExceptionResponse;
import com.mokasong.response.detail.RequestDataBindExceptionResponse;
import net.nurigo.sdk.message.exception.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

import static com.mokasong.exception.CustomExceptionList.*;

/**
 *  모든 Exception throw에 대한 처리는 GlobalExceptionHandler class의 exceptionHandle() 메소드로 처리합니다.
 *
 *  -------------------------------- 처리하는 exception 종류는 다음과 같습니다. --------------------------------
 *
 *  - 사용자 정의 Exception (김주원이 직접 만든 CustomException의 sub class exception들을 처리)
 *
 *  - 문자 발송 서비스 Exception (net.nurigo.sdk.message.exception에 정의되어 있음)
 *      - 전부 server의 문제라고 간주하고 응답합니다. (http status code: 500)
 *
 *  - JWT 관련 Exception (com.auth0.jwt.exceptions에 정의되어 있음)
 *      - JWTCreationException인 경우
 *          - 토큰 발급시 문제가 생겼다는 정보와 함께 응답합니다.
 *      - JWTVerificationException인 경우
 *          - TokenExpiredException인 경우
 *              - 토큰의 유효시간이 지났다고 응답합니다.
 *          - 그 외
 *              - 토큰을 변경하여 요청했다고 간주하여 응답합니다.
 *
 *  - request 데이터 유효성 검증 Exception
 *      - 유효성 검증에 통과하지 못한 내용을 포함하여 응답합니다.
 *          - org.springframework.validation.BindException인 경우 (request body)
 *          - javax.validation.ConstraintViolationException인 경우 (query paramter 또는 path variable)
 *
 *  - 그 외 예측할 수 없는 Exception인 경우
 *      - exception message와 함께 error code는 999로, http status code는 500으로 응답합니다.
 *
 *  구체적인 로직은 코드를 참고해주세요.
 *
 *  -----------------------------------------------------------------------------------------------------
 */

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ExceptionResponse> exceptionHandle(Throwable e, HandlerMethod handlerMethod) {
        // Custom Exception
        if (e instanceof CustomException) {
            return new ResponseEntity<>(new ExceptionResponse(
                    e.getMessage(),
                    ((CustomException) e).getErrorCode()),
                    ((CustomException) e).getHttpStatusCode());
        }

        // 문자발송 서비스 Exception
        else if (isKindOfNurigoException(e)) {
            return new ResponseEntity<>(new ExceptionResponse(
                    MESSAGE_SEND_HAS_PROBLEM.getMessage(),
                    MESSAGE_SEND_HAS_PROBLEM.getErrorCode()),
                    MESSAGE_SEND_HAS_PROBLEM.getHttpStatusCode());
        }

        // JWT 관련 Exception
        else if (isKindOfJWTException(e)) {
            // 토큰 발급 시 throw된 Exception
            if (e instanceof JWTCreationException) {
                return new ResponseEntity<>(new ExceptionResponse(
                        TOKEN_CREATION_HAS_PROBLEM.getMessage(),
                        TOKEN_CREATION_HAS_PROBLEM.getErrorCode()),
                        TOKEN_CREATION_HAS_PROBLEM.getHttpStatusCode());
            } else {
                // 토큰 만료시 throw된 Exception
                if (e instanceof TokenExpiredException) {
                    return new ResponseEntity<>(new ExceptionResponse(
                            TOKEN_EXPIRED.getMessage(),
                            TOKEN_EXPIRED.getErrorCode()),
                            TOKEN_EXPIRED.getHttpStatusCode());
                }
                // 그 외 Exception은 토큰 조작 감지로 처리
                // TODO: 더 견고하게 처리할 수 없을지 생각해보기
                else {
                    return new ResponseEntity<>(new ExceptionResponse(
                            TOKEN_DETECTED_DIRTY.getMessage(),
                            TOKEN_DETECTED_DIRTY.getErrorCode()),
                            TOKEN_DETECTED_DIRTY.getHttpStatusCode());
                }
            }
        }

        // 요청 데이터 유효성 검증 Exception
        else if ((e instanceof BindException) || (e instanceof ConstraintViolationException)) {
            List<String> dataValidationErrors = new ArrayList<>();

            // request body일 경우
            if (e instanceof BindException) {
                List<ObjectError> errors = ((BindException) e).getAllErrors();

                for (ObjectError error : errors) {
                    dataValidationErrors.add(error.getDefaultMessage());
                }
            }
            // query paramter 또는 path variable일 경우
            else {
                Set<ConstraintViolation<?>> constraintViolationSet = ((ConstraintViolationException) e).getConstraintViolations();

                Iterator<ConstraintViolation<?>> iterator = constraintViolationSet.iterator();

                while (iterator.hasNext()) {
                    dataValidationErrors.add(iterator.next().getMessage());
                }
            }

            return new ResponseEntity<>(new RequestDataBindExceptionResponse(
                    INVALID_REQUEST_DATA.getMessage(),
                    INVALID_REQUEST_DATA.getErrorCode(),
                    dataValidationErrors),
                    INVALID_REQUEST_DATA.getHttpStatusCode());
        }

        // 그 외 예측할 수 없는 Exception일 경우
        else {
            return new ResponseEntity<>(new ExceptionResponse(
                    e.getMessage() + e.getClass(),
                    UNPREDICTABLE.getErrorCode()),
                    UNPREDICTABLE.getHttpStatusCode());
        }
    }

    /**
     *  net.nurigo.sdk.message.exception.NurigoException은 sub class가 존재하지 않습니다.
     *  따라서 구체적인 Exception들에 대해 전부 검사합니다.
     */
    private boolean isKindOfNurigoException(Throwable e) {
        return (e instanceof NurigoApiKeyException)
            || (e instanceof NurigoBadRequestException)
            || (e instanceof NurigoEmptyResponseException)
            || (e instanceof NurigoFileUploadException)
            || (e instanceof NurigoInvalidApiKeyException)
            || (e instanceof NurigoMessageNotReceivedException)
            || (e instanceof NurigoUnknownException)
            || (e instanceof NurigoUnregisteredSenderIdException);
    }

    private boolean isKindOfJWTException(Throwable e) {
        return (e instanceof JWTCreationException)
            || (e instanceof JWTVerificationException);
    }
}
