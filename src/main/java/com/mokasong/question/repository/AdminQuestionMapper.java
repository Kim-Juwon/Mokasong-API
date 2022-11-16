package com.mokasong.question.repository;

import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.dto.response.admin.QuestionsResponse;
import com.mokasong.question.entity.Question;
import com.mokasong.question.query.QuestionsCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AdminQuestionMapper {
    // Read
    QuestionResponse.AdminPageQuestion getQuestionForAdminPage(@Param("questionId") Long questionId);

    Question getQuestion(@Param("questionId") Long questionId);

    Long getTotalCountOfQuestionsByCondition(@Param("condition") QuestionsCondition condition);

    List<QuestionsResponse.Question> getQuestionsByCondition(@Param("begin") Long begin, @Param("condition") QuestionsCondition condition);

    // Delete
    void deleteQuestion(@Param("questionId") Long questionId); // soft delete
}
