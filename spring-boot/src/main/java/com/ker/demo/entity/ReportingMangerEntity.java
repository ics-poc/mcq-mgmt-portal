package com.ker.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user_manager_map")
public class ReportingMangerEntity {
	
	@Id
    @Column(name = "user_manager_id", length = 50)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userMangerId;
 
    @Column(name = "manager_id")
	private Long managerId;
 
    @Column(name = "user_id")
    private Long userId;

}
