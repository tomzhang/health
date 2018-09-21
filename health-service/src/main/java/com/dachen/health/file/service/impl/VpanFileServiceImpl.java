package com.dachen.health.file.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CDurgorgUserInfo;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.file.constant.VpanFileEnum.VpanFileBucketType;
import com.dachen.health.file.constant.VpanFileEnum.VpanFileStatus;
import com.dachen.health.file.constant.VpanFileEnum.VpanFileType;
import com.dachen.health.file.dao.IVpanFileDao;
import com.dachen.health.file.entity.param.VpanFileParam;
import com.dachen.health.file.entity.po.VpanSendFile;
import com.dachen.health.file.entity.po.VpanSendRecord;
import com.dachen.health.file.entity.po.VpanUploadFile;
import com.dachen.health.file.entity.vo.LatelyVpanFileVo;
import com.dachen.health.file.entity.vo.VpanFileVO;
import com.dachen.health.file.entity.vo.VpanSendRecordVo;
import com.dachen.health.file.service.IVpanFileService;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.GroupUserInfo;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.BeanUtil;
import com.dachen.util.CheckUtils;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * 文件管理服务类
 *
 * @author wangqiao
 * @date 2016年1月13日
 */
@Service
public class VpanFileServiceImpl implements IVpanFileService {

    @Autowired
    private IVpanFileDao vpanFileDao;

    @Autowired
    private UserManager userManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IMsgService msgService;

    private static final Logger logger = LoggerFactory.getLogger(VpanFileServiceImpl.class);

    /**
     * access key
     */
//	private static String accessKey = PropertiesUtil.getContextProperty("qiniu.access.key");

    /**
     * secret key
     */
//	private static String secretKey = PropertiesUtil.getContextProperty("qiniu.secret.key");

    /**
     * vpan 空间名
     */
//    private static String vpanBucket = PropertiesUtil.getContextProperty("qiniu.vpan.bucket");
    private static String vpanBucket() {
      return PropertiesUtil.getContextProperty("qiniu.vpan.bucket");
    }

    /**
     * vpan 的下载域名
     */
//    private static String vpanDomain = PropertiesUtil.getContextProperty("qiniu.vpan.domain");
    private static String vpanDomain() {
        return PropertiesUtil.getContextProperty("qiniu.vpan.domain");
    }

    /**
     * vpan 空间名
     */
//    private static String messageBucket = PropertiesUtil.getContextProperty("qiniu.message.bucket");
    private static String messageBucket() {
        return PropertiesUtil.getContextProperty("qiniu.message.bucket");
    }

    /**
     * vpan 的下载域名
     */
//    private static String messageDomain = PropertiesUtil.getContextProperty("qiniu.message.domain");
    private static String messageDomain() {
        return PropertiesUtil.getContextProperty("qiniu.message.domain");
    }

    /**
     * 七牛文件授权管理工具
     */
//	private static Auth auth = Auth.create(accessKey, secretKey);

    /**
     * 文档 类型的文件后缀list
     */
//    private static List<String> docTypeList = Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.document").split(","));
    private static List<String> docTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.document").split(","));
    }
    /**
     * 图片 类型的文件后缀list
     */
//    private static List<String> picTypeList = Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.picture").split(","));
    private static List<String> picTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.picture").split(","));
    }

    /**
     * 视频 类型的文件后缀list
     */
//    private static List<String> videoTypeList = Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.video").split(","));
    private static List<String> videoTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.video").split(","));
    }

    /**
     * 音乐 类型的文件后缀list
     */
//    private static List<String> musicTypeList = Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.music").split(","));
    private static List<String> musicTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.music").split(","));
    }

//	@Override
//	public String getUploadToken(Integer userId) {
//
//		//返回七牛的上传token
//		String token = auth.uploadToken(vpanBucket, null, 3600*12, null);//token 有效期12小时
//		
//		return token;
//	}

    @Override
    public String saveFileInfo(Integer userId, String fileName, String mimeType, String hash, String key, Long size) {
        return saveCommonFileInfo(userId, fileName, mimeType, hash, key, size, vpanDomain(), vpanBucket());
    }

    @Override
    public String saveMassageFileInfo(Integer userId, String fileName, String mimeType, String hash, String key, Long size) {
        return saveCommonFileInfo(userId, fileName, mimeType, hash, key, size, messageDomain(), messageBucket());
    }

    /**
     * (non-Javadoc)
     *
     * @see com.dachen.health.file.service.IVpanFileService#saveCommonFileInfo(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.Long, java.lang.String, java.lang.String)
     */
    @Override
    public String saveCommonFileInfo(Integer userId, String fileName, String mimeType, String hash, String key, Long size, String domain, String bucket) {
        //参数校验
        if (userId == null || userId == 0) {
            throw new ServiceException("找不到登录用户信息");
        }
        if (StringUtils.isEmpty(fileName)) {
            throw new ServiceException("文件名为空");
        }
//				if(StringUtils.isEmpty(mimeType)){
//					throw new ServiceException("mimeType为空");
//				}
        if (StringUtils.isEmpty(hash)) {
            throw new ServiceException("hash为空");
        }
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException("key为空");
        }
        if (size == null) {
            size = (long) 0;
        }
        VpanUploadFile uploadFile = new VpanUploadFile();

        //解析文件后缀  文件名不包含.的后缀为null，文件名以.结尾的后缀为空字符串
        String[] split = fileName.split("\\.");
        String suffix = null;
        if (split != null && split.length > 1) {
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }

        //url拼接
        String url = "http://" + domain + "/" + key;

        //po数据初始化
//				uploadFile.setName(fileName);
        uploadFile.setSuffix(suffix);
        uploadFile.setMimeType(mimeType);
        uploadFile.setSize(size);
        uploadFile.setSpaceName(bucket);
        uploadFile.setUrl(url);
        uploadFile.setHash(hash);
        uploadFile.setType(matchFileType(suffix));
        uploadFile.setBucketType(VpanFileBucketType.pub.getValue());
        uploadFile.setStatus(VpanFileStatus.common.getValue());
        uploadFile.setUploader(userId);
        uploadFile.setLastUpdator(userId);
        uploadFile.setUploadDate(new Date().getTime());
        uploadFile.setLastUpdateDate(new Date().getTime());

        int userType = ReqUtil.instance.getUserType();
        uploadFile.setUserType(userType);

        //文件重名 在文件名后加上数字
        String newFileName = checkRepeatUploadFileName(fileName, fileName, suffix, userId, 1);
        uploadFile.setName(newFileName);

        //保存数据
        uploadFile = vpanFileDao.addVpanUploadFile(uploadFile);
        return uploadFile.getId().toString();
    }

    @Override
    public void updateFileName(String id, Integer userId, String newName, String newMimeType) {
        //参数校验
        if (id == null || StringUtils.isEmpty(id)) {
            throw new ServiceException("id为空");
        }
        if (userId == null || userId == 0) {
            throw new ServiceException("用户id为空");
        }
        if (newName == null || StringUtils.isEmpty(newName)) {
            throw new ServiceException("新的文件名为空");
        }
        //文件名重复校验
        VpanUploadFile repeatFile = vpanFileDao.findVpanUploadFile(newName, VpanFileStatus.common.getValue(), userId);
        if (repeatFile != null) {
            throw new ServiceException("已存在相关的文件名");
        }

        String[] split = newName.split("\\.");
        String newSuffix = null;
        if (split != null && split.length > 1) {
            newSuffix = newName.substring(newName.lastIndexOf(".") + 1);
        }

        //更新文件type
        String newType = matchFileType(newSuffix);

        // TODO 验证 userId是否有权限修改文件

        vpanFileDao.updateUploadFileName(id, newName, newMimeType, newSuffix, newType);

    }

    @Override
    public void deleteVpanUploadFile(String id, Integer userId) {
        //参数校验
        if (id == null || StringUtils.isEmpty(id)) {
            throw new ServiceException("id为空");
        }
        if (userId == null || userId == 0) {
            throw new ServiceException("用户id为空");
        }
        // TODO 验证 userId是否有权限删除文件


        //软删除数据
        vpanFileDao.deleteVpanUploadFile(id);
    }

    @Override
    public void deleteMultiVpanUploadFile(String[] ids, Integer userId) {
        //参数校验
        if (ids == null || ids.length == 0) {
            throw new ServiceException("参数 ids为空");
        }
        if (userId == null || userId == 0) {
            throw new ServiceException("用户id为空");
        }
        // TODO 验证 userId是否有权限删除文件


        //软删除数据
        vpanFileDao.deleteMultiVpanUploadFile(ids);
    }

    @Override
    public void deleteVpanSendFile(String sendFileId, Integer receiveUserId) {
        //参数校验
        if (sendFileId == null || StringUtils.isEmpty(sendFileId)) {
            throw new ServiceException("sendFileId为空");
        }
        if (receiveUserId == null || receiveUserId == 0) {
            throw new ServiceException("用户id为空");
        }
        //验证 该文件和接收人id是否对应得上

        //删除数据
        vpanFileDao.deleteVpanSendFile(sendFileId);
    }

    @Override
    public void deleteMultiVpanSendFile(String[] sendFileIds, Integer receiveUserId) {
        //参数校验
        if (sendFileIds == null || sendFileIds.length == 0) {
            throw new ServiceException("sendFileIds为空");
        }
        if (receiveUserId == null || receiveUserId == 0) {
            throw new ServiceException("用户id为空");
        }
        //验证 该文件和接收人id是否对应得上

        //删除数据
        vpanFileDao.deleteMultiVpanSendFile(sendFileIds);
    }

    @Override
    public PageVO searchUploadFile(VpanFileParam param) {
        //参数检测
        if (param == null) {
            throw new ServiceException("参数为空");
        }
        if (param.getUploader() == null || param.getUploader() == 0) {
            throw new ServiceException("用户id为空");
        }
        //TODO  非法的排序属性 需要过滤

        //查询数据
        PageVO page = new PageVO();
        String sortAttr = "uploadDate";//默认排序属性
        int sortType = -1;
        if (param.getSortAttr() != null && !StringUtils.isEmpty(param.getSortAttr())) {
            sortAttr = param.getSortAttr();
            //解析sortAttr
            if ("date".equals(param.getSortAttr())) {
                sortAttr = "uploadDate";
            } else if ("size".equals(param.getSortAttr())) {
                sortAttr = "size";
            } else if ("name".equals(param.getSortAttr())) {
                sortAttr = "name";
            }

        }
        if (param.getSortType() != null && param.getSortType() != 0) {
            sortType = param.getSortType();
        }
        /*Map<String ,Object> map = vpanFileDao.searchVpanUploadFile(param.getFileNameKey(), VpanFileStatus.common.getValue(), param.getUploader(),
                param.getType(), sortAttr, sortType, param.getPageSize(), param.getPageIndex());*/
        List<ObjectId> idList = new ArrayList<ObjectId>();
        if (param.getFileIdList() != null && param.getFileIdList().size() > 0) {
            for (String idStr : param.getFileIdList()) {
                idList.add(new ObjectId(idStr));
            }
        }
        Map<String, Object> map = vpanFileDao.searchFile(idList, param.getFileNameKey(), VpanFileStatus.common.getValue(), param.getUploader(),
                param.getType(), sortAttr, sortType, param.getPageSize(), param.getPageIndex());

        //查询结果 过滤
        List<VpanUploadFile> list = (List<VpanUploadFile>) map.get("list");
        List<VpanFileVO> voList = new ArrayList<VpanFileVO>();
        for (VpanUploadFile file : list) {
            VpanFileVO vo = BeanUtil.copy(file, VpanFileVO.class);
            vo.setFileId(file.getId().toString());
            voList.add(vo);
        }

        page.setPageData(voList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.parseLong(map.get("count").toString()));

        return page;
    }

    @Override
    public PageVO searchSendFile(VpanFileParam param) {
        //参数检测
        if (param == null) {
            throw new ServiceException("参数为空");
        }
        if (param.getReceiveUserId() == null || param.getReceiveUserId() == 0) {
            throw new ServiceException("用户id为空");
        }
        //查询数据
        PageVO page = new PageVO();
        String sortAttr = "sendDate";//默认排序属性
        int sortType = -1;
        if (param.getSortAttr() != null && !StringUtils.isEmpty(param.getSortAttr())) {
            sortAttr = param.getSortAttr();
            //解析sortAttr
            if ("date".equals(param.getSortAttr())) {
                sortAttr = "sendDate";
            } else if ("size".equals(param.getSortAttr())) {
                sortAttr = "size";
            } else if ("name".equals(param.getSortAttr())) {
                sortAttr = "name";
            }
        }
        if (param.getSortType() != null && param.getSortType() != 0) {
            sortType = param.getSortType();
        }

        Map<String, Object> map = vpanFileDao.searchVpanSendFile(param.getFileIdList(), param.getFileNameKey(), param.getReceiveUserId(),
                param.getType(), sortAttr, sortType, param.getPageSize(), param.getPageIndex());

        //查询结果 过滤
        List<VpanSendFile> list = (List<VpanSendFile>) map.get("list");
        List<VpanFileVO> voList = new ArrayList<VpanFileVO>();
        for (VpanSendFile sendFile : list) {
            VpanFileVO vo = BeanUtil.copy(sendFile, VpanFileVO.class);
            vo.setFileId(sendFile.getFileId());
            vo.setFileSendId(sendFile.getId().toString());

            voList.add(vo);
        }

        page.setPageData(voList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.parseLong(map.get("count").toString()));

        return page;
    }

    @Override
    public void saveSendFile(Integer userId, String fileId, String receiveUserIds) {
        // 参数校验
        if (userId == null || userId == 0) {
            throw new ServiceException("文件发送人为空");
        }
        if (fileId == null || StringUtils.isEmpty(fileId)) {
            throw new ServiceException("文件id为空");
        }
        if (receiveUserIds == null || StringUtils.isEmpty(receiveUserIds)) {
            throw new ServiceException("文件接收人为空");
        }
        //读取文件信息
        VpanUploadFile uploadFile = vpanFileDao.getUploadFileById(fileId);
        if (uploadFile == null) {
            throw new ServiceException("发送的文件不存在");
        }

        //拆分多个文件接收人id
        List<String> receiveUserIdList = Arrays.asList(receiveUserIds.split(","));

        List<VpanSendFile> sendFileList = new ArrayList();

        for (String receiveUserId : receiveUserIdList) {
            //排除自己给自己发送文件的情况
            //去掉排除 update by wangl 2016年2月26日16:14:56
            /*if(receiveUserId != null && receiveUserId.equals(userId.toString())){
                continue;
			}*/

            //初始化 文件发送po信息
            VpanSendFile sendFile = new VpanSendFile();
            sendFile.setFileId(fileId);
            sendFile.setSendUserId(userId);
            //需要校验 receiveUserId 是否是数字
            Integer receiveUserIdInt = null;
            try {
                receiveUserIdInt = Integer.parseInt(receiveUserId);
                sendFile.setReceiveUserId(receiveUserIdInt);
            } catch (NumberFormatException e) {
                //跳过不正确的接收人id
                continue;
            }
            sendFile.setSendDate(new Date().getTime());

            //文件冗余信息
            sendFile.setName(uploadFile.getName());
            sendFile.setSuffix(uploadFile.getSuffix());
            sendFile.setMimeType(uploadFile.getMimeType());
            sendFile.setSize(uploadFile.getSize());
            sendFile.setType(uploadFile.getType());
            sendFile.setUrl(uploadFile.getUrl());
            sendFile.setBucketType(uploadFile.getBucketType());

            VpanSendFile dbVsf = vpanFileDao.getSendFileByFileIdAndUserId(fileId, receiveUserIdInt);
            if (dbVsf == null) {
                sendFileList.add(sendFile);
            }
        }
        //批量新增
        vpanFileDao.addVpanSendFile(sendFileList);

    }


    @Override
    public void communitySaveSendFile(String fileId, String receiveUserIds) {
//		// 参数校验
//		if(userId == null ||  userId ==0){
//			throw new ServiceException("文件发送人为空");
//		}
        if (fileId == null || StringUtils.isEmpty(fileId)) {
            throw new ServiceException("文件id为空");
        }
        if (receiveUserIds == null || StringUtils.isEmpty(receiveUserIds)) {
            throw new ServiceException("文件接收人为空");
        }
        //读取文件信息
        VpanUploadFile uploadFile = vpanFileDao.getUploadFileById(fileId);
        if (uploadFile == null) {
            throw new ServiceException("发送的文件不存在");
        }

        //拆分多个文件接收人id
        List<String> receiveUserIdList = Arrays.asList(receiveUserIds.split(","));

        List<VpanSendFile> sendFileList = new ArrayList();

        for (String receiveUserId : receiveUserIdList) {
            //排除自己给自己发送文件的情况
            //去掉排除 update by wangl 2016年2月26日16:14:56
            /*if(receiveUserId != null && receiveUserId.equals(userId.toString())){
                continue;
			}*/

            //初始化 文件发送po信息
            VpanSendFile sendFile = new VpanSendFile();
            sendFile.setFileId(fileId);
            sendFile.setSendUserId(uploadFile.getUploader());
            //需要校验 receiveUserId 是否是数字
            Integer receiveUserIdInt = null;
            try {
                receiveUserIdInt = Integer.parseInt(receiveUserId);
                sendFile.setReceiveUserId(receiveUserIdInt);
            } catch (NumberFormatException e) {
                //跳过不正确的接收人id
                continue;
            }
            sendFile.setSendDate(new Date().getTime());

            //文件冗余信息
            sendFile.setName(uploadFile.getName());
            sendFile.setSuffix(uploadFile.getSuffix());
            sendFile.setMimeType(uploadFile.getMimeType());
            sendFile.setSize(uploadFile.getSize());
            sendFile.setType(uploadFile.getType());
            sendFile.setUrl(uploadFile.getUrl());
            sendFile.setBucketType(uploadFile.getBucketType());

            VpanSendFile dbVsf = vpanFileDao.getSendFileByFileIdAndUserId(fileId, receiveUserIdInt);
            if (dbVsf == null) {
                sendFileList.add(sendFile);
            }
        }
        //批量新增
        vpanFileDao.addVpanSendFile(sendFileList);

    }


    /**
     * @param suffix
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    private String matchFileType(String suffix) {
        //参数校验
        if (suffix == null) {
            return VpanFileType.other.getValue();
        }
        String lowerSuffix = StringUtils.lowerCase(suffix);
        //文档类型
        if (docTypeList().contains(lowerSuffix)) {
            return VpanFileType.document.getValue();
        }
        //图片类型
        if (picTypeList().contains(lowerSuffix)) {
            return VpanFileType.picture.getValue();
        }
        //视频类型
        if (videoTypeList().contains(lowerSuffix)) {
            return VpanFileType.video.getValue();
        }
        //音乐类型
        if (musicTypeList().contains(lowerSuffix)) {
            return VpanFileType.music.getValue();
        }

        return VpanFileType.other.getValue();
    }


    /**
     * 迭代校验自己上传的文件是否有重名，有重名则在文件名后自动添加序号直到不重名
     *
     * @param sourceName 原始文件名 （保证迭代过程中原文件名不变化）
     * @param fileName   文件名
     * @param suffix     文件后缀
     * @param uploader   文件上传人
     * @param count      添加序号
     * @return
     * @author wangqiao
     * @date 2016年1月13日
     */
    private String checkRepeatUploadFileName(String sourceName, String fileName, String suffix, Integer uploader, int count) {
        VpanUploadFile repeatFile = vpanFileDao.findVpanUploadFile(fileName, VpanFileStatus.common.getValue(), uploader);
        String newFileName;
        //没有重复文件名，直接返回
        if (repeatFile == null) {
            return fileName;
        }
        //文件名重复,在文件明后加上序号

        if (suffix == null) {
            //没有后缀
            newFileName = sourceName + count;
        } else {
            int index = sourceName.lastIndexOf('.');
            newFileName = sourceName.substring(0, index) + "_" + count + "." + suffix;
        }
        count++;
        //继续用新文件名迭代校验 直到文件名不重复
        return checkRepeatUploadFileName(sourceName, newFileName, suffix, uploader, count);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.file.service.IVpanFileService#getVpanDomain()
     */
    @Override
    public String getVpanDomain() {
        return vpanDomain();
    }

    @Override
    public PageVO searchFile(VpanFileParam param) {
        if (param == null) {
            throw new ServiceException("参数为空");
        }
        if (param.getUploader() == null || param.getUploader() == 0) {
            throw new ServiceException("用户id为空");
        }
        int userId = param.getUploader();
        /**
         * 1、获取发送给我的所有文件id
         * 2、根据1的返回条件和页面参数获取（我上传的 、 发送给我的文件列表）
         */
        Map<String, Object> fileMap = vpanFileDao.getAllFileIdByReceivedUserId(userId);
        @SuppressWarnings("unchecked")
        List<ObjectId> fileIdList = (List<ObjectId>) fileMap.get("fileIdList");

        @SuppressWarnings("unchecked")
        Map<String, Long> fileIdTimeMap = (Map<String, Long>) fileMap.get("fileIdTimeMap");

        PageVO page = new PageVO();

        String sortAttr = "uploadDate";//默认排序属性
        int sortType = -1;
        if (param.getSortAttr() != null && !StringUtils.isEmpty(param.getSortAttr())) {
            sortAttr = param.getSortAttr();
            //解析sortAttr
            if ("date".equals(param.getSortAttr())) {
                sortAttr = "uploadDate";
            } else if ("size".equals(param.getSortAttr())) {
                sortAttr = "size";
            } else if ("name".equals(param.getSortAttr())) {
                sortAttr = "name";
            }
        }
        if (param.getSortType() != null && param.getSortType() != 0) {
            sortType = param.getSortType();
        }

        Map<String, Object> map = vpanFileDao.searchFile(fileIdList, param.getFileNameKey(), VpanFileStatus.common.getValue(), param.getUploader(),
                param.getType(), sortAttr, sortType, param.getPageSize(), param.getPageIndex());

        //查询结果 过滤
        List<VpanUploadFile> list = (List<VpanUploadFile>) map.get("list");
        List<VpanFileVO> voList = new ArrayList<VpanFileVO>();

        List<Integer> uploaderIds = list.stream().filter(vpanUploadFile -> Objects.nonNull(vpanUploadFile.getUploader()))
                .map(VpanUploadFile::getUploader).collect(Collectors.toList());
        List<User> users = userRepository.findUsersWithOutStatus(uploaderIds);

        if (!CollectionUtils.isEmpty(list)) {
            for (VpanUploadFile file : list) {
                VpanFileVO vo = BeanUtil.copy(file, VpanFileVO.class);
                String temFileId = file.getId().toString();
                vo.setFileId(temFileId);
                //User user = userRepository.getUser(file.getUploader());
                if (!CollectionUtils.isEmpty(users)) {
                    for (User user : users) {
                        if (Objects.nonNull(file.getUploader()) && Objects.nonNull(user.getUserId()) && file.getUploader().intValue() == user.getUserId().intValue()) {
                            String uploaderName = user.getName();
                            vo.setUploaderName(uploaderName);
                        }
                    }
                }
                vo.setSizeStr();

                //如果不是我上传的文件，则将文件的时候修改为我点击保存按钮那一时刻的时间
                Long t = fileIdTimeMap.get(temFileId);
                if (t != null) {
                    vo.setUploadDate(t);
                }
                voList.add(vo);
            }
        }
        page.setPageData(voList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.parseLong(map.get("count").toString()));

        return page;
    }

    @Override
    public boolean isInMyFileList(String fileId, Integer receiveUserId) {
        boolean flag = false;
        VpanSendFile vs = vpanFileDao.getSendFileByFileIdAndUserId(fileId, receiveUserId);
        if (vs != null)
            flag = true;
        if (!flag) {
            VpanUploadFile vu = vpanFileDao.getSendUploadByFileIdAndUserId(fileId, receiveUserId);
            if (vu != null)
                flag = true;
        }
        return flag;
    }

    @Override
    public PageVO getUploadFileByFileId(VpanFileParam param) {
        //参数检测
        if (param == null) {
            throw new ServiceException("参数为空");
        }
        if (param.getUploader() == null || param.getUploader() == 0) {
            throw new ServiceException("用户id为空");
        }
        PageVO page = new PageVO();
        if (param.getFileIdList().size() == 0) {//如果文件id为空
            page = new PageVO();
            return page;
        }
        //查询数据
        String sortAttr = "uploadDate";//默认排序属性
        int sortType = -1;
        if (param.getSortAttr() != null && !StringUtils.isEmpty(param.getSortAttr())) {
            sortAttr = param.getSortAttr();
            //解析sortAttr
            if ("date".equals(param.getSortAttr())) {
                sortAttr = "uploadDate";
            } else if ("size".equals(param.getSortAttr())) {
                sortAttr = "size";
            } else if ("name".equals(param.getSortAttr())) {
                sortAttr = "name";
            }
        }
        if (param.getSortType() != null && param.getSortType() != 0) {
            sortType = param.getSortType();
        }
        List<ObjectId> idList = new ArrayList<ObjectId>();
        if (param.getFileIdList() != null && param.getFileIdList().size() > 0) {
            for (String idStr : param.getFileIdList()) {
                idList.add(new ObjectId(idStr));
            }
        }
        Map<String, Object> map = vpanFileDao.getUploadFileByFileId(idList, param.getFileNameKey(), VpanFileStatus.common.getValue(), param.getUploader(),
                param.getType(), sortAttr, sortType, param.getPageSize(), param.getPageIndex());
        //查询结果 过滤
        List<VpanUploadFile> list = (List<VpanUploadFile>) map.get("list");
        List<VpanFileVO> voList = new ArrayList<VpanFileVO>();
        for (VpanUploadFile file : list) {
            VpanFileVO vo = BeanUtil.copy(file, VpanFileVO.class);
            vo.setFileId(file.getId().toString());
            voList.add(vo);
        }
        page.setPageData(voList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.parseLong(map.get("count").toString()));
        return page;
    }

    @Override
    public PageVO myLastFiles(String keyword, Integer pageIndex, Integer pageSize) {
        Integer currentUserId = ReqUtil.instance.getUserId();
        //1、获取发送给我的所有文件id
        PageVO pageVO = vpanFileDao.getMyLastFile(currentUserId, keyword, pageIndex, pageSize);

        List<VpanSendRecord> vpanSendRecords = (List<VpanSendRecord>) pageVO.getPageData();

        if (!CollectionUtils.isEmpty(vpanSendRecords)) {
            List<VpanSendRecordVo> vpanSendRecordVos = BeanUtil.copyList(vpanSendRecords, VpanSendRecordVo.class);
            vpanSendRecordVos.stream().forEach(vpanSendRecordVo -> {
                if (vpanSendRecordVo.getSendUserId().intValue() == currentUserId.intValue()) {
                    vpanSendRecordVo.setSender(Boolean.TRUE);
                } else {
                    vpanSendRecordVo.setSender(Boolean.FALSE);
                }
                vpanSendRecordVo.setSizeStr();
            });
            pageVO.setPageData(vpanSendRecordVos);
        }

        return pageVO;
    }

    @Autowired
    protected DrugOrgApiClientProxy drugOrgApiClientProxy;

    @Override
    public void sendFileRecord(String fileId, String gid, String url, String msgId) throws HttpApiException {

        if (StringUtils.isBlank(url) || !CheckUtils.checkCommon(url, CheckUtils.CheckRegexEnum.URL.getRegex())) {
            throw new ServiceException("文件URL错误");
        }

        if (StringUtils.isBlank(fileId)) {
            throw new ServiceException("文件id为空");
        }

        if (StringUtils.isBlank(gid)) {
            throw new ServiceException("会话组id为空");
        }

        if (StringUtils.isBlank(msgId)) {
            throw new ServiceException("消息id为空");
        }

        VpanSendRecord temp = vpanFileDao.findByMsgId(msgId);
        if (Objects.nonNull(temp)) {
            throw new ServiceException("已存在对应的文件发送记录，请勿重复添加");
        }

        String bucket = null;

        if (url.contains("vpan")) {
            bucket = "vpan";
        } else if (url.contains("message")) {
            bucket = "message";
        } else {
            bucket = "vpan";
        }

        //当前用户id
        int currentUserId = ReqUtil.instance.getUserId();
        int currentUserType = ReqUtil.instance.getUserType();

        VpanSendRecord vpanSendRecord = new VpanSendRecord();

        //根据fileId，补充VpanSendRecord的属性
        VpanUploadFile uploadFile = vpanFileDao.getUploadFileById(fileId);
        vpanSendRecord.setName(uploadFile.getName());
        vpanSendRecord.setSendUserId(currentUserId);
        vpanSendRecord.setSuffix(uploadFile.getSuffix());
        vpanSendRecord.setMimeType(uploadFile.getMimeType());
        vpanSendRecord.setType(uploadFile.getType());
        vpanSendRecord.setSize(uploadFile.getSize());
        vpanSendRecord.setUrl(uploadFile.getUrl());
        vpanSendRecord.setBucketType(uploadFile.getBucketType());
        vpanSendRecord.setGid(gid);
        vpanSendRecord.setMsgId(msgId);
        vpanSendRecord.setFileId(fileId);
        vpanSendRecord.setGid(gid);
        vpanSendRecord.setBucket(bucket);
        vpanSendRecord.setUserType(currentUserType);

        //根据gid获取会话组信息
        GroupInfoRequestMessage requestMessage = new GroupInfoRequestMessage();
        requestMessage.setGid(gid);
        requestMessage.setUserId(String.valueOf(currentUserId));
        GroupInfo groupInfo = (GroupInfo) msgService.getGroupInfo(requestMessage);

        //设置会话组名称
        String imName = groupInfo.getGname();
        vpanSendRecord.setImName(imName);

        String sourceName = null;

        if (currentUserType == UserEnum.UserType.enterpriseUser.getIndex()) {
            //取药企的用户，药企没有提供单个获取用户的接口，只能获取多个
            CDurgorgUserInfo durgorgUserInfoDto = drugOrgApiClientProxy.getByContext(ReqUtil.instance.getToken());
            if (Objects.nonNull(durgorgUserInfoDto) && Objects.nonNull(durgorgUserInfoDto.getMajorUser())) {
                sourceName = durgorgUserInfoDto.getMajorUser().getName();
            }

        } else if (currentUserType == UserEnum.UserType.doctor.getIndex()) {
            //取health中的用户
            User currentUser = userRepository.getUser(currentUserId);
            if (Objects.nonNull(currentUser)) {
                sourceName = currentUser.getName();
            }
        }

        vpanSendRecord.setSourceName(sourceName);

        List<GroupUserInfo> groupUserInfos = groupInfo.getUserList();

        if (!CollectionUtils.isEmpty(groupUserInfos)) {
            List<GroupUserInfo> withOutSender = groupUserInfos.stream()
                    .filter(groupUserInfo -> Objects.nonNull(groupUserInfo.getId()))
                    .filter(groupUserInfo -> !StringUtils.equals(groupUserInfo.getId(), String.valueOf(currentUserId)))
                    .collect(Collectors.toList());

            if (!CollectionUtils.isEmpty(withOutSender)) {
                List<Integer> receiveUserId = withOutSender.stream()
                        .map(groupUserInfo -> Integer.valueOf(groupUserInfo.getId()))
                        .collect(Collectors.toList());

                vpanSendRecord.setReceiveUserId(receiveUserId);

                //当im中包含多个用户时，toUserName为会话组名称，sourceName也存为会话组名称
                if (withOutSender.size() == 1) {
                    vpanSendRecord.setTargetName(withOutSender.get(0).getName());
                } else {
                    vpanSendRecord.setTargetName(imName);
                    vpanSendRecord.setSourceName(imName);
                }
            }

        }

        vpanFileDao.saveSendFileRecord(vpanSendRecord);
    }

    @Override
    public void removeFileRecord(String msgId) {
        if (StringUtils.isBlank(msgId)) {
            throw new ServiceException("消息id为空");
        }
        VpanSendRecord temp = vpanFileDao.findByMsgId(msgId);
        if (Objects.isNull(temp)) {
            throw new ServiceException("不存在对应的文件发送记录");
        }
        vpanFileDao.removeFileRecord(msgId);
    }

    @Override
    public void updateLastFile(LatelyVpanFileVo latelyVpanFileVo) {
        logger.info("参数latelyVpanFileVo = {}"+latelyVpanFileVo);
        Integer userId = ReqUtil.instance.getUserId();
        String fileId = latelyVpanFileVo.id;
        VpanSendRecord record = vpanFileDao.findFileByUserIdAndFileId(userId, fileId);
        if(record != null)
            vpanFileDao.updateTimeToLast(record.getId());
        else {
            String bucket, url = latelyVpanFileVo.url;
            if (url.contains("vpan")) {
                bucket = "vpan";
            } else if (url.contains("message")) {
                bucket = "message";
            } else {
                bucket = "vpan";
            }
            VpanSendRecord vpanSendRecord = new VpanSendRecord();
            vpanSendRecord.setName(latelyVpanFileVo.name);
            vpanSendRecord.setSendUserId(userId);
            vpanSendRecord.setSuffix(latelyVpanFileVo.suffix);
            vpanSendRecord.setMimeType(latelyVpanFileVo.mimeType);
            vpanSendRecord.setType(latelyVpanFileVo.type);
            vpanSendRecord.setSize(latelyVpanFileVo.size);
            vpanSendRecord.setUrl(latelyVpanFileVo.url);
            vpanSendRecord.setBucketType(VpanFileBucketType.pub.getValue());
            vpanSendRecord.setFileId(fileId);
            vpanSendRecord.setBucket(bucket);
            vpanSendRecord.setStatus(VpanSendRecord.STATUS_NORMAL);
            vpanSendRecord.setSendDate(System.currentTimeMillis());
            vpanFileDao.saveSendFileRecord(vpanSendRecord);
        }
    }

}

