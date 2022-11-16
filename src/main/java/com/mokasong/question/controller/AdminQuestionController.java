package com.mokasong.question.controller;

import com.mokasong.common.annotation.Auth;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.dto.response.admin.QuestionsResponse;
import com.mokasong.question.query.QuestionsCondition;
import com.mokasong.question.service.AdminQuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.mokasong.user.state.Authority.ADMIN;

@Tag(name = "문의 (어드민)", description = "문의 API - 어드민")
@Auth(ADMIN)
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/questions")
public class AdminQuestionController {
    private final AdminQuestionService adminQuestionService;

    @Tag(name = "문의 (어드민)")
    @GetMapping("/{id}")
    @ApiOperation(value = "문의 조회", notes = "문의 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable("id") Long questionId) throws Exception {
        QuestionResponse responseBody = adminQuestionService.getQuestion(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Tag(name = "문의 (어드민)")
    @GetMapping("")
    @ApiOperation(value = "페이지별 문의 목록 조회", notes = "페이지별 문의 목록 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<QuestionsResponse> getQuestions(@Valid QuestionsCondition condition) throws Exception {
        QuestionsResponse response = adminQuestionService.getQuestions(condition);

        return ResponseEntity
                .ok()
                .body(response);
    }

    @Tag(name = "문의 (어드민)")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "문의 삭제", notes = "문의 soft delete", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteQuestion(@PathVariable("id") Long questionId) throws Exception {
        SuccessfulResponse responseBody = adminQuestionService.deleteQuestion(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
