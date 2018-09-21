package com.dachen.health.user.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Author: xuhuanjie
 * Date: 2018-05-03
 * Time: 11:36
 * Description:
 */
@Data
public class DoctorDealingInfoVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "医生id")
    private Integer userId;

    @ApiModelProperty(value = "医生姓名")
    private String name;

    @ApiModelProperty(value = "头像")
    private String headPicFileName;

    @ApiModelProperty(value = "证书图片")
    private List<Map<String, String>> checkImage;

    @ApiModelProperty(value = "医院")
    private String hospital;

    @ApiModelProperty(value = "医院Id")
    private String hospitalId;

    @ApiModelProperty(value = "科室")
    private String departments;

    @ApiModelProperty(value = "科室Id")
    private String departId;

    @ApiModelProperty(value = "科室电话")
    private String deptPhone;

    @ApiModelProperty(value = "职称")
    private String title;

    @ApiModelProperty(value = "状态")
    private Integer status;

}
