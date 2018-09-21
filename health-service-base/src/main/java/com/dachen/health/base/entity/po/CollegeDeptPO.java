package com.dachen.health.base.entity.po;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Property;

/**
 * Author: xuhuanjie
 * Date: 2018-09-07
 * Time: 15:03
 * Description:
 */
@Data
@Entity(value = "b_college_dept", noClassnameStored = true)
public class CollegeDeptPO {

    @Property("_id")
    private Integer id;

    private String deptName;
}
