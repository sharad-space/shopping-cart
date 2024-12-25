package com.ecommerce.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.UserDts;

public interface UserService {
	
	public UserDts saveUser(UserDts user);
	
	public UserDts getUserDetailsByEmail(String email);
	
	public List<UserDts> getAllUsers(String role);

	public Boolean updateAccountStatus(Integer id, Boolean status);
	
	public void increaseFailedAttempt(UserDts userDts);

	public void userAccountLock(UserDts userDts);
	
	public Boolean unlockAccountTimeExpired(UserDts userDts);
	
	public void resetAttempt(int userId);

	public void updateUserResetToken(String email, String resetToken);
	
	public UserDts getUserByToken(String token);
	
	public UserDts updaUserDts(UserDts user);
	
	public UserDts updaUserProfile(UserDts user,MultipartFile img);

	
}
