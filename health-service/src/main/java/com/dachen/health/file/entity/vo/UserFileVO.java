package com.dachen.health.file.entity.vo;

import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.file.constant.UserFileEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-06
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Data
public class UserFileVO {

    private static double ONE_BYTES = 1;
    private static double ONE_KB = ONE_BYTES * 1024;
    private static double ONE_MB = ONE_KB * 1024;
    private static double ONE_GB = ONE_MB * 1024;
    private static double ONE_TB = ONE_GB * 1024;
    private static double ONE_PB = ONE_TB * 1024;

    @ApiModelProperty(value = "文件id")
    private String fileId;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "目录Id")
    private Integer directoryId;

    @ApiModelProperty(value = "目录名")
    private String directoryName;

    @ApiModelProperty(value = "文件后缀")
    private String suffix;

    @ApiModelProperty(value = "mime类型")
    private String mimeType;

    @ApiModelProperty(value = "文件大小（字节单位）")
    private Long size = 0L;

    @ApiModelProperty(value = "文件大小（不满1KB，显示单位为bytes；不满1MB，显示单位为KB；不满1GB的，显示单位为MB）")
    private String sizeStr;

    @ApiModelProperty(value = "空间名")
    private String spaceName;

    @ApiModelProperty(value = "下载url")
    private String url;

    @ApiModelProperty(value = "文件hash值")
    private String hash;

    /**
     * @see UserFileEnum.UserFileType
     */
    @ApiModelProperty(value = "文件分类")
    private Integer fileType;

    /**
     * @see UserFileEnum.UserFileBucketType
     */
    @ApiModelProperty(value = "公有|私有")
    private Integer bucketType;

    /**
     * @see UserFileEnum.UserFileStatus
     */
    @ApiModelProperty(value = "文件状态")
    private Integer status;

    @ApiModelProperty(value = "拥有者Id")
    private Integer ownerId;

    @ApiModelProperty(value = "拥有者名字")
    private String ownerName;

    /**
     * @see UserFileEnum.UserFileSourceType
     */
    @ApiModelProperty(value = "文件来源（自己上传或者接收过来的）")
    private Integer sourceType;

    /**
     * @see UserEnum.UserType
     */
    @ApiModelProperty(value = "上传者的用户类型")
    private Integer userType;

    @ApiModelProperty(value = "更新人Id")
    private Integer modifyUser;

    @ApiModelProperty(value = "上传时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

}
