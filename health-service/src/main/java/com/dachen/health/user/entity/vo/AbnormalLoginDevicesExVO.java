package com.dachen.health.user.entity.vo;

import com.dachen.health.commons.constants.UserEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-09-10
 * Time: 21:00
 * Description:
 */
@Data
public class AbnormalLoginDevicesExVO {

    @ApiModelProperty(value = "设备Id 取t_user_login_info表中serial字段")
    private String deviceId;

    @ApiModelProperty(value = "异常次数 一天只算一次异常")
    private Integer abnormalCount;

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
     * @see UserEnum.Source
     */
    @ApiModelProperty(value = "注册来源")
    private String source;

    @ApiModelProperty(value = "登陆时间")
    private Long loginTime;

    @ApiModelProperty(value = "0 待处理 1 已处理")
    private Integer status;

    @ApiModelProperty(value = "处理人Id")
    private String handlerId;

    @ApiModelProperty(value = "处理人姓名")
    private String handlerName;

    @ApiModelProperty(value = "处理时间")
    private Long handleTime;

}
