package com.ker.demo.domin;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ReviewStatus {
	
	REVIEW_SCHEDULED(0, "Review Scheduled"),
    REVIEW_IN_PROGRESS(1, "Review In Progress"),
    REVIEW_COMPLETED(2, "Review Completed");

    private final int code;
    private final String label;

    ReviewStatus(int code, String label) {
        this.code = code;
        this.label = label;
    }

    public int getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    private static final Map<Integer, ReviewStatus> LOOKUP =
            Stream.of(values()).collect(Collectors.toMap(ReviewStatus::getCode, s -> s));

    public static String getLabelByCode(Integer code) {
        if (code == null) return REVIEW_SCHEDULED.label;
        return LOOKUP.getOrDefault(code, REVIEW_SCHEDULED).getLabel();
    }
}
