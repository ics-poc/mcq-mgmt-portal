package com.ker.demo.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ker.demo.entity.ReportingMangerEntity;

@Repository
public interface ReportingManagerRepo extends JpaRepository<ReportingMangerEntity, Long> {

	@Query("SELECT m FROM ReportingMangerEntity m JOIN FETCH m.userId WHERE m.userMangerId = :id")
	Optional<ReportingMangerEntity> findByIdWithUsers(@Param("id") Long id);
	
	@Query("SELECT m FROM ReportingMangerEntity m WHERE m.managerId = :managerId")
    List<ReportingMangerEntity> findAllByManagerId(@Param("managerId") Long managerId);
}
