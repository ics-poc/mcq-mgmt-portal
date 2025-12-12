package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.UserEvaluationSummaryEntity;

public interface UserEvaluationSummaryRepository extends JpaRepository<UserEvaluationSummaryEntity, Long> {

	List<UserEvaluationSummaryEntity> findByUserId(Long userId);
	
	UserEvaluationSummaryEntity findByUserIdAndManagerTemplateCategoryId(Long userId, Long managerTemplateCategoryId);

}
