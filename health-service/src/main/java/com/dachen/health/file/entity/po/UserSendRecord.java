package com.dachen.health.file.entity.po;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

import java.util.List;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-02
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Data
@Entity(value = "t_user_file_record", noClassnameStored = true)
public class UserSendRecord {

    @Id
    private ObjectId id;

    @ApiModelProperty(value = "文件id")
    @Indexed()
    private String fileId;

    @ApiModelProperty(value = "发送人id")
    @Indexed()
    private Integer sendUserId;

    @ApiModelProperty(value = "接收人id")
    private List<Integer> receiveUserId;

    @ApiModelProperty(value = "文件发送时间")
    private Long sendTime;

    @ApiModelProperty(value = "文件名（冗余）")
    private String name;

    @ApiModelProperty(value = "文件后缀（冗余）")
    private String suffix;

    @ApiModelProperty(value = "mime类型（冗余）")
    private String mimeType;

    @ApiModelProperty(value = "文件分类（冗余）")
    private Integer fileType;

    @ApiModelProperty(value = "文件大小（冗余）")
    private Long size;

    @ApiModelProperty(value = "文件下载地址（冗余）")
    private String url;

    @ApiModelProperty(value = "文件类型（公有，私有，会影响下载所以必须冗余）")
    private Integer bucketType;

    @ApiModelProperty(value = "会话组id")
    private String groupId;

    @ApiModelProperty(value = "会话组名称")
    private String IMName;

    @ApiModelProperty(value = "来源名称")
    private String sourceName;

    @ApiModelProperty(value = "文件发送目标名称")
    private String targetName;

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "消息id")
    private String msgId;

    @ApiModelProperty(value = "文件状态")
    private Integer status;

}
