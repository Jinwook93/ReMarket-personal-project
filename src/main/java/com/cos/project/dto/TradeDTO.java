package com.cos.project.dto;

import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.TradeEntity;
import com.cos.project.entity.TradeStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TradeDTO {

    private Long id;
    private Long member1Id;  // member1의 id
    private Long member2Id;  // member2의 id
    private Long boardEntityId;  // boardEntity의 id
    private Boolean accept1;
    private Boolean accept2;
    private Boolean completed1;
    private Boolean completed2;
    private String tradeStatus;  // TradeStatus의 Enum 값을 String으로 받음
    private Boolean booking1;
    private Boolean booking2;

    public TradeDTO(Long id, Long member1Id, Long member2Id, Long boardEntityId, 
                    Boolean accept1, Boolean accept2, Boolean completed1, 
                    Boolean completed2, String tradeStatus) {
        this.id = id;
        this.member1Id = member1Id;
        this.member2Id = member2Id;
        this.boardEntityId = boardEntityId;
        this.accept1 = accept1;
        this.accept2 = accept2;
        this.completed1 = completed1;
        this.completed2 = completed2;
        this.tradeStatus = tradeStatus;
    }

    // TradeEntity -> TradeDTO 변환 메소드
    public static TradeDTO fromEntity(TradeEntity tradeEntity) {
        return new TradeDTO(
                tradeEntity.getId(),
                tradeEntity.getMember1() != null ? tradeEntity.getMember1().getId() : null,
                tradeEntity.getMember2() != null ? tradeEntity.getMember2().getId() : null,
                tradeEntity.getBoardEntity() != null ? tradeEntity.getBoardEntity().getId() : null,
                tradeEntity.getAccept1(),
                tradeEntity.getAccept2(),
                tradeEntity.getCompleted1(),
                tradeEntity.getCompleted2(),
                tradeEntity.getTradeStatus() != null ? tradeEntity.getTradeStatus().name() : null
        );
    }

    // TradeDTO -> TradeEntity 변환 메소드
    public TradeEntity toEntity(MemberEntity member1, MemberEntity member2, BoardEntity board) {
        TradeEntity tradeEntity = new TradeEntity();
        tradeEntity.setId(this.id);
        // member1, member2, boardEntity는 ID 기반으로 세팅할 수 있음
         tradeEntity.setMember1(member1);
         tradeEntity.setMember2(member2);
         tradeEntity.setBoardEntity(board);
        tradeEntity.setAccept1(this.accept1);
        tradeEntity.setAccept2(this.accept2);
        tradeEntity.setCompleted1(this.completed1);
        tradeEntity.setCompleted2(this.completed2);
        tradeEntity.setTradeStatus(this.tradeStatus != null ? TradeStatus.valueOf(this.tradeStatus) : null);
        return tradeEntity;
    }
}
