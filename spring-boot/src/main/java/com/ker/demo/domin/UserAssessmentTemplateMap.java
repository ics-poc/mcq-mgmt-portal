package com.ker.demo.domin;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAssessmentTemplateMap {
    private Long userAssessmentTemplateId;
    private String userId;
    private Long managerTemplateCategoryId;
    private String assignedBy;
    private Timestamp assignedDate;
    private Timestamp scheduleDate;
    private Integer weighage;
	private String difficultyLevel;
	private String category;
	private String managerTemplateName;
	private Long score;
	private Integer status;
	private Boolean isRetake;
}

