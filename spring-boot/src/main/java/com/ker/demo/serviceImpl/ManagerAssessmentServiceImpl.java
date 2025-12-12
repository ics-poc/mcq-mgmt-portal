package com.ker.demo.serviceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ker.demo.entity.ReportingMangerEntity;
import com.ker.demo.entity.UserAssessmentTemplateMapEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.entity.UserEvaluationSummaryEntity;
import com.ker.demo.repository.ReportingManagerRepo;
import com.ker.demo.repository.UserAssessmentRepository;
import com.ker.demo.repository.UserEvaluationSummaryRepository;
import com.ker.demo.repository.UserRepo;
import com.ker.demo.service.ManagerAssessmentService;

@Service
@Transactional(readOnly = true)
public class ManagerAssessmentServiceImpl implements ManagerAssessmentService {

	@Autowired
	private UserAssessmentRepository assessmentRepo;

	@Autowired
	private UserEvaluationSummaryRepository evaluationRepo;
	
	@Autowired
	private UserRepo userRepo;
	
	@Autowired
    private ReportingManagerRepo reportingManagerRepo;
	
	private static final Map<Integer, String> STATUS_MAP = Map.of(
	        0, "Scheduled",
	        1, "Pending",
	        2, "Overdue",
	        3, "Completed",
	        4, "Fail"
	    );
	
	@Override
	@Transactional(readOnly = true)
	public List<Map<String, Object>> getDashboardByManager(String managerIdStr) {
	    Long managerId = Long.valueOf(managerIdStr);

	    List<ReportingMangerEntity> mappings = reportingManagerRepo.findAllByManagerId(managerId);
	    if (mappings.isEmpty()) return Collections.emptyList();

	    List<UserAssessmentTemplateMapEntity> assessments = new ArrayList<>();
	    for (ReportingMangerEntity map : mappings) {
	        Long userId = map.getUserId();
	        assessments.addAll(assessmentRepo.findByUserId(userId));
	    }
	    if (assessments.isEmpty()) return Collections.emptyList();

	    Set<Long> userIds = assessments.stream()
	            .map(UserAssessmentTemplateMapEntity::getUserId)
	            .collect(Collectors.toSet());

	    List<UserEntity> users = userRepo.findByUserIdIn(userIds);
	    Map<Long, UserEntity> userMap = users.stream()
	            .collect(Collectors.toMap(UserEntity::getUserId, u -> u));

	    Map<Long, List<UserAssessmentTemplateMapEntity>> groupedByUser =
	            assessments.stream().collect(Collectors.groupingBy(UserAssessmentTemplateMapEntity::getUserId));

	    List<Map<String, Object>> dashboardList = new ArrayList<>();

	    for (var entry : groupedByUser.entrySet()) {
	        Long userId = entry.getKey();
	        UserEntity user = userMap.get(userId);
	        if (user == null) continue;

	        List<UserAssessmentTemplateMapEntity> userAssessments = entry.getValue();

            long totalCount = userAssessments.size();
            long passedCount = userAssessments.stream()
                    .filter(a -> a.getStatus() != null && a.getStatus() == 3)
                    .count();

            List<Map<String, Object>> assessmentHistory = userAssessments.stream().map(uatm -> {
                var catMap = uatm.getManagerTemplateCategoryMap();
                UserEvaluationSummaryEntity evaluationSummary = evaluationRepo.findByUserIdAndManagerTemplateCategoryId(uatm.getUserId(), uatm.getManagerTemplateCategoryMap().getManagerTemplateCategoryId());
                Map<String, Object> history = new LinkedHashMap<>();
                history.put("userAssessmentTemplateId", uatm.getUserAssessmentTemplateId());
                history.put("managerTemplateCategoryId",
                        catMap != null ? catMap.getManagerTemplateCategoryId() : null);
                history.put("templateName", catMap != null && catMap.getManagerTemplate() != null
                        ? catMap.getManagerTemplate().getManagerTemplateName()
                        : null);
                
                if (catMap != null) {
                    history.put("difficultyLevel", catMap.getDifficultyLevel());
                } else {
                    history.put("difficultyLevel", null);
                }

	            List<Map<String, Object>> categoryList = new ArrayList<>();
	            if (catMap != null && catMap.getCategory() != null) {
	                Map<String, Object> categoryObj = new LinkedHashMap<>();

	                Map<String, Object> innerCategory = new LinkedHashMap<>();
	                innerCategory.put("categoryId", catMap.getCategory().getCategoryId());
	                innerCategory.put("categoryName", catMap.getCategory().getCategoryName());
	                innerCategory.put("description", catMap.getCategory().getDescription());

                    categoryObj.put("category", innerCategory);
	                categoryObj.put("weightage", catMap.getWeighage() + "%");

                    categoryList.add(categoryObj);
                }
                history.put("categories", categoryList);

                history.put("score", evaluationSummary != null ? evaluationSummary.getScore() : 0L);

                Integer status = uatm.getStatus();
                String statusText = STATUS_MAP.getOrDefault(status, "Unknown");
                history.put("status", statusText);

                boolean isRetake = (status != null && status == 4);
                history.put("isRetake", isRetake);

                history.put("date", uatm.getAssignedDate());
                return history;
            }).collect(Collectors.toList());

            Map<String, Object> userData = new LinkedHashMap<>();
            userData.put("userId", user.getUserId());
            userData.put("employeeId", user.getEmployeeNumber());
            userData.put("employeeName", user.getFirstName() + " " + user.getLastName());
            userData.put("employeeGrade", user.getEmployeeGrade());
            userData.put("project", user.getProject());
            userData.put("assessments", passedCount + "/" + totalCount);
            userData.put("assessmentHistory", assessmentHistory);

	        dashboardList.add(userData);
	    }
	    return dashboardList;
	}
}
