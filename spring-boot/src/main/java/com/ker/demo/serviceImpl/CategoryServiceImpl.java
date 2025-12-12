package com.ker.demo.serviceImpl;

import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ker.demo.domin.Category;
import com.ker.demo.domin.ReviewStatus;
import com.ker.demo.entity.CategoryEntity;
import com.ker.demo.exception.ResourceNotFoundException;
import com.ker.demo.repository.CategoryRepository;
import com.ker.demo.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category createCategory(Category category) {
		if (category.getCategoryName() == null || category.getCategoryName().trim().isEmpty()) {
			throw new IllegalArgumentException("Category name is required");
		}

		if (category.getCreatedUserId() == null || category.getCreatedUserId().trim().isEmpty()) {
			throw new ResourceNotFoundException("Created user ID is required");
		}

		CategoryEntity entity = new CategoryEntity();
		entity.setCategoryName(category.getCategoryName());
	    entity.setSubCategoryName(category.getSubCategoryName()); 
		entity.setDescription(category.getDescription());
		entity.setCreatedUserId(category.getCreatedUserId());
		entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
	    entity.setApplicationArea(category.getApplicationArea());
	    entity.setSkillLevel(category.getSkillLevel());
	    entity.setReference(category.getReference()); 

		CategoryEntity saved = categoryRepository.save(entity);
	    categoryRepository.flush(); 
		return mapToDomain(saved);
	}

	@Override
	public Category getCategoryById(Long id) {
		CategoryEntity entity = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("CategoryId does not exist"));
		return mapToDomain(entity);
	}

	@Override
	public List<Category> getAllCategories() {
		return categoryRepository.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
	}

	@Override
	public Category updateCategory(Long id, Category category) {
		CategoryEntity existing = categoryRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("CategoryId does not exist"));

		if (category.getCategoryName() != null && !category.getCategoryName().trim().isEmpty()) {
			existing.setCategoryName(category.getCategoryName());
		}
		existing.setDescription(category.getDescription());
		if (StringUtils.isNoneBlank(category.getModifiedUserId())) {
			existing.setModifiedUserId(category.getModifiedUserId());
		}
		existing.setModifiedDate(new Timestamp(System.currentTimeMillis()));

		CategoryEntity updated = categoryRepository.save(existing);
		return mapToDomain(updated);
	}

	@Override
	public void deleteCategory(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResourceNotFoundException("CategoryId does not exist");
		}
		categoryRepository.deleteById(id);
	}

	private Category mapToDomain(CategoryEntity entity) {
	    Category category = new Category();
	    category.setCategoryId(entity.getCategoryId()); 
	    category.setCategoryName(entity.getCategoryName());
	    category.setSubCategoryName(entity.getSubCategoryName());
	    category.setDescription(entity.getDescription());
	    category.setCreatedUserId(entity.getCreatedUserId());
	    category.setApplicationArea(entity.getApplicationArea());
	    category.setSkillLevel(entity.getSkillLevel());
	    category.setReference(entity.getReference());
	    category.setStatus(ReviewStatus.getLabelByCode(entity.getStatus()));
	    return category;
	}
}
