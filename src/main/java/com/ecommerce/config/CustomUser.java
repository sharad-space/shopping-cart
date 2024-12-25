package com.ecommerce.config;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.ecommerce.model.UserDts;

public class CustomUser implements UserDetails{
	
	
	private UserDts user;
	
	

	public CustomUser(UserDts user) {
		super();
		this.user = user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority=new SimpleGrantedAuthority(user.getRole());
		return Arrays.asList(authority);
	}

	@Override
	public String getPassword() {
		
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		
		return user.getEmail();
	}
	
	
	@Override
	public boolean isEnabled() {
		return user.getIsEnable();
		
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return user.getAccountNonLocked();
		
	}

}
