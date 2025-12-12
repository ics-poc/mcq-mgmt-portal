package com.ker.demo.service;

import java.util.List;

import com.ker.demo.domin.User;


public interface UserService {
	
	User createUser(User user);

	User getUserById(Long id);

	List<User> getAllUsers();

	User updateUser(Long id, User updatedUser);

	void deleteUserById(Long id);

	User login(String email, String password);
	
	List<User> getAllManagerDetails();
	
	List<User> getAllUsersByManagerId(Long managerId);

}
