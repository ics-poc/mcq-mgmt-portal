package com.ker.demo.domin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEvaluationSummary {
    private Long userEvaluationId;
    private String userId;
    private Long managerTemplateCategoryId;
    private Long score;
}

