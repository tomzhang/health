package com.dachen.health.file.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.page.PageVO;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CDurgorgUserInfo;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.file.constant.UserFileEnum;
import com.dachen.health.file.dao.IUserFileDao;
import com.dachen.health.file.dao.IVpanFileDao;
import com.dachen.health.file.entity.param.UserAfterUploadParam;
import com.dachen.health.file.entity.param.UserFileParam;
import com.dachen.health.file.entity.param.UserSaveFileParam;
import com.dachen.health.file.entity.po.UserFile;
import com.dachen.health.file.entity.po.UserSendRecord;
import com.dachen.health.file.entity.po.VpanSendRecord;
import com.dachen.health.file.entity.vo.AuthSimpleUser;
import com.dachen.health.file.entity.vo.UserFileVO;
import com.dachen.health.file.service.IUserFileService;
import com.dachen.health.file.utils.FormatSizeUtil;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.GroupUserInfo;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.manager.RemoteServiceResult;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.BeanUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xuhuanjie
 * @desc
 * @date 2018-03-05
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class UserFileServiceImpl implements IUserFileService {

    private static final Logger logger = LoggerFactory.getLogger(VpanFileServiceImpl.class);

    @Autowired
    private IUserFileDao iUserFileDao;

    @Autowired
    private IVpanFileDao vpanFileDao;

    @Autowired
    private RibbonManager ribbonManager;

    @Autowired
    private IMsgService msgService;

    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    @Autowired
    private UserRepository userRepository;

    @Override
    public String saveUploadFileInfo(UserAfterUploadParam param) {
        return saveCommonFileInfo(param, vpanDomain(), vpanBucket());
    }

    @Override
    public String saveCommonFileInfo(UserAfterUploadParam param, String domain, String bucket) {
        Integer userId = param.getUserId();
        Integer userType = param.getUserType();
        String fileName = param.getName();
        String hash = param.getHash();
        Long size = param.getSize();
        String mimeType = param.getMimeType();
        String key = param.getKey();
        Integer directoryId = param.getDirectoryId();
        String directoryName = param.getDirectoryName();
        //参数校验
        if (Objects.isNull(userId) || Objects.equals(userId, 0)) {
            throw new ServiceException("找不到登录用户信息");
        }
        if (Objects.isNull(userType)) {
            throw new ServiceException("用户类型为空");
        }
        if (StringUtils.isEmpty(fileName)) {
            throw new ServiceException("文件名为空");
        }
        if (StringUtils.isEmpty(hash)) {
            throw new ServiceException("文件hash值为空");
        }
        if (StringUtils.isEmpty(key)) {
            throw new ServiceException("文件key为空");
        }
        if (Objects.isNull(directoryId)) {
            throw new ServiceException("文件目录Id为空");
        }
        if (StringUtils.isEmpty(directoryName)) {
            throw new ServiceException("文件目录名为空");
        }
        if (Objects.isNull(size)) {
            size = (long) 0;
        }
        UserFile userFile = new UserFile();

        //解析文件后缀  文件名不包含.的后缀为null，文件名以.结尾的后缀为空字符串
        String[] split = fileName.split("\\.");
        String suffix = null;
        if (split.length > 1) {
            suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        }
        //url拼接
        String url = "http://" + domain + "/" + key;

        userFile.setSuffix(suffix);
        userFile.setMimeType(mimeType);
        userFile.setSize(size);
        userFile.setSpaceName(bucket);
        userFile.setUrl(url);
        userFile.setHash(hash);
        userFile.setFileType(matchFileType(suffix));
        userFile.setBucketType(UserFileEnum.UserFileBucketType.pub.getValue());
        userFile.setStatus(UserFileEnum.UserFileStatus.common.getValue());
        userFile.setOwnerId(userId);
        userFile.setUserType(userType);
        userFile.setDirectoryId(directoryId);
        userFile.setDirectoryName(directoryName);
        userFile.setModifyUser(userId);
        userFile.setSourceType(UserFileEnum.UserFileSourceType.upload.getValue());
        userFile.setCreateTime(System.currentTimeMillis());
        userFile.setModifyTime(System.currentTimeMillis());

        //文件重名 在文件名后加上数字
        String newFileName = checkRepeatUserFileName(fileName, fileName, suffix, userId, 1);
        userFile.setName(newFileName);

        //保存数据
        return iUserFileDao.addUserFile(userFile);
    }

    @Override
    public void deleteFile(List<String> fileIds) {
        iUserFileDao.deleteUserFiles(fileIds);
    }

    @Override
    public void updateFileName(String id, String newName, Integer userId) {
        //参数校验
        if (StringUtils.isEmpty(id)) {
            throw new ServiceException("id为空");
        }
        if (Objects.isNull(userId) || Objects.equals(userId, 0)) {
            throw new ServiceException("用户id为空");
        }
        if (StringUtils.isEmpty(newName)) {
            throw new ServiceException("新的文件名为空");
        }
        //文件名重复校验
        UserFile repeatFile = iUserFileDao.findUserFile(newName, UserFileEnum.UserFileStatus.common.getValue(), userId);
        if (Objects.nonNull(repeatFile)) {
            throw new ServiceException("已存在相关的文件名");
        }

        String[] split = newName.split("\\.");
        String newSuffix = null;
        if (split.length > 1) {
            newSuffix = newName.substring(newName.lastIndexOf(".") + 1);
        }

        //更新文件type
        Integer newFileType = matchFileType(newSuffix);

        // TODO 验证 userId是否有权限修改文件

        iUserFileDao.updateUserFileName(id, newName, newSuffix, newFileType);
    }

    @Override
    public PageVO searchFile(UserFileParam param) {
        if (Objects.isNull(param)) {
            throw new ServiceException("参数为空");
        }
        if (Objects.isNull(param.getOwnerId()) || Objects.equals(param.getOwnerId(), 0)) {
            throw new ServiceException("用户Id为空");
        }
        String sortAttr = UserFileEnum.UserFileSortAttr.date.getTitle();//默认排序属性
        int sortType = -1;
        if (Objects.nonNull(param.getSortAttr())) {
            if (Objects.equals(UserFileEnum.UserFileSortAttr.date.getValue(), param.getSortAttr())) {
                sortAttr = UserFileEnum.UserFileSortAttr.date.getTitle();
            } else if (Objects.equals(UserFileEnum.UserFileSortAttr.size.getValue(), param.getSortAttr())) {
                sortAttr = UserFileEnum.UserFileSortAttr.size.getTitle();
            } else if (Objects.equals(UserFileEnum.UserFileSortAttr.name.getValue(), param.getSortAttr())) {
                sortAttr = UserFileEnum.UserFileSortAttr.name.getTitle();
            }
        }
        if (Objects.nonNull(param.getSortType()) && param.getSortType() != 0) {
            sortType = param.getSortType();
        }
        Map<String, Object> map = iUserFileDao.searchFile(param, sortAttr, sortType);
        @SuppressWarnings("unchecked")
        List<UserFile> list = (List<UserFile>) map.get("list");
        List<UserFileVO> voList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(list)) {
            Set<Integer> ownerIds = list.stream().filter(UserFile -> Objects.nonNull(UserFile.getOwnerId()))
                                        .map(UserFile::getOwnerId).collect(Collectors.toSet());
            logger.info("+++++ ownerIds: {}", JSON.toJSONString(ownerIds));
            /* 从auth2取 */
            List<AuthSimpleUser> users = getSimpleUserList(Lists.newArrayList(ownerIds));
            logger.info("+++++ GetUserInfo from auth2; user: {}", JSON.toJSONString(users));
            Map<Integer, AuthSimpleUser> userIdMap = Maps.newHashMap();
            if (Objects.nonNull(users)) {
                userIdMap = Maps.uniqueIndex(users, AuthSimpleUser::getId);
            }
            for (UserFile file : list) {
                UserFileVO vo = BeanUtil.copy(file, UserFileVO.class);
                if (Objects.isNull(vo)) {
                    continue;
                }
                vo.setFileId(file.getId().toString());
                AuthSimpleUser user = userIdMap.get(file.getOwnerId());
                vo.setOwnerName(Objects.isNull(user) ? "" : user.getName());
                vo.setSizeStr(FormatSizeUtil.setSizeStr(file.getSize()));
                voList.add(vo);
            }
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageData(voList);
        pageVO.setPageIndex(param.getPageIndex());
        pageVO.setPageSize(param.getPageSize());
        pageVO.setTotal(Long.parseLong(map.get("count").toString()));
        return pageVO;
    }

    @Override
    public String saveFileInfo(UserSaveFileParam param) {
        if (Objects.isNull(param)) {
            throw new ServiceException("参数为空");
        }
        if (Objects.isNull(param.getFileId())) {
            throw new ServiceException("文件Id为空");
        }
        if (Objects.isNull(param.getDirectoryId())) {
            throw new ServiceException("文件目录Id为空");
        }
        if (StringUtils.isBlank(param.getDirectoryName())) {
            throw new ServiceException("文件目录名为空");
        }
        /* 历史数据 无法保存 detailMsg=invalid hexadecimal representation of an ObjectId: [o_1cge8b1js1ibthplmmraplv25h] */
        if (StringUtil.contains(param.getFileId(), "_")) {
            throw new ServiceException("该文件不支持保存到我的文件");
        }
        UserFile userFileByFileId = iUserFileDao.getUserFileByFileId(param.getFileId());
        if (Objects.isNull(userFileByFileId)) {
            throw new ServiceException("找不到该文件");
        }
        Integer userId = ReqUtil.instance.getUserId();
        UserFile copy = BeanUtil.copy(userFileByFileId, UserFile.class);
        copy.setId(null);
        copy.setOwnerId(userId);
        copy.setDirectoryId(param.getDirectoryId());
        copy.setDirectoryName(param.getDirectoryName());
        copy.setModifyUser(userId);
        copy.setSourceType(UserFileEnum.UserFileSourceType.receive.getValue());
        copy.setStatus(UserFileEnum.UserFileStatus.common.getValue());
        copy.setBucketType(UserFileEnum.UserFileBucketType.pub.getValue());
        copy.setCreateTime(System.currentTimeMillis());
        copy.setModifyTime(System.currentTimeMillis());
        //文件重名 在文件名后加上数字
        String newFileName = checkRepeatUserFileName(userFileByFileId.getName(), userFileByFileId.getName(), userFileByFileId.getSuffix(), userId, 1);
        copy.setName(newFileName);
        //保存数据
        return iUserFileDao.addUserFile(copy);
    }

    @Override
    public Boolean isInMyFileList(String id) {
        if (StringUtil.isBlank(id)) {
            throw new ServiceException("文件Id为空");
        }
        UserFile userFileByFileId = iUserFileDao.getUserFileByFileId(id);
        if (Objects.nonNull(userFileByFileId)) {
            String hash = userFileByFileId.getHash();
            UserFile userFile = iUserFileDao.getUserFileByHashAndOwnerId(hash, ReqUtil.instance.getUserId());
            if (Objects.isNull(userFile)) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    @Override
    public void sendFile(String groupId, List<String> fileIds) {
        if (StringUtil.isBlank(groupId)) {
            throw new ServiceException("会话组Id为空");
        }
        if (CollectionUtils.isEmpty(fileIds)) {
            throw new ServiceException("文件Id为空");
        }
        for (String fileId : fileIds) {
            /* 取文件信息 */
            UserFile userFile = iUserFileDao.getUserFileByFileId(fileId);
            if (Objects.isNull(userFile)) {
                throw new ServiceException("找不到该文件");
            }
            Integer userId = ReqUtil.instance.getUserId();
            MessageVO messageVO = new MessageVO();
            messageVO.setFromUserId(String.valueOf(userId));
            messageVO.setGid(groupId);
            /* 默认写死推送 */
            messageVO.setIsPush("true");
            messageVO.setType(MsgTypeEnum.FILE.getValue());
            messageVO.setKey(fileId);
            messageVO.setName(userFile.getName());
            messageVO.setUri(userFile.getUrl());
            messageVO.setFormat(userFile.getSuffix());
            messageVO.setSize(Long.toString(userFile.getSize()));
            /* 发送IM通知 */
            JSONObject json = (JSONObject) MsgHelper.sendMsg(messageVO);
            logger.info("用户：{}发送文件Json：{}", userId, json);
            /* 保存发送文件记录 */
            // 消息id
            String msgId = json.getString("mid");
            UserSendRecord temp = iUserFileDao.findByMsgId(msgId);
            if (Objects.nonNull(temp)) {
                throw new ServiceException("已存在对应的文件发送记录，请勿重复添加");
            }
            // 当前用户id
            int currentUserId = ReqUtil.instance.getUserId();
            int currentUserType = ReqUtil.instance.getUserType();
            UserSendRecord userSendRecord = new UserSendRecord();
            userSendRecord.setName(userFile.getName());
            userSendRecord.setSendUserId(currentUserId);
            userSendRecord.setUserType(currentUserType);
            userSendRecord.setSuffix(userFile.getSuffix());
            userSendRecord.setMimeType(userFile.getMimeType());
            userSendRecord.setFileType(userFile.getFileType());
            userSendRecord.setSize(userFile.getSize());
            userSendRecord.setUrl(userFile.getUrl());
            userSendRecord.setBucketType(userFile.getBucketType());
            userSendRecord.setGroupId(groupId);
            userSendRecord.setMsgId(msgId);
            userSendRecord.setFileId(fileId);
            userSendRecord.setStatus(UserFileEnum.UserFileStatus.common.getValue());
            userSendRecord.setSendTime(System.currentTimeMillis());

            /* 先兼容老接口 往旧表里写数据 */
            VpanSendRecord vpanSendRecord = new VpanSendRecord();
            vpanSendRecord.setName(userFile.getName());
            vpanSendRecord.setSendUserId(currentUserId);
            vpanSendRecord.setSuffix(userFile.getSuffix());
            vpanSendRecord.setMimeType(userFile.getMimeType());
            vpanSendRecord.setType(UserFileEnum.UserFileType.getEnum(userFile.getFileType()).name());
            vpanSendRecord.setSize(userFile.getSize());
            vpanSendRecord.setUrl(userFile.getUrl());
            vpanSendRecord.setBucketType("public");
            vpanSendRecord.setGid(groupId);
            vpanSendRecord.setMsgId(msgId);
            vpanSendRecord.setFileId(fileId);
            vpanSendRecord.setGid(groupId);
            vpanSendRecord.setBucket("vpan");
            vpanSendRecord.setUserType(currentUserType);

            /* 根据gid获取会话组信息 */
            GroupInfoRequestMessage requestMessage = new GroupInfoRequestMessage();
            requestMessage.setGid(groupId);
            requestMessage.setUserId(String.valueOf(currentUserId));
            GroupInfo groupInfo = null;
            try {
                groupInfo = (GroupInfo) msgService.getGroupInfo(requestMessage);
            } catch (HttpApiException e) {
                logger.error("获取会话组信息出错：{}", e.getMessage());
                e.printStackTrace();
            }

            // 设置会话组名称
            String imName = groupInfo.getGname();
            userSendRecord.setIMName(imName);

            // 先兼容老接口 往旧表里写数据
            vpanSendRecord.setImName(imName);

            String sourceName = null;
            if (currentUserType == UserEnum.UserType.enterpriseUser.getIndex()) {
                // 取药企的用户 药企没有提供单个获取用户的接口 只能获取多个
                CDurgorgUserInfo drugOrgUserInfo = null;
                try {
                    drugOrgUserInfo = drugOrgApiClientProxy.getByContext(ReqUtil.instance.getToken());
                } catch (HttpApiException e) {
                    logger.error("取药企的用户出错：{}", e.getMessage());
                    e.printStackTrace();
                }
                if (Objects.nonNull(drugOrgUserInfo) && Objects.nonNull(drugOrgUserInfo.getMajorUser())) {
                    sourceName = drugOrgUserInfo.getMajorUser().getName();
                }

            } else if (currentUserType == UserEnum.UserType.doctor.getIndex()) {
                // 取医生圈中的用户
                User currentUser = userRepository.getUser(currentUserId);
                if (Objects.nonNull(currentUser)) {
                    sourceName = currentUser.getName();
                }
            }

            userSendRecord.setSourceName(sourceName);

            // 先兼容老接口 往旧表里写数据
            vpanSendRecord.setSourceName(sourceName);

            // 取出会话组成员
            List<GroupUserInfo> groupUserInfoList = groupInfo.getUserList();
            /* 除去发送者 */
            if (!CollectionUtils.isEmpty(groupUserInfoList)) {
                List<GroupUserInfo> withoutSender = groupUserInfoList.stream()
                                                                     .filter(groupUserInfo -> Objects.nonNull(groupUserInfo.getId()))
                                                                     .filter(groupUserInfo -> !StringUtils.equals(groupUserInfo.getId(), String.valueOf(currentUserId)))
                                                                     .collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(withoutSender)) {
                    List<Integer> receiveUserId = withoutSender.stream()
                                                               .map(groupUserInfo -> Integer.valueOf(groupUserInfo.getId()))
                                                               .collect(Collectors.toList());
                    userSendRecord.setReceiveUserId(receiveUserId);

                    // 先兼容老接口 往旧表里写数据
                    vpanSendRecord.setReceiveUserId(receiveUserId);

                    /* 当IM中包含多个用户时 toUserName为会话组名称 sourceName也存为会话组名称 */
                    if (Objects.equals(withoutSender.size(), 1)) {
                        userSendRecord.setTargetName(withoutSender.get(0).getName());

                        // 先兼容老接口 往旧表里写数据
                        vpanSendRecord.setTargetName(withoutSender.get(0).getName());
                    } else {
                        userSendRecord.setTargetName(imName);
                        userSendRecord.setSourceName(imName);

                        // 先兼容老接口 往旧表里写数据
                        vpanSendRecord.setTargetName(imName);
                        vpanSendRecord.setSourceName(imName);
                    }
                }
            }
            // insert
            iUserFileDao.addUserSendRecord(userSendRecord);

            // 先兼容老接口 往旧表里写数据
            vpanFileDao.saveSendFileRecord(vpanSendRecord);
        }
    }

    /**
     * @param suffix 文件后缀
     * @return see UserFileEnum.UserFileType
     */
    private Integer matchFileType(String suffix) {
        //参数校验
        if (Objects.isNull(suffix)) {
            return UserFileEnum.UserFileType.other.getValue();
        }
        String lowerSuffix = StringUtils.lowerCase(suffix);
        //文档类型
        if (docTypeList().contains(lowerSuffix)) {
            return UserFileEnum.UserFileType.document.getValue();
        }
        //图片类型
        if (picTypeList().contains(lowerSuffix)) {
            return UserFileEnum.UserFileType.picture.getValue();
        }
        //视频类型
        if (videoTypeList().contains(lowerSuffix)) {
            return UserFileEnum.UserFileType.video.getValue();
        }
        //音乐类型
        if (musicTypeList().contains(lowerSuffix)) {
            return UserFileEnum.UserFileType.music.getValue();
        }
        return UserFileEnum.UserFileType.other.getValue();
    }


    /**
     * 迭代校验自己上传的文件是否有重名，有重名则在文件名后自动添加序号直到不重名
     *
     * @param sourceName 原始文件名 （保证迭代过程中原文件名不变化）
     * @param fileName   文件名
     * @param suffix     文件后缀
     * @param uploader   文件上传人
     * @param count      添加序号
     * @return newFileName
     */
    private String checkRepeatUserFileName(String sourceName, String fileName, String suffix, Integer uploader, int count) {
        UserFile repeatFile = iUserFileDao.findUserFile(fileName, UserFileEnum.UserFileStatus.common.getValue(), uploader);
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
        return checkRepeatUserFileName(sourceName, newFileName, suffix, uploader, count);
    }

    /**
     * vpan 的下载域名
     */
    private static String vpanDomain() {
        return PropertiesUtil.getContextProperty("qiniu.vpan.domain");
    }

    /**
     * vpan 空间名
     */
    private static String vpanBucket() {
        return PropertiesUtil.getContextProperty("qiniu.vpan.bucket");
    }

    /**
     * 文档 类型的文件后缀list
     */
    private static List<String> docTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.document").split(","));
    }

    /**
     * 图片 类型的文件后缀list
     */
    private static List<String> picTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.picture").split(","));
    }

    /**
     * 视频 类型的文件后缀list
     */
    private static List<String> videoTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.video").split(","));
    }

    /**
     * 音乐 类型的文件后缀list
     */
    private static List<String> musicTypeList() {
        return Arrays.asList(PropertiesUtil.getContextProperty("qiniu.vpan.filetype.music").split(","));
    }

    private List<AuthSimpleUser> getSimpleUserList(List<Integer> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return null;
        }
        Map<String, List<Integer>> idMap = Maps.newHashMap();
        idMap.put("userId", userIdList);
        String response = ribbonManager.post("http://AUTH2/v2/user/getSimpleUser", idMap);
        RemoteServiceResult result = JSON.parseObject(response, RemoteServiceResult.class);
        if (!Objects.equals(result.getResultCode(), Constants.ResultCode.Success)) {
            throw new ServiceException(result.getDetailMsg());
        }
        return JSONArray.parseArray(JSON.toJSONString(result.getData()), AuthSimpleUser.class);
    }

}


