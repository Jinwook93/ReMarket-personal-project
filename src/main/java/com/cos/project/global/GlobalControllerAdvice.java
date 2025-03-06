package com.cos.project.global;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.cos.project.details.PrincipalDetails;


@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute			//로그인 변수 전역적으로 처리
    public void addHeaderAttributes(Model model, @AuthenticationPrincipal PrincipalDetails principal) {
        if (principal != null) {
            model.addAttribute("isLoggedIn", true);
            model.addAttribute("name", principal.getMemberEntity().getName());
            model.addAttribute("loggedUserId", principal.getMemberEntity().getUserid());
            model.addAttribute("id", principal.getMemberEntity().getId());
            model.addAttribute("profileImage", principal.getMemberEntity().getProfileImage());
        } else {
            model.addAttribute("isLoggedIn", false);
        }
    }
}
