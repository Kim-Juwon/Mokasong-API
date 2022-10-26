package com.mokasong.question.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.question.dto.response.admin.QuestionResponse;

public interface AdminQuestionService {
    QuestionResponse getQuestion(Long questionId) throws Exception;
    SuccessfulResponse deleteQuestion(Long questionId) throws Exception;
    SuccessfulResponse undeleteQuestion(Long questionId) throws Exception;
}
