package com.cos.project.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

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
    @JsonIncludeProperties({"id","userid", "profileImage","name"})
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
    
    private String messageContent;  // 메시지 내용
    
    
    private boolean deleted = false;
    
    
//    private boolean exitMemberRendering = true;		//채팅방 재접속시 렌더링 할지에 대한 유무			
    

    @CreationTimestamp
    private Timestamp sendTime;  // 메시지 전송 시간




    
    
    

    // getters and setters
}
