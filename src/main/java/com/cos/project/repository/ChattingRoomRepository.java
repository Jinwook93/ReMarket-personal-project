package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.ChattingRoomEntity;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoomEntity, Long> {

	@Query("SELECT c FROM ChattingRoomEntity c WHERE c.member1.id = :loggedId AND c.member2.id = :userId AND c.boardEntity.id = :boardId")
	ChattingRoomEntity findEnableRoom(@Param("loggedId") Long loggedId, @Param("userId")Long userId, @Param("boardId")Long boardId);

	@Query("SELECT c.member2.id FROM ChattingRoomEntity c WHERE c.id=:id AND c.member1.id = :loggedId")	//상대방의 id 검색
	Long findMember2Id(@Param("id") Long id, @Param("loggedId") Long loggedId);

	@Query("SELECT c FROM ChattingRoomEntity c WHERE c.member1.id = :loggedId AND (c.exitedmemberId IS NULL OR c.exitedmemberId != :loggedId)")
	List<ChattingRoomEntity> findAllByLoggedMember(@Param("loggedId") Long loggedId);


//	
//	@Query("SELECT c  FROM ChattingRoomEntity c WHERE c.member2.id = :loggedId AND (c.exitedMemberId IS NULL OR c.exitedMemberId != :loggedId)")	
//	List<ChattingRoomEntity> findAllByLoggedMember2(@Param("loggedId") Long loggedId);
	
	
	@Query("SELECT c FROM ChattingRoomEntity c WHERE c.member2.id = :loggedId AND (c.exitedmemberId IS NULL OR c.exitedmemberId != :loggedId)")
	List<ChattingRoomEntity> findAllByLoggedMember2(@Param("loggedId") Long loggedId);



	
//	@Query("SELECT c  FROM ChattingRoomEntity c WHERE c.member1.id = :loggedId")	
//	List<ChattingRoomEntity> findAllByLoggedId(@Param("loggedId") Long loggedId);
}
