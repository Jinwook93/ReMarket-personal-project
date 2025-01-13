package com.cos.project.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

//import com.cos.project.details.PrincipalDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	 @Bean
	    public BCryptPasswordEncoder passwordEncoder() {
	        return new BCryptPasswordEncoder(); // BCryptPasswordEncoder 빈 정의
	    }
	
	
	

	 
	
		@Bean
		public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
		    return authenticationConfiguration.getAuthenticationManager();
		}
	 
	 
	 
	@Bean
	public SecurityFilterChain securityFilterChain (HttpSecurity http) throws Exception {
		
		http.csrf().disable()
		.authorizeHttpRequests(auth ->
		auth.requestMatchers("/findall").hasRole("ADMIN")
			.requestMatchers("/login").permitAll()	
			.requestMatchers("/join","/deleteuser/*","/updateuser/*").permitAll()
			.anyRequest().authenticated()
				)
		
	//	.formLogin().disable()
		.formLogin(login ->
		login.loginProcessingUrl("/login")
		//		.defaultSuccessUrl("/", true)												// 문제 해결 : 로그인 성공 후 설정을 "/"로 하였기 떄문에 ROLE에 상관없이 안되었던것이다
				.failureForwardUrl("/login?error=true")
	            .usernameParameter("userid") // 로그인 폼에서 userid 파라미터 사용
	           .passwordParameter("password") // 로그인 폼에서 password 파라미터 사용
				)
		.sessionManagement(session ->
		session.maximumSessions(1)
		.maxSessionsPreventsLogin(true)
		.expiredUrl("/")
				)

		.logout(logout ->
			logout.invalidateHttpSession(true)
				.logoutSuccessUrl("/")
				);
		return http.build();
	};

}
