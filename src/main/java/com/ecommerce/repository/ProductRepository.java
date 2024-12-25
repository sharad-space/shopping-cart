package com.ecommerce.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ecommerce.model.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

	public List<Product> findByIsActiveTrue();

	public List<Product> findByCategory(String gategory);
	
	public List<Product> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,String ch1);

}
