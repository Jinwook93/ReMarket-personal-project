package com.cos.project.entity;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonView;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "comment")
public class CommentEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;
	
	String content;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "board_id")
	@JsonBackReference("board-comments")
	private BoardEntity boardEntity;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "member_id")
	@JsonIncludeProperties({"id", "name", "userid", "profileImage", "nickname"})
	private MemberEntity memberEntity;

	
	private int totalLike=0;
	private int totalDislike=0;
	
	
//	@Column(nullable = true)
//	private Long parentCommentId;			//부모 댓글 아이디(ID) 
	
	private boolean Private;			//비밀댓글		(!!!!!!!!! private로 인식한다)
	
	private boolean blind;			//비공개댓글
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "parent_comment_id", nullable = true)
	@JsonIncludeProperties({"id","memberEntity","content"}) // parentComment의 id와 memberEntity를 포함
	private CommentEntity parentComment;
	
	
//	@ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "parent_comment_id",nullable = true)
//	@JsonBackReference  // 부모 댓글이 자식 댓글을 직렬화하지 않도록
//    private CommentEntity parentComment; // 부모 댓글

    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL)
    @JsonManagedReference  // 자식 댓글에서 부모 댓글을 직렬화할 수 있도록
    private List<CommentEntity> childComments = new ArrayList<>(); // 자식 댓글들
	
	
	
	
	@CreationTimestamp
	Timestamp createTime;
}
