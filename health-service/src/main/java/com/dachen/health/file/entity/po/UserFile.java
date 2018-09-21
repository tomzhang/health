package com.dachen.health.file.entity.po;

import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.file.constant.UserFileEnum.UserFileBucketType;
import com.dachen.health.file.constant.UserFileEnum.UserFileSourceType;
import com.dachen.health.file.constant.UserFileEnum.UserFileStatus;
import com.dachen.health.file.constant.UserFileEnum.UserFileType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-01
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Data
@Entity(value = "t_user_file", noClassnameStored = true)
@Indexes(@Index("-createTime,ownerId"))
public class UserFile {

    @Id
    private ObjectId id;

    @ApiModelProperty(value = "文件名")
    private String name;

    @ApiModelProperty(value = "目录Id")
    @Indexed(IndexDirection.ASC)
    private Integer directoryId;

    @ApiModelProperty(value = "目录名")
    private String directoryName;

    @ApiModelProperty(value = "文件后缀")
    private String suffix;

    @ApiModelProperty(value = "mime类型")
    private String mimeType;

    @ApiModelProperty(value = "文件大小（字节单位）")
    private Long size;

    @ApiModelProperty(value = "空间名")
    private String spaceName;

    @ApiModelProperty(value = "下载url")
    private String url;

    @ApiModelProperty(value = "文件hash值")
    @Indexed(IndexDirection.ASC)
    private String hash;

    /**
     * @see UserFileType
     */
    @ApiModelProperty(value = "文件分类")
    private Integer fileType;

    /**
     * @see UserFileBucketType
     */
    @ApiModelProperty(value = "公有|私有")
    private Integer bucketType;

    /**
     * @see UserFileStatus
     */
    @ApiModelProperty(value = "文件状态")
    private Integer status;

    @ApiModelProperty(value = "拥有者Id")
    private Integer ownerId;

    /**
     * @see UserFileSourceType
     */
    @ApiModelProperty(value = "文件来源（自己上传或者接收过来的）")
    private Integer sourceType;

    /**
     * @see UserType
     */
    @ApiModelProperty(value = "上传者的用户类型")
    private Integer userType;

    @ApiModelProperty(value = "更新人Id")
    private Integer modifyUser;

    @ApiModelProperty(value = "创建时间")
    private Long createTime;

    @ApiModelProperty(value = "更新时间")
    private Long modifyTime;

}
