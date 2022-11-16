package com.mokasong.question.service;

import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.common.exception.custom.InternalServerErrorException;
import com.mokasong.common.exception.custom.NotFoundException;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.dto.response.admin.QuestionsResponse;
import com.mokasong.question.entity.Question;
import com.mokasong.question.query.QuestionsCondition;
import com.mokasong.question.repository.AdminAnswerMapper;
import com.mokasong.question.repository.AdminQuestionMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class AdminQuestionServiceImplTest {
    @Mock AdminQuestionMapper adminQuestionMapper;
    @Mock AdminAnswerMapper adminAnswerMapper;
    AdminQuestionService service;

    @BeforeEach
    void init() {
        service = new AdminQuestionServiceImpl(adminQuestionMapper, adminAnswerMapper);
    }

    @Nested
    class getQuestion {
        Long questionId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock QuestionResponse.AdminPageQuestion question) throws Exception {
                // given
                questionId = 1L;


                // stubbing
                doReturn(question)
                        .when(adminQuestionMapper).getQuestionForAdminPage(questionId);
                doReturn(false)
                        .when(question).writerNotExists();
                doReturn(null)
                        .when(adminAnswerMapper).getAnswerByQuestionId(questionId);


                // when
                QuestionResponse response = service.getQuestion(questionId);


                // then
                assertNotNull(response.getQuestion());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("조회 안됨")
            void questionNotFound() {
                // given
                questionId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminQuestionMapper).getQuestionForAdminPage(questionId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getQuestion(questionId);
                });

                assertEquals("문의가 존재하지 않습니다.", exception.getMessage());
            }

            @Test
            @DisplayName("작성자 조회 안됨")
            void writerNotFound(@Mock QuestionResponse.AdminPageQuestion question) {
                // given
                questionId = 1L;


                // stubbing
                doReturn(question)
                        .when(adminQuestionMapper).getQuestionForAdminPage(questionId);
                doReturn(true)
                        .when(question).writerNotExists();


                // when & then
                Exception exception = assertThrows(InternalServerErrorException.class, () -> {
                    service.getQuestion(questionId);
                });

                assertEquals("작성자 정보가 없거나 탈퇴하였습니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class getQuestions {
        QuestionsCondition condition = new QuestionsCondition();

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test() throws Exception {
                // given
                condition.setLimit(10L);
                condition.setPage(1L);


                // stubbing
                doReturn(48L)
                        .when(adminQuestionMapper).getTotalCountOfQuestionsByCondition(condition);
                doReturn(new ArrayList<QuestionsResponse.Question>())
                        .when(adminQuestionMapper).getQuestionsByCondition(condition.extractBegin(), condition);


                // when
                QuestionsResponse response = service.getQuestions(condition);


                // then
                assertNotNull(response.getTotalCount());
                assertNotNull(response.getTotalPage());
                assertNotNull(response.getCurrentCount());
                assertNotNull(response.getCurrentPage());
                assertNotNull(response.getQuestions());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("유효하지 않은 페이지")
            void invalidPage() throws Exception {
                // given
                condition.setLimit(10L);
                condition.setPage(6L);


                // stubbing
                doReturn(48L)
                        .when(adminQuestionMapper).getTotalCountOfQuestionsByCondition(condition);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.getQuestions(condition);
                });

                assertEquals("유효하지 않은 페이지입니다.", exception.getMessage());
            }
        }
    }

    @Nested
    class deleteQuestion {
        Long questionId;

        @Nested
        @DisplayName("성공")
        class Success {

            @Test
            @DisplayName("성공 케이스")
            void test(@Mock Question question) throws Exception {
                // given
                questionId = 1L;


                // stubbing
                doReturn(question)
                        .when(adminQuestionMapper).getQuestion(questionId);


                // when
                SuccessfulResponse response = service.deleteQuestion(questionId);


                // then
                assertTrue(response.getSuccess());
            }
        }

        @Nested
        @DisplayName("실패")
        class Fail {

            @Test
            @DisplayName("문의가 조회되지 않음")
            void questionNotFound() throws Exception {
                // given
                questionId = 1L;


                // stubbing
                doReturn(null)
                        .when(adminQuestionMapper).getQuestion(questionId);


                // when & then
                Exception exception = assertThrows(NotFoundException.class, () -> {
                    service.deleteQuestion(questionId);
                });

                assertEquals("문의가 존재하지 않습니다.", exception.getMessage());
            }
        }
    }
}