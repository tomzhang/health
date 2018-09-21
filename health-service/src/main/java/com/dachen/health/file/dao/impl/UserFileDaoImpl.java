package com.dachen.health.file.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.file.constant.UserFileEnum;
import com.dachen.health.file.dao.IUserFileDao;
import com.dachen.health.file.entity.param.UserFileParam;
import com.dachen.health.file.entity.po.UserFile;
import com.dachen.health.file.entity.po.UserSendRecord;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-06
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Repository
public class UserFileDaoImpl extends NoSqlRepository implements IUserFileDao {

    @Override
    public Map<String, Object> searchFile(UserFileParam param, String sortAttr, int sortType) {
        Map<String, Object> map = new HashMap<>();
        String fileNameKey = param.getFileNameKey();
        Integer fileType = param.getFileType();
        Integer ownerId = param.getOwnerId();
        List<String> fileIds = param.getFileIdList();
        Query<UserFile> query = dsForRW.createQuery(UserFile.class);
        //模糊匹配 文件名
        if (StringUtils.isNotBlank(fileNameKey)) {
            query.field("name").containsIgnoreCase(fileNameKey);
        }
        if (Objects.nonNull(fileType) && !Objects.equals(fileType, 0)) {
            query.field("fileType").equal(fileType);
        }
        if (!CollectionUtils.isEmpty(fileIds)) {
            List<ObjectId> objectIds = fileIds.stream().map(ObjectId::new).collect(Collectors.toList());
            query.or(
                    query.criteria("ownerId").equal(ownerId).criteria("status").equal(UserFileEnum.UserFileStatus.common.getValue()),
                    query.criteria("_id").in(objectIds)
            );
        } else {
            query.field("ownerId").equal(ownerId);
            query.field("status").equal(UserFileEnum.UserFileStatus.common.getValue());
        }
        //排序
        if (sortType > 0) {
            //顺排
            query.order(sortAttr);
        } else {
            //倒排（默认）
            query.order("-" + sortAttr);
        }

        //分页
        query.offset(param.getPageSize() * param.getPageIndex());
        query.limit(param.getPageSize());

        List<UserFile> list = query.asList();
        map.put("list", list);
        long count = query.countAll();
        map.put("count", count);
        return map;
    }

    @Override
    public String addUserFile(UserFile userFile) {
        return dsForRW.insert(userFile).getId().toString();
    }

    @Override
    public void deleteUserFiles(List<String> fileIds) {
        List<ObjectId> objectIds = fileIds.stream().map(ObjectId::new).collect(Collectors.toList());
        BasicDBObject insIn = new BasicDBObject();
        insIn.put("$in", objectIds);
        DBObject query = new BasicDBObject("_id", insIn);
        DBObject update = new BasicDBObject("status", UserFileEnum.UserFileStatus.delete.getValue());
        dsForRW.getDB().getCollection("t_user_file").updateMulti(query, new BasicDBObject("$set", update));
    }

    @Override
    public UserFile findUserFile(String newName, Integer value, Integer userId) {
        //通过文件名，文件状态，上传人 三个参数进行查询
        Query<UserFile> query = dsForRW.createQuery(UserFile.class);
        if (!StringUtils.isEmpty(newName)) {
            query.field("name").equal(newName);
        } else {
            throw new ServiceException("文件名为空");
        }
        if (Objects.nonNull(value)) {
            query.field("status").equal(value);
        } else {
            throw new ServiceException("文件状态为空");
        }
        if (Objects.nonNull(userId) && !Objects.equals(userId, 0)) {
            query.field("ownerId").equal(userId);
        } else {
            throw new ServiceException("文件名上传人为空");
        }
        return query.get();
    }

    @Override
    public void updateUserFileName(String id, String newName, String newSuffix, Integer newFileType) {
        DBObject query = new BasicDBObject("_id", new ObjectId(id));
        DBObject update = new BasicDBObject();
        //更新文件名，顺带更新文件后缀 和 type类型
        update.put("name", newName);
        update.put("suffix", newSuffix);
        update.put("fileType", newFileType);
        dsForRW.getDB().getCollection("t_user_file").update(query, new BasicDBObject("$set", update));
    }

    @Override
    public UserFile getUserFileByFileId(String fileId) {
        Query<UserFile> query = dsForRW.createQuery(UserFile.class);
        query.field("_id").equal(new ObjectId(fileId));
        query.field("status").equal(UserFileEnum.UserFileStatus.common.getValue());
        return query.get();
    }

    @Override
    public UserFile getUserFileByHashAndOwnerId(String hash, Integer userId) {
        Query<UserFile> query = dsForRW.createQuery(UserFile.class);
        query.field("hash").equal(hash);
        query.field("ownerId").equal(userId);
        query.field("status").equal(UserFileEnum.UserFileStatus.common.getValue());
        return query.get();
    }

    @Override
    public UserSendRecord findByMsgId(String msgId) {
        Query<UserSendRecord> query = dsForRW.createQuery(UserSendRecord.class);
        query.field("msgId").equal(msgId);
        return query.get();
    }

    @Override
    public void addUserSendRecord(UserSendRecord userSendRecord) {
        dsForRW.insert(userSendRecord);
    }

}
