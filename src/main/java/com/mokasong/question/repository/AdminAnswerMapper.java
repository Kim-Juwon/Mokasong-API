package com.mokasong.question.repository;

import com.mokasong.question.entity.QuestionAnswer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AdminAnswerMapper {
    // Create
    void createAnswer(@Param("answer") QuestionAnswer answer);


    // Read
    QuestionAnswer getAnswer(@Param("answerId") Long answerId);

    QuestionAnswer getAnswerByQuestionId(@Param("questionId") Long questionId);


    // Update
    void updateAnswer(@Param("answer") QuestionAnswer answer);


    // Delete
    void deleteAnswer(@Param("answerId") Long answerId);

    void deleteAnswerByQuestionId(@Param("questionId") Long questionId);
}