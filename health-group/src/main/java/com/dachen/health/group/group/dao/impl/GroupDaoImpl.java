package com.dachen.health.group.group.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.mongo.MongoOperator;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupActive;
import com.dachen.health.commons.constants.GroupEnum.GroupCertStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupSkipStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.common.entity.vo.RecommendGroupVO;
import com.dachen.health.group.company.dao.ICompanyUserDao;
import com.dachen.health.group.company.entity.po.GroupUser;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.param.GroupCertParam;
import com.dachen.health.group.group.entity.param.GroupParam;
import com.dachen.health.group.group.entity.param.GroupsParam;
import com.dachen.health.group.group.entity.po.*;
import com.dachen.health.group.group.entity.vo.GroupDoctorVO;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.util.DbObjectUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author pijingwei
 * @dete 2015/8/7
 */
@Repository
public class GroupDaoImpl extends NoSqlRepository implements IGroupDao {

	@Autowired
	private UserManager userManager;

    @Autowired
    protected IBaseDataDao baseDataDao;

    @Autowired
    protected ICompanyUserDao companyUserDao;

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.dao.IGroupDao#save(com.dachen.health.group.group.entity.po.Group)
     */
    @Override
    public Group save(Group group) {
        if (group == null) {
            throw new ServiceException("持久化的bean对象不能为空");
        }

        String id = dsForRW.insert(group).getId().toString();
        return dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(id)).get();
    }

    @Override
    public Group update(Group group) {
        DBObject query = new BasicDBObject();
        DBObject update = new BasicDBObject();


        if (!StringUtil.isEmpty(group.getCompanyId())) {
            update.put("companyId", group.getCompanyId());
        }
        //更新集团名称
        if (!StringUtil.isEmpty(group.getName())) {
            update.put("name", group.getName());
        }
        //更新集团logoUrl
        if (!StringUtil.isEmpty(group.getLogoUrl())) {
            update.put("logoUrl", group.getLogoUrl());
        }
        if (group.getWeight() != null) {
            update.put("weight", group.getWeight());
        }

        if (group.getExpertIds() != null) {
            update.put("expertIds", group.getExpertIds());
        }
        //更新集团描述
        if (!StringUtil.isEmpty(group.getIntroduction())) {
            update.put("introduction", group.getIntroduction());
        }

        if (group.getCureNum() != null) {
            update.put("cureNum", group.getCureNum());
        }
        //更新最后更新人
        if (group.getUpdator() != null && group.getUpdator() != 0) {
            update.put("updator", group.getUpdator());
        }
        //更新最后更新时间
        if (group.getUpdatorDate() != null) {
            update.put("updatorDate", group.getUpdatorDate());
        }
        //更新集团状态
        if (group.getApplyStatus() != null) {
            update.put("applyStatus", group.getApplyStatus());
        }
        //更新集团激活状态
        if (group.getActive() != null) {
            update.put("active", group.getActive());
        }
        //更新集团加V认证状态
        if (group.getCertStatus() != null) {
            update.put("certStatus", group.getCertStatus());
        }
        //更新集团屏蔽状态
        if (group.getSkip() != null) {
            // 集团屏蔽 正常 add by tanyf 20160604
            update.put("skip", group.getSkip());
        }

        if (group.getStandard() != null) {
            update.put("standard", group.getStandard());
        }

        if (null != group.getConfig()) {
            DBObject config = null;
            try {
                Group oldGroup = getById(group.getId());
                config = DbObjectUtil.bean2DBObject(oldGroup.getConfig());
                config = config == null ? new BasicDBObject() : config;
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
            if (null != group.getConfig().getTextGroupProfit()) {
                config.put("textGroupProfit", group.getConfig().getTextGroupProfit());
            }
            if (null != group.getConfig().getTextParentProfit()) {
                config.put("textParentProfit", group.getConfig().getTextParentProfit());
            }
            if (null != group.getConfig().getPhoneGroupProfit()) {
                config.put("phoneGroupProfit", group.getConfig().getPhoneGroupProfit());
            }
            if (null != group.getConfig().getPhoneParentProfit()) {
                config.put("phoneParentProfit", group.getConfig().getPhoneParentProfit());
            }
            if (null != group.getConfig().getCarePlanGroupProfit()) {
                config.put("carePlanGroupProfit", group.getConfig().getCarePlanGroupProfit());
            }
            if (null != group.getConfig().getCarePlanParentProfit()) {
                config.put("carePlanParentProfit", group.getConfig().getCarePlanParentProfit());
            }
            if (null != group.getConfig().getClinicGroupProfit()) {
                config.put("clinicGroupProfit", group.getConfig().getClinicGroupProfit());
            }
            if (null != group.getConfig().getClinicParentProfit()) {
                config.put("clinicParentProfit", group.getConfig().getClinicParentProfit());
            }
            //添加会诊抽成比例
            if (null != group.getConfig().getConsultationGroupProfit()) {
                config.put("consultationGroupProfit", group.getConfig().getConsultationGroupProfit());
            }
            if (null != group.getConfig().getConsultationParentProfit()) {
                config.put("consultationParentProfit", group.getConfig().getConsultationParentProfit());
            }

            if (null != group.getConfig().getChargeItemGroupProfit()) {
                config.put("chargeItemGroupProfit", group.getConfig().getChargeItemGroupProfit());
            }

            if (null != group.getConfig().getChargeItemParentProfit()) {
                config.put("chargeItemParentProfit", group.getConfig().getChargeItemParentProfit());
            }

            if (null != group.getConfig().getDutyStartTime()) {
                config.put("dutyStartTime", group.getConfig().getDutyStartTime());
            }
            if (null != group.getConfig().getDutyEndTime()) {
                config.put("dutyEndTime", group.getConfig().getDutyEndTime());
            }

            config.put("memberInvite", group.getConfig().isMemberInvite());
            config.put("passByAudit", group.getConfig().isPassByAudit());
            config.put("memberApply", group.getConfig().isMemberApply());
            update.put("config", config);
        }

        if (group.getDiseaselist() != null) {
            update.put("diseaselist", group.getDiseaselist());
        }
        if (group.getOutpatientPrice() != null) {
            update.put("outpatientPrice", group.getOutpatientPrice());
        }

        query.put("_id", new ObjectId(group.getId()));
        dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set", update), true, false);
        return dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(group.getId())).get();
    }

    @Override
    public boolean delete(String... ids) {
        BasicDBList values = new BasicDBList();
        BasicDBObject in = new BasicDBObject();
        for (String str : ids) {
            values.add(new ObjectId(str));
        }
        in.put("$in", values);

        dsForRW.getDB().getCollection("c_group").remove(new BasicDBObject("_id", in));
        return true;
    }

    @Override
    public PageVO search(GroupParam group) {
        DBObject query = new BasicDBObject();

        if (!StringUtil.isEmpty(group.getName())) {
            query.put("name", group.getName());
        }

        if (!StringUtil.isEmpty(group.getIntroduction())) {
            query.put("introduction", group.getIntroduction());
        }

        if (null != group.getCreator()) {
            query.put("creator", group.getCreator());
        }
        if (!StringUtil.isEmpty(group.getCompanyId())) {
            query.put("companyId", group.getCompanyId());
        }

        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query);
        List<GroupVO> gList = new ArrayList<GroupVO>();

        BasicDBList docIds = new BasicDBList();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            GroupVO g = new GroupVO();

            g.setId(obj.get("_id").toString());
            g.setCompanyId(obj.get("companyId") == null ? "" : obj.get("companyId").toString());
            g.setName(obj.get("name").toString());
            g.setIntroduction(obj.get("introduction") == null ? "" : obj.get("introduction").toString());
            g.setCreator(Integer.valueOf(obj.get("creator").toString()));
            g.setCreatorDate(Long.valueOf(obj.get("creatorDate").toString()));
            g.setUpdator(Integer.valueOf(obj.get("updator").toString()));
            g.setUpdatorDate(Long.valueOf(obj.get("updatorDate").toString()));
            g.setCertStatus(obj.get("certStatus") == null ? GroupCertStatus.noCert.getIndex() : obj.get("certStatus").toString());
            docIds.add(Integer.valueOf(obj.get("creator").toString()));
            gList.add(g);
        }

        BasicDBObject docin = new BasicDBObject();
        docin.put("$in", docIds);

        DBCursor usor = dsForRW.getDB().getCollection("user").find(new BasicDBObject("_id", docin)).sort(new BasicDBObject("updatorDate", -1)).skip(group.getStart()).limit(group.getPageSize());
        while (usor.hasNext()) {
            DBObject obj = usor.next();
            for (GroupVO g : gList) {
                if (g.getCreator().equals(Integer.valueOf(obj.get("_id").toString()))) {
                    g.setAdminName(MongodbUtil.getString(obj, "name"));
                    break;
                }
            }
        }
        PageVO page = new PageVO();
        page.setPageData(gList);
        page.setPageIndex(group.getPageIndex());
        page.setPageSize(group.getPageSize());
        page.setTotal(dsForRW.getDB().getCollection("c_group").count(query));

        return page;
    }

    @Override
    public Group getById(String id, Integer creator) {
        Query<Group> query = dsForRW.createQuery(Group.class);
        if (!StringUtil.isEmpty(id)) {
            query.field("_id").equal(new ObjectId(id));
        }
        if (null != creator) {
            query.field("creator").equal(creator);
        }
        return query.get();
    }

    public Group getById(String id) {
        if (null == id) {
            return null;
        }
        return dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(id.trim())).get();
    }

    public List<Group> getAllGroup() {
        Query<Group> query = dsForRW.createQuery(Group.class).filter("type", GroupType.group.getIndex());
        return query.asList();
    }

    public List<Group> getAllGroupForEs() {
        Query<Group> query = dsForRW.createQuery(Group.class)
                .filter("type", GroupType.group.getIndex())
                .filter("skip", GroupEnum.GroupSkipStatus.normal.getIndex())
                .filter("active", "active")
                .filter("applyStatus", "P");
        return query.asList();
    }

    @Override
    public List<Group> getAllGroup(String companyId) {
        return dsForRW.createQuery(Group.class).field("companyId").equal(companyId).asList();
    }

    @Override
    public Group getByName(String name) {
        return dsForRW.createQuery(Group.class).field("name").equal(name).get();
    }

    /**
     * 更新值班时间段
     *
     * @param groupId       集团Id
     * @param dutyStartTime 开始时间
     * @param dutyEndTime   结束时间
     * @return
     */
    public boolean updateDutyTime(String groupId, String dutyStartTime, String dutyEndTime) {

        // 修改

        DBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(groupId));

        DBObject update = new BasicDBObject();
        update.put("config.dutyStartTime", dutyStartTime);
        update.put("config.dutyEndTime", dutyEndTime);

        // wasAcknowledged() isUpdateOfExisting()
        WriteResult writeResult = dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set", update));
        if (writeResult != null && writeResult.isUpdateOfExisting()) {
            return true;
        }

        return false;
    }

    /**
     * 查全部集团
     *
     * @return
     */
    public List<Group> findAll() {

        return dsForRW.find(Group.class).asList();
    }

    @Override
    public boolean submitCert(String groupId, Integer doctorId, GroupCertification groupCert) {
        DBObject query = new BasicDBObject();
        DBObject update = new BasicDBObject();

        query.put("_id", new ObjectId(groupId));

        DBCollection dbColl = dsForRW.getDB().getCollection("c_group");
        DBObject obj = dbColl.findOne(query);
        if (obj != null) {
            String groupCertStatus = MongodbUtil.getString(obj, "certStatus");
            if (GroupCertStatus.noPass.getIndex().equals(groupCertStatus)
                    || GroupCertStatus.noCert.getIndex().equals(groupCertStatus)
                    || StringUtils.isBlank(groupCertStatus)) {
                update.put("certStatus", GroupCertStatus.auditing.getIndex());
            }
        }
        update.put("groupCert.companyName", groupCert.getCompanyName());
        update.put("groupCert.orgCode", groupCert.getOrgCode());
        update.put("groupCert.license", groupCert.getLicense());
        update.put("groupCert.corporation", groupCert.getCorporation());
        update.put("groupCert.businessScope", groupCert.getBusinessScope());
        update.put("groupCert.accountName", groupCert.getAccountName());
        update.put("groupCert.openBank", groupCert.getOpenBank());
        update.put("groupCert.bankAcct", groupCert.getBankAcct());
        update.put("groupCert.adminName", groupCert.getAdminName());
        update.put("groupCert.idNo", groupCert.getIdNo());
        update.put("groupCert.adminTel", groupCert.getAdminTel());
        update.put("groupCert.idImage", groupCert.getIdImage());
        update.put("groupCert.orgCodeImage", groupCert.getOrgCodeImage());
        update.put("groupCert.licenseImage", groupCert.getLicenseImage());
        update.put("groupCert.createTime", System.currentTimeMillis());
        update.put("groupCert.creator", doctorId);
        WriteResult writeResult = dbColl.update(query,
                new BasicDBObject("$set", update));
        if (writeResult != null && writeResult.isUpdateOfExisting()) {
            return true;
        }
        return false;
    }

    @Override
    public Query<Group> getGroupCerts(GroupCertParam param) {

        Query<Group> q = dsForRW.createQuery("c_group", Group.class).field("certStatus").equal(param.getStatus());
        if (param.getKeyword() != null && param.getKeyword().trim().length() > 0) {
            Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
            //搜索匹配集团名称或公司名称
            q.or(q.criteria("name").equal(pattern),
                    q.criteria("groupCert.companyName").equal(pattern));
        }
        //按创建时间排序  add by wangqiao
        return q.order("-groupCert.createTime");
    }

    public boolean updateRemarks(String groupId, String remarks) {
        DBObject query = new BasicDBObject();
        DBObject update = new BasicDBObject();

        query.put("_id", new ObjectId(groupId));

        update.put("groupCert.remarks", remarks);

        WriteResult writeResult = dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set", update));
        if (writeResult != null && writeResult.isUpdateOfExisting()) {
            return true;
        }
        return false;
    }

    public boolean passCert(String groupId, String companyId) {
        DBObject query = new BasicDBObject();
        DBObject update = new BasicDBObject();

        query.put("_id", new ObjectId(groupId));

        update.put("certStatus", GroupCertStatus.passed.getIndex());
        update.put("companyId", companyId);
        update.put("processVTime", System.currentTimeMillis());

        WriteResult writeResult = dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set", update));
        if (writeResult != null && writeResult.isUpdateOfExisting()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean noPass(String groupId) {
        DBObject query = new BasicDBObject();
        DBObject update = new BasicDBObject();

        query.put("_id", new ObjectId(groupId));

        update.put("certStatus", GroupCertStatus.noPass.getIndex());
        update.put("processVTime", System.currentTimeMillis());

        WriteResult writeResult = dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set", update));
        if (writeResult != null && writeResult.isUpdateOfExisting()) {
            return true;
        }
        return false;
    }

    @Override
    public Query<Group> retrievedGroups(String groupId, boolean include) {
        if (groupId == null) {
            throw new IllegalArgumentException("invalid argument: groupId is null");
        }
        GroupCertification cert = getById(groupId).getGroupCert();
        Query<Group> q = dsForRW.createQuery("c_group", Group.class);
        if (cert == null || cert.getOrgCode() == null) {
            //无认证资料或无组织机构代码，返回空记录
            q.field("groupCert.orgCode").equal("#!@$**!))");
        } else {
            q.field("groupCert.orgCode").equal(cert.getOrgCode());
        }
        if (!include) {
            q.field("_id").notEqual(new ObjectId(groupId));
        }
        return q;
    }

    @Override
    public void openService(String groupId) {
        DBObject update = new BasicDBObject();
        DBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(groupId));
        update.put("config.openConsultation", true);
        dsForRW.getDB().getCollection("c_group").update(query, new BasicDBObject("$set", update));
    }

    @Override
    public void removeConsultationPackDoctor(String groupId, Integer doctorId) {
        DBCollection dbCollection = dsForRW.getDB().getCollection("t_group_consultation_pack");
        DBObject query = new BasicDBObject("groupId", groupId);
        DBObject update = new BasicDBObject("$pull", new BasicDBObject("doctorIds", doctorId));
        dbCollection.updateMulti(query, update);
    }

    @Override
    public GroupApply insertgroupApply(GroupApply groupApply) {
        String id = dsForRW.insert(groupApply).getId().toString();

        return dsForRW.createQuery(GroupApply.class).field("_id").equal(new ObjectId(id)).get();
    }

    @Override
    public GroupApply getGroupApplyByApplyUserId(Integer applyUserId) {
        return dsForRW.createQuery(GroupApply.class).field("applyUserId").equal(applyUserId).get();
    }

    @Override
    public GroupApply getGroupApplyById(String id) {
        return dsForRW.createQuery(GroupApply.class).field("_id").equal(new ObjectId(id)).get();
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.dao.IGroupDao#getLastGroupApplyByGroupId(java.lang.String)
     */
    @Override
    public GroupApply getLastGroupApplyByGroupId(String id) {
        return dsForRW.createQuery(GroupApply.class).field("groupId").equal(id).order("-applyDate").get();
    }

    @Override
    public void updateGroupApply(GroupApply groupApply) {
        Query<GroupApply> query =
                dsForRW.createQuery(GroupApply.class).field("_id").equal(new ObjectId(groupApply.getId()));
        UpdateOperations<GroupApply> ops = dsForRW.createUpdateOperations(GroupApply.class);

        if (StringUtil.isNotBlank(groupApply.getName())) {
            ops.set("name", groupApply.getName());
        }

        if (StringUtil.isNotBlank(groupApply.getIntroduction())) {
            ops.set("introduction", groupApply.getIntroduction());
        }

        if (StringUtil.isNotBlank(groupApply.getLogoUrl())) {
            ops.set("logoUrl", groupApply.getLogoUrl());
        }

        if (StringUtil.isNotBlank(groupApply.getStatus())) {
            ops.set("status", groupApply.getStatus());
        }

        if (groupApply.getApplyDate() != null) {
            ops.set("applyDate", groupApply.getApplyDate());
        }

        if (groupApply.getAuditUserId() != null) {
            ops.set("auditUserId", groupApply.getAuditUserId());
        }

        if (groupApply.getAuditDate() != null) {
            ops.set("auditDate", groupApply.getAuditDate());
        }

        if (StringUtil.isNotBlank(groupApply.getAuditMsg())) {
            ops.set("auditMsg", groupApply.getAuditMsg());
        }

        dsForRW.updateFirst(query, ops);
    }


    @Override
    public List<GroupApply> getApplyList(String status, String groupName, List<Integer> doctorIds, List<String> groupIdList, Integer pageSize,
                                         Integer pageIndex) {
        Query<GroupApply> q =
                dsForRW.createQuery(GroupApply.class);
        if (StringUtils.isNotBlank(status)) {
            q.field("status").equal(status);
        }
        List<Criteria> cs = new ArrayList<>();
        if (doctorIds != null && doctorIds.size() > 0) {
            cs.add(q.criteria("applyUserId").in(doctorIds));
        }
        if (StringUtils.isNotBlank(groupName)) {
            cs.add(q.criteria("name").contains(groupName));
        }
        if (cs.size() > 0) {
            Criteria[] cArr = new Criteria[cs.size()];
            q.or(cs.toArray(cArr));
        }
        // 集团过滤
        if (groupIdList != null && groupIdList.size() > 0) {
            q.field("groupId").in(groupIdList);
        }
        q.offset(pageIndex * pageSize);
        q.limit(pageSize);
        q.order("-auditDate,-applyDate");
        return q.asList();
    }

    @Override
    public long getApplyListCount(String status, String groupName, List<Integer> doctorIds, List<String> groupIdList) {
        Query<GroupApply> q =
                dsForRW.createQuery(GroupApply.class);
        if (StringUtils.isNotBlank(status)) {
            q.field("status").equal(status);
        }
        List<Criteria> cs = new ArrayList<>();
        if (doctorIds != null && doctorIds.size() > 0) {
            cs.add(q.criteria("applyUserId").in(doctorIds));
        }
        if (StringUtils.isNotBlank(groupName)) {
            cs.add(q.criteria("name").contains(groupName));
        }
        if (cs.size() > 0) {
            Criteria[] cArr = new Criteria[cs.size()];
            q.or(cs.toArray(cArr));
        }
        // 集团过滤
        if (groupIdList != null && groupIdList.size() > 0) {
            q.field("groupId").in(groupIdList);
        }
        return q.countAll();
    }

    @Override
    public void insertGroupUserApply(GroupUserApply groupUserApply) {
        dsForRW.insert(groupUserApply);
    }

    @Override
    public void updateGroupUserApplyToExpire(String groupId) {
        Query<GroupUserApply> query = dsForRW.createQuery(GroupUserApply.class).field("groupId").equal(groupId);
        UpdateOperations<GroupUserApply> ops = dsForRW.createUpdateOperations(GroupUserApply.class);
        ops.set("status", GroupEnum.GroupTransfer.expire.getIndex());
        dsForRW.update(query, ops);
    }

    @Override
    public GroupUserApply getGroupUserApplyById(String groupUserApplyId) {
        return dsForRW.createQuery(GroupUserApply.class).field("_id").equal(new ObjectId(groupUserApplyId)).get();
    }

    @Override
    public void updateGroupUserApply(GroupUserApply groupUserApply) {
        Query<GroupUserApply> query =
                dsForRW.createQuery(GroupUserApply.class)
                        .field("_id").equal(new ObjectId(groupUserApply.getId()));
        UpdateOperations<GroupUserApply> ops = dsForRW.createUpdateOperations(GroupUserApply.class);

        if (groupUserApply.getConfirmDate() != null) {
            ops.set("confirmDate", groupUserApply.getConfirmDate());
        }

        if (StringUtils.isNotBlank(groupUserApply.getStatus())) {
            ops.set("status", groupUserApply.getStatus());
        }

        dsForRW.updateFirst(query, ops);
    }

    @Override
    public GroupUserApply getTransferInfo(String groupUserApplyId) {
        return dsForRW.createQuery(GroupUserApply.class).field("_id").equal(new ObjectId(groupUserApplyId)).get();
    }

    @Override
    public void updateGroupApplyImageUrl(GroupApply groupApply) {
        Query<GroupApply> query =
                dsForRW.createQuery(GroupApply.class)
                        .field("_id").equal(new ObjectId(groupApply.getId()));
        UpdateOperations<GroupApply> ops = dsForRW.createUpdateOperations(GroupApply.class);

        if (StringUtils.isNotBlank(groupApply.getLogoUrl())) {
            ops.set("logoUrl", groupApply.getLogoUrl());
        }

        dsForRW.updateFirst(query, ops);
    }


    @Override
    public Group saveGroupAtAuditPass(Group group) {
        DBObject doc = new BasicDBObject();

        doc.put("_id", new ObjectId(group.getId()));
        doc.put("creator", group.getCreator());
        doc.put("creatorDate", group.getCreatorDate());
        doc.put("introduction", group.getIntroduction());
        doc.put("name", group.getName());
        doc.put("certStatus", group.getCertStatus());
        doc.put("active", group.getActive());
        dsForRW.getDB().getCollection("c_group").insert(doc);

        return dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(group.getId())).get();
    }


    @Override
    public void updateGroupProfit(Group group) {
        GroupConfig config = group.getConfig();
        Query<Group> query =
                dsForRW.createQuery(Group.class)
                        .field("_id").equal(new ObjectId(group.getId()));
        UpdateOperations<Group> ops = dsForRW.createUpdateOperations(Group.class);

        if (config.getTextGroupProfit() != null) {
            ops.set("config.textGroupProfit", config.getTextGroupProfit());
        }

        if (config.getTextParentProfit() != null) {
            ops.set("config.textParentProfit", config.getTextParentProfit());
        }

        if (config.getPhoneGroupProfit() != null) {
            ops.set("config.phoneGroupProfit", config.getPhoneGroupProfit());
        }

        if (config.getPhoneParentProfit() != null) {
            ops.set("config.phoneParentProfit", config.getPhoneParentProfit());
        }

        if (config.getCarePlanGroupProfit() != null) {
            ops.set("config.carePlanGroupProfit", config.getCarePlanGroupProfit());
        }

        if (config.getCarePlanParentProfit() != null) {
            ops.set("config.carePlanParentProfit", config.getCarePlanParentProfit());
        }

        if (config.getClinicGroupProfit() != null) {
            ops.set("config.clinicGroupProfit", config.getClinicGroupProfit());
        }

        if (config.getClinicParentProfit() != null) {
            ops.set("config.clinicParentProfit", config.getClinicParentProfit());
        }
        //添加会诊抽成比例
        if (config.getConsultationGroupProfit() != null) {
            ops.set("config.consultationGroupProfit", config.getConsultationGroupProfit());
        }

        if (config.getConsultationParentProfit() != null) {
            ops.set("config.consultationParentProfit", config.getConsultationParentProfit());
        }

        if (config.getChargeItemGroupProfit() != null) {
            ops.set("config.chargeItemGroupProfit", config.getChargeItemGroupProfit());
        }

        if (config.getChargeItemParentProfit() != null) {
            ops.set("config.chargeItemParentProfit", config.getChargeItemParentProfit());
        }

        if (config.getAppointmentGroupProfit() != null) {
            ops.set("config.appointmentGroupProfit", config.getAppointmentGroupProfit());
        }

        if (config.getAppointmentParentProfit() != null) {
            ops.set("config.appointmentParentProfit", config.getAppointmentParentProfit());
        }

        dsForRW.updateFirst(query, ops);
    }

    @Override
    public GroupDoctor checkDoctor(Integer doctorId) {
        Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class).filter("doctorId", doctorId).filter("status", "C").filter("type", GroupType.hospital.getIndex());
        return query.get();
    }

    /**
     * 读取医生所在的科室信息
     */
    @Override
    public GroupDoctor checkDept(Integer doctorId) {
        Query<GroupDoctor> query = dsForRW.createQuery(GroupDoctor.class).filter("doctorId", doctorId).filter("status", "C").filter("type", GroupType.dept.getIndex());
        return query.get();
    }


    @Override
    public PageVO groupHospitalList(GroupParam param) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<Group> q = dsForRW.createQuery(Group.class).filter("type", GroupType.hospital.getIndex());
        //按照关键字查询
        if (!StringUtil.isEmpty(param.getKeyWord())) {
            Pattern pattern = Pattern.compile("^.*" + param.getKeyWord() + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("name").equal(pattern));
        }
        q.offset((param.getPageIndex()) * param.getPageSize());
        q.order("-creatorDate");
        q.limit(param.getPageSize());
        List<Group> groupList = q.asList();
        for (Group group : groupList) {
            //根据集团id查询管理员信息
            GroupUser gUser = companyUserDao.getRootGroupManage(group.getId());
            User user=new User();
            if(gUser!=null) {
                user = userManager.getUser(gUser.getDoctorId());
            }

            HospitalPO hospital = baseDataDao.getHospitalDetail(group.getHospitalId());//医院基本信息
            if (hospital == null) {
                continue;
            }
            Area province = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
            Area city = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
            Area country = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("hospitalName", group.getName());
            map.put("adminName", user.getName());
            map.put("telephone", user.getTelephone());
            map.put("province", province == null ? "" : province.getName());
            map.put("city", city == null ? "" : city.getName());
            map.put("country", country == null ? "" : country.getName());
            map.put("creatorDate", group.getCreatorDate());
            map.put("id", group.getId());
            list.add(map);
        }
        PageVO page = new PageVO();
        if (param.getPageIndex() >= 0) {
            page.setPageIndex(param.getPageIndex());
        }
        if (param.getPageSize() >= 0) {
            page.setPageSize(param.getPageSize());
        }
        page.setTotal(Long.valueOf(groupHospitalListCount(param)));
        page.setPageData(list);
        return page;
    }


    public int groupHospitalListCount(GroupParam param) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<Group> q = dsForRW.createQuery(Group.class).filter("type", GroupType.hospital.getIndex());
        //按照关键字查询
        if (!StringUtil.isEmpty(param.getKeyWord())) {
            Pattern pattern = Pattern.compile("^.*" + param.getKeyWord() + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("name").equal(pattern));
        }
        q.order("-creatorDate");
        List<Group> groupList = q.asList();
        for (Group group : groupList) {
            //根据集团id查询管理员信息
            GroupUser gUser = companyUserDao.getRootGroupManage(group.getId());
            User user=new User();
            if(gUser!=null) {
                 user = userManager.getUser(gUser.getDoctorId());
            }

            HospitalPO hospital = baseDataDao.getHospitalDetail(group.getHospitalId());//医院基本信息
            if (hospital == null) {
                continue;
            }
            Area province = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
            Area city = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
            Area country = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("hospitalName", group.getName());
            map.put("adminName", user.getName());
            map.put("telephone", user.getTelephone());
            map.put("province", province == null ? "" : province.getName());
            map.put("city", city == null ? "" : city.getName());
            map.put("country", country == null ? "" : country.getName());
            map.put("creatorDate", group.getCreatorDate());
            map.put("id", group.getId());
            list.add(map);
        }
        return list.size();
    }

    @Override
    public Group checkHospital(String hospitalId) {
        Query<Group> query = dsForRW.createQuery(Group.class).filter("hospitalId", hospitalId).filter("type", GroupType.hospital.getIndex());
        return query.get();
    }

    @Override
    public void activeGroup(String groupId) {
        Query<Group> query = dsForRW.createQuery(Group.class).field("id").equal(new ObjectId(groupId));
        UpdateOperations<Group> ops = dsForRW.createUpdateOperations(Group.class);
        ops.set("active", GroupEnum.GroupActive.active.getIndex());
        dsForRW.updateFirst(query, ops);
    }

    @Override
    public Map<String, Object> getDetailByGroupId(String id) {
        Query<Group> query = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(id));
        if (null == query.get()) return null;
        Map<String, Object> map = new HashMap<String, Object>();
        Group group = query.get();
        //根据集团id查询管理员信息
        GroupUser gUser = companyUserDao.getRootGroupManage(group.getId());
        if (gUser == null) {
            throw new ServiceException("根据集团id查询到的集团用户信息已经不存在：" + group.getId());
        }
        User user = userManager.getUser(gUser.getDoctorId());
        if (user == null) {
            throw new ServiceException("根据医生id查询到的用户信息已经不存在：" + gUser.getDoctorId());
        }
        HospitalPO hospital = baseDataDao.getHospitalDetail(group.getHospitalId());//医院基本信息
        if (hospital == null) {
            throw new ServiceException("根据医院id查询到的医院信息已经不存在：" + group.getHospitalId());
        }
        Area province = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
        Area city = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
        Area country = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
        map.put("id", id);
        map.put("hospitalId", group.getHospitalId());
        map.put("type", group.getType());
        map.put("adminName", user.getName());
        map.put("telephone", user.getTelephone());
        map.put("province", province == null ? "" : province.getName());
        map.put("city", city == null ? "" : city.getName());
        map.put("country", country == null ? "" : country.getName());
        map.put("creatorDate", group.getCreatorDate());
        map.put("username", user.getName());
        map.put("HospitalName", hospital.getName());
        map.put("createDate", group.getCreatorDate());
        return map;
    }

    @Override
    public void updateAppointment(String groupId, Boolean openAppointment, Integer appointmentGroupProfit,
                                  Integer appointmentParentProfit) {
        Query<Group> q = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(groupId));
        UpdateOperations<Group> ops = dsForRW.createUpdateOperations(Group.class);
        ops.set("config.appointmentGroupProfit", appointmentGroupProfit);
        ops.set("config.appointmentParentProfit", appointmentParentProfit);
        ops.set("config.openAppointment", openAppointment);
        dsForRW.update(q, ops);
    }

    /* (non-Javadoc)
     * @see com.dachen.health.group.group.dao.IGroupDao#getGroupListByType(java.lang.String)
     */
    @Override
    public List<Group> getGroupListByType(String type) {
        if (type == null) {
            throw new ServiceException("参数type不能为空");
        }
        if (!type.equals(GroupType.hospital.getIndex()) && !type.equals(GroupType.group.getIndex())) {
            throw new ServiceException("参数type内容不正确");
        }

        return dsForRW.createQuery(Group.class).filter("type", type).asList();
    }

    @Override
    public String getAppointmentGroupId() {
        Group g = dsForRW.createQuery(Group.class).field("config.openAppointment").equal(true).get();
        if (g != null) {
            return g.getId();
        }
        return null;
    }

    @Override
    public void setGroupHospital(GroupParam param) {
//		Group g = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(param.getId())).get();
        Query<Group> q = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(param.getId()));
        UpdateOperations<Group> ops = dsForRW.createUpdateOperations(Group.class);
        ops.set("config.hospitalInfo", param.getConfig().getHospitalInfo());
        dsForRW.update(q, ops);
//		if (g.getConfig() == null) {
//			g.setConfig(new GroupConfig());
//		}
//		g.getConfig().setHospitalInfo(param.getConfig().getHospitalInfo());
//		dsForRW.save(g);
    }

    @Override
    public PageVO getGroupByKeyword(String keyword, String applyStatus, int pageIndex, int pageSize) {
        DBObject query = new BasicDBObject();

        if (StringUtils.isNotEmpty(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            query.put("name", pattern);
        }
        BasicDBList list = new BasicDBList();
        DBObject hospital = new BasicDBObject("type", "hospital");
        list.add(hospital);
        DBObject group = new BasicDBObject("type", "group");
        if (StringUtils.isNotEmpty(applyStatus)) {
            group.put("applyStatus", applyStatus);
        }
        list.add(group);
        query.put("$or", list);
        //查询所有正常状态的集团（2016-6-4傅永德）
        query.put("skip", GroupEnum.GroupSkipStatus.normal.getIndex());

        PageVO pageParam = new PageVO();
        pageParam.setPageIndex(pageIndex);
        pageParam.setPageSize(pageSize);

        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query).skip(pageParam.getStart()).limit(pageParam.getPageSize());
        long count = dsForRW.getDB().getCollection("c_group").count(query);
        List<GroupVO> gList = new ArrayList<GroupVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            GroupVO g = new GroupVO();
            g.setId(obj.get("_id").toString());
            g.setName(obj.get("name").toString());
            gList.add(g);
        }

        PageVO page = new PageVO();
        page.setPageData(gList);
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setTotal(count);

        return page;
    }

    /**
     * 根据条件查询集团列表
     *
     * @param group
     * @return List<Group>
     * @author tan.yf
     * @date 2016年6月2日
     */
    @Override
    public List<Group> getGroupList(GroupsParam group) {
        Query<Group> q = dsForRW.createQuery(Group.class);
        if (group != null) {
            if (StringUtil.isNotBlank(group.getActive())) {// 集团激活状态
                q.field("active").equal(group.getActive());
            }
            if (StringUtil.isNotBlank(group.getSkip())) {// 集团屏蔽状态
                q.field("skip").equal(group.getSkip());
            }
            if (StringUtil.isNotBlank(group.getName())) {// 集团屏蔽状态
                q.field("name").contains(group.getName());
            }
            if (StringUtils.isNotEmpty(group.getType())) {
                q.field("type").equal(group.getType());
            }
            if (!CollectionUtils.isEmpty(group.getGroupIds())) {// 集团ID列表
                List<ObjectId> values = new ArrayList<>();
                for (String str : group.getGroupIds()) {
                    values.add(new ObjectId(str));
                }
                q.field("_id").in(values);
            }
        }
        return q.asList();
    }

    @Override
    public List<String> getIdListBySkipAndType(GroupSkipStatus skipStatus, GroupEnum.GroupType groupType) {
        if (null == skipStatus || null == groupType) {
            return null;
        }

        Query<Group> q = dsForRW.createQuery(Group.class);
        q.field("skip").equal(skipStatus.getIndex());
        q.field("type").equal(groupType.getIndex());
        q.retrievedFields(true, MongoOperator.ID);

        List<Group> groupList = q.asList();
        if (CollectionUtils.isEmpty(groupList)) {
            return null;
        }

        List<String> groupIdList = groupList.stream().map(o -> o.getId().toString()).collect(Collectors.toList());

        return groupIdList;
    }

    /**
     * 获取全部已经屏蔽的集团
     *
     * @return
     */
    @Override
    public List<Group> getSkipGroups() {
        Query<Group> q = dsForRW.createQuery(Group.class);
        q.filter("skip", GroupEnum.GroupSkipStatus.skip.getIndex());
        return q.asList();
    }

    /**
     * 获取全部已经屏蔽的集团的id
     *
     * @return
     */
    @Override
    public List<String> getSkipGroupIds() {
        Query<Group> q = dsForRW.createQuery(Group.class)
                .filter("skip", GroupEnum.GroupSkipStatus.skip.getIndex())
                .retrievedFields(true, Mapper.ID_KEY);


        List<String> groupIdList = Lists.newArrayList();
        Iterator<Group> iter = q.fetch();
        while (iter.hasNext()) {
            Group group = iter.next();
            groupIdList.add(group.getId());
        }

        return groupIdList;
    }

    @Override
    public List<String> findOfflineHospitalIdByKeyword(String groupId, String keyWord) {
        Query<Group> q = dsForRW.createQuery(Group.class);
        q.field("config.openAppointment").equal(true);
        if (StringUtils.isNotBlank(groupId)) {
            q.field("_id").equal(new ObjectId(groupId));
        }

        if (StringUtils.isNoneBlank(keyWord)) {
            q.field("config.hospitalInfo.name").containsIgnoreCase(keyWord);
        }
        q.retrievedFields(true, "config.hospitalInfo.id");
        Group g = q.get();
        List<String> hospitalIds = new ArrayList<String>();
        if (g != null
                && g.getConfig() != null
                && g.getConfig().getHospitalInfo() != null) {
            List<HospitalInfo> infos = g.getConfig().getHospitalInfo();
            for (HospitalInfo h : infos) {
                hospitalIds.add(h.getId());
            }
        }
        return hospitalIds;
    }

    @Override
    public List<GroupDoctorVO> getGroupDoctorListByGroupId(String groupId) {
        return dsForRW.createQuery("c_group_doctor", GroupDoctorVO.class).filter("groupId", groupId)
                .filter("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex()).asList();
    }

    @Override
    public List<Group> searchGroupsByName(String groupName, Integer pageIndex, Integer pageSize) {

        List<Group> result = Lists.newArrayList();

        DBObject queryGroup = new BasicDBObject();
        if (null != groupName) {
            BasicDBList keyword = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + groupName + ".*$", Pattern.CASE_INSENSITIVE);
            keyword.add(new BasicDBObject("name", pattern));
            queryGroup.put(QueryOperators.OR, keyword);
        }

        queryGroup.put("active", GroupActive.active.getIndex());
        queryGroup.put("skip", GroupSkipStatus.normal.getIndex());
        queryGroup.put("applyStatus", GroupEnum.GroupApply.pass.getIndex());

        DBCursor gCursor;
        if (pageIndex == null && pageIndex == null) {
            gCursor = dsForRW.getDB().getCollection("c_group").find(queryGroup);
        } else {
            gCursor = dsForRW.getDB().getCollection("c_group").find(queryGroup).skip(pageIndex * pageSize).limit(pageSize);
        }

        while (gCursor.hasNext()) {
            Group group = new Group();
            DBObject object = gCursor.next();
            group.setId(MongodbUtil.getString(object, "_id"));
            group.setActive(MongodbUtil.getString(object, "active"));
            group.setSkip(MongodbUtil.getString(object, "skip"));
            group.setLogoUrl(MongodbUtil.getString(object, "logoUrl"));
            group.setName(MongodbUtil.getString(object, "name"));
            result.add(group);
        }

        return result;
    }

    @Override
    public List<RecommendGroupVO> getAllRecommendGroups() {
        return dsForRW.createQuery(RecommendGroupVO.class).order("-weight").asList();
    }

    @Override
    public List<RecommendGroupVO> getRecommenGroups() {
        Query<RecommendGroupVO> query = dsForRW.createQuery(RecommendGroupVO.class).order("-weight");
        return query.asList();
    }

    @Override
    public void saveRecommendGroup(RecommendGroupVO vo) {
        dsForRW.save(vo);
    }

    @Override
    public void removeGroupRecommended(String groupId) {
        BasicDBList values = new BasicDBList();
        BasicDBObject in = new BasicDBObject();
        values.add(groupId);
        in.put("$in", values);
        dsForRW.getDB().getCollection("t_group_recommend").remove(new BasicDBObject("_id", in));
    }

    @Override
    public List<Group> getGroupsListByIds(List<String> groupIds) {

        List<Group> result = Lists.newArrayList();

        BasicDBList ids = new BasicDBList();
        BasicDBObject query = new BasicDBObject();
        if (groupIds != null && groupIds.size() > 0) {
            for (String id : groupIds) {
                ids.add(new ObjectId(id));
            }
        }

        DBObject groupIdQuery = new BasicDBObject();
        groupIdQuery.put("$in", ids);
        query.put("_id", groupIdQuery);

        DBCursor dbCursor = dsForRW.getDB().getCollection("c_group").find(query);
        while (dbCursor.hasNext()) {
            DBObject obj = dbCursor.next();
            Group group = new Group();
            group.setId(MongodbUtil.getString(obj, "_id"));
            group.setName(MongodbUtil.getString(obj, "name"));
            group.setLogoUrl(MongodbUtil.getString(obj, "logoUrl"));
            group.setCureNum(MongodbUtil.getInteger(obj, "cureNum"));
            group.setIntroduction(MongodbUtil.getString(obj, "introduction"));
            group.setCertStatus(MongodbUtil.getString(obj, "certStatus"));
            List<String> diseaseList = Lists.newArrayList();

            BasicDBList bdBasicDBList = (BasicDBList) obj.get("diseaselist");
            if (bdBasicDBList != null) {
                Iterator<Object> iterator = bdBasicDBList.iterator();
                while (iterator.hasNext()) {
                    Object object = iterator.next();
                    diseaseList.add((String) object);
                }
                group.setDiseaselist(diseaseList);
            }
            result.add(group);
        }

        return result;
    }

    @Override
    public void updateGroupRecommended(RecommendGroupVO vo) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", vo.getId());
        BasicDBObject jo = new BasicDBObject();
        jo.put("weight", vo.getWeight());
        dsForRW.getDB().getCollection("t_group_recommend").update(query, new BasicDBObject("$set", jo));
    }

    @Override
    public Long getCountOfSearch(String groupName) {
        DBObject queryGroup = new BasicDBObject();
        if (null != groupName) {
            BasicDBList keyword = new BasicDBList();
            Pattern pattern = Pattern.compile("^.*" + groupName + ".*$", Pattern.CASE_INSENSITIVE);
            keyword.add(new BasicDBObject("name", pattern));
            queryGroup.put(QueryOperators.OR, keyword);
        }

        queryGroup.put("active", GroupActive.active.getIndex());
        queryGroup.put("skip", GroupSkipStatus.normal.getIndex());

        long count = dsForRW.getDB().getCollection("c_group").find(queryGroup).count();
        return count;
    }

    @Override
    public DBCursor searchByKeyword(Integer type, String keyword) {

        DBObject query = new BasicDBObject();
        if (StringUtils.isNotEmpty(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            query.put("name", pattern);
        }

        if (type == 1) {
            query.put("skip", GroupSkipStatus.normal.getIndex());
            query.put("active", GroupActive.active.getIndex());
            query.put("type", GroupType.group.getIndex());
            DBCursor dbCursor = dsForRW.getDB().getCollection("c_group").find(query);
            return dbCursor;
        } else if (type == 2) {
            query.put("userType", UserEnum.UserType.doctor.getIndex());
            query.put("status", UserStatus.normal.getIndex());
            DBCursor dbCursor = dsForRW.getDB().getCollection("user").find(query);
            return dbCursor;
        }

        return null;
    }

    @Override
    public void updateGroupGid(String groupId, String gid) {
        if (StringUtils.isNotBlank(groupId) && StringUtils.isNotBlank(gid)) {
            Query<Group> query = dsForRW.createQuery(Group.class).field("_id").equal(new ObjectId(groupId));
            UpdateOperations<Group> ops = dsForRW.createUpdateOperations(Group.class);
            ops.set("gid", gid);
            dsForRW.update(query, ops);
        }
    }

    @Override
    public List<Group> findByHospitalAndDept(String hospitalId, String deptId) {
        Query query = dsForRW.createQuery(Group.class);
        if (StringUtils.isNotBlank(hospitalId)) {
            query.field("hospitalId").equal(hospitalId);
        }
        if (StringUtils.isNotBlank(deptId)) {
            query.field("deptId").equal(deptId);
        }
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        return query.asList();
    }

    @Override
    public List<Group> findActiveDeptList() {
        Query<Group> query = dsForRW.createQuery(Group.class);
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        query.field("type").equal(GroupType.dept.getIndex());
        return query.asList();
    }

    @Override
    public List<Group> findActiveDeptListByKeyword(String keyword) {
        if(StringUtils.isBlank(keyword)) {
            return null;
        }
        Query<Group> query = dsForRW.createQuery(Group.class);
        Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
        query.field("skip").equal(GroupEnum.GroupSkipStatus.normal.getIndex());
        query.field("active").equal("active");
        query.field("type").equal(GroupType.dept.getIndex());
        query.filter("name", pattern);
        return query.asList();
    }

    @Override
    public void updateGroupApplyByGroupId(GroupApply groupApply) {

        if (StringUtil.isEmpty(groupApply.getGroupId())) {
            return;
        }


        Query<GroupApply> query =
                dsForRW.createQuery(GroupApply.class).field("groupId").equal(groupApply.getGroupId());

        GroupApply apply = query.get();

        UpdateOperations<GroupApply> ops = dsForRW.createUpdateOperations(GroupApply.class);

        if (StringUtil.isNotBlank(groupApply.getName())) {
            ops.set("name", groupApply.getName());
        }

        if (StringUtil.isNotBlank(groupApply.getIntroduction())) {
            ops.set("introduction", groupApply.getIntroduction());
        }

        if (StringUtil.isNotBlank(groupApply.getLogoUrl())) {
            ops.set("logoUrl", groupApply.getLogoUrl());
        }

        ops.set("updateTime", System.currentTimeMillis());

        dsForRW.update(query, ops);

    }

    @Override
    public PageVO findAllGroupExDept(String name,int pageIndex, int pageSize) {
        Query<Group> q=dsForRW.createQuery(Group.class).field("applyStatus").equal("P");
        if(StringUtil.isNotEmpty(name)) {
            Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
            q.filter("name", pattern);
        }
        q.field("type").notEqual("dept").field("active").equal("active");
        q.offset(pageIndex * pageSize);
        q.order("-creatorDate");
        q.limit(pageSize);
        List<Group> groups = q.asList();
        PageVO page = new PageVO();
        if (pageIndex >= 0) {
            page.setPageIndex(pageIndex);
        }
        if (pageSize >= 0) {
            page.setPageSize(pageSize);
        }
        page.setTotal(q.countAll());
        page.setPageData(groups);
        return page;
    }

    @Override
    public List<Group> findAllGroupExDept() {
        Query<Group> q=dsForRW.createQuery(Group.class).field("applyStatus").equal("P");
        q.field("type").notEqual("dept").field("active").equal("active");
        q.order("-creatorDate");
        List<Group> groups = q.asList();
        return groups;
    }
}
