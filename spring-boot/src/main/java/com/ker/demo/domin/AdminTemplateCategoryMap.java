package com.ker.demo.domin;

import java.sql.Timestamp;

import com.ker.demo.entity.AdminTemplateEntity;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTemplateCategoryMap {
	
	private Long adminTemplateCategoryId;
	private Long adminTemplateId;
	private Long categoryId;
	private Integer weighage;
	private String difficultyLevel;
	private String createdUserId;
	private Timestamp createdDate;
	private String modifiedUserId;
	private Timestamp modifiedDate;
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_template_id", insertable = false, updatable = false)
	private AdminTemplateEntity adminTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", insertable = false, updatable = false)
	private Category category;

}
