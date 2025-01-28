package com.cos.project.service;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.CommentLikeEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.CommentLikeRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.repository.MemberRepository;

import jakarta.transaction.Transactional;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	private final BoardRepository boardRepository;

	private final MemberRepository memberRepository;
	
	private final CommentLikeRepository commentLikeRepository;

	@Transactional
	public List<CommentEntity> getAllCommentAboutBoard(Long id) {

//		BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> 
//		new IllegalArgumentException("게시글을 찾을 수 없습니다"));

		List<CommentEntity> comments = commentRepository.findAllCommentsAboutBoard(id);

		if (comments == null) {
			return null;
		} else {
			return comments;
		}
	}

	@Transactional
	public CommentEntity addComment(Long id, CommentEntity commentEntity, MemberEntity memberEntity) {

		BoardEntity boardEntity = boardRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

		MemberEntity managedMemberEntity = memberRepository.findById(memberEntity.getId())
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

		commentEntity.setBoardEntity(boardEntity);
		commentEntity.setMemberEntity(managedMemberEntity);
		commentRepository.save(commentEntity);
		System.out.println("저장 완료...");

		return commentEntity;
	}

//@Transactional
//public MemberEntity CommentcheckMember (Long id,  CommentEntity commentEntity,  MemberEntity memberEntity) {
//	
//	BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> 
//	new IllegalArgumentException("게시글을 찾을 수 없습니다"));
//System.out.println("글번호"+boardEntity.getId());
//	MemberEntity managedMemberEntity = memberRepository.findById(memberEntity.getId())
//            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
//	System.out.println("회원정보"+managedMemberEntity .getName());
//	
//
//	commentEntity.setBoardEntity(boardEntity);
//	commentEntity.setMemberEntity(managedMemberEntity);
//	commentRepository.save(commentEntity);
//	System.out.println("저장 완료...");
//	
//	return  commentEntity;
//}

	@Transactional
	public List<CommentEntity> findMyComments(Long id) {
		List<CommentEntity> mycomments = commentRepository.findByMemberID(id);

		return mycomments;

	}

	@Transactional
	public boolean deleteComment(Long id) {
		commentLikeRepository.deleteByCommentId(id);	//댓글에 달린 좋아요,싫어요 삭제
		commentRepository.deleteById(id);
		return true;

	}

	@Transactional
	public boolean updateComment(Long id, CommentEntity commentEntity) {
		CommentEntity comment_before = commentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("댓글 데이터를 찾을 수 없습니다"));

		comment_before.setContent(commentEntity.getContent());

		commentRepository.save(comment_before);

		return true;
	}

	@Transactional
	public void deleteAllCommentAboutBoard(Long id) {

//	BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> 
//	new IllegalArgumentException("게시글을 찾을 수 없습니다"));

		List<CommentEntity> comments = commentRepository.findAllCommentsAboutBoard(id);
			for(CommentEntity comment : comments) {
				commentLikeRepository.deleteByCommentId(comment.getId());				//댓글 관련 좋아요,싫어요 지우기
				commentRepository.deleteById(comment.getId());									// 게시판 댓글 지우기
			}
		//commentRepository.deleteAllCommentsAboutBoard(id);
//		if (comments == null) {
//			return null;
//		} else {
//			return comments;
//		}
	}
}