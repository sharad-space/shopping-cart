package com.ecommerce.controller;

import java.security.Principal;
import java.util.List;

import org.aspectj.weaver.patterns.IScope;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Cart;
import com.ecommerce.model.Category;
import com.ecommerce.model.OrderRequest;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDts;
import com.ecommerce.repository.UserRepo;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.UserService;
import com.ecommerce.utils.CommonUtils;
import com.ecommerce.utils.OrderStatus;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	@Autowired
	private UserService userService;
	@Autowired
	private CategoryService categoryService;

	@Autowired
	private CartService cartService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CommonUtils commonUtils;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home() {
		return "user/home";
	}

	@ModelAttribute
	public void getUserDetails(Principal p, Model m) {
		if (p != null) {
			String email = p.getName();
			UserDts userDtls = userService.getUserDetailsByEmail(email);
			m.addAttribute("user", userDtls);
			
			Integer countCart = cartService.getCountCart(userDtls.getId());
			m.addAttribute("countCart", countCart);
		}

		List<Category> allActiveCategory = categoryService.getAllActiveCategory();
		m.addAttribute("categorys", allActiveCategory);
	}

	@GetMapping("/addCart")
	public String addToCart(@RequestParam Integer pid, @RequestParam Integer uid,HttpSession session) {
		Cart saveCart = cartService.saveCart(pid, uid);
		
		if (ObjectUtils.isEmpty(saveCart)) {
			session.setAttribute("errorMsg", "Product add to cart failed");
		}else {
			session.setAttribute("succMsg", "Product added to cart");
		}
		return "redirect:/view_product/" + pid;
	}
	
	@GetMapping("/cart")
	public String loadCartPage(Principal p,Model m) {
		 
		   
		UserDts user=getLoggedInUserDetails(p);
		List<Cart> carts = cartService.getCartsByUser(user.getId());
		m.addAttribute("carts", carts);
		if (!ObjectUtils.isEmpty(carts)) {
			Double amount = carts.get(carts.size()-1).getTotalOrderedAmount();
			m.addAttribute("totalAmount", amount);
		}
		
		
		return "/user/cart";
	}

	private UserDts getLoggedInUserDetails(Principal p) {
		  String name = p.getName();
		   UserDts userDts = userService.getUserDetailsByEmail(name);
		return userDts;
	}
	
	@GetMapping("/cartQuantityUpdate")
	public String updateCartQuantity(@RequestParam String sy,@RequestParam Integer cid) {
		
      cartService.updateQuantity(sy,cid);
		
		return "redirect:/user/cart";
	}
	
	@GetMapping("/orders")
	public String orderPage(Principal p, Model m) {
		
		   
			UserDts user=getLoggedInUserDetails(p);
			List<Cart> carts = cartService.getCartsByUser(user.getId());
			m.addAttribute("carts", carts);
			if (!ObjectUtils.isEmpty(carts)) {
				Double orderPrice = carts.get(carts.size()-1).getTotalOrderedAmount();
				Double amount = orderPrice+250+100;
				m.addAttribute("orderPrice", orderPrice);
				m.addAttribute("totalAmount", amount);
			}
			
		
		return "/user/order";
	}
	
	
	
	@PostMapping("/save-order")
	public String saveOrder(@ModelAttribute OrderRequest orderRequest,Principal principal) throws Exception {
//		System.out.println(orderRequest);
		UserDts user = getLoggedInUserDetails(principal);
		
		orderService.saveOrder(user.getId(), orderRequest);
		return "redirect:/user/success";
	}
	
	@GetMapping("/success")
	public String successPage() {
				
		return "/user/success";
	}
	
	@GetMapping("/myOrders")
	public String myOrders(Model model ,Principal p) {
		UserDts loggedInUser = getLoggedInUserDetails(p);
		List<ProductOrder> orders = orderService.getOrderByUser(loggedInUser.getId());
		model.addAttribute("orders", orders);
				
		return "/user/myOrders";
	}
	
	@GetMapping("/update-status")
	public String updateOrderStatus(@RequestParam Integer id,@RequestParam Integer st,HttpSession session) throws Exception {
		OrderStatus[] values = OrderStatus.values();
		String status=null;
		for (OrderStatus orderStatus : values) {
			if (orderStatus.getId().equals(st)) {
				
				status=orderStatus.getName();
			}
			
			
		}
		ProductOrder status2 = orderService.updateOrderStatus(id, status);
		
		commonUtils.sendMailForProductOrder(status2, status);
		
		if (!ObjectUtils.isEmpty(status2)) {
			 session.setAttribute("succMsg", "Status updated");
		}else {
			session.setAttribute("errorMsg", "Somthing wrong on server !");
		}
		
				
		return "redirect:/user/myOrders";
	}
	
	@GetMapping("/profile")
	public String profile() {
		
		return "/user/profile";
	}
	
	@PostMapping("/update-profile")
	public String updateProfile(@ModelAttribute UserDts user, @RequestParam MultipartFile img,HttpSession session) {
		UserDts updaUserProfile = userService.updaUserProfile(user, img);
		if (ObjectUtils.isEmpty(updaUserProfile)) {
			session.setAttribute("errorMsg", "Somthing wrong on server !");
		}else {
			
			 session.setAttribute("succMsg", "Profile Updated Successfully.");
		}
		
		return "redirect:/user/profile";
	}
	
	@PostMapping("/change-password")
	public String changePassword(@RequestParam String newPassword,@RequestParam String currentPassword,Principal p,HttpSession session) {
		UserDts loggedInUserDetails = getLoggedInUserDetails(p);
		boolean matches = passwordEncoder.matches(currentPassword, loggedInUserDetails.getPassword());
		if (matches) {
			loggedInUserDetails.setPassword(passwordEncoder.encode(newPassword));
			UserDts updaUser = userService.updaUserDts(loggedInUserDetails);
			if (ObjectUtils.isEmpty(updaUser)) {
				session.setAttribute("errorMsg", "Somthing wrong on server !");
				
			}else {
				session.setAttribute("succMsg", "Password Updated Successfully.");
			}
			
		}else {
			session.setAttribute("errorMsg", "Incorrect Current Password!");
		}
		
		return "redirect:/user/profile";
		
	}

}