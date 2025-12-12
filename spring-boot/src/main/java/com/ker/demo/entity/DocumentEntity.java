package com.ker.demo.entity;

import java.time.LocalDateTime;

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
@Table(name = "uploaded_documents")
public class DocumentEntity {
	
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	private Long documentId;

	private String categoryName;
	private String fileName;
	private String uploadLocation;
	private String uploadedBy;
	private LocalDateTime uploadDate;
}
