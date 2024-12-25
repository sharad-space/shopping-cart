package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.UserDts;

public interface UserRepo extends JpaRepository<UserDts, Integer> {
	
	public UserDts findByEmail(String email);

	public List<UserDts> findByRole(String role);
	
	public UserDts findByResetToken(String token);

}
