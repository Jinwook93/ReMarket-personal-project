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
import com.cos.project.service.LikeService;
import com.cos.project.service.LikeService;
import com.cos.project.service.MemberService;

@RestController
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    
    @PostMapping("/board/{boardId}/like")
    public ResponseEntity<List<Integer>> likePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addLike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.toggleLike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }

    // 좋아요 취소
    @DeleteMapping("/board/{boardId}/like")
    public ResponseEntity<List<Integer>> unlikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeLike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.toggleLike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
        }

    // 싫어요 추가
    @PostMapping("/board/{boardId}/dislike")
    public ResponseEntity<List<Integer>> dislikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addDislike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike =  likeService.toggleDislike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }

    // 싫어요 취소
    @DeleteMapping("/board/{boardId}/dislike")
    public ResponseEntity<List<Integer>> undislikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeDislike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = 	 likeService.toggleDislike(boardId, principalDetails.getMemberEntity().getId());
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
    
    
    
    
    
    
    @PostMapping("/comment/{commentId}/like")
    public ResponseEntity<List<Integer>> commentlikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addLike(commentId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.commentToggleLike(commentId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }

    // 좋아요 취소
    @DeleteMapping("/comment/{commentId}/like")
    public ResponseEntity<List<Integer>> commentunlikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeLike(commentId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.commentToggleLike(commentId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
        }

    // 싫어요 추가
    @PostMapping("/comment/{commentId}/dislike")
    public ResponseEntity<List<Integer>> commentdislikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addDislike(commentId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike =  likeService.commentToggleDislike(commentId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }

    // 싫어요 취소
    @DeleteMapping("/comment/{commentId}/dislike")
    public ResponseEntity<List<Integer>> commentundislikePost(
            @PathVariable(name = "commentId") Long commentId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeDislike(commentId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = 	 likeService.commentToggleDislike(commentId, principalDetails.getMemberEntity().getId());
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
    	boolean result = likeService.chatToggleLike(messageId, principalDetails.getMemberEntity().getId(), flag);
        return ResponseEntity.ok(result );
    }

//    // 좋아요 취소
//    @PostMapping("/chat/{messageId}/unlike")
//    public ResponseEntity<List<Integer>> chatunlikePost(
//            @PathVariable(name = "messageId") Long messageId,
//            @AuthenticationPrincipal PrincipalDetails principalDetails) {
////        likeService.removeLike(commentId, principalDetails.getMemberEntity().getId());
//    	boolean result = likeService.chatToggleLike(messageId, principalDetails.getMemberEntity().getId());
//        return ResponseEntity.ok(totallike_dislike );
//        }
    
    
    
    
    // 버튼 활성화
    @PostMapping("/chat/{messageId}/buttonenable")
    public ResponseEntity<?> chatenableButton(
            @PathVariable(name = "messageId") Long messageId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
    
//        likeService.removeDislike(commentId, principalDetails.getMemberEntity().getId());
String result = 	 likeService.chat_findMemberLike_Dislike(messageId, principalDetails.getMemberEntity().getId());
    	System.out.println("버튼활성화 상태 도착");
    	return ResponseEntity.ok(result);
    }
    
    
    
}
