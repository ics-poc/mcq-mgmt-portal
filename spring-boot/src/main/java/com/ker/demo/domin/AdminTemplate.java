package com.ker.demo.domin;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTemplate {
	
	@JsonProperty("adminTemplateId")
    private Long adminTemplateId;
	
	@JsonProperty("adminTemplateName")
    private String adminTemplateName;
    
    private String description;
    
    private String createdUserId;
	private Timestamp createdDate;
	private String modifiedUserId;
	private Timestamp modifiedDate;
	
	@JsonProperty("adminTemplateCategoryMap")
	@OneToMany(mappedBy = "adminTemplate", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<AdminTemplateCategoryMap> adminTemplateCategoryMap;

}
