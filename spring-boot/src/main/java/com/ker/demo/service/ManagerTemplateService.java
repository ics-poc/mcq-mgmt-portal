package com.ker.demo.service;

import java.util.List;

import com.ker.demo.domin.ManagerTemplate;

public interface ManagerTemplateService {

	ManagerTemplate createManagerTemplate(ManagerTemplate template);

	ManagerTemplate getManagerTemplateWithCategories(Long id);

	void deleteManagerTemplate(Long id);

	ManagerTemplate updateManagerTemplate(Long id, ManagerTemplate updatedTemplate);

	List<ManagerTemplate> getAllManagerTemplates();
}
