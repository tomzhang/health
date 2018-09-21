package com.dachen.health.user.entity.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.*;

/**
 * Author: xuhuanjie
 * Date: 2018-08-30
 * Time: 17:49
 * Description: 医生院校变更处理,医生变更的院校是自己填写的,需后台审核
 */
@Data
@Entity(value = "t_custom_college", noClassnameStored = true)
@Indexes(@Index("userId,learningExpId,checkCollegeId"))
public class CustomCollege {
    @Id
    private String id;

    @ApiModelProperty(value = "关联的医生")
    private Integer userId;

    @ApiModelProperty(value = "关联的学习经历Id")
    private String learningExpId;

    @ApiModelProperty(value = "医生提交的院校名称")
    private String customCollegeName;

    @ApiModelProperty(value = "审核后的院校ID")
    private String checkCollegeId;

    @ApiModelProperty(value = "审核后的院校名称")
    private String checkCollegeName;

    @ApiModelProperty(value = "手机")
    private String telephone;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "状态(0:未处理,1:已处理)")
    private Integer status;

    private Long createTime;

    private Long modifyTime;

}
