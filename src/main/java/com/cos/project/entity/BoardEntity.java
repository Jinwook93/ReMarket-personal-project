package com.cos.project.entity;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

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

    
    @Enumerated(EnumType.STRING)
    private Category category;
    
	private int totalLike= 0;
	private int totalDislike=0;
    
    @Column(columnDefinition = "LONGTEXT") 
    private String boardFiles;	// 첨부할 URL 추가
    
 
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
//    @JsonBackReference("member-boards")
    @JsonIncludeProperties({"id","name","profileImage"})
    private MemberEntity memberEntity; 
    
    @OneToMany(mappedBy = "boardEntity", cascade = CascadeType.ALL, orphanRemoval = true)
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
    
    
    
    
    
    
    
}
