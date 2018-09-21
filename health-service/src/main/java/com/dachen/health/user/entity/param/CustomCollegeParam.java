package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-08-30
 * Time: 17:56
 * Description:
 */
@Data
public class CustomCollegeParam {

    private String id;

    @ApiModelProperty(value = "关联的医生")
    private Integer userId;

    @ApiModelProperty(value = "关联的学习经历Id")
    private String learningExpId;

    @ApiModelProperty(value = "用户提交的院校名称")
    private String customCollegeName;

    @ApiModelProperty(value = "手机")
    private String telephone;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "状态(0:未处理,1:已处理)")
    private Integer status;

    @ApiModelProperty(value = "页码")
    private Integer pageIndex;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

}
