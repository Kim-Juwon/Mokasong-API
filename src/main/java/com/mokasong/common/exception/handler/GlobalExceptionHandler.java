package com.mokasong.common.exception.handler;

import com.mokasong.common.exception.CustomException;
import com.mokasong.common.dto.response.ExceptionResponse;
import com.mokasong.common.dto.response.RequestDataInvalidExceptionResponse;
import com.mokasong.common.exception.custom.UnprocessableEntityException;
import net.nurigo.sdk.message.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

import static com.mokasong.common.exception.ErrorCode.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleException(Throwable e, HandlerMethod handlerMethod) {
        if (e instanceof CustomException) {
            // TODO: Critical Exception에 대한 알림 코드 작성

            return ResponseEntity
                    .status(((CustomException) e).getHttpStatus())
                    .body(
                            ExceptionResponse.builder()
                                    .message(e.getMessage())
                                    .errorCode(((CustomException) e).getErrorCode())
                                    .build()
                    );
        }

        else if (e instanceof NurigoException) {
            // TODO: 알림 코드 작성

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            ExceptionResponse.builder()
                                    .message(e.getMessage())
                                    .errorCode(INTERNAL_SERVER_ERROR.getErrorCode())
                                    .build()
                    );
        }

        else if (this.isKindOfRequestDataInvalidException(e)) {
            List<RequestDataInvalidExceptionResponse.FieldAndValue> errors = new LinkedList<>();

            // request body일 경우
            if (e instanceof BindException) {
                List<FieldError> fieldErrors = ((BindException) e).getFieldErrors();

                for (FieldError fieldError : fieldErrors) {
                    errors.add(new RequestDataInvalidExceptionResponse.FieldAndValue(
                            fieldError.getField(), fieldError.getRejectedValue(), fieldError.getDefaultMessage()
                    ));
                }
            }
            // query paramter 또는 path variable일 경우
            else if (e instanceof ConstraintViolationException){
                Set<ConstraintViolation<?>> constraintViolationSet = ((ConstraintViolationException) e).getConstraintViolations();

                for (ConstraintViolation<?> violation : constraintViolationSet) {
                    String[] divided = violation.getPropertyPath().toString().split("\\.");
                    String field = divided[divided.length - 1];
                    Object invalidValue = violation.getInvalidValue();

                    errors.add(new RequestDataInvalidExceptionResponse.FieldAndValue(
                            field, invalidValue, violation.getMessage()
                    ));
                }
            }
            // 직접 throw한 Exception일경우 (UnprocessableEntityException)
            else {
                errors.add(new RequestDataInvalidExceptionResponse.FieldAndValue(
                        ((UnprocessableEntityException) e).getField(),
                        ((UnprocessableEntityException) e).getInvalidValue(),
                        e.getMessage()
                ));
            }

            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(
                            RequestDataInvalidExceptionResponse.builder()
                                    .message("요청에 제약조건에 맞지 않는 값이 들어있습니다.")
                                    .errorCode(UNPROCESSABLE_ENTITY.getErrorCode())
                                    .errors(errors)
                                    .build()
                    );
        }

        else {
            // TODO: 알림 코드 작성
            System.out.println(e.getMessage());
            e.printStackTrace();

            return ResponseEntity
                    .internalServerError()
                    .body(
                            ExceptionResponse.builder()
                                    .message("예기치 않은 에러가 발생하였습니다.")
                                    .errorCode(INTERNAL_SERVER_ERROR.getErrorCode())
                                    .build()
                    );
        }
    }

    private boolean isKindOfRequestDataInvalidException(Throwable e) {
        return (e instanceof BindException)
            || (e instanceof ConstraintViolationException)
            || (e instanceof UnprocessableEntityException);
    }
}
