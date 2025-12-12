package com.ker.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.ManagerTemplateEntity;

public interface ManagerTemplateRepository extends JpaRepository<ManagerTemplateEntity, Long> {
	
}
