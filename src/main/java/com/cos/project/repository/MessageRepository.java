package com.cos.project.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.cos.project.entity.ChattingRoomEntity;
import com.cos.project.entity.MessageEntity;

public interface MessageRepository extends JpaRepository<MessageEntity, Long>{

    @Query("SELECT m FROM MessageEntity m WHERE m.chattingRoomEntity.id = :roomId")
    List<MessageEntity> findByChattingRoomEntity(@Param("roomId") Long roomId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MessageEntity m WHERE m.chattingRoomEntity.id = :roomid AND m.sender.id = :sender AND m.receiver.id = :receiver")
    void deleteAllByRoomAndSenderAndReceiver(
        @Param("roomid") Long roomid, 
        @Param("sender") Long sender, 
        @Param("receiver") Long receiver
    );

}
