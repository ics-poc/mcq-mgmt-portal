package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.UserAssessmentTemplateMapEntity;

public interface UserAssessmentTemplateMapRepository extends JpaRepository<UserAssessmentTemplateMapEntity, Long> {
    List<UserAssessmentTemplateMapEntity> findByUserId(Long userId);
}
