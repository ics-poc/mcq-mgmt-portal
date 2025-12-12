package com.ker.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.PermissionEntity;

@Repository
public interface PermissionRepo extends JpaRepository<PermissionEntity, String> {

}