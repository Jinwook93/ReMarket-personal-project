package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long>{

	@Query(value = "SELECT * FROM board b WHERE b.TITLE = %:searchIndex%", nativeQuery = true)
	//@Query("SELECT b FROM BOARDENTITY b WHERE b.TITLE = %:searchIndex%")
	List<BoardEntity> searchResult (@Param("searchIndex") String searchinex);
	
	
	@Query("SELECT b FROM BoardEntity b WHERE b.memberEntity.id = :member_id")	
	List<BoardEntity> findByMemberID(@Param("member_id")Long member_id);
	
}
