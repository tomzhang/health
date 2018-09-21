package com.dachen.health.circle.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.api.credit.CreditApiProxy;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.circle.CircleImBizTypeEnum;
import com.dachen.health.circle.CirleErrorCodeEnum;
import com.dachen.health.circle.entity.*;
import com.dachen.health.circle.service.*;
import com.dachen.health.circle.vo.*;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.service.IFeeService;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupConfig;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.pub.model.po.PubPO;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.PropertiesUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Model(Group2.class)
@Service
public class Group2ServiceImpl extends BaseServiceImpl implements Group2Service {

    @Autowired
    protected IGroupDao groupDao;
    @Autowired
    protected IFeeService feeService;
    @Autowired
    protected PubGroupService pubGroupService;
    @Autowired
    protected IGroupDoctorDao groupDoctorDao;
    @Autowired
    protected GroupUser2Service groupUser2Service;
    @Autowired
    private CreditApiProxy creditApiProxy;
    @Autowired
    protected ImService imService;
    @Autowired
    protected GroupDoctor2Service groupDoctor2Service;
    @Autowired
    protected GroupDoctorApplyService groupDoctorApplyService;
    @Override
    public List<Group2> findFullByIds(List<String> idList) {
        List<Group2> group2List = this.findByIds(idList);
        this.wrapAll(group2List);
        return group2List;
    }

    protected String getDefaultDeptLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.dept"));
        return pic;
    }

    protected String getDefaultHospitalLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.hospital"));
        return pic;
    }
    protected String getDefaultGroupLogoPicUrl() {
        String pic = String.format("%s/default/%s", PropertiesUtil.getHeaderPrefix(),  PropertiesUtil.getContextProperty("group.group"));
        return pic;
    }

    @Autowired
    protected IBaseDataDao baseDataDao;

    @Override
    public Group2 createDept(Integer currentUserId, String hospitalId, String deptId, String childName, String introduction, String logoPicUrl) throws HttpApiException {
        String tag = "createDept";

        Integer doctorId = currentUserId;

        // 只有医生才能创建科室
        User user = this.user2Service.findAndCheckDoctor(doctorId);
        HospitalVO hospitalVO = baseDataDao.getHospital(hospitalId);
        DeptVO deptVO = baseDataDao.getDeptById(deptId);

        // 一名医生只能创建一个科室（第1次检查）
        this.groupDoctor2Service.checkCurDeptIdWhenJoinDept(doctorId);

        Group2 tmp = new Group2();
        tmp.setHospitalId(hospitalVO.getId());
        tmp.setHospitalName(hospitalVO.getName());
        tmp.setDeptId(deptVO.getId());
        tmp.setDeptName(deptVO.getName());
        tmp.setIntroduction(introduction);
        tmp.setLogoUrl(logoPicUrl);
        tmp.setType(GroupEnum.GroupType.dept.getIndex()); // 科室类型
        this.setNameAndCheck(tmp, childName);

        // 处理默认图片
        if (StringUtils.isBlank(tmp.getLogoUrl())) {
            tmp.setLogoUrl(this.getDefaultDeptLogoPicUrl());
        }

        Long now = System.currentTimeMillis();
        tmp.setCreator(currentUserId);
        tmp.setCreatorDate(now);
        tmp.setUpdator(currentUserId);
        tmp.setUpdatorDate(now);
        // 初始化 集团默认的配置参数
        GroupConfig config = this.initGroupConfigForCreateGroup();
        tmp.setConfig(config);
        tmp.setOutpatientPrice(1000); // 默认值班价格
        tmp.setActive(GroupEnum.GroupActive.active.getIndex());
        tmp.setSkip(GroupEnum.GroupSkipStatus.normal.getIndex());
        // 一名医生只能创建一个科室（第2次检查）
        this.groupDoctor2Service.checkCurDeptIdWhenJoinDept(doctorId);
        logger.info(tag+" tmp : {}" + ToStringBuilder.reflectionToString(tmp));
        Group2 Group2 = this.saveEntityAndFind(tmp);

        this.afterCreate(currentUserId, Group2);
        return tmp;
    }

    @Override
    public List<Group2> findDeptListByDoctor(List<Integer> doctorIdList) {
        List<String> groupIdList = groupDoctor2Service.findDeptIdListByDoctor(doctorIdList);
        if (SdkUtils.isEmpty(groupIdList)) {
            return null;
        }
        return this.findByIds(groupIdList);
    }


    @Override
    public MobileGroupVO createDeptAndVO(Integer currentUserId, String hospitalId, String deptId, String childName, String introduction, String logoPicUrl) throws HttpApiException {
        Group2 group2 = this.createDept(currentUserId, hospitalId, deptId, childName, introduction, logoPicUrl);
        MobileGroupVO vo = this.convertToMobile(group2);
        return vo;
    }

    @Override
    public Group2 findDeptByUser(Integer userId) {
        String groupId = groupDoctor2Service.findDeptIdByDoctor(userId);
        if (StringUtils.isBlank(groupId)) {
            return null;
        }
        Group2 group2 = this.findById(groupId);
        return group2;
    }

    @Override
    public Group2 findAndCheckById(String id) {
        Group2 group2 = this.findById(id);
        this.checkDept(group2);
        return group2;
    }

    @Override
    public Group2 findAndCheckDept(String id) {
        Group2 group2 = this.findById(id);

        this.checkDept(group2);
        return group2;
    }
    @Override
    public Group2 findAndCheckGroupOrDept(String id) {
        Group2 group2 = this.findById(id);
        checkDeptOrGroup(group2);
        return group2;
    }

    @Override
    public List<String> filterGroupOrDeptIds(List<String> idList) {
        if (SdkUtils.isEmpty(idList)) {
            return null;
        }

        Query<Group2> query = this.createQueryByPKs(idList);

        Criteria[] criteria = new Criteria[2];
        criteria[0] = query.criteria("type").equal(GroupEnum.GroupType.group.getIndex());
        criteria[1] = query.criteria("type").equal(GroupEnum.GroupType.dept.getIndex());
        query.or(criteria);

        query.retrievedFields(true, Mapper.ID_KEY);

        List<Group2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> idList2 = list.stream().map(o->o.getId().toString()).collect(Collectors.toList());
        return idList2;
    }

    @Override
    public void checkDept(Group2 group2) {
        if (null == group2) {
            throw new ServiceException(CirleErrorCodeEnum.GroupNoExistent.getId(),"科室已解散");
        }
        if (GroupEnum.GroupSkipStatus.skip.getIndex().equals(group2.getSkip())) {
            throw new ServiceException(CirleErrorCodeEnum.GroupNoExistent.getId(),"科室已解散");
        }
        if (!GroupEnum.GroupType.dept.getIndex().equals(group2.getType()) ) {
            throw new ServiceException("Forbidden");
        }
    }

    @Override
    public void checkDeptOrGroup(Group2 group2) {
        if (null == group2) {
            throw new ServiceException("已解散");
        }
        if (GroupEnum.GroupType.dept.getIndex().equals(group2.getType()) && GroupEnum.GroupSkipStatus.skip.getIndex().equals(group2.getSkip())) {
            throw new ServiceException(CirleErrorCodeEnum.GroupNoExistent.getId(),"科室已解散");
        }
        if (GroupEnum.GroupType.group.getIndex().equals(group2.getType()) && GroupEnum.GroupSkipStatus.skip.getIndex().equals(group2.getSkip())) {
            throw new ServiceException("圈子已解散");
        }
        if (!GroupEnum.GroupType.dept.getIndex().equals(group2.getType()) && !GroupEnum.GroupType.group.getIndex().equals(group2.getType())) {
            throw new ServiceException("Forbidden");
        }
    }

    /**
     * 获取全部已经屏蔽的集团的id
     */
    @Override
    public List<String> getSkipGroupIds() {
        Query<Group2> query = this.createQuery();
        query.filter("skip", GroupEnum.GroupSkipStatus.skip.getIndex());
        query.retrievedFields(true, Mapper.ID_KEY);

        List<Group2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        List<String> groupIdList = list.stream().map(o -> o.getId().toString()).collect(Collectors.toList());
        return groupIdList;
    }

    @Autowired
    protected User2Service user2Service;

    /**
     * 管理员解散科室
     *
     * 1、科室的所有管理员都能解散科室
     * 2、解散后，如果有科室学分，全部转移到解散科室的医生身上
     *
     * @param currentUserId
     * @param id
     * @return
     */
    @Override
    public boolean dismissDept(Integer currentUserId, String id) {
        Group2 group2 = this.findAndCheckDept(id);
        Integer doctorId = currentUserId;
        User user = this.user2Service.findAndCheckDoctor(doctorId);

        // check before delete
        int count = groupUnionMemberService.countUnionMemberByGroup(id);
        if (count > 0) {
            throw new ServiceException("已经加入科室联盟，需退出科室联盟后才能解散科室");
        }

        group2.setSkip(GroupEnum.GroupSkipStatus.skip.getIndex());
        this.saveEntity(group2);

        boolean ret = true;
//        boolean ret = this.deleteById(id);
        if (ret) {
            groupDoctor2Service.deleteByDept(id);   // 维护冗余字段
            groupUser2Service.deleteByGroup(id);
            groupUnionMemberService.deleteByGroup(currentUserId, id);
            groupUnionApplyService.closeByGroupDismiss(id);
            groupUnionInviteService.closeByGroupDismiss(id);

            // 取消关注
            try {
                groupFollowService.dismissGroup(currentUserId, id);
            } catch (HttpApiException e) {
                logger.error(" 取消关注失败 doctorId:"+doctorId+"id:"+id,e);
                logger.error(e.getMessage(), e);
            }
            // 发送解散通知
            this.sendDismissDeptIMMsgToClients(currentUserId,group2);
            // TODO：公众号需要删除？
            // TODO：科室学分转移到currentUserId名下
            try {
                String reason = String.format("%1$s学币转移", group2.getName());
                creditApiProxy.userIntegralChange(currentUserId+"", creditApiProxy.deptBalance(id), reason, reason, "234");
            } catch (HttpApiException e) {
                logger.error(" 转移科室学分失败 doctorId:"+doctorId+"id:"+id,e);
                logger.error(e.getMessage(), e);
            }
        }
        return ret;
    }

    protected void sendDismissDeptIMMsgToClients(Integer currentUserId, Group2 group2) {
        User user = user2Service.findDoctorById(currentUserId);
        List<Integer> userIds = groupUser2Service.findDoctorIdList(group2.getId().toString()); //获取groupId所有管理员
        if(userIds!=null && userIds.size()>0){
            return;
        }
        // 通知科室或者圈子所有管理员
        for (Integer userId:userIds) {
            String title = String.format("系统通知");
            String content = String.format("%s解散了%s", user.getName(), group2.getName());
            Map<String, Object> params = new HashedMap(2);
            params.put("bizType", CircleImBizTypeEnum.GroupDismiss.getId());
            params.put("bizId", group2.getId());
            imService.sendTodoNotifyMsg(userId, title, content, null, params);
        }
    }

    protected void afterCreate(Integer currentUserId, Group2 Group2) throws HttpApiException {
        // 将当前医生加入到科室
        groupDoctor2Service.addByCreateDept(currentUserId, Group2, currentUserId);

        //保存科室收费
        FeeParam param = new FeeParam();
        param.setGroupId(Group2.getId().toString());
        param.setTextMin(1000);
        param.setTextMax(10000);
        param.setPhoneMin(1000);
        param.setPhoneMax(10000);
        param.setCarePlanMin(0);
        param.setCarePlanMax(10000);
        feeService.save(param);

        /*
        15:48:01.172 [http-nio-8101-exec-6] INFO  com.dachen.sdk.component.RemoteInvokeComponent - executePostMethod. http://pubacc/inner/pub/create execute spent 44 ms, ret={"data":{"allssb":false,"creatTime":1495180081170,"creator":"100180","cssb":false,"default":true,"folder":"dept","mid":"591ea33160b2664c69738707","name":"科室动态_清苑县妇幼保健院普内科2","nickName":"清苑县妇幼保健院
普内科2","note":"222","pid":"pub_dept_591ea33160b2664c69738707","reply":false,"rtype":"pub_dept","state":2},"resultCode":1}
         */
        // 开通公众号
        PubPO pubPO = pubGroupService.createPubForDept("科室动态", Group2.getCreator(), Group2.getId().toString(), Group2.getName(),
                Group2.getIntroduction(), Group2.getLogoUrl());

        // 当前用户订阅公众号(等待公众号那边提供对应的类型）
        groupFollowService.add(currentUserId, Group2.getId().toString());

        // 2017-05-31 16:48:48,195 [http-nio-8101-exec-10] INFO  com.dachen.sdk.component.RemoteInvokeComponent - executePostMethod. http://pubacc/inner/pub/sendMsg execute spent 5 ms, ret={"detailMsg":"空指针错误","resultCode":100,"resultMsg":"空指针错误"}
//        imService.sendTextMsg(currentUserId, pubPO.getPid(), "Hello World. " + new Date().toString());
    }


    /**
     * 创建集团/医院时，设置默认的配置参数
     *
     * @return
     * @author wangqiao
     * @date 2016年4月29日
     */
    protected GroupConfig initGroupConfigForCreateGroup() {
        //设置集团为允许医生申请加入，也允许医生邀请其他医生加入
        GroupConfig config = new GroupConfig();
        config.setMemberApply(true);
        config.setMemberInvite(true);

        //抽成比例默认都设置为0
        config.setCarePlanGroupProfit(GroupConfig.CARE_PLAN_GROUP_PROFIT_DEFAULT);
        config.setCarePlanParentProfit(GroupConfig.CARE_PLAN_PARENT_PROFIT_DEFAULT);

        config.setTextGroupProfit(GroupConfig.TEXT_GROUP_PROFIT_DEFAULT);
        config.setTextParentProfit(GroupConfig.TEXT_PARENT_PROFIT_DEFAULT);

        config.setPhoneGroupProfit(GroupConfig.PHONE_GROUP_PROFIT_DEFAULT);
        config.setPhoneParentProfit(GroupConfig.PHONE_PARENT_PROFIT_DEFAULT);

        config.setConsultationGroupProfit(GroupConfig.CONSULTATION_GROUP_PROFIT_DEFAULT);
        config.setConsultationParentProfit(GroupConfig.CONSULTATION_PARENT_PROFIT_DEFAULT);

        config.setClinicGroupProfit(GroupConfig.CLINIC_GROUP_PROFIT_DEFAULT);
        config.setClinicParentProfit(GroupConfig.CLINIC_PARENT_PROFIT_DEFAULT);

        config.setChargeItemGroupProfit(GroupConfig.CHARGE_ITEM_GROUP_PROFIT_DEFAULT);
        config.setChargeItemParentProfit(GroupConfig.CHARGE_ITEM_PARENT_PROFIT_DEFAULT);

        config.setAppointmentGroupProfit(GroupConfig.APPOINTMENT_GROUP_PROFIT_DEFAULT);
        config.setAppointmentParentProfit(GroupConfig.APPOINTMENT_PARENT_PROFIT_DEFAULT);

        config.setGroupProfit(GroupConfig.GROUP_PROFIT_DEFAULT);
        config.setParentProfit(GroupConfig.PARENT_PROFIT_DEFAULT);

        // 默认设置值班开始和结束时间段为08:00-22:00
        config.setDutyStartTime(GroupUtil.GROUP_DUTY_START_TIME);
        config.setDutyEndTime(GroupUtil.GROUP_DUTY_END_TIME);
        return config;
    }

    @Override
    public Group2 findGroupHomePage(Integer currentUserId, String id) {
        Group2 dbItem = this.findAndCheckById(id);
        this.wrapAll(dbItem);
        this.wrapGroupUser(currentUserId, dbItem);
        this.wrapGroupApply(currentUserId, dbItem);
        this.wrapFollow(currentUserId, dbItem);

        return dbItem;
    }

    private void wrapGroupApply(Integer currentUserId, Group2 dbItem) {
        if (null == dbItem) {
            return;
        }
        GroupDoctor2 groupDoctor2 = groupDoctorApplyService.findByUKAndGroupDoctor(currentUserId, dbItem.getId().toString());
        dbItem.setGroupDoctor(groupDoctor2);
    }

    @Override
    public MobileGroupHomePageVO findGroupHomePageAndVO(Integer currentUserId, String id) {
        Group2 dbItem = this.findGroupHomePage(currentUserId, id);
        MobileGroupHomePageVO vo = this.convertToMobileHomePage(dbItem);
        return vo;
    }

    protected MobileGroupHomePageVO convertToMobileHomePage(Group2 group2) {
        if (null == group2) {
            return null;
        }
        MobileGroupHomePageVO vo = new MobileGroupHomePageVO(group2);
        return vo;
    }


    protected void wrapGroupDoctor(Integer doctorId, Group2 group) {
        if (null == group) {
            return;
        }
        GroupDoctor2 groupDoctor2 = groupDoctor2Service.findByUK(group.getId().toString(), doctorId);
        if (null != groupDoctor2) {
            group.setGroupDoctor(groupDoctor2);
        }
    }

    protected void wrapGroupUser(Integer doctorId, Group2 group) {
        if (null == group) {
            return;
        }
        GroupUser2 groupUser = groupUser2Service.findByUK(group.getId().toString(), doctorId);
        group.setGroupUser2(groupUser);
    }

    protected void wrapFollow(Integer currentUserId, Group2 group) {
        if (null == group) {
            return;
        }
        GroupFollow groupFollow = groupFollowService.findByUK(group.getId().toString(), currentUserId);
        group.setFollow(groupFollow);
    }

    protected MobileGroupVO convertToMobile(Group2 group2) {
        if (null == group2) {
            return null;
        }
        MobileGroupVO vo = new MobileGroupVO(group2);
        return vo;
    }

    protected List<MobileGroupVO> convertToMobile(List<Group2> groupList) {
        if (SdkUtils.isEmpty(groupList)) {
            return null;
        }
        List<MobileGroupVO> voList = new ArrayList<>(groupList.size());
        for (Group2 group2 : groupList) {
            voList.add(new MobileGroupVO(group2));
        }
        return voList;
    }

    @Override
    public MobileGroupVO updateLogoAndVO(Integer currentUserId, String id, String logoPicUrl) {
        Group2 dbItem = this.findById(id);
        this.groupUser2Service.checkRootOrAdminPri(id, currentUserId);

        dbItem.setLogoUrl(logoPicUrl);
        dbItem.setUpdator(currentUserId);
        dbItem.setUpdatorDate(System.currentTimeMillis());
        dbItem = this.saveEntityAndFind(dbItem);
        updatePubForDeptAsync(dbItem);
        MobileGroupVO vo = this.convertToMobile(dbItem);
        return vo;
    }

    protected void updatePubForDeptAsync(Group2 group2) {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    pubGroupService.updatePubForDept(group2.getId().toString(), group2.getName(), group2.getLogoUrl());
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    @Override
    public MobileGroupVO updateNameAndVO(Integer currentUserId, String id, String childName) {
        Group2 dbItem = this.findById(id);
        this.groupUser2Service.checkRootOrAdminPri(id, currentUserId);

        this.setNameAndCheck(dbItem, childName);

        dbItem.setUpdator(currentUserId);
        dbItem.setUpdatorDate(System.currentTimeMillis());
        dbItem = this.saveEntityAndFind(dbItem);
        updatePubForDeptAsync(dbItem);
        MobileGroupVO vo = this.convertToMobile(dbItem);
        return vo;
    }


    @Override
    public MobileGroupVO updateIntroAndVO(Integer currentUserId, String id, String intro) {
        Group2 dbItem = this.findById(id);
        this.groupUser2Service.checkRootOrAdminPri(id, currentUserId);

        dbItem.setIntroduction(intro);
        dbItem.setUpdator(currentUserId);
        dbItem.setUpdatorDate(System.currentTimeMillis());
        dbItem = this.saveEntityAndFind(dbItem);
        MobileGroupVO vo = this.convertToMobile(dbItem);
        return vo;
    }

    @Override
    public Group2 updateDept(Integer currentUserId, String id, String childName, String logoPicUrl, String intro) {
        Group2 dbItem = this.findById(id);
        this.groupUser2Service.checkRootOrAdminPri(id, currentUserId);

        this.setNameAndCheck(dbItem, childName);
        if (StringUtils.isNotBlank(intro)) {
            dbItem.setIntroduction(intro);
        }
        if (StringUtils.isNotBlank(logoPicUrl)) {
            dbItem.setLogoUrl(logoPicUrl);
        }
        dbItem.setUpdator(currentUserId);
        dbItem.setUpdatorDate(System.currentTimeMillis());
        dbItem = this.saveEntityAndFind(dbItem);
        return dbItem;
    }

    @Override
    public List<Group2> findNormalExceptDept(List<String> idList){
        Query<Group2> query = this.createQueryByPKs(idList);
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        query.field("type").notEqual(GroupEnum.GroupType.dept.getIndex());
        List<Group2> group2s = query.asList();
        return group2s;
    }

    public Group2 findNormalDeptByUK(String hospitalId, String deptId, String childName) {
        Query<Group2> query = this.createQuery();
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("type").equal(GroupEnum.GroupType.dept.getIndex());
        query.field("hospitalId").equal(hospitalId.toString());
        query.field("deptId").equal(deptId.toString());
        query.field("childName").equal(childName.trim());
        return query.get();
    }

    public void setNameAndCheck(Group2 group, String childName) {
        if (null == childName) {
            childName = "";
        }
        childName = childName.trim();
        Group2 dbItem = this.findNormalDeptByUK(group.getHospitalId(), group.getDeptId(), childName);
        if (null != dbItem) {
            if (null == group.getId()) {
                throw new ServiceException("该科室已存在，请输入其他科室名称");
            }
            if (!dbItem.getId().toString().equals(group.getId().toString())) {
                throw new ServiceException("该科室已存在，请输入其他科室名称");
            }
        }
        group.setChildName(childName);
        group.setName(String.format("%s%s%s", group.getHospitalName(), group.getDeptName(), childName));
    }

    @Autowired
    protected IGroupSearchDao groupSearchDao;

    @Override
    public Pagination<Group2> findPage(String kw, Integer pageIndex, Integer pageSize) {
        Query<Group2> query = this.createQuery();
        query.field(Mapper.ID_KEY).notEqual(new ObjectId(GroupUtil.PLATFORM_ID));
        if (StringUtils.isNotBlank(kw)) {
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("name").equal(pattern);
        }

        Criteria[] typeParams = new Criteria[2];
        typeParams[0] = query.criteria("type").equal(GroupEnum.GroupType.group.getIndex());
        typeParams[1] = query.criteria("type").equal(GroupEnum.GroupType.dept.getIndex());
        query.or(typeParams);

        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        query.order("-creatorDate");

        long total = query.countAll();
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<Group2> list = query.asList();

        Pagination<Group2> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<Group2> findPage(String kw, List<String> exceptIdList, Integer pageIndex, Integer pageSize) {
        Query<Group2> query = this.createQuery();
        query.field(Mapper.ID_KEY).notEqual(new ObjectId(GroupUtil.PLATFORM_ID));
        if (StringUtils.isNotBlank(kw)) {
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("name").equal(pattern);
        }

        if (SdkUtils.isNotEmpty(exceptIdList)) {
            query.field(Mapper.ID_KEY).notIn(this.convertToObjectId(exceptIdList));
        }

        Criteria[] typeParams = new Criteria[2];
        typeParams[0] = query.criteria("type").equal(GroupEnum.GroupType.group.getIndex());
        typeParams[1] = query.criteria("type").equal(GroupEnum.GroupType.dept.getIndex());
        query.or(typeParams);

        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        query.order("-creatorDate");

        long total = query.countAll();
        query.offset(pageIndex * pageSize).limit(pageSize);
        List<Group2> list = query.asList();

        Pagination<Group2> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<Group2> findDeptPage(String kw, List<String> exceptIdList, Integer pageIndex, Integer pageSize) {
        String tag = "findDeptPage";
        Query<Group2> query = this.createQuery();
        query.field(Mapper.ID_KEY).notEqual(new ObjectId(GroupUtil.PLATFORM_ID));
        if (StringUtils.isNotBlank(kw)) {
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("name").equal(pattern);
        }

        if (SdkUtils.isNotEmpty(exceptIdList)) {
            query.field(Mapper.ID_KEY).notIn(this.convertToObjectId(exceptIdList));
        }

        query.field("type").equal(GroupEnum.GroupType.dept.getIndex());

        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        long total = query.countAll();

        query.order("-creatorDate");

        query.offset(pageIndex * pageSize).limit(pageSize);

        logger.debug("{}. query={}", tag, query);

        List<Group2> list = query.asList();
        if (SdkUtils.isEmpty(list)) {
            return null;
        }

        Pagination<Group2> page = new Pagination<>(list, total, pageIndex, pageSize);
        return page;
    }

    @Override
    public Pagination<MobileGroupVO> findDeptPageAndVO(Integer currentUserId, String kw, Integer pageIndex, Integer pageSize) {
        Pagination<Group2> page = this.findDeptPage(kw, null, pageIndex, pageSize);
        if (null == page || SdkUtils.isEmpty(page.getPageData())) {
            return null;
        }
        List<Group2> list = page.getPageData();
        this.wrapAll(list);
        List<MobileGroupVO> ret = this.convertToMobile(list);
        Pagination<MobileGroupVO> page2 = new Pagination<>(ret, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    @Override
    public Pagination<MobileGroupVO> findPageAndVO(Integer currentUserId, Integer pageIndex, Integer pageSize) {
        Pagination<Group2> page = this.findPage(null, pageIndex, pageSize);
        List<Group2> list = page.getPageData();
        this.wrapAll(list);
        List<MobileGroupVO> ret = this.convertToMobile(list);
        Pagination<MobileGroupVO> page2 = new Pagination<>(ret, page.getTotal(), page.getPageIndex(), page.getPageSize());
        return page2;
    }

    @Autowired
    protected GroupUnionService groupUnionService;

    @Autowired
    protected GroupUnionMemberService groupUnionMemberService;

    @Override
    public Group2 findNormalById(String id) {
        Query<Group2> query = this.createQueryByPK(id);
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        return query.get();
    }

    @Override
    public List<Group2> findNormalByIdsAndKw(List<String> idList, String kw) {
        Query<Group2> query = this.createQueryByPKs(idList);
        query.field(Mapper.ID_KEY).notEqual(new ObjectId(GroupUtil.PLATFORM_ID));
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        if (StringUtils.isNotBlank(kw)) {
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("name").equal(pattern);
        }
        return query.asList();
    }

//    protected List<Group2> findMyGroupExcept(Integer doctorId, List<String> exceptIdList) {
//        List<String> doctorGroupIdList = groupDoctor2Service.findGroupIdListByDoctorExcept(doctorId, exceptIdList);     // 我加入的组织
//        if (SdkUtils.isEmpty(doctorGroupIdList)) {
//            return null;
//        }
//        return this.findByIds(doctorGroupIdList);
//    }

    @Autowired
    protected GroupUnionApplyService groupUnionApplyService;

    @Autowired
    protected GroupUnionInviteService groupUnionInviteService;

    @Override
    public List<Group2> findRecList(Integer currentUserId) {
        User user = user2Service.findDoctorById(currentUserId);
        String hospitalId = user.getDoctor().getHospitalId();
        String deptId = user.getDoctor().getDeptId();
        if (StringUtils.isBlank(deptId) || StringUtils.isBlank(hospitalId)) {
            return null;
        }
        List<Group2> group2List = this.findByHospitalAndDeptExceptMy(hospitalId, deptId, currentUserId);
        if (SdkUtils.isEmpty(group2List)) {
            return null;
        }

        return group2List;
    }

    @Override
    public List<MobileGroupVO> findRecListAndVO(Integer currentUserId) {
        List<Group2> group2List = this.findRecList(currentUserId);
        if (SdkUtils.isEmpty(group2List)) {
            return null;
        }

        this.wrapAll(group2List);

        List<MobileGroupVO> voList = this.convertToMobile(group2List);
        return voList;
    }

    @Override
    public UserGroupAndUnionMap findUserGroupAndUnionMap(Integer userId) {
        return this.findUserGroupAndUnionMap(userId, false);
    }

    @Override
    public UserGroupAndUnionMapVO findUserGroupAndUnionMapAndVO(Integer userId) {
        UserGroupAndUnionMap map = this.findUserGroupAndUnionMap(userId, false);
        UserGroupAndUnionMapVO vo = this.convertToMobile(map);
        return vo;
    }

    protected UserGroupAndUnionMapVO convertToMobile(UserGroupAndUnionMap map) {
        if (null == map) {
            return null;
        }
        UserGroupAndUnionMapVO vo = new UserGroupAndUnionMapVO(map);
        return vo;
    }

    protected UserGroupAndUnionHomeMapVO convertToMobile(UserGroupAndUnionHomeMap map) {
        if (null == map) {
            return null;
        }
        UserGroupAndUnionHomeMapVO vo = new UserGroupAndUnionHomeMapVO(map);
        return vo;
    }

    protected void wrapRecDeptList(UserGroupAndUnionMap userGroupAndUnionMap, Integer userId) {
        // 将推荐的科室加入
        List<Group2> recDeptList = this.findRecList(userId);
        if (SdkUtils.isEmpty(recDeptList)) {
            return;
        }
        for (Group2 recDept:recDeptList) {
            userGroupAndUnionMap.addGroupDoctor(new GroupDoctor2(recDept, userId));
        }
    }

    public UserGroupAndUnionMap findUserGroupAndUnionMap(Integer userId, boolean recDeptIfEmpty) {
        Integer doctorId = userId;
        List<GroupDoctor2> groupDoctorVOList = groupDoctor2Service.findFullByDoctor(doctorId);

        UserGroupAndUnionMap userGroupAndUnionMap = new UserGroupAndUnionMap(userId);
        if (SdkUtils.isEmpty(groupDoctorVOList)) {
            if (recDeptIfEmpty) {
                // 将推荐的科室加入
                wrapRecDeptList(userGroupAndUnionMap, userId);
            }
            return userGroupAndUnionMap;
        }

        userGroupAndUnionMap.setGroupDoctors(groupDoctorVOList);

        boolean foundDept = false;
        Set<String> groupIdSet = new HashSet<>(groupDoctorVOList.size());
        for (GroupDoctor2 vo:groupDoctorVOList) {
            groupIdSet.add(vo.getGroupId());
            if (vo.getType().equals(GroupEnum.GroupType.dept.getIndex())) {
                foundDept = true;
            }
        }

        if (!foundDept) {
            if (recDeptIfEmpty) {
                // 将推荐的科室加入
                wrapRecDeptList(userGroupAndUnionMap, userId);
            }
        }

        List<GroupUnionMember> groupUnionMemberVOList = groupUnionMemberService.findFullByGroups(new ArrayList<>(groupIdSet));
        userGroupAndUnionMap.setUnionMembers(groupUnionMemberVOList);

        return userGroupAndUnionMap;
    }

    @Override
    public UserGroupAndUnionHomeMap findUserGroupAndUnionHomeMap(Integer currentUserId) {
        UserGroupAndUnionMap userGroupAndUnionMap = this.findUserGroupAndUnionMap(currentUserId, true);

        UserGroupAndUnionHomeMap vo = new UserGroupAndUnionHomeMap(userGroupAndUnionMap);
        this.wrapIfUnionCanCreate(currentUserId, vo);
        return vo;
    }

    @Override
    public UserGroupAndUnionHomeMapVO findUserGroupAndUnionHomeMapAndVO(Integer currentUserId) {
        UserGroupAndUnionHomeMap homeMap = this.findUserGroupAndUnionHomeMap(currentUserId);
        UserGroupAndUnionHomeMapVO vo = this.convertToMobile(homeMap);
        return vo;
    }



    protected void wrapIfUnionCanCreate(Integer currentUserId, UserGroupAndUnionHomeMap vo) {
        if (null == vo) {
            return;
        }

        // 管理员可以创建科室联盟
        boolean ifCan = this.groupUnionService.ifCanCreate(currentUserId);
        vo.setIfUnionCanCreate(ifCan?1:0);
    }

    @Override
    public UserGroupAndUnionIdMap findUserGroupAndUnionIdMap(Integer userId) {
        Integer doctorId = userId;
        List<String> groupIdList = groupDoctor2Service.findGroupIdListByDoctor(doctorId);
        UserGroupAndUnionIdMap vo = new UserGroupAndUnionIdMap(userId);
        if (SdkUtils.isEmpty(groupIdList)) {
            return vo;
        }
        vo.setGroupIds(groupIdList);

        List<String> unionIdList = groupUnionMemberService.findUnionIdsByGroups(groupIdList);
        vo.setUnionIds(unionIdList);

        return vo;
    }

    @Override
    public int countTotalCure(String id) {
        List<Integer> doctorIdList = groupDoctor2Service.findDoctorIdListByGroup(id);
        if (SdkUtils.isEmpty(doctorIdList)) {
            return 0;
        }
        int count = user2Service.countTotalCure(doctorIdList);
        return count;
    }

    public List<Group2> findByHospitalAndDeptExceptMy(String hospitalId, String deptId, Integer exceptUserId) {
        String myDeptId = groupDoctor2Service.findDeptIdByDoctor(exceptUserId);

        Query<Group2> query = this.createQuery();
        query.field(Mapper.ID_KEY).notEqual(new ObjectId(GroupUtil.PLATFORM_ID));
        query.field("hospitalId").equal(hospitalId).field("deptId").equal(deptId);
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        if (StringUtils.isNotBlank(myDeptId)) {
            query.field(Mapper.ID_KEY).notEqual(new ObjectId(myDeptId));
        }
        return query.asList();
    }

    /**
     * 判断集团状态已激活且未屏蔽）
     */
    public boolean ifNormalGroup(String id) {
        Group2 group = this.findById(id);
        if (null == group) {
            return false;
        }
        if (GroupEnum.GroupType.hospital.getIndex().equals(group.getType())) { // 医院 不需判断 激活 审核 屏蔽
            return true;
        }

        if (GroupEnum.GroupActive.active.getIndex().equals(group.getActive())
                && GroupEnum.GroupSkipStatus.normal.getIndex().equals(group.getSkip())) {   // 已激活 且 未屏蔽
            return true;
        }

        return false;
    }

    @Autowired
    protected GroupFollowService groupFollowService;

    protected void wrapAll(Group2 group2) {
        if (null == group2) {
            return;
        }

        this.wrapCreator(group2);
        this.wrapTotal(group2);
    }

    protected void wrapAll(List<Group2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        this.wrapTotal(list);
        this.wrapCreator(list);
        this.wrapDefaultLogo(list);
    }


    protected void wrapTotal(Group2 group2) {
        if (null == group2) {
            return;
        }

        Integer count = groupDoctor2Service.countByGroup(group2.getId().toString());
        group2.setTotalMember(count);
        count = groupUser2Service.countByGroup(group2.getId().toString());
        group2.setTotalManager(count);
    }

    protected void wrapTotal(List<Group2> groupList) {
        if (SdkUtils.isEmpty(groupList)) {
            return;
        }
        Set<String> idSet = new HashSet<>(groupList.size());
        for (Group2 group2 : groupList) {
            idSet.add(group2.getId().toString());
        }
        List<String> idList = new ArrayList<>(idSet);
        Map<String, Integer> countMap = groupDoctor2Service.countByGroupList(idList);
        for (Group2 group2 : groupList) {
            Integer count = countMap.get(group2.getId().toString());
            group2.setTotalMember(count);
        }

        Map<String, Integer> countMap2 = groupUser2Service.countByGroupList(idList);
        for (Group2 group2 : groupList) {
            Integer count = countMap2.get(group2.getId().toString());
            group2.setTotalManager(count);
        }
    }

    protected void wrapCreator(Group2 group2) {
        if (null == group2) {
            return;
        }
        User user = user2Service.findById(group2.getCreator());
        group2.setCreatorUser(user);
    }


    protected void wrapDefaultLogo(List<Group2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        String defaultHospitalPicUrl = getDefaultHospitalLogoPicUrl();
        String defaultGroupPicUrl = getDefaultGroupLogoPicUrl();
        String defaultDeptPicUrl = getDefaultDeptLogoPicUrl();
        for (Group2 group2 : list) {
            if (StringUtils.isBlank(group2.getLogoUrl())) {
                switch (GroupEnum.GroupType.eval(group2.getType())) {
                    case hospital:
                        group2.setLogoUrl(defaultHospitalPicUrl);
                        break;
                    case group:
                        group2.setLogoUrl(defaultGroupPicUrl);
                        break;
                    case dept:
                        group2.setLogoUrl(defaultDeptPicUrl);
                        break;
                }
            }
        }
    }

    protected void wrapCreator(List<Group2> list) {
        if (SdkUtils.isEmpty(list)) {
            return;
        }
        Set<Integer> userIdSet = new HashSet<>();
        for (Group2 group2 : list) {
            userIdSet.add(group2.getCreator());
        }
        List<User> userList = user2Service.findByIds(new ArrayList<>(userIdSet));
        for (Group2 group2 : list) {
            for (User user : userList) {
                if (group2.getCreator().equals(user.getUserId())) {
                    group2.setCreatorUser(user);
                    break;
                }
            }
        }
    }

}
