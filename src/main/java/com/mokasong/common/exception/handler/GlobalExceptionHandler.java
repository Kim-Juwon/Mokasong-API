package com.mokasong.common.exception.handler;

import com.mokasong.common.exception.CustomException;
import com.mokasong.common.dto.response.ExceptionResponse;
import com.mokasong.common.dto.response.RequestDataInvalidExceptionResponse;
import net.nurigo.sdk.message.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
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


        else if (this.isKindOfMessageSendException(e)) {
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
            List<String> errorMessages = new LinkedList<>();

            // request body일 경우
            if (e instanceof BindException) {
                List<ObjectError> errors = ((BindException) e).getAllErrors();

                for (ObjectError error : errors) {
                    errorMessages.add(error.getDefaultMessage());
                }
            }
            // query paramter 또는 path variable일 경우
            else {
                Set<ConstraintViolation<?>> constraintViolationSet = ((ConstraintViolationException) e).getConstraintViolations();

                Iterator<ConstraintViolation<?>> iterator = constraintViolationSet.iterator();

                while (iterator.hasNext()) {
                    errorMessages.add(iterator.next().getMessage());
                }
            }

            return ResponseEntity
                    .status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(
                            RequestDataInvalidExceptionResponse.builder()
                                    .message("요청에 제약조건에 맞지 않는 값이 들어있습니다.")
                                    .errorCode(UNPROCESSABLE_ENTITY.getErrorCode())
                                    .errorMessages(errorMessages)
                                    .build()
                    );
        }

        else {
            // TODO: 알림 코드 작성

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

    /**
     *  net.nurigo.sdk.message.exception.NurigoException은 sub class가 존재하지 않습니다.
     *  따라서 구체적인 Exception들에 대해 전부 검사합니다.
     */
    private boolean isKindOfMessageSendException(Throwable e) {
        return (e instanceof NurigoApiKeyException)
            || (e instanceof NurigoBadRequestException)
            || (e instanceof NurigoEmptyResponseException)
            || (e instanceof NurigoFileUploadException)
            || (e instanceof NurigoInvalidApiKeyException)
            || (e instanceof NurigoMessageNotReceivedException)
            || (e instanceof NurigoUnknownException)
            || (e instanceof NurigoUnregisteredSenderIdException);
    }

    private boolean isKindOfRequestDataInvalidException(Throwable e) {
        return ((e instanceof BindException) || (e instanceof ConstraintViolationException));
    }
}
