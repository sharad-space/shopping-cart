package com.ecommerce.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Product;

@Service
public interface ProductService {
	
	public Product saveProduct(Product product);
	
	public List<Product> getAllProduct();
	
	public Boolean deleteProduct(Integer id);
	
	public Product getProductById(int id);
	
	public Product updateProduct(Product product,MultipartFile file);
	
	public List<Product> getAllActiveProduct(String category);
	
	public List<Product> searchProduct(String ch);
	
	

}
