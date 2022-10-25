package com.mokasong.question.entity;

import lombok.Getter;

import java.util.Date;

@Getter
public class QuestionAnswer {
    private Long questionAnswerId;
    private Long questionId;
    private Long userId;
    private String content;
    private Boolean seen;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;
}
