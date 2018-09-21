package com.dachen.health.file.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-05
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Data
public class UserAfterUploadParam {

    @ApiModelProperty(value = "目录Id")
    private Integer directoryId;

    @ApiModelProperty(value = "目录名")
    private String directoryName;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "mime类型")
    private String mimeType;

    @ApiModelProperty(value = "文件hash值")
    private String hash;

    @ApiModelProperty(value = "文件key")
    private String key;

    @ApiModelProperty(value = "文件大小")
    private Long size;

    @ApiModelProperty(value = "用户Id")
    private Integer userId;

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

}
