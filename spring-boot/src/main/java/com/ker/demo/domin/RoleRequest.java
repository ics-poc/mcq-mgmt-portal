package com.ker.demo.domin;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
	
    private String code;
    private String name;
    private String type;
    private Set<String> permissionCodes;

}
