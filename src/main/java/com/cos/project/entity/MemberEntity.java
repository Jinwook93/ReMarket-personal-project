package com.cos.project.entity;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Member")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class MemberEntity {

	@Id
	@GeneratedValue(strategy =GenerationType.IDENTITY )
	Long id;
	
    @Column(unique = true, nullable = false)
    private String userid;					//시큐리티에 로그인 할 ID

    @Column(nullable = false)
    private String password;			//시큐리티에 로그인 할 패스워드
	
	String name;
	
	
	int age;
	
	String phone;
	
	String address;
	
//	  @Lob																//Base64방식으로 사용할 경우
//	   @Column(name = "profile_image", columnDefinition = "BLOB")  // 프로필 이미지 데이터
//	    private byte[] profileImage;
	

	  
	  //이렇게 이미지 작성 파일을 만들어도 됨
	  private String profileImage; // 프로필 이미지 URL 속성 추가
	  
	  
	  
	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	
	@Enumerated(EnumType.STRING)
	private Roles roles;
	
	
	
	@OneToMany(mappedBy = "memberEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	@JsonManagedReference("member-boards")
	List<BoardEntity> boards = new ArrayList<>();
	
	
	@OneToMany(mappedBy = "memberEntity", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//	@JsonManagedReference("member-comments")
	List<CommentEntity> comments = new ArrayList<>();
	
	
//	   @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//	    @JsonManagedReference("board-likeboard")
//	    private List<BoardLikeEntity> boardLikeEntities;
//	    
//	    
//	    @OneToMany(mappedBy = "memberEntity", cascade = CascadeType.ALL, orphanRemoval = true)
//	    @JsonManagedReference("board-likecomment")
//	    private List<CommentLikeEntity> commentLikeEntities;

}
