package com.ker.demo.domin;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Permission {
    private String code;
    private String description;
    private String createdUserId;
    private String modifiedUserId;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
}