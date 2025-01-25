package com.cos.project.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.BoardLikeEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.repository.MemberRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    public List<Integer> toggleLike(Long boardId, Long memberId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 기존에 싫어요가 눌려 있었다면, 싫어요 취소
        if (boardLikeRepository.findBoardLikeEntity(boardId, memberId).isPresent() &&
                boardLikeRepository.findBoardLikeEntity(boardId, memberId).get().isFlag() == false) {
            boardEntity.setTotalDislike(boardEntity.getTotalDislike() - 1);  // 싫어요 감소
            boardLikeRepository.deleteBoardLikeEntity(boardId, memberId, false);  // 싫어요 삭제
        }

        // 이미 좋아요가 눌러져 있으면 좋아요 취소
        Optional<BoardLikeEntity> existingLike = boardLikeRepository.findBoardLikeEntity(boardId, memberId);
        if (existingLike.isPresent() && existingLike.get().isFlag() == true) {
            // 좋아요 취소
            boardEntity.setTotalLike(boardEntity.getTotalLike() - 1);  // 좋아요 감소
            boardLikeRepository.deleteBoardLikeEntity(boardId, memberId, true);  // 좋아요 삭제
        } else {
            // 좋아요 추가
            boardEntity.setTotalLike(boardEntity.getTotalLike() + 1);  // 좋아요 증가
            BoardLikeEntity boardLikeEntity = new BoardLikeEntity();
            boardLikeEntity.setBoardEntity(boardEntity);
            boardLikeEntity.setMemberEntity(memberEntity);
            boardLikeEntity.setFlag(true);  // 좋아요 상태로 설정
            boardLikeRepository.save(boardLikeEntity);
        }

        // 총 좋아요와 싫어요 수가 음수가 되지 않도록 처리
        if (boardEntity.getTotalLike() < 0) {
            boardEntity.setTotalLike(0);
        }
        if (boardEntity.getTotalDislike() < 0) {
            boardEntity.setTotalDislike(0);
        }

        // 결과 반환: 총 좋아요, 총 싫어요, 좋아요 상태 (true/false), 싫어요 상태 (true/false)
        boolean isLiked = existingLike.isPresent() && existingLike.get().isFlag() == true;
        return Arrays.asList(boardEntity.getTotalLike(), boardEntity.getTotalDislike());
    }

    public List<Integer> toggleDislike(Long boardId, Long memberId) {
        BoardEntity boardEntity = boardRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 기존에 좋아요가 눌려 있었다면, 좋아요 취소
        if (boardLikeRepository.findBoardLikeEntity(boardId, memberId).isPresent() &&
                boardLikeRepository.findBoardLikeEntity(boardId, memberId).get().isFlag() == true) {
            boardEntity.setTotalLike(boardEntity.getTotalLike() - 1);  // 좋아요 감소
            boardLikeRepository.deleteBoardLikeEntity(boardId, memberId, true);  // 좋아요 삭제
        }

        // 이미 싫어요가 눌러져 있으면 싫어요 취소
        Optional<BoardLikeEntity> existingDislike = boardLikeRepository.findBoardLikeEntity(boardId, memberId);
        if (existingDislike.isPresent() && existingDislike.get().isFlag() == false) {
            // 싫어요 취소
            boardEntity.setTotalDislike(boardEntity.getTotalDislike() - 1);  // 싫어요 감소
            boardLikeRepository.deleteBoardLikeEntity(boardId, memberId, false);  // 싫어요 삭제
        } else {
            // 싫어요 추가
            boardEntity.setTotalDislike(boardEntity.getTotalDislike() + 1);  // 싫어요 증가
            BoardLikeEntity boardLikeEntity = new BoardLikeEntity();
            boardLikeEntity.setBoardEntity(boardEntity);
            boardLikeEntity.setMemberEntity(memberEntity);
            boardLikeEntity.setFlag(false);  // 싫어요 상태로 설정
            boardLikeRepository.save(boardLikeEntity);
        }

        // 총 좋아요와 싫어요 수가 음수가 되지 않도록 처리
        if (boardEntity.getTotalLike() < 0) {
            boardEntity.setTotalLike(0);
        }
        if (boardEntity.getTotalDislike() < 0) {
            boardEntity.setTotalDislike(0);
        }

        // 결과 반환: 총 좋아요, 총 싫어요, 좋아요 상태 (true/false), 싫어요 상태 (false/true)
        boolean isDisliked = existingDislike.isPresent() && existingDislike.get().isFlag() == false;
        return Arrays.asList(boardEntity.getTotalLike(), boardEntity.getTotalDislike());
    }																															//1 : LIKE 활성화 신호

    
    public String findMemberLike_Dislike(Long boardId, Long memberId) {
    	BoardLikeEntity boardLikeEntity = boardLikeRepository.findBoardLikeEntity(boardId, memberId).get();
    		if(boardLikeEntity.getId() != null &&boardLikeEntity.isFlag() == true) {
    			return "ENABLE_LIKE";
    		}else if (boardLikeEntity.getId() != null &&boardLikeEntity.isFlag() == false)  {
    			return "ENABLE_DISLIKE";
    		}else {				//if (boardLikeEntity.getId() == null) 
    			return "DISABLE";
    		}
    }

}
