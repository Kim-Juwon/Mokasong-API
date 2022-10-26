package com.mokasong.question.controller;

import com.mokasong.common.annotation.Login;
import com.mokasong.common.dto.response.SuccessfulResponse;
import com.mokasong.question.dto.response.admin.QuestionResponse;
import com.mokasong.question.service.AdminQuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.Authorization;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static com.mokasong.user.state.Authority.ADMIN;

@RestController
@RequestMapping("/admin/questions")
@Tag(name = "Admin Question API", description = "문의 API - 어드민")
public class AdminQuestionController {
    private final AdminQuestionService adminQuestionService;

    @Value("${application.schema-and-host.current}")
    private String schemaAndHost;

    public AdminQuestionController(AdminQuestionService adminQuestionService) {
        this.adminQuestionService = adminQuestionService;
    }

    @Login(ADMIN)
    @Tag(name = "Admin Question API")
    @GetMapping("/{id}")
    @ApiOperation(value = "문의 조회", notes = "문의 조회", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<QuestionResponse> getQuestion(@PathVariable("id") Long questionId) throws Exception {
        QuestionResponse responseBody = adminQuestionService.getQuestion(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @Tag(name = "Admin Question API")
    @DeleteMapping("/{id}")
    @ApiOperation(value = "문의 삭제", notes = "문의 soft delete", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> deleteQuestion(@PathVariable("id") Long questionId) throws Exception {
        SuccessfulResponse responseBody = adminQuestionService.deleteQuestion(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }

    @Login(ADMIN)
    @Tag(name = "Admin Question API")
    @PatchMapping("{id}/undelete")
    @ApiOperation(value = "문의 삭제 해제", notes = "문의의 soft delete 해제", authorizations = @Authorization("Access-Token"))
    public ResponseEntity<SuccessfulResponse> undeleteQuestion(@PathVariable("id") Long questionId) throws Exception {
        SuccessfulResponse responseBody = adminQuestionService.undeleteQuestion(questionId);

        return ResponseEntity
                .ok()
                .body(responseBody);
    }
}
