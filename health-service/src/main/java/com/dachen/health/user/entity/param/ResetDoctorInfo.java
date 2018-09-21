package com.dachen.health.user.entity.param;

import com.dachen.health.commons.constants.DoctorInfoChangeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * Author: xuhuanjie
 * Date: 2018-04-26
 * Time: 17:19
 * Description:
 */
@Data
public class ResetDoctorInfo {

    @ApiModelProperty(value = "id(web端审核使用)")
    private String id;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "姓名")
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
    private String deptId;

    @ApiModelProperty(value = "科室电话")
    private String deptPhone;

    @ApiModelProperty(value = "职称")
    private String title;

    /**
     * @see DoctorInfoChangeEnum.VerifyResult
     */
    @ApiModelProperty(value = "处理结果(web端审核使用)(1：驳回；2：同意)")
    private Integer verifyResult;

    @ApiModelProperty(value = "处理人Id", hidden = true)
    private Integer checkerId;

}
