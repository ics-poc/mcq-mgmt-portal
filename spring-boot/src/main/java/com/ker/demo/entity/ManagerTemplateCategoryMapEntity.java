package com.ker.demo.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manager_template_category_map")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerTemplateCategoryMapEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_template_category_id")
    private Long managerTemplateCategoryId;

    @Column(name = "manager_template_id", nullable = false)
    private Long managerTemplateId;

    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "weighage", nullable = false)
    private Integer weighage;

    @Column(name = "difficulty_level", length = 50)
    private String difficultyLevel;

    @Column(name = "created_user_id", length = 100)
    private String createdUserId;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_user_id", length = 100)
    private String modifiedUserId;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private CategoryEntity category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_template_id", insertable = false, updatable = false)
    private ManagerTemplateEntity managerTemplate;
    
}
