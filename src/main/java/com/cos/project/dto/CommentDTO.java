package com.cos.project.dto;

import java.sql.Timestamp;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentDTO {

    private Long id;
    private String content;
    private Long boardId;
    private Long memberId;
    private String memberName;
    private String memberUserId;
    private String memberProfileImage;
    private int totalLike;
    private int totalDislike;
    private Long parentCommentId;
    private Boolean isPrivate;
    private Boolean isBlind;
    private Timestamp createTime;
    private Boolean updated;
    private Timestamp updateTime; // 채팅방 수정 시간 
    private Timestamp reCreateTime; // 채팅방 수정 시간 (updateTime이 의도치 않게 시간이 바뀌어서, 수정 시의 updateTime을 적용하는 용도)
    // Entity → DTO 변환 메서드
    public static CommentDTO fromEntity(CommentEntity entity) {
        return CommentDTO.builder()
                .id(entity.getId())
                .content(entity.getContent())
                .boardId(entity.getBoardEntity() != null ? entity.getBoardEntity().getId() : null)
                .memberId(entity.getMemberEntity() != null ? entity.getMemberEntity().getId() : null)
                .memberName(entity.getMemberEntity() != null ? entity.getMemberEntity().getName() : null)
                .memberUserId(entity.getMemberEntity() != null ? entity.getMemberEntity().getUserid() : null)
                .memberProfileImage(entity.getMemberEntity() != null ? entity.getMemberEntity().getProfileImage() : null)
                .totalLike(entity.getTotalLike())
                .totalDislike(entity.getTotalDislike())
                .parentCommentId(entity.getParentComment().getId())
                .isPrivate(entity.isPrivate())
                .isBlind(entity.isBlind())
                .createTime(entity.getCreateTime())
                .updated(entity.getUpdated())
                .build();
    }
    
 // DTO → Entity 변환
    public CommentEntity toEntity(BoardEntity boardEntity, MemberEntity memberEntity, CommentEntity commentEntity) {
        return CommentEntity.builder()
                .id(this.id)
                .content(this.content)
                .boardEntity(boardEntity)
                .memberEntity(memberEntity)
                .totalLike(this.totalLike)
                .totalDislike(this.totalDislike)
                .parentComment(commentEntity)
                .Private(this.isPrivate)
                .blind(this.isBlind)
                .createTime(this.createTime != null ? this.createTime : new Timestamp(System.currentTimeMillis()))
                .updated(false)
                .build();
    }
}
