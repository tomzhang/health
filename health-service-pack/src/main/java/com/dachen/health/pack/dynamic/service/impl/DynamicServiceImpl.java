package com.dachen.health.pack.dynamic.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.dachen.util.*;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.http.FileUploadUtil;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.template.PubTemplateUtil;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.pack.dynamic.constant.DynamicEnum;
import com.dachen.health.pack.dynamic.dao.IDynamicDao;
import com.dachen.health.pack.dynamic.entity.param.DynamicParam;
import com.dachen.health.pack.dynamic.entity.po.Dynamic;
import com.dachen.health.pack.dynamic.entity.vo.DynamicVO;
import com.dachen.health.pack.dynamic.service.IDynamicService;

@Service
public class DynamicServiceImpl implements IDynamicService {

    private static Logger logger = LoggerFactory.getLogger(DynamicServiceImpl.class);

    @Autowired
    private IDynamicDao dynamicDao;

    @Autowired
    private UserManager userManager;

    @Autowired
    private IGroupService groupService;
    
    @Autowired
    FileUploadUtil fileUpload;


    private void setData(List<DynamicVO> voList) {
        if (null == voList || voList.size() == 0) {
            return;
        }
        String userName = "";
        String headImage = "";
        for (DynamicVO dynamic : voList) {
            Integer userId = dynamic.getUserId();
            String groupDBId = dynamic.getGroupId();
            Integer catagory = dynamic.getCategory();
            if (DynamicEnum.CategoryEnum.doctor.getIndex() == catagory) {
                if (null != userId && userId.intValue() > 0) {
                    User user = userManager.getUser(userId);
                    if (null != user) {
                        userName = user.getName();
                        headImage = user.getHeadPicFileName();
                    }
                }
            } else {
                if (StringUtils.isNotEmpty(groupDBId)) {
                    Group group = groupService.getGroupById(groupDBId);

                    if (null != group) {
                        headImage = group.getLogoUrl();
                        userName = group.getName();
                    }
                }
            }
            dynamic.setName(userName == null ? "" : userName);
            dynamic.setHeadImage(headImage == null ? "" : headImage);
        }
    }


    @Override
    public PageVO getGroupAndDoctorDynamicListByGroupId(String groupId, Integer pageIndex, Integer pageSize) {

        Integer currentUserId = ReqUtil.instance.getUserId();

        PageVO page = dynamicDao.getGroupAndDoctorDynamicListByGroupId(groupId, currentUserId, pageIndex, pageSize);

        List<DynamicVO> voList = new ArrayList<DynamicVO>();
        if (null != page.getPageData() && page.getPageData().size() > 0) {
            List<Dynamic> dynamics = (List<Dynamic>) page.getPageData();
            if (null != dynamics && dynamics.size() > 0) {
                voList = BeanUtil.copyList(dynamics, DynamicVO.class);
                setData(voList);
            }
        }
        page.setPageData(voList);
        return page;
    }

    @Override
    public PageVO getPatientRelatedDynamicList(Integer userId, Long createTime, Integer pageSize) {

        Integer currentUserId = ReqUtil.instance.getUserId();

        PageVO page = dynamicDao.getPatientRelatedDynamicList(userId, currentUserId, createTime, pageSize);

        List<DynamicVO> voList = new ArrayList<DynamicVO>();

        if (null != page.getPageData() && page.getPageData().size() > 0) {
            List<Dynamic> dynamics = (List<Dynamic>) page.getPageData();
            if (null != dynamics && dynamics.size() > 0) {
                voList = BeanUtil.copyList(dynamics, DynamicVO.class);
                setData(voList);
            }
        }
        page.setPageData(voList);
        return page;
    }

    @Override
    public void addDoctorDynamic(Integer userId, String content, String[] imageList, Integer[] userIds) {
        if (StringUtils.isEmpty(content)) {
            throw new ServiceException("动态内容不能够为空！");
        }
        DynamicParam param = new DynamicParam();
        if (null != imageList) {
            if (imageList.length > 8) {
                throw new ServiceException("图片数量不超过8张！");
            }
            param.setImageList(imageList);
        }
        param.setStyleType(DynamicEnum.StyleEnum.text.getIndex());
        param.setCategory(DynamicEnum.CategoryEnum.doctor.getIndex());
        param.setContent(content);
        param.setUserId(userId);
        param.setCreator(userId.toString());
        if (userIds != null && userIds.length > 0) {
            param.setUserIds(Arrays.asList(userIds));
        } else {
            List<Integer> uIds = Lists.newArrayList();
            uIds.add(0);
            param.setUserIds(uIds);
        }
        dynamicDao.saveDynamic(param);

        EcEvent event = EcEvent.build(EventType.DOCTOR_NEW_DYNAMIC);
        event.param("doctorId", userId.toString());
        event.param("userIds", param.getUserIds());

        //医生动态推送
        EventProducer.fireEvent(event);
    }

    @Override
    public void deleteDoctorDynamic(String id) {

        if (StringUtils.isEmpty(id)) {
            throw new ServiceException("动态id不能够为空！");
        }
        dynamicDao.deleteDynamic(id);
    }

    @Override
    public PageVO getDoctorDynamicList(Integer userId, Integer pageIndex, Integer pageSize) {

        Integer currentUserId = ReqUtil.instance.getUserId();

        PageVO page = dynamicDao.getDoctorDynamicList(userId, currentUserId, pageIndex, pageSize);

        List<DynamicVO> voList = new ArrayList<DynamicVO>();
        if (null != page.getPageData() && page.getPageData().size() > 0) {
            List<Dynamic> dynamics = (List<Dynamic>) page.getPageData();
            if (null != dynamics && dynamics.size() > 0) {
                voList = BeanUtil.copyList(dynamics, DynamicVO.class);
                String userName = "";
                String headImage = "";
                User user = userManager.getUser(userId);
                if (null != user) {
                    userName = user.getName();
                    headImage = user.getHeadPicFileName();
                }

                for (DynamicVO dynamic : voList) {
                    dynamic.setName(userName == null ? "" : userName);
                    dynamic.setHeadImage(headImage == null ? "" : headImage);
                }
            }
        }
        page.setPageData(voList);
        return page;
    }

    @Override
    public PageVO getMyDynamicList(Integer doctorId, Integer pageIndex, Integer pageSize) {

        PageVO page = dynamicDao.getMyDynamicList(doctorId, pageIndex, pageSize);

        List<DynamicVO> voList = new ArrayList<DynamicVO>();
        if (null != page.getPageData() && page.getPageData().size() > 0) {
            List<Dynamic> dynamics = (List<Dynamic>) page.getPageData();
            if (null != dynamics && dynamics.size() > 0) {
                voList = BeanUtil.copyList(dynamics, DynamicVO.class);
                String userName = "";
                String headImage = "";
                User user = userManager.getUser(doctorId);
                if (null != user) {
                    userName = user.getName();
                    headImage = user.getHeadPicFileName();
                }

                for (DynamicVO dynamic : voList) {
                    dynamic.setName(userName == null ? "" : userName);
                    dynamic.setHeadImage(headImage == null ? "" : headImage);
                }
            }
        }
        page.setPageData(voList);
        return page;
    }

    @Override
    public void addDoctorDynamicForWeb(DynamicParam param) {
        if (StringUtils.isEmpty(param.getTitle())) {
            throw new ServiceException("动态标题不能够为空！");
        }
        if (StringUtils.isEmpty(param.getContentUrl())) {
            throw new ServiceException("动态题图不能够为空！");
        }
        param.setStyleType(DynamicEnum.StyleEnum.html_five.getIndex());
        param.setCategory(DynamicEnum.CategoryEnum.doctor.getIndex());
        param.setUserId(param.getUserId());
        param.setCreator(param.getUserId().toString());

        String content = param.getContent() == null ? "" : param.getContent();

        //获取用户姓名
        String author = getUserName(param.getUserId());
        //获取html5的路径
        String htmlUrl = getHtmlFiveUrl(param.getTitle(), param.getContentUrl(), content, author);
        param.setUrl(htmlUrl);
        dynamicDao.saveDynamic(param);

        EcEvent event = EcEvent.build(EventType.DOCTOR_NEW_DYNAMIC);
        event.param("doctorId", param.getUserId().toString());
        event.param("userIds", param.getUserIds());

        //医生动态推送
        EventProducer.fireEvent(event);

    }

    /**
     * 获取用户姓名
     *
     * @param userId
     * @return
     */
    private String getUserName(Integer userId) {
        String userName = "";
        User user = userManager.getUser(userId);
        if (null != user) {
            userName = user.getName();
        }

        return userName;
    }


    private String getHtmlFiveUrl(String title, String contentUrl, String content, String author) {
        String htmlUrl = "";

        if (!StringUtils.isEmpty(content)) {
            String titleStr = content;
            titleStr = ParserHtmlUtil.delHTMLTag(titleStr); //去标签
            titleStr = ParserHtmlUtil.delTransferredCode(titleStr);//去转义符
            //替换模版

            content = ParserHtmlUtil.changeLineTag(content);// 正文\n-<br>
            content = PubTemplateUtil.repPubContent(title, contentUrl, content, author).toString();

            // 上传到Servlet保存消息文章
//            final String upUrl = PropertiesUtil.getContextProperty("fileserver.upload") + "upload/UploadDocumentServlet";
//            htmlUrl = FileUploadUtil.uploadToFileServer(upUrl, "tmp.html", "pub", content);
            htmlUrl = fileUpload.uploadToFileServer("tmp.html", "pub", content);
        }
        return htmlUrl;
    }


    private String getGroupName(String groupId) {
        String
                groupName = userManager.getGroupNameById(groupId);
        if (null == groupName) {
            groupName = "";
        }
        return groupName;
    }


    @Override
    public void addGroupDynamicForWeb(DynamicParam param) {

        if (StringUtils.isEmpty(param.getTitle())) {
            throw new ServiceException("动态标题不能够为空！");
        }
        if (StringUtils.isEmpty(param.getContent())) {
            throw new ServiceException("动态内容不能够为空！");
        }
        if (StringUtils.isEmpty(param.getGroupId())) {
            throw new ServiceException("集团id不能够为空！");
        }

        param.setStyleType(DynamicEnum.StyleEnum.html_five.getIndex());
        param.setCategory(DynamicEnum.CategoryEnum.dorctr_group.getIndex());
        String content = param.getContent() == null ? "" : param.getContent();

        //获取集团名称
        String author = getGroupName(param.getGroupId());
        //获取html5的路径
        String htmlUrl = getHtmlFiveUrl(param.getTitle(), param.getContentUrl(), content, author);
        param.setUrl(htmlUrl);
        param.setCreator(param.getGroupId());
        dynamicDao.saveDynamic(param);

        EcEvent event = EcEvent.build(EventType.GROUP_NEW_DYNAMIC);
        event.param("groupId", param.getGroupId());
        event.param("userIds", param.getUserIds());

        //集团动态推送
        EventProducer.fireEvent(event);

    }

    @Override
    public PageVO getDynamicListByGroupIdForWeb(String groupId, Integer pageIndex, Integer pageSize) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException(50002, "集团id不能为空！");
        }
        PageVO page = dynamicDao.getDynamicListByGroupIdForWeb(groupId, pageIndex, pageSize);

        List<DynamicVO> voList = new ArrayList<DynamicVO>();
        if (null != page.getPageData() && page.getPageData().size() > 0) {
            List<Dynamic> dynamics = (List<Dynamic>) page.getPageData();
            if (null != dynamics && dynamics.size() > 0) {
                voList = BeanUtil.copyList(dynamics, DynamicVO.class);

                String groupName = null;
                String logo = null;
                Group group = groupService.getGroupById(groupId);

                if (null != group) {
                    logo = group.getLogoUrl();

                    groupName = group.getName();
                }

                for (DynamicVO dynamic : voList) {
                    dynamic.setName(groupName == null ? "" : groupName);
                    dynamic.setHeadImage(logo == null ? "" : logo);
                }
            }
        }
        page.setPageData(voList);
        return page;
    }


}
