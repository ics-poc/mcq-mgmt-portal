package com.ker.demo.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_assessment_template_map")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserAssessmentTemplateMapEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_assessment_template_id")
    private Long userAssessmentTemplateId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "manager_template_category_id", referencedColumnName = "manager_template_category_id")
    private ManagerTemplateCategoryMapEntity managerTemplateCategoryMap;

    @Column(name = "assigned_by", length = 50, nullable = false)
    private String assignedBy;

    @Column(name = "assigned_date", nullable = false)
    private Timestamp assignedDate;

    @Column(name = "time_limit")
    private Integer timeLimit;

    @Column(name = "schedule_date")
    private Timestamp scheduleDate;
    
    @Column(name = "status", nullable = false)
    private Integer status = 0; 

//    @OneToOne(fetch = FetchType.EAGER)
//    @JoinColumns({
//        @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false),
//        @JoinColumn(name = "manager_template_category_id", referencedColumnName = "manager_template_category_id", insertable = false, updatable = false)
//    })
//    private UserEvaluationSummaryEntity userEvaluationSummary;

}
