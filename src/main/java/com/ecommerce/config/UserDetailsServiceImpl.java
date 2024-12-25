package com.ecommerce.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ecommerce.model.UserDts;
import com.ecommerce.repository.UserRepo;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	
	@Autowired
    private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDts user = userRepo.findByEmail(username);
		
		if(user==null) {
			throw new UsernameNotFoundException("User Not Found!");
		}
		return new CustomUser(user);
	}

}

