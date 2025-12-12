package com.ker.demo.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ker.demo.domin.FileInfo;
import com.ker.demo.entity.QuestionnaireDocumentsEntity;
import com.ker.demo.repository.DocumentRepository;
import com.ker.demo.service.FileManagerService;

@RestController
public class FileManagerController {
	@Autowired
	private FileManagerService fileManagerService;

	@Autowired
	private DocumentRepository questionerDocRepo;

	private static final String STORAGE_DIRECTORY = "uploads";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestPart("file") MultipartFile file,
			@RequestParam("categoryId") Long categoryId, @RequestParam("uploadedBy") String uploadedBy) {

		if (file.isEmpty()) {
			return new ResponseEntity<>("Please select a file to upload.", HttpStatus.BAD_REQUEST);
		}

		try {
			byte[] fileBytes = file.getBytes();
			String originalFileName = file.getOriginalFilename();
			fileManagerService.saveFileAsync(fileBytes, originalFileName, categoryId, uploadedBy);

			return new ResponseEntity<>("File upload initiated successfully.", HttpStatus.ACCEPTED);
		} catch (SecurityException e) {
			return new ResponseEntity<>("File path issue: " + e.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/uploadfile")
	public ResponseEntity<String> uploadCsv(@RequestPart("file") MultipartFile file,
			@RequestParam("uploadedBy") String uploadedBy) {
		try {
			fileManagerService.processFile(file, uploadedBy);
			return ResponseEntity.ok("CSV processed and data stored successfully.");
		} catch (Exception e) {
			return ResponseEntity.status(500).body("Error processing CSV: " + e.getMessage());
		}
	}

	@GetMapping("/files")
	public ResponseEntity<List<FileInfo>> listFiles() {
		List<QuestionnaireDocumentsEntity> documents = questionerDocRepo.findAll();

		List<FileInfo> fileInfos = documents.stream().map(
				doc -> new FileInfo(doc.getFileName(), "http://localhost:8081/api/files/download/" + doc.getFileName()))

				.collect(Collectors.toList());

		return ResponseEntity.ok(fileInfos);
	}

	@GetMapping("/files/download/{filename}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) throws IOException {
		Path filePath = Paths.get(STORAGE_DIRECTORY).resolve(filename).normalize();
		Resource resource = new UrlResource(filePath.toUri());

		if (!resource.exists() || !resource.isReadable()) {
			return ResponseEntity.notFound().build();
		}

		String contentType = Files.probeContentType(filePath);
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

}
