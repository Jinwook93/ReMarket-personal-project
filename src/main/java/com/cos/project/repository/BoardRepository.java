package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.Category;
import com.cos.project.entity.CommentEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long>{

	@Query(value = "SELECT * FROM board b WHERE b.TITLE LIKE %:search%", nativeQuery = true)
	//@Query("SELECT b FROM BOARDENTITY b WHERE b.TITLE = %:search%")
	List<BoardEntity> searchResult (@Param("search") String search);
	
	
	@Query("SELECT b FROM BoardEntity b WHERE b.memberEntity.id = :member_id")	
	List<BoardEntity> findByMemberID(@Param("member_id")Long member_id);
	
	
	
	
	


	
	
	
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.title LIKE %:search%")	
	List<BoardEntity> findByCategoryAndTitleAndsearch(@Param("category")Category category, @Param("search")String search);
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.contents LIKE %:search%")
	List<BoardEntity> findByCategoryAndContentsAndsearch(@Param("category")Category category,  @Param("search")String search);
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.memberEntity.userid LIKE %:search%")	
	List<BoardEntity>findByCategoryAndUseridAndsearch(@Param("category")Category category, @Param("search")String search);
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.memberEntity.name LIKE %:search%")	
	List<BoardEntity> findByCategoryAndNameAndsearch(@Param("category")Category category,  @Param("search")String search);
	
}
