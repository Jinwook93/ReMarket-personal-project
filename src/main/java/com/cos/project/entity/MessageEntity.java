package com.cos.project.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import com.cos.project.dto.MessageDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@Entity
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "chatting_id")
    private ChattingRoomEntity chattingRoomEntity;  // 메시지가 속한 채팅방

    @ManyToOne
    @JoinColumn(name = "sender_id")
    @JsonIncludeProperties({"id","userid", "profileImage","name","nickname"})
    private MemberEntity sender;  // 메시지를 보낸 사람

    @ManyToOne
    @JoinColumn(name = "receiver_id")
   // @JsonIncludeProperties({"id","userid", "profileImage","name"})
    @JsonIgnore
    private MemberEntity receiver;  // 메시지를 받은 사람

    
    
    private boolean liked = false;
    
    
    private boolean isRead = false;
    
    
    private boolean exited = false;				//나갔다 들어온 상태 확인
    private Long exitedSenderId;	//대화방을 나간 유저의 Id(식별번호)
    
    @Column(columnDefinition = "TEXT") // 긴 문자열 저장 가능
    private String messageContent;  // 메시지 내용
    
    
    private boolean deleted = false;
    
    
    @Column(nullable = true)
    private Long parentMessageId;
    
//    private boolean exitMemberRendering = true;		//채팅방 재접속시 렌더링 할지에 대한 유무			
    
    private Boolean alarmType = false;
    
    
    private Boolean statusBar = null;
    
    @CreationTimestamp
    private Timestamp sendTime;  // 메시지 전송 시간

    @Column(nullable = true)
    private Boolean expired;  // messageButtonSelect 내의 버튼들의 기능을 비활성화 시킬 것



    public MessageDTO convertToDTO(MessageEntity message) {
        return new MessageDTO(
            message.getId(),
            message.getSender().getProfileImage(),
            message.getSender().getUserid(),
            message.getSender().getNickname(),
            message.getMessageContent(),
            message.getChattingRoomEntity().getId(),
            message.getStatusBar(),
            message.getSendTime(),
            message.getExpired(),
            message.getAlarmType()
            
        );
    }
    
 // Assuming MessageDTO is a class you have created to represent a message in a simpler form
//    public MessageDTO convertToDTO(MessageEntity message) {
//        if (message == null) {
//            return null;
//        }
//
//        // Assuming MessageDTO has a constructor that takes the necessary fields of MessageEntity
//        MessageDTO messageDTO = new MessageDTO();
//        messageDTO.setId(message.getId());
//        messageDTO.setContent(message.getContent());
//        messageDTO.setSendTime(message.getSendTime());
//        messageDTO.setSender(message.getSender()); // Assuming sender is a property of MessageEntity
//
//        // Add any additional fields from MessageEntity to MessageDTO if needed
//
//        return messageDTO;
//    }

    
    

    // getters and setters
}
