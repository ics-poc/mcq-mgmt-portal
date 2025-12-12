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
public class Category {

	private Long categoryId;
	private String categoryName;
	private String subCategoryName;
	private String description;
	private String applicationArea;
	private String skillLevel;
	private String reference;
	private String createdUserId;
	private Timestamp createdDate;
	private String modifiedUserId;
	private Timestamp modifiedDate;
	private String status;
}
