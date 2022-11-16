package com.mokasong.question.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.dto.response.admin.QuestionsResponse;
import com.mokasong.question.query.QuestionsCondition;

public interface AdminQuestionService {
    QuestionResponse getQuestion(Long questionId) throws Exception;

    QuestionsResponse getQuestions(QuestionsCondition condition) throws Exception;

    SuccessfulResponse deleteQuestion(Long questionId) throws Exception;
}
