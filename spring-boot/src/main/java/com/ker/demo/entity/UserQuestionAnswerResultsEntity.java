package com.ker.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_question_answer_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserQuestionAnswerResultsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_question_answer_result_id")
    private Long userQuestionAnswerResultId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "manager_template_category_id")
    private Long managerTemplateCategoryId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "selected_option", columnDefinition = "TEXT", nullable = false)
    private String selectedOption;
}

