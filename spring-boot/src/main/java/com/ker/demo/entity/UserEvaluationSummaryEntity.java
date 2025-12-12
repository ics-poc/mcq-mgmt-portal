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
@Table(name = "user_evalutaion_summary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEvaluationSummaryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_evaluation_id")
    private Long userEvaluationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "manager_template_id")
    private Long managerTemplateId;
    
    @Column(name = "manager_template_category_id")
    private Long managerTemplateCategoryId;

    @Column(name = "score", nullable = false)
    private Long score;
}
