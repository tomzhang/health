package com.dachen.health.user.entity.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.*;

/**
 * Author: xuhuanjie
 * Date: 2018-08-29
 * Time: 18:05
 * Description: 医生学习经历
 */
@Data
@Entity(value = "t_learning_exp", noClassnameStored = true)
@Indexes(@Index("userId,collegeId"))
public class LearningExperience {

    @Id
    private String id;

    @ApiModelProperty(value = "关联的医生")
    private Integer userId;

    @ApiModelProperty(value = "院校ID")
    private String collegeId;

    @ApiModelProperty(value = "院校名称")
    private String collegeName;

    @ApiModelProperty(value = "学历")
    private String qualifications;

    @ApiModelProperty(value = "入学时间")
    private Long startTime;

    @ApiModelProperty(value = "院系")
    private String departments;

    private Long createTime;

    private Long modifyTime;

}
