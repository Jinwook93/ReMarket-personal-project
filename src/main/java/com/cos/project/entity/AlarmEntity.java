package com.cos.project.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;

import com.cos.project.dto.AlarmDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = true, columnDefinition = "TEXT")
    String member1Content;

    @Column(nullable = true, columnDefinition = "TEXT")
    String member2Content;

    Boolean member1Visible = true; // 삭제보다는 숨김 처리
    Boolean member2Visible = true; // 삭제보다는 숨김 처리

    @CreationTimestamp
    @Column(updatable = false)
    private Timestamp createTime;

    // member1 and member2 are ManyToOne relationships with MemberEntity
    @ManyToOne
    @JoinColumn(name = "member1_id")
    MemberEntity member1; // 로그인한 유저

    @ManyToOne
    @JoinColumn(name = "member2_id", nullable = true)
    MemberEntity member2; // 상대방

    @Column(nullable = false)
    String type; // 알람 타입 (게시판, 댓글, 메시지, 좋아요 등)

    @Column(nullable = true)
    String childType; // 세부 타입 (게시판의 댓글, 댓글의 게시판 등)
    
    @Column(nullable = true)
    String object ; // 대상의 목적어 (좋아요, 싫어요 등)
    
    @Column(nullable = true)
    String action ; // 실제 행동 (좋아요, 댓글 등)

    @Column(nullable = false)
    String member1Read = "UNREAD"; // 알람 상태 (READ, UNREAD 등)
    
    @Column(nullable = false)
    String member2Read = "UNREAD"; // 알람 상태 (READ, UNREAD 등)

    @Column(nullable = false)
    String priority = "MEDIUM"; // 우선순위 (LOW, MEDIUM, HIGH)

//    @Column(nullable = false)
//    String targetId; // 알람 대상 ID (로그인 유저, 관리자 등)

    // You can add additional fields for member1Id and member2Id if you need them to be separately stored




    public AlarmDTO toDTO() {
        return new AlarmDTO(
                this.getId(),
                this.getMember1Content(),
                this.getMember2Content(),
                this.getMember1() != null ? this.getMember1().getId() : null,
                this.getMember2() != null ? this.getMember2().getId() : null,
                this.getMember1Visible(),
                this.getMember2Visible(),
                this.getCreateTime(),
                this.getType(),
                this.getChildType(),
                this.getObject(),
                this.getAction(),
                this.getPriority(),
                this.getMember1Read(),
                this.getMember2Read()
//                ,this.getTargetId()
        );
    }







}
