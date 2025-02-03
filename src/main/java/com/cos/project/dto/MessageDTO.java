package com.cos.project.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;               // 메시지 ID
    private Long roomId;           // 채팅방 ID (ChattingRoomEntity 참조 대신)
    private String senderUserId;   // 보낸 사람 ID
    private String receiverUserId; // 받은 사람 ID
    private String messageContent; // 메시지 내용
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp sendTime;    // 메시지 전송 시간
}
