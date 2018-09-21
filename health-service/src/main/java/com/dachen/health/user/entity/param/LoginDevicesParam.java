package com.dachen.health.user.entity.param;

import com.dachen.health.commons.constants.UserEnum.Source;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-09-06
 * Time: 20:47
 * Description:
 */
@Data
public class LoginDevicesParam {


    @ApiModelProperty(value = "邀请人姓名")
    private String inviterName;

    @ApiModelProperty(value = "医疗机构")
    private String hospital;

    @ApiModelProperty(value = "手机")
    private String phone;

    @ApiModelProperty(value = "姓名")
    private String name;

    @ApiModelProperty(value = "职称")
    private String title;

    /**
     * @see Source
     */
    @ApiModelProperty(value = "注册来源")
    private String source;

    @ApiModelProperty(value = "状态(0:未处理,1:已处理)")
    private Integer status;

    @ApiModelProperty(value = "查询开始时间")
    private String startTime;

    @ApiModelProperty(value = "查询结束时间")
    private String endTime;

    @ApiModelProperty(value = "只看重点设备")
    private Boolean onlyImportant;

    @ApiModelProperty(value = "页码")
    private Integer pageIndex;

    @ApiModelProperty(value = "每页大小")
    private Integer pageSize;

}
