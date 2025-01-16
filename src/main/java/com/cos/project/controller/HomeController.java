package com.cos.project.controller;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cos.project.details.PrincipalDetails;

@Controller
public class HomeController {
//	@GetMapping("/")
//	public String  home() {
//		return "index";
//	}
	
	@GetMapping("/")
    public String mainPage(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("username", principal.getUsername());
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
}
