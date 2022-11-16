package com.mokasong.question.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.InternalServerErrorException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.dto.response.admin.QuestionsResponse;
import com.mokasong.question.entity.QuestionAnswer;
import com.mokasong.question.query.QuestionsCondition;
import com.mokasong.question.repository.AdminAnswerMapper;
import com.mokasong.question.repository.AdminQuestionMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.mokasong.common.exception.ErrorCode.INTERNAL_SERVER_ERROR;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminQuestionServiceImpl implements AdminQuestionService {
    private final AdminQuestionMapper questionMapper;
    private final AdminAnswerMapper answerMapper;

    @Override
    @Transactional(readOnly = true)
    public QuestionResponse getQuestion(Long questionId) throws Exception {
        QuestionResponse.AdminPageQuestion question = questionMapper.getQuestionForAdminPage(questionId);

        if (question == null) {
            throw new NotFoundException("문의가 존재하지 않습니다.", 1);
        }

        // 회원 탈퇴시 해당 회원이 작성한 문의도 전부 soft delete 되므로, 만약 문의가 조회되는데 작성자가 없다면 모순이다.
        if (question.writerNotExists()) {
            throw new InternalServerErrorException("작성자 정보가 없거나 탈퇴하였습니다.", INTERNAL_SERVER_ERROR.getErrorCode());
        }

        QuestionAnswer answer = answerMapper.getAnswerByQuestionId(questionId);

        // 답변 여부 확인
        question.decideAnswered(answer);

        return QuestionResponse.builder()
                .question(question)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionsResponse getQuestions(QuestionsCondition condition) throws Exception {
        Long totalCount = questionMapper.getTotalCountOfQuestionsByCondition(condition);
        Long totalPage = condition.extractTotalPage(totalCount);
        Long currentPage = condition.getPage();

        if (currentPage > totalPage) {
            throw new NotFoundException("유효하지 않은 페이지입니다.", 1);
        }

        List<QuestionsResponse.Question> questions = questionMapper.getQuestionsByCondition(condition.extractBegin(), condition);

        return QuestionsResponse.builder()
                .totalCount(totalCount)
                .totalPage(totalPage)
                .currentCount((long) questions.size())
                .currentPage(currentPage)
                .questions(questions)
                .build();
    }

    @Override
    public SuccessfulResponse deleteQuestion(Long questionId) throws Exception {
        if (questionMapper.getQuestion(questionId) == null) {
            throw new NotFoundException("문의가 존재하지 않습니다.", 1);
        }

        questionMapper.deleteQuestion(questionId);

        answerMapper.deleteAnswerByQuestionId(questionId);

        return SuccessfulResponse.builder()
                .success(true)
                .build();
    }
}
