package com.ker.demo.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.UserEntity;

@Repository
public interface UserRepo extends JpaRepository<UserEntity, Long> {

	UserEntity findByEmail(String email);

	boolean existsByEmail(String email);

	List<UserEntity> findByUserIdIn(Set<Long> userIds);
	
	List<UserEntity> findByReportingManagerId(Long reportingManagerId);
}
