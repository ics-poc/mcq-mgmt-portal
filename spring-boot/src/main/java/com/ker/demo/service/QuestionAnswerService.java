package com.ker.demo.service;

import java.util.List;
import java.util.Map;

import com.ker.demo.domin.QuestionAnswer;

public interface QuestionAnswerService {

	List<QuestionAnswer> getQuestionsByCategoryId(Long categoryId);
	
	void updateStatusFlags(Map<Long, String> questions);

}
