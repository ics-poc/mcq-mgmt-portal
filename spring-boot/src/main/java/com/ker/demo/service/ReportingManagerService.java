package com.ker.demo.service;

import java.util.List;

import com.ker.demo.entity.ReportingMangerEntity;
import com.ker.demo.entity.UserEntity;

public interface ReportingManagerService {

	ReportingMangerEntity createManager(String managerId);

	ReportingMangerEntity assignUserToManager(Long managerId, Long userId);

	List<UserEntity> getUsersByManager(Long managerId);

}
