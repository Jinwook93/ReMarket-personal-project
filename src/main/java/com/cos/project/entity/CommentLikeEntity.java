//package com.cos.project.entity;
//
//import com.fasterxml.jackson.annotation.JsonIncludeProperties;
//import com.fasterxml.jackson.annotation.JsonProperty;
//
//import jakarta.inject.Named;
//import jakarta.persistence.CascadeType;
//import jakarta.persistence.Entity;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public class CommentLikeEntity {
//
//			@Id
//			@GeneratedValue(strategy = GenerationType.IDENTITY)
//			Long id;
//	
//			boolean flag;
//			
//			@ManyToOne(cascade = CascadeType.REMOVE)
//			@JoinColumn(name= "board_id" )
//			@JsonIncludeProperties({"id","content"})
//			CommentEntity CommentEntity;
//			
//			@ManyToOne(cascade = CascadeType.REMOVE)
//			@JoinColumn(name= "member_id" )
//			@JsonIncludeProperties({"id","userid"})
//			MemberEntity memberEntity;
//	
//}
