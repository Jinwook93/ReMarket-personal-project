package com.cos.project.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.Category;
import com.cos.project.entity.CommentEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long>{

	@Query(value = "SELECT * FROM board b WHERE b.title LIKE %:search%", nativeQuery = true)
	//@Query("SELECT b FROM BOARDENTITY b WHERE b.TITLE = %:search%")
	Optional<List<BoardEntity>> searchResult (@Param("search") String search);
	
	
	@Query("SELECT b FROM BoardEntity b WHERE b.memberEntity.id = :member_id")	
	List<BoardEntity> findByMemberID(@Param("member_id")Long member_id);
	
	

	@Query(value = "SELECT * FROM board b WHERE b.contents LIKE %:search%", nativeQuery = true)
	//@Query("SELECT b FROM BOARDENTITY b WHERE b.TITLE = %:search%")
	Optional<List<BoardEntity>> searchContentsResult (@Param("search") String search);
	
	

	@Query(value = "SELECT b.* FROM board b JOIN member m ON b.member_id = m.id WHERE m.userid LIKE %:search%", nativeQuery = true)
	Optional<List<BoardEntity>> searchMemberUseridResult(@Param("search") String search);

	@Query(value = "SELECT b.* FROM board b JOIN member m ON b.member_id = m.id WHERE m.name LIKE %:search%", nativeQuery = true)
	Optional<List<BoardEntity>> searchMembernameResult(@Param("search") String search);
	
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.title LIKE %:search%")	
	Optional<List<BoardEntity>> findByCategoryAndTitleAndsearch(@Param("category")Category category, @Param("search")String search);
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.contents LIKE %:search%")
	Optional<List<BoardEntity>>findByCategoryAndContentsAndsearch(@Param("category")Category category,  @Param("search")String search);
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.memberEntity.userid LIKE %:search%")	
	Optional<List<BoardEntity>>findByCategoryAndUseridAndsearch(@Param("category")Category category, @Param("search")String search);
	@Query("SELECT b FROM BoardEntity b WHERE b.category = :category AND b.memberEntity.name LIKE %:search%")	
	Optional<List<BoardEntity>> findByCategoryAndNameAndsearch(@Param("category")Category category,  @Param("search")String search);
	
}
