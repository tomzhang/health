package com.dachen.health.file.dao;

import com.dachen.health.file.entity.param.UserFileParam;
import com.dachen.health.file.entity.po.UserFile;
import com.dachen.health.file.entity.po.UserSendRecord;

import java.util.List;
import java.util.Map;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-06
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IUserFileDao {

    Map<String, Object> searchFile(UserFileParam param, String sortAttr, int sortType);

    String addUserFile(UserFile userFile);

    void deleteUserFiles(List<String> fileIds);

    UserFile findUserFile(String newName, Integer value, Integer userId);

    void updateUserFileName(String id, String newName, String newSuffix, Integer newFileType);

    UserFile getUserFileByFileId(String fileId);

    UserFile getUserFileByHashAndOwnerId(String hash, Integer userId);

    UserSendRecord findByMsgId(String msgId);

    void addUserSendRecord(UserSendRecord userSendRecord);
}
