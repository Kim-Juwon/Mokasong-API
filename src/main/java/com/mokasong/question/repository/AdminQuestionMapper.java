package com.mokasong.question.repository;

import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.entity.Question;
import com.mokasong.question.entity.QuestionAnswer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminQuestionMapper {
    QuestionResponse.AdminPageQuestion getQuestionForAdminPage(Long questionId);
    QuestionAnswer getQuestionAnswerByQuestionId(Long questionId);
    Question getQuestion(Long questionId);
    void deleteQuestion(Long questionId);
    void undeleteQuestion(Long questionId);
}
