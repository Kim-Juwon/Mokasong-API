package com.mokasong.question.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
public class Question {
    private Long questionId;
    private Long userId;
    private String title;
    private String content;
    private Boolean isDeleted;
    private Date createdAt;
    private Date updatedAt;
}
