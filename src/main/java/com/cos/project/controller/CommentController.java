package com.cos.project.controller;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.entity.CommentEntity;
import com.cos.project.service.CommentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController  // RESTful API로 사용
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 게시글에 대한 댓글 조회
    @GetMapping("/board/{id}")
    public List<CommentEntity> getAllCommentsAboutBoard(@PathVariable("id") Long id) {
        return commentService.getAllCommentAboutBoard(id);
    }

    // 댓글 추가
    @PostMapping("/board/{id}")
    public ResponseEntity<?> addComment(
        @PathVariable("id") Long id,
        @RequestBody CommentEntity commentEntity,
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws JsonProcessingException {

        CommentEntity result = commentService.addComment(id, commentEntity, principalDetails.getMemberEntity());

        	System.out.println("확인:"+result.getMemberEntity().getName());
        

       return ResponseEntity.ok(result);
        

    }

    // 댓글 삭제
    @DeleteMapping("/{id}")
    public String deleteComment(@PathVariable("id") Long id) {
        boolean result = commentService.deleteComment(id);
        if(result) {
            return "댓글 삭제 성공";
        } else {
            return "댓글 삭제 실패";
        }
    }

    // 댓글 수정
    @PutMapping("/{id}")
    public String updateComment(
        @PathVariable("id") Long id,
        @RequestBody CommentEntity commentEntity
    ) {
        boolean result = commentService.updateComment(id, commentEntity);
        if(result) {
            return "댓글 수정 성공";
        } else {
            return "댓글 수정 실패";
        }
    }
    
    
    
    
    
    
    
    
    
}
