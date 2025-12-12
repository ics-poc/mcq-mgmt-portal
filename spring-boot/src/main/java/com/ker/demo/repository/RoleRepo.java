package com.ker.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ker.demo.entity.RoleEntity;

public interface RoleRepo extends JpaRepository<RoleEntity, Long> {
    Optional<RoleEntity> findByCode(String code);
    boolean existsByCode(String code);
}