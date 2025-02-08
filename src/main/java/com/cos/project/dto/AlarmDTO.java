package com.cos.project.dto;

import java.sql.Timestamp;
import java.util.Optional;

import com.cos.project.entity.AlarmEntity;
import com.cos.project.entity.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {
	 private Long id; // 알람 ID
	    private String member1Content; // 로그인한 유저에게 보여줄 알람 내용
	    private String member2Content; // 상대방에게 보여줄 알람 내용
	    private Long member1Id;
	    private Long member2Id;
	    private boolean member1Visible; // 로그인한 유저가 볼 수 있는 알람 여부
	    private boolean member2Visible; // 상대방이 볼 수 있는 알람 여부
	    private Timestamp createTime; // 알람 생성 시간
	    private String type; // 알람 타입 (게시판, 댓글, 메시지 등)
	    private String childType; // 세부 타입 (게시판의 댓글, 댓글의 게시판 등)
	    private String object; // 대상의 목적어 (좋아요, 싫어요 등)
	    private String action; // 실제 행동 (좋아요, 댓글 등)
	    private String status; // 알람 상태 (READ, UNREAD 등)
	    private String priority; // 우선순위 (LOW, MEDIUM, HIGH)
	    private String targetId; // 알람 대상 ID (로그인 유저, 관리자 등)




	 // DTO -> Entity 변환 메서드
	    public AlarmEntity toEntity(MemberEntity member1, MemberEntity member2) {
	        return AlarmEntity.builder()
	                .id(this.id)
	                .member1Content(this.member1Content)
	                .member2Content(this.member2Content)
	                .member1Visible(this.member1Visible)
	                .member2Visible(this.member2Visible)
	                .type(this.type)
	                .childType(this.childType)
	                .object(this.object)
	                .action(this.action)
	                .priority(this.priority)
	                .member1Read(this.equals("UNREAD") ? "UNREAD" : "READ") // member1Read 설정
	                .member2Read(this.equals("UNREAD") ? "UNREAD" : "READ") // member2Read 설정
//	                .targetId(this.targetId)
	                .member1(member1)
	                .member2(member2)
	                .build();
	    }
}