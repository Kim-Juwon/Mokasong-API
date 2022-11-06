package com.mokasong.common.dto.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter @SuperBuilder
public class RequestDataInvalidExceptionResponse extends ExceptionResponse {
    private List<FieldAndValue> errors;

    @Getter
    public static class FieldAndValue {
        private final String field;
        private final Object value;
        private final String message;

        public FieldAndValue(String field, Object value, String message) {
            this.field = field;
            this.value = value;
            this.message = message;
        }
    }
}
