package com.dachen.health.commons.dao.mongo;

import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.mongo.MongoOperator;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.constant.DataStatusEnum;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.base.dao.IdxRepository.idxType;
import com.dachen.health.base.entity.param.OpenDoctorParam;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.DoctorTitleVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.base.utils.UserUtil;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.example.UserQueryExample;
import com.dachen.health.commons.vo.*;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.user.UserInfoNotify;
import com.dachen.health.user.entity.param.AddAdminUserParam;
import com.dachen.health.user.entity.param.CustomCollegeParam;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.LearningExperienceParam;
import com.dachen.health.user.entity.po.Assistant;
import com.dachen.health.user.entity.po.CustomCollege;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.Doctor.Check;
import com.dachen.health.user.entity.po.LearningExperience;
import com.dachen.im.server.data.UserSoundVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository
public class UserRepositoryImpl extends NoSqlRepository implements UserRepository {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected IdxRepository idxRepository;

    @Autowired
    protected IBaseDataService baseDataService;

    @Resource
    protected DiseaseTypeRepository diseaseTypeRepository;

    @Resource
    protected IBaseDataDao baseDataDao;

    @Autowired
    protected Auth2Helper auth2Helper;

    @Override
    public Map<String, Object> addUser(int userId, UserExample example) {
        BasicDBObject jo = new BasicDBObject();
        jo.put("_id", userId);// 索引

        jo.put("username", "");

        jo.put("userType", ValueUtil.parse(example.getUserType()));

        jo.put("telephone", example.getTelephone());// 索引

        jo.put("nickname", ValueUtil.parse(example.getNickname()));
        // 新用户默认不挂起 add by xuhuanjie
        jo.put("suspend", 0);

        if (StringUtils.isNotEmpty(example.getName())) {
            jo.put("name", ValueUtil.parse(example.getName()));// 索引
        } else {
            // App注册医生，如果没有填姓名，则默认姓名为“未命名” 与学明，老屈确认过了。edit by zl @2017-10-23
            if (example.getUserType() == UserType.doctor.getIndex()) {
                jo.put("name", UserConstant.DEFAULT_DOCTOR_NAME);
            }
        }

        //设置用户是否需要重置密码（2016-05-16傅永德）
        if (null != example.getNeedResetPassword()) {
            jo.put("needResetPassword", example.getNeedResetPassword());
        }

        //设置用户的来源（2016-05-16傅永德）
        if (null != example.getUserSource()) {
            Map<String, Object> source = Maps.newHashMap();
            if (null != example.getUserSource().getSourceType()) {
                source.put("sourceType", example.getUserSource().getSourceType());
            }
            if (null != example.getUserSource().getInviterId()) {
                source.put("inviterId", example.getUserSource().getInviterId());
            }
            if (null != example.getUserSource().getTerminal()) {
                source.put("terminal", example.getUserSource().getTerminal());
            }
            if (null != example.getUserSource().getGroupId()) {
                source.put("groupId", example.getUserSource().getGroupId());
            }
            if (null != example.getUserSource().getAppId()) {
                source.put("appId", example.getUserSource().getAppId());
            }
            jo.put("source", source);
        }

        if (null != example.getFarm()) {
            Map<String, Object> farm = Maps.newHashMap();
            if (null != example.getFarm().getNeedResetPhoneAndPass()) {
                farm.put("needResetPhoneAndPass", example.getFarm().getNeedResetPhoneAndPass());
            }
            if (null != example.getFarm().getIsFarm()) {
                farm.put("isFarm", example.getFarm().getIsFarm());
            }
            jo.put("farm", farm);
        }

        jo.put("createTime", System.currentTimeMillis());//创建时间
        jo.put("modifyTime", System.currentTimeMillis());//用户更新时间

        jo.put("isAuth", 0);

        jo.put("status", UserStatus.normal.getIndex());
        if (UserType.doctor.getIndex() == example.getUserType()) {
            jo.put("status", UserStatus.Unautherized.getIndex());
        } else if (UserType.nurse.getIndex() == example.getUserType()) {
            jo.put("status", UserStatus.Unautherized.getIndex());
        } else if (UserType.enterpriseUser.getIndex() == example.getUserType()) {
            jo.put("enterpriseId", example.getEnterpriseId());
            jo.put("modifyTime", new Date().getTime());//新增用户的时候 修改时间默认为新增时间
            String enterpriseName = example.getEnterpriseName();
            if (StringUtils.isNotEmpty(enterpriseName)) {
                jo.put("enterpriseName", enterpriseName);
            }
            Integer status = example.getStatus();
            if (null != status && status.intValue() != 0) {
                jo.put("status", status);//
            }
        } else if (UserType.DocGuide.getIndex() == example.getUserType()) {
            jo.put("status", UserStatus.normal.getIndex());
        }
        // 初始化登录日志
        jo.put("loginLog", User.LoginLog.init(example, true));
        // 初始化用户设置
        jo.put("settings", User.UserSettings.getDefault());

        jo.put("remarks", example.getRemarks());

        if (example.getUserType() != null && UserType.doctor.getIndex() == example.getUserType()) {
            String doctorNum = null;
            if (StringUtils.isNotEmpty(example.getDoctorNum())) {
                doctorNum = example.getDoctorNum();
            } else {
                doctorNum = idxRepository.nextDoctorNum(idxType.doctorNum);
            }
            Map<String, Object> doctor = new HashMap<String, Object>();
            doctor.put("doctorNum", doctorNum);
            doctor.put("serviceStatus", UserEnum.ServiceStatus.close.getIndex());
            jo.put("doctor", doctor);
        }
        
        //医生圈app注册的医生，初始化为游客身份；运营后台新建的医生，初始化为临时会员身份；
        Map<String,String> opeartorLog = new HashMap<>();
        opeartorLog.put("operationType", OperationLogTypeDesc.DOCOTORLEVELCHANGE);
        if (Objects.equals(example.getUserType(), UserType.doctor.getIndex())) {
            UserSource userSource = example.getUserSource();
            UserEnum.Source source = UserEnum.Source.getEnum(userSource.getSourceType());
            if (Objects.nonNull(userSource)) {
                if (Objects.equals(userSource.getSourceType(), UserEnum.Source.app.getIndex())) {
                    jo.put("userLevel", UserLevel.TemporaryUser.getIndex());
                    jo.put("limitedPeriodTime", UserEnum.FOREVER_LIMITED_PERIOD);//游客7天有效期
                    opeartorLog.put("content", String.format("(%1$s)通过%2$s注册为医生用户身份变更为-%3$s(%4$s)",
                            example.getTelephone(), Objects.nonNull(source) ? source.getSource() : "",
                            UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()), DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD, null)));
                } else if (Objects.equals(userSource.getSourceType(), UserEnum.Source.checkAdmin.getIndex())) {
                    jo.put("userLevel", UserLevel.TemporaryUser.getIndex());
                    // jo.put("limitedPeriodTime",
                    // System.currentTimeMillis()+UserEnum.TEMPUSER_LIMITED_PERIOD);
                    jo.put("limitedPeriodTime", UserEnum.FOREVER_LIMITED_PERIOD);
                    opeartorLog.put("content",
                            String.format("(%1$s)通过%2$s注册为医生用户身份变更为-%3$s(%4$s)", example.getTelephone(),
                                    Objects.nonNull(source) ? source.getSource() : "",
                                    UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()),
                                    DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
                } else if (Objects.equals(userSource.getSourceType(), UserEnum.Source.doctorCircleInviteJoin.getIndex())) {
                    jo.put("userLevel", UserLevel.TemporaryUser.getIndex());
                    // jo.put("limitedPeriodTime",
                    // System.currentTimeMillis()+UserEnum.TEMPUSER_LIMITED_PERIOD);
                    jo.put("limitedPeriodTime", UserEnum.FOREVER_LIMITED_PERIOD);
                    opeartorLog.put("content",
                            String.format("(%1$s)通过%2$s注册为医生用户身份变更为-%3$s(%4$s)", example.getTelephone(),
                                    Objects.nonNull(source) ? source.getSource() : "",
                                    UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()),
                                    DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
                } else {
                    if (!Objects.equals(userSource.getSourceType(), UserEnum.Source.checkAdminLot.getIndex())) {
                        jo.put("userLevel", UserLevel.TemporaryUser.getIndex());
                        jo.put("limitedPeriodTime", UserEnum.FOREVER_LIMITED_PERIOD);
                        opeartorLog.put("content", String.format("(%1$s)通过%2$s注册为医生用户身份变更为-%3$s(%4$s)",
                                example.getTelephone(), Objects.nonNull(source) ? source.getSource() : "",
                                UserEnum.UserLevel.getName(UserLevel.TemporaryUser.getIndex()), DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD, null)));
                    }
                }
            }
        }

        //设置User导医信息
        if (example.getUserType() != null && UserType.DocGuide.getIndex() == example.getUserType()) {
            Map<String, Object> doctorGuider = new HashMap<String, Object>();
            doctorGuider.put("groupId", example.getLoginGroupId());
            jo.put("doctorGuider", doctorGuider);
        }

        //绑定微信用户信息
        if (example.getWeUserInfo() != null) {
            addWeChatInfo(example.getWeUserInfo(), jo);
        }

        // 1、新增用户
        if (example.isAddUser()) {
            dsForRW.getDB().getCollection("user").insert(jo);
        } else {
            BasicDBObject query = new BasicDBObject();
            query.put("_id", userId);
            dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", jo));
        }
        try {
            // 2、缓存用户认证数据到
            String name = StringUtil.isBlank(jo.getString("name")) ? jo.getString("nickname") : jo.getString("name");
            User user = new User();
            user.setUserId(userId);
            user.setUserType(jo.getInt("userType"));
            user.setName(name);
            Map<String, Object> data = new HashMap<>();
            data.put("userId", userId);
            data.put("nickname", jo.getString("nickname"));
            data.put("name", jo.getString("name"));
            User userDB = getUser(userId);
            data.put("user", userDB);

            //注册用户之后，将用户的信息放入到redis里面
            updateUserSessionCache(userDB);
            if (example.getUserType() != null && UserType.doctor.getIndex() == example.getUserType()) {
                data.put("doctor", jo.get("doctor"));
            }
            if (example.getUserType() != null && UserType.DocGuide.getIndex() == example.getUserType()) {
                data.put("doctorGuider", jo.get("doctorGuider"));
            }
            return data;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("缓存用户数据失败", e);
        }
        
        /*if(!CollectionUtils.isEmpty(opeartorLog)&&Objects.equals(opeartorLog.size(),2)){
        	operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),opeartorLog.get("operationType"),opeartorLog.get("content"));
        }*/
        return null;
    }

    private void addWeChatInfo(WeChatUserInfo wechat, BasicDBObject jo) {
        Map<String, Object> weInfo = new HashMap<String, Object>();
        if (StringUtil.isNotBlank(wechat.getOpenid())) {
            weInfo.put("openid", wechat.getOpenid());
        }
        if (StringUtil.isNotBlank(wechat.getMpOpenid())) {
            weInfo.put("mpOpenid", wechat.getMpOpenid());
        }
        if (StringUtil.isNotBlank(wechat.getNickname())) {
            weInfo.put("nickname", wechat.getNickname());
        }
        if (StringUtil.isNotBlank(wechat.getProvince())) {
            weInfo.put("province", wechat.getProvince());
        }
        if (StringUtil.isNotBlank(wechat.getCity())) {
            weInfo.put("city", wechat.getCity());
        }
        if (StringUtil.isNotBlank(wechat.getCountry())) {
            weInfo.put("country", wechat.getCountry());
        }
        if (StringUtil.isNotBlank(wechat.getUnionid())) {
            weInfo.put("unionid", wechat.getUnionid());
        }
        jo.put("weInfo", weInfo);
    }

    /**
     * 更新微信用户基本信息
     *
     * @param userId
     * @param wechat
     */
    public void updateWeChatInfo(Integer userId, WeChatUserInfo wechat) {
        BasicDBObject query = new BasicDBObject();
        query.put("_id", userId);
        BasicDBObject ops = new BasicDBObject();
        if (StringUtil.isNotBlank(wechat.getOpenid())) {
            ops.put("weInfo.openid", wechat.getOpenid());
        }
        if (StringUtil.isNotBlank(wechat.getMpOpenid())) {
            ops.put("weInfo.mpOpenid", wechat.getMpOpenid());
        }
        if (StringUtil.isNotBlank(wechat.getNickname())) {
            ops.put("weInfo.nickname", wechat.getNickname());
        }
        if (StringUtil.isNotBlank(wechat.getProvince())) {
            ops.put("weInfo.province", wechat.getProvince());
        }
        if (StringUtil.isNotBlank(wechat.getCity())) {
            ops.put("weInfo.city", wechat.getCity());
        }
        if (StringUtil.isNotBlank(wechat.getCountry())) {
            ops.put("weInfo.country", wechat.getCountry());
        }
        if (StringUtil.isNotBlank(wechat.getUnionid())) {
            ops.put("weInfo.unionid", wechat.getUnionid());
        }
        ops.put("modifyTime", System.currentTimeMillis());
        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", ops));
    }

    @Override
    public void addUser(User user) {
        // 新用户默认不挂起 add by xuhuanjie
        user.setSuspend(0);
        dsForRW.save(user);
    }

    @Override
    public List<User> findByTelephone(List<String> telephoneList) {
        Query<User> query = dsForRW.createQuery(User.class).filter("telephone in", telephoneList);
        return query.asList();
    }

    @Override
    public long getCount(String telephone) {
        return dsForRW.createQuery(User.class).field("telephone").equal(telephone).countAll();
    }

    @Override
    public long getCount(String telephone, int userType) {
        return dsForRW.createQuery(User.class).field("telephone").equal(telephone).field("userType").equal(userType)
                .countAll();
    }

    @Override
    public User.LoginLog getLogin(int userId) {
        return dsForRW.createQuery(User.class).field("_id").equal(userId).get().getLoginLog();
    }

    @Override
    public User.UserSettings getSettings(int userId) {
        return dsForRW.createQuery(User.class).field("_id").equal(userId).get().getSettings();
    }

    public User getUserByUnionid(String unionid, Integer userType) {
        return dsForRW.createQuery(User.class).filter("userType", userType).field("weInfo.unionid").equal(unionid).get();
    }

    public List<User> findUsers(List<Integer> doctorIds) {
        List<User> data = new ArrayList<User>();

        if (doctorIds != null && !doctorIds.isEmpty()) {
            Query<User> query = dsForRW.createQuery(User.class).field("_id").in(doctorIds).field("status").equal(1)
                    .order("doctor.titleRank,-doctor.cureNum");
            data = query.asList();
        }

        return data;
    }

    public List<User> findUsersWithOutStatus(List<Integer> doctorIds) {
        List<User> data = new ArrayList<User>();

        if (doctorIds != null && !doctorIds.isEmpty()) {
            Query<User> query = dsForRW.createQuery(User.class).field("_id").in(doctorIds);
            data = query.asList();
        }

        return data;
    }

    public Query<User> findUserQuery(List<Integer> doctorIds, List<String> hospitalIds) {
        if (doctorIds == null || doctorIds.isEmpty()) {
            doctorIds.add(-1);
        }

        Query<User> query = dsForRW.createQuery(User.class).field("_id").in(doctorIds).field("status").equal(1);
        if (hospitalIds != null && hospitalIds.size() > 0) {
            query = query.field("doctor.hospitalId").in(hospitalIds);
        }
        return query.order("doctor.titleRank,-doctor.cureNum");
    }

    public Integer countUsers(List<Integer> doctorIds, Integer userStatus) {
        Integer countNum = 0;
        if (doctorIds != null && !doctorIds.isEmpty()) {
            Long count = dsForRW.createQuery(User.class).field("_id").in(doctorIds).field("status").equal(userStatus)
                    .countAll();
            countNum = Integer.valueOf(count + "");
        }
        return countNum;
    }

    public Integer countUsers2(List<Integer> doctorIds, Integer userStatus) {
        Integer countNum = 0;
        if (doctorIds != null && !doctorIds.isEmpty()) {
            Long count = dsForRW.createQuery(User.class).field("_id").in(doctorIds)
                    .field("status").equal(userStatus)
                    .field("doctor.hospitalId").notEqual(null)
                    .countAll();
            countNum = Integer.valueOf(count + "");
        }
        return countNum;
    }

    public List<User> findUsers(List<Integer> doctorIds, PageVO pageVO) {
        List<User> data = new ArrayList<User>();

        if (doctorIds != null && !doctorIds.isEmpty()) {
            Query<User> query = dsForRW.createQuery(User.class).field("_id").in(doctorIds).field("status").equal(1)
                    .order("doctor.titleRank,-doctor.cureNum");
            pageVO.setTotal(query.countAll());
            data = query.offset(pageVO.getPageIndex() * pageVO.getPageSize()).limit(pageVO.getPageSize()).asList();
        }
        return data;
    }

    public List<User> findUserList(List<Integer> doctorIds) {
        List<User> data = new ArrayList<User>();

        if (doctorIds != null && !doctorIds.isEmpty()) {
            Query<User> query = dsForRW.createQuery(User.class).field("_id").in(doctorIds).field("status").equal(1)
                    .order("doctor.titleRank,-doctor.cureNum");
            data = query.asList();
        }

        return data;
    }

    public List<CarePlanDoctorVO> findUserByDocs(List<Integer> doctorIds) {
        List<CarePlanDoctorVO> data = new ArrayList<CarePlanDoctorVO>();
        DBObject query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", doctorIds));
        query.put("status", 1);
        DBObject orderBy = new BasicDBObject();
        orderBy.put("doctor.titleRank", 1);
        orderBy.put("doctor.cureNum", -1);
        DBCollection collection = dsForRW.getDB().getCollection("user");
        DBCursor cursor = collection.find(query).sort(orderBy);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            CarePlanDoctorVO userInfo = new CarePlanDoctorVO();
            userInfo.setDoctorName(MongodbUtil.getString(obj, "name"));
            Integer userId = Integer.valueOf(obj.get("_id").toString());
            userInfo.setDoctorId(userId);
            User user = getUser(userId);
            if (user == null) {
                continue;
            }
            userInfo.setDoctorName(user.getName());
            userInfo.setHeadPicFileName(user.getHeadPicFileName());
            Doctor doctor = user.getDoctor();
            if (doctor != null) {
                userInfo.setCureNum(doctor.getCureNum());
                userInfo.setSkill(doctor.getSkill());
                userInfo.setDepartments(doctor.getDepartments());
                userInfo.setTitle(doctor.getTitle());
                userInfo.setDiseases(doctor.getExpertise());
            }
            data.add(userInfo);
        }
        return data;
    }

    @Override
    public List<CarePlanDoctorVO> findDoctorInfoGroup(List<Integer> doctorIds, Integer mainDoctorId) {
        List<CarePlanDoctorVO> vos = this.findUserByDocs(doctorIds, null);
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }

        for (CarePlanDoctorVO carePlanDoctorVO : vos) {
            List<String> diseaseIdList = carePlanDoctorVO.getDiseases();
            if (!CollectionUtils.isEmpty(diseaseIdList)) {
                String deseaseNames = diseaseTypeRepository.findNameByIds(diseaseIdList);
                carePlanDoctorVO.setDisease(deseaseNames);
                carePlanDoctorVO.setDiseases(null);
            }
            if (null != mainDoctorId && carePlanDoctorVO.getDoctorId().intValue() == mainDoctorId.intValue()) {
                carePlanDoctorVO.setGroupType(1);
            }
        }
        return vos;
    }

    @Override
    public List<User> findByHospitalAndDept(String hospitalId, String departmentId, List<Integer> withOutIds) {
        Query<User> query = dsForRW.createQuery(User.class);
        if (StringUtils.isNotBlank(hospitalId)) {
            query.field("doctor.hospitalId").equal(hospitalId);
        }
        if (StringUtils.isNotBlank(departmentId)) {
            query.field("doctor.deptId").equal(departmentId);
        }
        if (!CollectionUtils.isEmpty(withOutIds)) {
            query.field("_id").notIn(withOutIds);
        }
        return query.asList();
    }

    /**
     * 注意，此方法要与findUserByDocs保持一样的查询顺序
     *
     * @see #findUserByDocs(List, Integer)
     */
    @Override
    public List<User> findDoctorListByDoctorUserIds(List<Integer> doctorUserIds) {
        Query<User> query = dsForRW.createQuery(User.class)
                .field(MongoOperator.ID).in(doctorUserIds);
        query.order("doctor.titleRank, -doctor.cureNum");
        return query.asList();
    }

    @Override
    public List<CarePlanDoctorVO> findUserByDocs(List<Integer> doctorIds, Integer status) {
        List<CarePlanDoctorVO> data = new ArrayList<CarePlanDoctorVO>();
        DBObject query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", doctorIds));
        if (status != null)
            query.put("status", status);
        DBObject orderBy = new BasicDBObject();
        orderBy.put("doctor.titleRank", 1);
        orderBy.put("doctor.cureNum", -1);
        DBCollection collection = dsForRW.getDB().getCollection("user");
        DBCursor cursor = collection.find(query).sort(orderBy);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            CarePlanDoctorVO userInfo = new CarePlanDoctorVO();
            userInfo.setDoctorName(MongodbUtil.getString(obj, "name"));
            Integer userId = Integer.valueOf(obj.get("_id").toString());
            userInfo.setDoctorId(userId);
            User user = getUser(userId);
            if (user == null) {
                continue;
            }
            userInfo.setDoctorName(user.getName());
            userInfo.setHeadPicFileName(user.getHeadPicFileName());
            Doctor doctor = user.getDoctor();
            if (doctor != null) {
                userInfo.setCureNum(doctor.getCureNum());
                userInfo.setSkill(doctor.getSkill());
                userInfo.setDepartments(doctor.getDepartments());
                userInfo.setTitle(doctor.getTitle());
                userInfo.setDiseases(doctor.getExpertise());
            }
            data.add(userInfo);
        }
        return data;
    }

    @Override
    public User getUser(int userId) {
        Query<User> query = dsForRW.createQuery(User.class).field("_id").equal(userId);

        return query.get();
    }

    @Override
    public User getUserWithFields(int userId, String ... fields) {
        Query<User> query = dsForRW.createQuery(User.class).field("_id").equal(userId).retrievedFields(true, fields);

        return query.get();
    }

    @Override
    public List<User> getUsers(List<Integer> ids) {
        if (null == ids || 0 == ids.size()) {
            return new ArrayList<>();
        }
        Query<User> query = dsForRW.createQuery(User.class).field("_id").in(ids);
        return query.asList();
    }

    @Override
    public User getUser(String telephone) {
        Query<User> query = dsForRW.createQuery(User.class).field("telephone").equal(telephone);

        return query.get();
    }

    @Override
    public List<User> getUser(String telephone, List<Integer> userTypeList) {
        Query<User> query = dsForRW.createQuery(User.class).field("telephone").equal(telephone).filter("userType in", userTypeList);

        return query.asList();
    }

    @Override
    public User getUser(String telephone, Integer userType) {
        User user = getUserByTelAndType(telephone, userType);

//		if (user == null && UserType.doctor.getIndex() == userType) {
//			user = dsForRW.createQuery(User.class).filter("doctor.doctorNum", telephone).filter("userType", userType)
//					.get();
//		}

        return user;
    }

    @Override
    public User getUserByTelAndType(String telephone, Integer userType) {
        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("telephone is null");
        }
        if (userType == null) {
            throw new ServiceException("userType is null");
        }
        return dsForRW.createQuery(User.class).filter("telephone", telephone.trim()).filter("userType", userType).get();
    }

    @Override
    public List<User> getUserByNameAndTelephoneAndType(String name, String telephone, Integer userType) {
        if (userType == null) {
            throw new ServiceException("userType is null");
        }

        Query<User> query = dsForRW.createQuery(User.class).filter("userType", userType);
        if (!StringUtil.isEmpty(name)) {
            query = query.filter("name", name.trim());
        }
        if (!StringUtil.isEmpty(telephone)) {
            query = query.filter("telephone", telephone.trim());
        }
        return query.asList();
    }

    @Override
    public User getUser(String telephone, Integer userType, String password) {
        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("telephone is null");
        }
        if (userType == null) {
            throw new ServiceException("userType is null");
        }

        User user = dsForRW.createQuery(User.class).filter("telephone", telephone).filter("userType", userType)
                .filter("telephone", telephone).get();

//		if (user == null && UserType.doctor.getIndex() == userType) {
//			user = dsForRW.createQuery(User.class).filter("doctor.doctorNum", telephone).filter("userType", userType)
//					.get();
//		}

        return user;
    }

    @Override
    public User getUser(String telephone, Integer userType, Integer status) {
        if (StringUtil.isEmpty(telephone)) {
            throw new ServiceException("telephone is null");
        }
        if (userType == null) {
            throw new ServiceException("userType is null");
        }
        User user = dsForRW.createQuery(User.class).field("telephone").equal(telephone).field("userType")
                .equal(userType).field("status").equal(status).get();
        if (user == null && UserType.doctor.getIndex() == userType) {
            user = dsForRW.createQuery(User.class).field("doctor.doctorNum").equal(telephone).field("userType")
                    .equal(userType).field("status").equal(status).get();
        }
        return user;
    }

    @Override
    public User getUser(String userKey, String password) {
        Query<User> query = dsForRW.createQuery(User.class);
        BasicDBObject filter = new BasicDBObject();
        filter.put("userKey", userKey);
        filter.put("password", password);
        query.getCollection().find(null, filter);

        return query.get();
    }

    @Override
    public List<DBObject> queryUser(UserQueryExample example) {
        List<DBObject> list = Lists.newArrayList();
        DBObject ref = new BasicDBObject();
        if (null != example.getUserId())
            ref.put("_id", new BasicDBObject("$lt", example.getUserId()));
        if (!StringUtil.isEmpty(example.getNickname()))
            ref.put("name", Pattern.compile(example.getNickname()));
        if (null != example.getSex())
            ref.put("sex", example.getSex());
        if (null != example.getStartTime())
            ref.put("birthday", new BasicDBObject("$gte", example.getStartTime()));
        if (null != example.getEndTime())
            ref.put("birthday", new BasicDBObject("$lte", example.getEndTime()));
        DBObject fields = new BasicDBObject();
        fields.put("userKey", 0);
        fields.put("password", 0);
        fields.put("money", 0);
        fields.put("moneyTotal", 0);
        fields.put("status", 0);
        DBCursor cursor = dsForRW.getDB().getCollection("user").find(ref, fields).sort(new BasicDBObject("_id", -1))
                .limit(example.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            obj.put("userId", obj.get("_id"));
            obj.removeField("_id");

            list.add(obj);
        }

        return list;
    }

    @Override
    public void updateUserSessionCache(User user) {
        try {
            this.notifyUserUpdate(user);
            this.removeUserRedisCache(user.getUserId());
        } catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public void updateUserRedisCache(User user) {
        String userIdKey = KeyBuilder.userIdKey(user.getUserId());

        Map<String, String> map = this.jedisTemplate.hgetAll(userIdKey);
        if (map == null) {
            map = new HashMap<String, String>();
        }
        map.put("userId", String.valueOf(user.getUserId()));
        if (user.getUserType() != null && user.getUserType() != 0) {
            map.put("userType", String.valueOf(user.getUserType()));
        }
        if (StringUtil.isNotBlank(user.getName())) {
            map.put("name", user.getName());
        }
        if (user.getSex() != null) {
            map.put("sex", user.getSex() + "");
            map.put("headPicFileName", user.getHeadPicFileName());
        }
        if (StringUtil.isNotBlank(user.getHeadPicFileName())) {
            map.put("headPicFileName", user.getHeadPicFileName());
        }
        if (null != user.getSource() && null != user.getSource().getTerminal()) {
            map.put("terminal", String.valueOf(user.getSource().getTerminal()));
        }
        this.jedisTemplate.hmset(userIdKey, map);
    }

    public void removeUserRedisCache(Integer userId) {
        if (Objects.nonNull(userId)) {
            String userIdKey = KeyBuilder.userIdKey(userId);
            jedisTemplate.del(userIdKey);
        }
    }

    @Override
    public void updateLogin(int userId, String serial) {
        DBObject value = new BasicDBObject();
        // value.put("isFirstLogin", 0);
        // value.put("loginTime", DateUtil.currentTimeSeconds());
        // value.put("apiVersion", example.getApiVersion());
        // value.put("osVersion", example.getOsVersion());
        // value.put("model", example.getModel());
        value.put("serial", serial);
        // value.put("latitude", example.getLatitude());
        // value.put("longitude", example.getLongitude());
        // value.put("location", example.getLocation());
        // value.put("address", example.getAddress());

        DBObject q = new BasicDBObject("_id", userId);
        DBObject o = new BasicDBObject("$set", new BasicDBObject("loginLog", value));
        dsForRW.getDB().getCollection("user").update(q, o);
    }

    @Override
    public void
    updateLogin(int userId, UserExample example) {
        BasicDBObject loc = new BasicDBObject(2);
        loc.put("loc.lng", example.getLongitude());
        loc.put("loc.lat", example.getLatitude());

        DBObject values = new BasicDBObject();
        values.put("loginLog.isFirstLogin", 0);
        values.put("loginLog.loginTime", System.currentTimeMillis());
        values.put("loginLog.apiVersion", example.getApiVersion());
        values.put("loginLog.osVersion", example.getOsVersion());
        values.put("loginLog.model", example.getModel());
        values.put("loginLog.serial", example.getSerial());
        values.put("loginLog.latitude", example.getLatitude());
        values.put("loginLog.longitude", example.getLongitude());
        values.put("loginLog.location", example.getLocation());
        values.put("loginLog.address", example.getAddress());
        values.put("loc.lng", example.getLongitude());
        values.put("loc.lat", example.getLatitude());

        DBObject q = new BasicDBObject("_id", userId);
        DBObject o = new BasicDBObject("$set", values);
        dsForRW.getCollection(User.class).update(q, o);

    }

    @Override
    public User updateUser(int userId, UserExample example) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        User oldUser = q.get();
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

        boolean needUpdateCache = false;
        if (null != example.getUserType())
            ops.set("userType", example.getUserType());
        if (null != example.getCompanyId())
            ops.set("companyId", example.getCompanyId());

        if (!StringUtil.isEmpty(example.getNickname()))
            ops.set("nickname", example.getNickname());
        if (!StringUtil.isEmpty(example.getDescription()))
            ops.set("description", example.getDescription());
        if (null != example.getBirthday())
            ops.set("birthday", example.getBirthday());
        if (null != example.getSex()) {
            ops.set("sex", example.getSex());
            needUpdateCache = true;
        }
        if (null != example.getCountryId())
            ops.set("countryId", example.getCountryId());
        if (null != example.getProvinceId())
            ops.set("provinceId", example.getProvinceId());
        if (null != example.getCityId())
            ops.set("cityId", example.getCityId());
        if (null != example.getAreaId())
            ops.set("areaId", example.getAreaId());

        if (null != example.getName()) {
            ops.set("name", example.getName());
            needUpdateCache = true;
        }
        if (example.getHeadPicFileName() != null) {
            if (!example.getHeadPicFileName().contains("default")) {
                ops.set("headPicFileName", example.getHeadPicFileName());
                needUpdateCache = true;
            }
        }

        if (null != example.getIdcard())
            ops.set("idcard", example.getIdcard());
        if (null != example.getIdcardUrl())
            ops.set("idcardUrl", example.getIdcardUrl());

        if (null != example.getAge())
            ops.set("age", example.getAge());

        if (null != example.getArea())
            ops.set("area", example.getArea());

        if (Objects.nonNull(example.getWorkTime())) {
            ops.set("workTime", example.getWorkTime());
        }
        ops.set("loc.lng", example.getLongitude());
        ops.set("loc.lat", example.getLatitude());

        // 更新用户时，更新用户的来源（2016-05-26傅永德）
        if (null != example.getUserSource()) {
            Map<String, Object> source = Maps.newHashMap();
            if (null != example.getUserSource().getSourceType()) {
                source.put("sourceType", example.getUserSource().getSourceType());
            }
            if (null != example.getUserSource().getInviterId()) {
                source.put("inviterId", example.getUserSource().getInviterId());
            }
            if (null != example.getUserSource().getTerminal()) {
                source.put("terminal", example.getUserSource().getTerminal());
            }
            if (null != example.getUserSource().getGroupId()) {
                source.put("groupId", example.getUserSource().getGroupId());
            }
            ops.set("source", source);
        }

        if (null != example.getFarm()) {
            Map<String, Object> farm = Maps.newHashMap();
            if (null != example.getFarm().getNeedResetPhoneAndPass()) {
                farm.put("needResetPhoneAndPass", example.getFarm().getNeedResetPhoneAndPass());
            }
            if (null != example.getFarm().getIsFarm()) {
                farm.put("isFarm", example.getFarm().getIsFarm());
            }
            ops.set("farm", farm);
        }

        // 更新时间
        ops.set("modifyTime", System.currentTimeMillis());// 修改用户信息的时候 增加修改时间
        // 修改手机号码
        if (null != example.getTelephone()) {
            ops.set("telephone", example.getTelephone());
        }
        // 修改备注
        if (null != example.getRemarks()) {
            ops.set("remarks", example.getRemarks());
        }

        // 修改用户邮箱
        if (StringUtils.isNotBlank(example.getEmail())) {
            ops.set("email", example.getEmail());
        }

        if (UserType.patient.getIndex() == oldUser.getUserType()) {
            // 患者
        } else if (UserType.assistant.getIndex() == oldUser.getUserType()) {
            // 医助
            Assistant assistant = example.getAssistant();
            if (assistant != null) {
                if (assistant.getCompany() != null) {
                    ops.set("assistant.company", assistant.getCompany());
                }
                if (assistant.getDepartment() != null) {
                    ops.set("assistant.department", assistant.getDepartment());
                }
                if (assistant.getPosition() != null) {
                    ops.set("assistant.position", assistant.getPosition());
                }
                if (assistant.getArea() != null) {
                    ops.set("assistant.area", assistant.getArea());
                }
            }
        } else if (UserType.doctor.getIndex() == oldUser.getUserType()) {
            // 医生
            Doctor doctor = example.getDoctor();
            if (doctor != null) {
                if (oldUser.getDoctor() == null) {
                    String doctorNum = null;
                    if (StringUtils.isNotEmpty(example.getDoctorNum())) {
                        doctorNum = example.getDoctorNum();
                    } else {
                        doctorNum = idxRepository.nextDoctorNum(idxType.doctorNum);
                    }
                    ops.set("doctor.doctorNum", doctorNum);
                }
                if (doctor.getHospital() != null) {
                    ops.set("doctor.hospital", doctor.getHospital());
                }
                if (StringUtil.isNotBlank(doctor.getHospitalId())) {
                    ops.set("doctor.hospitalId", doctor.getHospitalId());
                } else if (StringUtil.isNotBlank(doctor.getHospital())) {
                    List<HospitalVO> hospitals = baseDataService.getHospitals(doctor.getHospital());
                    if (hospitals != null && hospitals.size() == 1) {
                        ops.set("doctor.hospitalId", hospitals.get(0).getId());
                    } else {
                        ops.set("doctor.hospitalId", "");
                    }
                }
                if (doctor.getDepartments() != null) {
                    ops.set("doctor.departments", doctor.getDepartments());
                }
                if (StringUtil.isNotBlank(doctor.getDeptId())) {
                    ops.set("doctor.deptId", doctor.getDeptId());
                }
                if (doctor.getTitle() != null) {
                    ops.set("doctor.title", doctor.getTitle());
                }
                if (doctor.getAssistantId() != null) {
                    ops.set("doctor.assistantId", doctor.getAssistantId());
                }
                if (doctor.getSkill() != null) {
                    ops.set("doctor.skill", doctor.getSkill());
                }
                if (doctor.getIntroduction() != null) {
                    ops.set("doctor.introduction", doctor.getIntroduction());
                }
                if (StringUtil.isNotBlank(doctor.getDeptPhone())) {
                    if (!VerificationUtil.checkTelephone(doctor.getDeptPhone())) {
                        throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
                    }
                    ops.set("doctor.deptPhone", doctor.getDeptPhone());
                }
            }
        }
        User user = dsForRW.findAndModify(q, ops);

        //任何时候对用户信息的修改，都要更新缓存（目前的策略是直接从缓存中删除）
        this.updateUserSessionCache(user);

        return user;
    }

    /**
     * 用户名为空打印堆栈
     * @param name
     */
    private void log(String name) {
        try {
            if (StringUtils.isBlank(name)) {
                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                logger.error("username is black... {}", printArray(stackTraceElements));
            }
        } catch (Exception e) {

        }
    }

    public String printArray(StackTraceElement[] a) {
        if (a == null)
            return "null";

        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder("\n");
        for (int i = 0; ; i++) {
            b.append(String.valueOf(a[i]));
            if (i == iMax)
                return b.toString();
            b.append("\n");
        }
    }

    public User updateUserNotChangeStatus(int userId, UserExample example) {

        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

        // 更新用户时，更新用户的来源（2016-05-26傅永德）
        if (null != example.getUserSource()) {
            Map<String, Object> source = Maps.newHashMap();
            if (null != example.getUserSource().getSourceType()) {
                source.put("sourceType", example.getUserSource().getSourceType());
            }
            if (null != example.getUserSource().getInviterId()) {
                source.put("inviterId", example.getUserSource().getInviterId());
            }
            if (null != example.getUserSource().getTerminal()) {
                source.put("terminal", example.getUserSource().getTerminal());
            }
            ops.set("source", source);
            ops.set("modifyTime", System.currentTimeMillis());
        }

        return dsForRW.findAndModify(q, ops);
    }

    @Override
    public User updateUser(User user) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(user.getUserId());
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

        if (!StringUtil.isNullOrEmpty(user.getTelephone())) {
            // ops.set("userKey", DigestUtils.md5Hex(user.getTelephone()));
            ops.set("telephone", user.getTelephone());
        }
        if (!StringUtil.isNullOrEmpty(user.getUsername()))
            ops.set("username", user.getUsername());
        if (!StringUtil.isNullOrEmpty(user.getPassword()))
            ops.set("password", user.getPassword());

        if (null != user.getUserType())
            ops.set("userType", user.getUserType());

        if (!StringUtil.isNullOrEmpty(user.getName()))
            ops.set("name", user.getName());

        if (!StringUtil.isNullOrEmpty(user.getDescription()))
            ops.set("description", user.getDescription());
        if (null != user.getBirthday())
            ops.set("birthday", user.getBirthday());
        if (null != user.getSex())
            ops.set("sex", user.getSex());
        if (null != user.getRename()) {
            ops.set("rename", user.getRename());
        }

		/*
         * if (null != user.getCountryId()) ops.set("countryId",
		 * user.getCountryId());
		 */

		/*
         * if (null != user.getAreaId()) ops.set("areaId", user.getAreaId());
		 */

        // if (null != user.getFriendsCount())
        // ops.set("friendsCount", user.getFriendsCount());
        // if (null != user.getFansCount())
        // ops.set("fansCount", user.getFansCount());
        // if (null != user.getAttCount())
        // ops.set("attCount", user.getAttCount());

        // ops.set("createTime", null);
        ops.set("modifyTime", System.currentTimeMillis());

        if (null != user.getStatus())
            ops.set("status", user.getStatus());

        if (null != user.getSuspend()) {
            ops.set("suspend", user.getSuspend());
        }
        if (Objects.nonNull(user.getSuspendInfo())) {
            ops.set("suspendInfo", user.getSuspendInfo());
        }
        return dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updatePassword(String telephone, String password) {
        throw new ServiceException("修改密码接口调用错误，请联系后台开发人员！");
//		Query<User> q = dsForRW.createQuery(User.class).field("userKey").equal(Md5Util.md5Hex(telephone));
//		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
//		ops.set("password", Md5Util.md5Hex(password));
//		dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updatePassowrd(int userId, String password) {
        throw new ServiceException("修改密码接口调用错误，请联系后台开发人员！");
//		Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
//		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
//		ops.set("password", Md5Util.md5Hex(password));
//		dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updateTel(int userId, String telephone) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("telephone", telephone);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public User updateDoctor(int userId, Doctor param) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        User oldUser = q.get();
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("modifyTime", System.currentTimeMillis());
        if (UserType.doctor.getIndex() == oldUser.getUserType()) {
            // 医生
            if (param != null) {
                if (oldUser.getDoctor() == null) {
                    String doctorNum = idxRepository.nextDoctorNum(idxType.doctorNum);
                    ops.set("doctor.doctorNum", doctorNum);
                }
                if (param.getHospital() != null) {
                    ops.set("doctor.hospital", param.getHospital());
                }
                if (param.getHospitalId() != null) {
                    ops.set("doctor.hospitalId", param.getHospitalId());
                }
                if (param.getDepartments() != null) {
                    ops.set("doctor.departments", param.getDepartments());
                }
                if (param.getTitle() != null) {
                    ops.set("doctor.title", param.getTitle());
                }
                if (StringUtil.isNotBlank(param.getDeptPhone())) {
                    if (!VerificationUtil.checkTelephone(param.getDeptPhone())) {
                        throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
                    }
                    ops.set("doctor.deptPhone", param.getDeptPhone());
                }
            }
        }

        User user = dsForRW.findAndModify(q, ops);

        return user;
    }

    public boolean updateDoctorHospital(int userId, HospitalVO hospital) {
        if (hospital == null) {
            return false;
        }
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        User user = q.get();
        if (UserType.doctor.getIndex() != user.getUserType()) {
            return false;
        }
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("modifyTime", System.currentTimeMillis());
        if (StringUtils.isNotBlank(hospital.getName())) {
            ops.set("doctor.hospital", hospital.getName());
            if (user.getDoctor() != null && user.getDoctor().getCheck() != null
                && user.getDoctor().getCheck().getHospitalId() != null
                && user.getDoctor().getCheck().getHospitalId().equals(hospital.getId())) {
                ops.set("doctor.check.hospital", hospital.getName());
            }
        }
        // 省市区，要么不改，要么都改。
        if (hospital.getProvince() != null) {
            ops.set("doctor.provinceId", hospital.getProvince());
            ops.set("doctor.province", hospital.getProvinceName());
            ops.set("doctor.cityId", hospital.getCity());
            ops.set("doctor.city", hospital.getCityName());
            ops.set("doctor.countryId", hospital.getCountry());
            ops.set("doctor.country", hospital.getCountryName());
        }
        UpdateResults results = dsForRW.update(q, ops);
        return results != null && results.getUpdatedExisting();
    }

    @Override
    public User updateAssistant(int userId, Assistant param) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        User oldUser = q.get();
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

        if (UserType.assistant.getIndex() == oldUser.getUserType()) {
            // 医助
            if (param != null) {
                if (param.getCompany() != null) {
                    ops.set("assistant.company", param.getCompany());
                }
                if (param.getDepartment() != null) {
                    ops.set("assistant.department", param.getDepartment());
                }
                if (param.getPosition() != null) {
                    ops.set("assistant.position", param.getPosition());
                }
                if (param.getArea() != null) {
                    ops.set("assistant.area", param.getArea());
                }
                ops.set("modifyTime", System.currentTimeMillis());
            }
        }

        User user = dsForRW.findAndModify(q, ops);

        return user;

    }

    @Override
    public void setRemindVoice(Integer userId, UserConfig param) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("userConfig", param);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.update(q, ops, true);
        UserSoundVO requestMsg = new UserSoundVO();
        requestMsg.setUserId(String.valueOf(userId));
        requestMsg.setSound(param.getRemindVoice());
        MsgHelper.setSound(requestMsg);
    }

    @Override
    public boolean setRemarks(Integer userId, String remarks) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("remarks", remarks);
        ops.set("modifyTime", System.currentTimeMillis());
        UpdateResults ur = dsForRW.update(q, ops);
        if (ur != null && ur.getUpdatedExisting()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean exsistUser(String phone, Integer userType) {
        DBObject query = new BasicDBObject();
        DBObject projection = new BasicDBObject();
        query.put("telephone", phone);
        query.put("userType", userType);
        int count = dsForRW.getDB().getCollection("user").find(query, projection).count();
        return count > 0;
    }


    @Override
    public boolean setHeaderPicName(Integer userId, String headerPicName) throws HttpApiException {
        Query<User> q = dsForRW.createQuery(User.class).filter("_id", userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        if (StringUtil.isBlank(headerPicName)) {
            return false;
        }
        ops.set("headPicFileName", headerPicName);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.update(q, ops);
        headPicModifyNotify(q.get());
        return true;
    }

    public void headPicModifyNotify(User user) throws HttpApiException {
        updateUserSessionCache(user);
    }

    /**
     * 用户的端信息变化
     *
     * @param user
     */
    public void terminalModifyNotify(User user) {
        updateUserSessionCache(user);
    }

    /*
     * 根据用户Id获取头像名称
     *
     * @see
     * com.dachen.health.commons.dao.UserRepository#getHeaderPicName(java.lang
     * .Integer)
     */
    @Override
    public String getHeaderPicName(Integer userId) {
        DBCollection dbUser = dsForRW.getCollection(User.class);
        DBObject condition = new BasicDBObject("_id", userId);

        // 查询字段
        BasicDBObject fields = new BasicDBObject();
        fields.put("headPicFileName", 1);
        /*
		 * 只获取headPicFileName
		 */
        DBCursor cursor = dbUser.find(condition, fields);
        String headPicFileName = null;
        if (cursor.hasNext()) {
            headPicFileName = (String) cursor.next().get("headPicFileName");
        }
        return headPicFileName;
    }

    @Override
    public List<User> getHeaderPicName(List<Integer> userIdList) {
        Query<User> uq = dsForRW.createQuery(User.class).field("_id").in(userIdList);
        // uq.filter("_id", userIdList);
        // uq.retrievedFields(include, fields);
        return uq.asList();
    }

    /**
     * </p>
     * 判断是否为好友
     * </p>
     *
     * @param session
     * @param user
     * @return
     * @author fanp
     * @date 2015年7月16日
     */
    public boolean isFriend(UserSession session, User user) {
        // 1.获取当前用户登录信息，2.判断是否为同一人，3.根据userType确定关系类型
        if (session == null) {
            throw new ServiceException("请登录");
        }
        if (user.getUserId() == session.getUserId()) {
            // 同一人
            return true;
        }

        Integer userId = session.getUserId();
        Integer toUserId = user.getUserId();

        // 医患关系
        if ((session.getUserType() == UserEnum.UserType.doctor.getIndex()
                && user.getUserType() == UserEnum.UserType.patient.getIndex())
                || (session.getUserType() == UserEnum.UserType.patient.getIndex()
                && user.getUserType() == UserEnum.UserType.doctor.getIndex())) {
            return isRelation("u_doctor_patient", userId, toUserId);
        }
        // 医生好友关系
        if (session.getUserType() == UserEnum.UserType.doctor.getIndex()
                && user.getUserType() == UserEnum.UserType.doctor.getIndex()) {
            return isRelation("u_doctor_friend", userId, toUserId);
        }
        // 医生助理关系
        if ((session.getUserType() == UserEnum.UserType.doctor.getIndex()
                && user.getUserType() == UserEnum.UserType.assistant.getIndex())
                || (session.getUserType() == UserEnum.UserType.assistant.getIndex()
                && user.getUserType() == UserEnum.UserType.doctor.getIndex())) {
            return isRelation("u_doctor_assistant", userId, toUserId);
        }
        // 患者好友关系
        if (session.getUserType() == UserEnum.UserType.patient.getIndex()
                && user.getUserType() == UserEnum.UserType.patient.getIndex()) {
            return isRelation("u_patient_friend", userId, toUserId);
        }

        return true;
    }

    /**
     * </p>
     * 查找关系是否存在
     * </p>
     *
     * @param collection
     * @param userId
     * @param toUserId
     * @return
     * @author fanp
     * @date 2015年7月16日
     */
    private boolean isRelation(String collection, Integer userId, Integer toUserId) {
        DBObject query = new BasicDBObject();
        query.put("userId", "userId");
        query.put("toUserId", "toUserId");
        query.put("status", UserEnum.RelationStatus.normal.getIndex());

        long count = dsForRW.getDB().getCollection(collection).count(query);
        return count == 0 ? true : false;
    }

    @Override
    public void updateVoip(int userid, Map<String, Object> voip) {
        if (voip != null) {
            Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userid);
            UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
            ops.set("voip.dateCreated", voip.get("dateCreated"));
            // ops.set("voip.subToken", voip.get("subToken"));
            ops.set("voip.voipPwd", voip.get("voipPwd"));
            ops.set("voip.subAccountSid", voip.get("subAccountSid"));
            ops.set("voip.voipAccount", voip.get("voipAccount"));

            dsForRW.findAndModify(q, ops);

        }

    }

    @Override
    public User getUserByVoip(String voip) {
        Query<User> query = dsForRW.createQuery(User.class).field("voip.voipAccount").equal(voip);

        return query.get();
    }

    @Override
    public Query<User> getDoctorQuery(List<Integer> doctorIds, List<String> hospitals, String deptId) {
        return getDoctorQuery(doctorIds, hospitals, deptId, 0);
    }

    @Override
    public Query<User> getDoctorQuery(List<Integer> doctorIds, List<String> hospitals, String deptId, Integer code) {
        if (doctorIds != null && doctorIds.isEmpty()) {
            doctorIds.add(-1);
        }
        return doctorQuery(doctorIds, hospitals, deptId, code);
    }

    public Query<User> doctorQuery(List<Integer> doctorIds, List<String> hospitals, String deptId, Integer code) {
        Query<User> q = dsForRW.createQuery("user", User.class).filter("status", 1).filter("userType", 3);
        if (doctorIds != null && !doctorIds.isEmpty()) {
            q.filter("_id in", doctorIds);
        }
        if (hospitals != null && !hospitals.isEmpty()) {
            q.filter("doctor.hospitalId in", hospitals);
        } else {
            q.filter("doctor.hospitalId !=", null);
        }
        if (StringUtil.isNotBlank(deptId)) {
            Pattern pattern = Pattern.compile("^" + deptId + ".*$");
            q.filter("doctor.deptId", pattern);
        }
        if (code != null && code != 0) {
            if (code % 10000 == 0) {
                q.field("doctor.provinceId").equal(code);
            } else if (code % 100 == 0) {
                q.field("doctor.cityId").equal(code);
            } else {
                q.field("doctor.countryId").equal(code);
            }
        }
        return q.order("-doctor.serviceStatus,doctor.titleRank,-doctor.cureNum");
    }


    public List<Integer> findUserBlurryByNameAndIphone(String userName, String telephone) {

        List<Integer> userIds = new ArrayList<Integer>();

        Query<User> q = dsForRW.createQuery("user", User.class);

        if (StringUtils.isNotBlank(userName)) {
            q.field("name").contains(userName);
        }
        if (StringUtils.isNotBlank(telephone)) {
            q.field("telephone").equal(telephone);
        }

        List<User> users = q.asList();

        for (User user : users) {
            userIds.add(user.getUserId());
        }

        return userIds;
    }

    @Override
    public boolean updateStatus(Integer userId, Integer userStatus) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("status", userStatus);
        ops.set("modifyTime", System.currentTimeMillis());
        UpdateResults ur = dsForRW.update(q, ops);

        if (userStatus == 5) {
            /**
             * 删除当前导医的所有token
             */
            UserUtil.clearUserTokens(userId);
        }
        if (ur != null && ur.getUpdatedExisting()) {
            return true;
        }

        return false;
    }

    @Override
    public boolean updateInvitationInfo(Integer userId, Integer inviterId, Integer subsystem, String way, Boolean deptInvitation) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("source.inviterId", inviterId);
        ops.set("source.sourceType", subsystem);
        ops.set("source.invateWay", way);
        ops.set("source.deptInvitation", deptInvitation);
        ops.set("modifyTime", System.currentTimeMillis());
        UpdateResults ur = dsForRW.update(q, ops);
        return ur != null && ur.getUpdatedExisting();
    }

    private void notifyUserUpdate(User user) {
        // 用户信息修改通知
        UserInfoNotify.notifyUserUpdate(user.getUserId());

        // 用户头像修改通知:1、生成头像缩略图;2：生成用户二维码；3、如果是医生，则更改医生公共号头像
        UserInfoNotify.notifyUserPicUpdate(user, "avatar");
    }

    @Override
    public User getDoctorByTelOrNum(String number) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.or(q.criteria("telephone").equal(number), q.criteria("doctor.doctorNum").equal(number));
        return q.get();
    }

    @Override
    public User getDoctorByTelOrNumNoStatus(String number) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.or(q.criteria("telephone").equal(number), q.criteria("doctor.doctorNum").equal(number));
        return q.get();
    }

    @Override
    public long searchConsultationDoctorsCount(Set<Integer> doctorIds, List<String> hospitalIds, String name,
                                               String deptId) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());

        if (hospitalIds != null && hospitalIds.size() > 0) {
            q.field("doctor.hospitalId").in(hospitalIds);
        }
        if (StringUtil.isNotEmpty(name)) {
            q.or(q.criteria("name").contains(name), q.criteria("doctor.hospital").contains(name));
        }

        if (StringUtil.isNotBlank(deptId)) {
            Pattern pattern = Pattern.compile("^" + deptId + ".*$");
            q.filter("doctor.deptId", pattern);
        }

        if (doctorIds != null && doctorIds.size() > 0) {
            q.field("_id").in(doctorIds);
        }
        return q.countAll();
    }

    @Override
    public List<User> getConsultationDoctors(Set<Integer> doctorIds, List<String> hospitalIds, String name,
                                             String deptId, Integer pageIndex, Integer pageSize) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());

        if (hospitalIds != null && hospitalIds.size() > 0) {
            q.field("doctor.hospitalId").in(hospitalIds);
        }

        if (StringUtil.isNotEmpty(name)) {
            q.or(q.criteria("name").contains(name), q.criteria("doctor.hospital").contains(name));
        }
        if (StringUtil.isNotBlank(deptId)) {
            Pattern pattern = Pattern.compile("^" + deptId + ".*$");
            q.filter("doctor.deptId", pattern);
        }
        if (doctorIds != null && doctorIds.size() > 0) {
            q.field("_id").in(doctorIds);
        }
        q.order("doctor.titleRank, -doctor.cureNum");
        q.offset(pageIndex * pageSize);
        q.limit(pageSize);
        return q.asList();
    }

    @Override
    public List<User> getDoctorsByIds(List<Integer> doctorIds) {
        return dsForRW.createQuery(User.class).field("_id").in(doctorIds).order("name").asList();
    }

    @Override
    public long fingTotalDoctorCount(int flag, List<Integer> doctorList, Integer cityId, String hospitalId,
                                     String title, String deptId, Integer pageIndex, Integer pageSize) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", doctorList);
        if (flag == 1) // 全不选
        {
//			q.or(q.criteria("doctor.title").equal(title), q.criteria("doctor.cityId").equal(cityId),
//					q.criteria("doctor.hospitalId").equal(hospitalId));
        } else // 全选 或者 存在某个选择
        {
            if (cityId != null) {
                q.field("doctor.cityId").equal(cityId);
            }
            if (StringUtil.isNotEmpty(hospitalId)) {
                q.field("doctor.hospitalId").equal(hospitalId);
            }
            if (StringUtil.isNotEmpty(title)) {
                q.field("doctor.title").equal(title);
            }
        }
        if (deptId != null) {
            q.filter("doctor.deptId", deptId);
        }
        return q.countAll();
    }

    @Override
    public List<Map<String, Object>> fingDoctorList(int flag, List<Integer> doctorList, Integer cityId,
                                                    String hospitalId, String title, String deptId, Integer pageIndex, Integer pageSize) {
        List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", doctorList);
        if (flag == 1) // 全不选
        {
//			q.or(q.criteria("doctor.title").equal(title), q.criteria("doctor.cityId").equal(cityId),
//					q.criteria("doctor.hospitalId").equal(hospitalId));
        } else // 全选 或者 存在某个选择
        {
            if (cityId != null) {
                q.field("doctor.cityId").equal(cityId);
            }
            if (StringUtil.isNotEmpty(hospitalId)) {
                q.field("doctor.hospitalId").equal(hospitalId);
            }
            if (StringUtil.isNotEmpty(title)) {
                q.field("doctor.title").equal(title);
            }
        }
        if (deptId != null) {
            q.filter("doctor.deptId", deptId);
        }
        q.order("name");
        q.offset(pageIndex * pageSize);
        q.limit(pageSize);
        List<User> users = q.asList();
        if (null != users && users.size() > 0) {
            for (User user : users) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("doctorId", user.getUserId());
                userMap.put("deptId", user.getDoctor().getDeptId());
                userMap.put("name", user.getName());
                userMap.put("hospital", user.getDoctor().getHospital() == null ? "" : user.getDoctor().getHospital());
                userMap.put("skill", getDoctorSkill(user, user.getDoctor().getSkill()));
                userMap.put("departments",
                        user.getDoctor().getDepartments() == null ? "" : user.getDoctor().getDepartments());
                userMap.put("title", user.getDoctor().getTitle());
                userMap.put("headPicFileName", user.getHeadPicFileName());
                userMap.put("cureNum", user.getDoctor().getCureNum());
                userMap.put("cityId", user.getDoctor().getCityId());
                userMap.put("hospitalId", user.getDoctor().getHospitalId());
                userMap.put("troubleFree", user.getDoctor().getTroubleFree());
                map.add(userMap);
            }
        }
        return map;
    }

    private String getDoctorSkill(User user, String skillStr) {

        String result = "";
        StringBuffer skill = new StringBuffer();
        Doctor doctor = user.getDoctor();
        if (null != doctor) {
            List<String> expertise = doctor.getExpertise();
            if (expertise != null && expertise.size() > 0) {
                List<String> diseaseTypeIds = new ArrayList<String>();
                for (Object object : expertise) {
                    diseaseTypeIds.add((String) object);
                }
                if (!diseaseTypeIds.isEmpty()) {
                    List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(diseaseTypeIds);
                    if (null != diseaseTypes && diseaseTypes.size() > 0) {
                        for (int i = 0; i < diseaseTypes.size(); i++) {
                            DiseaseType type = diseaseTypes.get(i);

                            if (null != type && StringUtils.isNotEmpty(type.getName())) {
                                if (i < diseaseTypes.size() - 1) {
                                    skill.append(type.getName()).append(",");
                                } else {
                                    skill.append(type.getName());
                                }
                            }
                        }

                    }

                }
            }
        }
        if (StringUtils.isNotEmpty(skillStr)) {
            result = skill.toString() + "," + skillStr;
        }
        return result;
    }

    @Override
    public long searchConsultationDoctorsByKeywordCount(Set<Integer> beSearchIds, String keyword) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", beSearchIds);
        q.or(q.criteria("doctor.hospital").contains(keyword), q.criteria("name").contains(keyword));
        return q.countAll();
    }

    @Override
    public List<User> searchConsultationDoctorsByKeyword(Set<Integer> beSearchIds, String keyword, Integer pageIndex,
                                                         Integer pageSize) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", beSearchIds);
        q.or(q.criteria("doctor.hospital").contains(keyword), q.criteria("name").contains(keyword));
        q.order("doctor.titleRank, -doctor.cureNum");
        q.offset(pageIndex * pageSize).limit(pageSize);
        return q.asList();
    }

    @Override
    public long fingDoctorByKeyWord(String keyWord, Integer pageIndex, Integer pageSize, List<Integer> doctorList) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", doctorList);
        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        q.or(q.criteria("name").equal(pattern),
                q.criteria("doctor.hospital").equal(pattern),
                q.criteria("doctor.departments").equal(pattern),
                q.criteria("doctor.title").equal(pattern),
                q.criteria("doctor.skill").equal(pattern)
        );
        return q.countAll();
    }

    @Override
    public List<Map<String, Object>> fingDoctorByKeyWordList(String keyWord, Integer pageIndex, Integer pageSize,
                                                             List<Integer> doctorList) {
        List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", doctorList);
        Pattern pattern = Pattern.compile("^.*" + keyWord + ".*$", Pattern.CASE_INSENSITIVE);
        q.or(q.criteria("name").equal(pattern),
                q.criteria("doctor.hospital").equal(pattern),
                q.criteria("doctor.departments").equal(pattern),
                q.criteria("doctor.title").equal(pattern),
                q.criteria("doctor.skill").equal(pattern)
        );
        q.order("name");
        q.offset(pageIndex * pageSize);
        q.limit(pageSize);
        List<User> users = q.asList();
        if (null != users && users.size() > 0) {
            for (User user : users) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("doctorId", user.getUserId());
                userMap.put("name", user.getName());
                userMap.put("hospital", user.getDoctor().getHospital() == null ? "" : user.getDoctor().getHospital());
                userMap.put("skill", getDoctorSkill(user, user.getDoctor().getSkill()));
                userMap.put("departments",
                        user.getDoctor().getDepartments() == null ? "" : user.getDoctor().getDepartments());
                userMap.put("title", user.getDoctor().getTitle());
                userMap.put("headPicFileName", user.getHeadPicFileName());
                userMap.put("cureNum", user.getDoctor().getCureNum());
                userMap.put("cityId", user.getDoctor().getCityId());
                userMap.put("hospitalId", user.getDoctor().getHospitalId());
                map.add(userMap);
            }
        }
        return map;
    }

    @Override
    public List<Integer> getDoctorsByNameOrHospitalNameOrTelephone(String doctorName, String hospitalName,
                                                                   String telephone) {
        if (StringUtils.isBlank(doctorName) && StringUtils.isBlank(hospitalName) && StringUtils.isBlank(telephone)) {
            return null;
        }
        List<Integer> list = new ArrayList<Integer>();
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        List<Criteria> cs = new ArrayList<>();
        if (StringUtils.isNotBlank(hospitalName)) {
            cs.add(q.criteria("doctor.hospital").contains(hospitalName));
            // q.field("doctor.hospital").contains(hospitalName);
        }
        if (StringUtils.isNotBlank(doctorName)) {
//			cs.add(q.criteria("doctor.name").contains(doctorName));
            cs.add(q.criteria("name").contains(doctorName)); // [^] tanyf 20160608
            // q.field("name").contains(doctorName);
        }
        if (StringUtils.isNotBlank(telephone)) {
            cs.add(q.criteria("telephone").contains(telephone));
            // q.field("telephone").contains(telephone);
        }
        if (cs.size() > 0) {
            Criteria[] cArr = new Criteria[cs.size()];
            q.or(cs.toArray(cArr));
        }
        q.retrievedFields(true, "userId");
        List<User> users = q.asList();
        if (null != users && users.size() > 0) {
            for (User user : users) {
                list.add(user.getUserId());
            }
        }
        return list;
    }

    /**
     * 按关键字搜索
     */
    public User getUser(int userId, String keyword) {
        Query<User> q = dsForRW.createQuery(User.class);
        if (StringUtils.isNotEmpty(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            q.criteria("name").equal(pattern);
        }
        q.criteria("userId").equal(userId);
        return q.get();
    }

    public List<User> findByName(String name) {
        return dsForRW.createQuery(User.class).filter("name", name).asList();
    }

    @Override
    public PageVO getGuideDoctorList(String userName, String telephone, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        if (null != pageIndex && pageIndex > 0) {
            pageVo.setPageIndex(pageIndex);
        }
        if (null != pageSize && pageSize > 0) {
            pageVo.setPageSize(pageSize);
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list = getGuidList(userName, telephone, pageIndex, pageSize);
        pageVo.setTotal(Long.valueOf(list.size()));
        pageVo.setPageData(list);
        return pageVo;
    }

    @Override
    public PageVO getGuideDoctorList(String groupId, String userName, String telephone, Integer pageIndex,
                                     Integer pageSize) {
        PageVO pageVo = new PageVO();
        if (null != pageIndex && pageIndex > 0) {
            pageVo.setPageIndex(pageIndex);
        }
        if (null != pageSize && pageSize > 0) {
            pageVo.setPageSize(pageSize);
        }
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        list = getGuidList(groupId, userName, telephone, pageIndex, pageSize);
        pageVo.setTotal(Long.valueOf(list.size()));
        pageVo.setPageData(list);
        return pageVo;
    }

    public List<Map<String, Object>> getGuidList(String groupId, String userName, String telephone, Integer pageIndex,
                                                 Integer pageSize) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.DocGuide.getIndex())
                .filter("doctorGuider.groupId", groupId);
        // 按照关键字查询
        if (!StringUtil.isEmpty(userName)) {
            Pattern pattern = Pattern.compile("^.*" + userName + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("name").equal(pattern));
        }
        if (!StringUtil.isEmpty(telephone)) {
            Pattern pattern = Pattern.compile("^.*" + telephone + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("telephone").equal(pattern));
        }
        q.offset((pageIndex == null ? 0 : pageIndex) * pageSize);
        q.order("-createTime");
        q.limit(pageSize);
        List<User> user = q.asList();
        if (null != user && user.size() > 0) {
            for (User u : user) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("userId", u.getUserId());
                userMap.put("name", u.getName());
                userMap.put("telephone", u.getTelephone());
                userMap.put("createTime", u.getCreateTime());
                userMap.put("status", u.getStatus());
                list.add(userMap);
            }
        }
        return list;
    }

    public List<Map<String, Object>> getGuidList(String userName, String telephone, Integer pageIndex,
                                                 Integer pageSize) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.DocGuide.getIndex());
        // 按照关键字查询
        if (!StringUtil.isEmpty(userName)) {
            Pattern pattern = Pattern.compile("^.*" + userName + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("name").equal(pattern));
        }
        if (!StringUtil.isEmpty(telephone)) {
            Pattern pattern = Pattern.compile("^.*" + telephone + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("telephone").equal(pattern));
        }
        q.offset((pageIndex == null ? 0 : pageIndex) * pageSize);
        q.order("-createTime");
        q.limit(pageSize);
        List<User> user = q.asList();
        if (null != user && user.size() > 0) {
            for (User u : user) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("userId", u.getUserId());
                userMap.put("name", u.getName());
                userMap.put("telephone", u.getTelephone());
                userMap.put("createTime", u.getCreateTime());
                userMap.put("status", u.getStatus());
                list.add(userMap);
            }
        }
        return list;
    }

    @Override
    public List<Integer> getUserIdList(String keyword) {
        List<Integer> userIds = new ArrayList<Integer>();
        Query<User> q = dsForRW.createQuery(User.class);
        if (StringUtils.isNotEmpty(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            q.criteria("name").equal(pattern);
            List<User> userList = q.asList();
            if (null != userList && userList.size() > 0) {

                for (User user : userList) {
                    userIds.add(user.getUserId());
                }
            }
        }

        return userIds;
    }

    @Override
    public List<User> findDoctorsInIds(List<Integer> packFriendIds, Integer pageIndex, Integer pageSize) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("_id").in(packFriendIds);
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.offset((pageIndex == null ? 0 : pageIndex) * pageSize);
        q.order("doctor.titleRank, -doctor.cureNum");
        q.limit(pageSize);
        return q.asList();
    }

    @Override
    public long findDoctorsInIdsCount(List<Integer> packFriendIds) {
        return dsForRW.createQuery(User.class).field("_id").in(packFriendIds).field("status")
                .equal(UserEnum.UserStatus.normal.getIndex()).countAll();
    }

    @Override
    public PageVO findDoctorsByCondition(DoctorParam param) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.doctor.getIndex())
                .filter("status", UserEnum.UserStatus.normal.getIndex()).field("suspend").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        // 按照关键字查询
        if (!StringUtil.isEmpty(param.getKeyWord())) {
            Pattern pattern = Pattern.compile("^.*" + param.getKeyWord() + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("name").equal(pattern), q.criteria("telephone").equal(pattern),
                    q.criteria("doctor.hospital").equal(pattern));
        }
        //查询总记录数
        long totalCount=q.countAll();
        q.offset((param.getPageIndex()) * param.getPageSize());
        q.order("-createTime");
        q.limit(param.getPageSize());
        List<User> user = q.asList();
        if (null != user && user.size() > 0) {
            for (User u : user) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("userId", u.getUserId());
                userMap.put("name", u.getName());
                userMap.put("telephone", u.getTelephone());
                userMap.put("createTime", u.getCreateTime());
                userMap.put("status", u.getStatus());
                userMap.put("title", u.getDoctor().getTitle());
                userMap.put("departments", u.getDoctor().getDepartments());
                userMap.put("hospital", u.getDoctor().getHospital());
                userMap.put("headPicFileName", u.getHeadPicFileName());
                list.add(userMap);
            }
        }
        PageVO page = new PageVO();
        if (param.getPageIndex() >= 0) {
            page.setPageIndex(param.getPageIndex());
        }
        if (param.getPageSize() >= 0) {
            page.setPageSize(param.getPageSize());
        }
        page.setTotal(totalCount);
        page.setPageData(list);
        return page;
    }

    @Override
    public PageVO findDoctorsByOrgId(String orgId, String hospitalId, String deptId, String name,
        Integer userId, Integer status, Long ts,
        Integer pageIndex,
        Integer pageSize) {
        if (StringUtil.isBlank(orgId)) {
            throw new ServiceException("企业Id不能为空");
        }

        if (Objects.isNull(pageIndex)) {
            pageIndex = 0;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 10;
        }

        List<Map<String, Object>> list = new ArrayList<>();
        Query<User> query = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.doctor.getIndex())
                .filter("modifyTime >= ", ts);
        if(status != null){
            query.filter("status", status);
        }

        if(StringUtil.isNotBlank(hospitalId))
            query.field("doctor.hospitalId").equal(hospitalId);

        if(StringUtil.isNotBlank(deptId))
            query.field("doctor.deptId").equal(deptId);

        if(StringUtil.isNotBlank(name))
            query.field("name").contains(name);

        if(userId != null)
            query.field("_id").equal(userId);

        long total = query.countAll();
        query.offset(pageIndex*pageSize);
        query.order("-createTime");
        query.limit(pageSize);

        query.retrievedFields(true, "_id", "name", "telephone", "sex",
            "doctor.title", "doctor.departments", "doctor.hospital",
            "doctor.deptId", "doctor.hospitalId", "status", "suspend", "userLevel", "createTime", "doctor.check.checkTime");
        List<User> userList = query.asList();
        if (!CollectionUtils.isEmpty(userList)) {

            List<Integer> userIdList = userList.stream().map(User::getUserId).collect(Collectors.toList());

            Map<Integer, String> openIdMap = new HashMap<>();
            try{
                List<AccessToken> accessTokenList = auth2Helper.getOpenIdList(userIdList);
                if (!CollectionUtils.isEmpty(accessTokenList)) {
                    for(AccessToken at : accessTokenList){
                        if(Objects.nonNull(at.getUserId())){
                            openIdMap.put(at.getUserId(), at.getOpenId());
                        }
                    }
                }
            }catch(Exception ex){
                logger.info(this.getClass().getName(), ex);
            }

            for (User user : userList) {
                Map<String, Object> userMap = new HashMap<>();
                if(Objects.isNull(user)){
                    list.add(userMap);
                    continue;
                }
                try{
                    userMap.put("name", user.getName());
                    userMap.put("sex", user.getSex());
                    userMap.put("telephone", user.getTelephone());
                    userMap.put("status", user.getStatus());
                    userMap.put("suspend", user.getSuspend());
                    userMap.put("userLevel", user.getUserLevel());
                    userMap.put("userId", user.getUserId());
                    userMap.put("createTime", user.getCreateTime());//注册时间
                    Doctor doctor = user.getDoctor();
                    if(Objects.nonNull(doctor)){
                        DoctorTitleVO doctorTitleVO = getDocTitleInfo(doctor.getTitle());
                        if (Objects.nonNull(doctorTitleVO)) {
                            userMap.put("titleId", doctorTitleVO.getId());//职称Id
                        }
                        userMap.put("title", doctor.getTitle());//职称
                        userMap.put("deptId", doctor.getDeptId());//科室Id
                        userMap.put("departments", doctor.getDepartments());//科室
                        userMap.put("hospital", doctor.getHospital());//医疗机构
                        userMap.put("hospitalId", doctor.getHospitalId());//医疗机构Id

                        Check check = doctor.getCheck();
                        if(Objects.nonNull(check)) {
                            userMap.put("checkTime", check.getCheckTime());//审核时间
                        }
                    }
                    userMap.put("openId", openIdMap.get(user.getUserId()));
                }catch(Exception ex){
                    logger.info(this.getClass().getName(), ex);
                }
                list.add(userMap);
            }


        }
        PageVO page = new PageVO();
        page.setPageIndex(pageIndex);
        page.setPageSize(pageSize);
        page.setTotal(total);
        page.setPageData(list);
        return page;
    }


    @Override
    public PageVO findDoctorInfoByModifyTime(OpenDoctorParam param) {
        Query<User> query = dsForRW.createQuery(User.class).filter("userType", UserType.doctor.getIndex());
        if (param.getModifyTime() != null) {
            query.field("modifyTime").greaterThanOrEq(param.getModifyTime());
        }
        long count = query.countAll();
        query.offset(param.getStart()).limit(param.getPageSize());
        return new PageVO(query.asList(), count);
    }

    private DoctorTitleVO getDocTitleInfo(String title){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("name", title);
        Query<DoctorTitleVO> query = baseDataDao.getIncInfos(map, "b_doctortitle", DoctorTitleVO.class);
        return query.get();
    }

    @Override
    public PageVO findDoctorsByName(String keyword, Integer pageIndex, Integer pageSize) {

/*        AbstractSearchParam searchParam = new SearchParam.Builder(TypeDefine.Constants.INDEX_HEALTH)
            .type(new String[]{TypeDefine.Constants.TYPE_DOCTOR})
            .searchKey(keyword)
            .from(pageIndex)
            .size(pageSize)
            .build();

        Map<String,List<String>> map_es = ElasticSearchFactory.getInstance().searchAndReturnBizId(searchParam);
        logger.info("es_map:" + JSON.toJSONString(map_es));
        if(null==map_es)return null;
        List<Integer> list_id = Lists.newArrayList();
        for (String id : map_es.get("doctor")) {
            list_id.add(Integer.valueOf(id));
        }*/

        Query<User> query= dsForRW.createQuery(User.class);
        query.field("userType").equal(UserType.doctor.getIndex());
        query.field("status").equal(UserStatus.normal.getIndex());
        query.field("suspend").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        if(StringUtil.isNotEmpty(keyword)) {
            query.field("name").contains(keyword);
        }

        PageVO page = new PageVO();
        page.setTotal(query.countAll());

        query.offset(pageIndex * pageSize);
        query.limit(pageSize);

        List<User> users = query.asList();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (!CollectionUtils.isEmpty(users))
            for (User u : users) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("userId", u.getUserId());
                userMap.put("name", u.getName());
                userMap.put("telephone", u.getTelephone());
                userMap.put("title", u.getDoctor().getTitle());
                userMap.put("departments", u.getDoctor().getDepartments());
                userMap.put("hospital", u.getDoctor().getHospital());
                userMap.put("headPicFileName", u.getHeadPicFileName());
                list.add(userMap);
        }

        page.setPageData(list);
        return page;

    }

    public List<User> searchDoctor(String[] hospitalIdList, String keyword, String departments) {
        if (hospitalIdList == null || hospitalIdList.length == 0)
            return null;

        Query<User> q = dsForRW.createQuery(User.class).filter("doctor.hospitalId in", hospitalIdList);
        if (StringUtil.isNotBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            q.filter("name", pattern);
        }
        if (StringUtil.isNotBlank(departments)) {
            q.filter("departments", departments);
        }
        q.field("status").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        return q.asList();
    }

    public List<User> searchOkStatusDoctor(String[] hospitalIdList, String keyword, String departments) {
        if (hospitalIdList == null || hospitalIdList.length == 0)
            return null;

        Query<User> q = dsForRW.createQuery(User.class).filter("doctor.hospitalId in", hospitalIdList);
        if (StringUtil.isNotBlank(keyword)) {
            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
            q.filter("name", pattern);
        }
        if (StringUtil.isNotBlank(departments)) {
            q.filter("departments", departments);
        }
        q.field("status").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        List<User> list = q.asList();
        return list;
    }

    @Override
    public List<User> searchDoctorByKeyword(String keyword, String departments) {
        if (StringUtil.isEmpty(keyword)){
            return null;
        }
        Query<User> q = dsForRW.createQuery(User.class);
        if (StringUtil.isNotBlank(keyword)) {
//            Pattern pattern = Pattern.compile("^.*" + keyword + ".*$", Pattern.CASE_INSENSITIVE);
//            q.filter("name", pattern);

            keyword = keyword.replaceAll("\\(", "[(]");//解决正则表达式中的“(”的问题
            keyword = keyword.replaceAll("\\)", "[)]");//解决正则表达式中的“)”的问题
            q.field("name").containsIgnoreCase(keyword);
        }
        if (StringUtil.isNotBlank(departments)) {
            q.filter("doctor.departments", departments);
        }
        q.filter("userType", UserType.doctor.getIndex());
        return q.asList();
    }

    @Override
    public List<User> searchOkStatusDoctorByKeyword(String keyword, String departments) {
        if (StringUtil.isEmpty(keyword)){
            return null;
        }
        Query<User> q = dsForRW.createQuery(User.class);
        if (StringUtil.isNotBlank(keyword)) {
            keyword = keyword.replaceAll("\\(", "[(]");//解决正则表达式中的“(”的问题
            keyword = keyword.replaceAll("\\)", "[)]");//解决正则表达式中的“)”的问题
            q.field("name").containsIgnoreCase(keyword);
        }
        if (StringUtil.isNotBlank(departments)) {
            q.filter("doctor.departments", departments);
        }
        q.filter("userType", UserType.doctor.getIndex());
        q.field("status").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        q.field("suspend").notEqual(UserConstant.SuspendStatus.tempForbid.getIndex());
        return q.asList();
    }

    @Override
    public PageVO findHospitalByCondition(DoctorParam param) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<HospitalPO> q = dsForRW.createQuery(HospitalPO.class).filter("status", DataStatusEnum.normal.getValue());
        // 按照关键字查询
        if (!StringUtil.isEmpty(param.getKeyWord())) {
            // 支持关键字拆分的搜索
//			String key = ".*";
            String key = param.getKeyWord();

            /**
             * 若关键字之间有多个空格,则以空格分隔关键字,同时满足多个关键字搜索
             * edit by wanghong 2017-10-31 begin
             * */
            if (key.trim().indexOf(" ")  != -1){
                String[] keys = key.trim().split(" ");
                List<Pattern> patterns = Lists.newArrayList();
                for (String k:keys){
                    if (StringUtil.isNotEmpty(k)){
                        Pattern pattern = Pattern.compile("^.*" + k + ".*$", Pattern.CASE_INSENSITIVE);
                        patterns.add(pattern);
                    }
                }
               // q.and(q.criteria("name").equal(patterns));
                q.filter("name all", patterns);
            }else {
			/*for (int i = 0; i < param.getKeyWord().length(); i++) {
				key = key + param.getKeyWord().substring(i, i + 1) + ".*";
			}*/
                Pattern pattern = Pattern.compile("^.*" + key + ".*$", Pattern.CASE_INSENSITIVE);
                // Pattern pattern = Pattern.compile("^.*" + param.getKeyWord() +
                // ".*$", Pattern.CASE_INSENSITIVE);
                q.and(q.criteria("name").equal(pattern));
                // q.filter("name", pattern);
            }
            /***
             * edit by wanghong 2017-10-31 end
             */
        }
        q.offset((param.getPageIndex()) * param.getPageSize());
        q.order("-level");
        q.limit(param.getPageSize());
        List<HospitalPO> hospital = q.asList();
        if (null != hospital && hospital.size() > 0) {
            for (HospitalPO hospitalpo : hospital) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                Area province = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospitalpo.getProvince())
                        .get();
                Area city = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospitalpo.getCity()).get();
                Area country = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospitalpo.getCountry())
                        .get();
                userMap.put("city", hospitalpo.getCity());
                userMap.put("name", hospitalpo.getName());
                userMap.put("level", hospitalpo.getLevel());
                userMap.put("address", hospitalpo.getAddress());
                userMap.put("description", hospitalpo.getDescription());
                userMap.put("hospitalType", hospitalpo.getHospitalType());
                userMap.put("provinceName", province == null ? "" : province.getName());
                userMap.put("cityName", city == null ? "" : city.getName());
                userMap.put("countryName", country == null ? "" : country.getName());
                userMap.put("id", hospitalpo.getId());
                list.add(userMap);
            }
        }
        PageVO page = new PageVO();
        if (param.getPageIndex() >= 0) {
            page.setPageIndex(param.getPageIndex());
        }
        if (param.getPageSize() >= 0) {
            page.setPageSize(param.getPageSize());
        }
        page.setTotal(Long.valueOf(list.size()));
        page.setPageData(list);
        return page;
    }

    @Override
    public List<Map<String, Object>> findHospitalByConditionNoPaging(String keywords) {
        if (StringUtil.isBlank(keywords)) {
            return null;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        Query<HospitalPO> q = dsForRW.createQuery(HospitalPO.class).filter("status", DataStatusEnum.normal.getValue());
        // 按照关键字查询
        if (StringUtil.isNotBlank(keywords)) {
            q.field("name").contains(keywords);
        }
        q.order("-level");
        q.limit(100);
        List<HospitalPO> hospitals = q.asList();
        if (!CollectionUtils.isEmpty(hospitals)) {
            for (HospitalPO hospitalpo : hospitals) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                Area province = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospitalpo.getProvince()).get();
                Area city = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospitalpo.getCity()).get();
                Area country = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospitalpo.getCountry()).get();
                userMap.put("city", hospitalpo.getCity());
                userMap.put("name", hospitalpo.getName());
                userMap.put("level", hospitalpo.getLevel());
                userMap.put("address", hospitalpo.getAddress());
                userMap.put("description", hospitalpo.getDescription());
                userMap.put("hospitalType", hospitalpo.getHospitalType());
                userMap.put("provinceName", province == null ? "" : province.getName());
                userMap.put("cityName", city == null ? "" : city.getName());
                userMap.put("countryName", country == null ? "" : country.getName());
                userMap.put("id", hospitalpo.getId());
                list.add(userMap);
            }
        }

        return list;
    }

    @Override
    public PageVO getPlatformSelectedDoctors(List<Integer> notInDoctorIds, String keyWord, Integer pageIndex,
                                             Integer pageSize) {
        PageVO pageVo = new PageVO();
        pageIndex = pageIndex == null ? pageVo.getPageIndex() : pageIndex;
        pageSize = pageSize == null ? pageVo.getPageSize() : pageSize;
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
		/*
		 * List<String> ranks = new ArrayList<String>(); ranks.add("1");
		 * ranks.add("2"); q.field("doctor.titleRank").in(ranks);
		 */
        if (notInDoctorIds != null && notInDoctorIds.size() > 0) {
            q.field("_id").notIn(notInDoctorIds);
        }
        if (StringUtils.isNotBlank(keyWord)) {
            q.or(q.criteria("name").contains(keyWord), q.criteria("telephone").contains(keyWord),
                    q.criteria("doctor.hospital").contains(keyWord));
        }
        Long total = q.countAll();
        q.offset(pageIndex * pageSize);
        q.limit(pageSize);
        q.order("doctor.titleRank,-doctor.cureNum");
        List<User> list = q.asList();
        List<Map<String, Object>> dataList = null;
        if (list != null && list.size() > 0) {
            dataList = new ArrayList<Map<String, Object>>();
            for (User u : list) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("userId", u.getUserId());
                userMap.put("name", u.getName());
                userMap.put("telephone", u.getTelephone());
                userMap.put("createTime", u.getCreateTime());
                userMap.put("headPicFileName", u.getHeadPicFileName());
                userMap.put("status", u.getStatus());
                if (u.getDoctor() != null) {
                    userMap.put("title", u.getDoctor().getTitle());
                    userMap.put("departments", u.getDoctor().getDepartments());
                    userMap.put("hospital", u.getDoctor().getHospital());
                }
                dataList.add(userMap);
            }
        }
        pageVo.setTotal(total);
        pageVo.setPageData(dataList);
        return pageVo;
    }

    @Override
    public String getDoctorRankById(Integer doctorId) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("_id").equal(doctorId);
        q.retrievedFields(true, "doctor.titleRank");
        User u = q.get();
        if (u != null && u.getDoctor() != null) {
            return StringUtils.trim(u.getDoctor().getTitleRank());
        }
        return null;
    }

    @Override
    public List<User> getUserListByTypeAndFuzzyName(Integer type, String name) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(type);
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
        q.criteria("name").equal(pattern);
        return q.asList();
    }

    @Override
    public HospitalPO getHostpitalByPK(String id) {
        Query<HospitalPO> q = dsForRW.createQuery(HospitalPO.class).field("_id").equal(id);
        return q.get();
    }

    @Override
    public List<User> getNormalUser(int type) {
        Query<User> query = dsForRW.createQuery(User.class).filter("userType", type).filter("status",
                UserEnum.UserStatus.normal.getIndex());
        return query.asList();
    }

    @Override
    public PageVO getNormalUserPaging(Integer type, Integer pageIndex, Integer pageSize) {
        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        Query<User> query = dsForRW.createQuery(User.class).filter("userType", type).filter("status",
            UserEnum.UserStatus.normal.getIndex()).filter("telephone <>", "10000");//超级管理员的权限不能编辑
        Long total = query.countAll();
        query.order("userId");
        query.offset(pageIndex * pageSize);
        query.limit(pageSize);
        pageVO.setPageData(query.asList());
        pageVO.setTotal(total);
        return pageVO;
    }

    /**
     * 反审核一个医生
     */
    @Override
    public User uncheck(User user, Integer checkerId, String checkerName) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(user.getUserId());
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("modifyTime", System.currentTimeMillis());
        if (null != user.getStatus()) {
            ops.set("status", user.getStatus());
            ops.set("userLevel", UserEnum.UserLevel.TemporaryUser.getIndex());
            ops.set("limitedPeriodTime", user.getLimitedPeriodTime());
        }
        ops.set("modifyTime", System.currentTimeMillis());
        ops.set("doctor.check.checkTime", System.currentTimeMillis());
        ops.set("doctor.check.checkerId", checkerId);
        ops.set("doctor.check.checker", System.currentTimeMillis());
        return dsForRW.findAndModify(q, ops);
    }

    /**
     * 模糊查询查找 用户列表中的用户信息
     *
     * @param userType   用户类型
     * @param userStatus 用户状态
     * @param name       姓名
     * @param userIds    用户列表
     * @return
     */

    @Override
    public List<User> getUserByTypeAndNameInUserIds(Integer userType, Integer userStatus, String name,
                                                    List<Integer> userIds) {

        Query<User> q = dsForRW.createQuery(User.class);
        if (userIds != null && userIds.size() > 0)
            q.filter("userId in ", userIds);
		/* 不等于空添加条件 */
        if (userType != null)
            q.field("userType").equal(userType);
        if (userStatus != null)
            q.field("status").equal(userStatus);
        if (StringUtils.isNotBlank(name)) {
            Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
            q.criteria("name").equal(pattern);
        }
        return q.asList();

    }

    @Override
    public PageVO findByAreaAndDeptWithInIds(List<Integer> ids, Integer countryId, Integer provinceId, Integer cityId,
                                             String deptId, Integer pageIndex, Integer pageSize) {
        Query<User> userQuery = dsForRW.createQuery(User.class);

        userQuery.filter("userType", 3);
        userQuery.filter("userId in ", ids);

        if (countryId != null) {
            userQuery.filter("doctor.countryId", countryId);
        }
        if (provinceId != null) {
            userQuery.filter("doctor.provinceId", provinceId);
        }
        if (cityId != null) {
            userQuery.filter("doctor.cityId", cityId);
        }
        if (StringUtils.isNotEmpty(deptId)) {
            userQuery.filter("doctor.deptId", deptId);
        }
        int skip = pageIndex * pageSize;
        skip = skip < 0 ? 0 : skip;
        userQuery.offset(skip);
        userQuery.order("-doctor.cureNum, doctor.titleRank");
        //userQuery.order("doctor.titleRank");
        userQuery.limit(pageSize);

        PageVO pageVO = new PageVO();
        pageVO.setPageData(userQuery.asList());
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        pageVO.setTotal(userQuery.countAll());

        return pageVO;
    }

    @Override
    public GuestUser getGuestByDeviceID(String deviceID) {
        DBObject query = new BasicDBObject();
        query.put("derviceID", deviceID);
        DBObject cursor = dsForRW.getDB().getCollection("guest_user").findOne(query);

        GuestUser guest = new GuestUser();
        if (cursor != null) {
            guest.setId(MongodbUtil.getString(cursor, "_id"));
            guest.setDeviceID(MongodbUtil.getString(cursor, "derviceID"));
            guest.setCreateTime(MongodbUtil.getLong(cursor, "createTime"));
            guest.setActiveTime(MongodbUtil.getLong(cursor, "activeTime"));
        }

        return guest;
    }

    @Override
    public void saveGuestUser(String deviceID) {
        DBObject query = new BasicDBObject();
        query.put("derviceID", deviceID);
        query.put("createTime", System.currentTimeMillis());
        query.put("activeTime", System.currentTimeMillis());
        dsForRW.getDB().getCollection("guest_user").save(query);
    }

    public Map<String, Object> saveGuest(String deviceID, String guest_token) {
        // 先校验guest_token是否过期
        String cacheKey = KeyBuilder.getCacheKey(Constants.CacheKeyPre.GuestSession, guest_token);
        String cacheDevice = jedisTemplate.get(cacheKey);
        if (cacheDevice != null && cacheDevice.equals(deviceID)) {
            jedisTemplate.expire(cacheKey, Constants.Expire.DAY7);// 续租
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("guest_token", guest_token);
            return data;
        }

        String accessToken = UserEnum.IS_Guest_TOKEN + StringUtil.randomUUID();
        return updateGuestUserToken(deviceID, accessToken);

    }

    public Map<String, Object> updateGuestUserToken(String deviceID, String guest_token) {
        // 设置登录信息
        String cacheKey = KeyBuilder.getCacheKey(Constants.CacheKeyPre.GuestSession, guest_token);

        jedisTemplate.set(cacheKey, String.valueOf(deviceID));
        jedisTemplate.expire(cacheKey, Constants.Expire.DAY7);
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("guest_token", guest_token);
        return data;
    }

    @Override
    public void updateGuestUser(GuestUser guser) {
        DBObject query = new BasicDBObject();
        query.put("_id", new ObjectId(guser.getId()));
        query.put("derviceID", guser.getDeviceID());
        query.put("createTime", guser.getCreateTime());
        query.put("activeTime", guser.getActiveTime());
        dsForRW.getDB().getCollection("guest_user").save(query);

    }


    @Override
    public boolean checkIdCard(String idcard, Integer idtype, Integer userType) {
        DBObject query = new BasicDBObject();
        query.put("idcard", idcard);
        query.put("idtype", idtype.toString());
        query.put("userType", userType);
        DBObject cursor = dsForRW.getDB().getCollection("user").findOne(query);
        if (cursor != null) {
            return true;
        }
        return false;
    }

    @Override
    public long searchAppointmentDoctorCount(String keyWord, Integer pageIndex, Integer pageSize,
                                             List<Integer> doctorList, List<Integer> keywordUserIds) {
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", doctorList);
        if (StringUtils.isNoneBlank(keyWord)) {
            if (keywordUserIds.size() > 0) {
                q.or(q.criteria("name").contains(keyWord),
                        q.criteria("doctor.hospital").contains(keyWord),
                        q.criteria("doctor.departments").contains(keyWord),
                        q.criteria("doctor.title").contains(keyWord),
                        q.criteria("doctor.skill").contains(keyWord),
                        q.criteria("userId").in(keywordUserIds)
                );
            } else {
                q.or(q.criteria("name").contains(keyWord),
                        q.criteria("doctor.hospital").contains(keyWord),
                        q.criteria("doctor.departments").contains(keyWord),
                        q.criteria("doctor.title").contains(keyWord),
                        q.criteria("doctor.skill").contains(keyWord)
                );
            }
        }
        return q.countAll();
    }

    @Override
    public List<Map<String, Object>> searchAppointmentDoctor(String keyWord, Integer pageIndex, Integer pageSize,
                                                             List<Integer> doctorList, List<Integer> keywordUserIds) {
        List<Map<String, Object>> map = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());
        q.field("status").equal(UserEnum.UserStatus.normal.getIndex());
        q.filter("userId in", doctorList);
        if (StringUtils.isNoneBlank(keyWord)) {
            if (keywordUserIds.size() > 0) {
                q.or(q.criteria("name").contains(keyWord),
                        q.criteria("doctor.hospital").contains(keyWord),
                        q.criteria("doctor.departments").contains(keyWord),
                        q.criteria("doctor.title").contains(keyWord),
                        q.criteria("doctor.skill").contains(keyWord),
                        q.criteria("userId").in(keywordUserIds)
                );
            } else {
                q.or(q.criteria("name").contains(keyWord),
                        q.criteria("doctor.hospital").contains(keyWord),
                        q.criteria("doctor.departments").contains(keyWord),
                        q.criteria("doctor.title").contains(keyWord),
                        q.criteria("doctor.skill").contains(keyWord)
                );
            }
        }
        q.order("name");
        q.offset(pageIndex * pageSize);
        q.limit(pageSize);
        List<User> users = q.asList();
        if (null != users && users.size() > 0) {
            for (User user : users) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("doctorId", user.getUserId());
                userMap.put("name", user.getName());
                userMap.put("hospital", user.getDoctor().getHospital() == null ? "" : user.getDoctor().getHospital());
                userMap.put("skill", getDoctorSkill(user, user.getDoctor().getSkill()));
                userMap.put("departments",
                        user.getDoctor().getDepartments() == null ? "" : user.getDoctor().getDepartments());
                userMap.put("title", user.getDoctor().getTitle());
                userMap.put("headPicFileName", user.getHeadPicFileName());
                userMap.put("cureNum", user.getDoctor().getCureNum());
                userMap.put("cityId", user.getDoctor().getCityId());
                userMap.put("hospitalId", user.getDoctor().getHospitalId());
                map.add(userMap);
            }
        }
        return map;
    }

    @Override
    public List<Integer> findByIdsAndDeptIds(List<Integer> doctorIds, List<String> deptIds) {
        DBObject userQuery = new BasicDBObject();

        BasicDBList userIds = new BasicDBList();
        if (doctorIds != null && doctorIds.size() > 0) {
            for (Integer doctorId : doctorIds) {
                userIds.add(doctorId);
            }
            DBObject userIdObjects = new BasicDBObject();
            userIdObjects.put("$in", userIds);
            userQuery.put("_id", userIdObjects);
        }

        BasicDBList deptsIn = new BasicDBList();
        if (deptIds != null && deptIds.size() > 0) {
            for (String departmentId : deptIds) {
                deptsIn.add(departmentId);
            }
            DBObject deptsInObjects = new BasicDBObject();
            deptsInObjects.put("$in", deptsIn);
            userQuery.put("doctor.deptId", deptsInObjects);
        }

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(userQuery);
        List<Integer> ids = Lists.newArrayList();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            ids.add(MongodbUtil.getInteger(object, "_id"));
        }

        return ids;
    }

    @Override
    public List<Integer> findByIdsAndProvinceId(List<Integer> doctorIds,
                                                String locId, String range) {
        DBObject userQuery = new BasicDBObject();

        BasicDBList userIds = new BasicDBList();
        if (doctorIds != null && doctorIds.size() > 0) {
            for (Integer doctorId : doctorIds) {
                userIds.add(doctorId);
            }
            DBObject userIdObjects = new BasicDBObject();
            userIdObjects.put("$in", userIds);
            userQuery.put("_id", userIdObjects);
        }

        BasicDBList locList = new BasicDBList();
        if (StringUtils.isNotEmpty(locId)) {
            locList.add(Integer.valueOf(locId));
            DBObject areaInObjects = new BasicDBObject();
            if (range.equals("province")) {
                areaInObjects.put("$in", locList);
                userQuery.put("doctor.provinceId", areaInObjects);
            } else if (range.equals("city")) {
                areaInObjects.put("$in", locList);
                userQuery.put("doctor.cityId", areaInObjects);
            } else if (range.equals("country")) {
                areaInObjects.put("$in", locList);
                userQuery.put("doctor.countryId", areaInObjects);
            }
        }

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(userQuery);
        List<Integer> ids = Lists.newArrayList();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            ids.add(MongodbUtil.getInteger(object, "_id"));
        }

        return ids;

    }

    @Override
    public List<Integer> findNoLoctionDoctorsByIds(List<Integer> doctorIds) {
        DBObject userQuery = new BasicDBObject();

        BasicDBList userIds = new BasicDBList();
        if (doctorIds != null && doctorIds.size() > 0) {
            for (Integer doctorId : doctorIds) {
                userIds.add(doctorId);
            }
            DBObject userIdObjects = new BasicDBObject();
            userIdObjects.put("$in", userIds);
            userQuery.put("_id", userIdObjects);
        }

        List<User> users = dsForRW.createQuery(User.class).field("_id").in(doctorIds).asList();
        List<Integer> ids = Lists.newArrayList();

        for (User user : users) {
            if (user.getDoctor() != null && StringUtils.isNotEmpty(user.getDoctor().getProvince())) {
            } else {
                ids.add(user.getUserId());
            }
        }

        return ids;
    }

    @Override
    public boolean updateFeldsherStatus(Integer userId, Integer userStatus) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("status", userStatus);
        ops.set("modifyTime", System.currentTimeMillis());
        UpdateResults ur = dsForRW.update(q, ops);

        if (userStatus == 2) {
            /**
             * 删除当前医生助手的所有token
             */
            UserUtil.clearUserTokens(userId);
        }

        if (ur != null && ur.getUpdatedExisting()) {
            return true;
        }

        return false;
    }

    @Override
    public PageVO getFeldsherList(String keywords, Integer pageIndex, Integer pageSize, Integer userType) {
        //参数校验
        if (userType == null || userType == 0) {
            throw new ServiceException("参数userType不能为空");
        }

        PageVO pageVo = new PageVO();
        if (null == pageIndex) {
            pageIndex = 0;
        }
        if (null == pageSize) {
            pageSize = 15;
        }
        pageVo.setPageIndex(pageIndex);
        pageVo.setPageSize(pageSize);

        Query<User> query = dsForRW.createQuery(User.class);

        //模糊查询
        if (StringUtils.isNotEmpty(keywords)) {
            Pattern pattern = Pattern.compile("^.*" + keywords + ".*$", Pattern.CASE_INSENSITIVE);
            query.or(query.criteria("name").equal(pattern), query.criteria("telephone").equal(pattern));
        }
        query.filter("userType", userType);

        long count = query.countAll();
        int skip = pageIndex * pageSize;
        skip = skip < 0 ? 0 : skip;
        query.order("-createTime");
        query.offset(skip);
        query.limit(pageSize);
        List<User> list = query.asList();
        pageVo.setTotal(count);
        pageVo.setPageData(list);

        return pageVo;
    }

    @Override
    public List<User> getAvailableFeldsherList(Integer userType) {
        //参数校验
        if (userType == null || userType == 0) {
            throw new ServiceException("参数userType不能为空");
        }
        //查询
        Query<User> query = dsForRW.createQuery(User.class);
        query.filter("userType", userType);
        query.filter("status", 1);//启用状态
        query.order("-createTime");//根据创建时间倒序

        return query.asList();
    }

    public List<User> findUsersByIds(List<Integer> doctorIds) {
        List<User> data = Lists.newArrayList();
        if (doctorIds != null && !doctorIds.isEmpty()) {
            Query<User> query = dsForRW.createQuery(User.class)
                    .field("_id").in(doctorIds)
                    .field("status").equal(UserEnum.UserStatus.normal.getIndex())
                    .order("-doctor.cureNum, doctor.titleRank");
            data = query.asList();
        }

        return data;
    }

    public List<User> findUsersWithOutStatusByIds(List<Integer> doctorIds) {
        List<User> data = Lists.newArrayList();
        if (doctorIds != null && !doctorIds.isEmpty()) {
            Query<User> query = dsForRW.createQuery(User.class)
                    .field("_id").in(doctorIds);
            data = query.asList();
        }

        return data;
    }

    @Override
    public List<User> queryDoctorsByAssistantId(String keywords, Integer assistantId) {
        Query<User> query = dsForRW.createQuery(User.class).filter("doctor.assistantId", assistantId);
        //模糊查询
        if (StringUtils.isNotEmpty(keywords)) {
            Pattern pattern = Pattern.compile("^.*" + keywords + ".*$", Pattern.CASE_INSENSITIVE);
            query.and(query.criteria("name").equal(pattern));
        }
        return query.asList();
    }

    @Override
    public PageVO getGroupAddrBook(List<Integer> userIds, Integer areaCode, String deptId, Integer pageIndex, Integer pageSize) {
        DBObject query = new BasicDBObject();

        DBObject userIdsQuery = new BasicDBObject();
        userIdsQuery.put(QueryOperators.IN, userIds);
        query.put("_id", userIdsQuery);

        if (areaCode != null) {
            //组装地区的查询条件
            //根据areaCode，获取全部叶子节点

            List<Integer> areaCodes = baseDataDao.getAllAreaChildByParentId(areaCode);
            areaCodes.add(areaCode);
            DBObject areaQuery = new BasicDBObject();
            areaQuery.put(QueryOperators.IN, areaCodes);
            if (areaCode % 10000 == 00) {
                query.put("doctor.provinceId", areaQuery);
            } else if (areaCode % 100 == 00) {
                query.put("doctor.cityId", areaQuery);
            } else {
                query.put("doctor.countryId", areaQuery);
            }

        }

        if (StringUtils.isNotEmpty(deptId)) {
            //组装科室的查询条件
            //根据deptId，获取全部叶子节点
            List<String> deptIds = baseDataDao.getAllDeptChildByParentId(deptId);
            deptIds.add(deptId);
            DBObject deptQuery = new BasicDBObject();
            deptQuery.put(QueryOperators.IN, deptIds);
            query.put("doctor.deptId", deptQuery);
        }

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).skip(pageIndex * pageSize).limit(pageSize);
        List<Map<String, Object>> groupAddrBooks = Lists.newArrayList();
        while (cursor.hasNext()) {
            DBObject object = cursor.next();
            Map<String, Object> groupAddrBook = Maps.newHashMap();
            Integer id = MongodbUtil.getInteger(object, "_id");
            String name = MongodbUtil.getString(object, "name");
            String headPicFileName = MongodbUtil.getString(object, "headPicFileName");
            groupAddrBook.put("id", id);
            groupAddrBook.put("name", name);
            groupAddrBook.put("headPicFileName", headPicFileName);
            DBObject doctor = (DBObject) object.get("doctor");
            if (doctor != null) {
                String title = MongodbUtil.getString(doctor, "title");
                groupAddrBook.put("title", title);
            }
            groupAddrBooks.add(groupAddrBook);
        }

        PageVO pageVO = new PageVO();
        pageVO.setPageData(groupAddrBooks);
        pageVO.setTotal(Long.valueOf(dsForRW.getDB().getCollection("user").find(query).count()));
        pageVO.setPageSize(pageSize);
        pageVO.setPageIndex(pageIndex);
        return pageVO;
    }

    @Override
    public PageVO getDoctorsByDiseaseId(String diseaseId, Integer pageIndex, Integer pageSize) {
        Query<DiseaseType> q = dsForRW.createQuery(DiseaseType.class);
        q.criteria("_id").equal(diseaseId);
        DiseaseType diseaseTypes = q.get();

        Query<User> query = dsForRW.createQuery(User.class).field("status").equal(UserEnum.UserStatus.normal.getIndex());
        query.or(query.criteria("doctor.expertise").contains(diseaseId),
                query.criteria("doctor.expertise").contains(diseaseTypes.getParent())
        );
        query.order("-doctor.serviceStatus,doctor.titleRank,-doctor.cureNum");
        //.field("doctor.expertise").contains(diseaseId);
        PageVO pageVO = new PageVO();
        pageVO.setTotal(query.countAll());
        query.offset(pageIndex * pageSize);
        query.limit(pageSize);
        pageVO.setPageData(query.asList());
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        return pageVO;
    }

    @Override
    public PageVO getDoctorsByDiseaseIds(List<String> diseaseId, Integer pageIndex, Integer pageSize) {

        //要找对对应的上级菜单的信息
        Query<DiseaseType> q = dsForRW.createQuery(DiseaseType.class);
        q.criteria("_id").in(diseaseId);
        List<DiseaseType> diseaseTypes = q.asList();
        List<String> parseIds = new ArrayList<>();
        for (DiseaseType diseaseType : diseaseTypes) {
            diseaseId.add(diseaseType.getParent());
        }
        Query<User> query = dsForRW.createQuery(User.class).field("status").equal(UserEnum.UserStatus.normal.getIndex());
        Criteria[] arrayA = new Criteria[diseaseId.size()];

        for (int i = 0; i < diseaseId.size(); i++) {
            arrayA[i] = query.criteria("doctor.expertise").contains(diseaseId.get(i));
        }
        query.or(arrayA);
        //Criteria[] arrayB=new Criteria[parseIds.size()];
        //for(int i=0;i<parseIds.size();i++){
        //	arrayB[i]=query.criteria("doctor.expertise").contains(parseIds.get(i));
        //}
        //query.or(arrayB);
        query.order("-doctor.serviceStatus,doctor.titleRank,-doctor.cureNum");
        query.offset(pageIndex * pageSize);
        query.limit(pageSize);
        PageVO pageVO = new PageVO();
        pageVO.setTotal(query.countAll());

        pageVO.setPageData(query.asList());
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        return pageVO;

    }

    @Override
    public PageVO getAllUserIdByUserType(Integer userType, Integer pageIndex, Integer pageSize) {
        PageVO pageVo = new PageVO();
        //参数校验
        if (userType == null || userType == 0) {
            throw new ServiceException("参数userType不能为空");
        }

        List<Integer> status = Lists.newArrayList();
        status.add(UserEnum.UserStatus.normal.getIndex());
        status.add(UserEnum.UserStatus.uncheck.getIndex());
        status.add(UserEnum.UserStatus.fail.getIndex());
        status.add(UserEnum.UserStatus.Unautherized.getIndex());

        Query<User> query = dsForRW.createQuery(User.class)
            .filter("userType", userType)
            .field("status").in(status)
            .retrievedFields(true, "userId");//此处只需返回userId
        long count = query.countAll();
        query.order("userId").limit(pageSize).offset(pageIndex * pageSize);
        List<User> userList = query.asList();
        if (userList != null && userList.size() > 0) {
            List<String> userIds = userList.stream().map(o -> o.getUserId().toString()).collect(Collectors.toList());
            pageVo.setPageData(userIds);
        }
        pageVo.setTotal(count);
        return pageVo;
    }

    @Override
    public List<User> findByNameKeyWordAndUserType(String name, Integer userType) {
        //参数校验
        if (userType == null || userType == 0 || name == null || name.isEmpty()) {
            throw new ServiceException("参数异常");
        }
        Query<User> query = dsForRW.createQuery(User.class).filter("userType", userType).filter("status",
                UserEnum.UserStatus.normal.getIndex());
        query.field("name").contains(name);
        return query.asList();
    }

    @Override
    public List<Integer> getDoctorIds(String name, Integer userType) {

        Query<User> query = dsForRW.createQuery(User.class).filter("userType", userType).filter("status",
                UserEnum.UserStatus.normal.getIndex());
        query.field("name").contains(name);
        List<User> userList = query.asList();
        List<Integer> userIds = new ArrayList<Integer>();
        for (User user : userList) {
            userIds.add(user.getUserId());
        }
        return userIds;
    }

    @Override
    public List<Integer> getDoctorIdsByName(String name) {
        Query<User> query = dsForRW.createQuery(User.class).filter("status", UserEnum.UserStatus.normal.getIndex());
        query.field("name").contains(name);
        List<User> userList = query.asList();
        List<Integer> userIds = new ArrayList<Integer>();
        for (User user : userList) {
            userIds.add(user.getUserId());
        }
        return userIds;
    }

    @Override
    public PageVO getByKeyword(String keyword, Integer pageIndex, Integer pageSize) {
        //组装User表查询条件
        DBObject query = new BasicDBObject();
        Pattern pattern = Pattern.compile("^.*" + StringUtil.trim(keyword) + ".*$", Pattern.CASE_INSENSITIVE);
        BasicDBList or = new BasicDBList();
        or.add(new BasicDBObject("name", pattern));
        or.add(new BasicDBObject("doctor.check.hospital", pattern));
        or.add(new BasicDBObject("doctor.hospital", pattern));
        query.put(QueryOperators.OR, or);
        query.put("userType", UserEnum.UserType.doctor.getIndex());
        DBCollection collection = dsForRW.getDB().getCollection("user");
        DBCursor cursor = collection.find(query).skip(pageIndex * pageSize).limit(pageSize);
        List<Map<String, Object>> result = Lists.newArrayList();
        List<Integer> doctorIds = Lists.newArrayList();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Map<String, Object> objMap = Maps.newHashMap();
            Integer id = MongodbUtil.getInteger(obj, "_id");
            String name = MongodbUtil.getString(obj, "name");
            DBObject doctor = (BasicDBObject) obj.get("doctor");
            String hospital = null;
            String department = null;
            String title = null;
            if (doctor != null) {
                hospital = MongodbUtil.getString(doctor, "hospital");
                department = MongodbUtil.getString(doctor, "departments");
                title = MongodbUtil.getString(doctor, "title");
            }
            Integer status = MongodbUtil.getInteger(obj, "status");
            objMap.put("doctorId", id);
            objMap.put("name", name);
            objMap.put("hospital", hospital);
            objMap.put("department", department);
            objMap.put("title", title);
            objMap.put("status", status);
            objMap.put("group", new ArrayList<Map<String, Object>>());
            result.add(objMap);
            doctorIds.add(id);
        }

        //组装c_group_doctor表的查询条件
        DBObject gdQuery = new BasicDBObject();
        BasicDBList gdDoctorIds = new BasicDBList();
        gdDoctorIds.addAll(doctorIds);
        DBObject gdDoctorIdsIn = new BasicDBObject();
        gdDoctorIdsIn.put(QueryOperators.IN, gdDoctorIds);
        gdQuery.put("doctorId", gdDoctorIdsIn);
        //过滤医生在集团的状态为正常的
        gdQuery.put("status", "C");
        DBCollection groupDoctorCollection = dsForRW.getDB().getCollection("c_group_doctor");
        DBCursor groupDoctorCursor = groupDoctorCollection.find(gdQuery);

        List<String> gIds = Lists.newArrayList();

        while (groupDoctorCursor.hasNext()) {
            DBObject obj = groupDoctorCursor.next();
            String groupId = MongodbUtil.getString(obj, "groupId");
            gIds.add(groupId);
            Integer doctorIdTemp = MongodbUtil.getInteger(obj, "doctorId");
            if (result != null && result.size() > 0) {
                for (Map<String, Object> objMap : result) {
                    Integer doctorId = (Integer) objMap.get("doctorId");
                    if (doctorId.equals(doctorIdTemp)) {
                        List<Map<String, Object>> group = (List<Map<String, Object>>) objMap.get("group");
                        Map<String, Object> groupMap = Maps.newHashMap();
                        groupMap.put("groupId", groupId);
                        group.add(groupMap);
                    }
                }
            }
        }

        //查询集团信息，设置集团名称
        DBObject groupQuery = new BasicDBObject();
        BasicDBList groupIds = new BasicDBList();
        gIds.forEach((groupId) -> {
            groupIds.add(new ObjectId(groupId));
        });
        DBObject groupIdIn = new BasicDBObject();
        groupIdIn.put(QueryOperators.IN, groupIds);
        groupQuery.put("_id", groupIdIn);
        DBCollection groupCollection = dsForRW.getDB().getCollection("c_group");
        DBCursor groupCursor = groupCollection.find(groupQuery);

        while (groupCursor.hasNext()) {
            DBObject obj = groupCursor.next();
            String groupId = MongodbUtil.getString(obj, "_id");
            String groupName = MongodbUtil.getString(obj, "name");
            if (result != null && result.size() > 0) {
                for (Map<String, Object> objMap : result) {
                    List<Map<String, Object>> group = (List<Map<String, Object>>) objMap.get("group");
                    if (group != null && group.size() > 0) {
                        for (Map<String, Object> groupMap : group) {
                            String tempGroupId = (String) groupMap.get("groupId");
                            if (tempGroupId.equals(groupId)) {
                                groupMap.put("name", groupName);
                            }
                        }
                    }
                }
            }
        }

        PageVO pageVO = new PageVO();
        pageVO.setPageData(result);
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        Long total = Long.valueOf(collection.find(query).count());
        pageVO.setTotal(total);

        return pageVO;
    }

    @Override
    public List<Integer> getUserIdsByName(String name, Integer userType) {
        Query<User> query = dsForRW.createQuery(User.class);

        //模糊查询
        if (StringUtils.isNotEmpty(name)) {
            Pattern pattern = Pattern.compile("^.*" + name + ".*$", Pattern.CASE_INSENSITIVE);
            query.criteria("name").equal(pattern);
        }
        query.filter("userType", userType);
        query.retrievedFields(true, "userId");
        List<User> userList = query.asList();

        List<Integer> userIdList = null;

        if (!CollectionUtils.isEmpty(userList)) {
            userIdList = new ArrayList<>();
            for (User user : userList) {
                userIdList.add(user.getUserId());
            }
        }

        return userIdList;
    }

    @Override
    public User findByDoctorNum(String doctorNum) {
        return dsForRW.createQuery(User.class).filter("doctor.doctorNum", doctorNum).filter("userType", UserEnum.UserType.doctor.getIndex()).get();
    }

    @Override
    public List<User> getFarmUser() {
        Query<User> query = dsForRW.createQuery(User.class);
        query.filter("userType", UserType.doctor.getIndex());
        query.filter("farm.isFarm", true);
        return query.asList();
    }

    @Override
    public Set<String> getFarmUserDoctorNums() {
        List<User> farmUsers = getFarmUser();
        Set<String> doctorNums = Sets.newHashSet();
        if (farmUsers != null && farmUsers.size() > 0) {
            for (User user : farmUsers) {
                if (null != user.getDoctor() && StringUtils.isNotEmpty(user.getDoctor().getDoctorNum())) {
                    doctorNums.add(user.getDoctor().getDoctorNum());
                }
            }
        }
        return doctorNums;
    }

    @Override
    public void updateDoctorServiceStatus(Integer userId, Integer serviceStatus) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        if (serviceStatus != null) {
            ops.set("doctor.serviceStatus", serviceStatus);
        }
        ops.set("modifyTime", System.currentTimeMillis());

        dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updateDoctorCheckInGiveStatus(Integer userId, Integer checkInGive) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        if (checkInGive != null) {
            ops.set("doctor.checkInGive", checkInGive);
        }
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updateSubmitTime(Integer userId, Long submitTime) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        if (submitTime != null) {
            ops.set("submitTime", submitTime);
        }
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updateLastLoginTime(Integer userId) {
        Query<User> query = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("lastLoginTime", System.currentTimeMillis());
        dsForRW.findAndModify(query, ops);
    }

    @Override
    public void updateGiveCoin(Integer userId, Integer giveCoin) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        if (giveCoin != null) {
            ops.set("giveCoin", giveCoin);
        }
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }
    
    
    @Override
    public void updateUserLevel(int userId, Integer userLevel,Long limitedPeriodTime) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("userLevel", userLevel);
        ops.set("limitedPeriodTime", limitedPeriodTime);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

	
	@Override
	public List<User> getLimitPeriodUser(Long scanTime) {
		List<Integer> queryLevel = new ArrayList<>();
		queryLevel.add(UserLevel.Tourist.getIndex());
		queryLevel.add(UserLevel.TemporaryUser.getIndex());
        Query<User> q = dsForRW.createQuery(User.class);
        q.field("userType").equal(UserEnum.UserType.doctor.getIndex());        
        q.field("userLevel").in(queryLevel);
        q.field("limitedPeriodTime").greaterThanOrEq(scanTime);
    	q.field("limitedPeriodTime").lessThan(System.currentTimeMillis());
        return q.asList();
    }
	
	
	@Override
    public void batchUserToPeriod(List<Integer> userIds,Integer userLevel) {
        Query<User> query = dsForRW.createQuery(User.class).field("_id").in(userIds);
        UpdateOperations<User> ops = dsForRW
            .createUpdateOperations(User.class);
//        ops.set("userLevel", Objects.nonNull(userLevel)?userLevel:UserLevel.Expire.getIndex());
        dsForRW.update(query, ops);
    }

    @Override
    @Async
    public void saveUserLoginInfo(String serial, String version, String telephone, String phoneModel, String romVersion) {
        UserLoginInfo userLoginInfo = new UserLoginInfo();
        userLoginInfo.setSerial(serial);
        userLoginInfo.setVersion(version);
        userLoginInfo.setLoginTime(System.currentTimeMillis());
        userLoginInfo.setTelephone(telephone);
        userLoginInfo.setPhoneModel(phoneModel);
        userLoginInfo.setRomVersion(romVersion);
        dsForRW.insert(userLoginInfo);
    }

    @Override
    public void updateUserRole(Integer userId, List<String> roleIds) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("roleIds", roleIds);
        dsForRW.findAndModify(q, ops);
    }

	@Override
	public List<Map.Entry<String, Integer>> getDepartmentsTopnByUser(Integer topn,Integer userType, Integer status) {
		Long startTime = System.currentTimeMillis();
		Map<String,Integer> topnDepartments = new HashMap<>();
		BasicDBObject retrievedFields = new BasicDBObject();
		retrievedFields.put("doctor.departments",1);
		DBObject query1 = new BasicDBObject();
		query1.put("status", UserStatus.normal.getIndex());
		query1.put("userType", UserEnum.UserType.doctor.getIndex());
		DBCursor users  = dsForRW.getDB().getCollection("user").find(query1, retrievedFields);
		while (users.hasNext()) {
            DBObject obj = users.next();
            DBObject doctorObject = (DBObject)obj.get("doctor");
            if(Objects.nonNull(doctorObject)){
                String department = MongodbUtil.getString(doctorObject, "departments");
                if(Objects.nonNull(department)){
      				if(topnDepartments.containsKey(department)){
      					topnDepartments.put(department,topnDepartments.get(department)+1);
      				}else{
      					topnDepartments.put(department,1);
      				}
      			}
            }

        }
		logger.info("getDepartmentsTopnByUser.size{}.doTime{}", topnDepartments.size(),System.currentTimeMillis()-startTime);
		List<Map.Entry<String, Integer>> departments = new ArrayList<Map.Entry<String, Integer>>(topnDepartments.entrySet());
		// 排序
		Collections.sort(departments, new Comparator<Map.Entry<String, Integer>>() {
		    public int compare(Map.Entry<String, Integer> o1,
		            Map.Entry<String, Integer> o2) {
		        return -( o1.getValue()-o2.getValue());
		    }
		});
		if(departments.size()<=topn){
			return departments;
		}else{
			return departments.subList(0, topn);
		}
	}


	@Override
	public Long getDoctorNumByDepartments(Integer userType,String department) {
		Query<User> query = dsForRW.createQuery(User.class).field("userType").equal(UserEnum.UserType.doctor.getIndex())
				.field("doctor.departments").equal(department);
		return query.countAll();
	}
	
	
	@Override
	public List<Map<String, Object>> getDepartmentsTopnByUserNew(Integer topn,Integer userType) {
		Long startTime = System.currentTimeMillis();
		Map<String,Integer> topnDepartments = new HashMap<>();
		Map<String,Integer> allDepartments = new HashMap<>();
		BasicDBObject retrievedFields = new BasicDBObject();
		retrievedFields.put("doctor.departments",1);
		retrievedFields.put("status",1);
		DBObject query1 = new BasicDBObject();
		query1.put("userType", UserEnum.UserType.doctor.getIndex());
		DBCursor users  = dsForRW.getDB().getCollection("user").find(query1, retrievedFields);
		while (users.hasNext()) {
            DBObject obj = users.next();
            Integer status = MongodbUtil.getInteger(obj, "status");
            DBObject doctorObject = (DBObject)obj.get("doctor");
            if(Objects.nonNull(doctorObject)){
                String department = MongodbUtil.getString(doctorObject, "departments");
                if(Objects.nonNull(department)){
      				if(allDepartments.containsKey(department)){
      					allDepartments.put(department,allDepartments.get(department)+1);
      				}else{
      					allDepartments.put(department,1);
      				}
      				if(Objects.equals(1,status)){
      					if(topnDepartments.containsKey(department)){
      						topnDepartments.put(department,topnDepartments.get(department)+1);
          				}else{
          					topnDepartments.put(department,1);
          				}
      				}
      			}
            }
        }
		logger.info("getDepartmentsTopnByUserNew.size{}.doTime{}", topnDepartments.size(),System.currentTimeMillis()-startTime);
		List<Map.Entry<String, Integer>> departments = new ArrayList<Map.Entry<String, Integer>>(topnDepartments.entrySet());
		// 排序
		Collections.sort(departments, new Comparator<Map.Entry<String, Integer>>() {
		    public int compare(Map.Entry<String, Integer> o1,
		            Map.Entry<String, Integer> o2) {
		        return -( o1.getValue()-o2.getValue());
		    }
		});
		if(departments.size()>topn){
			departments =  departments.subList(0, topn);
		}
		List<Map<String,Object>> listMap = new ArrayList<>();
		for(int i=0;i<departments.size();i++){
			Entry<String,Integer> ent=departments.get(i);
			Map<String,Object> map = new HashMap<>();
			map.put("department", ent.getKey());
			map.put("certifyDoctors", ent.getValue());
			map.put("regDoctors",allDepartments.containsKey(ent.getKey())?allDepartments.get(ent.getKey()):0);
			listMap.add(map);
		}
		return listMap;
	}

    @Override
    public void updateSuspend(Integer userId, Integer suspend) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("suspend", suspend);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public List<Long> findUserIdsByDeptId(String deptId, int pageIndex, int pageSize, int offset) {
        DBObject fiflter = new BasicDBObject("doctor.deptId", deptId);
        fiflter.put("status", UserStatus.normal.getIndex());
        fiflter.put("userType", UserType.doctor.getIndex());
        
        DBObject orderBy = new BasicDBObject("_id", 1);
        BasicDBObject retrievedFields = new BasicDBObject("_id", 1);
        
        DBCursor resultCursor  = dsForRW.getDB().getCollection("user").find(fiflter, retrievedFields).sort(orderBy);
        resultCursor.skip((pageIndex * pageSize) + offset).limit(pageSize);
        
        List<Long> idsList = new ArrayList<>();
        while (resultCursor.hasNext()) {
            idsList.add(MongodbUtil.getLong(resultCursor.next(), "_id"));
        }
        return idsList;
    }

    @Override
    public void updateUserRoleIds(Integer userId, List<String> roleIds) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(userId);
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("roleIds", roleIds);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public void updateAdminUser(User user, AddAdminUserParam addAdminUserParam) {
        Query<User> q = dsForRW.createQuery(User.class).field("_id").equal(user.getUserId());
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("modifyTime", System.currentTimeMillis());
        if (!Objects.equals(user.getName(), addAdminUserParam.getName())) {
            ops.set("name", addAdminUserParam.getName());
        }
        if (!Objects.equals(user.getTelephone(), addAdminUserParam.getTelephone())) {
            ops.set("telephone", addAdminUserParam.getTelephone());
        }
        if (!Objects.equals(user.getRoleIds(), addAdminUserParam.getRoleIds())) {
            ops.set("roleIds", addAdminUserParam.getRoleIds());
        }
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public PageVO findDoctorsByParamCondition(DoctorParam param, List<String> phones) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Query<User> q = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.doctor.getIndex())
                               .filter("status", UserEnum.UserStatus.normal.getIndex());
        // 按照关键字查询
        if (!StringUtil.isEmpty(param.getKeyWord())) {
            Pattern pattern = Pattern.compile("^.*" + param.getKeyWord() + ".*$", Pattern.CASE_INSENSITIVE);
            q.or(q.criteria("name").equal(pattern), q.criteria("telephone").equal(pattern),
                    q.criteria("doctor.hospital").equal(pattern));
        }
        if(!CollectionUtils.isEmpty(phones)) {
            q.field("telephone").notIn(phones);
        }
        q.offset((param.getPageIndex()) * param.getPageSize());
        q.order("-createTime");
        q.limit(param.getPageSize());
        List<User> user = q.asList();
        if (null != user && user.size() > 0) {
            for (User u : user) {
                Map<String, Object> userMap = new HashMap<String, Object>();
                userMap.put("userId", u.getUserId());
                userMap.put("name", u.getName());
                userMap.put("telephone", u.getTelephone());
                userMap.put("createTime", u.getCreateTime());
                userMap.put("status", u.getStatus());
                userMap.put("title", u.getDoctor().getTitle());
                userMap.put("departments", u.getDoctor().getDepartments());
                userMap.put("hospital", u.getDoctor().getHospital());
                userMap.put("headPicFileName", u.getHeadPicFileName());
                list.add(userMap);
            }
        }
        PageVO page = new PageVO();
        if (param.getPageIndex() >= 0) {
            page.setPageIndex(param.getPageIndex());
        }
        if (param.getPageSize() >= 0) {
            page.setPageSize(param.getPageSize());
        }
        page.setTotal(q.countAll());
        page.setPageData(list);
        return page;
    }

    @Override
    public PageVO getUsersByTelList(List<String> telList, Integer pageIndex, Integer pageSize) {
        Query<User> query = dsForRW.createQuery(User.class).field("userType").equal(UserType.doctor.getIndex());
        query.criteria("telephone").in(telList);
        PageVO pageVO = new PageVO();
        pageVO.setTotal(query.countAll());
        query.offset(pageIndex * pageSize);
        query.limit(pageSize);
        pageVO.setPageData(query.asList());
        pageVO.setPageIndex(pageIndex);
        pageVO.setPageSize(pageSize);
        return pageVO;
    }

    @Override
    public String addLearningExperience(LearningExperienceParam param, Integer userId) {
        LearningExperience copy = BeanUtil.copy(param, LearningExperience.class);
        copy.setUserId(userId);
        long timeMillis = System.currentTimeMillis();
        copy.setCreateTime(timeMillis);
        copy.setModifyTime(timeMillis);
        return dsForRW.insert(copy).getId().toString();
    }

    @Override
    public void updateLearningExperience(LearningExperienceParam param) {
        Query<LearningExperience> q = dsForRW.createQuery(LearningExperience.class).field(Mapper.ID_KEY).equal(new ObjectId(param.getId()));
        UpdateOperations<LearningExperience> ops = dsForRW.createUpdateOperations(LearningExperience.class);
        if (StringUtils.isNotBlank(param.getCollegeId())) {
            ops.set("collegeId", param.getCollegeId());
        }
        if (StringUtils.isNotBlank(param.getCollegeName())) {
            ops.set("collegeName", param.getCollegeName());
        }
        if (StringUtils.isNotBlank(param.getQualifications())) {
            ops.set("qualifications", param.getQualifications());
        }
        if (StringUtils.isNotBlank(param.getDepartments())) {
            ops.set("departments", param.getDepartments());
        }
        if (Objects.nonNull(param.getStartTime())) {
            ops.set("startTime", param.getStartTime());
        }
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(q, ops);
    }

    @Override
    public List<LearningExperience> getLearningExperience(Integer userId) {
        Query<LearningExperience> query = dsForRW.createQuery(LearningExperience.class);
        query.field("userId").equal(userId);
        query.order("-createTime");
        return query.asList();
    }

    @Override
    public Boolean delLearningExperience(String id) {
        Query<LearningExperience> query = dsForRW.createQuery(LearningExperience.class);
        query.field(Mapper.ID_KEY).equal(new ObjectId(id));
        return dsForRW.delete(query).getN() > 0;
    }

    @Override
    public Integer getLearningExpCount(Integer userId) {
        Query<LearningExperience> query = dsForRW.createQuery(LearningExperience.class);
        query.field("userId").equal(userId);
        return query.asList().size();
    }

    @Override
    public Boolean delLearningExpByUserId(Integer userId) {
        Query<LearningExperience> query = dsForRW.createQuery(LearningExperience.class);
        query.field("userId").equal(userId);
        return dsForRW.delete(query).getN() > 0;
    }

    @Override
    public void addCustomCollege(CustomCollegeParam param) {
        CustomCollege copy = BeanUtil.copy(param, CustomCollege.class);
        copy.setLearningExpId(param.getLearningExpId());
        // 未处理状态
        copy.setStatus(0);
        long timeMillis = System.currentTimeMillis();
        copy.setCreateTime(timeMillis);
        copy.setModifyTime(timeMillis);
        dsForRW.insert(copy);
    }

    @Override
    public void checkCustomCollege(String learningExpId, String checkCollegeName, String checkCollegeId) {
        Query<CustomCollege> query = dsForRW.createQuery(CustomCollege.class);
        query.field("learningExpId").equal(learningExpId);
        query.field("status").equal(0);
        UpdateOperations<CustomCollege> ops = dsForRW.createUpdateOperations(CustomCollege.class);
        ops.set("checkCollegeName", checkCollegeName);
        ops.set("checkCollegeId", checkCollegeId);
        // 已处理状态
        ops.set("status", 1);
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(query, ops);
    }

    @Override
    public PageVO getCustomCollege(CustomCollegeParam param) {
        if (Objects.isNull(param.getPageSize())) {
            param.setPageSize(15);
        }
        if (Objects.isNull(param.getPageIndex())) {
            param.setPageIndex(0);
        }
        PageVO pageVO = new PageVO();
        pageVO.setPageIndex(param.getPageIndex());
        pageVO.setPageSize(param.getPageSize());
        Query<CustomCollege> query = dsForRW.createQuery(CustomCollege.class);
        query.field("status").equal(param.getStatus());
        if (StringUtils.isNotBlank(param.getCustomCollegeName())) {
            query.field("customCollegeName").contains(param.getCustomCollegeName());
        }
        query.order("-createTime");
        pageVO.setTotal(query.countAll());
        query.offset(param.getPageIndex() * param.getPageSize());
        query.limit(param.getPageSize());
        pageVO.setPageData(query.asList());
        return pageVO;
    }

    @Override
    public Boolean existCustomCollege(String learningExpId) {
        DBObject query = new BasicDBObject();
        DBObject projection = new BasicDBObject();
        query.put("learningExpId", learningExpId);
        query.put("status", 0);
        int count = dsForRW.getDB().getCollection("t_custom_college").find(query, projection).count();
        return count > 0;
    }

    @Override
    public void updateCustomCollege(CustomCollegeParam param) {
        Query<CustomCollege> query = dsForRW.createQuery(CustomCollege.class);
        query.field("learningExpId").equal(param.getLearningExpId());
        UpdateOperations<CustomCollege> ops = dsForRW.createUpdateOperations(CustomCollege.class);
        ops.set("customCollegeName", param.getCustomCollegeName());
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.findAndModify(query, ops);
    }

    @Override
    public List<User> getUsersByTelSet(Set<String> telSet) {
        Query<User> query = dsForRW.createQuery(User.class).field("userType").equal(UserType.doctor.getIndex()).field("telephone").in(telSet);
        return query.asList();
    }

    @Override
    public void updateLearningExperienceMul(LearningExperienceParam param) {
        Query<LearningExperience> q = dsForRW.createQuery(LearningExperience.class).field("collegeId").equal(param.getCollegeId());
        UpdateOperations<LearningExperience> ops = dsForRW.createUpdateOperations(LearningExperience.class);
        if (StringUtils.isNotBlank(param.getCollegeId())) {
            ops.set("collegeId", param.getCollegeId());
        }
        if (StringUtils.isNotBlank(param.getCollegeName())) {
            ops.set("collegeName", param.getCollegeName());
        }
        ops.set("modifyTime", System.currentTimeMillis());
        dsForRW.update(q, ops);
    }

}