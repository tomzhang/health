package com.dachen.health.commons.example;

import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.user.entity.po.Doctor;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-05-24
 * Time: 16:01
 * Description:
 */
@Data
public class UserExampleV2 extends BaseExample {


    @ApiModelProperty(required = true, value = "手机号")
    private String telephone;

    @ApiModelProperty(required = true, value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(required = true, value = "密码")
    private String password;

    @ApiModelProperty(value = "用户来源")
    private UserSource userSource;

    @ApiModelProperty(value = "签名")
    private String sign;

    @ApiModelProperty(value = "注册类型:(1:微信;2:第三方)")
    private String type;

    @ApiModelProperty(value = "Doctor PO")
    private Doctor doctor;

}
