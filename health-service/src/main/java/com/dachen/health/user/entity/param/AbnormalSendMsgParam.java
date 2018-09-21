package com.dachen.health.user.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-09-10
 * Time: 11:00
 * Description:
 */
@Data
public class AbnormalSendMsgParam {

    @ApiModelProperty(value = "手机号")
    private String phone;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "设备号")
    private String deviceId;

}
