package com.mokasong.question.dto.response.admin;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mokasong.question.entity.QuestionAnswer;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter @Builder
public class QuestionResponse {
    private AdminPageQuestion question;

    @Getter
    public static class AdminPageQuestion {
        private Long questionId;
        private String title;
        private Writer writer;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date createdAt;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private Date updatedAt;
        private Boolean isSecret;
        private String content;
        private Boolean answered;

        public boolean writerNotExists() {
            return this.writer == null;
        }

        public void decideAnswered(QuestionAnswer questionAnswer) {
            this.answered = (questionAnswer != null);
        }
    }

    @Getter
    private static class Writer {
        private Long userId;
        private String email;
        private String name;
    }
}
