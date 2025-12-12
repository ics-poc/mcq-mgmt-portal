package com.ker.demo.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "user_role")
public class RoleEntity {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="role_id")
	private Long id;

	@Column(name="role_code", nullable = false, unique = true)
	private String code;

	@Column(name="role_name",nullable = false)
	private String name;

	@Column(name="role_type", nullable = false)
	private String type;

//	@ManyToMany(fetch = FetchType.EAGER)
//	@JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_code"))
//	private Set<com.ker.demo.entity.PermissionEntity> permissions;

	@Column(name="created_user_id", nullable = false)
	private String createdUserId;

	@Column(name="created_date", nullable = false)
	private Timestamp createdDate;

}