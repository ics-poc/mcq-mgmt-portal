package com.ker.demo.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name="permission")
public class PermissionEntity {

	@Id
	@Column(name = "code", columnDefinition = "VARCHAR(50)")
	private String code;

	@Column(name = "description", columnDefinition = "VARCHAR(255)", nullable = false)
	private String description;

	@Column(name = "created_user_id", columnDefinition = "VARCHAR(100)", nullable = false)
	private String createdUserId;

	@Column(name = "modified_user_id", columnDefinition = "VARCHAR(100)", nullable = false)
	private String modifiedUserId;

	@Column(name = "created_date", nullable = false)
	private Timestamp createdDate;

	@Column(name = "modified_date", nullable = false)
	private Timestamp modifiedDate;
}