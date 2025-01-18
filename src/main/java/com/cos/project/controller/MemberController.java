package com.cos.project.controller;

import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.dto.LoginRequest;
import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.MemberEntity;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.authentication.UserServiceBeanDefinitionParser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
//@RequestMapping("/member")
public class MemberController {

	private MemberService memberService;
		
    @Autowired
	public MemberController(MemberService memberService) {
		this.memberService = memberService;
	}


    @Autowired
    private BoardService boardService;
    
    @Autowired
    private CommentService commentService;
    
    
//	@GetMapping("/")
//	public String  home() {
//		return "메인 페이지";
//	}
//	
    
    
    @PostMapping("/loginmember")			//   (요청 -> filterchain -> 인증/인가 -> 컨트롤러 순서 )
    																//시큐리티가 가로채서 loginProcessingUrl("/loginMember") POST 요청을 실행 
    															//  만약 Spring Security가 요청을 처리하면, 컨트롤러에 도달하지 않습니다. 따라서 , 이 컨트롤러는 구현을 할 수 없다
    
    												/*Spring Security가 가로채는 경로(/loginmember)와 컨트롤러에서 사용하는 경로(/loginMember)는 중복되지 않도록 분리해야 합니다.
    												Spring Security를 비활성화하지 않는 이상, 같은 경로(/loginmember)의 POST 요청을 컨트롤러에서 직접 처리할 수 없습니다.
    												프로젝트 요구사항에 따라, Spring Security의 인증 로직을 유지하면서 커스터마이징하거나, 완전히 비활성화하고 커스텀 구현을 선택해야 합니다. 😊 
    													=> 1. PostMapping url을 loginProcessingUrl과 다른 값으로 설정 (※ formLogin()을 계속 사용하기 원할 경우)	
    													-- 프론트 쪽에서는 전송 데이터 타입을 x-www-urlencoded 타입으로 해야한다 (시큐리티에서 기본으로 지원하고 있음)
    														2.  formLogin().disabled               -- formLogin 비활성화	
    														(※ PostMapping의 url을  "loginMember" (loginProcessingUrl 지정값)으로 설정을 해야할 경우)
    																-- 프론트 쪽에서는 전송 데이터 타입을 JSON 타입으로 해야한다 (컨트롤러에서 RequestBody (JSON)으로 받고 있음)
    																		--  @RequestBody는 JSON이나 XML 데이터를 처리할 때 사용되며, 
    																		--   x-www-form-urlencoded 데이터는 @RequestParam을 사용하여 받습니다.
    																		
  													
    */
       // public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
    
    
    
    	 public ResponseEntity<?> login(@RequestParam (name = "userid") String userid, @RequestParam (name = "password") String password) {
    	LoginRequest loginRequest = new LoginRequest(userid,password); 
        System.out.println("loginMember 컨트롤러 실행 중...");
       return ResponseEntity.ok(memberService.checkLogin(loginRequest));
    }
	
    @PostMapping("/checkId")
   public String checkId(@RequestBody String userid) throws JsonMappingException, JsonProcessingException {
    	System.out.println(userid);
    	ObjectMapper om = new ObjectMapper();
    	MemberDTO userid_change = om.readValue(userid, MemberDTO.class);		//DTO 클래스로 변환
       System.out.println("중복ID 컨트롤러");
       return memberService.checkId(userid_change.getUserid());
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
