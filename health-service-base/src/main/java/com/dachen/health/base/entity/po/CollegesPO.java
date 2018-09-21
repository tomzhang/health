package com.dachen.health.base.entity.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * Author: xuhuanjie
 * Date: 2018-08-28
 * Time: 14:57
 * Description: 院校基础数据
 */
@Data
@Entity(value = "b_colleges", noClassnameStored = true)
public class CollegesPO {

    @Id
    private String id;

    @ApiModelProperty(value = "院校名字")
    private String collegeName;

    @ApiModelProperty(value = "院校标识码")
    private String collegeCode;

    @ApiModelProperty(value = "院校省")
    private String collegeProvince;

    @ApiModelProperty(value = "院校地区")
    private String collegeArea;

    @ApiModelProperty(value = "院校层次")
    private String collegeLevel;

    private Long createTime;

    private Long modifyTime;

}
