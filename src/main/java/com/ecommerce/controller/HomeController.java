package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.UserDts;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.CommonUtils;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;


@Controller
public class HomeController {
	
	@Autowired
	private CategoryService categoryService;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CommonUtils commonUtils;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
    private CartService cartService;
	
	@ModelAttribute
	public void getUserDetails(Principal p,Model m) {
		if (p!=null) {
			String email = p.getName();
			UserDts userDetails = userService.getUserDetailsByEmail(email);
			m.addAttribute("user", userDetails);
			Integer countCart = cartService.getCountCart(userDetails.getId());
			m.addAttribute("countCart", countCart);
			
		}
		
		List<Category> activeCategory = categoryService.getAllActiveCategory();
		m.addAttribute("category", activeCategory);
		
	}
	
	@GetMapping("/")
	public String index() {
		
		return "index";
		
	}
	
	@GetMapping("/signin")
	public String login() {
		return "login";
	}
	
	@GetMapping("/register")
	public String register() {
		
		return "register";
		
	}
	
	@GetMapping("/product")
	public String product(Model m,@RequestParam(value = "gategory", defaultValue = "") String gategory) {
		List<Category> categories = categoryService.getAllActiveCategory();
		List<Product> products = productService.getAllActiveProduct(gategory);
		m.addAttribute("categories", categories);
		m.addAttribute("products", products);
		m.addAttribute("parmValue", gategory);
		return "product";
		
	}
	
	@GetMapping("/view_product/{id}")
	public String viewProduct(@PathVariable int id,Model m) {
		Product product = productService.getProductById(id);
		m.addAttribute("product", product);
		
		return "view_product";
		
	}
	
	@PostMapping("/saveUser")
	public String saveUser(@ModelAttribute UserDts user,@RequestParam("img") MultipartFile file,HttpSession session) throws IOException {
		
		String image=file.isEmpty() ? "default.png":file.getOriginalFilename();
		user.setProfileImage(image);
		UserDts saveUser = userService.saveUser(user);
		if(!ObjectUtils.isEmpty(saveUser)) {
			if (!file.isEmpty()) {
				File saveFilePath = new ClassPathResource("static/img").getFile();

				java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + "profile_img"
						+ File.separator + file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}
			session.setAttribute("succMsg", "Register successfully");
		}else {
			session.setAttribute("errorMsg", "Somthing went wrong!");
		}
		return "redirect:/register";
	}
	
	@GetMapping("/forgot-password")
	public String showForgotPassword() {
		
		return "forgot_password.html";
		
	}
	
    @PostMapping("/forgot-password")
	public String  processForgotPassword(@RequestParam String email,HttpSession session,HttpServletRequest request) throws UnsupportedEncodingException, MessagingException {
		
    	 UserDts byEmail = userService.getUserDetailsByEmail(email);
    	 if(!ObjectUtils.isEmpty(byEmail)) {
    		 
    		 String resetToken = UUID.randomUUID().toString();
    		userService.updateUserResetToken(email,resetToken);
    	    String url= CommonUtils.generateUrl(request)+"/reset-password?token="+resetToken;
    		 
    		 
    		 Boolean sendMail = commonUtils.sendMail(url,email);
    		 if (sendMail) {
    			 session.setAttribute("succMsg", "Please check your mail to change your password");
			}else {
				session.setAttribute("errorMsg", "Somthing wrong on server ! mail not send !");
			}
    	 }else {
			session.setAttribute("errorMsg", "Invaild Email");
		}
		return "redirect:/forgot-password";
		
	}
	
	@GetMapping("/reset-password")
	public String showResetPassword(@RequestParam String token,HttpSession session,Model m) {
		UserDts userByToken = userService.getUserByToken(token);
		if (userByToken==null) {
			m.addAttribute("Msg", "Your link is Invalid or Expired ");
			return "message";
		}
		m.addAttribute("token", token);
		return "reset_password";
		
	}
	
	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam String token,@RequestParam String password, HttpSession session,Model m) {
		UserDts userByToken = userService.getUserByToken(token);
		
		if (userByToken==null) {
			m.addAttribute("Msg", "Your link is Invalid or Expired ");
			return "message";
		}else {
			userByToken.setPassword(passwordEncoder.encode(password));
			userByToken.setResetToken(null);
			userService.updaUserDts(userByToken);
			session.setAttribute("succMsg", "Password change successfully!");
			m.addAttribute("Msg","Password change successfully!");
			return "message";
		}
		
		
		
	}
	
	@GetMapping("/search-prodcut")
	public String searchProduct(@RequestParam String ch,Model m) {
		List<Product> searchProduct = productService.searchProduct(ch);
		m.addAttribute("products", searchProduct);
		
		List<Category> categories = categoryService.getAllActiveCategory();
		
		m.addAttribute("categories", categories);
		
		return "product";
	}
	

}
