package com.ker.demo.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ker.demo.domin.AdminTemplate;
import com.ker.demo.domin.AdminTemplateCategoryMap;
import com.ker.demo.domin.Category;
import com.ker.demo.entity.AdminTemplateCategoryMapEntity;
import com.ker.demo.entity.AdminTemplateEntity;
import com.ker.demo.entity.CategoryEntity;
import com.ker.demo.exception.ValidationException;
import com.ker.demo.repository.AdminTemplateCategoryMapRepository;
import com.ker.demo.repository.AdminTemplateRepository;
import com.ker.demo.repository.CategoryRepository;
import com.ker.demo.service.AdminTemplateService;

@Service
public class AdminTemplateServiceImpl implements AdminTemplateService {

	@Autowired
	private AdminTemplateRepository adminTemplateRepository;
	@Autowired
	private AdminTemplateCategoryMapRepository categoryMapRepository;
	@Autowired
	private CategoryRepository categoryRepository;

	/*
	 * Create AdminTemplates
	 */
	@Override
	public AdminTemplate createAdminTemplate(AdminTemplate template) {
		validateTemplate(template);

		AdminTemplateEntity savedEntity = adminTemplateRepository.save(mapToEntity(template));
		List<AdminTemplateCategoryMap> categoryList = new ArrayList<>();

		if (template.getAdminTemplateCategoryMap() != null) {
			for (AdminTemplateCategoryMap map : template.getAdminTemplateCategoryMap()) {
				validateCategoryId(map.getCategoryId());
				AdminTemplateCategoryMapEntity savedMap = categoryMapRepository
						.save(mapToEntity(map, savedEntity.getAdminTemplateId()));
				categoryList.add(mapToDomain(savedMap));
			}
		}

		return mapToDomain(savedEntity, categoryList);
	}

	/*
	 * fetch AdminTemplate by Id
	 */
	@Override
	public AdminTemplate getAdminTemplateWithCategories(Long id) {
		AdminTemplateEntity entity = adminTemplateRepository.findById(id)
				.orElseThrow(() -> new ValidationException("AdminTemplate not found with id: " + id));

		List<AdminTemplateCategoryMapEntity> mappings = categoryMapRepository.findByAdminTemplateId(id);
		List<AdminTemplateCategoryMap> categoryList = mappings.stream().map(this::mapToDomain)
				.collect(Collectors.toList());

		return mapToDomain(entity, categoryList);
	}

	/*
	 * delete AdminTemplate by id
	 */
	@Override
	public void deleteAdminTemplate(Long adminTemplateId) {
		AdminTemplateEntity template = adminTemplateRepository.findById(adminTemplateId)
				.orElseThrow(() -> new ValidationException("AdminTemplate not found with id: " + adminTemplateId));

		adminTemplateRepository.delete(template);
	}

	/*
	 * update AdminTemplates
	 */
	@Override
	public AdminTemplate updateAdminTemplate(Long id, AdminTemplate updatedTemplate) {
		AdminTemplateEntity entity = adminTemplateRepository.findById(id)
				.orElseThrow(() -> new ValidationException("AdminTemplate not found with id: " + id));

		// ✅ Update allowed fields
		if (updatedTemplate.getAdminTemplateName() != null) {
			entity.setAdminTemplateName(updatedTemplate.getAdminTemplateName());
		}
		if (updatedTemplate.getDescription() != null) {
			entity.setDescription(updatedTemplate.getDescription());
		}
		entity.setModifiedUserId(updatedTemplate.getModifiedUserId());
		entity.setModifiedDate(new Timestamp(System.currentTimeMillis()));
		adminTemplateRepository.save(entity);

		// ✅ Update category mappings
		if (updatedTemplate.getAdminTemplateCategoryMap() != null) {
			List<AdminTemplateCategoryMapEntity> mappings = categoryMapRepository.findByAdminTemplateId(id);
			Map<Long, AdminTemplateCategoryMapEntity> mapByCategoryId = mappings.stream()
					.collect(Collectors.toMap(AdminTemplateCategoryMapEntity::getCategoryId, Function.identity()));

			for (AdminTemplateCategoryMap updateMap : updatedTemplate.getAdminTemplateCategoryMap()) {
				AdminTemplateCategoryMapEntity existing = mapByCategoryId.get(updateMap.getCategoryId());
				if (existing != null) {
					if (updateMap.getDifficultyLevel() != null) {
						existing.setDifficultyLevel(updateMap.getDifficultyLevel());
					}
					if (updateMap.getWeighage() != null) {
						existing.setWeighage(updateMap.getWeighage());
					}
					existing.setModifiedUserId(updateMap.getModifiedUserId());
					existing.setModifiedDate(new Timestamp(System.currentTimeMillis()));
					categoryMapRepository.save(existing);
				}
			}
		}

		return getAdminTemplateWithCategories(id);
	}

	/*
	 * fetch all AdminTemplates
	 */
	@Override
	public List<AdminTemplate> getAllAdminTemplates() {
		List<AdminTemplateEntity> templates = adminTemplateRepository.findAll();
		List<AdminTemplate> result = new ArrayList<>();

		for (AdminTemplateEntity entity : templates) {
			List<AdminTemplateCategoryMapEntity> mappings = categoryMapRepository
					.findByAdminTemplateId(entity.getAdminTemplateId());
			List<AdminTemplateCategoryMap> categoryList = mappings.stream().map(this::mapToDomain)
					.collect(Collectors.toList());
			result.add(mapToDomain(entity, categoryList));
		}

		return result;
	}

	private void validateTemplate(AdminTemplate template) {
		if (template.getAdminTemplateName() == null || template.getAdminTemplateName().isBlank()) {
			throw new ValidationException("Field 'adminTemplateName' is required");
		}
		if (template.getCreatedUserId() == null || template.getCreatedUserId().isBlank()) {
			throw new ValidationException("Field 'createdUserId' is required");
		}
	}

	private void validateCategoryId(Long categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new ValidationException("Category ID " + categoryId + " is not present");
		}
	}

	private AdminTemplateEntity mapToEntity(AdminTemplate template) {
		AdminTemplateEntity entity = new AdminTemplateEntity();
		entity.setAdminTemplateName(template.getAdminTemplateName());
		entity.setDescription(template.getDescription());
		entity.setCreatedUserId(template.getCreatedUserId());
		entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		entity.setModifiedUserId(template.getModifiedUserId());
		entity.setModifiedDate(template.getModifiedDate());
		return entity;
	}

	private AdminTemplateCategoryMapEntity mapToEntity(AdminTemplateCategoryMap map, Long templateId) {
		AdminTemplateCategoryMapEntity entity = new AdminTemplateCategoryMapEntity();
		entity.setAdminTemplateId(templateId);
		entity.setCategoryId(map.getCategoryId());
		entity.setWeighage(map.getWeighage());
		entity.setDifficultyLevel(map.getDifficultyLevel());
		entity.setCreatedUserId(map.getCreatedUserId());
		entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		entity.setModifiedUserId(map.getModifiedUserId());
		entity.setModifiedDate(map.getModifiedDate());
		return entity;
	}

	private AdminTemplate mapToDomain(AdminTemplateEntity entity, List<AdminTemplateCategoryMap> categories) {
		AdminTemplate template = new AdminTemplate();
		template.setAdminTemplateId(entity.getAdminTemplateId());
		template.setAdminTemplateName(entity.getAdminTemplateName());
		template.setDescription(entity.getDescription());
		template.setCreatedDate(entity.getCreatedDate());
		template.setModifiedDate(entity.getModifiedDate());
		template.setAdminTemplateCategoryMap(categories);
		return template;
	}

	private AdminTemplateCategoryMap mapToDomain(AdminTemplateCategoryMapEntity entity) {
		AdminTemplateCategoryMap map = new AdminTemplateCategoryMap();
		map.setAdminTemplateCategoryId(entity.getAdminTemplateCategoryId());
		map.setAdminTemplateId(entity.getAdminTemplateId());
		map.setCategoryId(entity.getCategoryId());
		map.setWeighage(entity.getWeighage());
		map.setDifficultyLevel(entity.getDifficultyLevel());
		map.setCreatedDate(entity.getCreatedDate());
		map.setModifiedDate(entity.getModifiedDate());

		CategoryEntity categoryEntity = categoryRepository.findById(entity.getCategoryId()).orElseThrow(
				() -> new ValidationException("Category ID " + entity.getCategoryId() + " is not present"));

		Category category = new Category();
		category.setCategoryId(categoryEntity.getCategoryId());
		category.setCategoryName(categoryEntity.getCategoryName());
		category.setDescription(categoryEntity.getDescription());
		category.setCreatedUserId(categoryEntity.getCreatedUserId());
		category.setCreatedDate(categoryEntity.getCreatedDate());
		category.setModifiedUserId(categoryEntity.getModifiedUserId());
		category.setModifiedDate(categoryEntity.getModifiedDate());

		map.setCategory(category);
		return map;
	}
}
