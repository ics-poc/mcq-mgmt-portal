package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.QuestionAnswerEntity;

@Repository
public interface QuestionAnswerRepository extends JpaRepository<QuestionAnswerEntity, Long> {

	List<QuestionAnswerEntity> findByCategoryId(Long categoryId);
}
