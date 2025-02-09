package com.cos.project.entity;

import java.sql.Timestamp;

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
	@JsonIncludeProperties({"id", "name", "userid", "profileImage"})
	private MemberEntity memberEntity;

	
	private int totalLike=0;
	private int totalDislike=0;
	
	
	@Column(nullable = true)
	private Long parentCommentId;			//부모 댓글 아이디(ID) 
	
	private boolean PRIVATE =false;			//비밀댓글
	
	private boolean blind =false;			//비공개댓글
	
	
	@CreationTimestamp
	Timestamp createTime;
}
