package com.cos.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cos.project.entity.CommentLikeEntity;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLikeEntity, Long>{

	 @Query("SELECT c FROM CommentLikeEntity c WHERE c.commentEntity.id = :comment_id AND c.memberEntity.id = :member_id")
	    Optional<CommentLikeEntity> findCommentLikeEntity(@Param("comment_id") Long comment_id, @Param("member_id") Long member_id);

	    @Modifying
	    @Transactional
	    @Query("DELETE FROM CommentLikeEntity c WHERE c.commentEntity.id = :comment_id AND c.memberEntity.id = :member_id AND c.flag = :flag")
	    void deleteCommentLikeEntity(@Param("comment_id") Long comment_id, @Param("member_id") Long member_id, @Param("flag") boolean flag);

	    @Query("SELECT c FROM CommentLikeEntity c WHERE c.commentEntity.id = :comment_id AND c.flag = :flag")
	    List<CommentLikeEntity> findByCommentId(@Param("comment_id") Long comment_id, @Param("flag") boolean flag);
}
