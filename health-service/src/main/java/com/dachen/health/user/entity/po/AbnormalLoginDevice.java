package com.dachen.health.user.entity.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.List;

/**
 * Author: xuhuanjie
 * Date: 2018-09-05
 * Time: 14:37
 * Description:
 */
@Data
@Entity(value = "t_abnormal_device", noClassnameStored = true)
public class AbnormalLoginDevice {

    @Id
    private String id;

    @ApiModelProperty(value = "设备Id 取t_user_login_info表中serial字段")
    @Indexed
    private String deviceId;

    @ApiModelProperty(value = "异常次数 一天只算一次异常")
    private Integer abnormalCount;

    @ApiModelProperty(value = "是否为重点关注设备，发送短信则标为重点")
    private Boolean important;

    @ApiModelProperty(value = "0 待处理 1 已处理")
    private Integer status;

    @ApiModelProperty(value = "处理人Id")
    private String handlerId;

    @ApiModelProperty(value = "处理人姓名")
    private String handlerName;

    @ApiModelProperty(value = "处理时间")
    private Long handleTime;

    @ApiModelProperty(value = "该设备登陆的用户列表")
    private List<LoginUser> loginUsers;

    private Long createTime;

    private Long modifyTime;

}
