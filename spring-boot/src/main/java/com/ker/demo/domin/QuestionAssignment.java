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
public class QuestionAssignment {
	private Long assignmentId;
	private String userId;
	private Long questionId;
	private String assignedBy;
	private Timestamp assignedDate;
}
