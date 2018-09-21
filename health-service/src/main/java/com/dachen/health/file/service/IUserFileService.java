package com.dachen.health.file.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.file.entity.param.UserAfterUploadParam;
import com.dachen.health.file.entity.param.UserFileParam;
import com.dachen.health.file.entity.param.UserSaveFileParam;

import java.util.List;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-05
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IUserFileService {

    /**
     * 保存文件元信息
     *
     * @param param see UserAfterUploadParam
     * @return 文件Id
     */
    String saveUploadFileInfo(UserAfterUploadParam param);

    /**
     * 通用文件元信息保存调用
     *
     * @param param  UserAfterUploadParam
     * @param domain 访问文件使用的域名
     * @param bucket 保存文件的空间名
     * @return 文件Id
     */
    String saveCommonFileInfo(UserAfterUploadParam param, String domain, String bucket);

    /**
     * 删除 上传的文件（软删除）
     *
     * @param fileIds 文件Ids
     */
    void deleteFile(List<String> fileIds);

    /**
     * 更新文件名
     *  @param id          文件id
     * @param newName     新的文件名
     */
    void updateFileName(String id, String newName, Integer userId);

    /**
     * 搜索 文件列表（分页）
     *
     * @param param see UserFileParam
     * @return
     */
    PageVO searchFile(UserFileParam param);

    String saveFileInfo(UserSaveFileParam param);

    Boolean isInMyFileList(String id);

    void sendFile(String groupId, List<String> fileIds);
}
