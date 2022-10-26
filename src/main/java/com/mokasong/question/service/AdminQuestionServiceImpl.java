package com.mokasong.question.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.ConflictException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.entity.Question;
import com.mokasong.question.entity.QuestionAnswer;
import com.mokasong.question.repository.AdminQuestionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminQuestionServiceImpl implements AdminQuestionService {
    private final AdminQuestionMapper adminQuestionMapper;

    public AdminQuestionServiceImpl(AdminQuestionMapper adminQuestionMapper) {
        this.adminQuestionMapper = adminQuestionMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long questionId) throws Exception {
        QuestionResponse.AdminPageQuestion question = adminQuestionMapper.getQuestionForAdminPage(questionId);

        if (question == null) {
            throw new NotFoundException("없는 문의입니다.", 1);
        }

        QuestionAnswer answer = adminQuestionMapper.getQuestionAnswerByQuestionId(questionId);

        // 답변 여부 확인
        question.decideAnswered(answer);

        return QuestionResponse.builder()
                .question(question)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse deleteQuestion(Long questionId) throws Exception {
        Question question = checkQuestionExists(questionId, 1);

        if (question.getIsDeleted()) {
            throw new ConflictException("이미 soft delete 되어있는 문의입니다.", 2);
        }

        adminQuestionMapper.deleteQuestion(questionId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    @Override
    @Transactional
    public SuccessfulResponse undeleteQuestion(Long questionId) throws Exception {
        Question question = checkQuestionExists(questionId, 1);

        if (!question.getIsDeleted()) {
            throw new ConflictException("soft delete 되어있는 문의가 아닙니다.", 2);
        }

        adminQuestionMapper.undeleteQuestion(questionId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }

    private Question checkQuestionExists(Long questionId, Integer errorCode) {
        Question question = adminQuestionMapper.getQuestion(questionId);

        if (question == null) {
            throw new NotFoundException("없는 문의입니다.", errorCode);
        }

        return question;
    }
}
