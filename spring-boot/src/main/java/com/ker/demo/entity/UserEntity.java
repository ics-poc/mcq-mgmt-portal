package com.ker.demo.entity;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_profile")
public class UserEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "user_id")
	private Long userId;

	@Column(name = "first_name", columnDefinition = "VARCHAR(50)")
	private String firstName;

	@Column(name = "last_name", columnDefinition = "VARCHAR(50)")
	private String lastName;

	@Column(name = "email", columnDefinition = "VARCHAR(255)")
	private String email;

	@Column(name = "phone", columnDefinition = "VARCHAR(50)")
	private String phone;

	@Column(name = "role_id", columnDefinition = "VARCHAR(50)")
	private Long userRoleId;

	@Column(name = "status", columnDefinition = "VARCHAR(50)")
	private String status;
		
	@Column(name = "employee_number")
	private String employeeNumber;

	@Column(name = "employee_grade")
	private String employeeGrade;

	@Column(name = "reporting_manager_id")
	private Long reportingManagerId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "reporting_manager_id", referencedColumnName = "user_manager_id", insertable = false, updatable = false)
	private ReportingMangerEntity reportingManager;

	@Column(name = "business_unit")
	private String businessUnit;

	@Column(name = "program_project")
	private String project;

	@Column(name = "language")
	private String language;

	@Column(name = "timezone")
	private String timezone;

	@Column(name = "password", columnDefinition = "TEXT")
	private String password;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@Column(name = "last_login_date")
	private Timestamp lastLoginDate;

	@Column(name = "last_logout_date")
	private Timestamp lastLogoutDate;

	@Column(name = "created_user_id")
	private String createdUserId;

	@Column(name = "modified_user_id")
	private String modifiedUserId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
	@Column(name = "created_date")
	private Timestamp createdDate;

}
