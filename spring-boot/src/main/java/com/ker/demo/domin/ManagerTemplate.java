package com.ker.demo.domin;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerTemplate {

	private Long managerTemplateId;
	
	private String managerTemplateName;
	
	private Long managerId;
	
	private String description;
	
	private String skillLevel;
	
	private Integer questionCount;
	
	private String createdUserId;
	
	private Timestamp createdDate;
	
	private String modifiedUserId;
	
	private Timestamp modifiedDate;

	private List<ManagerTemplateCategoryMap> managerTemplateCategoryMap;
}
