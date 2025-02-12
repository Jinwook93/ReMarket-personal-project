package com.cos.project.controller;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.CommentDTO;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController  // RESTful API로 사용
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    // 게시글에 대한 댓글 조회
//    @GetMapping("/board/{id}")
//    public List<CommentEntity> getAllCommentsAboutBoard(@PathVariable("id") Long id) {
//        return commentService.getAllCommentAboutBoard(id);
//    }
    
    @GetMapping("/board/{id}")			//부모 댓글이 없는 댓글들을 역순으로 나열
    public List<CommentEntity> getAllCommentsAboutBoard(@PathVariable("id") Long id) {
        return commentService.getAllCommentAboutBoard(id);
    }
    
//    @GetMapping("/board/{boardId}")
//    public List<CommentEntity> getComments(@PathVariable(name = "boardId") Long boardId, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        String loggedInUserId = (principalDetails != null) ? principalDetails.getMemberEntity().getUserid() : null;
//        List<CommentEntity> filteredComments = commentService.getFilteredComments(boardId, loggedInUserId);
//        return filteredComments;
//    }
    
    
    
    
    
    @GetMapping("/board/child/{parentCommentId}")			//부모 댓글에 대한 자식 댓글들을 역순으로 나열
    public List<CommentEntity> getChildComments(@PathVariable("parentCommentId") Long parentCommentId) {
    	System.out.println("자식 테스트!!!!!!!!!!!!"+parentCommentId);
        return commentService.getChildComment(parentCommentId);
    }

    // 댓글 추가
    @PostMapping("/board/{id}")									//게시글을 쓴 아이디를 반환
    public ResponseEntity<?> addComment(
        @PathVariable("id") Long boardId,
        @RequestBody CommentDTO commentDTO,
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws JsonProcessingException {
    	System.out.println("Private"+commentDTO.getIsPrivate());
        CommentEntity result = commentService.addComment(commentDTO, principalDetails.getMemberEntity());
        String boardUserId = result.getBoardEntity().getMemberEntity().getUserid();
        
       return ResponseEntity.ok(boardUserId);
        

    }
    
    
    
//    // 대댓글(자식 댓글) 추가
//    @PostMapping("/comment/{parentCommentId}")
//    public ResponseEntity<?> addChildComment(
//        @PathVariable("parentCommentId") Long id,
//        @RequestBody CommentDTO commentDTO,
//        @AuthenticationPrincipal PrincipalDetails principalDetails
//    ) throws JsonProcessingException {
//        CommentEntity result = commentService.addComment(commentDTO,principalDetails.getMemberEntity());
//
//        	System.out.println("확인:"+result.getMemberEntity().getName());
//        
//
//       return ResponseEntity.ok(result);
//        
//
//    }
    



    // 댓글 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(
        @PathVariable("id") Long id,
        @RequestBody CommentDTO commentDTO
    ) {
       	System.out.println("수정된 댓글 ID:"+id);
    	System.out.println("수정된 댓글 내용:"+commentDTO.getContent());
    	System.out.println("수정된 댓글 : 비밀? "+commentDTO.getIsPrivate());
        Boolean result = commentService.updateComment(id, commentDTO);
	      
        return ResponseEntity.ok(result);
    }
    
   
    // 댓글 블라인드
    @PutMapping("/blind/{id}")
    public ResponseEntity<?> blindComment(
        @PathVariable("id") Long id
       , @RequestBody CommentDTO commentDTO
    ) {
        boolean result = commentService.blindComment(id, commentDTO.getIsBlind());
        return ResponseEntity.ok(result);
    }  
    
    
    
    
    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id) {
       List<Long> result = commentService.deleteComment(id);   	
        return ResponseEntity.ok(result);
    }
    
    //부모 댓글 찾기
    @GetMapping("/parent/{id}")
    public ResponseEntity<?> findParentComment(@PathVariable("id") Long id) {
        CommentEntity parentComment = commentService.findParentComment(id);

        // 부모 댓글에서 필요한 속성만 가져와서 Map에 담기
        Map<String, Object> parentObject = new HashMap<>();
        parentObject.put("id", parentComment.getId());
        parentObject.put("loggedId", parentComment.getMemberEntity().getId());
        parentObject.put("userid", parentComment.getMemberEntity().getUserid());

        // Map을 응답으로 반환
        return ResponseEntity.ok(parentObject);
    }

    
    
}
