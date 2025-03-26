package com.cos.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.cos.project.details.PrincipalDetailsService;
import com.cos.project.filter.AuthenticateUserFilter;
import com.cos.project.filter.CustomAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

//import com.cos.project.details.PrincipalDetailsService;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

@Autowired
	private final CustomAuthenticationSuccessHandler successHandler;



	@Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder(); // BCryptPasswordEncoder 빈 정의
	    }
	
	 @Bean
	 public CorsConfigurationSource corsConfigurationSource() {
		 CorsConfiguration source = new CorsConfiguration();
		 source.addAllowedHeader("*");
		 source.addAllowedMethod("*");
		 source.addAllowedOrigin("http://localhost:5173");
		 source.setAllowCredentials(true);
		
		 UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
		 urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", source);
		 
		 return urlBasedCorsConfigurationSource;
	 }
	

//	 
//	 @Autowired
//	 PrincipalDetailsService principalDetailsService;
	 
	
	 
	 
	 
	 @Autowired
	 AuthenticationConfiguration authenticationConfiguration;
	 

	 
	 
	 @Bean
	    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
	        return authenticationConfiguration.getAuthenticationManager();
	    }


		
		
		@Bean
		public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		    http
		        .addFilterBefore(new AuthenticateUserFilter(authenticationManager(authenticationConfiguration)), UsernamePasswordAuthenticationFilter.class)
		        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
		        .csrf().disable()
		        .authorizeHttpRequests(auth ->													
		            auth.requestMatchers("/findall").hasRole("ADMIN")
		                .requestMatchers("/board/writeboard").authenticated()
		                .requestMatchers("/login").permitAll()
		                .requestMatchers("/profileimage/**").permitAll()  // '/profileimage' 경로 하위 모든 이미지에 대해 접근 허용
		                .requestMatchers("/profileimage/**").permitAll()  // '/profileimage' 경로 하위 모든 이미지에 대해 접근 허용
		                // 이미지 파일 경로에 대한 접근을 허용
		       
		                .anyRequest().permitAll()
		        )
		        														//formLogin().disable 할 시 requestMatchers를 인증할 방법이 없으므로, 'permitAll' 만 접근이 가능하다
		        
	//	        .formLogin().disable()
		        
		        .formLogin(login ->							//formLogin은 서버 사이드렌더링에서 사용. 클라이언트 사이드 렌더링을 만들 것이기 떄문에 주석 처리할 것이다
		            login	
		            	.loginPage("/formlogin")							//로그인 GET URL
		                .loginProcessingUrl("/formlogin") // 로그인 POST URL
		                .successHandler(successHandler)			//리다이렉션될 시 적용될 handler
//		                .defaultSuccessUrl("/")								//리디렉트 url(기본값 생략가능)
//		                .failureUrl("/formlogin?error")				//리디렉트 url (기본값 생략가능)
//		                .successForwardUrl("/login")					//포워딩할 url (AuthenticateUserFilter 필터 테스트)
//	                .failureForwardUrl("/login?error")			//	실패시 url (AuthenticateUserFilter 필터 테스트)
		                .usernameParameter("userid") // 로그인 폼에서 userid 파라미터 사용
		                .passwordParameter("password") // 로그인 폼에서 password 파라미터 사용
		                
		                .permitAll()
		        )
		        .sessionManagement(session ->
		            session
		                .maximumSessions(1)
		                .maxSessionsPreventsLogin(true)
		         //       .expiredUrl("/")
		        )
		        .logout(logout ->
		            logout
		                .invalidateHttpSession(true)
		    //            .logoutSuccessUrl("/")
		        );

		    return http.build();
		}

};
