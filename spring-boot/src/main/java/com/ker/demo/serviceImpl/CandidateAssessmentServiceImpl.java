package com.ker.demo.serviceImpl;
 
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ker.demo.domin.UserEvaluationSummary;
import com.ker.demo.entity.CategoryEntity;
import com.ker.demo.entity.ManagerTemplateCategoryMapEntity;
import com.ker.demo.entity.ManagerTemplateEntity;
import com.ker.demo.entity.QuestionAnswerEntity;
import com.ker.demo.entity.UserAssessmentTemplateMapEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.entity.UserEvaluationSummaryEntity;
import com.ker.demo.entity.UserQuestionAnswerResultsEntity;
import com.ker.demo.repository.CategoryRepository;
import com.ker.demo.repository.ManagerTemplateCategoryMapRepository;
import com.ker.demo.repository.ManagerTemplateRepository;
import com.ker.demo.repository.QuestionAnswerRepository;
import com.ker.demo.repository.UserAssessmentTemplateMapRepository;
import com.ker.demo.repository.UserEvaluationSummaryRepository;
import com.ker.demo.repository.UserQuestionAnswerResultsRepository;
import com.ker.demo.repository.UserRepo;
import com.ker.demo.service.CandidateAssessmentService;
import com.ker.demo.utils.ShuffleController;

import jakarta.transaction.Transactional;
 
@Service
public class CandidateAssessmentServiceImpl implements CandidateAssessmentService {
 
    @Autowired
    private UserRepo userRepo;
 
    @Autowired
    private UserAssessmentTemplateMapRepository userAssessmentRepo;
    
    @Autowired
    private ManagerTemplateCategoryMapRepository managerTemplateCategoryMapRepository;
    
    @Autowired
    private ManagerTemplateRepository managerTemplateRepository;
    
    @Autowired
    private QuestionAnswerRepository questionAnswerRepository;
    
    @Autowired
    private UserQuestionAnswerResultsRepository userAnswersRepo;
    
    @Autowired
    private UserEvaluationSummaryRepository evaluationRepo;
    
    @Autowired
    private CategoryRepository categoryRepository;
 
    @Override
    // ... (imports and class structure omitted for brevity)
    public List<Map<String, Object>> getAllResults(Long userId, Long managerTemplateId) {
        if (userRepo.findById(userId).isEmpty()) {
            throw new NoSuchElementException("Candidate not found.");
        }
 
        List<ManagerTemplateCategoryMapEntity> templateCategories;
        if (managerTemplateId != null) {
            templateCategories = managerTemplateCategoryMapRepository.findByManagerTemplateId(managerTemplateId);
        } else {
            templateCategories = managerTemplateCategoryMapRepository.findAll();
        }
 
        return templateCategories.stream()
            .flatMap(templateCategory -> {
                List<UserEvaluationSummaryEntity> summaries = evaluationRepo.findAll().stream()
                    .filter(s -> s.getUserId().equals(userId) &&
                               s.getManagerTemplateCategoryId().equals(templateCategory.getManagerTemplateCategoryId()))
                    .collect(Collectors.toList());
 
                return summaries.stream().map(s -> {
                    Map<String, Object> result = new HashMap<>();
                    result.put("score", s.getScore());
                    result.put("userEvaluationId", s.getUserEvaluationId());
                   
                    // Get Manager Template info
                    Optional<ManagerTemplateEntity> mtOpt = managerTemplateRepository.findById(templateCategory.getManagerTemplateId());
                    Map<String, Object> managerTemplate = new HashMap<>();
                    mtOpt.ifPresent(mt -> {
                        managerTemplate.put("id", mt.getManagerTemplateId());
                        managerTemplate.put("name", mt.getManagerTemplateName());
                        managerTemplate.put("templateId", mt.getManagerTemplateId());
                        managerTemplate.put("templateName", mt.getManagerTemplateName());
                        managerTemplate.put("skillLevel", mt.getSkillLevel());
                       
                        // Get questions and answers
                        List<Map<String, Object>> questions = new ArrayList<>();
                       
                        // Get all answers for this user and template category
                        List<UserQuestionAnswerResultsEntity> answers =
                            userAnswersRepo.findByUserIdAndManagerTemplateCategoryId(userId, s.getManagerTemplateCategoryId());

                        List<Long> questionIds = answers.stream()
                            .map(UserQuestionAnswerResultsEntity::getQuestionId)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());

                        List<QuestionAnswerEntity> userQuestions = questionIds.isEmpty()
                            ? Collections.emptyList()
                            : questionAnswerRepository.findAllById(questionIds);

                        // Create a map of questionId to answer for quick lookup
                        Map<Long, UserQuestionAnswerResultsEntity> answersByQuestionId = new HashMap<>();
                        for (UserQuestionAnswerResultsEntity answer : answers) {
                            if (answer != null && answer.getQuestionId() != null) {
                                answersByQuestionId.put(answer.getQuestionId(), answer);
                            }
                        }

                        // Map questions with their answers (only user-linked ones)
                        for (QuestionAnswerEntity question : userQuestions) {
                            if (question == null) continue;
                           
                            Map<String, Object> q = new HashMap<>();
                            q.put("questionId", question.getQuestionId());
                            q.put("question", question.getQuestion());

                            try {
                                ObjectMapper mapper = new ObjectMapper();
                                Map<String, Object> optionsMap = mapper.readValue(
                                    question.getOptions(),
                                    new TypeReference<Map<String, Object>>() {}
                                );
                                q.put("options", optionsMap);
                            } catch (Exception e) {
                                q.put("options", question.getOptions());
                            }

                            // Add correct answer
                            String correctAnswer = question.getAnswer();
                            q.put("answer", correctAnswer);

                            // Get the user's answer for this question
                            UserQuestionAnswerResultsEntity userAnswer = answersByQuestionId.get(question.getQuestionId());
                           
                            if (userAnswer != null) {
                                String selectedOption = userAnswer.getSelectedOption();
                                if (selectedOption != null && !selectedOption.trim().isEmpty()) {
                                    q.put("selectedAnswer", selectedOption);
                                   
                                    // Try to convert numeric index to letter format
                                    try {
                                        int index = Integer.parseInt(selectedOption.trim());
                                        if (index >= 0) {
                                            q.put("selectedOptionLetter", String.valueOf((char)('A' + index)));
                                        } else {
                                            q.put("selectedOptionLetter", selectedOption);
                                        }
                                    } catch (NumberFormatException e) {
                                        // If not a number, assume it's already a letter
                                        q.put("selectedOptionLetter", selectedOption);
                                    }
                                } else {
                                    // Initialize selected answer fields with null if empty answer
                                    q.put("selectedOptionLetter", null);
                                    q.put("selectedAnswer", null);
                                }
                            } else {
                                // Initialize selected answer fields with null if no answer found
                                q.put("selectedAnswer", null);
                            }
                            questions.add(q);
                        }
                        managerTemplate.put("questions", questions);
                    });
                   
                    result.put("managerTemplate", managerTemplate);
                    return result;
                });
            })
            .collect(Collectors.toList());
    }




 
    @Override
    public Map<String, Object> getResultById(Long userId, Long evaluationId) {
        if (userRepo.findById(userId).isEmpty())
            throw new NoSuchElementException("Candidate not found.");
 
        Optional<UserEvaluationSummaryEntity> opt = evaluationRepo.findById(evaluationId);
        if (opt.isEmpty() || !opt.get().getUserId().equals(userId))
            throw new NoSuchElementException("Result not found.");
 
        UserEvaluationSummaryEntity summary = opt.get();
        List<UserQuestionAnswerResultsEntity> answers = userAnswersRepo.findByUserIdAndManagerTemplateCategoryId(userId,
                summary.getManagerTemplateCategoryId());
 
        Map<String, Object> resp = new HashMap<>();
        resp.put("summary", new UserEvaluationSummary(summary.getUserEvaluationId(),
                String.valueOf(summary.getUserId()), summary.getManagerTemplateCategoryId(), summary.getScore()));
 
        // Build questions array with questionId, question, options, correct answer, and selected answer
        List<Map<String, Object>> questions = answers.stream().map(a -> {
            Map<String, Object> m = new HashMap<>();
            m.put("questionId", a.getQuestionId());
            QuestionAnswerEntity question = questionAnswerRepository.findById(a.getQuestionId()).orElse(null);
            if (question != null) {
                m.put("question", question.getQuestion());
                m.put("options", question.getOptions());
                m.put("correctAnswer", question.getAnswer());
                m.put("selectedAnswer", a.getSelectedOption());
            } else {
                m.put("selectedAnswer", a.getSelectedOption());
            }
            return m;
        }).collect(Collectors.toList());
 
        resp.put("questions", questions);
        return resp;
    }
 
    @Override
    public List<Map<String, Object>> getAssignedExams(Long userId) {
        Optional<UserEntity> user = userRepo.findById(userId);
        if (user.isEmpty())
            throw new NoSuchElementException("Candidate not found.");
 
        List<UserAssessmentTemplateMapEntity> assignments = userAssessmentRepo.findByUserId(userId);
        List<Map<String, Object>> result = new ArrayList<>();
 
        for (UserAssessmentTemplateMapEntity a : assignments) {
            Optional<ManagerTemplateCategoryMapEntity> mapOpt = managerTemplateCategoryMapRepository
                    .findById(a.getManagerTemplateCategoryMap().getCategoryId());
            if (mapOpt.isEmpty())
                continue;
            ManagerTemplateCategoryMapEntity map = mapOpt.get();
 
            Optional<ManagerTemplateEntity> mtOpt = managerTemplateRepository.findById(map.getManagerTemplateId());
            String templateName = mtOpt.map(ManagerTemplateEntity::getManagerTemplateName).orElse("");
 
            String categoryName = "";
            if (map.getCategoryId() != null) {
                Optional<CategoryEntity> categoryOpt = categoryRepository.findById(map.getCategoryId());
                if (categoryOpt.isPresent()) {
                    categoryName = categoryOpt.get().getCategoryName();
                }
            }
 
            Map<String, Object> entry = new HashMap<>();
            entry.put("id", map.getManagerTemplateCategoryId());
            entry.put("name", templateName);
            entry.put("category", categoryName);
            entry.put("weightage", map.getWeighage());
            entry.put("skillLevel", mtOpt.map(ManagerTemplateEntity::getSkillLevel).orElse(""));
            entry.put("scheduleDate", a.getAssignedDate());
            result.add(entry);
        }
 
        return result;
    }
 
    @Override
    public Map<String, Object> getExamDetails(Long userId, Long managerTemplateId) {
        if (userRepo.findById(userId).isEmpty())
            throw new NoSuchElementException("Candidate not found.");

        Optional<ManagerTemplateEntity> mtOpt = managerTemplateRepository.findById(managerTemplateId);
        if (mtOpt.isEmpty())
            throw new NoSuchElementException("Exam not found.");

        ManagerTemplateEntity mt = mtOpt.get();
        List<ManagerTemplateCategoryMapEntity> categories = managerTemplateCategoryMapRepository
                .findByManagerTemplateId(managerTemplateId);

        Map<String, Object> response = new HashMap<>();
        response.put("name", mt.getManagerTemplateName());
        response.put("skillLevel", mt.getSkillLevel());
        // time_limit not present in schema; defaulting to 10 as per example
        response.put("time_limit", 10);
        // FIX: Include managerTemplateId in the response
        response.put("managerTemplateId", managerTemplateId); 

        // scheduleDate: if there is an assignment record for this user and any category of this template, use its assignedDate
        String scheduleDate = null;
        try {
            List<UserAssessmentTemplateMapEntity> assignments = userAssessmentRepo.findByUserId(userId);
            for (UserAssessmentTemplateMapEntity a : assignments) {
                // match against categories of this manager template
                boolean matches = categories.stream()
                        .anyMatch(c -> c.getManagerTemplateCategoryId().equals(a.getManagerTemplateCategoryMap().getCategoryId()));
                if (matches && a.getAssignedDate() != null) {
                    // Assuming getAssignedDate() returns a Timestamp or Date compatible with toLocalDateTime()
                    scheduleDate = a.getAssignedDate().toLocalDateTime().toLocalDate().toString();
                    break;
                }
            }
        } catch (Exception ignore) {
            // Log this exception instead of ignoring in production code
        }
        if (scheduleDate != null) {
            response.put("scheduleDate", scheduleDate);
        }

        Map<String, Object> categoryMap = new HashMap<>();
        for (ManagerTemplateCategoryMapEntity map : categories) {
            String catName = categoryRepository.findById(map.getCategoryId())
                    .map(CategoryEntity::getCategoryName)
                    .orElse("category-");

            List<QuestionAnswerEntity> questions = questionAnswerRepository.findByCategoryId(map.getCategoryId());

			// Convert to ShuffleController.Question
			List<ShuffleController.Question> questionList = questions.stream().map(q -> {
				ShuffleController.Question sq = new ShuffleController.Question();
				sq.id = q.getQuestionId();
				sq.question = q.getQuestion();
				return sq;
			}).collect(Collectors.toList());

			// Shuffle and batch
			ShuffleController<ShuffleController.Question> sampler = new ShuffleController<>(questionList, 5);
			List<ShuffleController.Question> batch = sampler.getNextBatch();

			// Convert batch to response format
			List<Map<String, Object>> qList = batch.stream().map(q -> {
				Map<String, Object> qm = new HashMap<>();
				qm.put("id", q.id);
				qm.put("question", q.question);

				// Find matching QuestionAnswerEntity to get options and answer
				QuestionAnswerEntity original = questions.stream().filter(qa -> Objects.equals(qa.getQuestionId(), q.id)).findFirst()
						.orElse(null);

				if (original != null) {
					ObjectMapper mapper = new ObjectMapper();
					try {
						Map<String, String> optionMap = mapper.readValue(original.getOptions(), Map.class);
						qm.put("options", new ArrayList<>(optionMap.values()));
					} catch (Exception e) {
						qm.put("options", Collections.emptyList());
					}
					qm.put("answer", original.getAnswer());
				}
				return qm;
			}).collect(Collectors.toList());

            // Create category details map with both questions and weightage
            Map<String, Object> categoryDetails = new HashMap<>();
            categoryDetails.put("questions", qList);
            // Add weightage if available
            if (map.getWeighage() != null) {
                categoryDetails.put("weightage", map.getWeighage());
            }

            categoryMap.put(catName, categoryDetails);
        }

        response.put("category", categoryMap);
        return response;
    }

 
 
    @Override
    @Transactional
    public Map<String, Object> submitExam(Long userId, Long managerTemplateId, SubmitRequest request) {
        if (request == null || request.answers == null || request.answers.isEmpty()) {
            throw new IllegalArgumentException("Answers are required.");
        }
 
        Optional<UserEntity> userOpt = userRepo.findById(userId);
        if (userOpt.isEmpty())
            throw new NoSuchElementException("Candidate not found.");
 
        List<ManagerTemplateCategoryMapEntity> maps = managerTemplateCategoryMapRepository
                .findByManagerTemplateId(managerTemplateId);
        if (maps.isEmpty())
            throw new NoSuchElementException("Exam not found.");
 
        // Group questions by category for individual scoring
        Map<Long, List<QuestionAnswerEntity>> questionsByCategory = new HashMap<>();
        Map<Long, QuestionAnswerEntity> questionById = new HashMap<>();
        Map<Long, Long> categoryTemplateMap = new HashMap<>();
        Set<Long> allCategoryIds = new HashSet<>();
       
        for (ManagerTemplateCategoryMapEntity m : maps) {
            try {
            	allCategoryIds.add(m.getCategoryId());
            	categoryTemplateMap.put(m.getCategoryId(), m.getManagerTemplateCategoryId());
            	List<QuestionAnswerEntity> qs = questionAnswerRepository.findByCategoryId(m.getCategoryId());
                questionsByCategory.put(m.getCategoryId(), qs);
                for (QuestionAnswerEntity q : qs) {
                    questionById.put(q.getQuestionId(), q);
                }
            } catch (Exception e) {
                System.err.println("Error loading questions for category " + m.getCategoryId() + ": " + e.getMessage());
                // Continue with other categories even if one fails
                questionsByCategory.put(m.getCategoryId(), new ArrayList<>());
            }
        }
 
 
        // New logic: For all assigned subjects, include all questions in analysis, even if not answered
        int total = 0;
        int correct = 0;
        int unanswered = 0;
        List<Map<String, Object>> questionAnalysis = new ArrayList<>();
        Map<Long, Integer> correctByCategory = new HashMap<>();
        Map<Long, Integer> totalByCategory = new HashMap<>();
 
//        // Get all assigned category maps for the user (avoid duplicate variable names)
//        List<UserAssessmentTemplateMapEntity> allAssignmentsForQuestions = userAssessmentRepo.findByUserId(userId);
//        List<Long> allCategoryMapIdsForQuestions = new ArrayList<>();
//        for (UserAssessmentTemplateMapEntity assignment : allAssignmentsForQuestions) {
//            allCategoryMapIdsForQuestions.add(assignment.getManagerTemplateCategoryMap().getCategoryId());
//        }
//        List<ManagerTemplateCategoryMapEntity> allCategoryMapsForQuestions = new ArrayList<>();
//        for (Long mapId : allCategoryMapIdsForQuestions) {
//            managerTemplateCategoryMapRepository.findById(mapId).ifPresent(allCategoryMapsForQuestions::add);
//        }
//        Set<Long> allCategoryIds = new HashSet<>();
//        for (ManagerTemplateCategoryMapEntity m : allCategoryMapsForQuestions) {
//            allCategoryIds.add(m.getCategoryId());
//        }
        
        Integer incorrect = 0;
    	Double totalScore = 0.0;
 
        // For each assigned subject (category), get all questions
        for (Long categoryId : allCategoryIds) {
        	List<QuestionAnswerEntity> questions = questionAnswerRepository.findByCategoryId(categoryId);
        	Long manageTemplateCategoryId = categoryTemplateMap.get(categoryId);
        	
        	incorrect = 0;
        	totalScore = 0.0;
        	
        	for (QuestionAnswerEntity q : questions) {
                total++;
                String selected = null;
                String status = "unanswered";
                String correctAnswer = null;
                try {
                	correctAnswer = q.getAnswer();
                } catch (Exception ignore) {}
 
                // Check if this question was answered in the request
                if (request.answers == null || !request.answers.containsKey(String.valueOf(q.getQuestionId()))) {
                    continue;
                }
                total++;
                selected = request.answers.get(String.valueOf(q.getQuestionId()));

                if (selected == null || selected.isEmpty()) {
                    unanswered++;
                    status = "unanswered";
                } else if (selected.equals(correctAnswer)) {
                    correct++;
                    status = "correct";
                    correctByCategory.put(categoryId, correctByCategory.getOrDefault(categoryId, 0) + 1);
                } else {
                    status = "incorrect";
                }

                totalByCategory.put(categoryId, totalByCategory.getOrDefault(categoryId, 0) + 1);

                if (selected != null) {
                    UserQuestionAnswerResultsEntity ur = new UserQuestionAnswerResultsEntity();
                    ur.setUserId(userId);
                    ur.setManagerTemplateCategoryId(manageTemplateCategoryId);
                    ur.setQuestionId(q.getQuestionId());
                    ur.setSelectedOption(selected);
                    userAnswersRepo.save(ur);
                }
 
                Map<String, Object> qa = new HashMap<>();
                qa.put("id", q.getQuestionId());
                qa.put("question", q.getQuestion());
                qa.put("options", q.getOptions());
                qa.put("correct_answer", correctAnswer);
                qa.put("user_answer", selected);
                qa.put("status", status);
                questionAnalysis.add(qa);
            }
            
            incorrect = total - correct - unanswered;
            totalScore = total == 0 ? 0.0 : (100.0 * correct) / total;

            UserEvaluationSummaryEntity summary = new UserEvaluationSummaryEntity();
            summary.setUserId(userId);
            summary.setManagerTemplateId(managerTemplateId);
            summary.setManagerTemplateCategoryId(manageTemplateCategoryId);
            summary.setScore(Math.round(totalScore));
            evaluationRepo.save(summary);
        }
 
        Map<String, Object> analysis = new HashMap<>();
        analysis.put("totalQuestions", total);
        analysis.put("correct", correct);
        analysis.put("incorrect", incorrect);
        analysis.put("unanswered", unanswered);
        analysis.put("questions", questionAnalysis);
 
        Map<String, Object> resp = new HashMap<>();
        resp.put("submittedAt", Instant.now().toString());
 
        // Calculate subject scores by category, including all subjects assigned to the candidate
        Map<String, Object> subjects = new HashMap<>();
        double weightedTotalScore = 0;
        double totalWeightage = 0;
 
        // Get all categories (subjects) assigned to the candidate
        List<UserAssessmentTemplateMapEntity> allAssignments = userAssessmentRepo.findByUserId(userId);
        List<Long> allCategoryMapIds = new ArrayList<>();
        for (UserAssessmentTemplateMapEntity assignment : allAssignments) {
            allCategoryMapIds.add(assignment.getManagerTemplateCategoryMap().getCategoryId());
        }
        // Get all ManagerTemplateCategoryMapEntity for these ids
        List<ManagerTemplateCategoryMapEntity> allCategoryMaps = new ArrayList<>();
        for (Long mapId : allCategoryMapIds) {
            managerTemplateCategoryMapRepository.findById(mapId).ifPresent(allCategoryMaps::add);
        }
        // Use a set to avoid duplicate category ids
        Map<Long, ManagerTemplateCategoryMapEntity> uniqueCategoryMaps = new HashMap<>();
        for (ManagerTemplateCategoryMapEntity m : allCategoryMaps) {
            uniqueCategoryMaps.put(m.getCategoryId(), m);
        }
 
        // For each unique subject (category), calculate score or set to 0 if not present in this exam
        for (ManagerTemplateCategoryMapEntity m : uniqueCategoryMaps.values()) {
            String categoryName = categoryRepository.findById(m.getCategoryId())
                    .map(CategoryEntity::getCategoryName)
                    .orElse("category-" + m.getCategoryId());
 
            int categoryCorrect = correctByCategory.getOrDefault(m.getCategoryId(), 0);
            int categoryTotal = totalByCategory.getOrDefault(m.getCategoryId(), 0);
            int categoryScore = categoryTotal == 0 ? 0 : (100 * categoryCorrect) / categoryTotal;
 
            Integer weightage = m.getWeighage() != null ? m.getWeighage() : 1;
            totalWeightage += weightage;
 
            double weightedCategoryScore = (categoryScore * weightage);
            weightedTotalScore += weightedCategoryScore;
 
            Map<String, Object> categoryDetails = new HashMap<>();
            categoryDetails.put("score", categoryScore);
            categoryDetails.put("weightage", weightage);
            categoryDetails.put("weightedScore", weightedCategoryScore / weightage);
 
            subjects.put(categoryName.toLowerCase(), categoryDetails);
        }
 
        // Calculate final weighted score
        double finalScore = totalWeightage == 0 ? 0 : (weightedTotalScore / totalWeightage);
 
        resp.put("subjects", subjects);
        resp.put("totalScore", Math.round(finalScore * 100.0) / 100.0);
        resp.put("weightageDetails", Map.of(
            "totalWeightage", totalWeightage,
            "weightedScore", Math.round(weightedTotalScore * 100.0) / 100.0
        ));
        resp.put("analysis", analysis);
        return resp;
    }
}
 