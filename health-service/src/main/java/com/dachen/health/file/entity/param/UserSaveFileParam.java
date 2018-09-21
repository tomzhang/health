package com.dachen.health.file.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Author: xuhuanjie
 * Date: 2018-05-15
 * Time: 18:29
 * Description:
 */
@Data
public class UserSaveFileParam {

    @ApiModelProperty(value = "目录Id")
    private Integer directoryId;

    @ApiModelProperty(value = "目录名")
    private String directoryName;

    @ApiModelProperty(value = "目录名")
    private String fileId;

}
