package com.cos.project.details;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.project.entity.MemberEntity;

import lombok.Getter;

@Getter
public class PrincipalDetails implements UserDetails {

    private final MemberEntity memberEntity;

    public PrincipalDetails(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        authorities.add(new GrantedAuthority() {
			
			@Override
			public String getAuthority() {	
				return  "ROLE_" + memberEntity.getRoles();
			}
		});
        
        return authorities;
    }

    @Override
    public String getPassword() {
        return memberEntity.getPassword();
    }

    @Override
    public String getUsername() {
        return memberEntity.getUserid();
    }
    

    @Override
    public boolean isAccountNonExpired() {
        return true; // Adjust based on your requirements
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Adjust based on your requirements
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Adjust based on your requirements
    }

    
}
