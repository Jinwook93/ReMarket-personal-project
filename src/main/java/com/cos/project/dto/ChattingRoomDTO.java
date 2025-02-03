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
    
    
    
    private Set<MessageDTO> messages;
}