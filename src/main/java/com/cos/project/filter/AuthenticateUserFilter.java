package com.cos.project.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class AuthenticateUserFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;



@Autowired
    // 생성자에서 AuthenticationManager 주입
    public AuthenticateUserFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        
    }

    // 로그인 시도 시 호출되는 메서드
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        // 요청에서 username과 password를 추출
    	
    	
    	 if (!"POST".equalsIgnoreCase(request.getMethod())) {
    	        throw new AuthenticationException("지원되지 않는 HTTP 메서드입니다. POST 요청만 허용됩니다.") {};
    	    }
    	
        String username = request.getParameter("userid");  // 예: 리액트 로그인 폼에서 전달되는 "userid"
        String password = request.getParameter("password"); // 예: 리액트 로그인 폼에서 전달되는 "password"

        // 로그 출력 (디버깅 용도)
        System.out.println("로그인 시도 - 아이디: " + username + ", 비밀번호: " + password);

        // 인증 객체 생성
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

        // 인증을 진행하고, 결과를 반환
        return authenticationManager.authenticate(authenticationToken);
    }

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		String username = request.getParameter("userid");  // 예: 리액트 로그인 폼에서 전달되는 "userid"
        String password = request.getParameter("password"); // 예: 리액트 로그인 폼에서 전달되는 "password"
		 System.out.println("로그인 성공 - 아이디: " + username + ", 비밀번호: " + password);
		super.successfulAuthentication(request, response, chain, authResult);
	}

	
	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		String username = request.getParameter("userid");  // 예: 리액트 로그인 폼에서 전달되는 "userid"
        String password = request.getParameter("password"); // 예: 리액트 로그인 폼에서 전달되는 "password"
		 System.out.println("로그인 실패 - 아이디: " + username + ", 비밀번호: " + password);
		super.unsuccessfulAuthentication(request, response, failed);
	}

	

//    // doFilter 메서드는 필터 체인에서 작동하며, 인증 처리를 진행하는 필터의 순서에 영향을 미침
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        // 요청을 필터 체인에 넘기기 전에 처리할 로직을 추가 가능
//        System.out.println("Custom filter: 필터 체인에 요청 전달 전");
//
//        // 필터 체인에 요청 전달
//        chain.doFilter(request, response);
//    }
    
    
    
}
