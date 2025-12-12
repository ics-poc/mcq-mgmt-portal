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
@Table(name = "category")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "category_name", length = 255, nullable = false)
    private String categoryName;

    @Column(name = "sub_category_name",length = 255)
    private String subCategoryName;
    
    @Column(name = "application_area",length = 255)
    private String applicationArea;
    
    @Column(name = "skill_level", length = 255)
    private String skillLevel;
    
    @Column(name = "reference", length = 255)
    private String reference;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_user_id", length = 100)
    private String createdUserId;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_user_id", length = 100)
    private String modifiedUserId;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;
    
    @Column(name = "status", columnDefinition = "INT DEFAULT 0")
    private Integer status;

}
