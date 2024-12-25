package com.ecommerce.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;

import ch.qos.logback.classic.turbo.TurboFilter;

@Service
public class ProductServiceImpl implements ProductService {
	
	@Autowired
    ProductRepository productRepository;
	
	@Override
	public Product saveProduct(Product product) {
		
		
		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProduct() {
		List<Product> allProducts = productRepository.findAll();
		return allProducts;
	}

	@Override
	public Boolean deleteProduct(Integer id) {
		Product product = productRepository.findById(id).orElse(null);
		if(!ObjectUtils.isEmpty(product)) {
			productRepository.delete(product);
			return true;
		}
		return false ;
	}

	@Override
	public Product getProductById(int id) {
		Product product = productRepository.findById(id).orElse(null);
		return product;
	}

	@Override
	public Product updateProduct(Product product, MultipartFile image)  {

		 Product dProduct = getProductById(product.getId());
		String imageName= image.isEmpty()? dProduct.getImage(): image.getOriginalFilename();
		dProduct.setTitle(product.getTitle());
		dProduct.setDescription(product.getDescription());
		dProduct.setPrice(product.getPrice());
		dProduct.setStock(product.getStock());
		dProduct.setImage(imageName);
		dProduct.setIsActive(product.getIsActive());
		dProduct.setDiscount(product.getDiscount());
		Double discountDouble=(product.getPrice()*(product.getDiscount()))/100.0;
		dProduct.setDiscountPrice(product.getPrice()-discountDouble);
		
		Product updateProduct = productRepository.save(dProduct);
		if (!ObjectUtils.isEmpty(updateProduct)) {
			if (!image.isEmpty()) {
				try {
					File saveFilePath = new ClassPathResource("static/img").getFile();

					java.nio.file.Path path = Paths.get(saveFilePath.getAbsolutePath() + File.separator + "product_img"
							+ File.separator + image.getOriginalFilename());
					
					Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				} catch (Exception e) {
					e.printStackTrace();
				}
           
				
			}
			
			return product;
			
		}

		
		return null;
	}

	@Override
	public List<Product> getAllActiveProduct(String gategory) {
		List<Product> products =null;
		if(ObjectUtils.isEmpty(gategory)) {
			products= productRepository.findByIsActiveTrue();
		}else {
			products=productRepository.findByCategory(gategory);
		}
		
		return products;
	}

	@Override
	public List<Product> searchProduct(String ch) {
		
		
	      return	productRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch);
		
	}

}
