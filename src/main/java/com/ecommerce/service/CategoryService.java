package com.ecommerce.service;

import java.util.List;

import com.ecommerce.model.Category;

public interface CategoryService {
	
	public Category saveCategory(Category category);
	
	public List<Category> getAllCategories();
	
	public Boolean existCategory(String name);
	
	public boolean deleteCategory(int id);
	
	public Category getCategoryById(int id);
	
	public List<Category> getAllActiveCategory();

}
