package com.dachen.health.user.entity.po;

import com.dachen.health.commons.constants.UserEnum.Source;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Transient;

/**
 * Author: xuhuanjie
 * Date: 2018-09-05
 * Time: 14:42
 * Description:
 */
@Data
public class LoginUser {

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "医院名称")
    private String hospital;

    @ApiModelProperty(value = "科室名称")
    private String dept;

    @ApiModelProperty(value = "职称")
    private String title;

    @ApiModelProperty(value = "邀请人Id")
    private Integer inviteId;

    @ApiModelProperty(value = "邀请人姓名")
    private String inviteName;

    /**
     * @see Source
     */
    @ApiModelProperty(value = "注册来源")
    private String source;

    @ApiModelProperty(value = "登陆时间 取t_user_login_info表中loginTime字段")
    private Long loginTime;

    @ApiModelProperty(value = "学币余额 每次动态查询 避免同步")
    @Transient
    private Long credit;

    @ApiModelProperty(value = "是否禁用学币")
    private Boolean isBlack;

}
