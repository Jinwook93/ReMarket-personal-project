package com.cos.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.repository.MessageRepository;
import com.cos.project.service.AlarmService;
import com.cos.project.service.BoardService;
import com.cos.project.service.ChatService;
import com.cos.project.service.CommentService;
import com.cos.project.service.LikeService;
import com.cos.project.service.LikeService;
import com.cos.project.service.MemberService;
import com.cos.project.service.MessageService;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @Autowired
    private AlarmService alarmService;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private BoardService boardService;
    
    @Autowired
    private CommentService commentService;
    
    @Autowired
    private MemberRepository memberRepository ;
    
    @Autowired
    private BoardRepository boardRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    MessageRepository messageRepository;
    
    @PostMapping("/board/{boardId}/like")
    public ResponseEntity<List<Integer>> likePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addLike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.toggleLike(boardId, principalDetails.getMemberEntity().getId());
   
    	
    	Long loggedId = principalDetails.getMemberEntity().getId();
    	BoardEntity boardEntity = boardRepository.findById(boardId).get();
    	Long member2Id = boardEntity.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "게시판", "좋아요", "활성화", null);	
    	
    	
    	return ResponseEntity.ok(totallike_dislike );
    }

    // 좋아요 취소
    @DeleteMapping("/board/{boardId}/like")
    public ResponseEntity<List<Integer>> unlikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeLike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.toggleLike(boardId, principalDetails.getMemberEntity().getId());
       
      	Long loggedId = principalDetails.getMemberEntity().getId();
    	BoardEntity boardEntity = boardRepository.findById(boardId).get();
    	Long member2Id = boardEntity.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "게시판", "좋아요", "취소", null);	
    	
    	return ResponseEntity.ok(totallike_dislike );
        }

    // 싫어요 추가
    @PostMapping("/board/{boardId}/dislike")
    public ResponseEntity<List<Integer>> dislikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addDislike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike =  likeService.toggleDislike(boardId, principalDetails.getMemberEntity().getId());

      	Long loggedId = principalDetails.getMemberEntity().getId();
    	BoardEntity boardEntity = boardRepository.findById(boardId).get();
    	Long member2Id = boardEntity.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD",  "게시판", "싫어요","활성화", null);	
    	
    	
    	
    	return ResponseEntity.ok(totallike_dislike );
    }

    // 싫어요 취소
    @DeleteMapping("/board/{boardId}/dislike")
    public ResponseEntity<List<Integer>> undislikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeDislike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = 	 likeService.toggleDislike(boardId, principalDetails.getMemberEntity().getId());
     
      	Long loggedId = principalDetails.getMemberEntity().getId();
    	BoardEntity boardEntity = boardRepository.findById(boardId).get();
    	Long member2Id = boardEntity.getMemberEntity().getId();
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "게시판", "싫어요", "취소", null);	
    	
    	return ResponseEntity.ok(totallike_dislike );
    }
    
    // 버튼 활성화
    @PostMapping("/board/{boardId}/buttonenable")
    public ResponseEntity<?> enableButton(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
    
//        likeService.removeDislike(boardId, principalDetails.getMemberEntity().getId());
    	String result = 	 likeService.findMemberLike_Dislike(boardId, principalDetails.getMemberEntity().getId());
    	System.out.println("버튼활성화 상태 도착");
    	return ResponseEntity.ok(result);
    }
    
    
    
    
    
    //=============================댓글 좋아요 컨트롤러
    
    
    
    
    
    //좋아요 활성화
    @PostMapping("/comment/{commentId}/like")				
    public ResponseEntity<List<Integer>> commentlikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
  
        
    	CommentEntity commentEntity = commentRepository.findById(commentId).orElse(null);
    	
     	Long loggedId = principalDetails.getMemberEntity().getId();
    	Long member2Id = commentEntity.getMemberEntity().getId();
    	
      	List<Integer> totallike_dislike = likeService.commentToggleLike(commentId, principalDetails.getMemberEntity().getId());
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "댓글", "좋아요", "활성화", null);	
    	
    	
    	return ResponseEntity.ok(totallike_dislike );
    }

    // 좋아요 취소
    @DeleteMapping("/comment/{commentId}/like")
    public ResponseEntity<List<Integer>> commentunlikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	CommentEntity commentEntity = commentRepository.findById(commentId).orElse(null);
    	
     	Long loggedId = principalDetails.getMemberEntity().getId();
    	Long member2Id = commentEntity.getMemberEntity().getId();
    	List<Integer> totallike_dislike = likeService.commentToggleLike(commentId, principalDetails.getMemberEntity().getId());
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "댓글", "좋아요", "취소", null);	
    	
    	
    	return ResponseEntity.ok(totallike_dislike );
        }

    // 싫어요 추가
    @PostMapping("/comment/{commentId}/dislike")
    public ResponseEntity<List<Integer>> commentdislikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
      
    	CommentEntity commentEntity = commentRepository.findById(commentId).orElse(null);
    	
     	Long loggedId = principalDetails.getMemberEntity().getId();
    	Long member2Id = commentEntity.getMemberEntity().getId();
    	List<Integer> totallike_dislike =  likeService.commentToggleDislike(commentId, principalDetails.getMemberEntity().getId());
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "댓글", "싫어요", "활성화", null);	
    	return ResponseEntity.ok(totallike_dislike );
    }

    // 싫어요 취소
    @DeleteMapping("/comment/{commentId}/dislike")
    public ResponseEntity<List<Integer>> commentundislikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
    	CommentEntity commentEntity = commentRepository.findById(commentId).orElse(null);
     	Long loggedId = principalDetails.getMemberEntity().getId();
     	Long member2Id = commentEntity.getMemberEntity().getId();
    	List<Integer> totallike_dislike = 	 likeService.commentToggleDislike(commentId, principalDetails.getMemberEntity().getId());
		alarmService.postAlarm(loggedId,loggedId, member2Id, "BOARD", "댓글", "싫어요", "취소", null);	
    	
    	
    	return ResponseEntity.ok(totallike_dislike );
    }
    
    // 버튼 활성화
    @PostMapping("/comment/{commentId}/buttonenable")
    public ResponseEntity<?> commentenableButton(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
    
//        likeService.removeDislike(commentId, principalDetails.getMemberEntity().getId());
String result = 	 likeService.comment_findMemberLike_Dislike(commentId, principalDetails.getMemberEntity().getId());
    	System.out.println("버튼활성화 상태 도착");
    	return ResponseEntity.ok(result);
    }
    
    
    
    
    
    
    
    //=============================채팅 좋아요 컨트롤러

    @PostMapping("/chat/{messageId}/like")
    public ResponseEntity<?> chatlikePost(
            @PathVariable(name = "messageId") Long messageId,
            @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody boolean flag) {
//        likeService.addLike(commentId, principalDetails.getMemberEntity().getId());
    	
    	Long loggedId = principalDetails.getMemberEntity().getId();
    	boolean result = likeService.chatToggleLike(messageId, loggedId , flag);
    	
    	MessageEntity message = messageRepository.findById(messageId).get();
    	
    	ChattingRoomEntity room = message.getChattingRoomEntity();
    	
//    	if(result == true) {
//    		alarmService.postAlarm(loggedId,room.getMember1().getId(), room.getMember2().getId(), "MESSAGE", "댓글", "좋아요", "활성화", null);	
//    	}else {
//    		alarmService.postAlarm(loggedId,room.getMember1().getId(), room.getMember2().getId(), "MESSAGE", "댓글", "좋아요", "취소", null);	
//    	}
    	
    	String statusText ="";
    	
    	if(result == true) {
    		statusText = "활성화";
    	}else {
    		statusText = "취소";
    	}
    	
    	
    	alarmService.postAlarm(loggedId,room.getMember1().getId(), room.getMember2().getId(), "MESSAGE", "좋아요",String.valueOf(room.getId()),statusText,null);	
    	
    	
    	
//    	alarmService.postAlarm(member1.getId(),member1.getId(), member2.getId(), "MESSAGE", "게시판", String.valueOf(roomId), "삭제", null);
        return ResponseEntity.ok(result );
    }
 
    
    
}
