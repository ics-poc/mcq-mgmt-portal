package com.ker.demo.service;

import java.util.List;
import java.util.Map;

public interface ManagerAssessmentService {

	List<Map<String, Object>> getDashboardByManager(String managerId);
}
