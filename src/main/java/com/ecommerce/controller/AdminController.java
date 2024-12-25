package com.ecommerce.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;

import org.apache.catalina.authenticator.SavedRequest;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Category;
import com.ecommerce.model.Product;
import com.ecommerce.model.ProductOrder;
import com.ecommerce.model.UserDts;
import com.ecommerce.repository.ProductOrderRepository;
import com.ecommerce.service.CartService;
import com.ecommerce.service.CategoryService;
import com.ecommerce.service.OrderService;
import com.ecommerce.service.ProductService;
import com.ecommerce.service.UserService;
import com.ecommerce.service.commanService;
import com.ecommerce.utils.CommonUtils;
import com.ecommerce.utils.OrderStatus;

import jakarta.persistence.Id;
import jakarta.persistence.criteria.Path;
import jakarta.servlet.http.HttpSession;
import jakarta.websocket.Session;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private CategoryService categoryService;
	@Autowired
	private ProductService productService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private CartService cartService;
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private CommonUtils commonUtils;
	
	
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

		return "admin/index";

	}

	@GetMapping("/addProduct")
	public String addProduct(Model m) {
		List<Category> categories = categoryService.getAllCategories();
		m.addAttribute("categories", categories);
		return "admin/add_product";

	}

	@GetMapping("/category")
	public String category(Model model) {
		model.addAttribute("cotegorys", categoryService.getAllCategories());

		return "admin/category";

	}

	@PostMapping("/saveCategory")
	public String saveCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {

		String imageName = file != null ? file.getOriginalFilename() : "default.jpg";
		category.setImageName(imageName);

		Boolean existCategory = categoryService.existCategory(category.getName());

		if (existCategory) {
			session.setAttribute("errorMsg", "Cateogry is already exist");
		} else {
			Category saveCategory = categoryService.saveCategory(category);
			if (org.springframework.util.ObjectUtils.isEmpty(saveCategory)) {
				session.setAttribute("errorMsg", "Not Saved !");

			} else {
				session.setAttribute("succMsg", "Successfully !");

				File saveFilePath = new ClassPathResource("static/img").getFile();

				java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + "category_img"
						+ File.separator + file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}
		}

		return "redirect:/admin/category";
	}

	@GetMapping("/deleteCategory/{id}")
	public String deleteCategory(@PathVariable int id, HttpSession session) {

		boolean deleteCategory = categoryService.deleteCategory(id);
		if (deleteCategory) {
			session.setAttribute("succMsg", "Successfully Deleted");
		} else {
			session.setAttribute("errorMsg", "Somthing went wrong!");
		}
		return "redirect:/admin/category";

	}

	@GetMapping("/editCategory/{id}")
	public String editCategory(@PathVariable int id, Model m) {
		m.addAttribute("category", categoryService.getCategoryById(id));
		return "admin/edit_category";
	}

	@PostMapping("/updateCategory")
	public String updateCategory(@ModelAttribute Category category, @RequestParam("file") MultipartFile file,
			HttpSession session) throws IOException {
		Category categoryById = categoryService.getCategoryById(category.getId());
		String imageFile = file.isEmpty() ? categoryById.getImageName() : file.getOriginalFilename();
		if (!ObjectUtils.isEmpty(categoryById)) {
			categoryById.setName(category.getName());
			categoryById.setIsActive(category.getIsActive());
			categoryById.setImageName(imageFile);

		}

		Category saveCategory = categoryService.saveCategory(categoryById);
		if (!ObjectUtils.isEmpty(saveCategory)) {
			if (!file.isEmpty()) {

				File saveFilePath = new ClassPathResource("static/img").getFile();

				java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + "category_img"
						+ File.separator + file.getOriginalFilename());
				System.out.println(path);
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			}
			session.setAttribute("succMsg", "Updated successfully");
		} else {
			session.setAttribute("errorMsg", "Somthing went wrong!");
		}
		return "redirect:/admin/editCategory/" + category.getId();
	}

	@PostMapping("/saveProduct")
	public String saveProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session) throws IOException {
		String imageName = image.isEmpty() ? "default.jpg" : image.getOriginalFilename();
		product.setImage(imageName);
		product.setDiscount(0);
		product.setDiscountPrice(product.getPrice());
		Product saveProduct = productService.saveProduct(product);

		if (!ObjectUtils.isEmpty(saveProduct)) {
			File saveFilePath = new ClassPathResource("static/img").getFile();

			java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + "product_img"
					+ File.separator + image.getOriginalFilename());
			System.out.println(path);
			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			session.setAttribute("succMsg", "Product added successfully");
		} else {
			session.setAttribute("errorMsg", "Something went Wrong");
		}

		return "redirect:/admin/addProduct";
	}

	@GetMapping("/loadProduct")
	public String loadProduct(Model m) {
		m.addAttribute("products", productService.getAllProduct());

		return "admin/products";
	}

	@GetMapping("/deleteProduct/{id}")
	public String deleteProduct(@PathVariable int id, HttpSession session) {
		Boolean deleteProduct = productService.deleteProduct(id);
		if (deleteProduct) {
			session.setAttribute("succMsg", "Product deleted successfully");
		} else {
			session.setAttribute("errorMsg", "Something went wrong!");

		}
		return "redirect:/admin/loadProduct";
	}

	@GetMapping("/editProduct/{id}")
	public String editProduct(@PathVariable int id, Model m) {
		m.addAttribute("product", productService.getProductById(id));
		m.addAttribute("categories", categoryService.getAllCategories());

		return "admin/edit_products";
	}

	@PostMapping("/updateProduct")
	public String updateProduct(@ModelAttribute Product product, @RequestParam("file") MultipartFile image,
			HttpSession session, Model m) {
		if (product.getDiscount() < 0 || product.getDiscount() > 100) {
			session.setAttribute("errorMsg", "Invaild Discount");
		} else {

			Product updateProduct = productService.updateProduct(product, image);
			if (!ObjectUtils.isEmpty(updateProduct)) {
				session.setAttribute("succMsg", "Product updated successfully");

			} else {
				session.setAttribute("errorMsg", "Something went wrong!");
			}

		}
		return "redirect:/admin/editProduct/" + product.getId();
	}
	
	@GetMapping("/users")
	public String getAllUsers(Model m) {
		List<UserDts> users = userService.getAllUsers("ROLE_USER");
		m.addAttribute("users", users);
		return "/admin/users";
		
	}
	
	@GetMapping("/updateSts")
	public String updateUserAccountStatus(@RequestParam Boolean status,@RequestParam Integer id,HttpSession session) {
		
	Boolean f=	userService.updateAccountStatus(id,status);
	
	if (f) {
		session.setAttribute("succMsg", "Account Status Updated");
	}else {
		session.setAttribute("errorMsg", "Something went wrong!");
	}
		
		return "redirect:/admin/users";
	}
	
	@GetMapping("/orders")
	public String getAllOrders(Model model) {
		
		List<ProductOrder> allOrder = orderService.getAllOrder();
		model.addAttribute("orders", allOrder);
		model.addAttribute("srch",false);

		return "/admin/orders";

	}
	
	@PostMapping("/update-order-status")
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
		
				
		return "redirect:/admin/orders";
	}
	
	@GetMapping("/search-order")
	public String searchProduct(@RequestParam String orderId,Model m,HttpSession session) {
		
		if (orderId.length()!=0) {
		
		ProductOrder order = orderService.getOrderByOrderId(orderId.trim());
		if (ObjectUtils.isEmpty(order)) {
			session.setAttribute("errorMsg", "Order ID Incorrect !");
		}else {
          m.addAttribute("orderDtls", order);
		}
		m.addAttribute("srch", true);
		}else {
			List<ProductOrder> allOrder = orderService.getAllOrder();
			m.addAttribute("orders", allOrder);
			m.addAttribute("srch",false);
		}
		
		return "/admin/orders";
	}
}
