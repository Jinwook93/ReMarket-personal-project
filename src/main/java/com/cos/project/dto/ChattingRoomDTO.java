package com.cos.project.dto;

import java.sql.Timestamp;
import java.util.Set;

import org.hibernate.annotations.UpdateTimestamp;

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
    private String member1Nickname;
    private String member2Nickname;
    private String boardId;
    private Long exitedmemberId;
    private Long messageIndex1;		//채팅방 재접속시 렌더링 할지에 대한 유무		
    private Long messageIndex2;		//채팅방 재접속시 렌더링 할지에 대한 유무		
    private Long recentExitedmemberId;			//최근 나간 유저ID 정보	(채팅방을 나갈 때에만 ID가 갱신됨
    
    private Boolean member1Visible; //채팅방이 '최초' 만들어졌을 경우 볼 수 있는 권한
    private Boolean member2Visible;		//채팅방이 '최초' 만들어졌을 경우 볼 수 있는 권한
    
    private Timestamp reCreateTime; // 채팅방 나가기, 재입장 시에 대한 수정 시간
    
    private Set<MessageDTO> messages;
}