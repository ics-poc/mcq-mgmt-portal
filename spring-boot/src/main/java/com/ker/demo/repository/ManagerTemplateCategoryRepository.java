package com.ker.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.ManagerTemplateCategoryEntity;

public interface ManagerTemplateCategoryRepository extends JpaRepository<ManagerTemplateCategoryEntity, Long> {
}