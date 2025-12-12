package com.ker.demo.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ker.demo.domin.QuestionAnswer;
import com.ker.demo.entity.QuestionAnswerEntity;
import com.ker.demo.repository.QuestionAnswerRepository;
import com.ker.demo.service.QuestionAnswerService;

import jakarta.transaction.Transactional;

@Service
public class QuestionAnswerServiceImpl implements QuestionAnswerService {

	@Autowired
	private QuestionAnswerRepository repository;

	@Autowired
	private ObjectMapper objectMapper;

	@Override
	public List<QuestionAnswer> getQuestionsByCategoryId(Long categoryId) {
		return repository.findByCategoryId(categoryId).stream().map(this::convertToDomain).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void updateStatusFlags(Map<Long, String> questions) {
		questions.forEach((questionId, statusFlag) -> {
			repository.findById(questionId).ifPresent(entity -> {
				entity.setStatusFlag(statusFlag);
				repository.save(entity);
			});
		});
	}

	private QuestionAnswer convertToDomain(QuestionAnswerEntity entity) {
		List<String> options = parseOptions(entity.getOptions());
		String resolvedAnswer = resolveAnswer(entity.getOptions(), entity.getAnswer());

		return new QuestionAnswer(entity.getQuestionId(), entity.getCategoryId(), entity.getQuestion(), options,
				resolvedAnswer, entity.getCreatedDate(), entity.getStatusFlag());
	}

	private List<String> parseOptions(String json) {
		try {
			Map<String, String> optionMap = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			return new ArrayList<>(optionMap.values());
		} catch (Exception e) {
			return null;
		}
	}

	private String resolveAnswer(String json, String answerKey) {
		try {
			Map<String, String> optionMap = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {
			});
			return optionMap.get(answerKey);
		} catch (Exception e) {
			return null;
		}
	}
}
