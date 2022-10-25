package com.mokasong.question.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.service.QuestionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mokasong.user.state.Authority.ADMIN;

@RestController
@RequestMapping("/admin/questions")
public class AdminQuestionController {
    private final QuestionService questionService;

    public AdminQuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @Login(ADMIN)
    @GetMapping("/{id}")
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable("id") Long questionId) throws Exception {
        QuestionResponse responseBody = questionService.getQuestionForAdmin(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<SuccessfulResponse> deleteQuestion(@PathVariable("id") Long questionId) throws Exception {
        SuccessfulResponse responseBody = questionService.deleteQuestionForAdmin(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @PatchMapping("{id}/undelete")
    public ResponseEntity<SuccessfulResponse> undeleteQuestion(@PathVariable("id") Long questionId) throws Exception {
        SuccessfulResponse responseBody = questionService.undeleteQuestionForAdmin(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
