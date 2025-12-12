package com.ker.demo.domin;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
	
	private Long userId;
	private String firstName;
	private String lastName;
	private String email;
	private String phone;
	private Long userRoleId;
	private UserStatus status;
	private String password;
	private String roleCode;
	private String roleName;
	private String language;
	private String timezone;
	private String employeeNumber;
	private String employeeGrade;
	private String businessUnit;
	private String project;
	private Timestamp lastLoginDate;
	private Timestamp lastLogoutDate;
	private String createdUserId;
	private Long managerId;
	private String modifiedUserId;
	private Timestamp createdDate;
}
