package com.cos.project.dto;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.annotations.UpdateTimestamp;

import com.cos.project.entity.Buy_Sell;
import com.cos.project.entity.Category;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BoardDTO {

    private Long id;                  // 게시글 ID
    private String title;             // 제목
    private String contents;          // 내용
    private long view;                // 조회수
    private Timestamp createTime;     // 생성 시간
    private String boardFiles;        // 첨부 파일 URL
    private String  memberUserId;         // 작성자 정보
    private String memberNickname;
    private List<CommentDTO> comments; // 댓글 리스트
    private Integer price;
    private String address;				//거래 장소
    private Category category;
    private Buy_Sell buy_Sell;
    private String product;
    
    private Timestamp updateTime;  // 채팅방 수정 시간
    private Timestamp reCreateTime; // 채팅방 수정 시간 (updateTime이 의도치 않게 시간이 바뀌어서, 수정 시의 updateTime을 적용하는 용도)
    
    private Boolean deleted;   //삭제 및 숨김처리
    private Boolean updated;   //수정 상태
    
    
}
