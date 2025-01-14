package com.cos.project.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
	

	@Enumerated(EnumType.STRING)
	private Gender gender;
	
	
	@Enumerated(EnumType.STRING)
	private Roles roles;
	
	
	
	@OneToMany(mappedBy = "memberEntity", fetch = FetchType.EAGER)
	List<BoardEntity> boards = new ArrayList<>();
	
	
	@OneToMany(mappedBy = "memberEntity", fetch = FetchType.EAGER)
	List<CommentEntity> comments = new ArrayList<>();

}
