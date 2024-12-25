package com.ecommerce.service;

import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.ecommerce.model.Category;
import com.ecommerce.repository.CategoryRepository;

@Service
public class CategoryServiceImpl implements CategoryService {
	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {
		Category cat = categoryRepository.save(category);
		
		return cat;
	}

	@Override
	public List<Category> getAllCategories() {
		
		return categoryRepository.findAll();
	}

	@Override
	public Boolean existCategory(String name) {
		
		return categoryRepository.existsByName(name);
	}

	@Override
	public boolean deleteCategory(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(category)) {
			categoryRepository.delete(category);
			return true;
		}
		
		
		return false;
		
	}

	@Override
	public Category getCategoryById(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		
		return category;
	}

	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = categoryRepository.findByIsActiveTrue();
		return categories;
	}
	
	
	
	
		
	

}
