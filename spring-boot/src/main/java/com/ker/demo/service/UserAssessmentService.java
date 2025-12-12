package com.ker.demo.service;

import java.util.List;
import java.util.Map;

import com.ker.demo.domin.Status;

public interface UserAssessmentService {

	List<Map<String, Object>> getAllAssessmentsForUser(Long userId);
	
	Map<String, Object> getAllUserAssessmentCountsByManager(Long managerId);

}
