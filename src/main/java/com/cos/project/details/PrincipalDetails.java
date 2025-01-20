package com.cos.project.details;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.cos.project.entity.MemberEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PrincipalDetails implements UserDetails {

    private final MemberEntity memberEntity;
    private final Collection<? extends GrantedAuthority> authorities;    // getAuthorities 인자를 안 받아도 잘 됨

    public PrincipalDetails(MemberEntity memberEntity) {
        this.memberEntity = memberEntity;
        this.authorities = getAuthorities();     // getAuthorities 인자를 안 받아도 잘 됨
    }
    // getAuthorities 인자를 안 받아도 잘 됨
    public PrincipalDetails(MemberEntity memberEntity, Collection<? extends GrantedAuthority> authorities) {
    		this.memberEntity = memberEntity;
    		this.authorities = authorities;

    
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
