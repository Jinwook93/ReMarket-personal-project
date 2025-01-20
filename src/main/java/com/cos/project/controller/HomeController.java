package com.cos.project.controller;


import java.security.Key;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.BoardEntity;
import com.cos.project.entity.CommentEntity;
import com.cos.project.entity.Gender;
import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.Roles;
import com.cos.project.service.BoardService;
import com.cos.project.service.CommentService;
import com.cos.project.service.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {
//	@GetMapping("/")
//	public String  home() {
//		return "index";
//	}
	@Autowired
	MemberService memberService;
	
	@Autowired
	BoardService boardService;
	
	@Autowired
	CommentService commentService;
	

	
	private final static String ADMIN_PASSWORD  = "admin!@#$";
	
			
	
	@GetMapping("/")
	public String mainPage(Model model, @AuthenticationPrincipal PrincipalDetails principal) {

		   if (principal != null) {
	        // Principal 정보 활용
	        model.addAttribute("isLoggedIn", true);
	        model.addAttribute("name", principal.getMemberEntity().getName());
	        model.addAttribute("id", principal.getMemberEntity().getId());
	    } else {
	        // 비로그인 상태
	        model.addAttribute("isLoggedIn", false);
	    }
	    return "index";
	}

	
	@GetMapping("/formlogin")
	public String  loginpage() {
		return "formlogin";
	}
	
	@GetMapping("/join")
	public String  joinpage() {
		return "join";
	}
	
	@PostMapping("/serverjoin")
	public String serverJoin(
	        @RequestParam(name = "userid") String userid,
	        @RequestParam(name = "password") String password,
	        @RequestParam(name = "name") String name,
	        @RequestParam(name = "phone") String phone,
	        @RequestParam(name = "age") int age,
	        @RequestParam(name = "address") String address,
	        @RequestParam(name = "gender") Gender gender,
	        @RequestParam(name = "password_admin") String password_admin
	) {
		Roles role;
		if(!password_admin.equals(ADMIN_PASSWORD)  || password_admin.equals("")){
		role = Roles.USER;
		}else {
		role = Roles.ADMIN;
		}
		
	    // Build the MemberDTO using builder pattern
	    MemberDTO memberDTO = MemberDTO.builder()
	        .userid(userid)
	        .password(password)
	        .name(name)
	        .phone(phone)
	        .age(age)
	        .address(address)
	        .gender(gender)
	        .roles(role)
	        .build();

	    // Call the service to process the member join
	    String result = memberService.joinMember(memberDTO);

	    if (result.equals("회원가입 완료")) {
	        return "redirect:/";  // Redirect to the main page after successful registration
	    } else {
	        return "redirect:/join";  // Redirect back to the join page if the signup fails
	    }
	}


	//내가 쓴 댓글 리스트 확인
//	@GetMapping("/commentlist/{id}")
//	public String myComment(@PathVariable("id") Long id, Model model) {
//		List<CommentEntity> mycomments = commentService.findMyComments(id);
//		model.addAttribute("mycomments", mycomments);
//		
//		return "mycommentlist";
//	}

	//내가 쓴 댓글 리스트 확인 2 : 이렇게 찾아도 됨
	@GetMapping("/commentlist/{id}")
	public String myComment(@PathVariable("id") Long id, Model model) throws IllegalAccessException {
		MemberEntity member = memberService.findById(id);
		model.addAttribute("mycomments", member.getComments());
		
		return "mycommentlist";
	}
	
	
	
	//내가 쓴 게시글 리스트 확인
	@GetMapping("/boardlist/{id}")
	public String myBoard(@PathVariable("id") Long id, Model model) {
		List<BoardEntity> myboards = boardService.findMyBoards(id);
		model.addAttribute("myboards", myboards);
		
		return "myboardlist";
	}
	
	
	//마이페이지 확인
	@GetMapping("/mypage/{id}")
	public String myPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberEntity memberEntity = principalDetails.getMemberEntity();
		System.out.println("나이"+memberEntity.getAge());
		System.out.println("성별"+memberEntity.getGender());
		System.out.println("역할"+memberEntity.getRoles());
		if(memberEntity == null) {
			model.addAttribute("message", "사용자 정보를 조회할 수 없습니다");
		}
		else {
			
			model.addAttribute("member", memberEntity);
		}
	
		
		return "mypage";
	}
	
	
	//마이페이지 수정창 이동
	@GetMapping("/updatemypage/{id}")
	public String goUpdatemyPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
		MemberEntity memberEntity = principalDetails.getMemberEntity();
		
		if(memberEntity == null) {
			model.addAttribute("message", "사용자 정보를 조회할 수 없습니다");
		}
		else {
			
			model.addAttribute("member", memberEntity);
		}
	
		
		return "goUpdateMemberForm";
	}
	
	
	
	
	
	
	
	
	
	
//	//DB에 마이페이지 수정 적용
//	@PutMapping("/updatemypage/{id}")
//	public String updateMyPage(@PathVariable("id") Long id, Model model, @AuthenticationPrincipal PrincipalDetails principalDetails) {
//		MemberEntity memberEntity = principalDetails.getMemberEntity();
//		memberEntity.set
//		if(memberEntity == null) {
//			model.addAttribute("message", "사용자 정보를 조회할 수 없습니다");
//		}
//		else {
//			
//			model.addAttribute("member", memberEntity);
//		}
//	
//		
//		return "mypage";
//	}
	
	
	
	
}
