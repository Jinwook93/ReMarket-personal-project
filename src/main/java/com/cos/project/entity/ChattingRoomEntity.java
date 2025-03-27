package com.cos.project.entity;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
    @JsonIncludeProperties({"id","userid", "profileImage","name","nickname"})
    private MemberEntity member1;

    @ManyToOne
    @JoinColumn(name = "member2_id")
    @JsonIncludeProperties({"id","userid", "profileImage","name","nickname"})
    private MemberEntity member2;

  private Long exitedmemberId;		// 채팅방에 들어갈 시 null로 설정 됨
  private Long messageIndex1 ;		//채팅방 재접속시 렌더링 할지에 대한 유무	
  private Long messageIndex2;		//채팅방 재접속시 렌더링 할지에 대한 유무
  private Long recentExitedmemberId;			//최근 나간 유저ID 정보	(채팅방을 나갈 때에만 ID가 갱신됨
  
  private Boolean member1Visible;		//채팅방이 '최초' 만들어졌을 경우 볼 수 있는 권한
  private Boolean member2Visible;			//채팅방이 '최초' 만들어졌을 경우 볼 수 있는 권한
  
    @ManyToOne
    @JoinColumn(name = "board_id")
    @JsonIncludeProperties({"id","title", "price"})
    private BoardEntity boardEntity;  // 게시판과 연결

    @CreationTimestamp
    private Timestamp createTime;  // 채팅방 생성 시간
    
    @UpdateTimestamp
    private Timestamp updateTime;  // 채팅방 수정 시간
    
    @Column(nullable = true)
    private Timestamp reCreateTime1; // member1의 채팅방 재입장 시에 대한 수정 시간
    @Column(nullable = true)
    private Timestamp reCreateTime2; // member1의 채팅방 재입장 시에 대한 수정 시간

    private boolean liked;  // 좋아요 (개인적으로 메시지나 채팅방에 좋아요 기능 추가 가능)
    														//MySQL 예약어와 겹치므로 like로 생성 불가
    @OneToMany(mappedBy = "chattingRoomEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<MessageEntity> messages = new HashSet<>();  // 메시지들

    // getters and setters
}
