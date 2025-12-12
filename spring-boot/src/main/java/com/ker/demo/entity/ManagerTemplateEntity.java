package com.ker.demo.entity;

import java.sql.Timestamp;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "manager_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerTemplateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "manager_template_id")
    private Long managerTemplateId;

    @Column(name = "manager_template_name", length = 255, nullable = false)
    private String managerTemplateName;

    @Column(name = "manager_id", nullable = false)
    private Long managerId;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "skill_level", length = 50)
    private String skillLevel;

    @Column(name = "question_count")
    private Integer questionCount;
    @Column(name = "created_user_id", length = 100)
    private String createdUserId;

    @Column(name = "modified_user_id", length = 100)
    private String modifiedUserId;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @OneToMany(mappedBy = "managerTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManagerTemplateCategoryMapEntity> managerTemplateCategoryMapEntity;
}
