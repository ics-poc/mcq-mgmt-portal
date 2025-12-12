package com.ker.demo.entity;

import java.sql.Timestamp;

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

@Entity
@Table(name = "manager_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ManagerUserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "manager_id", length = 50, nullable = false)
    private String managerId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "assigned_date")
    private Timestamp assignedDate;
}
