package com.ker.demo.domin;

import java.sql.Timestamp;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class ManagerTemplateCategoryMap {

	private Long managerTemplateCategoryId;
	private Long managerTemplateId;
	private Long categoryId;
	private Integer weighage;
	private String difficultyLevel;
	private String createdUserId;
	private Timestamp createdDate;
	private String modifiedUserId;
	private Timestamp modifiedDate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "manager_template_id", insertable = false, updatable = false)
	private ManagerTemplate adminTemplate;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "category_id", insertable = false, updatable = false)
	private Category category;
	

	public ManagerTemplateCategoryMap(Long managerTemplateCategoryId, Long managerTemplateId, Long categoryId,
			Integer weighage, String difficultyLevel, Timestamp createdDate, Timestamp modifiedDate) {
		this.managerTemplateCategoryId = managerTemplateCategoryId;
		this.managerTemplateId = managerTemplateId;
		this.categoryId = categoryId;
		this.weighage = weighage;
		this.difficultyLevel = difficultyLevel;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
	}

}
