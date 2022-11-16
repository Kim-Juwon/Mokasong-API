package com.mokasong.question.entity;

import com.mokasong.common.exception.custom.UnprocessableEntityException;
import com.mokasong.question.dto.request.admin.CreateAnswerRequest;
import com.mokasong.question.dto.request.admin.UpdateAnswerRequest;
import com.mokasong.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Objects;

@Getter
@NoArgsConstructor
public class QuestionAnswer {
    private Long questionAnswerId;
    private Long questionId;
    private Long userId; // 답변 작성자 id
    private String content;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;

    public QuestionAnswer(Long questionId, Long userId, CreateAnswerRequest requestBody) throws Exception {
        if (this.exceedMaximumOfByte(requestBody.getContent())) {
            throw new UnprocessableEntityException("최대 65535 byte 입니다.", "content", requestBody.getContent());
        }

        this.questionId = questionId;
        this.userId = userId;
        this.content = requestBody.getContent();
    }

    public QuestionAnswer update(User user, UpdateAnswerRequest requestBody) {
        this.userId = user.getUserId();
        this.content = requestBody.getContent();
        return this;
    }

    public boolean isAbout(Long questionId) {
        return this.questionId.equals(questionId);
    }

    public boolean sameAs(User user, UpdateAnswerRequest requestBody) {
        return Objects.equals(this.userId, user.getUserId())
                && Objects.equals(this.content, requestBody.getContent());
    }

    private boolean exceedMaximumOfByte(String content) {
        // MySQL8 의 TEXT 최대 byte는 65535
        return content.getBytes().length > 65535;
    }
}
