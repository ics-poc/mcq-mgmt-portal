package com.ker.demo.domin;

public enum UserStatus {
	ACTIVE, TERMINATED, DISABLED;

	public static UserStatus findByText(String value) {
		for (UserStatus state : UserStatus.values()) {
			if (state.name().equalsIgnoreCase(value)) {
				return state;
			}
		}
		return ACTIVE;
	}

	public static UserStatus findByTextWithoutDefault(String value) {
		for (UserStatus state : UserStatus.values()) {
			if (state.name().equalsIgnoreCase(value)) {
				return state;
			}
		}
		return null;
	}

	public boolean isActive() {
		return this == ACTIVE;
	}

}
