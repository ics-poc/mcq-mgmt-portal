package com.ker.demo.serviceImpl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ker.demo.domin.Category;
import com.ker.demo.domin.ManagerTemplate;
import com.ker.demo.domin.ManagerTemplateCategoryMap;
import com.ker.demo.entity.CategoryEntity;
import com.ker.demo.entity.ManagerTemplateCategoryMapEntity;
import com.ker.demo.entity.ManagerTemplateEntity;
import com.ker.demo.exception.ValidationException;
import com.ker.demo.repository.CategoryRepository;
import com.ker.demo.repository.ManagerTemplateCategoryMapRepository;
import com.ker.demo.repository.ManagerTemplateRepository;
import com.ker.demo.service.ManagerTemplateService;

@Service
public class ManagerTemplateServiceImpl implements ManagerTemplateService {

	@Autowired
	private ManagerTemplateRepository managerTemplateRepository;

	@Autowired
	private ManagerTemplateCategoryMapRepository managerTemplateCategoryMapRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public ManagerTemplate createManagerTemplate(ManagerTemplate template) {
		validateTemplate(template);

		ManagerTemplateEntity savedEntity = managerTemplateRepository.save(mapToEntity(template));
		List<ManagerTemplateCategoryMap> categoryList = new ArrayList<>();

		if (template.getManagerTemplateCategoryMap() != null) {
			for (ManagerTemplateCategoryMap map : template.getManagerTemplateCategoryMap()) {
				validateCategoryId(map.getCategoryId());
				ManagerTemplateCategoryMapEntity savedMap = managerTemplateCategoryMapRepository
						.save(mapToEntity(map, savedEntity));
				categoryList.add(mapToDomain(savedMap));
			}
		}

		return mapToDomain(savedEntity, categoryList);
	}

	private void validateTemplate(ManagerTemplate template) {
		if (template.getManagerTemplateName() == null || template.getManagerTemplateName().isBlank()) {
			throw new ValidationException("Field 'managerTemplateName' is required");
		}
		if (template.getCreatedUserId() == null || template.getCreatedUserId().isBlank()) {
			throw new ValidationException("Field 'createdUserId' is required");
		}
		if (template.getSkillLevel() == null || template.getSkillLevel().isBlank()) {
			throw new ValidationException("Field 'skillLevel' is required");
		}
	}

	private void validateCategoryId(Long categoryId) {
		if (!categoryRepository.existsById(categoryId)) {
			throw new ValidationException("Category ID " + categoryId + " is not present");
		}
	}

	@Override
	public ManagerTemplate getManagerTemplateWithCategories(Long id) {
		ManagerTemplateEntity entity = managerTemplateRepository.findById(id)
				.orElseThrow(() -> new ValidationException("ManagerTemplate not found with id: " + id));

		List<ManagerTemplateCategoryMapEntity> mappings = managerTemplateCategoryMapRepository
				.findByManagerTemplateId(id);

		List<ManagerTemplateCategoryMap> categoryList = mappings.stream().map(this::mapToDomain)
				.collect(Collectors.toList());

		return mapToDomain(entity, categoryList);
	}

	@Override
	public void deleteManagerTemplate(Long id) {
		ManagerTemplateEntity entity = managerTemplateRepository.findById(id)
				.orElseThrow(() -> new ValidationException("ManagerTemplate not found with id: " + id));

		managerTemplateRepository.delete(entity);
	}

	@Override
	public ManagerTemplate updateManagerTemplate(Long id, ManagerTemplate updatedTemplate) {
	    ManagerTemplateEntity entity = managerTemplateRepository.findById(id)
	        .orElseThrow(() -> new ValidationException("ManagerTemplate not found with id: " + id));

	    // Update basic fields
	    Optional.ofNullable(updatedTemplate.getManagerTemplateName()).ifPresent(entity::setManagerTemplateName);
	    Optional.ofNullable(updatedTemplate.getDescription()).ifPresent(entity::setDescription);
	    Optional.ofNullable(updatedTemplate.getSkillLevel()).ifPresent(entity::setSkillLevel);
	    entity.setModifiedUserId(updatedTemplate.getModifiedUserId());
	    entity.setModifiedDate(new Timestamp(System.currentTimeMillis()));
	    managerTemplateRepository.save(entity);

	    // Fetch authoritative mappings from DB
	    List<ManagerTemplateCategoryMapEntity> dbMappings = managerTemplateCategoryMapRepository.findByManagerTemplateId(id);

	    // Apply updates using DB-backed category IDs
	    if (updatedTemplate.getManagerTemplateCategoryMap() != null) {
	        for (int i = 0; i < dbMappings.size(); i++) {
	            ManagerTemplateCategoryMapEntity dbMap = dbMappings.get(i);

	            // Match update by index or custom logic if needed
	            if (i < updatedTemplate.getManagerTemplateCategoryMap().size()) {
	                ManagerTemplateCategoryMap updateMap = updatedTemplate.getManagerTemplateCategoryMap().get(i);

	                Optional.ofNullable(updateMap.getDifficultyLevel()).ifPresent(dbMap::setDifficultyLevel);
	                Optional.ofNullable(updateMap.getWeighage()).ifPresent(dbMap::setWeighage);
	                dbMap.setModifiedUserId(updateMap.getModifiedUserId());
	                dbMap.setModifiedDate(new Timestamp(System.currentTimeMillis()));

	                managerTemplateCategoryMapRepository.save(dbMap);
	            }
	        }
	    }
	    return getManagerTemplateWithCategories(id);
	}

	@Override
	public List<ManagerTemplate> getAllManagerTemplates() {
		List<ManagerTemplateEntity> entities = managerTemplateRepository.findAll();
		List<ManagerTemplate> templates = new ArrayList<>();

		for (ManagerTemplateEntity entity : entities) {
			List<ManagerTemplateCategoryMapEntity> mappings = managerTemplateCategoryMapRepository
					.findByManagerTemplateId(entity.getManagerTemplateId());

			List<ManagerTemplateCategoryMap> categoryList = mappings.stream().map(this::mapToDomain)
					.collect(Collectors.toList());

			templates.add(mapToDomain(entity, categoryList));
		}
		return templates;
	}

	private ManagerTemplateEntity mapToEntity(ManagerTemplate template) {
		ManagerTemplateEntity entity = new ManagerTemplateEntity();
		entity.setManagerTemplateName(template.getManagerTemplateName());
		entity.setDescription(template.getDescription());
		entity.setSkillLevel(template.getSkillLevel());
		entity.setCreatedUserId(template.getCreatedUserId());
		entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		entity.setModifiedUserId(template.getModifiedUserId());
		entity.setModifiedDate(template.getModifiedDate());
		entity.setManagerId(Objects.requireNonNull(template.getManagerId(), "Manager ID cannot be null"));
		entity.setQuestionCount(Objects.requireNonNull(template.getQuestionCount(), "Question count cannot be null"));
		return entity;
	}

	private ManagerTemplateCategoryMapEntity mapToEntity(ManagerTemplateCategoryMap map,
														 ManagerTemplateEntity templateEntity) {
		ManagerTemplateCategoryMapEntity entity = new ManagerTemplateCategoryMapEntity();
		entity.setManagerTemplate(templateEntity); // ✅ Set relationship
		entity.setCategoryId(map.getCategoryId());
		entity.setWeighage(map.getWeighage());
		entity.setDifficultyLevel(map.getDifficultyLevel());
		entity.setCreatedUserId(map.getCreatedUserId());
		entity.setCreatedDate(new Timestamp(System.currentTimeMillis()));
		entity.setModifiedUserId(map.getModifiedUserId());
		entity.setModifiedDate(map.getModifiedDate());
		return entity;
	}

	private ManagerTemplate mapToDomain(ManagerTemplateEntity entity, List<ManagerTemplateCategoryMap> categories) {
		ManagerTemplate template = new ManagerTemplate();
		template.setManagerTemplateId(entity.getManagerTemplateId());
		template.setManagerTemplateName(entity.getManagerTemplateName());
		template.setDescription(entity.getDescription());
		template.setSkillLevel(entity.getSkillLevel());
		template.setCreatedDate(entity.getCreatedDate());
		template.setModifiedDate(entity.getModifiedDate());
		template.setManagerTemplateCategoryMap(categories);
		return template;
	}

	private ManagerTemplateCategoryMap mapToDomain(ManagerTemplateCategoryMapEntity entity) {
		ManagerTemplateCategoryMap map = new ManagerTemplateCategoryMap();
		map.setManagerTemplateCategoryId(entity.getManagerTemplateCategoryId());
		map.setManagerTemplateId(entity.getManagerTemplate().getManagerTemplateId()); // ✅ Use relationship
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
