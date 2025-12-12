package com.ker.demo.entity;

import java.sql.Timestamp;

import org.hibernate.annotations.ColumnTransformer;

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
@Table(name = "question_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	private Long questionId;

	@Column(name = "category_id", nullable = false)
	private Long categoryId;

	@Column(name = "question", columnDefinition = "TEXT", nullable = false)
	private String question;

	@Column(name = "options", columnDefinition = "jsonb", nullable = false)
	@ColumnTransformer(write = "?::jsonb")
	private String options;

	@Column(name = "answer", columnDefinition = "TEXT", nullable = false)
	private String answer;

	@Column(name = "created_date")
	private Timestamp createdDate;
	
	@Column(name = "status_flag", columnDefinition = "CHAR", nullable = false)
    private String statusFlag;

}
