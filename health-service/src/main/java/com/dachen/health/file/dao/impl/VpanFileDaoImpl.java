package com.dachen.health.file.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.file.constant.VpanFileEnum.VpanFileStatus;
import com.dachen.health.file.dao.IVpanFileDao;
import com.dachen.health.file.entity.po.VpanSendFile;
import com.dachen.health.file.entity.po.VpanSendRecord;
import com.dachen.health.file.entity.po.VpanUploadFile;
import com.dachen.util.ReqUtil;
import com.google.common.collect.Lists;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 文件管理dao实现类
 *
 * @author wangqiao
 * @date 2016年1月13日
 */
@Repository
public class VpanFileDaoImpl extends NoSqlRepository implements IVpanFileDao {

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#addVpanUploadFile(com.dachen.health.file.entity.po.VpanUploadFile)
     */
    public VpanUploadFile addVpanUploadFile(VpanUploadFile file) {
        //新增记录
        Object id = dsForRW.insert(file).getId();
        try {
            file.setId(new ObjectId(id.toString()));
        } catch (Exception e) {
            throw new ServiceException("文件id错误");
        }


        return file;
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#addVpanSendFile(com.dachen.health.file.entity.po.VpanSendFile)
     */
    public void addVpanSendFile(List<VpanSendFile> sendFileList) {
        if (sendFileList == null || sendFileList.size() == 0) {
            return;
        }

        //新增记录
//		dsForRW
        dsForRW.insert(sendFileList.toArray());
//		for(VpanSendFile sendFile:sendFileList){
//			dsForRW.insert(sendFile);
//		}


        return;
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#findVpanUploadFile(java.lang.String, java.lang.String, java.lang.Integer)
     */
    public VpanUploadFile findVpanUploadFile(String name, String status, Integer uploader) {
        //通过文件名，文件状态，上传人 三个参数进行查询
        Query<VpanUploadFile> query = dsForRW.createQuery(VpanUploadFile.class);
        if (name != null && !StringUtils.isEmpty(name)) {
            query.field("name").equal(name);
        } else {
            throw new ServiceException("文件名为空");
        }
        if (status != null && !StringUtils.isEmpty(status)) {
            query.field("status").equal(status);
        } else {
            throw new ServiceException("文件状态为空");
        }
        if (uploader != null && uploader != 0) {
            query.field("uploader").equal(uploader);
        } else {
            throw new ServiceException("文件名上传人为空");
        }
        return query.get();
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#searchVpanUploadFile(java.lang.String, java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    public Map<String, Object> searchVpanUploadFile(String nameKey, String status, Integer uploader,
                                                    String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex) {
        Map<String, Object> map = new HashMap<String, Object>();

        Query<VpanUploadFile> query = dsForRW.createQuery(VpanUploadFile.class);
        //模糊匹配 文件名
        if (nameKey != null && !StringUtils.isEmpty(nameKey)) {
            query.field("name").containsIgnoreCase(nameKey);
        }
        //状态适配
        if (status != null && !StringUtils.isEmpty(status)) {
            query.field("status").equal(status);
        }
        if (uploader != null && uploader != 0) {
            query.field("uploader").equal(uploader);
        }
        if (type != null && !StringUtils.isEmpty(type)) {
            query.field("type").equal(type);
        }

        //排序
        if (sortAttr != null && !StringUtils.isEmpty(sortAttr)) {
            if (sortType != null && sortType > 0) {
                //顺排
                query.order(sortAttr);
            } else {
                //倒排（默认）
                query.order("-" + sortAttr);
            }
        }

        //分页
        query.offset(pageSize * pageIndex);
        query.limit(pageSize);


//		long count = query.countAll();
        List<VpanUploadFile> list = query.asList();
        map.put("list", list);
        long count = query.countAll();
        map.put("count", count);

        return map;
    }


    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#searchVpanSendFile(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    public Map<String, Object> searchVpanSendFile(List<String> fileIdList, String nameKey, Integer receiveUserId,
                                                  String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex) {
        Map<String, Object> map = new HashMap<String, Object>();

        Query<VpanSendFile> query = dsForRW.createQuery(VpanSendFile.class);
        //模糊匹配 文件名
        if (nameKey != null && !StringUtils.isEmpty(nameKey)) {
            query.field("name").containsIgnoreCase(nameKey);
        }
        //状态适配
        if (receiveUserId != null && receiveUserId != 0) {
            query.field("receiveUserId").equal(receiveUserId);
        }
        if (type != null && !StringUtils.isEmpty(type)) {
            query.field("type").equal(type);
        }
        if (fileIdList != null && fileIdList.size() > 0) {
            query.field("fileId").in(fileIdList);
        }
        //排序
        if (sortAttr != null && !StringUtils.isEmpty(sortAttr)) {
            if (sortType != null && sortType > 0) {
                //顺排
                query.order(sortAttr);
            } else {
                //倒排（默认）
                query.order("-" + sortAttr);
            }
        }
        query.offset(pageSize * pageIndex);
        query.limit(pageSize);

        long count = query.countAll();
        List<VpanSendFile> list = query.asList();
        map.put("count", count);
        map.put("list", list);

        return map;
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#deleteVpanUploadFile(java.lang.String)
     */
    public void deleteVpanUploadFile(String fileId) {
        //通过id软删除，将状态更新为已删除
        DBObject query;
        try {
            query = new BasicDBObject("_id", new ObjectId(fileId));

            int userId = ReqUtil.instance.getUserId();
            DBObject q = new BasicDBObject();
            q.put("fileId", "fileId");
            q.put("receiveUserId", userId);
            dsForRW.getDB().getCollection("f_vpan_send").remove(q);

        } catch (Exception e) {
            throw new ServiceException("文件id错误");
        }
        DBObject update = new BasicDBObject("status", VpanFileStatus.delete.getValue());
        dsForRW.getDB().getCollection("f_vpan_upload").update(query, new BasicDBObject("$set", update));

    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#deleteMultiVpanUploadFile(java.lang.String[])
     */
    public void deleteMultiVpanUploadFile(String[] fileIds) {
        //封装数组
        BasicDBList idList = new BasicDBList();
        for (String fileId : fileIds) {
            try {
                idList.add(new ObjectId(fileId));

                int userId = ReqUtil.instance.getUserId();
                DBObject q = new BasicDBObject();
                q.put("fileId", fileId);
                q.put("receiveUserId", userId);
                dsForRW.getDB().getCollection("f_vpan_send").remove(q);

            } catch (Exception e) {
                throw new ServiceException("文件ids错误");
            }
        }
        //设置条件
        BasicDBObject insIn = new BasicDBObject();
        insIn.put("$in", idList);

        DBObject query = new BasicDBObject("_id", insIn);
        DBObject update = new BasicDBObject("status", VpanFileStatus.delete.getValue());
        dsForRW.getDB().getCollection("f_vpan_upload").updateMulti(query, new BasicDBObject("$set", update));
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#deleteVpanSendFile(java.lang.String)
     */
    public void deleteVpanSendFile(String sendFileId) {
        DBObject query;
        try {
            query = new BasicDBObject("_id", new ObjectId(sendFileId));
        } catch (Exception e) {
            throw new ServiceException("sendFileId错误");
        }
        //通过id删除记录
        dsForRW.getDB().getCollection("f_vpan_send").remove(query);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#deleteMultiVpanSendFile(java.lang.String[])
     */
    public void deleteMultiVpanSendFile(String[] sendFileIds) {
        //封装数组
        BasicDBList idList = new BasicDBList();
        for (String sendId : sendFileIds) {
            try {
                idList.add(new ObjectId(sendId));
            } catch (Exception e) {
                throw new ServiceException("发送文件ids错误");
            }
        }
        //设置条件
        BasicDBObject insIn = new BasicDBObject();
        insIn.put("$in", idList);
        DBObject query = new BasicDBObject("_id", insIn);
        //通过id删除记录
        dsForRW.getDB().getCollection("f_vpan_send").remove(query);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#updateUploadFileName(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void updateUploadFileName(String fileId, String newName, String newMimeType, String newSuffix, String newType) {
        DBObject query;
        try {
            query = new BasicDBObject("_id", new ObjectId(fileId));
        } catch (Exception e) {
            throw new ServiceException("文件id错误");
        }
        DBObject update = new BasicDBObject();
        //更新文件名，顺带更新文件后缀 和 type类型
        update.put("name", newName);
        update.put("suffix", newSuffix);
        update.put("type", newType);
        //更新 文件mime类型
        if (newMimeType != null) {
            update.put("mimeType", newMimeType);
        }
        dsForRW.getDB().getCollection("f_vpan_upload").update(query, new BasicDBObject("$set", update));

    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.dao.IVpanFileDao#getUploadFileById(java.lang.String)
     */
    public VpanUploadFile getUploadFileById(String id) {
        Query<VpanUploadFile> query = dsForRW.createQuery(VpanUploadFile.class);
        //通过id查询
        if (id != null) {
            try {
                query.field("_id").equal(new ObjectId(id));
            } catch (Exception e) {
                throw new ServiceException("文件id错误");
            }
        }

        return query.get();
    }

    @Override
    public Map<String, Object> getAllFileIdByReceivedUserId(int receiveUserId) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Long> fileIdTimeMap = new HashMap<String, Long>();

        Query<VpanSendFile> q = dsForRW.createQuery(VpanSendFile.class);
        q.field("receiveUserId").equal(receiveUserId);
        q.retrievedFields(true, "fileId", "sendDate");
        List<VpanSendFile> vsfList = q.asList();
        List<ObjectId> fileIdList = null;
        if (vsfList != null && vsfList.size() > 0) {
            fileIdList = new ArrayList<ObjectId>();
            for (VpanSendFile vsf : vsfList) {
                fileIdList.add(new ObjectId(vsf.getFileId()));
                fileIdTimeMap.put(vsf.getFileId(), vsf.getSendDate());
            }
        }
        map.put("fileIdList", fileIdList);
        map.put("fileIdTimeMap", fileIdTimeMap);
        return map;
    }

    @Override
    public Map<String, Object> searchFile(List<ObjectId> fileIds, String fileNameKey, String status, Integer uploader,
                                          String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex) {

        Map<String, Object> map = new HashMap<String, Object>();

        Query<VpanUploadFile> query = dsForRW.createQuery(VpanUploadFile.class);
        //模糊匹配 文件名
        if (StringUtils.isNotBlank(fileNameKey)) {
            query.field("name").containsIgnoreCase(fileNameKey);
        }
        //状态适配
        /*if(StringUtils.isNotBlank(status)){
            query.field("status").equal(status);
		}*/
        if (type != null && !StringUtils.isEmpty(type)) {
            query.field("type").equal(type);
        }
        if (fileIds != null && fileIds.size() > 0) {
            query.or(
                    query.criteria("uploader").equal(uploader).criteria("status").equal(VpanFileStatus.common.getValue()),
                    query.criteria("_id").in(fileIds)
            );
        } else {
            query.field("uploader").equal(uploader);
            query.field("status").equal(VpanFileStatus.common.getValue());
        }

        //排序
        if (sortAttr != null && !StringUtils.isEmpty(sortAttr)) {
            if (sortType != null && sortType > 0) {
                //顺排
                query.order(sortAttr);
            } else {
                //倒排（默认）
                query.order("-" + sortAttr);
            }
        }

        //分页
        query.offset(pageSize * pageIndex);
        query.limit(pageSize);

        List<VpanUploadFile> list = query.asList();

        map.put("list", list);
        long count = query.countAll();
        map.put("count", count);

        return map;
    }

    @Override
    public VpanSendFile getSendFileByFileIdAndSendIdAndReceiveId(String fileId, Integer sendUserId, Integer receiveUserId) {
        return dsForRW.createQuery(VpanSendFile.class)
                .field("fileId").equal(fileId)
                .field("sendUserId").equal(sendUserId)
                .field("receiveUserId").equal(receiveUserId).get();
    }


    @Override
    public Map<String, Object> getUploadFileByFileId(List<ObjectId> fileIds,
                                                     String fileNameKey, String status, Integer uploader, String type,
                                                     String sortAttr, Integer sortType, Integer pageSize,
                                                     Integer pageIndex) {
        Map<String, Object> map = new HashMap<String, Object>();
        Query<VpanUploadFile> query = dsForRW.createQuery(VpanUploadFile.class);
        //模糊匹配 文件名
        if (StringUtils.isNotBlank(fileNameKey)) {
            query.field("name").containsIgnoreCase(fileNameKey);
        }
        if (type != null && !StringUtils.isEmpty(type)) {
            query.field("type").equal(type);
        }
        if (fileIds != null && fileIds.size() > 0) {
            query.criteria("_id").in(fileIds);
        }
        //排序
        if (sortAttr != null && !StringUtils.isEmpty(sortAttr)) {
            if (sortType != null && sortType > 0) {
                //顺排
                query.order(sortAttr);
            } else {
                //倒排（默认）
                query.order("-" + sortAttr);
            }
        }
        //分页
        query.offset(pageSize * pageIndex);
        query.limit(pageSize);
        List<VpanUploadFile> list = query.asList();
        map.put("list", list);
        long count = query.countAll();
        map.put("count", count);
        return map;
    }

    @Override
    public VpanSendFile getSendFileByFileIdAndUserId(String fileId, Integer userId) {
        return dsForRW.createQuery(VpanSendFile.class)
                .field("fileId").equal(fileId)
                .field("receiveUserId").equal(userId).get();
    }

    @Override
    public VpanUploadFile getSendUploadByFileIdAndUserId(String fileId, Integer useId) {
        return dsForRW.createQuery(VpanUploadFile.class)
                .field("id").equal(new ObjectId(fileId))
                .field("uploader").equal(useId)
                .field("status").equal(VpanFileStatus.common.getValue())
                .get();
    }

    @Override
    public PageVO getMyLastFile(Integer userId, String keyword, Integer pageIndex, Integer pageSize) {
        Query<VpanSendRecord> query = dsForRW.createQuery(VpanSendRecord.class);
        List<Integer> receiveUserId = Lists.newArrayList(userId);
        query.field("status").equal(VpanSendRecord.STATUS_NORMAL);
        if (StringUtils.isNoneBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            query.filter("name", pattern);
        }
        query.or(
                query.criteria("sendUserId").equal(userId),
                query.criteria("receiveUserId").in(receiveUserId)
        );
        query.order("-sendDate");
        long count = query.countAll();
        if (pageIndex < 0) {
            pageIndex = 0;
        }
        if (pageSize < 0) {
            pageSize = 10;
        }
        int start = pageIndex * pageSize;
        query.offset(start);
        query.limit(pageSize);
        PageVO pageVO = new PageVO(query.asList(), count, pageIndex, pageSize);
        return pageVO;
    }

    @Override
    public void saveSendFileRecord(VpanSendRecord vpanSendRecord) {
        Clock clock = Clock.systemUTC();
        vpanSendRecord.setSendDate(clock.millis());
        vpanSendRecord.setStatus(VpanSendRecord.STATUS_NORMAL);
        dsForRW.insert(vpanSendRecord);
    }

    @Override
    public void removeFileRecord(String msgId) {
        Query<VpanSendRecord> query = dsForRW.createQuery(VpanSendRecord.class).field("msgId").equal(msgId);
        UpdateOperations<VpanSendRecord> ops = dsForRW.createUpdateOperations(VpanSendRecord.class);
        ops.set("status", VpanSendRecord.STATUS_DELETE);
        dsForRW.update(query, ops);
    }

    @Override
    public VpanSendRecord findByMsgId(String msgId) {
        Query<VpanSendRecord> query = dsForRW.createQuery(VpanSendRecord.class).field("msgId").equal(msgId);
        return query.get();
    }

    @Override
    public VpanSendRecord findFileByUserIdAndFileId(Integer userId, String fileId) {
        Query<VpanSendRecord> query = dsForRW.createQuery(VpanSendRecord.class);
        List<Integer> receiveUserId = Lists.newArrayList(userId);
        query.field("status").equal(VpanSendRecord.STATUS_NORMAL);
        query.or(
            query.criteria("sendUserId").equal(userId),
            query.criteria("receiveUserId").in(receiveUserId)
        );
        query.field("fileId").equal(fileId);
        return query.get();
    }

    @Override
    public void updateTimeToLast(String _id) {
        Query<VpanSendRecord> query = dsForRW.createQuery(VpanSendRecord.class);
        query.field("_id").equal(new ObjectId(_id));
        UpdateOperations<VpanSendRecord> ops = dsForRW.createUpdateOperations(VpanSendRecord.class);
        ops.set("sendDate", System.currentTimeMillis());
        dsForRW.update(query, ops);
    }

}

