package com.ker.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.UserQuestionAnswerResultsEntity;

public interface UserQuestionAnswerResultsRepository extends JpaRepository<UserQuestionAnswerResultsEntity, Long> {
    List<UserQuestionAnswerResultsEntity> findByUserIdAndManagerTemplateCategoryId(Long userId, Long managerTemplateCategoryId);
}
