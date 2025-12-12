package com.ker.demo.entity;

import java.sql.Timestamp;

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
@Table(name = "admin_template_category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTemplateCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "admin_template_id", length = 50, nullable = false)
    private String adminTemplateId;

    @Column(name = "category_id", length = 50, nullable = false)
    private String categoryId;

    @Column(name = "weighage", nullable = false)
    private Integer weighage;

    @Column(name = "difficulty_level", length = 50)
    private String difficultyLevel;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;
}
