package com.dachen.health.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-05-23
 * Time: 20:50
 * Description:
 */
@Data
public class IsNewAccountVO {

    @ApiModelProperty(value = "是否新账号")
    private Boolean isNewAccount;

    @ApiModelProperty(value = "令牌")
    private String token;

    @ApiModelProperty(value = "用户状态")
    private Integer userStatus;
    
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
