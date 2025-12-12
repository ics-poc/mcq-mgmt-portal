package com.ker.demo.serviceImpl;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ker.demo.domin.Status;
import com.ker.demo.entity.ManagerTemplateCategoryMapEntity;
import com.ker.demo.entity.UserAssessmentTemplateMapEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.entity.UserEvaluationSummaryEntity;
import com.ker.demo.repository.UserAssessmentRepository;
import com.ker.demo.repository.UserEvaluationSummaryRepository;
import com.ker.demo.repository.UserRepo;
import com.ker.demo.service.UserAssessmentService;

@Service
public class UserAssessmentServiceImpl implements UserAssessmentService {

	@Autowired
	private UserAssessmentRepository userAssessmentRepository;

	@Autowired
	private UserEvaluationSummaryRepository evaluationRepo;
	
	@Autowired
	private UserRepo userRepo;

	private static final Map<Integer, String> STATUS_MAP = Map.of(0, "Scheduled", 1, "Pending", 2, "Overdue", 3, "Completed",
			4, "Fail");

	@Override
	@Transactional
	public List<Map<String, Object>> getAllAssessmentsForUser(Long userId) {
		List<UserAssessmentTemplateMapEntity> entities = userAssessmentRepository.findByUserId(userId);

		if (entities.isEmpty()) {
			throw new RuntimeException("No assessments found for userId: " + userId);
		}

		return entities.stream().map(entity -> {
			Map<String, Object> assessmentMap = new LinkedHashMap<>();

			assessmentMap.put("userAssessmentTemplateId", entity.getUserAssessmentTemplateId());
			assessmentMap.put("userId", entity.getUserId());
			assessmentMap.put("assignedBy", entity.getAssignedBy());
			assessmentMap.put("assignedDate", entity.getAssignedDate());
			assessmentMap.put("scheduleDate", entity.getScheduleDate());

			ManagerTemplateCategoryMapEntity mtc = entity.getManagerTemplateCategoryMap();
			if (mtc != null) {
				assessmentMap.put("managerTemplateCategoryId", mtc.getManagerTemplateCategoryId());
				assessmentMap.put("weighage", mtc.getWeighage());
				assessmentMap.put("difficultyLevel", mtc.getDifficultyLevel());
				assessmentMap.put("managerTemplateId", mtc.getManagerTemplateId());
				assessmentMap.put("managerTemplateName",
						mtc.getManagerTemplate() != null ? mtc.getManagerTemplate().getManagerTemplateName() : null);

				if (mtc.getCategory() != null) {
					Map<String, Object> categoryDetails = new LinkedHashMap<>();
					categoryDetails.put("categoryId", mtc.getCategory().getCategoryId());
					categoryDetails.put("categoryName", mtc.getCategory().getCategoryName());
					categoryDetails.put("description", mtc.getCategory().getDescription());

					assessmentMap.put("category",
							List.of(Map.of("categoryName", categoryDetails, "weightage", mtc.getWeighage() + "%")));
				} else {
					assessmentMap.put("category", Collections.emptyList());
				}
			}

			UserEvaluationSummaryEntity evaluationSummary = evaluationRepo.findByUserIdAndManagerTemplateCategoryId(
					entity.getUserId(), entity.getManagerTemplateCategoryMap().getManagerTemplateCategoryId());
			assessmentMap.put("score", evaluationSummary != null ? evaluationSummary.getScore() : 0L);

			Integer status = entity.getStatus();
			String statusText = STATUS_MAP.getOrDefault(status, "Unknown");
			assessmentMap.put("status", statusText);

			boolean isRetake = (status != null && status == 4);
			assessmentMap.put("isRetake", isRetake);

			return assessmentMap;
		}).collect(Collectors.toList());
	}
	
	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getAllUserAssessmentCountsByManager(Long managerId) {
	    List<UserEntity> users = userRepo.findByReportingManagerId(managerId);

	    if (users.isEmpty()) {
	        return Map.of("managerId", managerId, "userIds", List.of(), "statusCounts", Map.of());
	    }

	    List<Long> userIds = users.stream().map(UserEntity::getUserId).toList();

	    List<UserAssessmentTemplateMapEntity> entities = userAssessmentRepository.findAllByUserIdIn(userIds);

	    if (entities.isEmpty()) {
	        return Map.of("managerId", managerId, "userIds", userIds, "statusCounts", Map.of());
	    }

	    Map<String, Long> mergedCountMap = entities.stream()
	            .collect(Collectors.groupingBy(
	                    e -> STATUS_MAP.getOrDefault(e.getStatus(), "Unknown"),
	                    Collectors.counting()
	            ));

	    Map<String, Long> orderedMap = new LinkedHashMap<>();
	    for (int i = 0; i <= 4; i++) {
	        String label = STATUS_MAP.get(i);
	        orderedMap.put(label, mergedCountMap.getOrDefault(label, 0L));
	    }

	    Map<String, Object> response = new LinkedHashMap<>();
	    response.put("managerId", managerId);
	    response.put("userIds", userIds);
	    response.put("statusCounts", orderedMap);

	    return response;
	}

}
