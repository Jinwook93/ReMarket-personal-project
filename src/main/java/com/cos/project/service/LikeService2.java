//package com.cos.project.service;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.stereotype.Service;
//
//import com.cos.project.entity.BoardEntity;
//import com.cos.project.entity.commentLikeEntity;
//import com.cos.project.entity.MemberEntity;
//import com.cos.project.repository.commentLikeRepository;
//import com.cos.project.repository.BoardRepository;
//import com.cos.project.repository.CommentRepository;
//import com.cos.project.repository.MemberRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class LikeService2 {
//
//    private final commentLikeRepository commentLikeRepository;
//    private final BoardRepository boardRepository;
//    private final MemberRepository memberRepository;
//    private final CommentRepository commentRepository;
//
//    public List<Integer> commentToggleLike(Long boardId, Long memberId) {
//        BoardEntity boardEntity = boardRepository.findById(boardId)
//                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
//        MemberEntity memberEntity = memberRepository.findById(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
//
//        // 기존에 싫어요가 눌려 있었다면, 싫어요 취소
//        if (commentLikeRepository.findcommentLikeEntity(boardId, memberId).isPresent() &&
//                commentLikeRepository.findcommentLikeEntity(boardId, memberId).get().isFlag() == false) {
//            boardEntity.setTotalDislike(boardEntity.getTotalDislike() - 1);  // 싫어요 감소
//            commentLikeRepository.deletecommentLikeEntity(boardId, memberId, false);  // 싫어요 삭제
//        }
//
//        // 이미 좋아요가 눌러져 있으면 좋아요 취소
//        Optional<commentLikeEntity> existingLike = commentLikeRepository.findcommentLikeEntity(boardId, memberId);
//        if (existingLike.isPresent() && existingLike.get().isFlag() == true) {
//            // 좋아요 취소
//            boardEntity.setTotalLike(boardEntity.getTotalLike() - 1);  // 좋아요 감소
//            commentLikeRepository.deletecommentLikeEntity(boardId, memberId, true);  // 좋아요 삭제
//        } else {
//            // 좋아요 추가
//            boardEntity.setTotalLike(boardEntity.getTotalLike() + 1);  // 좋아요 증가
//            commentLikeEntity commentLikeEntity = new commentLikeEntity();
//            commentLikeEntity.setBoardEntity(boardEntity);
//            commentLikeEntity.setMemberEntity(memberEntity);
//            commentLikeEntity.setFlag(true);  // 좋아요 상태로 설정
//            commentLikeRepository.save(commentLikeEntity);
//        }
//
//        // 총 좋아요와 싫어요 수가 음수가 되지 않도록 처리
//        if (boardEntity.getTotalLike() < 0) {
//            boardEntity.setTotalLike(0);
//        }
//        if (boardEntity.getTotalDislike() < 0) {
//            boardEntity.setTotalDislike(0);
//        }
//
//        // 결과 반환: 총 좋아요, 총 싫어요, 좋아요 상태 (true/false), 싫어요 상태 (true/false)
//        boolean isLiked = existingLike.isPresent() && existingLike.get().isFlag() == true;
//        return Arrays.asList(boardEntity.getTotalLike(), boardEntity.getTotalDislike());
//    }
//
//    public List<Integer> commentToggleDislike(Long boardId, Long memberId) {
//        BoardEntity boardEntity = boardRepository.findById(boardId)
//                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
//        MemberEntity memberEntity = memberRepository.findById(memberId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
//
//        // 기존에 좋아요가 눌려 있었다면, 좋아요 취소
//        if (commentLikeRepository.findcommentLikeEntity(boardId, memberId).isPresent() &&
//                commentLikeRepository.findcommentLikeEntity(boardId, memberId).get().isFlag() == true) {
//            boardEntity.setTotalLike(boardEntity.getTotalLike() - 1);  // 좋아요 감소
//            commentLikeRepository.deletecommentLikeEntity(boardId, memberId, true);  // 좋아요 삭제
//        }
//
//        // 이미 싫어요가 눌러져 있으면 싫어요 취소
//        Optional<commentLikeEntity> existingDislike = commentLikeRepository.findcommentLikeEntity(boardId, memberId);
//        if (existingDislike.isPresent() && existingDislike.get().isFlag() == false) {
//            // 싫어요 취소
//            boardEntity.setTotalDislike(boardEntity.getTotalDislike() - 1);  // 싫어요 감소
//            commentLikeRepository.deletecommentLikeEntity(boardId, memberId, false);  // 싫어요 삭제
//        } else {
//            // 싫어요 추가
//            boardEntity.setTotalDislike(boardEntity.getTotalDislike() + 1);  // 싫어요 증가
//            commentLikeEntity commentLikeEntity = new commentLikeEntity();
//            commentLikeEntity.setBoardEntity(boardEntity);
//            commentLikeEntity.setMemberEntity(memberEntity);
//            commentLikeEntity.setFlag(false);  // 싫어요 상태로 설정
//            commentLikeRepository.save(commentLikeEntity);
//        }
//
//        // 총 좋아요와 싫어요 수가 음수가 되지 않도록 처리
//        if (boardEntity.getTotalLike() < 0) {
//            boardEntity.setTotalLike(0);
//        }
//        if (boardEntity.getTotalDislike() < 0) {
//            boardEntity.setTotalDislike(0);
//        }
//
//        // 결과 반환: 총 좋아요, 총 싫어요, 좋아요 상태 (true/false), 싫어요 상태 (false/true)
//        boolean isDisliked = existingDislike.isPresent() && existingDislike.get().isFlag() == false;
//        return Arrays.asList(boardEntity.getTotalLike(), boardEntity.getTotalDislike());
//    }																															//1 : LIKE 활성화 신호
//
//    
//    public String comment_findMemberLike_Dislike(Long boardId, Long memberId) {
//    	commentLikeEntity commentLikeEntity = commentLikeRepository.findcommentLikeEntity(boardId, memberId).get();
//    		if(commentLikeEntity.getId() != null &&commentLikeEntity.isFlag() == true) {
//    			return "ENABLE_LIKE";
//    		}else if (commentLikeEntity.getId() != null &&commentLikeEntity.isFlag() == false)  {
//    			return "ENABLE_DISLIKE";
//    		}else {				//if (commentLikeEntity.getId() == null) 
//    			return "DISABLE";
//    		}
//    }
//
//}
