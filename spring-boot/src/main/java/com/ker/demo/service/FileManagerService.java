package com.ker.demo.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FileManagerService {

	void saveFileAsync(byte[] fileBytes, String originalFileName, Long categoryId, String uploadedBy);

	void processFile(MultipartFile file, String uploadedBy) throws IOException;

}
