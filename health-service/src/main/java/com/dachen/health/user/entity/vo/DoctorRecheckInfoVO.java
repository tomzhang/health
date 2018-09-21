package com.dachen.health.user.entity.vo;

import com.dachen.health.commons.constants.DoctorInfoChangeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-04-28
 * Time: 14:17
 * Description:
 */
@Data
public class DoctorRecheckInfoVO {

    @ApiModelProperty(value = "id")
    private String id;

    @ApiModelProperty(value = "医生id")
    private Integer userId;

    @ApiModelProperty(value = "手机号")
    private String telephone;

    @ApiModelProperty(value = "医生姓名")
    private String name;

    /**
     * @see DoctorInfoChangeEnum.InfoStatus
     */
    @ApiModelProperty(value = "认证信息状态(1：未处理；2：已处理)")
    private Integer infoStatus;

    /**
     * @see DoctorInfoChangeEnum.VerifyResult
     */
    @ApiModelProperty(value = "处理结果(1：驳回；2：同意)")
    private Integer verifyResult;

    @ApiModelProperty(value = "处理时间")
    private Long verifyTime;

    @ApiModelProperty(value = "申请时间")
    private Long createTime;

}
