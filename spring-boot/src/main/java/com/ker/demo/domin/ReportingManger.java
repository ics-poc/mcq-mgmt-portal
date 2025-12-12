package com.ker.demo.domin;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportingManger {
	
	private long userMangerId;
 
	private String managerName;

	private List<String> userId;
}

