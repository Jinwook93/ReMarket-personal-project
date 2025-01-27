package com.cos.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cos.project.entity.ChattingRoomEntity;

@Repository
public interface ChattingRoomRepository extends JpaRepository<ChattingRoomEntity, Long> {

	@Query("SELECT c FROM ChattingRoomEntity c WHERE c.member1.id = :member1Id AND c.member2.id = :member2Id AND c.boardEntity.id = :boardId")
	ChattingRoomEntity findEnableRoom(@Param("member1Id") Long member1Id, @Param("receiverId")Long member2Id, @Param("boardId")Long boardId);

}
