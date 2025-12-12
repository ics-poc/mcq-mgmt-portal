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
@Table(name = "user_submission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "submission_id")
    private Long submissionId;

    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "selected_option", columnDefinition = "TEXT", nullable = false)
    private String selectedOption;

    @Column(name = "submitted_date")
    private Timestamp submittedDate;
}
