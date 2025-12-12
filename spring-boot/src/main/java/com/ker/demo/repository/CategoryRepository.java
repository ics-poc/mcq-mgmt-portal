package com.ker.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.CategoryEntity;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

	CategoryEntity findByCategoryName(String categoryName);

	Optional<CategoryEntity> findByCategoryNameAndSubCategoryNameAndApplicationAreaAndSkillLevelAndReference(
			String area, String subArea, String applicationArea, String level, String reference);
}
