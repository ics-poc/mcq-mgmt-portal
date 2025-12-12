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
public class ManagerUser {
	private Long id;
	private String managerId;
	private String userId;
	private Timestamp assignedDate;
}
