package com.ker.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ker.demo.domin.Assessment;
import com.ker.demo.entity.AdminTemplateCategoryMapEntity;
import com.ker.demo.entity.AdminTemplateEntity;
import com.ker.demo.entity.ManagerTemplateCategoryMapEntity;
import com.ker.demo.entity.ManagerTemplateEntity;
import com.ker.demo.entity.UserAssessmentTemplateMapEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.repository.AdminTemplateCategoryMapRepository;
import com.ker.demo.repository.AdminTemplateRepository;
import com.ker.demo.repository.ManagerTemplateCategoryMapRepository;
import com.ker.demo.repository.ManagerTemplateRepository;
import com.ker.demo.repository.UserAssessmentTemplateMapRepository;
import com.ker.demo.repository.UserRepo;
import com.ker.demo.service.AssessmentService;

@Service
public class AssessmentServiceImpl implements AssessmentService {

    @Autowired
    private AdminTemplateRepository adminTemplateRepository;

    @Autowired
    private AdminTemplateCategoryMapRepository adminTemplateCategoryMapRepository;

    @Autowired
    private ManagerTemplateRepository managerTemplateRepository;

    @Autowired
    private ManagerTemplateCategoryMapRepository managerTemplateCategoryMapRepository;

    @Autowired
    private UserAssessmentTemplateMapRepository userAssessmentTemplateMapRepository;

    @Autowired
    private UserRepo userRepo;

    @Transactional
    public Assessment createAssessment(Assessment assessment) {
        try {
            // Validate input parameters
            validateAssessmentRequest(assessment);
            
            // Step 1: Get AdminTemplate by templateId (adminTemplateId)
            Optional<AdminTemplateEntity> adminTemplateOpt = adminTemplateRepository.findById(assessment.getTemplateId());
            if (adminTemplateOpt.isEmpty()) {
                throw new RuntimeException("AdminTemplate not found with ID: " + assessment.getTemplateId());
            }
            AdminTemplateEntity adminTemplate = adminTemplateOpt.get();
            assessment.setTemplateName(adminTemplate.getAdminTemplateName());

            // Step 2: Create ManagerTemplate from AdminTemplate data
            ManagerTemplateEntity managerTemplate = new ManagerTemplateEntity();
            managerTemplate.setManagerTemplateName(adminTemplate.getAdminTemplateName());
            managerTemplate.setManagerId(assessment.getManagerId()); // managerId from request -> manager_template.manager_id
            managerTemplate.setDescription(adminTemplate.getDescription());
            managerTemplate.setSkillLevel(assessment.getSkillLevel()); // skillLevel from request -> manager_template.skill_level
            managerTemplate.setQuestionCount(Integer.parseInt(assessment.getQuestionCount())); // questionCount from request -> manager_template.question_count
            managerTemplate.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
            managerTemplate.setCreatedUserId(assessment.getManagerId().toString());
            
            // Save ManagerTemplate and get the generated ID
            managerTemplate = managerTemplateRepository.save(managerTemplate);

            // Step 3: Copy admin_template_category_map entries to manager_template_category_map
            List<AdminTemplateCategoryMapEntity> adminCategoryMaps = 
                    adminTemplateCategoryMapRepository.findByAdminTemplateId(assessment.getTemplateId());

            if (adminCategoryMaps.isEmpty()) {
                throw new RuntimeException("No category mappings found for AdminTemplate ID: " + assessment.getTemplateId());
            }

            for (AdminTemplateCategoryMapEntity adminCategoryMap : adminCategoryMaps) {
                ManagerTemplateCategoryMapEntity managerCategoryMap = new ManagerTemplateCategoryMapEntity();
                managerCategoryMap.setManagerTemplateId(managerTemplate.getManagerTemplateId());
                managerCategoryMap.setCategoryId(adminCategoryMap.getCategoryId());
                managerCategoryMap.setWeighage(adminCategoryMap.getWeighage());
                managerCategoryMap.setDifficultyLevel(adminCategoryMap.getDifficultyLevel());
                managerCategoryMap.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));
                managerCategoryMap.setCreatedUserId(assessment.getManagerId().toString());
                managerTemplateCategoryMapRepository.save(managerCategoryMap);
            }

            // Step 4: Save candidate mappings in user_assessment_template_map
            if (assessment.getUserIds() == null || assessment.getUserIds().isEmpty()) {
                throw new RuntimeException("Candidate IDs cannot be null or empty");
            }

            // Get the first category mapping for the manager template
            List<ManagerTemplateCategoryMapEntity> categoryMaps = 
                    managerTemplateCategoryMapRepository.findByManagerTemplateId(managerTemplate.getManagerTemplateId());
            
            if (categoryMaps.isEmpty()) {
                throw new RuntimeException("No category mappings found for ManagerTemplate ID: " + managerTemplate.getManagerTemplateId());
            }
            
            Long managerTemplateCategoryId = categoryMaps.get(0).getManagerTemplateCategoryId();

            for (Long userId : assessment.getUserIds()) {
                UserAssessmentTemplateMapEntity userMap = new UserAssessmentTemplateMapEntity();
                userMap.setUserId(userId ); // candidateIds from request -> user_assessment_template_map.user_id
                ManagerTemplateCategoryMapEntity managerTemplateCategory = new ManagerTemplateCategoryMapEntity();
                managerTemplateCategory.setManagerTemplateCategoryId(managerTemplateCategoryId);
                userMap.setManagerTemplateCategoryMap(managerTemplateCategory);
                userMap.setAssignedBy(assessment.getManagerId().toString()); // Default assigned by
                userMap.setAssignedDate(Timestamp.valueOf(LocalDateTime.now()));
                userMap.setTimeLimit(Integer.parseInt(assessment.getTimeLimitMinutes())); // timeLimitMinutes from request -> user_assessment_template_map.time_limit
                userMap.setScheduleDate(Timestamp.valueOf(assessment.getScheduledAt())); // scheduledAt from request -> user_assessment_template_map.schedule_date
                userAssessmentTemplateMapRepository.save(userMap);
            }
            List<UserEntity> users = userRepo.findByUserIdIn(
            	    new HashSet<>(assessment.getUserIds())
            	);


            List<String> userFullNames = users.stream()
                .map(u -> u.getFirstName() + " " + u.getLastName())
                .toList();

            assessment.setUserIds(null);
            assessment.setUserNames(userFullNames);
            return assessment;
            
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid question count format: " + assessment.getQuestionCount(), e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating assessment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validates the assessment request parameters
     */
    private void validateAssessmentRequest(Assessment assessment) {
        if (assessment == null) {
            throw new RuntimeException("Assessment request cannot be null");
        }
        if (assessment.getTemplateId() == null) {
            throw new RuntimeException("Template ID is required");
        }
        if (assessment.getManagerId() == null) {
            throw new RuntimeException("Manager ID is required");
        }
        if (assessment.getQuestionCount() == null || assessment.getQuestionCount().trim().isEmpty()) {
            throw new RuntimeException("Question count is required");
        }
//        if (assessment.getSkillLevel() == null || assessment.getSkillLevel().trim().isEmpty()) {
//            throw new RuntimeException("Skill level is required");
//        }
        if (assessment.getScheduledAt() == null) {
            throw new RuntimeException("Scheduled date is required");
        }
        if (assessment.getTimeLimitMinutes() == null || assessment.getTimeLimitMinutes().trim().isEmpty()) {
            throw new RuntimeException("Time limit is required");
        }
        
        // Validate question count is a positive integer
        try {
            int questionCount = Integer.parseInt(assessment.getQuestionCount());
            if (questionCount <= 0) {
                throw new RuntimeException("Question count must be a positive integer");
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Question count must be a valid integer");
        }
        
        // Validate scheduled date is not in the past
        if (assessment.getScheduledAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Scheduled date cannot be in the past");
        }
    }
}
