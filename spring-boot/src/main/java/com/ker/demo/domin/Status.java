package com.ker.demo.domin;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Status {
	private Long userId;
    private Map<String, Long> statusCounts;
}
