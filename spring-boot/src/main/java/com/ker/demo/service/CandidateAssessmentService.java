package com.ker.demo.service;

import java.util.List;
import java.util.Map;

public interface CandidateAssessmentService {

    class SubmitRequest {
        public Map<String, String> answers;
    }

    List<Map<String, Object>> getAllResults(Long userId, Long managerTemplateId);
    Map<String, Object> getResultById(Long userId, Long evaluationId);
    List<Map<String, Object>> getAssignedExams(Long userId);
    Map<String, Object> getExamDetails(Long userId, Long managerTemplateId);
    Map<String, Object> submitExam(Long userId, Long managerTemplateId, SubmitRequest request);
}

