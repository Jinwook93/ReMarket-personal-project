package com.cos.project.controller;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.CommentDTO;
import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.AlarmRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;
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
    private final AlarmService alarmService;
    private final BoardService boardService;
    private final MemberService memberService;

    private final CommentRepository commentRepository;
    private final AlarmRepository alarmRepository;
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
    @PostMapping("/board/{boardId}")									//게시글을 쓴 아이디를 반환
    public ResponseEntity<?> addComment(
        @PathVariable("boardId") Long boardId,
        @RequestBody CommentDTO commentDTO,
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) throws JsonProcessingException {
    	System.out.println("Private"+commentDTO.getIsPrivate());
        CommentEntity result = commentService.addComment(commentDTO, principalDetails.getMemberEntity());
        String boardUserId = result.getBoardEntity().getMemberEntity().getUserid();
        Long boardMemberId =  result.getBoardEntity().getMemberEntity().getId();			//보드 작성자
        
        
        String boardId_String = String.valueOf(boardId);
 
        
    	Long loggedId = principalDetails.getMemberEntity().getId();		// 로그인 유저
    	AlarmEntity alarmEntity_Board =alarmService.postAlarm(loggedId,loggedId, boardMemberId, "BOARD", "댓글", boardId_String, "등록", null);	
    	if(commentDTO.getParentCommentId() !=null) {
 	       String parentCommentId_String = String.valueOf(commentDTO.getParentCommentId());
 	       	MemberEntity parentCommentMember = commentRepository.findById(commentDTO.getParentCommentId()).get().getMemberEntity();	//부모 댓글을 쓴 유저
    		alarmService.postAlarm(loggedId,loggedId, parentCommentMember.getId(), "BOARD", "대댓글",parentCommentId_String, "등록", null);	
    		alarmEntity_Board.setMember1Visible(false);		//대댓글, 보드댓글 내용이 겹침
       		alarmRepository.save(alarmEntity_Board);
    	}
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
        @RequestBody CommentDTO commentDTO,
        @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
       	System.out.println("수정된 댓글 ID:"+id);
    	System.out.println("수정된 댓글 내용:"+commentDTO.getContent());
    	System.out.println("수정된 댓글 : 비밀? "+commentDTO.getIsPrivate());
        Boolean result = commentService.updateComment(id, commentDTO);
        BoardEntity boardEntity = commentRepository.findById(id).orElse(null).getBoardEntity();
       Long member2Id = boardEntity.getMemberEntity().getId(); //보드 관리자
       
       Long member1Id = commentRepository.findById(id).get().getMemberEntity().getId();	//댓글작성자
   
       Long loggedId = principalDetails.getMemberEntity().getId();		//로그인 유저
       
       CommentEntity commentEntity = commentRepository.findById(id).orElse(null);
       
        String id_String = String.valueOf(id);
    	AlarmEntity alarmEntity_Board = alarmService.postAlarm(loggedId,member1Id,member2Id, "BOARD", "댓글", id_String, "수정", null);	  
    	//보드 관리자에게도 알려야 함
    	
     	if(commentEntity.getParentComment()!=null) {
  	       String parentCommentId_String = String.valueOf(commentEntity.getParentComment().getId());
  	       	MemberEntity parentCommentMember = commentRepository.findById(commentEntity.getParentComment().getId()).get().getMemberEntity();	//부모 댓글을 쓴 유저
     		alarmService.postAlarm(loggedId,loggedId, parentCommentMember.getId(), "BOARD", "대댓글",parentCommentId_String, "수정", null);	
     		alarmEntity_Board.setMember1Visible(false);		//대댓글, 보드댓글 내용이 겹침
        		alarmRepository.save(alarmEntity_Board);
     	}
        return ResponseEntity.ok(result);
    }
    
   
    // 댓글 블라인드
    @PutMapping("/blind/{id}")
    public ResponseEntity<?> blindComment(
        @PathVariable("id") Long id
       , @RequestBody CommentDTO commentDTO,
       @AuthenticationPrincipal PrincipalDetails principalDetails
    ) {
        boolean result = commentService.blindComment(id, commentDTO.getIsBlind());
   
        CommentEntity commentEntity = commentRepository.findById(id).get();
        
        Long loggedId = principalDetails.getMemberEntity().getId();
        
        if(result) {
        	alarmService.postAlarm(loggedId,loggedId, commentEntity.getMemberEntity().getId(), "BOARD", "댓글",String.valueOf(id), "블라인드", null);	
        }else {
        	alarmService.postAlarm(loggedId,loggedId, commentEntity.getMemberEntity().getId(), "BOARD", "댓글",String.valueOf(id), "블라인드 취소", null);	
        }
        
        return ResponseEntity.ok(result);
    }  
    
    
    
    
    // 댓글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable("id") Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	
       
       
       	
       
       BoardEntity boardEntity = commentRepository.findById(id).orElse(null).getBoardEntity();
       Long member2Id = boardEntity.getMemberEntity().getId(); //보드 관리자
       
       Long member1Id = commentRepository.findById(id).get().getMemberEntity().getId();	//댓글작성자
   
       Long loggedId = principalDetails.getMemberEntity().getId();		//로그인 유저
       System.out.println("댓글 작성자"+member1Id);
       System.out.println("보드 관리자"+member2Id);
        String id_String = String.valueOf(id);			//댓글 id
        List<Long> result = commentService.deleteComment(id);   
    	alarmService.postAlarm(loggedId,member1Id,member2Id, "BOARD", "댓글", id_String, "삭제", null);	  
       
       //대댓글 관련 내용은 추가하지 않음
       
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
