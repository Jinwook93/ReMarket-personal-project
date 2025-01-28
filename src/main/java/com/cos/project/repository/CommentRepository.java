package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.CommentEntity;

import jakarta.transaction.Transactional;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity, Long>  {

	@Query("SELECT c FROM CommentEntity c JOIN c.boardEntity b WHERE b.id = :boardId")
	List<CommentEntity> findAllCommentsAboutBoard(@Param("boardId")Long boardId);

//@Query("SELECT c FROM CommentEntity c WHERE c.memberEntity.id = :member_id")		JPQL
//List<CommentEntity> findByMemberID(@Param("member_id")Long member_id);

	@Query(value = "SELECT * FROM comment c WHERE c.member_id = :member_id", nativeQuery = true)		//nativeQuery
	List<CommentEntity> findByMemberID(@Param("member_id")Long member_id);
	
	
	@Modifying
	@Transactional
	@Query("DELETE FROM CommentEntity c WHERE c.boardEntity.id = :boardId")
	void deleteAllCommentsAboutBoard(@Param("boardId") Long boardId);
}
