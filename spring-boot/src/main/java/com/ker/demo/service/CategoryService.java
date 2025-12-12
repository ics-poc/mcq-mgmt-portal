package com.ker.demo.service;

import java.util.List;

import com.ker.demo.domin.Category;

public interface CategoryService {
	
	Category createCategory(Category category);

	Category getCategoryById(Long id);

	List<Category> getAllCategories();

	Category updateCategory(Long id, Category category);

	void deleteCategory(Long id);
}
