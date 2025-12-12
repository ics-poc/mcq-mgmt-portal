package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.AdminTemplateCategoryMapEntity;

public interface AdminTemplateCategoryMapRepository extends JpaRepository<AdminTemplateCategoryMapEntity, Long> {

	List<AdminTemplateCategoryMapEntity> findByAdminTemplateId(Long id);

}
