package com.dachen.health.user.entity.vo;

import com.dachen.health.user.entity.po.LoginUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Id;

import java.util.List;

/**
 * Author: xuhuanjie
 * Date: 2018-09-03
 * Time: 18:13
 * Description:
 */
@Data
public class AbnormalLoginVO {

    @Id
    private String id;

    @ApiModelProperty(value = "设备号")
    private String serial;

    @ApiModelProperty(value = "是否为重点关注设备，发送短信则标为重点")
    private Boolean important;

    @ApiModelProperty(value = "同一设备不同手机号的登录次数")
    private Integer count;

    @ApiModelProperty(value = "该设备登陆的用户列表")
    private List<LoginUser> loginUser;

}
