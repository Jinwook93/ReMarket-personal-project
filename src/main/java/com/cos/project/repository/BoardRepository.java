package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.BoardEntity;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long>{

	@Query(value = "SELECT b FROM BOARD b WHERE b.TITLE = %:searchIndex%", nativeQuery = true)
	//@Query("SELECT b FROM BOARDENTITY b WHERE b.TITLE = %:searchIndex%")
	List<BoardEntity> searchResult (@Param("searchIndex") String searchinex);
	
}
