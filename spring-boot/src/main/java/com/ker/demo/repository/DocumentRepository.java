package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.QuestionnaireDocumentsEntity;
import com.ker.demo.entity.UserEntity;

@Repository
public interface DocumentRepository extends JpaRepository<QuestionnaireDocumentsEntity, Long> {

	void save(UserEntity user);
	
	List<QuestionnaireDocumentsEntity> findAll();

}
