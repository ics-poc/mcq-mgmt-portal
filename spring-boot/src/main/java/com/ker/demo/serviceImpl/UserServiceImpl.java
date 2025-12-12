package com.ker.demo.serviceImpl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ker.demo.domin.User;
import com.ker.demo.domin.UserStatus;
import com.ker.demo.entity.ReportingMangerEntity;
import com.ker.demo.entity.RoleEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.repository.ReportingManagerRepo;
import com.ker.demo.repository.RoleRepo;
import com.ker.demo.repository.UserRepo;
import com.ker.demo.service.RoleService;
import com.ker.demo.service.UserService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	
	@Autowired
	private RoleService roleService;

	@Autowired
	private UserRepo userRespo;

	@Autowired
	private RoleRepo rolerepo;

	@Autowired
	private ReportingManagerRepo reportRepo;
	
	@Override
	public User createUser(User user) {
	    if (user == null) {
	        throw new IllegalArgumentException("User object cannot be null.");
	    }

	    if (user.getEmail() != null && userRespo.existsByEmail(user.getEmail())) {
	        throw new IllegalArgumentException("Email '" + user.getEmail() + "' is already in use.");
	    }
	    
	    Optional<RoleEntity> roleData = roleService.getRoleByCode(user.getRoleCode());
	    user.setUserRoleId(roleData.get().getId());

	    user.setStatus(UserStatus.ACTIVE);
	    prepareUserForCreation(user);

	    UserEntity userEntity = convertDataToEntity(user);
	    userEntity.setCreatedUserId("ADMIN");
	    userEntity.setCreatedDate(Timestamp.valueOf(LocalDateTime.now()));

	    if (user.getUserRoleId() != null) {
	        userEntity.setUserRoleId(user.getUserRoleId());
	    }
	    userEntity.setReportingManager(null);
	    UserEntity savedUser = userRespo.save(userEntity);

	    RoleEntity role = null;
	    if (user.getUserRoleId() != null) {
	        role = rolerepo.findById(user.getUserRoleId()).orElse(null);
	    }

	    if (user.getUserRoleId() != null && user.getManagerId() != null) {
	        if (role != null && !("MANAGER".equalsIgnoreCase(role.getCode()) || "ADMIN".equalsIgnoreCase(role.getCode()))) {
	            ReportingMangerEntity managerEntity = new ReportingMangerEntity();
	            managerEntity.setManagerId(user.getManagerId());
	            managerEntity.setUserId(savedUser.getUserId());
	            ReportingMangerEntity savedManagerEntity = reportRepo.save(managerEntity);
	            
	            savedUser.setReportingManagerId(savedManagerEntity.getUserMangerId());
	            userRespo.save(savedUser);
	        }
	    }
	    savedUser = userRespo.findById(savedUser.getUserId())
	            .orElseThrow(() -> new RuntimeException("User not found after save"));

	    return convertEntityToData(savedUser);
	}

	private void prepareUserForCreation(User user) {
		Timestamp now = new Timestamp(System.currentTimeMillis());

		if (user.getLastLoginDate() == null) {
			user.setLastLoginDate(now);
		}
		if (user.getLastLogoutDate() == null) {
			user.setLastLogoutDate(now);
		}
		if (user.getUserRoleId() == null) {
			user.setUserRoleId(4L);
		}

		String base64Password = null;
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			base64Password = Base64.getEncoder().encodeToString(user.getPassword().getBytes());
			user.setPassword(base64Password);
		} else {
			base64Password = Base64.getEncoder().encodeToString("irm@2025".getBytes());
			user.setPassword(base64Password);
		}	
	}

	@Override
	public User updateUser(Long id, User updatedUser) {
		UserEntity existingUserEntity = userRespo.findById(id)
				.orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

		Long roleId = existingUserEntity.getUserRoleId();
		if (roleId == null) {
			throw new RuntimeException("User has no assigned role.");
		}

		RoleEntity role = rolerepo.findById(roleId)
				.orElseThrow(() -> new RuntimeException("Role not found for userRoleId: " + roleId));

		if ("MANAGER".equalsIgnoreCase(role.getCode())) {
			if (updatedUser.getFirstName() != null) {
				existingUserEntity.setFirstName(updatedUser.getFirstName());
			}
			if (updatedUser.getLastName() != null) {
				existingUserEntity.setLastName(updatedUser.getLastName());
			}
			if (updatedUser.getEmail() != null) {
				existingUserEntity.setEmail(updatedUser.getEmail());
			}
			if (updatedUser.getPhone() != null) {
				existingUserEntity.setPhone(updatedUser.getPhone());
			}
			if (updatedUser.getStatus() != null) {
				existingUserEntity.setStatus(updatedUser.getStatus().name());
			}
			if (updatedUser.getLanguage() != null) {
				existingUserEntity.setLanguage(updatedUser.getLanguage());
			}
			if (updatedUser.getEmployeeGrade() != null) {
				existingUserEntity.setEmployeeGrade(updatedUser.getEmployeeGrade());
			}
			if (updatedUser.getProject() != null) {
				existingUserEntity.setProject(updatedUser.getProject());
			}

			UserEntity savedEntity = userRespo.save(existingUserEntity);
			return convertEntityToData(savedEntity);
		}

		throw new RuntimeException("User does not have permission to update. Role 'MANAGER' required.");
	}

	@Transactional
	@Override
	public User getUserById(Long id) {
		return userRespo.findById(id).map(this::convertEntityToData)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	@Override
	public List<User> getAllUsers() {
		List<User> users = userRespo.findAll().stream().map(this::convertEntityToData).collect(Collectors.toList());
		if (users.isEmpty()) {
			throw new RuntimeException("No users found");
		}
		return users;
	}
	
	@Override
	public List<User> getAllManagerDetails() {
	    return userRespo.findAll().stream()
	        .filter(user -> {
	            RoleEntity role = rolerepo.findById(user.getUserRoleId()).orElse(null);
	            return role != null && "manager".equalsIgnoreCase(role.getCode());
	        })
	        .map(this::convertEntityToData)
	        .collect(Collectors.toList());
	}
	
	@Override
	public List<User> getAllUsersByManagerId(Long managerId) {
	    List<UserEntity> users = userRespo.findByReportingManagerId(managerId);
	    return users.stream().map(this::convertEntityToData).collect(Collectors.toList());
	}

	@Override
	public void deleteUserById(Long id) {
		if (!userRespo.existsById(id)) {
			throw new NoSuchElementException("User with ID " + id + " not found.");
		}
		userRespo.deleteById(id);
	}

	private User convertEntityToData(UserEntity userEntity) {
		User user = new User();
		user.setUserId(userEntity.getUserId());
		user.setFirstName(userEntity.getFirstName());
		user.setLastName(userEntity.getLastName());
		user.setEmail(userEntity.getEmail());
		user.setStatus(UserStatus.findByTextWithoutDefault(userEntity.getStatus()));
		user.setPhone(userEntity.getPhone());
		user.setUserRoleId(userEntity.getUserRoleId());
		user.setBusinessUnit(userEntity.getBusinessUnit());
		user.setEmployeeGrade(userEntity.getEmployeeGrade());
		user.setLastLoginDate(userEntity.getLastLoginDate());
		user.setLastLogoutDate(userEntity.getLastLogoutDate());
		user.setPassword("*******");
		user.setLanguage(userEntity.getLanguage());
		user.setTimezone(userEntity.getTimezone());
		user.setCreatedDate(userEntity.getCreatedDate());
		user.setEmployeeNumber(userEntity.getEmployeeNumber());
		user.setProject(userEntity.getProject());
		user.setCreatedUserId("ADMIN");
		user.setModifiedUserId(userEntity.getModifiedUserId());
		user.setManagerId(userEntity.getReportingManagerId());

		RoleEntity role = rolerepo.findById(userEntity.getUserRoleId())
				.orElseThrow(() -> new RuntimeException("Role not found"));
		user.setRoleCode(role.getCode());
		user.setRoleName(role.getName());

		return user;
	}

	private UserEntity convertDataToEntity(User user) {
		UserEntity userEntity = new UserEntity();
		userEntity.setUserId(user.getUserId());
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		userEntity.setEmail(user.getEmail());
		if (user.getStatus() != null) {
			userEntity.setStatus(user.getStatus().name());
		}
		userEntity.setPhone(user.getPhone());
		userEntity.setUserRoleId(user.getUserRoleId());
		userEntity.setLastLoginDate(user.getLastLoginDate());
		userEntity.setLastLogoutDate(user.getLastLogoutDate());
		userEntity.setPassword(user.getPassword());
		userEntity.setLanguage(user.getLanguage());
		userEntity.setTimezone(user.getTimezone());
		userEntity.setBusinessUnit(user.getBusinessUnit());
		userEntity.setEmployeeGrade(user.getEmployeeGrade());
		userEntity.setCreatedDate(user.getCreatedDate());
		userEntity.setEmployeeNumber(user.getEmployeeNumber());
		userEntity.setProject(user.getProject());
		userEntity.setCreatedUserId("ADMIN");
		userEntity.setModifiedUserId(user.getModifiedUserId());
		userEntity.setReportingManagerId(user.getManagerId());
		return userEntity;
	}

	@Override
	public User login(String email, String password) {
		if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			throw new IllegalArgumentException("Email and password are required");
		}

		UserEntity userEntity = userRespo.findByEmail(email);
		if (userEntity == null || !password.equals(userEntity.getPassword())) {
			throw new RuntimeException("Invalid credentials");
		}
		User user = convertEntityToData(userEntity);

		return user;
	}

}
