package com.cos.project.controller;


import java.security.Key;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;
import com.cos.project.dto.MemberDTO;
import com.cos.project.entity.Gender;
import com.cos.project.entity.Roles;
import com.cos.project.service.MemberService;

@Controller
public class HomeController {
//	@GetMapping("/")
//	public String  home() {
//		return "index";
//	}
	@Autowired
	MemberService memberService;
	
	private final static String ADMIN_PASSWORD  = "admin!@#$";
	
			
	
	@GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("name", principal.getMemberEntity().getName());
        } else {
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
	public String postMethodName(
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


	

	
	
}
