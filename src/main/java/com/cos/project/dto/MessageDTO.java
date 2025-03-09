package com.cos.project.dto;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;               // 메시지 ID
    private Long roomId;           // 채팅방 ID (ChattingRoomEntity 참조 대신)
    private String senderUserId;   // 보낸 사람 ID
    private String receiverUserId; // 받은 사람 ID
    private String messageContent; // 메시지 내용
    private boolean liked;	// 메시지 좋아요 활성화 상태
    private boolean isRead;			//읽음 표시
    private boolean exited;				//나갔다 들어온 상태 확인
    private Long exitedSenderId;	//대화방을 나간 유저의 Id(식별번호)
    private boolean deleted;
    private Long parentMessageId;
    private String profileImageUrl1;
    private String profileImageUrl2;
    private Boolean alarmType;
    private Boolean statusBar;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private Timestamp sendTime;    // 메시지 전송 시간
	
    
    
    public MessageDTO(Long id, String senderUserId, String messageContent, Boolean statusBar,Timestamp sendTime) {
		super();
		this.id = id;
		this.senderUserId = senderUserId;
		this.messageContent = messageContent;
		this.statusBar = statusBar;
		this.sendTime = sendTime;
	}

    
    
    
    
    
    
    
    
    
    
}
