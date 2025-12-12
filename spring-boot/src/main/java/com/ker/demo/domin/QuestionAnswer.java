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
public class QuestionAnswer {
	
	private Long questionId;
	private Long categoryId;
	private String question;
	private List<String> options;
	private String answer;
	private Timestamp createdDate;
	private String statusFlag;
}
