package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>{

    @Query("SELECT m FROM MessageEntity m WHERE m.chattingRoomEntity.id = :roomId")
    List<MessageEntity> findByChattingRoomEntity(@Param("roomId") Long roomId);

}
