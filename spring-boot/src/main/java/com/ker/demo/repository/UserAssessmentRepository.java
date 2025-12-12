package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.UserAssessmentTemplateMapEntity;


@Repository
public interface UserAssessmentRepository extends JpaRepository<UserAssessmentTemplateMapEntity, Long>{

	    List<UserAssessmentTemplateMapEntity> findByUserId(Long userId);
	    
	    List<UserAssessmentTemplateMapEntity> findAllByUserIdIn(List<Long> userIds);
}
