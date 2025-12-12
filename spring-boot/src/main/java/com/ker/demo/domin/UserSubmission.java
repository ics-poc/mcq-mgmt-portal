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
public class UserSubmission {
	private Long submissionId;
	private String userId;
	private Long questionId;
	private String selectedOption;
	private Timestamp submittedDate;
}
