package com.dachen.health.file.entity.param;

import com.dachen.health.file.constant.UserFileEnum.UserFileSortAttr;
import com.dachen.health.file.constant.UserFileEnum.UserFileType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-05
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Data
public class UserFileParam {

    @ApiModelProperty(value = "拥有者Id")
    private Integer ownerId;

    @ApiModelProperty(value = "搜索 文件名称关键字")
    private String fileNameKey;

    @ApiModelProperty(value = "目录Id")
    private Integer directoryId;

    @ApiModelProperty(value = "目录名")
    private String directoryName;

    /**
     * @see UserFileType
     */
    @ApiModelProperty(value = "文件分类")
    private Integer fileType;

    /**
     * @see UserFileSortAttr
     */
    @ApiModelProperty(value = "排序属性")
    private Integer sortAttr;

    @ApiModelProperty(value = "排序顺序 1=顺序，-1=倒序")
    private Integer sortType;

    @ApiModelProperty(value = "文件Ids")
    private List<String> fileIdList;

    @ApiModelProperty(value = "页码")
    protected Integer pageIndex = 0;

    @ApiModelProperty(value = "每页数据大小")
    protected Integer pageSize = 15;

}
