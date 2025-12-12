package com.ker.demo.serviceImpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ker.demo.domin.Category;
import com.ker.demo.domin.QuestionnaireDocuments;
import com.ker.demo.domin.User;
import com.ker.demo.domin.UserStatus;
import com.ker.demo.entity.CategoryEntity;
import com.ker.demo.entity.QuestionAnswerEntity;
import com.ker.demo.entity.QuestionnaireDocumentsEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.repository.CategoryRepository;
import com.ker.demo.repository.DocumentRepository;
import com.ker.demo.repository.QuestionAnswerRepository;
import com.ker.demo.service.CategoryService;
import com.ker.demo.service.FileManagerService;
import com.ker.demo.service.UserService;

@Service
public class FileManagerServiceImpl implements FileManagerService {

	private static final String STORAGE_DIRECTORY = "uploads"; // relative to your project root

	@Autowired
	private DocumentRepository documentRepository;

	@Autowired
	private QuestionAnswerRepository questionAnswerRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private CategoryService categoryService;

	private QuestionnaireDocumentsEntity convertDtoToEntity(QuestionnaireDocuments dto) {
		QuestionnaireDocumentsEntity entity = new QuestionnaireDocumentsEntity();
		entity.setQuestionnaireDocumentId(dto.getQuestionnaireDocumentId());
		entity.setCategoryId(dto.getCategoryId());
		entity.setFileName(dto.getFileName());
		entity.setUploadLocation(dto.getUploadLocation());
		entity.setUploadedBy(dto.getUploadedBy());
		entity.setUploadDate(dto.getUploadDate());
		return entity;
	}

	private QuestionnaireDocuments convertEntityToDto(QuestionnaireDocumentsEntity entity) {
		QuestionnaireDocuments dto = new QuestionnaireDocuments();
		dto.setQuestionnaireDocumentId(entity.getQuestionnaireDocumentId());
		dto.setCategoryId(entity.getCategoryId());
		dto.setFileName(entity.getFileName());
		dto.setUploadLocation(entity.getUploadLocation());
		dto.setUploadedBy(entity.getUploadedBy());
		dto.setUploadDate(entity.getUploadDate());
		return dto;
	}

	@Override
	@Transactional
	public void saveFileAsync(byte[] fileBytes, String originalFileName, Long categoryId, String uploadedBy) {
		try {
			File targetFile = new File(STORAGE_DIRECTORY + File.separator + originalFileName);
			Files.createDirectories(targetFile.getParentFile().toPath());
			Files.write(targetFile.toPath(), fileBytes);

			// Save metadata
			QuestionnaireDocuments newDocumentDto = new QuestionnaireDocuments();
			newDocumentDto.setCategoryId(categoryId);
			newDocumentDto.setFileName(originalFileName);
			newDocumentDto.setUploadLocation(targetFile.getAbsolutePath());
			newDocumentDto.setUploadedBy(uploadedBy);
			newDocumentDto.setUploadDate(LocalDateTime.now());

			QuestionnaireDocumentsEntity documentEntity = convertDtoToEntity(newDocumentDto);
			documentRepository.save(documentEntity);
			saveUsersFromCsv(targetFile);

		} catch (Exception e) {
			System.err.println("Async file upload failed: " + e.getMessage());
		}

	}

	private void saveUsersFromCsv(File csvFile) {
		System.out.println("File exists: " + csvFile.exists());
		System.out.println("Absolute path: " + csvFile.getAbsolutePath());

		try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
			String line;
			String[] headers = null;

			while ((line = reader.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty())
					continue;

				String[] fields = line.split("\",\"");
				for (int i = 0; i < fields.length; i++) {
					fields[i] = fields[i].replaceAll("^\"|\"$", "").trim();
				}

				if (headers == null) {
					headers = fields; // first line is header
					continue;
				}

				Map<String, String> userData = new HashMap<>();
				for (int i = 0; i < headers.length && i < fields.length; i++) {
					userData.put(headers[i], fields[i]);
				}

				UserEntity user = new UserEntity();
				user.setFirstName(userData.get("firstName"));
				user.setLastName(userData.get("lastName"));
				user.setEmail(userData.get("email"));
				user.setPhone(userData.get("phone"));
				user.setStatus(userData.get("status"));
				user.setLanguage(userData.get("language"));
				user.setTimezone(userData.get("timezone"));
				user.setPassword(userData.get("password"));
				user.setEmployeeNumber(userData.get("employeeNumber"));
				user.setEmployeeGrade(userData.get("employeeGrade"));
				user.setProject(userData.get("project"));
				user.setBusinessUnit(userData.get("businessUnit"));

				try {
					user.setUserRoleId(Long.parseLong(userData.get("userRoleId")));
				} catch (NumberFormatException e) {
					user.setUserRoleId(null);
				}

				String managerIdStr = userData.get("reportingManagerId");
				if (managerIdStr != null && !managerIdStr.isBlank()) {
					try {
						user.setReportingManagerId(Long.parseLong(managerIdStr));
					} catch (NumberFormatException e) {
						user.setReportingManagerId(null);
					}
				}

				sendUserToProfile(user);
			}
		} catch (IOException e) {
			System.err.println("CSV parsing error: " + e.getMessage());
		}
	}

	private void sendUserToProfile(UserEntity userEntity) {
		try {
			User user = new User();
			user.setFirstName(userEntity.getFirstName());
			user.setLastName(userEntity.getLastName());
			user.setEmail(userEntity.getEmail());
			user.setPhone(userEntity.getPhone());
			user.setUserRoleId(userEntity.getUserRoleId());
			user.setStatus(UserStatus.ACTIVE);
			user.setLanguage(userEntity.getLanguage());
			user.setTimezone(userEntity.getTimezone());
			user.setPassword(userEntity.getPassword());
			user.setEmployeeNumber(userEntity.getEmployeeNumber());
			user.setEmployeeGrade(userEntity.getEmployeeGrade());
			user.setManagerId(userEntity.getReportingManagerId());

			user.setProject(userEntity.getProject());
			user.setBusinessUnit(userEntity.getBusinessUnit());

			User createdUser = userService.createUser(user);

			System.out.println("User created: " + createdUser.getEmail());
		} catch (Exception e) {
			System.err.println("Failed to create user: " + e.getMessage());
		}
	}

	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void processFile(MultipartFile file, String uploadedBy) throws IOException {
	    String originalFileName = file.getOriginalFilename();
	    byte[] fileBytes = file.getBytes();

	    File targetFile = new File(STORAGE_DIRECTORY + File.separator + originalFileName);
	    Files.createDirectories(targetFile.getParentFile().toPath());
	    Files.write(targetFile.toPath(), fileBytes);

	    String extension = originalFileName.substring(originalFileName.lastIndexOf('.') + 1).toLowerCase();
	    if (extension.equals("csv")) {
	    	processCsv(targetFile, uploadedBy, originalFileName);
	    } else if (extension.equals("xlsx")) {
	        processXlsxFile(targetFile, uploadedBy, originalFileName);
	    } else {
	        throw new IllegalArgumentException("Unsupported file format: " + extension);
	    }
	}

	private void processCsv(File targetFile, String uploadedBy, String originalFileName) throws IOException {
	    Map<Long, CategoryEntity> categoryMap = new HashMap<>();
	    List<QuestionAnswerEntity> questionList = new ArrayList<>();

	    CSVFormat format = CSVFormat.Builder.create()
	        .setHeader()
	        .setIgnoreSurroundingSpaces(true)
	        .setTrim(true)
	        .setQuote('"')
	        .setDelimiter(',')
	        .build();

	    try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(targetFile), StandardCharsets.UTF_8))) {
	        Iterable<CSVRecord> records = format.parse(reader);
	        for (CSVRecord record : records) {
	            processRecord(
	                record.get("Level"),
	                record.get("Area"),
	                record.get("Sub-Area"),
	                record.get("Application Area if Applicable"),
	                record.get("Reference"),
	                record.get("Questions"),
	                record.get("Multiple Choices"),
	                record.get("Correct Answer"),
	                uploadedBy,
	                categoryMap,
	                questionList
	            );
	        }
	    }

	    saveQuestionsAndDocument(questionList, categoryMap, originalFileName, uploadedBy);
	}

	private void processXlsxFile(File targetFile, String uploadedBy, String originalFileName) throws IOException {
	    Map<Long, CategoryEntity> categoryMap = new HashMap<>();
	    List<QuestionAnswerEntity> questionList = new ArrayList<>();

	    try (FileInputStream fis = new FileInputStream(targetFile);
	         Workbook workbook = new XSSFWorkbook(fis)) {

	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rowIterator = sheet.iterator();

	        if (rowIterator.hasNext()) rowIterator.next(); // skip header

	        while (rowIterator.hasNext()) {
	            Row row = rowIterator.next();
	            processRecord(
	                getCellValue(row.getCell(0)),
	                getCellValue(row.getCell(1)),
	                getCellValue(row.getCell(2)),
	                getCellValue(row.getCell(3)),
	                getCellValue(row.getCell(4)),
	                getCellValue(row.getCell(5)),
	                getCellValue(row.getCell(6)),
	                getCellValue(row.getCell(7)),
	                uploadedBy,
	                categoryMap,
	                questionList
	            );
	        }
	    }

	    saveQuestionsAndDocument(questionList, categoryMap, originalFileName, uploadedBy);
	}

	private void processRecord(String level, String area, String subArea, String applicationArea, String reference,
	                           String question, String optionsRaw, String correctAnswer, String uploadedBy,
	                           Map<Long, CategoryEntity> categoryMap, List<QuestionAnswerEntity> questionList) {
	    try {
	        if (question.isEmpty() || optionsRaw.isEmpty() || correctAnswer.isEmpty()) {
	            System.err.println("Skipping record due to missing fields");
	            return;
	        }

	        Optional<CategoryEntity> existingCategory = categoryRepository
	            .findByCategoryNameAndSubCategoryNameAndApplicationAreaAndSkillLevelAndReference(
	                area, subArea, applicationArea, level, reference);

	        CategoryEntity category = existingCategory.orElseGet(() -> {
	            Category newCategory = new Category();
	            newCategory.setCategoryName(area);
	            newCategory.setSubCategoryName(subArea);
	            newCategory.setApplicationArea(applicationArea);
	            newCategory.setSkillLevel(level);
	            newCategory.setReference(reference);
	            newCategory.setCreatedUserId(uploadedBy);
	            Category created = categoryService.createCategory(newCategory);
	            return categoryRepository.findById(created.getCategoryId())
	                .orElseThrow(() -> new IllegalStateException("Failed to retrieve saved category"));
	        });

	        Long categoryId = category.getCategoryId();
	        categoryMap.putIfAbsent(categoryId, category);

	        ObjectMapper mapper = new ObjectMapper();
	        List<String> cleanedOptions = Arrays.stream(optionsRaw.split("[\\r\\n]+|(?<!\\))\\s+(?=[A-D]\\))"))
	            .map(String::trim)
	            .filter(opt -> !opt.isEmpty())
	            .collect(Collectors.toList());

	        String resolvedAnswer = correctAnswer;
	        if (correctAnswer.matches("^[A-Da-d]$")) {
	            int index = Character.toUpperCase(correctAnswer.charAt(0)) - 'A';
	            if (index >= 0 && index < cleanedOptions.size()) {
	                resolvedAnswer = cleanedOptions.get(index);
	            }
	        }

	        QuestionAnswerEntity qa = new QuestionAnswerEntity();
	        qa.setCategoryId(categoryId);
	        qa.setQuestion(sanitize(question));
	        qa.setAnswer(sanitize(resolvedAnswer));
	        qa.setOptions(sanitize(mapper.writeValueAsString(cleanedOptions)));
	        qa.setCreatedDate(new Timestamp(System.currentTimeMillis()));

	        questionList.add(qa);
	    } catch (Exception e) {
	        System.err.println("Skipping malformed record");
	        e.printStackTrace();
	    }
	}

	private void saveQuestionsAndDocument(List<QuestionAnswerEntity> questionList, Map<Long, CategoryEntity> categoryMap,
	                                      String originalFileName, String uploadedBy) {
	    for (QuestionAnswerEntity qa : questionList) {
	        if (qa.getCategoryId() != null) {
	            questionAnswerRepository.save(qa);
	        }
	    }

	    Long firstCategoryId = categoryMap.keySet().stream().findFirst().orElse(null);
	    if (firstCategoryId != null && categoryRepository.existsById(firstCategoryId)) {
	        QuestionnaireDocumentsEntity doc = new QuestionnaireDocumentsEntity();
	        doc.setCategoryId(firstCategoryId);
	        doc.setFileName(originalFileName);
	        doc.setUploadLocation("http://localhost:8081/api/files/" + originalFileName);
	        doc.setUploadedBy(uploadedBy);
	        doc.setUploadDate(LocalDateTime.now());
	        documentRepository.save(doc);
	    }
	}

	private String getCellValue(Cell cell) {
	    if (cell == null) return "";
	    switch (cell.getCellType()) {
	        case STRING: return cell.getStringCellValue().trim();
	        case NUMERIC: return String.valueOf(cell.getNumericCellValue()).trim();
	        case BOOLEAN: return String.valueOf(cell.getBooleanCellValue()).trim();
	        case FORMULA: return cell.getCellFormula().trim();
	        default: return "";
	    }
	}


	public static String sanitize(String input) {
	    return input.replaceAll("[^\\x00-\\x7F]", ""); // removes non-ASCII
	}

}
