package com.mokasong.question.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.question.dto.response.admin.QuestionResponse;

public interface QuestionService {
    QuestionResponse getQuestionForAdmin(Long questionId) throws Exception;
    SuccessfulResponse deleteQuestionForAdmin(Long questionId) throws Exception;
    SuccessfulResponse undeleteQuestionForAdmin(Long questionId) throws Exception;
}
