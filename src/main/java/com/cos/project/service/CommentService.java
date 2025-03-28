package com.cos.project.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.CommentDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.CommentLikeEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.repository.BoardRepository;
import com.cos.project.repository.CommentLikeRepository;
import com.cos.project.repository.CommentRepository;
import com.cos.project.repository.MemberRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
	
	@PersistenceContext
	private final EntityManager entityManager;

//	@Transactional
//	public List<CommentEntity> getAllCommentAboutBoard(Long id) {				//모든 댓글
//
////		BoardEntity boardEntity = boardRepository.findById(id).orElseThrow(() -> 
////		new IllegalArgumentException("게시글을 찾을 수 없습니다"));
//
//		List<CommentEntity> comments = commentRepository.findAllCommentsAboutBoard(id);
//
//		List<CommentEntity> childComments = commentRepository.findAllCommentsAboutBoard(id);
//		
//		if (comments == null) {
//			return null;
//		} else {
//			return comments;
//		}
//	}
	
	
	@Transactional
	public List<CommentEntity> getAllCommentAboutBoard(Long boardid) {				//parentCommentId==NULL인 모든 댓글

		List<CommentEntity> comments = commentRepository.findByBoardIdAndParentCommentIsNull(boardid);

		if (comments == null) {
			return null;
		} else {
			return comments;
		}
	}
	
	
//	@Transactional
//	public List<CommentEntity> getFilteredComments(Long boardId, String loggedInUserId, Boolean childFlag) {
//	    List<CommentEntity> comments = boardRepository.findById(boardId)
//	                                                   .get()
//	                                                   .getComments();
//
//	    return comments.stream()
//	            .filter(comment -> comment.getParentComment() == null) // Only consider parent comments
//	            .map(comment -> {
//	                // Check if the logged-in user is the owner of the comment, board, or parent comment
//	                boolean isOwner = comment.getMemberEntity().getUserid().equals(loggedInUserId);
//	                boolean isBoardOwner = comment.getBoardEntity().getMemberEntity().getUserid().equals(loggedInUserId);
//	                boolean isParentOwner = (comment.getParentComment() != null) && 
//	                                        comment.getParentComment().getMemberEntity().getUserid().equals(loggedInUserId);
//
//	                // Determine if the user has permission to view the comment
//	                boolean canView = !comment.isPrivate() || isOwner || isBoardOwner || isParentOwner;
//
//	                // Create a new CommentEntity with the filtered content
//	                return new CommentEntity(
//	                    comment.getId(),
//	                    canView ? comment.getContent() : "🔐 비밀 댓글입니다", // Show "🔐 비밀 댓글입니다" if the user cannot view
//	                    comment.getBoardEntity(),
//	                    comment.getMemberEntity(),
//	                    comment.getTotalLike(),
//	                    comment.getTotalDislike(),
//	                    comment.isPrivate(),
//	                    comment.isBlind(),
//	                    comment.getParentComment(),
//	                    comment.getChildComments(),
//	                    comment.getCreateTime()
//	                );
//	            })
//	            .collect(Collectors.toList());
//	}



	
	
	
	
	
	
	@Transactional
	public List<CommentEntity> getChildComment(Long parentCommentId) { 
	    List<CommentEntity> comments = commentRepository.findByParentCommentId(parentCommentId);

	    // 자식 댓글을 ID 기준으로 역순 정렬 (최신순)
	    return comments.stream()
	            .sorted(Comparator.comparing(CommentEntity::getId).reversed())  
	            .collect(Collectors.toList());
	}
	
	
	
	
	
	

	@Transactional
	public CommentEntity addComment(CommentDTO commentDTO, MemberEntity memberEntity) {
		
//		System.out.println("게시자 개인 댓글 테스트 : "+ commentDTO.getIsPrivate());
		BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId())
				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));

		MemberEntity managedMemberEntity = memberRepository.findById(memberEntity.getId())
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));

		CommentEntity parentComment = null;
		if(commentDTO.getParentCommentId() != null) {
		 parentComment = commentRepository.findById(commentDTO.getParentCommentId()).orElse(null);
		}
		
		CommentEntity commentEntity = commentDTO.toEntity(boardEntity, managedMemberEntity, parentComment);
//		commentDTO.setBoardEntity(boardEntity);
//		commentDTO.setMemberEntity(managedMemberEntity);
		commentRepository.save(commentEntity);
//		System.out.println("저장 완료...");

		return commentEntity;
	}
	
	
//	@Transactional
//	public CommentEntity addChildComment( CommentDTO commentDTO, MemberEntity memberEntity) {
//
//		
//		BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardId())
//				.orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다"));
//
//		MemberEntity managedMemberEntity = memberRepository.findById(memberEntity.getId())		//댓글을 작성한 사용자
//				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다"));
//
////		if(commentDTO.getParentCommentId() != null) {
////			commentEntity.setParentCommentId(commentDTO.getParentCommentId());
////		}
//		CommentEntity parentComment = commentRepository.findById(commentDTO.getParentCommentId()).orElseThrow(() -> new IllegalArgumentException("부모 댓글이 없습니다"));
//		
//		
//		CommentEntity commentEntity = commentDTO.toEntity(boardEntity, memberEntity, parentComment);
////		commentDTO.setBoardEntity(boardEntity);
////		commentDTO.setMemberEntity(managedMemberEntity);
//		commentRepository.save(commentEntity);
//		System.out.println("저장 완료...");
//
//		return commentEntity;
//	}
	
	
	
	
	
	
	
	
	
	

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
	public List<Long> deleteComment(Long id) {
		
		  // 1. 댓글 엔터티 조회
	    CommentEntity parentComment = commentRepository.findById(id)
	        .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));

	    // 2. 자식 댓글 목록 조회
	    List<CommentEntity> childComments = parentComment.getChildComments();

	    // 3. 자식 댓글 ID 리스트 추출
	    List<Long> childCommentIds = childComments.stream()
	        .map(CommentEntity::getId)
	        .collect(Collectors.toList());

	    // 4. 자식 댓글의 좋아요/싫어요 삭제
	    if (!childCommentIds.isEmpty()) {
	        commentLikeRepository.deleteAllByCommentIdIn(childCommentIds);
	    }

	    // 5. 부모 댓글의 좋아요/싫어요 삭제
	    commentLikeRepository.deleteByCommentId(id);

	    // 6. 자식 댓글 삭제
	    commentRepository.deleteAll(childComments);

	    // 7. 부모 댓글 삭제
	    commentRepository.deleteById(id);
		
		
		
		
//		commentLikeRepository.deleteByCommentId(id);	//댓글에 달린 좋아요,싫어요 삭제
//		commentRepository.deleteById(id);
		return childCommentIds;

	}

	@Transactional
	public Boolean updateComment(Long id, CommentDTO commentDTO) {
		CommentEntity comment = commentRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("댓글 데이터를 찾을 수 없습니다"));

		comment.setContent(commentDTO.getContent());
		comment.setPrivate(commentDTO.getIsPrivate());
		if(comment.getUpdated().equals(Boolean.FALSE)) {
		comment.setUpdated(Boolean.TRUE);
		}
		  // 댓글 저장
	    commentRepository.save(comment);

	    // 즉시 DB에 반영
	    commentRepository.flush();  // 변경사항을 DB에 즉시 반영

	    // 저장된 후에 updateTime을 기반으로 reCreateTime 설정
	    comment.setReCreateTime(comment.getUpdateTime());  // updateTime을 reCreateTime에 설정

	    // 변경 사항을 DB에 즉시 반영
	    commentRepository.flush();

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
	
	
	

	
	@Transactional			//부모 댓글에 대한 대댓글 조회
	public List<CommentEntity> childCommentList(Long parentCommentId, Long loggedId, Long boardId, Long member2Id) {
		MemberEntity member1 = boardRepository.findById(boardId).get().getMemberEntity();
		MemberEntity member2 = boardRepository.findById(member2Id).get().getMemberEntity();
		List<CommentEntity> childCommentList = commentRepository.findByParentCommentId(parentCommentId);
		return childCommentList != null?childCommentList:Collections.EMPTY_LIST;
	}
	
	@Transactional 		//대댓글 설정 (자식 댓글 기입)
	public Long addChildComment(Long commentId, Long parentCommentId, Long loggedId, Long boardId, Long member2Id) {
		CommentEntity comment = commentRepository.findById(commentId).get();			//자식
		CommentEntity parentComment = commentRepository.findById(parentCommentId).orElse(null);		
		comment.setParentComment(parentComment);
		return comment.getParentComment().getId();
	}

	
	@Transactional 		//댓글 블라인드
	public Boolean blindComment(Long commentId, Boolean isBlind) {
		CommentEntity comment = commentRepository.findById(commentId).get();	
		comment.setBlind(!isBlind);
		commentRepository.saveAndFlush(comment);
		return comment.isBlind();
	}
	
	
	@Transactional	//부모 댓글 ID찾기
	public CommentEntity findParentComment(Long commentId) {
		CommentEntity child = commentRepository.findById(commentId).orElse(null);	
	
		return child.getParentComment();
	}
	

	
	
}