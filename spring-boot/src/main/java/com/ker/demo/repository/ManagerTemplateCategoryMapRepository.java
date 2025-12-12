package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.ManagerTemplateCategoryMapEntity;

public interface ManagerTemplateCategoryMapRepository extends JpaRepository<ManagerTemplateCategoryMapEntity, Long> {
	
	List<ManagerTemplateCategoryMapEntity> findByManagerTemplateId(Long id);
	
}