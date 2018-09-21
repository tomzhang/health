package com.dachen.health.base.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-08-29
 * Time: 15:06
 * Description:
 */
@Data
public class CollegeParam {

    @ApiModelProperty(value = "院校id(更新时候传)")
    private String id;

    @ApiModelProperty(value = "院校名字")
    private String collegeName;

    @ApiModelProperty(value = "院校标识码")
    private String collegeCode;

    @ApiModelProperty(value = "院校省")
    private String collegeProvince;

    @ApiModelProperty(value = "院校所在市")
    private String collegeArea;

    @ApiModelProperty(value = "院校级别")
    private String collegeLevel;

    @ApiModelProperty(value = "页码(Web查询时候传)")
    private Integer pageIndex;

    @ApiModelProperty(value = "每页大小(Web查询时候传)")
    private Integer pageSize;

}
