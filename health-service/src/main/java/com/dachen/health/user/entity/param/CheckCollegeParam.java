package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-08-31
 * Time: 11:42
 * Description:
 */
@Data
public class CheckCollegeParam {

    @ApiModelProperty(value = "关联的学习经历Id")
    private String learningExpId;

    @ApiModelProperty(value = "后台审核的院校名称")
    private String checkCollegeId;

    @ApiModelProperty(value = "后台审核的院校名称")
    private String checkCollegeName;

    @ApiModelProperty(value = "后台审核的用户Id")
    private Integer userId;

}
