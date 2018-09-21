package com.dachen.health.file.dao;

import java.util.List;
import java.util.Map;

import com.dachen.commons.page.PageVO;
import com.dachen.health.file.entity.po.VpanSendRecord;
import org.bson.types.ObjectId;

import com.dachen.health.file.entity.po.VpanSendFile;
import com.dachen.health.file.entity.po.VpanUploadFile;

/**
 * vpan文件管理dao
 *
 * @author wangqiao
 */
public interface IVpanFileDao {


    /**
     * 新增 vpan 上传文件数据
     *
     * @param file
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    VpanUploadFile addVpanUploadFile(VpanUploadFile file);

    /**
     * 批量新增 vpan 分享文件数据
     *
     * @param sendFileList 分享文件list
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    void addVpanSendFile(List<VpanSendFile> sendFileList);

    /**
     * 查询 上传文件数据（通过三个参数匹配 ：文件名，文件状态，上传者）
     *
     * @param name     文件名
     * @param status   状态
     * @param uploader 上传人
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    VpanUploadFile findVpanUploadFile(String name, String status, Integer uploader);


    /**
     * 搜索我上传的文件
     *
     * @param nameKey   文件名关键字
     * @param status    文件状态
     * @param uploader  上传者id
     * @param type      文件分类
     * @param sortAttr  文件排序属性
     * @param sortType  文件排序顺序
     * @param pageSize  分页大小
     * @param pageIndex 取第几页数据
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    Map<String, Object> searchVpanUploadFile(String nameKey, String status, Integer uploader,
                                             String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex);

    /**
     * 搜索我接收的文件
     *
     * @param nameKey       文件名关键字
     * @param receiveUserId 接收者id
     * @param type          文件分类
     * @param sortAttr      文件排序属性
     * @param sortType      文件排序顺序
     * @param pageSize      分页大小
     * @param pageIndex     取第几页数据
     * @param list
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    Map<String, Object> searchVpanSendFile(List<String> list, String nameKey, Integer receiveUserId,
                                           String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex);

    /**
     * 删除上传的文件（软删除）
     *
     * @param fileId
     * @author wangqiao
     * @date 2016年1月13日
     */
    void deleteVpanUploadFile(String fileId);

    /**
     * 删除 多个上传的文件（软删除）
     *
     * @param fileId
     * @author wangqiao
     * @date 2016年1月13日
     */
    void deleteMultiVpanUploadFile(String[] fileId);

    /**
     * 删除发送的文件
     *
     * @param sendFileId
     * @author wangqiao
     * @date 2016年1月13日
     */
    void deleteVpanSendFile(String sendFileId);

    /**
     * 删除 多个发送的文件
     *
     * @param sendFileIds 文件发送id数组
     * @author wangqiao
     * @date 2016年1月15日
     */
    void deleteMultiVpanSendFile(String[] sendFileIds);

    /**
     * 更新上传的 文件名
     *
     * @param fileId
     * @param newName
     * @param newMimeType
     * @param newSuffix
     * @param newType
     * @author wangqiao
     * @date 2016年1月13日
     */
    void updateUploadFileName(String fileId, String newName, String newMimeType, String newSuffix, String newType);

    /**
     * 通过文件id 读取文件信息
     *
     * @param id
     * @return
     * @author wangqiao
     * @date 2016年1月14日
     */
    VpanUploadFile getUploadFileById(String id);

    /**
     * 通过接受者id查找文件id
     *
     * @param receiveUserId
     * @return
     */
    Map<String, Object> getAllFileIdByReceivedUserId(int receiveUserId);

    /**
     * 搜索文件列表
     *
     * @param fileIds       发送给我的文件id
     * @param fileNameKey   文件名关键字
     * @param receiveUserId 接收者id
     * @param type          文件分类
     * @param sortAttr      文件排序属性
     * @param sortType      文件排序顺序
     * @param pageSize      分页大小
     * @param pageIndex     取第几页数据
     * @return
     * @author wangl
     * @date 2016年2月19日17:59:28
     */
    Map<String, Object> searchFile(List<ObjectId> fileIds, String fileNameKey, String status, Integer uploader,
                                   String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex);

    VpanSendFile getSendFileByFileIdAndSendIdAndReceiveId(String fileId, Integer sendUserId, Integer receiveUserId);


    /**
     * 根据品种查询文档
     *
     * @param fileIds       发送给我的文件id
     * @param fileNameKey   文件名关键字
     * @param uploader      接收者id
     * @param type          文件分类
     * @param sortAttr      文件排序属性
     * @param sortType      文件排序顺序
     * @param pageSize      分页大小
     * @param pageIndex     取第几页数据
     * @return
     * @author wangl
     * @date 2016年2月19日17:59:28
     */
    Map<String, Object> getUploadFileByFileId(List<ObjectId> fileIds, String fileNameKey, String status, Integer uploader,
                                              String type, String sortAttr, Integer sortType, Integer pageSize, Integer pageIndex);

    VpanSendFile getSendFileByFileIdAndUserId(String fileId, Integer userId);

    VpanUploadFile getSendUploadByFileIdAndUserId(String fileId, Integer userId);

    /**
     * 获取我的最近文件
     * @param userId
     * @return
     */
    PageVO getMyLastFile(Integer userId, String keyword, Integer pageIndex, Integer pageSize);

    void saveSendFileRecord(VpanSendRecord vpanSendRecord);

    void removeFileRecord(String msgId);

    VpanSendRecord findByMsgId(String msgId);

    VpanSendRecord findFileByUserIdAndFileId(Integer userId, String fileId);

    void updateTimeToLast(String _id);
}

