package com.dachen.health.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-05-29
 * Time: 10:58
 * Description:
 */
@Data
public class SimUserVO {

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "头像")
    private String headPicFileName;

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "所属医院")
    private String hospital;

    @ApiModelProperty(value = "所属医院Id")
    private String hospitalId;

    @ApiModelProperty(value = "所属科室")
    private String departments;

    @ApiModelProperty(value = "科室Id")
    private String deptId;

    @ApiModelProperty(value = "职称")
    private String title;

}
