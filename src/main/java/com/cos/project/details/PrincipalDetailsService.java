package com.cos.project.details;

import java.lang.reflect.Member;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.cos.project.entity.MemberEntity;
import com.cos.project.entity.Roles;
import com.cos.project.repository.MemberRepository;

@Service
public class PrincipalDetailsService implements UserDetailsService{

	
	@Autowired
	public MemberRepository memberRepository;
	


	
	@Override
	public UserDetails loadUserByUsername(String userid) throws UsernameNotFoundException {
		MemberEntity member = memberRepository.findByUserid(userid).orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다"));
		
//		Set<SimpleGrantedAuthority> authorities = new HashSet<>();
//
//		// roles가 Enum 타입인 경우
//      //  for (Roles role : member.getRoles()) {
//            authorities.add(new SimpleGrantedAuthority("ROLE_" + member.getRoles()));  // Enum의 경우 name()을 사용
//       // }

        // 혹시 Roles가 Entity라면 role.getName() 등을 사용해야 합니다.
        // 예: authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
		  
		return new PrincipalDetails(member);
	}

}
