package com.ker.demo.domin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionAnswerResults {
    private Long userQuestionAnswerResultId;
    private String userId;
    private Long managerTemplateCategoryId;
    private Long questionId;
    private String selectedOption;
}
