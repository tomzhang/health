package com.dachen.health.user.entity.vo;

import com.dachen.health.commons.constants.DoctorInfoChangeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Author: xuhuanjie
 * Date: 2018-04-28
 * Time: 17:04
 * Description:
 */
@Data
public class DoctorRecheckInfoDetailVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "医生id")
    private Integer userId;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "医生姓名")
    private String name;

    @ApiModelProperty(value = "头像")
    private String headPicFileName;

    @ApiModelProperty(value = "证书图片")
    private List<String> checkImage;

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

    /**
     * @see DoctorInfoChangeEnum.VerifyResult
     */
    @ApiModelProperty(value = "处理结果(1：驳回；2：同意)")
    private Integer verifyResult;

}
