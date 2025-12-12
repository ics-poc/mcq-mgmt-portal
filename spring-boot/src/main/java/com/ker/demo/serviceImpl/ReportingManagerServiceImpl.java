package com.ker.demo.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ker.demo.domin.ReportingManger;
import com.ker.demo.entity.ReportingMangerEntity;
import com.ker.demo.entity.UserEntity;
import com.ker.demo.repository.ReportingManagerRepo;
import com.ker.demo.repository.UserRepo;
import com.ker.demo.service.ReportingManagerService;

@Service
public class ReportingManagerServiceImpl implements ReportingManagerService{

	@Autowired
	private UserRepo userRespo;

	@Autowired
	private ReportingManagerRepo reportRepo;
	
	  // Create a new manager record
    public ReportingMangerEntity createManager(String managerId) {
        ReportingMangerEntity manager = new ReportingMangerEntity();
      //  manager.setManagerId(managerId);
      //  manager.setUserId(new ArrayList<>());
        return reportRepo.save(manager);
    }

    // Assign a user to a manager
    public ReportingMangerEntity assignUserToManager(Long managerId, Long userId) {
        ReportingMangerEntity manager = reportRepo.findById(managerId)
            .orElseThrow(() -> new RuntimeException("Manager not found"));

//        List<String> userIds = manager.getUserId();
//        if (userIds == null) {
//            userIds = new ArrayList<>();
//        }

//        String userIdStr = String.valueOf(userId);
//        if (!userIds.contains(userIdStr)) {
//            userIds.add(userIdStr);
//            manager.setUserId(userIds);
//            reportRepo.save(manager);
//        }

        return manager;
    }

    // Get all users under a manager
    @Transactional
    public List<UserEntity> getUsersByManager(Long managerId) {
//    	ReportingMangerEntity manager = reportRepo.findByIdWithUsers(managerId)
//    		    .orElseThrow(() -> new RuntimeException("Manager not found"));

       // List<String> userIds = manager.getUserId();
//        if (userIds == null || userIds.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<Long> longIds = userIds.stream()
//            .map(Long::valueOf)
//            .collect(Collectors.toList());

        return userRespo.findAllById(null);
    }
    public ReportingManger convertEntityToData(ReportingMangerEntity entity) {
        ReportingManger dto = new ReportingManger();
        dto.setUserMangerId(entity.getUserMangerId());
      //  dto.setManagerName(entity.getManagerId());
       // dto.setUserId(new ArrayList<>(entity.getUserId())); // Access while session is open
        return dto;
    }

}
