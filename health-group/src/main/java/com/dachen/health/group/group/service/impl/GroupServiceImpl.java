package com.dachen.health.group.group.service.impl;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventProducer;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.model.EsDiseaseType;
import com.dachen.elasticsearch.model.EsGroup;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.ExistRegionVo;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.po.RegionVo;
import com.dachen.health.base.entity.vo.DepartmentVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.JobTaskUtil;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupUserStatus;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.entity.UserDiseaseLaber;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.common.entity.vo.RecommendGroupVO;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.company.service.ICompanyUserService;
import com.dachen.health.group.group.PubGroupUtils;
import com.dachen.health.group.group.dao.*;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.param.GroupsParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupApply;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.GroupUserApply;
import com.dachen.health.group.group.entity.vo.*;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.recommand.dao.IDiseaseLaberDao;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.Doctor.Check;
import com.dachen.health.user.entity.po.OperationRecord;
import com.dachen.im.server.data.GroupManagerVO;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.im.server.enums.RelationTypeEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.pub.model.PubTypeEnum;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.MongodbUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author pijingwei
 * @date 2015/8/7
 */
@Service
public class GroupServiceImpl implements IGroupService {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected IGroupDao groupDao;

    @Autowired
    protected IGroupDoctorService gdocService;

    @Autowired
    protected ICompanyUserService companyUserService;

    @Autowired
    protected IGroupDoctorDao gdDao;

    @Autowired
    protected IBaseDataService baseDataService;

    @Autowired
    protected IBusinessServiceMsg businessMsgServiceImpl;

    @Autowired
    protected MobSmsSdk mobSmsSdk;

    @Autowired
    protected UserManager userManager;

    @Autowired
    protected IBaseDataDao baseDataDao;

    @Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;

    @Autowired
    protected PubGroupService pubGroupService;

    @Autowired
    protected IGroupUserDao groupUserDao;

    @Autowired
    protected IGroupDoctorDao groupDoctroDao;

    @Autowired
    protected IGroupSearchDao groupSearchDao;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected IQrCodeService qrCodeService;

    @Autowired
    protected IOperateRecordDao recordDao;

    @Autowired
    protected IDiseaseLaberDao laberDao;

    @Autowired
    protected IMsgService imsgService;

    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;

    /**
     * 直辖市：北京、天津、上海、重庆
     */
    public List<Integer> directCityCodes = Arrays.asList(new Integer[]{110000, 120000, 310000, 500000});

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupService#createGroup(com.dachen.health.group.group.entity.po.Group)
     */
    @Override
    public Group createGroup(Group group) {
        if (StringUtil.isEmpty(group.getName())) {
            throw new ServiceException("集团名称不能为空！");
        }
        if (groupDao.getByName(group.getName()) != null) {
            throw new ServiceException("该集团名称已被占用，请重新选择集团名称！");
        }

        int createUserId = group.getCreator();

        //只有医生才能创建医生集团
//		DoctorBasicInfo doctor = commonDao.getDoctorBasicInfoById(createUserId, 3, null);
//		DoctorBasicInfo doctor = commonService.getDoctorBasicInfoById(createUserId, UserType.doctor.getIndex());
        User doctor = userManager.getUser(createUserId);

        if (doctor == null || doctor.getUserType() != UserType.doctor.getIndex()) {
            throw new ServiceException("您不是医生，无法创建");
        }

        //持久化
        Group gp = groupDao.save(group);

        //移到 groupFacadeService
//		this.createPubFromGroup(gp,"集团动态",PubTypeEnum.PUB_GROUP_DOCTOR.getValue());//
//		this.createPubFromGroup(gp,"患者之声",PubTypeEnum.PUB_GROUP_PATIENT.getValue());//

        JobTaskUtil.offlineTimeout(gp.getId());

        return gp;
    }


    public Group updateGroup(Group group) throws HttpApiException {
        if (StringUtil.isEmpty(group.getId())) {
            throw new ServiceException("医生集团Id为空！");
        }

        //修改集团信息时，同步修改apply表
        GroupApply apply = new GroupApply();
        apply.setGroupId(group.getId());

        boolean updateToEs = false;
        boolean updateToPub = false;
        boolean updateToGroupIMName = false;
        boolean updateToGroupIMLogo = false;

        if (!StringUtil.isEmpty(group.getName())) {
            apply.setName(group.getName());
            updateToEs = true;
            updateToPub = true;
            updateToGroupIMName = true;
        }
        if (!StringUtil.isEmpty(group.getIntroduction())) {
            apply.setIntroduction(group.getIntroduction());
            updateToPub = true;
        }
        if (!StringUtil.isEmpty(group.getLogoUrl())) {
            apply.setLogoUrl(group.getLogoUrl());
            updateToPub = true;
            updateToGroupIMLogo = true;
        }
        if (group.getDiseaselist() != null) {
            updateToEs = true;
        }

        //保存修改记录，包括logo、名称、简介
        saveModifyRecord(group);

        Group newGroup = groupDao.update(group);
        groupDao.updateGroupApplyByGroupId(apply);

        //平台直接返回
        if (group.getId().equals(GroupUtil.PLATFORM_ID)) {
            return newGroup;
        }

        if (updateToEs && isNormalGroup(group.getId())) {
            EcEvent event = EcEvent.build(EventType.GroupInfoUpdateForEs)
                    .param("bizid", newGroup.getId())
                    .param("name", newGroup.getName())
                    .param("diseaselist", getEsDiseaseTypeList(newGroup.getDiseaselist()));
            EventProducer.fireEvent(event);
        }
        if (updateToPub) {
            PubGroupUtils.updatePubInfo(group);
        }

        Group temp = groupDao.getById(group.getId());
        if (updateToGroupIMName) {
            updateGroupIMName(temp);
        }

        if (updateToGroupIMLogo) {
            updateGroupIMLogo(temp);
        }

        return newGroup;
    }

    public PageVO searchGroup(GroupParam group) {
        return groupDao.search(group);
    }

    @Deprecated
    public boolean deleteGroup(String... ids) {
        //暂时废弃 删除集团相关业务
//		if(dedocDao.getCountByGroupIds(ids) > 0) {
//			throw new ServiceException("请先删除集团科室下分配的医生");
//		}
//		if(departDao.findCountByGroupIds(ids) > 0) {
//			throw new ServiceException("请先删除集团下的科室");
//		}
//		if(cuserDao.getQueryByGroupIds(ids).countAll() > 0) {
//			throw new ServiceException("请先删除集团下的管理员");
//		}
//		if(gdDao.getCountByGroupIds(ids) > 0) {
//			throw new ServiceException("请先删除集团下的医生");
//		}
//		groupDao.delete(ids);
//		profitDao.delete(ids);
        return true;
    }

    public Group getPlatformInfo() {
        return this.getGroupById(GroupUtil.PLATFORM_ID);
    }

    @Override
    public Group getGroupById(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("集团Id为空");
        }
        Group g = groupDao.getById(id, null);
        if (g == null) {
            return null;
        }

        GroupUser groupUser = groupUserDao.findGroupRootAdmin(id);
        if (groupUser != null) {
            User creator = userManager.getUser(groupUser.getDoctorId());
            if (creator != null) {
                g.setCreatorName(creator.getName());
            }
        }

        Map<String, Object> map = Maps.newHashMap();
        Integer userId = ReqUtil.instance.getUserId();
        if (userId != null && userId > 0) {
            User u = userManager.getUser(userId);
            String rootAdmin = getGroupRootAdmin(id, userId);
            map.put("rootAdmin", rootAdmin);
            map.put("name", u.getName());
            map.put("headPicture", u.getHeadPicFileName());
            map.put("doctorId", u.getUserId());
            //用户状态 1:未加入集团 2：加入集团非管理员 3:管理员
            List<GroupDoctor> list = gdDao.findGroupDoctor(userId, id, GroupEnum.GroupDoctorStatus.正在使用.getIndex());
            GroupUser gu = companyUserService.getGroupUserByIdAndStatus(null, userId, id,null, GroupUserStatus.正常使用.getIndex());

            if (list != null && list.size() > 0) {
                if (gu != null) {
                    g.setUserStatus("3");
                } else {
                    g.setUserStatus("2");
                }
            } else {
                g.setUserStatus("1");
            }
            g.setGroupUser(map);
        }
        return g;
    }

    private String getGroupRootAdmin(String groupId, int userId) {
        GroupUser gu = gdDao.findGroupDoctorByGroupIdAndDoctorId(groupId, userId);
        if (gu != null) {
            return gu.getRootAdmin();
        }
        return null;
    }

    @Override
    public Group increaseCureNum(String groupId) throws HttpApiException {
        Group group = getGroupById(groupId);
        if (group != null) {
            Integer cureNum = group.getCureNum() != null ? (group.getCureNum() + 1) : 1;
            group = new Group();
            group.setId(groupId);
            group.setCureNum(cureNum);
            group = updateGroup(group);
        }
        return group;
    }

    /**
     * rtype:TODO
     */
//	@Override
//	public PubPO createPubFromGroup(Group gp,String name,String rtype)
//	{
//		/**
//		 * 创建公共号
//		 */
//		Integer creator = gp.getCreator();
//		// TODO
//		String photoUrl = "/cert/"+String.valueOf(creator % 10000)+"/"+creator+"/"+gp.getId()+"/groupLogo.jpg";
//		PubParam pubParam = new PubParam();
//		pubParam.setMid(gp.getId());
//		/**
//		 * rtype=RelationTypeEnum.DOCTOR 集团新闻
//		 * rtype=RelationTypeEnum.PATIENT 患者之声
//		 */
//		pubParam.setName(name+"_"+gp.getName());
//		pubParam.setNickName(gp.getName());
//		pubParam.setNote(gp.getIntroduction());
//		pubParam.setRtype(rtype);
//		pubParam.setPhotourl(photoUrl);
//		pubParam.setDefault(true);
//		pubParam.setCreator(String.valueOf(creator));
//		if(PubTypeEnum.PUB_GROUP_DOCTOR.getValue().equals(rtype))
//		{
//			pubParam.setPid(PubUtils.PUB_GROUP_NEWS+gp.getId());
//		}
//		else
//		{
//			pubParam.setPid(PubUtils.PUB_PATIENT_VOICE+gp.getId());
//		}
//
//		PubPO pub =  pubAccountService.createPub(pubParam);
////		pubAccountService.addPubUser(pub.getPid(),String.valueOf(creator),UserRoleEnum.ADMIN,false);
//		return pub;
//	}
    @Override
    public List<Map<String, Object>> getConsultationGroupByDoctorId(Integer doctorId, List<String> groupIds) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<GroupDoctor> gds = gdocService.findGroupDoctor(doctorId, null, GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        for (GroupDoctor gd : gds) {
            Map<String, Object> map = new HashMap<String, Object>();
            String groupId = gd.getGroupId();
            Group group = getGroupById(groupId);
            if (group != null
                    && group.getConfig() != null
                    && group.getConfig().getOpenConsultation()
                    && groupIds.contains(groupId)) {
                map.put("groupId", group.getId());
                map.put("groupName", group.getName());
                map.put("isMain", (gd.isMain() != null) ? gd.isMain() : false);
                map.put("createTime", gd.getCreatorDate());
                list.add(map);
            }
        }
        return list;
    }


    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupService#createNewGroupApply(com.dachen.health.group.group.entity.po.Group, java.lang.String)
     */
    @Override
    public GroupApply createNewGroupApply(Group group, String logoUrl) {
        GroupApply groupApply = new GroupApply();
        groupApply.setGroupId(group.getId());
        groupApply.setName(group.getName());
        groupApply.setIntroduction(group.getIntroduction());
        groupApply.setApplyUserId(group.getCreator());
        groupApply.setLogoUrl(logoUrl);

        groupApply.setStatus(GroupEnum.GroupApply.audit.getIndex());
        groupApply.setApplyDate(System.currentTimeMillis());
        return groupDao.insertgroupApply(groupApply);
    }

    @Override
    @Deprecated
    public GroupApply groupApply(GroupApply groupApply) throws HttpApiException {
        if (groupApply == null) {
            return null;
        }
        if (groupApply.getApplyUserId() == null) {
            throw new ServiceException("申请者为空");
        }
        if (StringUtil.isEmpty(groupApply.getName())) {
            throw new ServiceException("集团名称为空");
        }
        if (StringUtil.isNotBlank(groupApply.getLogoUrl())) {
            String logoPath = PropertiesUtil.removeUrlPrefix(groupApply.getLogoUrl());
            groupApply.setLogoUrl(logoPath);
        }
        int applyUserId = groupApply.getApplyUserId();
        GroupUser gu = gdDao.findByRootAndDoctorId(applyUserId);
        if (gu != null) {
            throw new ServiceException("您已经是其他集团超级管理员，不能再次申请");
        }
        if (StringUtil.isEmpty(groupApply.getId())) {
            //第一次申请
            GroupApply ga = groupDao.getGroupApplyByApplyUserId(groupApply.getApplyUserId());
            if (ga == null) {
                groupApply.setStatus(GroupEnum.GroupApply.audit.getIndex());
                groupApply.setApplyDate(System.currentTimeMillis());
                groupDao.insertgroupApply(groupApply);
                User user = userManager.getUser(applyUserId);
                if (user != null) {
                    List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                    ImgTextMsg textMsg = new ImgTextMsg();
                    textMsg.setStyle(7);
                    textMsg.setTitle("申请创建医生圈子");
                    textMsg.setTime(System.currentTimeMillis());
                    textMsg.setContent("尊敬的" + user.getName() + "医生，您已申请创建医生圈子，目前正在审核中，请您耐心等待。");
                    Map<String, Object> param = new HashMap<String, Object>();
                    textMsg.setParam(param);
                    mpt.add(textMsg);
                    businessMsgServiceImpl.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                    businessMsgServiceImpl.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
                    //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您已申请创建医生集团，目前正在审核中，请您耐心等待。");
                    final String msg = baseDataService.toContent("1020", user.getName());
                    mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
                }
            } else {
                if (ga.getStatus().equals(GroupEnum.GroupApply.pass.getIndex())) {
                    throw new ServiceException("您已有医生圈子");
                } else {
                    throw new ServiceException("您已提交申请");
                }
            }
        } else {
            //再次申请
            GroupApply ga = groupDao.getGroupApplyById(groupApply.getId());
            if (ga == null) {
                throw new ServiceException("参数异常");
            } else {
                String status = ga.getStatus();
                if (!GroupEnum.GroupApply.notpass.getIndex().equals(status)) {
                    throw new ServiceException("参数数据异常");
                } else {
                    groupApply.setStatus(GroupEnum.GroupApply.audit.getIndex());
                    groupApply.setApplyDate(System.currentTimeMillis());
                    groupDao.updateGroupApply(groupApply);

                    User user = userManager.getUser(applyUserId);
                    if (user != null) {
                        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                        ImgTextMsg textMsg = new ImgTextMsg();
                        textMsg.setStyle(7);
                        textMsg.setTitle("申请创建医生圈子");
                        textMsg.setTime(System.currentTimeMillis());
                        textMsg.setContent("尊敬的" + user.getName() + "医生，您已申请创建医生圈子，目前正在审核中，请您耐心等待。");
                        Map<String, Object> param = new HashMap<String, Object>();
                        textMsg.setParam(param);
                        mpt.add(textMsg);
                        businessMsgServiceImpl.sendTextMsg(applyUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                        //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您已申请创建医生集团，目前正在审核中，请您耐心等待。");
                        final String msg = baseDataService.toContent("1020", user.getName(), BaseConstants.XG_YSQ_APP);
                        mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
                    }

                }
            }
        }
        return groupApply;
    }

    @Override
    public GroupApply getGroupApplyInfo(String groupApplyId, Integer applyUserId) {
        if (StringUtil.isNullOrEmpty(groupApplyId)
                && applyUserId == null) {
            throw new ServiceException("参数为空");
        }
        GroupApply ga = null;
        if (StringUtil.isNotBlank(groupApplyId)) {
            ga = groupDao.getGroupApplyById(groupApplyId);
        } else {
            ga = groupDao.getGroupApplyByApplyUserId(applyUserId);
        }
        return ga;
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupService#getLastGroupApply(java.lang.String)
     */
    @Override
    public GroupApply getLastGroupApply(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("参数为空");
        }
        return groupDao.getLastGroupApplyByGroupId(groupId);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupService#processGroupApplyNew(com.dachen.health.group.group.entity.po.GroupApply)
     */
    @Override
    public void processGroupApplyNew(GroupApply groupApply) throws HttpApiException {
        //参数校验
        if (groupApply == null) {
            throw new ServiceException("参数不能为空");
        }
        String status = groupApply.getStatus();
        if (StringUtils.isBlank(status)) {
            throw new ServiceException("审核状态错误");
        }
        GroupApply ga = groupDao.getGroupApplyById(groupApply.getId());
        if (ga == null) {
            throw new ServiceException("找不到集团申请信息");
        }
        if (!StringUtils.equals(GroupEnum.GroupApply.audit.getIndex(), ga.getStatus())) {
            throw new ServiceException("该集团已审核");
        }

        long nowDate = System.currentTimeMillis();
        int doctorId = ga.getApplyUserId();
        //更新集团信息
        Group group = getGroupById(ga.getGroupId());
        if (group == null) {
            throw new ServiceException("找不到集团申请信息");
        }
        group.setUpdator(groupApply.getAuditUserId());
        group.setUpdatorDate(nowDate);
        group.setApplyStatus(status);
        groupDao.update(group);

        //更新集团申请信息
//		groupApply.setAuditUserId(auditUserId);
//		groupApply.setAuditMsg(auditMsg);
//		groupApply.setStatus(status);
        groupApply.setAuditDate(nowDate);
        groupDao.updateGroupApply(groupApply);

        //审核通过
        if (StringUtils.equals(GroupEnum.GroupApply.pass.getIndex(), status)) {
            //发送审核通过通知
            User user = userManager.getUser(doctorId);
            if (user != null) {
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("医生圈子已通过审核");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + user.getName() + "医生，您申请的医生圈子已通过审核。现在可以邀请您的医生好友加入您的医生圈子");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setFooter("立即邀请");
                param.put("bizType", 23);
                param.put("bizId", ga.getGroupId());
                param.put("groupName", group.getName());
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
                //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您申请的医生集团已通过审核。现在可以邀请您的医生好友加入您的医生集团");
                final String msg = baseDataService.toContent("1019", user.getName());
                mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
                //一切操作完成之后将数据推向ES服务器
                syncGroupDataToEs(group);

                //异步创建集团会话组
                createGroupIm(group);

				/*EsGroup esGroup = new EsGroup(group.getId());
                esGroup.setName(group.getName());
				esGroup.setExpertise(getEsDiseaseTypeList(group.getDiseaselist()));
				ElasticSearchFactory.getInstance().insertDocument(esGroup);*/
                //开通公共号
                pubGroupService.createPubForGroupCreate("患者之声", PubTypeEnum.PUB_GROUP_PATIENT.getValue(),
                        group.getCreator(), group.getId(), group.getName(), group.getIntroduction(), group.getLogoUrl());
                pubGroupService.createPubForGroupCreate("集团动态", PubTypeEnum.PUB_GROUP_DOCTOR.getValue(),
                        group.getCreator(), group.getId(), group.getName(), group.getIntroduction(), group.getLogoUrl());
            }
        } else if (StringUtils.equals(GroupEnum.GroupApply.notpass.getIndex(), status)) {
            //发送审核不通过通知
            User user = userManager.getUser(doctorId);
            if (user != null) {
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("医生圈子未通过审核");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + user.getName() + "医生，您的申请未通过审核。");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setFooter("查看详情");
                String url = PropertiesUtil.getContextProperty("application.rootUrl")
                                        + PropertiesUtil.getContextProperty("group.web.prefix")
                                        + "/attachments/group_transfer/group_check_msg.html?groupApplyId=" + ga.getId() + "&access_token={{token}}";
                textMsg.setUrl(url);
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                final String msg = baseDataService.toContent("1018", user.getName(), "http://xg.mediportal.com.cn/health/web/mobile/#/common/openApp/dog", BaseConstants.XG_YSQ_APP);
                mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
                //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您的申请未通过审核。查看详情"+shortUrl);
            }
        }

    }


    private void syncGroupDataToEs(Group group) {
        if (isNormalGroup(group.getId())) { // add by tanyf 激活状态 且 审核通过 且 没有屏蔽（默认） 的集团才同步到 es中
            /*EcEvent event = EcEvent.build(EventType.GroupInfoInsertForEs)
					.param("bizid",group.getId())
					.param("name", group.getName())
					.param("diseaselist",getEsDiseaseTypeList(group.getDiseaselist()));
			EventProducer.fireEvent(event);*/
            //将全文索引里面该集团的信息保存
            EsGroup esGroup = new EsGroup(group.getId());
            esGroup.setName(group.getName());
            esGroup.setExpertise(getEsDiseaseTypeList(group.getDiseaselist()));
            ElasticSearchFactory.getInstance().insertDocument(esGroup);
        }
    }

    private List<EsDiseaseType> getEsDiseaseTypeList(List<String> diseaseIds) {
        if (diseaseIds == null || diseaseIds.size() == 0) {
            return null;
        }
        List<DiseaseType> diseases = diseaseTypeRepository.findByIds(diseaseIds);
        //医生集团擅长病种
        List<EsDiseaseType> diseaseTypeList = new ArrayList<EsDiseaseType>(diseaseIds.size());
        for (DiseaseType type : diseases) {
            diseaseTypeList.add(new EsDiseaseType(type.getName(), type.getAlias(), type.getRemark()));//TODO remark
        }
        return diseaseTypeList;
    }

    @Override
    @Deprecated
    public void processGroupApply(GroupApply groupApply) throws HttpApiException {
        if (groupApply == null) {
            throw new ServiceException("参数为空");
        }
        String status = groupApply.getStatus();
        if (StringUtils.isBlank(status)) {
            throw new ServiceException("审核状态错误");
        }
        GroupApply ga = groupDao.getGroupApplyById(groupApply.getId());
        if (ga == null) {
            throw new ServiceException("参数数据为空");
        }

        if (!StringUtils.equals(GroupEnum.GroupApply.audit.getIndex(), ga.getStatus())) {
            throw new ServiceException("该集团已审核");
        }

        long time = System.currentTimeMillis();
        int doctorId = ga.getApplyUserId();
        if (StringUtils.equals(GroupEnum.GroupApply.pass.getIndex(), status)) {
            /**
             * 1、创建群组
             * 2、添加申请者为群组超级管理员，并发送短信、通知
             * 3、更新申请记录的groupId字段
             */
            Group group = new Group();
            group.setId(ga.getId());
            group.setCreator(doctorId);
            group.setCreatorDate(time);
            group.setIntroduction(ga.getIntroduction());
            group.setName(ga.getName());
            group.setCertStatus(GroupEnum.GroupCertStatus.noCert.getIndex());
            group.setActive(GroupEnum.GroupActive.inactive.getIndex());
            String groupId = groupDao.saveGroupAtAuditPass(group).getId();

            groupApply.setGroupId(groupId);

            GroupDoctor groupDoctor = new GroupDoctor();

            groupDoctor.setCreator(doctorId);
            groupDoctor.setCreatorDate(time);
            groupDoctor.setDoctorId(doctorId);
            groupDoctor.setDeptName(ga.getName());
            groupDoctor.setGroupId(groupId);
            groupDoctor.setOnLineState(GroupEnum.OnLineState.onLine.getIndex());
            groupDoctor.setStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
            groupDoctor.setReferenceId(doctorId);
            groupDoctor.setUpdator(doctorId);
            groupDoctor.setUpdatorDate(time);
            gdDao.save(groupDoctor);

//			GroupUser groupUser = new GroupUser();
//			groupUser.setCreator(doctorId);
//			groupUser.setCreatorDate(time);
//			groupUser.setDoctorId(doctorId);
//			groupUser.setObjectId(groupId);
//			groupUser.setRootAdmin("root");
//			groupUser.setStatus(GroupEnum.GroupUserStatus.正常使用.getIndex());
//			groupUser.setType(2);
//			groupUser.setUpdator(doctorId);
//			groupUser.setUpdatorDate(time);
//			cuserDao.save(groupUser);

            //设置为超级管理员
            companyUserService.addRootGroupManage(groupId, doctorId, doctorId);

            User user = userManager.getUser(doctorId);
            if (user != null) {
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("申请创建医生圈子");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + user.getName() + "医生，您的申请的医生圈子已通过审核。现在可以邀请您的医生好友加入您的医生圈子");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setFooter("立即邀请");
                param.put("bizType", 23);
                param.put("bizId", groupId);
                param.put("groupName", group.getName());
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
                //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您的申请的医生集团已通过审核。现在可以邀请您的医生好友加入您的医生集团");
                final String msg = baseDataService.toContent("1019", user.getName());
                mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
            }

        } else if (StringUtils.equals(GroupEnum.GroupApply.notpass.getIndex(), status)) {
            User user = userManager.getUser(doctorId);
            if (user != null) {
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("申请创建医生圈子");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + user.getName() + "医生，您的申请未通过审核。");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setFooter("查看详情");
                String url =
                                PropertiesUtil.getContextProperty("application.rootUrl")
                                        + PropertiesUtil.getContextProperty("group.web.prefix")
                                        + "/attachments/group_transfer/group_check_msg.html?groupApplyId=" + ga.getId() + "&access_token={{token}}";
                textMsg.setUrl(url);
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您的申请未通过审核。查看详情"+shortUrl);
                final String msg = baseDataService.toContent("1018", user.getName(), "http://xg.mediportal.com.cn/health/web/mobile/#/common/openApp/dog");
                mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
            }
        }
        groupApply.setAuditDate(time);
        groupDao.updateGroupApply(groupApply);
    }

    @Override
    public PageVO getApplyList(String status, String groupActive, String skip, String groupName, String doctorName, String hospitalName, String telephone,
                               Integer pageSize, Integer pageIndex) {
        PageVO pageVo = new PageVO();
        List<Integer> doctorIds = null;
        // 关键词过滤
        if (StringUtils.isNotBlank(doctorName) || StringUtils.isNotBlank(hospitalName) || StringUtils.isNotBlank(telephone)) {
            doctorIds = userManager.getDoctorsByNameOrHospitalNameOrTelephone(doctorName, hospitalName, telephone);
        }
        // add by tanyf 2016-06-02
        List<String> groupIdList = null;
        Map<String, Group> groupIdMap = null;
//		if(StringUtil.isNotBlank(groupActive) || StringUtil.isNotBlank(skip)){
        GroupsParam g = new GroupsParam();
        g.setActive(groupActive);// 集团激活状态 过滤
        g.setSkip(skip);// 集团屏蔽状态 过滤
//			g.setName(groupName);
        //只查询集团，不查询医院
        g.setType("group");
        List<Group> groupList = groupDao.getGroupList(g);

        if (!CollectionUtils.isEmpty(groupList)) {
            // 提取集团Id列表
            groupIdList = Lists.transform(groupList, new Function<Group, String>() {
                @Override
                public String apply(Group input) {
                    return input.getId();
                }
            });
            groupIdMap = new HashMap<>();
            for (Group group : groupList) {
                groupIdMap.put(group.getId(), group);
            }
        } else {
            pageVo.setTotal(0L);
            pageVo.setPageData(new ArrayList<GroupApplyPageVo>());
            return pageVo;
        }

//		}

        long count = groupDao.getApplyListCount(status, groupName, doctorIds, groupIdList);
        List<GroupApply> gas = groupDao.getApplyList(status, groupName, doctorIds, groupIdList, pageSize, pageIndex);

        List<GroupApplyPageVo> datas = new ArrayList<GroupApplyPageVo>();
        if (gas != null && gas.size() > 0) {
            for (GroupApply ga : gas) {
                GroupApplyPageVo vo = new GroupApplyPageVo();
                vo.setApplyDate(ga.getApplyDate());
                vo.setAuditDate(ga.getAuditDate());
                vo.setGroupApplyId(ga.getId());
                vo.setGroupName(ga.getName());
                vo.setGroupId(ga.getGroupId());
                vo.setStatus(ga.getStatus());
                if (groupIdMap != null && !groupIdMap.isEmpty()) {
                    Group group = groupIdMap.get(ga.getGroupId());
                    vo.setGroupActive(group != null ? group.getActive() : null);
                    vo.setSkip(group != null ? group.getSkip() : null);
                    vo.setLogoUrl(group.getLogoUrl());
                }
                // add by tanyf 2016-06-02
                long memberNum = gdocService.findDoctorsCountByGroupId(ga.getGroupId());// 集团下的成员数   TODO 循环查询有待优化
                vo.setMemberNum(memberNum);
                if (StringUtil.isEmpty(vo.getLogoUrl()) && StringUtil.isNotBlank(ga.getLogoUrl())) {
                    vo.setLogoUrl(PropertiesUtil.addUrlPrefix(ga.getLogoUrl()));
                }

                User user = null;

                if (StringUtils.equals("P", status)) {
                    //审核通过的获取集团管理者的逻辑
                    //集团管理者显示当前的集团拥有者，而不是最开始集团的创建人
                    GroupUser groupUser = groupUserDao.findGroupRootAdmin(ga.getGroupId());
                    if (groupUser != null) {
                        user = userManager.getUser(groupUser.getDoctorId());
                    } else {
                        user = userManager.getUser(ga.getApplyUserId());
                    }
                } else {
                    user = userManager.getUser(ga.getApplyUserId());
                }
//				user = userManager.getUser(ga.getApplyUserId());
                if (user != null) {
                    vo.setTelephone(user.getTelephone());
                    Doctor doctor = user.getDoctor();
                    if (doctor != null) {
                        vo.setApplyDoctorName(user.getName());//申请医生姓名
                        vo.setHospitalName(doctor.getHospital());
                        vo.setTitle(doctor.getTitle());
                        HospitalPO po = baseDataDao.getHospitalDetail(doctor.getHospitalId());
                        if (po != null) {
                            vo.setLevel(po.getLevel());
                        }
                    }
                    vo.setAdminName(user.getName());
                }

                datas.add(vo);
            }
        }
        pageVo.setTotal(count);
        pageVo.setPageData(datas);
        pageVo.setPageIndex(pageIndex);
        pageVo.setPageSize(pageSize);
        return pageVo;
    }

    @Override
    public GroupApplyDetailPageVo getApplyDetail(String groupApplyId) {
        GroupApplyDetailPageVo vo = new GroupApplyDetailPageVo();
        GroupApply ga = groupDao.getGroupApplyById(groupApplyId);
        if (ga != null) {
            vo.setGroupName(ga.getName());
            User user = null;
            if (StringUtils.equals(ga.getStatus(), "P")) {
                GroupUser groupUser = groupUserDao.findGroupRootAdmin(ga.getGroupId());
                if (groupUser != null) {
                    user = userManager.getUser(groupUser.getDoctorId());
                } else {
                    user = userManager.getUser(ga.getApplyUserId());
                }
            } else {
                int doctorId = ga.getApplyUserId();
                user = userManager.getUser(doctorId);
            }
            vo.setDoctorName(user.getName());
            vo.setDoctorId(user.getUserId());
            vo.setTelephone(user.getTelephone());
            vo.setUserStatus(user.getStatus());
            Doctor doctor = user.getDoctor();

            if (doctor != null) {
                vo.setHospitalName(doctor.getHospital());
                vo.setDepartments(doctor.getDepartments());
                vo.setTitle(doctor.getTitle());
                Check check = doctor.getCheck();
                if (check != null) {
                    vo.setLicenseNum(check.getLicenseNum());
                    vo.setLicenseExpire(check.getLicenseExpire());
                }
                HospitalPO po = baseDataDao.getHospitalDetail(doctor.getHospitalId());
                if (po != null) {
                    vo.setLevel(po.getLevel());
                }
            }

            Integer auditUserId = ga.getAuditUserId();
            vo.setAuditMsg(ga.getAuditMsg());
            vo.setApplyStatus(ga.getStatus());
            vo.setAuditDate(ga.getAuditDate());
            if (auditUserId != null) {
                User admin = userManager.getUser(auditUserId);
                if (admin != null) {
                    vo.setAdminName(admin.getName());
                }
            }
            //ga.getStatus();
            // add by tanyf 20160602
            String groupId = ga.getGroupId();
            if (!StringUtil.isEmpty(groupId)) {
                Group group = groupDao.getById(groupId);
                vo.setGroupActive(group.getActive());//集团激活状态
                vo.setGroupId(groupId);//集团ID
                vo.setSkip(group.getSkip());// 集团屏蔽状态 add by tanyf 20160604
                // add by tanyf 2016-06-02
                long memberNum = gdocService.findDoctorsCountByGroupId(ga.getGroupId());// 集团下的成员数
                vo.setMemberNum(memberNum);// 集团下成员人数
                vo.setLogoUrl(group.getLogoUrl());
            }
            if (StringUtil.isNotEmpty(groupId)) {
                String QRQrl = qrCodeService.generateQr(groupId, "group");
                if (StringUtil.isNotEmpty(QRQrl)) {
                    vo.setQRUrl(QRQrl);
                }
            }
        }
        return vo;
    }

    @Override
    public void applyTransfer(GroupUserApply groupUserApply) throws HttpApiException {
        String groupId = groupUserApply.getGroupId();
        Integer inviteUserId = groupUserApply.getInviteUserId();
        Integer confirmUserId = groupUserApply.getConfirmUserId();
        if (StringUtils.isBlank(groupId) ||
                inviteUserId == null ||
                confirmUserId == null) {
            throw new ServiceException("参数为空");
        }

        groupTransferValid(groupId, inviteUserId, confirmUserId);

        groupDao.updateGroupUserApplyToExpire(groupId);

        groupUserApply.setInviteDate(System.currentTimeMillis());
        groupUserApply.setStatus(GroupEnum.GroupTransfer.tobeConfirm.getIndex());
        groupDao.insertGroupUserApply(groupUserApply);

        //尊敬的小T医生，张强医生申请将集团转让给您，请你点击处理
        User transferUser = userManager.getUser(inviteUserId);
        User receiveUser = userManager.getUser(confirmUserId);
        String transferUserName = null;
        if (transferUser != null) {
            transferUserName = transferUser.getName();
        }
        if (receiveUser != null) {
            List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
            ImgTextMsg textMsg = new ImgTextMsg();
            textMsg.setStyle(7);
            textMsg.setTitle("医生圈子转让通知");
            textMsg.setTime(System.currentTimeMillis());
            textMsg.setContent("尊敬的" + receiveUser.getName() + "医生，" + transferUserName + "医生申请将圈子转让给您，请你点击处理");
            Map<String, Object> param = new HashMap<String, Object>();
            textMsg.setFooter("立即查看");
//			param.put("bizType", 24);
//			param.put("bizId",groupId);

            String url = PropertiesUtil.getContextProperty("application.rootUrl")
                + PropertiesUtil.getContextProperty("group.web.prefix")
                + "/attachments/group_transfer/group_transfer_confirm.html?groupUserApplyId=" + groupUserApply.getId() + "&access_token={{token}}";

            logger.info("圈子通知转让跳转url:  {}",url);
            textMsg.setUrl(url);
            textMsg.setParam(param);
            mpt.add(textMsg);
            businessMsgServiceImpl.sendTextMsg(confirmUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
            businessMsgServiceImpl.sendTextMsg(confirmUserId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
            //mobSmsSdk.send(receiveUser.getTelephone(),"尊敬的"+receiveUser.getName()+"医生，"+transferUserName+"医生申请将集团转让给您，请你点击处理"+shortUrl);
            final String msg = baseDataService.toContent("1017", receiveUser.getName(), transferUserName, "http://xg.mediportal.com.cn/health/web/mobile/#/common/openApp/dog");
            mobSmsSdk.send(receiveUser.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
        }
    }

    @Override
    public void confirmTransfer(String groupUserApplyId, String pageStatus) throws HttpApiException {
        GroupUserApply groupUserApply = groupDao.getGroupUserApplyById(groupUserApplyId);
        if (groupUserApply == null ||
                !GroupEnum.GroupTransfer.tobeConfirm.getIndex().equals(groupUserApply.getStatus())) {
            throw new ServiceException("没有医生圈子转让记录");
        }
        String groupId = groupUserApply.getGroupId();
        Integer inviteUserId = groupUserApply.getInviteUserId();
        Integer confirmUserId = groupUserApply.getConfirmUserId();
        /**
         * 1、验证转让者是该集团的超级管理员
         * 2、确定当前集团已经是激活状态
         * 3、确定接受者不是其他集团的超级管理员
         * 4、确定接受者没有正在申请的集团
         * 5、修改转让者为普通管理员
         * 6、添加被转让者为组成员、组超级管理员
         * 7、修改申请转让记录状态
         * 8、短信、通知给转让者
         */

        groupTransferValid(groupId, inviteUserId, confirmUserId);
        long time = System.currentTimeMillis();
        String groupName = groupDao.getById(groupId).getName();
        if (GroupEnum.GroupTransfer.confirmPass.getIndex().equals(pageStatus)) {
            GroupUser transferGu = new GroupUser();
            transferGu.setDoctorId(inviteUserId);
            transferGu.setObjectId(groupId);
            transferGu.setUpdatorDate(time);
            transferGu.setRootAdmin("admin");
            gdDao.updateGroupUser(transferGu);

            List<GroupDoctor> gds = gdDao.findGroupDoctor(confirmUserId, groupId, GroupEnum.GroupDoctorStatus.正在使用.getIndex());
            if (gds == null || gds.size() < 1) {
                //添加接受者到该集团
                GroupDoctor groupDoctor = new GroupDoctor();
                groupDoctor.setCreator(confirmUserId);
                groupDoctor.setCreatorDate(time);
                groupDoctor.setDoctorId(confirmUserId);
                groupDoctor.setDeptName(groupName);
                groupDoctor.setGroupId(groupId);
                groupDoctor.setOnLineState(GroupEnum.OnLineState.onLine.getIndex());
                groupDoctor.setStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
                groupDoctor.setReferenceId(confirmUserId);
                groupDoctor.setUpdator(confirmUserId);
                groupDoctor.setUpdatorDate(time);
                gdDao.save(groupDoctor);
            }

//			GroupUser groupUser = gdDao.findGroupUser(confirmUserId, groupId, GroupEnum.GroupUserStatus.正常使用.getIndex());
            GroupUser groupUser = companyUserService.getGroupUserByIdAndStatus(null, confirmUserId, groupId,
                    null, GroupUserStatus.正常使用.getIndex());
            if (groupUser == null) {
//				GroupUser gu = new GroupUser();
//				gu.setCreator(confirmUserId);
//				gu.setCreatorDate(time);
//				gu.setDoctorId(confirmUserId);
//				gu.setObjectId(groupId);
//				gu.setRootAdmin("root");
//				gu.setStatus(GroupEnum.GroupUserStatus.正常使用.getIndex());
//				gu.setType(2);
//				gu.setUpdator(confirmUserId);
//				gu.setUpdatorDate(time);
//				cuserDao.save(gu);
                //设置为超级管理员
                companyUserService.addRootGroupManage(groupId, confirmUserId, confirmUserId);

            } else {
                GroupUser receiveGu = new GroupUser();
                receiveGu.setDoctorId(confirmUserId);
                receiveGu.setObjectId(groupId);
                receiveGu.setUpdatorDate(time);
                receiveGu.setRootAdmin("root");
                gdDao.updateGroupUser(receiveGu);
            }
            groupUserApply.setConfirmDate(time);
            //TODO 通知消息
            User receiveUser = userManager.getUser(confirmUserId);
            String receiveUserName = "";
            if (receiveUser != null) {
                receiveUserName = receiveUser.getName();
            }
            User transferUser = userManager.getUser(inviteUserId);
            if (transferUser != null) {
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("医生圈子转让通知");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + transferUser.getName() + "医生，您转让的" + groupName + "集团已被" + receiveUserName + "医生接受");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(inviteUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                businessMsgServiceImpl.sendTextMsg(inviteUserId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
                //mobSmsSdk.send(transferUser.getTelephone(),"尊敬的"+transferUser.getName()+"医生，您转让的"+groupName+"集团已被"+receiveUserName+"医生接受");
                final String msg = baseDataService.toContent("1015", transferUser.getName(), groupName, receiveUserName);
                mobSmsSdk.send(transferUser.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
            }
        } else if (GroupEnum.GroupTransfer.notpassNoPass.getIndex().equals(pageStatus)) {
            //TODO 通知消息
            User receiveUser = userManager.getUser(confirmUserId);
            String receiveUserName = "";
            if (receiveUser != null) {
                receiveUserName = receiveUser.getName();
            }
            User transferUser = userManager.getUser(inviteUserId);
            if (transferUser != null) {
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("医生圈子转让通知");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + transferUser.getName() + "医生，您转让的" + groupName + "集团已被" + receiveUserName + "医生拒接");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(inviteUserId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                //mobSmsSdk.send(transferUser.getTelephone(),"尊敬的"+transferUser.getName()+"医生，您转让的"+groupName+"集团已被"+receiveUserName+"医生拒绝");
                final String msg = baseDataService.toContent("1016", transferUser.getName(), groupName, receiveUserName);
                mobSmsSdk.send(transferUser.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
            }
        } else {
            throw new ServiceException("状态错误");
        }
        groupUserApply.setStatus(pageStatus);
        groupDao.updateGroupUserApply(groupUserApply);


    }

    private void groupTransferValid(String groupId, Integer inviteUserId, Integer confirmUserId) {
        Group group = groupDao.getById(groupId);
        if (group == null) {
            throw new ServiceException("该集团不存在");
        }
        String active = group.getActive();
        if (!GroupEnum.GroupActive.active.getIndex().equals(active)) {
            throw new ServiceException("集团没有被激活");
        }
        GroupUser igu = gdDao.findGroupDoctorByGroupIdAndDoctorId(groupId, inviteUserId);
        if (igu == null || !"root".equals(igu.getRootAdmin())) {
            throw new ServiceException("转让者不是该集团超级管理员");
        }

        GroupUser cgu = gdDao.findByRootAndDoctorId(confirmUserId);
        if (cgu != null) {
            throw new ServiceException("您选择的医生已创建其他圈子");
        }

        GroupApply ga = groupDao.getGroupApplyByApplyUserId(confirmUserId);
        if (ga != null && GroupEnum.GroupApply.audit.getIndex().equals(ga.getStatus())) {
            throw new ServiceException("接受者已申请了其他圈子");
        }
    }

    @Override
    public Object getTransferInfo(String groupUserApplyId) {
        GroupUserApply gua = groupDao.getTransferInfo(groupUserApplyId);
        Map<String, Object> map = new HashMap<String, Object>();
        if (gua != null) {
            if (gua.getStatus().equals("E")) {
                map.put("status", gua.getStatus());
                return map;
            }
            map.put("id", gua.getId());
            map.put("groupId", gua.getGroupId());
            map.put("inviteUserId", gua.getInviteUserId());
            map.put("confirmUserId", gua.getConfirmUserId());
            map.put("inviteDate", gua.getInviteDate());
            map.put("confirmUserId", gua.getConfirmDate());
            map.put("status", gua.getStatus());
            User user = userManager.getUser(gua.getInviteUserId());
            if (user != null) {
                map.put("inviteUserName", user.getName());
            }
            Group g = groupDao.getById(gua.getGroupId());
            if (g != null) {
                map.put("groupName", g.getName());
            }
        }
        return map;
    }

    @Override
    public void updateGroupApplyImageUrl(GroupApply groupApply) {
        groupDao.updateGroupApplyImageUrl(groupApply);
    }

    @Override
    public void updateGroupProfit(Group group) {
        if (group == null ||
                group.getConfig() == null ||
                StringUtils.isBlank(group.getId())) {
            throw new ServiceException("参数错误");
        }
        groupDao.updateGroupProfit(group);
    }

    @Override
    public boolean hasCreateRole(Integer doctorId) {
        User u = userManager.getUser(doctorId);
        if (u == null || u.getStatus() == null || u.getStatus().intValue() != UserEnum.UserStatus.normal.getIndex()) {
            return false;
        }
        GroupUser cgu = gdDao.findByRootAndDoctorId(doctorId);
        return cgu == null;
    }


    @Override
    public Object getGroupStatusInfo(String groupId) {
        GroupApply ga = getLastGroupApply(groupId);
        Group g = groupDao.getById(groupId);
        Map<String, String> data = new HashMap<String, String>();
        if (ga != null) {
            String applyStatus = ga.getStatus();
            data.put("applyStatus", applyStatus);
        }
        if (g != null) {
            String active = g.getActive();
            data.put("active", active);
        }
        return data;
    }

    /**
     * 当前时间是否在集团可值班时间
     *
     * @return
     */
    public boolean isWithinDutyTime(String groupId) {
        Group group = groupDao.getById(groupId);
        if (null == group) {
            throw new ServiceException("Group不存在. groupId=" + groupId);
        }

        String dutyStartTime = GroupUtil.GROUP_DUTY_START_TIME;
        String dutyEndTime = GroupUtil.GROUP_DUTY_END_TIME;
        if (GroupUtil.PLATFORM_ID.equals(group.getId()))
            return true;
        if (group.getConfig() != null) {
            if (StringUtil.isNoneBlank(group.getConfig().getDutyStartTime())) {
                dutyStartTime = group.getConfig().getDutyStartTime();
            }
            if (StringUtil.isNoneBlank(group.getConfig().getDutyEndTime())) {
                dutyEndTime = group.getConfig().getDutyEndTime();
            }
        }
        return GroupUtil.isCanOnDuty(dutyStartTime, dutyEndTime);
    }


    @Override
    public PageVO findGroupByKeyword(String keyword, String applyStatus, int pageIndex, int pageSize) {
        return groupDao.getGroupByKeyword(keyword, applyStatus, pageIndex, pageSize);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.service.IGroupService#getGroupNameByGroupId(java.lang.String)
     */
    @Override
    public String getGroupNameByGroupId(String id) {
        if (StringUtil.isEmpty(id)) {
            throw new ServiceException("集团id不能为空");
        }
        Group group = groupDao.getById(id, null);
        if (group == null) {
            return null;
        } else {
            return group.getName();
        }
    }

    /**
     * 激活集团
     *
     * @param groupId
     * @return
     * @author tan.yf
     * @date 2016年6月2日
     */
    @Override
    public void activeGroup(String groupId) throws HttpApiException {

        Group group = groupDao.getById(groupId);
        if (group != null && !group.getActive().equals(GroupEnum.GroupActive.active.getIndex())) {
            groupDao.activeGroup(groupId);// 激活集团

            //创建集团会话组
            if (StringUtils.isBlank(group.getGid())) {
                createGroupIm(group);
            }

            /**
             * 发送激活通知
             */
            GroupUser gu = gdDao.findGroupRootAdmin(groupId);
            if (gu != null) {
                int doctorId = gu.getDoctorId();
                User user = userManager.getUser(doctorId);
                List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
                ImgTextMsg textMsg = new ImgTextMsg();
                textMsg.setStyle(7);
                textMsg.setTitle("医生圈子已激活");
                textMsg.setTime(System.currentTimeMillis());
                textMsg.setContent("尊敬的" + user.getName() + "医生，您的医生圈子已激活， 欢迎登录玄关健康平台使用更多医生圈子专属功能");
                Map<String, Object> param = new HashMap<String, Object>();
                textMsg.setParam(param);
                mpt.add(textMsg);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
                businessMsgServiceImpl.sendTextMsg(doctorId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
                final String msg = baseDataService.toContent("1021", user.getName());
                mobSmsSdk.send(user.getTelephone(), msg, BaseConstants.XG_YSQ_APP);
                //mobSmsSdk.send(user.getTelephone(),"尊敬的"+user.getName()+"医生，您的医生集团已激活， 欢迎登录玄关健康平台使用更多医生集团专属功能");
            } else {
                logger.error("集团id= " + groupId + "的集团 ，找不到超级管理员");
            }

            syncGroupDataToEs(group);
        }
    }


    @Override
    public String isOpenSelfGuideAndGetGroupId(Integer userId, int userType) {
        String groupId = groupDao.getAppointmentGroupId();
        if (StringUtils.isBlank(groupId)) {
            return null;
        }
        Group g = groupDao.getById(groupId);
        if (g.getConfig() == null || !g.getConfig().isOpenSelfGuide()) {
            return null;
        }
        if (UserType.doctor.getIndex() == userType) {
            List<GroupDoctor> gds = gdDao.findMainGroupByDoctorId(userId);
            if (gds != null && gds.size() > 0 && StringUtils.equals(groupId, gds.get(0).getGroupId())) {
                return groupId;
            }
        } else if (UserType.DocGuide.getIndex() == userType) {
            User u = userManager.getUser(userId);
            if (u.getDoctorGuider() != null && StringUtils.equals(groupId, u.getDoctorGuider().getGroupId())) {
                return groupId;
            }
        }
        return null;
    }

    /**
     * 屏蔽集团
     *
     * @param groupId 集团ID
     * @author tan.yf
     * @date 2016年6月6日
     */
    @Override
    public Group blockGroup(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            throw new ServiceException("集团id不能为空");
        }
        Group g = groupDao.getById(groupId);
        if (null == g) {
            throw new ServiceException("集团不存在");
        }
        if (GroupEnum.GroupSkipStatus.skip.getIndex().equals(g.getSkip())) {// 集团已屏蔽
            return g;
        }
        Group group = new Group();
        group.setId(groupId);
        group.setSkip(GroupEnum.GroupSkipStatus.skip.getIndex());
        group.setUpdator(ReqUtil.instance.getUserId());
        group.setUpdatorDate(System.currentTimeMillis());
        Group returnValue = groupDao.update(group);
        /** 发送指令 消息 */
        businessMsgServiceImpl.changeSkipGroupNotify(groupId, GroupEnum.GroupSkipStatus.skip.getIndex());
        // 集团下的医生
        List<GroupDoctorVO> groupDoctorList = gdDao.findGroupDoctorListByGroupId(returnValue.getId());
        if (!CollectionUtils.isEmpty(groupDoctorList)) {
            /** 批量更新  集团下所有医生的主集团  设置为false */
            GroupDoctor groupDoctor = new GroupDoctor();
            groupDoctor.setGroupId(returnValue.getId());
            groupDoctor.setMain(Boolean.FALSE);
            gdocService.updateGroupDoctor(groupDoctor);
            for (GroupDoctorVO vo : groupDoctorList) {
                gdocService.initMainGroup(vo.getDoctorId());
            }
        }
        // 删除索引
        //将全文索引里面该集团的信息删除
        EsGroup esGroup = new EsGroup(returnValue.getId());
        ElasticSearchFactory.getInstance().deleteDocument(esGroup);

        //操作记录
        OperationRecord record = new OperationRecord();
        record.setContent("集团被屏蔽");
        record.setCreateTime(System.currentTimeMillis());
        record.setCreator(ReqUtil.instance.getUserId());
        record.setObjectId(groupId);
        recordDao.save(record);

        return returnValue;
    }

    /**
     * 取消屏蔽集团
     *
     * @param groupId 集团ID
     * @author tan.yf
     * @date 2016年6月6日
     */
    @Override
    public Group unBlockGroup(String groupId) {
        if (StringUtils.isBlank(groupId)) {
            throw new ServiceException("集团id不能为空");
        }
        Group g = groupDao.getById(groupId);
        if (null == g) {
            throw new ServiceException("集团不存在");
        }
        if (GroupEnum.GroupSkipStatus.normal.getIndex().equals(g.getSkip())) {// 集团已取消屏蔽
            return g;
        }
        Group group = new Group();
        group.setId(groupId);
        group.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());
        group.setUpdator(ReqUtil.instance.getUserId());
        group.setUpdatorDate(System.currentTimeMillis());
        Group returnValue = groupDao.update(group);
        /** 发送指令 消息 */
        businessMsgServiceImpl.changeSkipGroupNotify(groupId, GroupEnum.GroupSkipStatus.normal.getIndex());
        // 集团下的医生
        List<GroupDoctorVO> groupDoctorList = gdDao.findGroupDoctorListByGroupId(returnValue.getId());
        if (!CollectionUtils.isEmpty(groupDoctorList)) {
            for (GroupDoctorVO vo : groupDoctorList) {
                gdocService.initMainGroup(vo.getDoctorId());
            }
        }

        //操作记录
        OperationRecord record = new OperationRecord();
        record.setContent("集团取消屏蔽");
        record.setCreateTime(System.currentTimeMillis());
        record.setCreator(ReqUtil.instance.getUserId());
        record.setObjectId(groupId);
        recordDao.save(record);

        //一切操作完成之后将数据推向ES服务器
        this.syncGroupDataToEs(returnValue);
        return returnValue;
    }

    /**
     * 判断集团（审核通过且激活且未屏蔽
     *
     * @param groupId
     * @return true
     * @author tan.yf
     * @date 2016年6月15日
     */
    public boolean isNormalGroup(String groupId) {
        Group group = groupDao.getById(groupId);
        if (group != null && ((GroupEnum.GroupType.group.getIndex().equals(group.getType())
                && GroupEnum.GroupActive.active.getIndex().equals(group.getActive())
//				&& GroupEnum.GroupApply.pass.getIndex().equals(group.getApplyStatus()) 集团已经激活了是否还有必要再判断“集团申请状态”为已通过?
                && GroupEnum.GroupSkipStatus.normal.getIndex().equals(group.getSkip())))
                || (GroupEnum.GroupType.hospital.getIndex().equals(group.getType()))) { // 医院 不需判断 激活 审核 屏蔽
            return true;
        } else {
            return false;
        }
    }

    /**
     * 集团审核
     *
     * @param groupApplyId
     * @return
     * @author tan.yf
     * @date 2016年6月2日
     */
    @Override
    public GroupApply getGroupApplyById(String groupApplyId) {
        return groupDao.getGroupApplyById(groupApplyId);
    }


    @Override
    public List<RegionVo> getGroupRegion(String groupId) {

        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团ID为空");
        }

        //先查找该集团下面的医生
        List<GroupDoctor> groupDoctors = groupDoctroDao.findDoctorsByGroupId(groupId);
        Set<RegionVo> provinces = Sets.newHashSet();
        Set<RegionVo> cities = Sets.newHashSet();
        Set<RegionVo> countries = Sets.newHashSet();
        if (groupDoctors != null && groupDoctors.size() > 0) {
            List<Integer> doctorIds = Lists.newArrayList();
            for (GroupDoctor groupDoctor : groupDoctors) {
                if (null != groupDoctor.getDoctorId()) {
                    doctorIds.add(groupDoctor.getDoctorId());
                }
            }
            //查找医生对应的用户
            List<User> users = userManager.getDoctorsByIds(doctorIds);

            if (users != null && users.size() > 0) {
                for (User user : users) {
                    Doctor doctor = user.getDoctor();
                    if (null != doctor) {
                        Integer provinceId = doctor.getProvinceId();
                        String province = doctor.getProvince();
                        Integer cityId = doctor.getCityId();
                        String city = doctor.getCity();
                        Integer countryId = doctor.getCountryId();
                        String country = doctor.getCountry();

                        //获取省份信息
                        if (provinceId != null && StringUtils.isNotEmpty(province)) {
                            RegionVo provinceRegion = new RegionVo();
                            provinceRegion.setId(String.valueOf(provinceId));
                            provinceRegion.setName(province);
                            provinceRegion.setParentId("0");
                            provinces.add(provinceRegion);

                            //获取市信息
                            if (cityId != null && StringUtils.isNotEmpty(city)) {
                                RegionVo cityRegion = new RegionVo();
                                cityRegion.setId(String.valueOf(cityId));
                                cityRegion.setName(city);
                                cityRegion.setParentId(String.valueOf(provinceId));
                                cities.add(cityRegion);

                                //获取地区信息
                                if (countryId != null && StringUtils.isNotEmpty(country)) {
                                    RegionVo countryRegion = new RegionVo();
                                    countryRegion.setId(String.valueOf(countryId));
                                    countryRegion.setName(country);
                                    countryRegion.setParentId(String.valueOf(cityId));
                                    countries.add(countryRegion);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (provinces != null && provinces.size() > 0) {
            for (RegionVo province : provinces) {
                String provinceId = province.getId();
                List<RegionVo> subCityList = Lists.newArrayList();
                for (RegionVo city : cities) {
                    String cityId = city.getId();
                    List<RegionVo> subCountryList = Lists.newArrayList();
                    for (RegionVo country : countries) {
                        if (StringUtils.equals(cityId, country.getParentId())) {
                            subCountryList.add(country);
                        }
                    }
                    city.setSubList(subCountryList);
                    if (StringUtils.equals(provinceId, city.getParentId())) {
                        subCityList.add(city);
                    }
                }
                province.setSubList(subCityList);
            }
        }
        List<RegionVo> result = new ArrayList<>(provinces);
        return result;
    }

    @Override
    public List<DepartmentVO> getDeptsOfDoctors(String groupId) {

        List<DepartmentVO> departmentVOs = baseDataDao.getAllDepartments();
        //将departmentVOs转化为Map
        Map<String, DepartmentVO> departmentMap = Maps.newHashMap();
        if (departmentVOs != null && departmentVOs.size() > 0) {
            for (DepartmentVO departmentVO : departmentVOs) {
                departmentMap.put(departmentVO.getId(), departmentVO);
            }
        }

        //获取某集团的医生
        List<GroupDoctorVO> groupDoctorVOs = groupDao.getGroupDoctorListByGroupId(groupId);
        List<Integer> doctorIds = Lists.newArrayList();
        if (groupDoctorVOs != null && groupDoctorVOs.size() > 0) {
            for (GroupDoctorVO groupDoctorVO : groupDoctorVOs) {
                doctorIds.add(groupDoctorVO.getDoctorId());
            }
        }

        //获取该集团所有的子节点及其所有父节点
        Set<DepartmentVO> groupDepartmentVos = Sets.newHashSet();
        List<User> users = userManager.getDoctorsByIds(doctorIds);
        if (users != null && users.size() > 0) {
            for (User user : users) {
                Doctor doctor = user.getDoctor();
                if (null != doctor) {
                    String deptId = doctor.getDeptId();
                    if (StringUtils.isNotEmpty(deptId)) {
                        DepartmentVO departmentVO = departmentMap.get(deptId);
                        if (departmentVO != null) {
                            getGroupAllDepartments(departmentMap, groupDepartmentVos, departmentVO);
                        }
                    }
                }
            }
        }

        //构造树形结构
        List<DepartmentVO> result = Lists.newArrayList();
        if (groupDepartmentVos != null && groupDepartmentVos.size() > 0) {
            for (DepartmentVO departmentVO : groupDepartmentVos) {
                if (StringUtils.equals(departmentVO.getParentId(), "A")) {
                    setGroupAllDepartments(groupDepartmentVos, departmentVO);
                    result.add(departmentVO);
                }
            }

        }
        return result;
    }

    public void getGroupAllDepartments(Map<String, DepartmentVO> map, Set<DepartmentVO> set, DepartmentVO departmentVO) {
        set.add(departmentVO);
        if (departmentVO != null && departmentVO.getIsLeaf() != null
                && departmentVO.getIsLeaf().equals(1) && !StringUtils.equals(departmentVO.getParentId(), "A")) {
            DepartmentVO parent = map.get(departmentVO.getParentId());
            if (parent != null) {
                getGroupAllDepartments(map, set, parent);
            }
        }
    }

    public void setGroupAllDepartments(Set<DepartmentVO> set, DepartmentVO departmentVO) {
        String id = departmentVO.getId();
        List<DepartmentVO> subList = Lists.newArrayList();
        for (DepartmentVO departmentVO2 : set) {
            if (StringUtils.equals(id, departmentVO2.getParentId())) {
                setGroupAllDepartments(set, departmentVO2);
                subList.add(departmentVO2);
            }
        }
        departmentVO.setSubList(subList);
    }


    @Override
    public List<Integer> getDoctorIdsByGroupId(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团ID为空");
        }

        List<GroupDoctor> groupDoctors = groupDoctroDao.findDoctorsByGroupId(groupId);
        List<Integer> doctorIds = Lists.newArrayList();

        if (groupDoctors != null && groupDoctors.size() > 0) {
            for (GroupDoctor groupDoctor : groupDoctors) {
                if (null != groupDoctor.getDoctorId()) {
                    doctorIds.add(groupDoctor.getDoctorId());
                }
            }
        }
        return doctorIds;
    }


    @Override
    public PageVO searchGroupByName(String groupName, Integer pageIndex, Integer pageSize) {

        List<Group> groups = groupDao.searchGroupsByName(groupName, null, null);

        if (groups == null || groups.size() <= 0) {
            return new PageVO(null, 0L, pageIndex, pageSize);
        }

        List<RecommendGroupVO> result = Lists.newArrayList();

        List<String> idList = Lists.newArrayList();
        List<RecommendGroupVO> allReGroup = groupDao.getAllRecommendGroups();
        if (allReGroup != null && allReGroup.size() > 0) {
            for (RecommendGroupVO vo : allReGroup) {
                idList.add(vo.getId());
            }
        }

        if (groups != null && groups.size() > 0) {
            for (Group group : groups) {
                RecommendGroupVO vo = new RecommendGroupVO();

                //查找管理员
                GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
                if (groupUser != null) {
                    User user = userManager.getUser(groupUser.getDoctorId());
                    if (user != null) {
                        vo.setManager(user.getName());
                        vo.setManagerTitle(user.getDoctor().getTitle());
                    }
                }
                vo.setId(group.getId());
                vo.setName(group.getName());

                //集团成员数
                long memberNum = gdocService.findDoctorsCountByGroupId(group.getId());
                if (memberNum == 0L) {
                    continue;
                }
                vo.setMemberNumber(memberNum);
                if (StringUtil.isNotBlank(group.getLogoUrl())) {
                    vo.setLogoUrl((PropertiesUtil.addUrlPrefix(group.getLogoUrl())));
                }

                //判断是否已在推荐列表里
                if (idList.contains(vo.getId())) {
                    vo.setIsSelect(1);
                } else {
                    vo.setIsSelect(0);
                }
                result.add(vo);
            }
        }
        long count = Long.valueOf(result.size());
        List<RecommendGroupVO> list = Lists.newArrayList();
        for (int i = (pageIndex * pageSize); i < ((pageIndex * pageSize + pageSize) > count ? count : (pageIndex * pageSize + pageSize)); i++) {
            list.add(result.get(i));
        }

        return new PageVO(list, count, pageIndex, pageSize);
    }

    @Override
    public PageVO getRecommends(Integer pageIndex, Integer pageSize) {
        PageVO page = new PageVO();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setPageData(getGroupRecommendedList(pageIndex, pageSize));
        List<RecommendGroupVO> recommendGroupVOs = groupDao.getRecommenGroups();
        page.setTotal(CollectionUtils.isEmpty(recommendGroupVOs) ? 0L : Long.valueOf(recommendGroupVOs.size()));
        return page;
    }

    @Override
    public List<RecommendGroupVO> getGroupRecommendedList(Integer pageIndex, Integer pageSize) {

        //查找用户关注的疾病列表
        List<UserDiseaseLaber> labers = Lists.newArrayList();
        labers = laberDao.findByUserId(ReqUtil.instance.getUserId());
        Map<String, UserDiseaseLaber> laberMap = Maps.newHashMap();
        for (UserDiseaseLaber laber : labers) {
            laberMap.put(laber.getDiseaseId(), laber);
            laberMap.put(laber.getParentId(), laber);
        }

        List<RecommendGroupVO> result = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        Map<String, RecommendGroupVO> voMap = Maps.newHashMap();

        List<RecommendGroupVO> recommendGroupVOs = groupDao.getRecommenGroups();

        if (recommendGroupVOs != null && recommendGroupVOs.size() > 0) {
            for (RecommendGroupVO vo : recommendGroupVOs) {
                voMap.put(vo.getId(), vo);
                groupIds.add(vo.getId());
            }
        } else {
            return result;
        }

        //查找所有的集团详情
        List<Group> groups = groupDao.getGroupsListByIds(groupIds);
        for (Group group : groups) {
            RecommendGroupVO vo = new RecommendGroupVO();

            List<String> diseases = group.getDiseaselist();
            if (!CollectionUtils.isEmpty(diseases)) {
                for (String disease : diseases) {
                    UserDiseaseLaber laber = laberMap.get(disease);
                    //集团擅长匹配用户疾病列表，大幅增加排序权重
                    if (null != laber && laber.getWeight() != null) {
                        if (null == vo.getWeight()) {
                            vo.setWeight(voMap.get(group.getId()).getWeight() + laber.getWeight() * 1000000);
                        } else {
                            vo.setWeight(vo.getWeight() + laber.getWeight() * 1000000);
                        }
                    }
                }
            }

            if (null == vo.getWeight()) {
                vo.setWeight(voMap.get(group.getId()).getWeight());
            }

            vo.setId(group.getId());
            vo.setName(group.getName());
            if (StringUtil.isNotBlank(group.getLogoUrl())) {
                vo.setLogoUrl((PropertiesUtil.addUrlPrefix(group.getLogoUrl())));
            }
            //获取管理者及名称
            GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
            if (groupUser != null) {
                User user = userManager.getUser(groupUser.getDoctorId());
                if (user != null) {
                    vo.setManager(user.getName());
                    vo.setManagerTitle(user.getDoctor().getTitle());
                }
            }
            //获取成员数
            //vo.setMemberNumber(Long.valueOf(groupDao.getGroupDoctorListByGroupId(group.getId()).size()));

            //设置集团就诊量
            vo.setGroupCureNum(Long.valueOf(groupSearchDao.getGroupCureNum(group.getId())));

            vo.setIntroduction((group.getIntroduction() != null) ? group.getIntroduction() : "");

            if (group.getDiseaselist() != null && group.getDiseaselist().size() > 0) {

                vo.setSkill(groupSearchDao.getDisease(group.getDiseaselist()));
            }

            if (StringUtils.isNotEmpty(group.getCertStatus())) {
                vo.setCertStatus(group.getCertStatus());
            }

            result.add(vo);
        }

        //查找专家数
        if (groupIds != null && groupIds.size() > 0) {
            Map<String, Integer> map = groupSearchDao.getGroupExperNum(groupIds, 1);
            if (result != null && result.size() > 0) {
                for (RecommendGroupVO recommendGroupVO : result) {
                    Integer expertNum = map.get(recommendGroupVO.getId());
                    recommendGroupVO.setMemberNumber(expertNum == null ? 0L : Long.valueOf(expertNum));
                }
            }
        }

        Collections.sort(result, new Comparator<RecommendGroupVO>() {
            public int compare(RecommendGroupVO vo0, RecommendGroupVO vo1) {
                return vo1.getWeight().compareTo(vo0.getWeight());
            }
        });


        if (result.size() < (pageIndex * pageSize)) {
            result = Lists.newArrayList();
        } else {
            if ((pageIndex + 1) * pageSize < result.size()) {
                result = result.subList((pageIndex * pageSize), (pageIndex + 1) * pageSize);
            } else {
                result = result.subList((pageIndex * pageSize), result.size());
            }
        }

        return result;
    }

    @Override
    public void setGroupToRecommended(String[] groupId) {
        if (groupId == null || groupId.length == 0) {
            throw new ServiceException("集团ID为空");
        }
        List<RecommendGroupVO> allGroup = groupDao.getAllRecommendGroups();

        //校验医生是否已被推荐
        List<String> ids = Lists.newArrayList();
        for (RecommendGroupVO vo : allGroup) {
            ids.add(vo.getId());
        }
        for (int i = 0; i < groupId.length; i++) {
            if (ids.contains(groupId[i])) {
                throw new ServiceException("请勿重复添加已被推荐的医生圈子。");
            }
        }

        for (int i = 0; i < groupId.length; i++) {
            RecommendGroupVO vo = new RecommendGroupVO();
            Group group = groupDao.getById(groupId[i], null);
            long count = 2;

            vo.setId(group.getId());
            vo.setName(group.getName());

            List<RecommendGroupVO> groups = groupDao.getAllRecommendGroups();
            if (!groups.isEmpty()) {
                long weight = groups.get(0).getWeight();
                count = (weight + 1);
            }
            vo.setWeight(count);

            groupDao.saveRecommendGroup(vo);
        }
    }

    @Override
    public void removeGroupRecommended(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团ID为空");
        }
        groupDao.removeGroupRecommended(groupId);
    }

    @Override
    public void upWeight(String groupId) {
        List<RecommendGroupVO> groups = groupDao.getAllRecommendGroups();
        for (int i = 0; i < groups.size(); i++) {
            Long temp = null;
            if (groupId.equals(groups.get(i).getId())) {
                if (i == 0) {
                    throw new ServiceException("当前已为第一位，无需上移。");
                }
                temp = groups.get(i).getWeight();
                groups.get(i).setWeight(groups.get(i - 1).getWeight());
                groups.get(i - 1).setWeight(temp);
                groupDao.updateGroupRecommended(groups.get(i));
                groupDao.updateGroupRecommended(groups.get(i - 1));
            }
        }
    }

    @Override
    public List<ExistRegionVo> getGroupExistRegion(String groupId) {
        if (StringUtils.isEmpty(groupId)) {
            throw new ServiceException("集团ID为空");
        }

        //先查找该集团下面的医生
        List<GroupDoctor> groupDoctors = groupDoctroDao.findDoctorsByGroupId(groupId);
        Set<ExistRegionVo> provinces = Sets.newHashSet();
        Set<ExistRegionVo> cities = Sets.newHashSet();
        if (groupDoctors != null && groupDoctors.size() > 0) {
            List<Integer> doctorIds = Lists.newArrayList();
            for (GroupDoctor groupDoctor : groupDoctors) {
                if (null != groupDoctor.getDoctorId()) {
                    doctorIds.add(groupDoctor.getDoctorId());
                }
            }
            //查找医生对应的用户
            List<User> users = userManager.getDoctorsByIds(doctorIds);

            if (users != null && users.size() > 0) {
                for (User user : users) {
                    Doctor doctor = user.getDoctor();
                    if (null != doctor) {
                        Integer provinceId = doctor.getProvinceId();
                        String province = doctor.getProvince();
                        Integer cityId = doctor.getCityId();
                        String city = doctor.getCity();
                        Integer countryId = doctor.getCountryId();
                        String country = doctor.getCountry();

                        //获取省份信息
                        if (provinceId != null && StringUtils.isNotEmpty(province)) {
                            ExistRegionVo provinceRegion = new ExistRegionVo();
                            provinceRegion.setCode(provinceId);
                            provinceRegion.setName(province);
                            provinceRegion.setParentId(0);
                            provinces.add(provinceRegion);

                            //直获取市信息  辖市去除第2级“市辖区”、“县”，返回第3级
                            if (directCityCodes.contains(provinceId) && countryId != null && StringUtils.isNotEmpty(country)) {
                                ExistRegionVo cityRegion = new ExistRegionVo();
                                cityRegion.setCode(countryId);
                                cityRegion.setName(country);
                                cityRegion.setParentId(provinceId);
                                cities.add(cityRegion);
                            } else {
                                if (cityId != null && StringUtils.isNotEmpty(city)) {
                                    ExistRegionVo cityRegion = new ExistRegionVo();
                                    cityRegion.setCode(cityId);
                                    cityRegion.setName(city);
                                    cityRegion.setParentId(provinceId);
                                    cities.add(cityRegion);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (provinces != null && provinces.size() > 0) {
            for (ExistRegionVo province : provinces) {
                Integer provinceId = province.getCode();
                List<ExistRegionVo> subCityList = Lists.newArrayList();
                for (ExistRegionVo city : cities) {
                    if (provinceId.equals(city.getParentId())) {
                        subCityList.add(city);
                    }
                }
                province.setChildren(subCityList);
            }
        }
        List<ExistRegionVo> result = new ArrayList<>(provinces);
        Collections.sort(result, (a, b) -> a.getCode().compareTo(b.getCode()));
        return result;
    }


    @Override
    public Map<String, Object> groupInfo(String groupId) {
        //1、查询group_doctor表，获取集团总人数
        long count = groupDoctroDao.findDoctorsCountByGroupId(groupId);
        //2、查询group_user表获取root用户
        List<GroupUser> roots = groupUserDao.findGroupRoots(groupId);
        List<Integer> rootIds = Lists.newArrayList();
        if (roots != null && roots.size() > 0) {
            roots.forEach((a) -> rootIds.add(a.getDoctorId()));
        }
        List<User> rootUsers = userRepository.findUsersWithOutStatusByIds(rootIds);
        List<Map<String, Object>> rootUsersList = Lists.newArrayList();
        if (rootUsers != null && rootUsers.size() > 0) {
            rootUsers.forEach((a) -> {
                Map<String, Object> user = Maps.newHashMap();
                user.put("id", a.getUserId());
                user.put("name", a.getName());
                user.put("headPicFileName", a.getHeadPicFileName());
                rootUsersList.add(user);
            });
        }

        //3、查询group_user表获取admin用户
        List<GroupUser> admins = groupUserDao.findGroupAdminWithOutRoot(groupId);
        List<Integer> adminIds = Lists.newArrayList();
        if (admins != null && admins.size() > 0) {
            admins.forEach((a) -> adminIds.add(a.getDoctorId()));
        }
        List<User> adminUsers = userRepository.findUsersWithOutStatusByIds(adminIds);
        List<Map<String, Object>> adminUsersList = Lists.newArrayList();
        if (adminUsers != null && adminUsers.size() > 0) {
            adminUsers.forEach((a) -> {
                Map<String, Object> user = Maps.newHashMap();
                user.put("id", a.getUserId());
                user.put("name", a.getName());
                user.put("headPicFileName", a.getHeadPicFileName());
                adminUsersList.add(user);
            });
        }

        Map<String, Object> result = Maps.newHashMap();
        result.put("count", count);
        result.put("roots", rootUsersList);
        result.put("admins", adminUsersList);
        return result;
    }


    @Override
    public List<Map<String, String>> searchBykeyword(Integer type, String keyword) {
        List<Map<String, String>> result = Lists.newArrayList();

        if (type == null) {
            throw new ServiceException("集团名称不能为空！");
        }
        DBObject query = new BasicDBObject();

        if (StringUtils.isNotEmpty(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            query.put("name", pattern);
        }

        DBCursor dbCursor = groupDao.searchByKeyword(type, keyword);
        if (dbCursor == null) {
            return result;
        }
        //查找医生
        if (1 == type) {
            while (dbCursor.hasNext()) {
                DBObject object = dbCursor.next();
                Map<String, String> map = Maps.newHashMap();
                map.put("id", MongodbUtil.getString(object, "_id"));
                map.put("name", MongodbUtil.getString(object, "name"));
                result.add(map);
            }
            //查找集团
        } else if (2 == type) {
            while (dbCursor.hasNext()) {
                DBObject object = dbCursor.next();
                Map<String, String> map = Maps.newHashMap();
                map.put("id", MongodbUtil.getInteger(object, "_id").toString());
                map.put("name", MongodbUtil.getString(object, "name"));
                result.add(map);
            }
        }
        return result;
    }


    @Override
    public List<String> getSkipGroupIds() {
        List<String> skipGroupIdList = this.groupDao.getSkipGroupIds();
        return skipGroupIdList;
    }

    @Override
    public void changAdmin(String groupId, Integer receiverId) throws HttpApiException {
        if (StringUtil.isEmpty(groupId) || null == receiverId) {
            throw new ServiceException("参数缺失");
        }

        /**
         * 1、确定当前集团已经是激活状态
         * 2、确定接受者不是其他集团的超级管理员
         * 3、确定接受者没有正在申请的集团
         * 4、修改转让者为普通管理员
         * 5、添加被转让者为组成员、组超级管理员
         * 6、修改申请转让记录状态
         * 7、短信、通知给接受者
         */
        //确认参数合法性
        changeAdminValid(groupId, receiverId);
        //查找集团原管理员
        GroupUser groupUser = groupDoctroDao.findGroupRootAdmin(groupId);

        groupDao.updateGroupUserApplyToExpire(groupId);
        GroupUserApply groupUserApply = new GroupUserApply();

        Integer rootId = groupUser.getDoctorId();

        groupUserApply.setInviteUserId(rootId);
        groupUserApply.setGroupId(groupId);
        groupUserApply.setConfirmUserId(receiverId);
        groupUserApply.setInviteDate(System.currentTimeMillis());
        groupUserApply.setStatus(GroupEnum.GroupTransfer.tobeConfirm.getIndex());
        groupDao.insertGroupUserApply(groupUserApply);


        String groupName = groupDao.getById(groupId).getName();
        Long time = System.currentTimeMillis();
        if (null != groupUser) {
            GroupUser transferGu = new GroupUser();
            transferGu.setDoctorId(rootId);
            transferGu.setObjectId(groupId);
            transferGu.setUpdatorDate(time);
            transferGu.setRootAdmin("admin");
            gdDao.updateGroupUser(transferGu);
        }

        List<GroupDoctor> gds = gdDao.findGroupDoctor(receiverId, groupId, GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        if (gds == null || gds.size() < 1) {
            //添加接受者到该集团
            GroupDoctor groupDoctor = new GroupDoctor();
            groupDoctor.setCreator(receiverId);
            groupDoctor.setCreatorDate(time);
            groupDoctor.setDoctorId(receiverId);
            groupDoctor.setDeptName(groupName);
            groupDoctor.setGroupId(groupId);
            groupDoctor.setOnLineState(GroupEnum.OnLineState.onLine.getIndex());
            groupDoctor.setStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
            groupDoctor.setReferenceId(receiverId);
            groupDoctor.setUpdator(receiverId);
            groupDoctor.setUpdatorDate(time);
            gdDao.save(groupDoctor);
        }

        GroupUser groupUser1 = companyUserService.getGroupUserByIdAndStatus(null, receiverId, groupId, null, GroupUserStatus.正常使用.getIndex());

        if (groupUser1 == null) {
            companyUserService.addRootGroupManage(groupId, receiverId, receiverId);
        } else {
            GroupUser receiveGu = new GroupUser();
            receiveGu.setDoctorId(receiverId);
            receiveGu.setObjectId(groupId);
            receiveGu.setUpdatorDate(time);
            receiveGu.setRootAdmin("root");
            gdDao.updateGroupUser(receiveGu);
        }

        groupUserApply.setConfirmDate(time);
        groupUserApply.setStatus("P");
        groupDao.updateGroupUserApply(groupUserApply);

        User receiveUser = userManager.getUser(receiverId);
        User transferUser = userManager.getUser(rootId);
        String receiveUserName = "空";
        String transferUserName = "空";
        if (receiveUser != null) {
            receiveUserName = receiveUser.getName();

            List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
            ImgTextMsg textMsg = new ImgTextMsg();
            textMsg.setStyle(7);
            textMsg.setTitle("医生圈子转让通知");
            textMsg.setTime(System.currentTimeMillis());
            textMsg.setContent("您已成为" + groupName + "的管理员，可登录集团后台管理集团（http://xg.mediportal.com.cn/health/web/");
            Map<String, Object> param = new HashMap<String, Object>();
            textMsg.setParam(param);
            mpt.add(textMsg);
            businessMsgServiceImpl.sendTextMsg(receiverId + "", SysGroupEnum.TODO_NOTIFY, mpt, null);
            businessMsgServiceImpl.sendTextMsg(receiverId + "", SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        }

        if (null != transferUser) {
            transferUserName = transferUser.getName();
        }

        //记录操作
        OperationRecord record = new OperationRecord();
        String content = "集团创建人：由【" + transferUserName + "】改为【" + receiveUserName + "】";

        record.setObjectId(groupId);
        record.setCreateTime(time);
        record.setCreator(ReqUtil.instance.getUserId());
        record.setContent(content);
        recordDao.save(record);
    }

    @Override
    public void initAllGroupIM() {
        // 找到所有的集团
        List<Group> groups = groupDao.findAll();
        logger.info("共{}个集团信息", groups.size());
        for (Group group : groups) {
            try {
                createGroupIm(group);
            } catch (Exception e) {
                continue;
            }
        }

    }

    @Override
    public Object getIMInfo(String gid) throws HttpApiException {
        Integer currentUserId = ReqUtil.instance.getUserId();
        GroupInfoRequestMessage requestMessage = new GroupInfoRequestMessage();
        requestMessage.setGid(gid);
        requestMessage.setUserId(String.valueOf(currentUserId));
        return imsgService.getGroupInfo(requestMessage);
    }

    @Override
    public void fixAllGroupIM() {
        // 找到所有的集团
        List<Group> groups = groupDao.findAll();
        logger.info("共{}个集团信息", groups.size());
        for (Group group : groups) {
            try {
                updateGroupIMLogo(group);
            } catch (Exception e) {
                logger.error("{}集团修复时发生异常：{}", group.getName(), e.getMessage());
                continue;
            }
        }
    }

    @Override
    public void updateGroupIMLogo(Group group) {
        logger.info("--->>>开始修改{}集团的会话组的图片", group.getName());

        if (Objects.nonNull(group) && StringUtils.isNotBlank(group.getGid()) && StringUtils.isNotBlank(group.getLogoUrl())) {
            String gid = group.getGid();

            GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
            String fromUserId = null;
            if (Objects.nonNull(groupUser)) {
                fromUserId = String.valueOf(groupUser.getDoctorId());
            } else {
                fromUserId = String.valueOf(group.getCreator());
            }

            UpdateGroupRequestMessage updateGroupParam = new UpdateGroupRequestMessage();
            updateGroupParam.setGid(gid);
            updateGroupParam.setAct(4);
            updateGroupParam.setFromUserId(fromUserId);
            updateGroupParam.setName(group.getLogoUrl());
            imsgService.updateGroup(updateGroupParam);
            logger.info("--->>>{}集团的会话组的图片更新成功", group.getName());
        } else {
            logger.info("--->>>{}集团的会话组的图片更新失败", group.getName());
        }
    }

    @Override
    public void updateGroupIMName(Group group) {
        logger.info("--->>>开始修改{}集团的会话组的名称", group.getName());

        if (Objects.nonNull(group) && StringUtils.isNotBlank(group.getGid()) && StringUtils.isNotBlank(group.getLogoUrl())) {
            String gid = group.getGid();

            GroupUser groupUser = groupUserDao.findGroupRootAdmin(group.getId());
            String fromUserId = null;
            if (Objects.nonNull(groupUser)) {
                fromUserId = String.valueOf(groupUser.getDoctorId());
            } else {
                fromUserId = String.valueOf(group.getCreator());
            }

            UpdateGroupRequestMessage updateGroupParam = new UpdateGroupRequestMessage();
            updateGroupParam.setGid(gid);
            updateGroupParam.setAct(3);
            updateGroupParam.setFromUserId(fromUserId);
            updateGroupParam.setName(group.getName());
            imsgService.updateGroup(updateGroupParam);
            logger.info("--->>>{}集团的会话组的名称更新成功", group.getName());
        } else {
            logger.info("--->>>{}集团的会话组的名称更新失败", group.getName());
        }
    }

    @Override
    public List<GroupDeptVO> findActiveDeptList() {
        List<Group> groupList = groupDao.findActiveDeptList();

        List<GroupDeptVO> voList = new ArrayList<>();
        Map<String , List<DeptVO>> map = Maps.newHashMap();
        for (Group group : groupList) {
            GroupDeptVO vo = new GroupDeptVO();
            vo.setId(group.getHospitalId());
            vo.setName(group.getHospitalName());
            voList.add(vo);

            if (map.containsKey(group.getHospitalId())){
                List<DeptVO> deptVOs = map.get(group.getHospitalId());
                DeptVO deptVO = new DeptVO();
                deptVO.setId(group.getId());
                deptVO.setName(group.getName());
                deptVOs.add(deptVO);
            } else {
                List<DeptVO> deptVOs = new ArrayList<>();
                DeptVO deptVO = new DeptVO();
                deptVO.setId(group.getId());
                deptVO.setName(group.getName());
                deptVOs.add(deptVO);

                map.put(group.getHospitalId(), deptVOs);
            }
        }

        for (GroupDeptVO groupDeptVO:voList) {
            groupDeptVO.setDeptList(map.get(groupDeptVO.getId()));
        }

        return voList;
    }

    @Override
    public List<DeptVO> findActiveDeptListByKeyword(String keyword) {
        if (StringUtils.isBlank(keyword)) {
            return null;
        }
        List<Group> groupList = groupDao.findActiveDeptListByKeyword(keyword);

        List<DeptVO> voList = new ArrayList<>();
        for (Group group : groupList) {
            DeptVO deptVO = new DeptVO();
            deptVO.setId(group.getId());
            deptVO.setName(group.getName());
            voList.add(deptVO);
        }

        return voList;
    }

    public void createGroupIm(Group group) throws HttpApiException {
        logger.info("--->>>开始处理{}集团的会话组", group.getName());
        String groupId = group.getId();
        String gid = group.getGid();
        // 查询该集团所有的状态为正常的成员
        List<GroupDoctor> groupDoctors = groupDoctroDao.findDoctorsByGroupId(groupId);
        GroupUser groupUser = groupUserDao.findGroupRootAdmin(groupId);
        String fromUserId = null;
        if (Objects.nonNull(groupUser)) {
            fromUserId = String.valueOf(groupUser.getDoctorId());
        } else {
            fromUserId = String.valueOf(group.getCreator());
        }
        if (StringUtils.isBlank(gid)) {
            Set<Integer> doctorIds = groupDoctors.stream()
                    .filter(groupDoctor -> Objects.nonNull(groupDoctor.getDoctorId()))
                    .map(GroupDoctor::getDoctorId).collect(Collectors.toSet());
            if (CollectionUtils.isEmpty(doctorIds)) {
                doctorIds = Sets.newHashSet();
            }
            doctorIds.add(groupUser.getDoctorId());

            String toUserId = StringUtils.join(doctorIds, "|");

            try {
                CreateGroupRequestMessage createGroupParam = new CreateGroupRequestMessage();
                createGroupParam.setFromUserId(fromUserId);
                createGroupParam.setToUserId(toUserId);
                createGroupParam.setType(GroupTypeEnum.MULTI.getValue());
                createGroupParam.setGtype(RelationTypeEnum.DOCTOR.getValue());
                createGroupParam.setGname(group.getName());
                createGroupParam.setCreateNew(true);
                createGroupParam.setGpic(group.getLogoUrl());

                GroupManagerVO groupManagerVO = new GroupManagerVO();
                groupManagerVO.setCanEdit(false);
                groupManagerVO.setDisplayUser(false);
                createGroupParam.setManager(groupManagerVO);

                Map<String, Object> param = Maps.newHashMap();
                param.put("type", "group");

                createGroupParam.setParam(param);
                // 建立会话组
                GroupInfo groupInfo = (GroupInfo) imsgService.createGroup(createGroupParam);

                // 更新会话组id
                if (Objects.nonNull(groupInfo)) {
                    groupDao.updateGroupGid(groupId, groupInfo.getGid());
                    logger.info("===>>>{}集团的会话组处理完毕", group.getName());
                    StringBuffer content = new StringBuffer();
                    content.append(group.getName()).append("创建成功");
                    businessServiceMsg.sendNotifyMsgToAll(groupInfo.getGid(), content.toString());
                }
            } catch (Exception e) {
                logger.error("--->>>{}集团的会话组创建失败，原因：{}", group.getName(), e.getMessage());
            }
        } else {
            //若存在，则发送一条消息
            StringBuffer content = new StringBuffer();
            content.append(group.getName()).append("创建成功");
            businessServiceMsg.sendNotifyMsgToAll(gid, content.toString());
            logger.info("--->>>{}集团的会话组已存在，发送消息成功", group.getName());
        }
    }

    private void changeAdminValid(String groupId, Integer userId) {
        Group group = groupDao.getById(groupId);
        if (group == null) {
            throw new ServiceException("该集团不存在");
        }
        String active = group.getActive();
        if (!GroupEnum.GroupActive.active.getIndex().equals(active)) {
            throw new ServiceException("集团没有被激活");
        }

        GroupUser cgu = gdDao.findByRootAndDoctorId(userId);
        if (cgu != null) {
            throw new ServiceException("您选择的医生已创建其他圈子");
        }

        GroupApply ga = groupDao.getGroupApplyByApplyUserId(userId);
        if (ga != null && GroupEnum.GroupApply.audit.getIndex().equals(ga.getStatus())) {
            throw new ServiceException("接受者已申请了其他圈子");
        }
    }


    private void saveModifyRecord(Group group) {
        Group po = groupDao.getById(group.getId());
        List<String> contents = new ArrayList<>();

        if (StringUtil.isNotEmpty(group.getLogoUrl()) && !group.getLogoUrl().equals(po.getLogoUrl())) {
            contents.add("集团logo:由【" + covertLogoUrl(po.getLogoUrl()) + "】</a>改为【" + covertLogoUrl(group.getLogoUrl()) + "】</a>");
        }

        if (StringUtil.isNotEmpty(group.getName()) && !group.getName().equals(po.getName())) {
            contents.add("集团名称：由【" + po.getName() + "】改为【" + group.getName() + "】");
        }

        if (StringUtil.isNotEmpty(group.getIntroduction()) && !group.getIntroduction().equals(po.getIntroduction())) {
            contents.add("集团简介：由【" + po.getIntroduction() + "】改为【" + group.getIntroduction() + "】");
        }

        if (StringUtil.isEmpty(group.getIntroduction()) && StringUtil.isNotEmpty(po.getIntroduction())) {
            contents.add("集团简介：由【" + po.getIntroduction() + "】改为【无】");
        }
        for (String content : contents) {
            OperationRecord record = new OperationRecord();
            record.setContent(content);
            record.setCreateTime(System.currentTimeMillis());
            record.setCreator(ReqUtil.instance.getUserId());
            record.setObjectId(group.getId());

            recordDao.save(record);
        }

    }

    private String covertLogoUrl(String string) {
        if (StringUtils.isNotBlank(string)) {
            if (string.equals("无")) {
                return string;
            }
            String[] links = string.split(",");

            StringBuffer sb = new StringBuffer();

            for (int i = 0; i < links.length; i++) {
                sb.append("<a class='mblue' target='_blank' href=' ");
                sb.append(links[i]);
                sb.append("'>链接");
                if (i + 1 != links.length) {
                    sb.append(",");
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    @Override
    public PageVO findAllGroupExDept(String name,int pageIndex, int pageSize) {
        return groupDao.findAllGroupExDept(name,pageIndex,pageSize);
    }
}
