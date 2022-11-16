package com.mokasong.question.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mokasong.common.dto.response.PaginationResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
import java.util.List;

@Getter @SuperBuilder
public class QuestionsResponse extends PaginationResponse {
    List<Question> questions;

    @Getter
    public final static class Question {
        private Long questionId;
        private String title;
        private String emailOfWriter;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updatedAt;
    }
}
