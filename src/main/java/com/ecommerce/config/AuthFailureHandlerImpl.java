package com.ecommerce.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecommerce.model.UserDts;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailureHandlerImpl extends SimpleUrlAuthenticationFailureHandler {

	@Autowired
	private UserRepo userRepo;
	
	@Autowired
	private UserService userService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		  String email = request.getParameter("username");
		  UserDts user = userRepo.findByEmail(email);
		  if (user!=null) {
			
		
		  if(user.getIsEnable()) {
			  if (user.getAccountNonLocked()) {
				if(user.getFaildAttempt()<AppConstant.ATTEMPT_TIME) {
					userService.increaseFailedAttempt(user);
				}else {
					userService.userAccountLock(user);
					exception=new LockedException("Your Account is Locked !! faild attempt more than 3");
				}
			}else {
				if(userService.unlockAccountTimeExpired(user)) {
					exception=new LockedException("Your Account is unLocked !! Please try to login");
					
				}else {
					exception=new LockedException("Your Account is Locked !! Please try after sometimes");
				}
				
			}
			  
		  }else {
			exception=new LockedException("Your Account is Inactive !");
		}
		  }else {
			  exception=new LockedException("Email & Password Incorrect !");
		}
		  
		super.setDefaultFailureUrl("/signin?error");  
		super.onAuthenticationFailure(request, response, exception);
	}

}
