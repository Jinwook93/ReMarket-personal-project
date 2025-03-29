package com.cos.project.entity;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cos.project.repository.BoardLikeRepository;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "board")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;


    @Column(nullable = false, columnDefinition = "TEXT")
    private String contents;

    
    private long view=0;
    
    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createTime;

    @UpdateTimestamp
    private Timestamp updateTime;  // 채팅방 수정 시간
    
    @Column(nullable = true)
    private Timestamp reCreateTime; // 채팅방 수정 시간 (updateTime이 의도치 않게 시간이 바뀌어서, 수정 시의 updateTime을 적용하는 용도)
    
    
    @Enumerated(EnumType.STRING)
    private Category category;
    
	private int totalLike= 0;
	private int totalDislike=0;
    
    @Column(columnDefinition = "LONGTEXT") 
    private String boardFiles;	// 첨부할 URL 추가
    
 
    @Enumerated(EnumType.STRING)
    private Buy_Sell buy_Sell;
    
    private String product;
    
    
    private int price;
    
    @Column(nullable = true)
    private String address;			//거래 장소
    
    @Column(nullable = true)			//삭제 아닌 숨김 처리
    private Boolean deleted =false;
    
    @Column(nullable = true)			// 게시글 수정 상태
    private Boolean updated =false;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
//    @JsonBackReference("member-boards")
    @JsonIncludeProperties({"id", "userid","name","profileImage","nickname"})
    private MemberEntity memberEntity; 
    
    @OneToMany(mappedBy = "boardEntity", cascade =  CascadeType.REMOVE, orphanRemoval = true)
    @JsonManagedReference("board-comments")
    private List<CommentEntity> comments = new ArrayList<>();
    
    
//    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference("board-likeboard")
//    private List<BoardLikeEntity> boardLikeEntities;
//    
//    
//    @OneToMany(mappedBy = "commentEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference("board-likecomment")
//    private List<CommentLikeEntity> commentLikeEntities;
    
    
    
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIncludeProperties({"id", "booking1", "booking2", "accept1", "accept2", "completed1", "completed2", "tradeStatus", "member1", "member2"})
    private List<TradeEntity> trades = new ArrayList<>();
    
    
    
}
