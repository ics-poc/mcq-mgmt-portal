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
@Table(name = "question_assignment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAssignmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long assignmentId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "assigned_by", length = 50)
    private String assignedBy;

    @Column(name = "assigned_date")
    private Timestamp assignedDate;
}

