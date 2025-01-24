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
@RequestMapping("/board")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    
    @PostMapping("/{boardId}/like")
    public ResponseEntity<List<Integer>> likePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addLike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.toggleLike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }

    // 좋아요 취소
    @DeleteMapping("/{boardId}/like")
    public ResponseEntity<List<Integer>> unlikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeLike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = likeService.toggleLike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
        }

    // 싫어요 추가
    @PostMapping("/{boardId}/dislike")
    public ResponseEntity<List<Integer>> dislikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.addDislike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike =  likeService.toggleDislike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }

    // 싫어요 취소
    @DeleteMapping("/{boardId}/dislike")
    public ResponseEntity<List<Integer>> undislikePost(
            @PathVariable(name = "boardId") Long boardId,
            @AuthenticationPrincipal PrincipalDetails principalDetails) {
//        likeService.removeDislike(boardId, principalDetails.getMemberEntity().getId());
    	List<Integer> totallike_dislike = 	 likeService.toggleDislike(boardId, principalDetails.getMemberEntity().getId());
        return ResponseEntity.ok(totallike_dislike );
    }
}
