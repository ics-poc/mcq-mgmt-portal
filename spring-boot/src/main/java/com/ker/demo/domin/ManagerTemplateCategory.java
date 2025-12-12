package com.ker.demo.domin;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
//@AllArgsConstructor
public class ManagerTemplateCategory {

    private Long id;
    private String managerTemplateId;
    private String categoryId;
    private Integer weighage;
    private String difficultyLevel;
    private Timestamp createdDate;
    private Timestamp modifiedDate;
    
 // ✅ Add this constructor to match the one you're using
    public ManagerTemplateCategory(Long id, String managerTemplateId, String categoryId,
                                    Integer weighage, String difficultyLevel,
                                   Timestamp createdDate, Timestamp modifiedDate) {
        this.id = id;
        this.managerTemplateId = managerTemplateId;
        this.categoryId = categoryId;
        this.weighage = weighage;
        this.difficultyLevel = difficultyLevel;
        this.createdDate = createdDate;
        this.modifiedDate = modifiedDate;
    }
}
