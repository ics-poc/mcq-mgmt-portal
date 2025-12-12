package com.ker.demo.entity;

import java.time.LocalDateTime;

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
@Table(name = "questionnaire_documents")
public class QuestionnaireDocumentsEntity {

	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long questionnaireDocumentId;
	
	@Column(name = "category_id")
	private Long categoryId;
	
	@Column(name = "file_name")
	private String fileName;
	
	@Column(name = "upload_location")
	private String uploadLocation;
	
	@Column(name = "uploaded_by")
	private String uploadedBy;
	
	@Column(name = "upload_date")
	private LocalDateTime uploadDate;
}
