package com.cos.project.dto;

import com.cos.project.entity.Gender;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.Roles;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//@Entity
//@Table(name = "Member")
//@AllArgsConstructor
//@NoArgsConstructor
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberDTO {

//	@Id
//	@GeneratedValue(strategy =GenerationType.IDENTITY )
	Long id;
	
	
	
//	@Column(unique = true)
	String userid;
	
	String password;
	
	String nickname; 
	
	String name;
	
//	@Enumerated(EnumType.STRING)
	private Gender gender=Gender.MALE;
	
	
	int age;
	
	String phone;
	
	String address;
	
	private Roles roles = Roles.USER;
	
	
	private String profileImage; // 프로필 이미지 URL 또는 경로
	
//	 public MemberDTO(MemberEntity memberEntity) {
//	        this.userid = memberEntity.getUserid();
//	        this.password = memberEntity.getPassword();
//	        this.address = memberEntity.getAddress();
//	        this.age = memberEntity.getAge();
//	        this.phone= memberEntity.getPhone();
//	        this.gender= memberEntity.getGender();
//	        this.name = memberEntity.getName();
//	        
//	    }

	
	
	
}
