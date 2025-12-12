package com.ker.demo.domin;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Assessment {
	
    private String questionCount;
    private Long templateId;
    private String templateName;
    private Long managerId;
    private String skillLevel;
    private List<Long> userIds;
    private LocalDateTime scheduledAt;
    private String timeLimitMinutes;
    private List<String> userNames;
}
