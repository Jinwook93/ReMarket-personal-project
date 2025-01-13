package com.cos.project.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.MemberEntity;
import com.cos.project.service.MemberService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
public class MemberController {

	private MemberService memberService;
		
    @Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}

    
    
	@GetMapping("/")
	public String  home() {
		return "메인 페이지";
	}
	
	
	@GetMapping("/findall")
	public ResponseEntity<?> findAllUser() {
	    return ResponseEntity.ok(memberService.showAllMember());
	}
    
//    
//	@GetMapping("/findall")
//	public List<MemberEntity> findAllUser() {
//		return memberService.showAllMember();
//	}
	
	@PostMapping("/join")
public ResponseEntity<?>  joinUser(@RequestBody MemberDTO entity) {
    	String result = memberService.joinMember(entity);
    
    return ResponseEntity.ok(result);
}
	
//	@PostMapping("/join")
//public String joinUser(@RequestBody MemberDTO entity) {
//    	String result = memberService.joinMember(entity);
//    
//    return result;
//}

@PutMapping("updateuser/{id}")
public ResponseEntity<?> updateUser(@PathVariable(name = "id") Long id, @RequestBody MemberDTO memberDTO) {
    String result = memberService.updateMember(id, memberDTO);
    return ResponseEntity.ok(result);
}

//@PutMapping("updateuser/{id}")
//public String updateUser(@PathVariable(name = "id") Long id, @RequestBody MemberDTO memberDTO) {
//    String result = memberService.updateMember(id, memberDTO);
//    return result;
//}

@DeleteMapping("deleteuser/{id}")
public ResponseEntity<?> deleteUser(@PathVariable(name = "id") Long id) {
    String result = memberService.deleteMember(id);
    return ResponseEntity.ok(result);
}

//@DeleteMapping("deleteuser/{id}")
//public String deleteUser(@PathVariable(name = "id") Long id) {
//    String result = memberService.deleteMember(id);
//    return result;
//}

	
}
