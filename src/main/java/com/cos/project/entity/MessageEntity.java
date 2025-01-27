package com.cos.project.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.*;

@Entity
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatting_id")
    private ChattingRoomEntity chattingRoomEntity;  // 메시지가 속한 채팅방

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private MemberEntity sender;  // 메시지를 보낸 사람

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private MemberEntity receiver;  // 메시지를 받은 사람

    private String messageContent;  // 메시지 내용

    @CreationTimestamp
    private Timestamp sendTime;  // 메시지 전송 시간

    // getters and setters
}
