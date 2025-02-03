package com.cos.project.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class ChattingRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;    // 채팅 방 제목

    private int price;       // 가격 (옵션, 필요 없으면 제거)

//    private String content;  // 채팅방 내용

    @ManyToOne
    @JoinColumn(name = "member1_id")
    @JsonIncludeProperties({"id","userid", "profileImage","name"})
    private MemberEntity member1;

    @ManyToOne
    @JoinColumn(name = "member2_id")
    @JsonIncludeProperties({"id","userid", "profileImage","name"})
    private MemberEntity member2;

  
    
    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIncludeProperties({"id","title", "price"})
    private BoardEntity boardEntity;  // 게시판과 연결

    @CreationTimestamp
    private Timestamp createTime;  // 채팅방 생성 시간

    private boolean liked;  // 좋아요 (개인적으로 메시지나 채팅방에 좋아요 기능 추가 가능)
    														//MySQL 예약어와 겹치므로 like로 생성 불가
    @OneToMany(mappedBy = "chattingRoomEntity", cascade = CascadeType.ALL)
    private Set<MessageEntity> messages = new HashSet<>();  // 메시지들

    // getters and setters
}
