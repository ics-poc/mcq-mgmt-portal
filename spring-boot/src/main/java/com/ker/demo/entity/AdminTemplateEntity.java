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
@Table(name = "admin_template")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTemplateEntity {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_template_id")
    private Long adminTemplateId;

    @Column(name = "admin_template_name", length = 255, nullable = false)
    private String adminTemplateName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "created_user_id", length = 100)
    private String createdUserId;

    @Column(name = "modified_user_id", length = 100)
    private String modifiedUserId;

    @Column(name = "created_date")
    private Timestamp createdDate;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;
    
    @OneToMany(mappedBy = "adminTemplate", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AdminTemplateCategoryMapEntity> adminTemplateCategoryMap;

}
