package com.cos.project.filter;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 로그인 후 리다이렉션할 URL을 설정
    	
    	System.out.println("요청 URL"+request.getRequestURI());
    	
        String redirectUrl = "/";  // 기본 리다이렉트 URL

        // 추가 로직을 통해 사용자의 권한에 따라 리다이렉션 URL을 변경할 수 있습니다.
        if (authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            redirectUrl = "/adminPage";
        }

        response.sendRedirect(redirectUrl);  // 설정한 URL로 리다이렉트
    }
}

