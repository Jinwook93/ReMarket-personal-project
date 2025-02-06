package com.cos.project.dto;

import java.sql.Timestamp;
import java.util.Set;

import com.cos.project.entity.MessageEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ChattingRoomDTO {
    private Long id;
    private String title;
    private int price;
    private Timestamp createTime;
    private boolean liked;
    
//    private MemberDTO member1;
//    private MemberDTO member2;
//    private BoardDTO board;
    
    
    private String member1UserId;
    private String member2UserId;
    private String boardId;
    private Long exitedmemberId;
    private Long messageIndex1;		//채팅방 재접속시 렌더링 할지에 대한 유무		
    private Long messageIndex2;		//채팅방 재접속시 렌더링 할지에 대한 유무		
    private Long recentExitedmemberId;			//최근 나간 유저ID 정보	(채팅방을 나갈 때에만 ID가 갱신됨
    
    private Set<MessageDTO> messages;
}