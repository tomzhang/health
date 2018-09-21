package com.dachen.health.file.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.file.entity.param.VpanFileParam;
import com.dachen.health.file.entity.po.VpanUploadFile;
import com.dachen.health.file.entity.vo.LatelyVpanFileVo;
import com.dachen.sdk.exception.HttpApiException;

/**
 * 文件管理 服务类
 *
 * @author wangqiao
 * @date 2016年1月13日
 */
public interface IVpanFileService {


    /**
     * 获取文件上传token （有效期12小时）
     * @param userId 文件上传人userId
     * @return
     *@author wangqiao
     *@date 2016年1月13日
     */
//	 String getUploadToken(Integer userId);

    /**
     * 保存Vpan文件元信息
     *
     * @param userId   上传人id
     * @param fileName 文件名（包含后缀）
     * @param mimeType mime类型
     * @param hash     hash值
     * @param key      文件key值（七牛返回）
     * @param size     文件大小（单位字节）
     * @return 记录id
     * @author wangqiao
     * @date 2016年1月13日
     */
    String saveFileInfo(Integer userId, String fileName, String mimeType, String hash, String key, Long size);

    /**
     * 保存Vpan文件元信息
     *
     * @param userId   上传人id
     * @param fileName 文件名（包含后缀）
     * @param mimeType mime类型
     * @param hash     hash值
     * @param key      文件key值（七牛返回）
     * @param size     文件大小（单位字节）
     * @return 记录id
     * @author wangqiao
     * @date 2016年1月13日
     */
    String saveMassageFileInfo(Integer userId, String fileName, String mimeType, String hash, String key, Long size);

    /**
     * 保存通用文件元信息
     *
     * @param userId   上传人id
     * @param fileName 文件名（包含后缀）
     * @param mimeType mime类型
     * @param hash     hash值
     * @param key      文件key值（七牛返回）
     * @param size     文件大小（单位字节）
     * @param domain   访问文件使用的域名
     * @param bucket   保存文件的空间名
     * @return fileId 文件id
     */
    String saveCommonFileInfo(Integer userId, String fileName, String mimeType, String hash, String key, Long size, String domain, String bucket);

    /**
     * 更新文件名
     *
     * @param id          文件id
     * @param userId      用户id
     * @param newName     新的文件名
     * @param newMimeType 新的mimeType
     * @author wangqiao
     * @date 2016年1月14日
     */
    void updateFileName(String id, Integer userId, String newName, String newMimeType);

    /**
     * 删除 上传的文件（软删除）
     *
     * @param id     文件id
     * @param userId 用户id
     * @author wangqiao
     * @date 2016年1月13日
     */
    void deleteVpanUploadFile(String id, Integer userId);

    /**
     * 删除多个 上传的文件（软删除）
     *
     * @param ids
     * @param userId
     * @author wangqiao
     * @date 2016年1月15日
     */
    void deleteMultiVpanUploadFile(String[] ids, Integer userId);


    /**
     * 搜索 我上传的文件（分页）
     *
     * @param param fileNameKey         	搜索关键字（可以为空）
     *              type         	文件分类（可以为空） 文档=document，图片=picture，视频=video，音乐=music，其它=other
     *              sortAttr         	排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序（默认）
     *              sortType         	排序顺序  1=顺序（默认），-1=倒序
     *              uploader  上传者id
     *              fileIdList 文件idlist
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    PageVO searchUploadFile(VpanFileParam param);

    /**
     * 搜索 我接收的文件（分页）
     *
     * @param param fileNameKey         	搜索关键字（可以为空）
     *              type         	文件分类（可以为空） 文档=document，图片=picture，视频=video，音乐=music，其它=other
     *              sortAttr         	排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序（默认）
     *              sortType         	排序顺序  1=顺序（默认），-1=倒序
     *              receiveUserId   接收者id
     *              fileIdList 文件idlist
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    PageVO searchSendFile(VpanFileParam param);

    /**
     * 保存 文件发送记录
     *
     * @param userId         发送人id
     * @param fileId         文件id
     * @param receiveUserIds 接收人id（多个接收人，","逗号分隔）
     * @author wangqiao
     * @date 2016年1月14日
     */
    void saveSendFile(Integer userId, String fileId, String receiveUserIds);

    /**
     * 保存 文件发送记录
     *
     * @param fileId         文件id
     * @param receiveUserIds 接收人id（多个接收人，","逗号分隔）
     * @author 李明
     * @date 2016年1月14日
     */
    void communitySaveSendFile(String fileId, String receiveUserIds);

    /**
     * 删除 文件发送记录
     *
     * @param sendFileId    文件发送id
     * @param receiveUserId 接收人id
     * @author wangqiao
     * @date 2016年1月14日
     */
    void deleteVpanSendFile(String sendFileId, Integer receiveUserId);

    /**
     * 删除多个 文件发送记录
     *
     * @param sendFileIds   文件发送id数组
     * @param receiveUserId 接收人id
     * @author wangqiao
     * @date 2016年1月15日
     */
    void deleteMultiVpanSendFile(String[] sendFileIds, Integer receiveUserId);

    /**
     * 读取vpan空间中文件的访问域名
     *
     * @return
     * @author wangqiao
     * @date 2016年1月26日
     */
    String getVpanDomain();


    /**
     * 搜索 文件列表（分页）
     *
     * @param param fileNameKey         	搜索关键字（可以为空）
     *              type         	文件分类（可以为空） 文档=document，图片=picture，视频=video，音乐=music，其它=other
     *              sortAttr         	排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序（默认）
     *              sortType         	排序顺序  1=顺序（默认），-1=倒序
     *              uploader             上传者
     * @return
     * @author wangl
     * @date 2016年2月19日17:28:54
     */
    PageVO searchFile(VpanFileParam param);


    boolean isInMyFileList(String fileId, Integer receiveUserId);


    /**
     * 根据品种查询该品种下上传了那些文件
     *
     * @param param fileNameKey         	搜索关键字（可以为空）
     *              type         	文件分类（可以为空） 文档=document，图片=picture，视频=video，音乐=music，其它=other
     *              sortAttr         	排序属性  name=按名称排序，size=按文件大小排序，date=按上传时间排序（默认）
     *              sortType         	排序顺序  1=顺序（默认），-1=倒序
     *              uploader  上传者id
     *              fileIdList 文件idlist
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    PageVO getUploadFileByFileId(VpanFileParam param);

    PageVO myLastFiles(String keyword, Integer pageIndex, Integer pageSize);

    void sendFileRecord(String fileId, String gid, String url, String msgId) throws HttpApiException;

    void removeFileRecord(String msgId);

    void updateLastFile(LatelyVpanFileVo latelyVpanFileVo);
}

