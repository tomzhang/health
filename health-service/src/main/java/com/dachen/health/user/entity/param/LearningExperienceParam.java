package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-08-29
 * Time: 17:47
 * Description:
 */
@Data
public class LearningExperienceParam {

    @ApiModelProperty(value = "学习经历ID(更新时候传)")
    private String id;

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

}
