package com.ker.demo.domin;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdminTemplateCategory {
	private Long id;
	private String adminTemplateId;
	private String categoryId;
	private Integer weighage;
	private String difficultyLevel;
	private Timestamp createdDate;
	private Timestamp modifiedDate;
}
