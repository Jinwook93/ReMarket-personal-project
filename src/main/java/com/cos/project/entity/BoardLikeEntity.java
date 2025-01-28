package com.cos.project.entity;

import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.inject.Named;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardLikeEntity {

			@Id
			@GeneratedValue(strategy = GenerationType.IDENTITY)
			Long id;
	
			boolean flag;		//활성화 및 취소
			
			@ManyToOne
			@JoinColumn(name= "board_id" )
			@JsonIncludeProperties({"id","title"})
			BoardEntity boardEntity;
			
			@ManyToOne
			@JoinColumn(name= "member_id" )
			@JsonIncludeProperties({"id","userid"})
			MemberEntity memberEntity;
	
}
