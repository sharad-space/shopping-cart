package com.ecommerce.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.UserDts;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.AppConstant;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepo userRepo;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDts saveUser(UserDts user) {
		user.setRole("ROLE_USER");
		user.setIsEnable(true);
		user.setAccountNonLocked(true);
		user.setFaildAttempt(0);
		user.setLockTimeDate(null);
		String password = passwordEncoder.encode(user.getPassword());
		user.setPassword(password);
		UserDts save = userRepo.save(user);
		return save;
	}

	@Override
	public UserDts getUserDetailsByEmail(String email) {
		UserDts userDts = userRepo.findByEmail(email);
		return userDts;
	}

	@Override
	public List<UserDts> getAllUsers(String role) {
		return userRepo.findByRole(role);

	}

	@Override
	public Boolean updateAccountStatus(Integer id, Boolean status) {
		Optional<UserDts> byId = userRepo.findById(id);
		if (byId.isPresent()) {
			UserDts user = byId.get();
			user.setIsEnable(status);
			userRepo.save(user);
			return true;
		}
		return false;
	}

	@Override
	public void increaseFailedAttempt(UserDts user) {
		int attempt = user.getFaildAttempt() + 1;
		user.setFaildAttempt(attempt);
		userRepo.save(user);

	}

	@Override
	public void userAccountLock(UserDts user) {
		user.setAccountNonLocked(false);
		user.setLockTimeDate(new Date());
		userRepo.save(user);

	}

	@Override
	public Boolean unlockAccountTimeExpired(UserDts user) {
		long lockTime = user.getLockTimeDate().getTime();
		long unLockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
		
		long currentTime = System.currentTimeMillis();
		if (unLockTime<currentTime) {
			user.setAccountNonLocked(true);
			user.setFaildAttempt(0);
			user.setLockTimeDate(null);
			userRepo.save(user);	
			return true;
		}
 
		return false;
	}

	@Override
	public void updateUserResetToken(String email, String resetToken) {
		UserDts byEmail = userRepo.findByEmail(email);
		byEmail.setResetToken(resetToken);
		userRepo.save(byEmail);
		
	}

	@Override
	public UserDts updaUserDts(UserDts user) {
		
		return userRepo.save(user);
	}

	@Override
	public void resetAttempt(int userId) {
		

	}

	@Override
	public UserDts getUserByToken(String token) {
		UserDts byResetToken = userRepo.findByResetToken(token);
		
		return byResetToken;
	}

	@Override
	public UserDts updaUserProfile(UserDts user,MultipartFile img) {
		
		UserDts existUser = userRepo.findById(user.getId()).get();
		
		if(!img.isEmpty()) {
			existUser.setProfileImage(img.getOriginalFilename());
		}
		existUser.setName(user.getName());
		existUser.setMobileNumber(user.getMobileNumber());
		existUser.setAddress(user.getAddress());
		existUser.setPincode(user.getPincode());
		existUser.setCity(user.getCity());
		existUser.setState(user.getState());
		existUser = userRepo.save(existUser);
		try {
		
		if (!img.isEmpty()) {
			File saveFilePath = new ClassPathResource("static/img").getFile();

			java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + "profile_img"
					+ File.separator + img.getOriginalFilename());
			System.out.println(path);
			Files.copy(img.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

		}} catch (Exception e) {
			e.printStackTrace();
		}
		return existUser;
	}

}
