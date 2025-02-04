package com.cos.project.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.BoardLikeEntity;import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.CommentLikeEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.MessageEntity;
import com.cos.project.repository.BoardLikeRepository;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.CommentLikeRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.repository.MemberRepository;
import com.cos.project.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final BoardLikeRepository boardLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final MessageRepository messageRepository;
    
    @Transactional
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

    @Transactional
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

    @Transactional
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

    
    
    
    ///댓글에 관한 좋아요,싫어요 내용=========================
    
    @Transactional
    public List<Integer> commentToggleLike(Long commentId, Long memberId) {
        CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 기존에 싫어요가 눌려 있었다면, 싫어요 취소
        if (commentLikeRepository.findCommentLikeEntity(commentId, memberId).isPresent() &&
                commentLikeRepository.findCommentLikeEntity(commentId, memberId).get().isFlag() == false) {
            commentEntity.setTotalDislike(commentEntity.getTotalDislike() - 1);  // 싫어요 감소
            commentLikeRepository.deleteCommentLikeEntity(commentId, memberId, false);  // 싫어요 삭제
        }

        // 이미 좋아요가 눌러져 있으면 좋아요 취소
        Optional<CommentLikeEntity> existingLike = commentLikeRepository.findCommentLikeEntity(commentId, memberId);
        if (existingLike.isPresent() && existingLike.get().isFlag() == true) {
            // 좋아요 취소
            commentEntity.setTotalLike(commentEntity.getTotalLike() - 1);  // 좋아요 감소
            commentLikeRepository.deleteCommentLikeEntity(commentId, memberId, true);  // 좋아요 삭제
        } else {
            // 좋아요 추가
            commentEntity.setTotalLike(commentEntity.getTotalLike() + 1);  // 좋아요 증가
            CommentLikeEntity commentLikeEntity = new CommentLikeEntity();
            commentLikeEntity.setCommentEntity(commentEntity);
            commentLikeEntity.setMemberEntity(memberEntity);
            commentLikeEntity.setFlag(true);  // 좋아요 상태로 설정
            commentLikeRepository.save(commentLikeEntity);
        }

        // 총 좋아요와 싫어요 수가 음수가 되지 않도록 처리
        if (commentEntity.getTotalLike() < 0) {
            commentEntity.setTotalLike(0);
        }
        if (commentEntity.getTotalDislike() < 0) {
            commentEntity.setTotalDislike(0);
        }

        // 결과 반환: 총 좋아요, 총 싫어요, 좋아요 상태 (true/false), 싫어요 상태 (true/false)
        boolean isLiked = existingLike.isPresent() && existingLike.get().isFlag() == true;
        return Arrays.asList(commentEntity.getTotalLike(), commentEntity.getTotalDislike());
    }

    @Transactional
    public List<Integer> commentToggleDislike(Long commentId, Long memberId) {
    	CommentEntity commentEntity = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
        MemberEntity memberEntity = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

        // 기존에 좋아요가 눌려 있었다면, 좋아요 취소
        if (commentLikeRepository.findCommentLikeEntity(commentId, memberId).isPresent() &&
                commentLikeRepository.findCommentLikeEntity(commentId, memberId).get().isFlag() == true) {
            commentEntity.setTotalLike(commentEntity.getTotalLike() - 1);  // 좋아요 감소
            commentLikeRepository.deleteCommentLikeEntity(commentId, memberId, true);  // 좋아요 삭제
        }

        // 이미 싫어요가 눌러져 있으면 싫어요 취소
        Optional<CommentLikeEntity> existingDislike = commentLikeRepository.findCommentLikeEntity(commentId, memberId);
        if (existingDislike.isPresent() && existingDislike.get().isFlag() == false) {
            // 싫어요 취소
        	 commentEntity.setTotalDislike( commentEntity.getTotalDislike() - 1);  // 싫어요 감소
            commentLikeRepository.deleteCommentLikeEntity(commentId, memberId, false);  // 싫어요 삭제
        } else {
            // 싫어요 추가
        	commentEntity.setTotalDislike(commentEntity.getTotalDislike() + 1);  // 싫어요 증가
            CommentLikeEntity commentLikeEntity = new CommentLikeEntity();
            commentLikeEntity.setCommentEntity(commentEntity);
            commentLikeEntity.setMemberEntity(memberEntity);
            commentLikeEntity.setFlag(false);  // 싫어요 상태로 설정
            commentLikeRepository.save(commentLikeEntity);
        }

        // 총 좋아요와 싫어요 수가 음수가 되지 않도록 처리
        if (commentEntity.getTotalLike() < 0) {
        	commentEntity.setTotalLike(0);
        }
        if (commentEntity.getTotalDislike() < 0) {
        	commentEntity.setTotalDislike(0);
        }

        // 결과 반환: 총 좋아요, 총 싫어요, 좋아요 상태 (true/false), 싫어요 상태 (false/true)
        boolean isDisliked = existingDislike.isPresent() && existingDislike.get().isFlag() == false;
        return Arrays.asList(commentEntity.getTotalLike(), commentEntity.getTotalDislike());
    }																															//1 : LIKE 활성화 신호

    @Transactional
    public String comment_findMemberLike_Dislike(Long commentId, Long memberId) {
    	CommentLikeEntity commentLikeEntity = commentLikeRepository.findCommentLikeEntity(commentId, memberId).get();
    		if(commentLikeEntity.getId() != null &&commentLikeEntity.isFlag() == true) {
    			return "ENABLE_LIKE";
    		}else if (commentLikeEntity.getId() != null &&commentLikeEntity.isFlag() == false)  {
    			return "ENABLE_DISLIKE";
    		}else {				//if (commentLikeEntity.getId() == null) 
    			return "DISABLE";
    		}
    }

    
    
    
    
    //메시지 좋아요
    @Transactional
    public boolean chatToggleLike(Long messageId, Long id, boolean flag) {
        // 메시지 존재 여부 확인
        MessageEntity messageEntity = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid message ID"));

        // flag와 상관없이 liked 상태를 반전
        messageEntity.setLiked(!messageEntity.isLiked());

        // 변경된 내용 저장
        messageRepository.save(messageEntity);

        // 변경된 liked 값 반환
        return messageEntity.isLiked();
    }


    @Transactional
	//버튼 활성화
	public String chat_findMemberLike_Dislike(Long messageId, Long id) {
		// TODO Auto-generated method stub
		return null;
	}

    
    
    
    
    
    
    
    
    
    
    
}
