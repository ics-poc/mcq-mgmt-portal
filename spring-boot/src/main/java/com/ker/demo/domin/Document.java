package com.ker.demo.domin;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Document {
	private Long documentId;
	private String categoryName;
	private String fileName;
	private String uploadLocation;
	private String uploadedBy;
	private LocalDateTime uploadDate;

}
