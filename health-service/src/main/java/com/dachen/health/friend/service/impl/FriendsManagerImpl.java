package com.dachen.health.friend.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.jedis.JedisTemplate;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CEnterpriseUser;
import com.dachen.drugorg.api.entity.CSimpleUser;
import com.dachen.health.base.constant.FriendReqStatus;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.InviteWayEnum;
import com.dachen.health.commons.constants.UserEnum.RelationStatus;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserLevel;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.friend.dao.FriendsRepository;
import com.dachen.health.friend.dao.IFriendReqDao;
import com.dachen.health.friend.entity.param.FriendReqQuery;
import com.dachen.health.friend.entity.po.DoctorAssistant;
import com.dachen.health.friend.entity.po.DoctorFriend;
import com.dachen.health.friend.entity.po.DoctorPatient;
import com.dachen.health.friend.entity.po.EnterpriseUserDoctorFriend;
import com.dachen.health.friend.entity.po.FriendReq;
import com.dachen.health.friend.entity.po.FriendSetting;
import com.dachen.health.friend.entity.po.PatientFriend;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.user.entity.vo.UserDetailVO;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.component.ShortUrlComponent;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Resource;
import org.bson.types.ObjectId;
import org.mongodb.morphia.AdvancedDatastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class FriendsManagerImpl implements FriendsManager {

    //private static final String groupCode = "110";

    //private static Logger Log = LoggerFactory.getLogger(FriendsManager.class);

    @Autowired
    private FriendsRepository friendsRepository;

    @Autowired
    private UserManager userService;

    @Autowired
    private IFriendReqDao friendReqDao;
    
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    private IBusinessServiceMsg businessMsgService;

    @Resource
    protected AdvancedDatastore dsForRW;

    @Resource(name = "jedisTemplate")
    protected JedisTemplate jedisTemplate;

    @Autowired
    private IBaseDataService baseDataService;

    @Resource
    private MobSmsSdk mobSmsSdk;

    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    private static String drugorg_url = PropertiesUtil.getContextProperty("drugorg.server");

    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;
    
    @Autowired
	private OperationLogMqProducer operationLogMqProducer;

    @Override
    public boolean addFriends(Integer userId, Integer toUserId) {
        if (userId.equals(toUserId)) {// 不能添加自己为好友
            throw new ServiceException("不能添加自己");
        }
        User user = dsForRW.createQuery(User.class).field("_id").equal(userId).get();
        User tuser = dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();

        if (user == null || tuser == null) {
            throw new ServiceException("找不到用户");
        }

        if (UserType.patient.getIndex() == user.getUserType() && UserType.patient.getIndex() == tuser.getUserType()) {// 患者好友

            PatientFriend frd = dsForRW.createQuery(PatientFriend.class).field("userId").equal(userId).field("toUserId").equal(toUserId).get();
            if (frd == null) {// 我未加对方
                frd = new PatientFriend();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(userId);
                frd.setToUserId(toUserId);
                FriendSetting setting = new FriendSetting();
                frd.setSetting(setting);
                dsForRW.save(frd);

                frd = new PatientFriend();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(toUserId);
                frd.setToUserId(userId);
                setting = new FriendSetting();
                frd.setSetting(setting);
                dsForRW.save(frd);
                return true;
            } else {// 我已经加对方
                throw new ServiceException("已经是好友");
            }

        } else if (UserType.doctor.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {// 医生好友
            DoctorFriend frd = dsForRW.createQuery(DoctorFriend.class).field("userId").equal(userId).field("toUserId").equal(toUserId).get();
            if (frd == null) {// 我未加对方
                frd = new DoctorFriend();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(userId);
                frd.setToUserId(toUserId);

                FriendSetting setting = new FriendSetting();
                frd.setSetting(setting);

                dsForRW.save(frd);

                frd = new DoctorFriend();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(toUserId);
                frd.setToUserId(userId);

                setting = new FriendSetting();
                frd.setSetting(setting);

                dsForRW.save(frd);
                Map<String,String> opeartorLog = new HashMap<>();
                opeartorLog.put("operationType",OperationLogTypeDesc.DOCOTORLEVELCHANGE);
				if (Objects.equals(UserLevel.Tourist.getIndex(), tuser.getBaseUserLevel())) {
					// 医生圈添加好友，如果是游客，需要变更游客为临时会员，并且，到期时间改为永久  by xuhuanjie
					userRepository.updateUserLevel(tuser.getUserId(), UserLevel.TemporaryUser.getIndex(),
							UserEnum.FOREVER_LIMITED_PERIOD);
					// 发送审核指令
					businessServiceMsg.sendEventForDoctor(tuser.getUserId(), tuser.getUserId(), tuser.getStatus(),
							tuser.getName(), UserEnum.UserLevel.TemporaryUser.getIndex(),
							UserEnum.FOREVER_LIMITED_PERIOD);

					opeartorLog.put("content",
							String.format("%1$s医生添加%2$s为好友为升级为临时用户,有效期由(%3$s)变为(%4$s)", user.getTelephone(),
									tuser.getTelephone(), DateUtil.formatDate2Str(tuser.getLimitedPeriodTime(), null),
									DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
				}    
				if (!CollectionUtils.isEmpty(opeartorLog) && Objects.equals(opeartorLog.size(), 2)) {
                	/*operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),opeartorLog.get("operationType"),opeartorLog.get("content"));*/
                	userService.userInfoChangeNotify(userId);
                }   
                
                return true;
            } else {// 我已经加对方
                throw new ServiceException("已经是好友");
            }

        } else if (UserType.doctor.getIndex() == user.getUserType() && UserType.assistant.getIndex() == tuser.getUserType()
                || UserType.assistant.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {// 医助好友

            DoctorAssistant frd = dsForRW.createQuery(DoctorAssistant.class).field("userId").equal(userId).field("toUserId").equal(toUserId).get();
            if (frd == null) {// 我未加对方
                frd = new DoctorAssistant();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(userId);
                frd.setToUserId(toUserId);

                FriendSetting setting = new FriendSetting();
                frd.setSetting(setting);
                dsForRW.save(frd);

                frd = new DoctorAssistant();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(toUserId);
                frd.setToUserId(userId);

                setting = new FriendSetting();
                frd.setSetting(setting);
                dsForRW.save(frd);
                return true;
            } else {// 我已经加对方
                throw new ServiceException("已经是好友");
            }

        }
        if (UserType.doctor.getIndex() == user.getUserType() && UserType.patient.getIndex() == tuser.getUserType()
                || UserType.patient.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {// 医患好友

            DoctorPatient frd = dsForRW.createQuery(DoctorPatient.class).field("userId").equal(userId).field("toUserId")
                    .equal(toUserId).get();
            if (frd == null) {// 我未加对方
                frd = new DoctorPatient();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(userId);
                frd.setToUserId(toUserId);

                FriendSetting setting = new FriendSetting();
                frd.setSetting(setting);
                dsForRW.save(frd);

                frd = new DoctorPatient();
                frd.setCreateTime(System.currentTimeMillis());
                frd.setStatus(RelationStatus.normal.getIndex());
                frd.setUserId(toUserId);
                frd.setToUserId(userId);

                setting = new FriendSetting();
                frd.setSetting(setting);
                dsForRW.save(frd);
                return true;
            } else {// 我已经加对方
                throw new ServiceException("已经是好友");
            }

        }

        return true;

    }

    @Override
    public boolean deleteFriends(Integer userId, Integer toUserId) throws HttpApiException {
        if (userId.equals(toUserId)) {
            throw new ServiceException("不能删除自己");
        }
        User user = dsForRW.createQuery(User.class).field("_id").equal(userId).get();
        User tuser = dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();

        //若tuser为空，则需要去药企查询用户数据

        boolean isEnterpriseUser = false;
        List<CEnterpriseUser> enterpriseUserList = null;
        if (Objects.isNull(tuser)) {
            enterpriseUserList = drugOrgApiClientProxy.getByUserId(Lists.newArrayList(toUserId));
            if (!CollectionUtils.isEmpty(enterpriseUserList)) {
                isEnterpriseUser = true;
            }
        }

        if (Objects.isNull(user) || (Objects.isNull(tuser) && CollectionUtils.isEmpty(enterpriseUserList))) {
            throw new ServiceException("找不到用户");
        }

        // 判断是否为同集团
        if (isSameGroup(userId, toUserId)) {
            delGroupFriend(userId, toUserId);
            return true;
        }

        if (isEnterpriseUser) {
            //删除医生和医药代表好友关系
            deleteFriends(userId, toUserId, EnterpriseUserDoctorFriend.class);
        } else {
            if (UserType.patient.getIndex() == user.getUserType() && UserType.patient.getIndex() == tuser.getUserType()) {
                deleteFriends(userId, toUserId, PatientFriend.class);
            } else if (UserType.doctor.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {
                deleteFriends(userId, toUserId, DoctorFriend.class);
            } else if (UserType.patient.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()
                    || UserType.doctor.getIndex() == user.getUserType() && UserType.patient.getIndex() == tuser.getUserType()) {
                deleteFriends(userId, toUserId, DoctorPatient.class);
            } else if (UserType.assistant.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()
                    || UserType.doctor.getIndex() == user.getUserType() && UserType.assistant.getIndex() == tuser.getUserType()) {
                deleteFriends(userId, toUserId, DoctorAssistant.class);
            }
        }

        businessMsgService.userChangeNotify(UserChangeTypeEnum.DEL_FRIEND, userId, toUserId);

        return true;

    }


    @Override
    public List<Object> queryBlacklist(Integer userId) {
        return friendsRepository.queryBlacklist(userId);
    }


    @Override
    public boolean setFriends(Integer userId, Integer toUserId, FriendSetting settings) {
        User user = dsForRW.createQuery(User.class).field("_id").equal(userId).get();
        User tuser = dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();
        if (settings != null) {
            int c = settings.verifys();
            if (c == 0) {
                throw new ServiceException("setting is not contain correct value");
            }
        } else {
            throw new ServiceException("setting is null");
        }
        if (user == null || tuser == null) {
            throw new ServiceException("找不到用户");
        }


        Integer defriend = settings.getDefriend();
        Integer messageMasking = settings.getMessageMasking();
        boolean needUpdateCache = false;
        FriendSetting oldSetting = new FriendSetting();
        if (UserType.patient.getIndex() == user.getUserType() && UserType.patient.getIndex() == tuser.getUserType()) {// 患者好友

            PatientFriend frd = dsForRW.createQuery(PatientFriend.class).field("userId").equal(userId)
                    .field("toUserId").equal(toUserId).get();
            if (frd != null) {
                if (frd.getSetting() != null) {
                    oldSetting = frd.getSetting();
                }
                UpdateOperations<PatientFriend> ops = dsForRW.createUpdateOperations(PatientFriend.class);

                if (settings.getCollection() != null) {
                    ops.set("setting.collection", settings.getCollection());

                }
                if (settings.getDefriend() != null) {
                    ops.set("setting.defriend", settings.getDefriend());
                    needUpdateCache = true;

                }
                if (settings.getMessageMasking() != null) {
                    ops.set("setting.messageMasking", settings.getMessageMasking());
                    needUpdateCache = true;

                }
                if (settings.getTopNews() != null) {
                    ops.set("setting.topNews", settings.getTopNews());

                }

                dsForRW.update(frd, ops);

                // return true;
            } else {// 我未加对方
                // return false;
                throw new ServiceException("还不是好友");
            }

        } else if (UserType.doctor.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {// 医生好友

            DoctorFriend frd = dsForRW.createQuery(DoctorFriend.class).field("userId").equal(userId).field("toUserId").equal(toUserId).get();
            if (frd != null) {
                if (frd.getSetting() != null) {
                    oldSetting = frd.getSetting();
                }
                UpdateOperations<DoctorFriend> ops = dsForRW.createUpdateOperations(DoctorFriend.class);

                if (settings.getCollection() != null) {
                    ops.set("setting.collection", settings.getCollection());

                }
                if (settings.getDefriend() != null) {
                    ops.set("setting.defriend", settings.getDefriend());
                    needUpdateCache = true;

                }
                if (settings.getMessageMasking() != null) {
                    ops.set("setting.messageMasking", settings.getMessageMasking());
                    needUpdateCache = true;

                }
                if (settings.getTopNews() != null) {
                    ops.set("setting.topNews", settings.getTopNews());

                }

                dsForRW.update(frd, ops);

                // return true;
            } else {// 我已经加对方
                throw new ServiceException("还不是好友");
            }

        } else if (UserType.doctor.getIndex() == user.getUserType() && UserType.patient.getIndex() == tuser.getUserType()
                || UserType.patient.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {// 医患好友

            DoctorPatient frd = dsForRW.createQuery(DoctorPatient.class).field("userId").equal(userId)
                    .field("toUserId").equal(toUserId).get();
            if (frd != null) {
                if (frd.getSetting() != null) {
                    oldSetting = frd.getSetting();
                }
                UpdateOperations<DoctorPatient> ops = dsForRW.createUpdateOperations(DoctorPatient.class);

                if (settings.getCollection() != null) {
                    ops.set("setting.collection", settings.getCollection());

                }
                if (settings.getDefriend() != null) {
                    ops.set("setting.defriend", settings.getDefriend());
                    needUpdateCache = true;

                }
                if (settings.getMessageMasking() != null) {
                    ops.set("setting.messageMasking", settings.getMessageMasking());
                    needUpdateCache = true;

                }
                if (settings.getTopNews() != null) {
                    ops.set("setting.topNews", settings.getTopNews());

                }

                dsForRW.update(frd, ops);

                // return true;
            } else {// 我已经加对方
                throw new ServiceException("还不是好友");
            }

        } else if (UserType.doctor.getIndex() == user.getUserType() && UserType.assistant.getIndex() == tuser.getUserType()
                || UserType.assistant.getIndex() == user.getUserType() && UserType.doctor.getIndex() == tuser.getUserType()) {// 医患好友

            DoctorAssistant frd = dsForRW.createQuery(DoctorAssistant.class).field("userId").equal(userId)
                    .field("toUserId").equal(toUserId).get();
            if (frd != null) {
                if (frd.getSetting() != null) {
                    oldSetting = frd.getSetting();
                }
                UpdateOperations<DoctorAssistant> ops = dsForRW.createUpdateOperations(DoctorAssistant.class);

                if (settings.getCollection() != null) {
                    ops.set("setting.collection", settings.getCollection());

                }
                if (settings.getDefriend() != null) {
                    ops.set("setting.defriend", settings.getDefriend());
                    needUpdateCache = true;

                }
                if (settings.getMessageMasking() != null) {
                    ops.set("setting.messageMasking", settings.getMessageMasking());
                    needUpdateCache = true;

                }
                if (settings.getTopNews() != null) {
                    ops.set("setting.topNews", settings.getTopNews());

                }

                dsForRW.update(frd, ops);

                // return true;
            } else {// 我未加对方
                throw new ServiceException("还不是好友");
            }

        }

//		update cache
//		if (needUpdateCache) {
//			String relations_setting = jedisTemplate.get(KeyGenerate.buildRelationSettingKey(userId, toUserId));
//			if (defriend == null || messageMasking == null) {
//				if (relations_setting != null) {
//					String strs[] = relations_setting.split(",");
//					if (strs != null && strs.length == 2) {
//						if (defriend == null) {
//							defriend = Integer.parseInt(strs[0]);
//						}
//						if (messageMasking == null) {
//							messageMasking = Integer.parseInt(strs[1]);
//						}
//					} else {
//						 Log.warn(KeyGenerate.buildRelationSettingKey(userId,
//						 toUserId)+"struct has change ");
//					}
//				}
//			}
//			if (defriend == null) {
//				defriend = oldSetting.getDefriend();
//				if (defriend == null) {
//					defriend = 1;
//				}
//			}
//			if (messageMasking == null) {
//				messageMasking = oldSetting.getMessageMasking();
//				if (messageMasking == null) {
//					messageMasking = 1;
//				}
//			}
//			jedisTemplate.set(KeyGenerate.buildRelationSettingKey(userId, toUserId), defriend+","+messageMasking);
//		}


        return true;

    }

    /**
     * 该函数有明显的问题
     * 搜索之后发现 只有医患关系 会调用该函数 所以只修改了医患关系类型的判断
     * 建议后面不要使用该函数
     */
    @Override
    @Deprecated
    public Object getFriend(Integer userId, Integer toUserId) {
        User user = userService.getUser(userId);
        User tUser = userService.getUser(toUserId);
        if (user != null && tUser != null) {
            int userType = user.getUserType();
            int tuserType = tUser.getUserType();
            if (UserType.patient.getIndex() == userType
                    && UserType.patient.getIndex() == tuserType) {
                PatientFriend f = dsForRW.createQuery(PatientFriend.class).field("userId").equal(userId).field("toUserId").equal(userId).get();
                return f;
            }
            if (UserType.doctor.getIndex() == userType
                    && UserType.doctor.getIndex() == tuserType) {
                DoctorFriend f = dsForRW.createQuery(DoctorFriend.class).field("userId").equal(userId).field("toUserId").equal(userId).get();
                return f;
            }

            if (UserType.patient.getIndex() == userType
                    && UserType.doctor.getIndex() == tuserType
                    || UserType.doctor.getIndex() == userType
                    && UserType.patient.getIndex() == tuserType) {
                DoctorPatient f = dsForRW.createQuery(DoctorPatient.class).field("userId").equal(userId).field("toUserId").equal(toUserId).get();
                return f;
            }
            if (UserType.assistant.getIndex() == userType
                    && UserType.doctor.getIndex() == tuserType
                    || UserType.doctor.getIndex() == userType
                    && UserType.assistant.getIndex() == tuserType) {

                DoctorAssistant f = dsForRW.createQuery(DoctorAssistant.class).field("userId").equal(userId).field("toUserId").equal(userId).get();
                return f;

            }

        }
        return null;
    }

    @Override
    public Object getSessionList(int userId, int queryType, long lastTime) {


        Map<String, Object> data = new HashMap<String, Object>();

        //查询条件
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);

        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_assistant").find(query, fields);


        List<Integer> relationId = new ArrayList<Integer>();//关系id列表

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        cursor = dsForRW.getDB().getCollection("u_doctor_friend").find(query, fields);

        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query, fields);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        cursor = dsForRW.getDB().getCollection("u_patient_friend").find(query, fields);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            relationId.add((Integer) obj.get("toUserId"));
        }

        //List<UserDetailVO> list = new ArrayList<UserDetailVO>();
        List<Object> list = new ArrayList<Object>();
        List<Object> confirmMsg = new ArrayList<Object>();

        if (relationId.size() > 0) {
            // 查找用户信息
            DBObject uquery = new BasicDBObject();
            uquery.put("_id", new BasicDBObject("$in", relationId));
            // uquery.put("status", UserEnum.UserStatus.normal.getIndex());

            DBObject ufield = new BasicDBObject();
            ufield.put("_id", 1);
            ufield.put("name", 1);
            ufield.put("telephone", 1);
            ufield.put("sex", 1);
            ufield.put("birthday", 1);
            ufield.put("doctor", 1);
            ufield.put("headPicFileName", 1);
            ufield.put("userType", 1);

            DBCursor userCursor = dsForRW.getDB().getCollection("user").find(uquery, ufield);
            while (userCursor.hasNext()) {
                DBObject obj = userCursor.next();
                UserDetailVO vo = new UserDetailVO();
                vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
                vo.setName(MongodbUtil.getString(obj, "name"));
                vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
                vo.setSex(MongodbUtil.getInteger(obj, "sex"));
                vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
                vo.setUserType(MongodbUtil.getInteger(obj, "userType"));

                Long birthday = MongodbUtil.getLong(obj, "birthday");
                if (birthday != null) {
                    vo.setAge(DateUtil.yearDiff(new Date(), new Date(birthday)));
                }

                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if (doctor != null) {
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                }

                //tigase 消息查询
                DBObject subquery = new BasicDBObject();
                DBObject or = new BasicDBObject();
                or.put("receiver", vo.getUserId());
                or.put("sender", userId);

                DBObject or1 = new BasicDBObject();
                or1.put("receiver", userId);
                or1.put("sender", vo.getUserId());
                subquery.put("$or", new DBObject[]{or});//,or1
                subquery.put("type", new BasicDBObject("$ne", 12));

                DBObject sort = new BasicDBObject();
                sort.put("ts", -1);

                boolean hasMsg = false;
                if (hasMsg) {
                    list.add(vo);
                }
                hasMsg = false;
                subquery.put("type", 12);
                UserDetailVO v = new UserDetailVO();
                try {
                    v = vo.deepClone();
                } catch (ClassNotFoundException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                if (hasMsg) {
                    confirmMsg.add(v);
                }
                //tigase 消息查询

            }

        }


        data.put("msgs", list);
        data.put("confirmMsgs", confirmMsg);
        return data;

    }


    @Override
    public Object getMsgsByUser(int userId, int toUserId, long lastTime) {

        // 查找用户信息
        DBObject uquery = new BasicDBObject();
        uquery.put("_id", toUserId);
        // uquery.put("status", UserEnum.UserStatus.normal.getIndex());

        DBObject ufield = new BasicDBObject();
        ufield.put("_id", 1);
        ufield.put("name", 1);
        ufield.put("telephone", 1);
        ufield.put("sex", 1);
        ufield.put("birthday", 1);
        ufield.put("doctor", 1);
        ufield.put("headPicFileName", 1);
        ufield.put("userType", 1);

        DBCursor userCursor = dsForRW.getDB().getCollection("user").find(uquery, ufield);
        while (userCursor.hasNext()) {
            DBObject obj = userCursor.next();
            UserDetailVO vo = new UserDetailVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            vo.setSex(MongodbUtil.getInteger(obj, "sex"));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
            vo.setUserType(MongodbUtil.getInteger(obj, "userType"));

            Long birthday = MongodbUtil.getLong(obj, "birthday");
            if (birthday != null) {
                vo.setAge(DateUtil.yearDiff(new Date(), new Date(birthday)));
            }

            DBObject doctor = (BasicDBObject) obj.get("doctor");
            if (doctor != null) {
                vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                vo.setTitle(MongodbUtil.getString(doctor, "title"));
            }

            //tigase 消息查询
            DBObject subquery = new BasicDBObject();
            DBObject or = new BasicDBObject();
            or.put("receiver", toUserId);
            or.put("sender", userId);
            DBObject or1 = new BasicDBObject();
            or1.put("receiver", userId);
            or1.put("sender", toUserId);
            subquery.put("$or", new DBObject[]{or});
            subquery.put("type", new BasicDBObject("$ne", 12));

            subquery.put("ts", new BasicDBObject("$lte", lastTime));

            DBObject sort = new BasicDBObject();
            sort.put("ts", -1);

            List<Object> msgs = new ArrayList<Object>();
            vo.setMsg(msgs);
            //tigase 消息查询
            return vo;

        }


        throw new ServiceException("找不到对应用户");


    }


    /**
     * 发送好友验证请求
     * before: 验证是不是好友，如果是，则不做操作
     * 1、增加一条等待验证的好友验证请求；
     * 2、给B发送一条好友验证消息，通知B更新验证请求；
     */
    public void sendApplyAddFriend(Integer userId, Integer toUserId, String applyContent) throws HttpApiException {

        DoctorFriend df = dsForRW.createQuery(DoctorFriend.class)
                .field("userId").equal(userId)
                .field("toUserId").equal(toUserId)
                .field("status").equal(RelationStatus.normal.getIndex())
                .get();
        if (df != null) {
            //已经是好友
            return;
        }
        FriendReq untreatedFriendReq = friendReqDao.getUnTreatedFriendReq(userId, toUserId);
        FriendReq existFriendReq = friendReqDao.getFriendReq(userId, toUserId);
        if (untreatedFriendReq != null) {
            untreatedFriendReq.setApplyContent(applyContent);
            untreatedFriendReq.setCreateTime(System.currentTimeMillis());
            untreatedFriendReq.setStatus(FriendReqStatus.WAIT_ACCEPT.getValue());
            friendReqDao.update(untreatedFriendReq);
        } else if (existFriendReq != null) {//删除了好友关系，再发好友申请，修改原来的申请状态为“等待验证”
            existFriendReq.setApplyContent(applyContent);
            existFriendReq.setCreateTime(System.currentTimeMillis());
            existFriendReq.setStatus(FriendReqStatus.WAIT_ACCEPT.getValue());
            friendReqDao.update(existFriendReq);
        } else {
            FriendReq friendReq = new FriendReq();
            friendReq.setFromUserId(userId);
            friendReq.setToUserId(toUserId);
            friendReq.setApplyContent(applyContent);
            friendReq.setStatus(FriendReqStatus.WAIT_ACCEPT.getValue());
            friendReq.setCreateTime(System.currentTimeMillis());
            friendReqDao.save(friendReq);
        }

        businessMsgService.userChangeNotify(UserChangeTypeEnum.FRIEND_REQ_CHANGE, userId, toUserId);
    }


    /**
     * 加好友
     * 1、将A和B加为好友；
     * 2、增加一条已添加的好友验证请求；
     * 3、给B发一个已加好友的消息，通知B更新验证请求和通讯录；
     * 4、给A发送一个已加B为好友的通知消息；
     */
    public User applyAddFriend(Integer userId, Integer toUserId, boolean isNeedFriendReq) throws HttpApiException {

        boolean isAdd = addFriends(userId, toUserId);
        if (!isAdd) {
            throw new ServiceException("加好友失败！");
        }
        if (isNeedFriendReq) {
            FriendReq friendReq = new FriendReq();
            friendReq.setFromUserId(userId);
            friendReq.setToUserId(toUserId);
            friendReq.setApplyContent("已加为好友！");
            friendReq.setStatus(FriendReqStatus.ACCEPTED.getValue());
            friendReq.setCreateTime(System.currentTimeMillis());
            friendReqDao.save(friendReq);
        }

        User toUser = dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();

        businessMsgService.userChangeNotify(UserChangeTypeEnum.ADD_FRIEND, userId, toUserId);

        return toUser;
    }


    /**
     * 处理验证请求
     * 1、将好友验证请求置为已接受；
     * 2、将A和B加为好友；
     * 3、给A发送一个消息，通知更新验证请求和更新通讯录
     * 4、给B发一个已加A为好友的通知
     */

    public void replyAddFriend(String id, int result) throws HttpApiException {
        if (result != FriendReqStatus.ACCEPTED.getValue()) {
            throw new ServiceException(1000101, "处理状态不正确！");
        }
        FriendReq friendReq = friendReqDao.getFriendReqById(id);
        applyAddFriend(friendReq.getFromUserId(), friendReq.getToUserId(), false);

        friendReq.setStatus(result);
        friendReq.setUpdateTime(System.currentTimeMillis());
        friendReqDao.update(friendReq);

    }


    /**
     * 获取该用户所有的好友请求
     */
    public PageVO getFriendReq(FriendReqQuery friendReqQuery) throws HttpApiException {
        PageVO pageVO = friendReqDao.queryFriendReq(friendReqQuery);
        //进入新的好友界面时，需要调用IM接口清除该会话组的未读消息数
        String groupId = friendReqQuery.getGroupId();
        if (StringUtils.isEmpty(groupId)) {
            groupId = SysGroupEnum.FRIEND_DOCTOR.getValue();
        }
        UpdateGroupRequestMessage imMsg = new UpdateGroupRequestMessage();
        imMsg.setAct(5);
        imMsg.setFromUserId(String.valueOf(friendReqQuery.getUserId()));
        imMsg.setGid(groupId);
        MsgHelper.updateGroup(imMsg);

        setFromUserInfo(pageVO);//设置医药代表姓名及头相
        return pageVO;
    }

    private void setFromUserInfo(PageVO page) throws HttpApiException {
        if (page == null || page.getPageData() == null) {
            return;
        }
        List data = page.getPageData();
        List<Integer> idList = new ArrayList<Integer>();
        for (Object obj : data) {
            BasicDBObject dbObj = (BasicDBObject) obj;
            if (dbObj.getInt("userReqType") == 1) {//医药代表
                idList.add(dbObj.getInt("fromUserId"));
            }
        }
        if (idList.isEmpty()) {
            return;
        }
        Map<Integer, CSimpleUser> userInfo = getUserInfo(idList);
        for (Object obj : data) {
            BasicDBObject dbObj = (BasicDBObject) obj;
            if (userInfo.containsKey(dbObj.getInt("fromUserId"))) {
                dbObj.put("fromUserName", userInfo.get(dbObj.getInt("fromUserId")).getName());
                dbObj.put("fromHeadPicFileName", userInfo.get(dbObj.getInt("fromUserId")).getHeadPic());
            }
        }
    }

    public Map<Integer, CSimpleUser> getUserInfo(List<Integer> idList) throws HttpApiException {

        List<CSimpleUser> result = drugOrgApiClientProxy.getSimpleUser(idList);

        Map<Integer, CSimpleUser> mapresult = Maps.newConcurrentMap();
        for (CSimpleUser user : result) {
            mapresult.put(user.getId(), user);
        }
        return mapresult;
    }

    /**
     * 获取该用户所有的好友请求
     */
    public FriendReq getFriendReqById(String id) {
        return friendReqDao.getFriendReqById(id);
    }

    public boolean addFriendReq(FriendReq friendReq) {
        friendReq.setStatus(FriendReqStatus.ACCEPTED.getValue());
        friendReq.setCreateTime(System.currentTimeMillis());
        friendReq.setUpdateTime(System.currentTimeMillis());
        friendReqDao.save(friendReq);
        return true;
    }


    /**
     * 添加手机联系人
     * 1、根据当前用户类型，如果是医生，则添加医生，如果是患者，则添加患者，目前先添加医生
     * 2、判断如果对方是平台医生，则判断对方是否需要验证，如果需要，则发送验证请求；如果不需要，则直接加为好友；
     * 3、如果对方不是平台医生，则提示用户
     */
    public Map addPhoneFriend(Integer userId, String phone) throws HttpApiException {
        Map map = new HashMap();
        User user = userService.getUser(userId);
        User toUser = userService.getUser(phone, user.getUserType());
        if (toUser == null || toUser.getUserType() != UserType.doctor.getIndex()) {
            map.put("state", 3);
            map.put("msg", "对方不是平台医生！");
        } else if (friendReqDao.getFriendReq(toUser.getUserId(), user.getUserId(),
                FriendReqStatus.WAIT_ACCEPT.getValue()) != null) {
            replyAddFriend(friendReqDao
                    .getFriendReq(toUser.getUserId(), user.getUserId(), FriendReqStatus.WAIT_ACCEPT.getValue()).getId()
                    .toString(), FriendReqStatus.ACCEPTED.getValue());
            map.put("state", 1);
            map.put("msg", "已添加为好友");
            map.put("user", toUser);
        } else if (toUser != null && toUser.getSettings().getDoctorVerify() == 1) {// 需要验证
            map.put("state", 2);
            map.put("msg", "对方需要验证！");
            map.put("userId", toUser.getUserId());
            // sendApplyAddFriend( userId, user.getUserId(),"来自手机联系人请求");
        } else {
            map.put("state", 1);
            map.put("msg", "已添加为好友");
            toUser = applyAddFriend(userId, toUser.getUserId(), true);
            map.put("user", toUser);
        }
        return map;
    }


    @Autowired
    protected ShortUrlComponent shortUrlComponent;

    @Autowired
    protected AsyncTaskPool asyncTaskPool;

    protected Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 发送邀请短信
     */
    public void sendInviteMsgAsync(int userId, String phone) {
        this.asyncTaskPool.getPool().submit(new Runnable() {
            @Override
            public void run() {
                try {
                    sendInviteMsg(userId, phone);
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    public void sendInviteMsg(int userId, String phone) throws HttpApiException {
        User user = userService.getUser(userId);
        String content = "";
        //"0011" : {0}邀请您加入{1}，请点击{2} 下载";
        if (mobSmsSdk.isBDJL(user)) {//根據邀請人確認是博德還是玄關
            String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.download.bdjl"));
            content = baseDataService.toContent("0011", user.getName(), BaseConstants.BD_DOC_APP, generateUrl);
        } else if (ReqUtil.instance.isMedicalCircle()) {
            /**修改成从应用宝获取应用**/
            //String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
            String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
            content = baseDataService.toContent("0011", user.getName(), BaseConstants.XG_DOC_MEDICAL_CIRCLE, generateUrl);
        } else {
//            String generateUrl = shortUrlComponent.generateShortUrl(PropertiesUtil.getContextProperty("invite.url") + PropertiesUtil.getContextProperty("invite.registerJoin.MedicalCircle"));
            String shortUrl = getDoctorInviteShortUrl(userId, PropertiesUtil.getContextProperty("inviteActivityId"),
                PropertiesUtil.getContextProperty("registerActivityId"), InviteWayEnum.sms.name(), Source.doctorCircle.getIndex());
            content = baseDataService.toContent("0011", user.getName(), BaseConstants.XG_YSQ_APP, shortUrl);
        }
        String signature = mobSmsSdk.isBDJL(user) ? BaseConstants.BD_SIGN : BaseConstants.XG_YSQ_APP;
        mobSmsSdk.send(phone, content, signature);
    }

    private String getDoctorInviteShortUrl(Integer doctorId, String inviteActivityId,
        String registerActivityId, String way, Integer subsystem) throws HttpApiException {
        String tag = "getDoctorInviteShortUrl";
        StringBuilder sb = new StringBuilder();
//        sb.append(this.getConfigValue("invite.url"));
//        sb.append(this.getConfigValue("invite.joinDoctorCircle"));
        sb.append(PropertiesUtil.getContextProperty("activity.invite.qiniu.url"));

        sb.append("?");
        sb.append("inviterId=");//邀请人Id
        sb.append(doctorId);
        sb.append("&inviteActivityId=");//邀请活动Id
        sb.append(inviteActivityId);
        sb.append("&registerActivityId=");//注册活动Id
        sb.append(registerActivityId);
        sb.append("&subsystem=");//来源子系统
        sb.append(subsystem);
        sb.append("&way=");//邀请方式
        sb.append(way);
        sb.append("&ts=");//时间戳
        sb.append(System.currentTimeMillis());

        String url = sb.toString();
        logger.debug("{}. url={}", tag, url);

        return shortUrlComponent.generateShortUrl(url);
    }

    /**
     * 添加同集团好友（单向，不需要验证或对方同意）
     *
     * @param userId
     * @param toUserId
     */
    @Override
    public User addGroupFriend(Integer userId, Integer toUserId) {
        if (userId.equals(toUserId)) {// 不能添加自己为好友
            throw new ServiceException("不能添加自己");
        }
        DoctorFriend frd = dsForRW.createQuery(DoctorFriend.class).field("userId").equal(userId).field("toUserId").equal(toUserId).get();
        if (frd == null) {// 我未加对方
            frd = new DoctorFriend();
            frd.setCreateTime(System.currentTimeMillis());
            frd.setStatus(RelationStatus.normal.getIndex());
            frd.setUserId(userId);
            frd.setToUserId(toUserId);

            FriendSetting setting = new FriendSetting();
            frd.setSetting(setting);

            dsForRW.save(frd);
        } else {// 我已经加对方
            throw new ServiceException("已经是好友");
        }
        return dsForRW.createQuery(User.class).field("_id").equal(toUserId).get();
    }

    /**
     * 删除同集团好友（单向）
     *
     * @param userId
     * @param toUserId
     */
    @Override
    public void delGroupFriend(Integer userId, Integer toUserId) {
        Query<DoctorFriend> query = dsForRW.createQuery(DoctorFriend.class).field("userId").equal(userId).field("toUserId").equal(toUserId);
        dsForRW.delete(query);
    }

    /**
     * 删除非同集团好友（双向）
     *
     * @param userId
     * @param toUserId
     * @param clazz
     */
    private <T> void deleteFriends(Integer userId, Integer toUserId, Class<T> clazz) {
        Query<T> query = dsForRW.createQuery(clazz).field("userId").equal(userId).field("toUserId").equal(toUserId);
        dsForRW.delete(query);

        query = dsForRW.createQuery(clazz).field("userId").equal(toUserId).field("toUserId").equal(userId);
        dsForRW.delete(query);
    }

    public <T> List<T> getFriends(Integer userId, Class<T> clazz) {
        Query<T> q = dsForRW.createQuery(clazz).field("userId").equal(userId);
        q.filter("status", UserEnum.RelationStatus.normal.getIndex());
        q.filter("setting.defriend", 1);
        return q.asList();
    }

    /**
     * 判断是否为同集团
     *
     * @param userId
     * @param toUserId
     * @return
     */
    private boolean isSameGroup(Integer userId, Integer toUserId) {
        DBObject fields = new BasicDBObject();
        fields.put("groupId", 1);

        DBObject userQuery = new BasicDBObject();
        userQuery.put("doctorId", userId);
        DBCursor userCursor = dsForRW.getDB().getCollection("c_group_doctor").find(userQuery, fields);

        List<String> userGroupIds = Lists.newArrayList();
        while (Objects.nonNull(userCursor) && userCursor.hasNext()) {
            DBObject object = userCursor.next();
            userGroupIds.add(MongodbUtil.getString(object, "groupId"));
        }

        DBObject toUserQuery = new BasicDBObject();
        toUserQuery.put("doctorId", toUserId);
        DBCursor toUserCursor = dsForRW.getDB().getCollection("c_group_doctor").find(toUserQuery, fields);

        List<String> toUserGroupIds = Lists.newArrayList();
        while (Objects.nonNull(toUserCursor) && toUserCursor.hasNext()) {
            DBObject object = toUserCursor.next();
            toUserGroupIds.add(MongodbUtil.getString(object, "groupId"));
        }

        //若两个人所在的集团的id的交集存在，则为相同集团
        if (!CollectionUtils.isEmpty(userGroupIds) && !CollectionUtils.isEmpty(toUserGroupIds) && userGroupIds.retainAll(toUserGroupIds)) {
            return true;
        }
        return false;
    }

    public <T> boolean friends(Integer userId, Integer toUserId, Class<T> clazz) {
        return dsForRW.createQuery(clazz).filter("userId", userId).filter("toUserId", toUserId).get() != null;
    }

    @Override
    public FriendReq getUnTreatedFriendReq(Integer userId, Integer toUserId) {
        return friendReqDao.getUnTreatedFriendReq(userId, toUserId);
    }

    @Override
    public FriendReq getFriendReq(Integer userId, Integer toUserId) {
        return friendReqDao.getFriendReq(userId, toUserId);
    }

    @Override
    public boolean deleteFriendReqById(String id) {
        try {
            friendReqDao.deleteFriendReqById(new ObjectId(id));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean addFriendReqOfDrugOrg(FriendReq friendReq) {
        if (friendReq == null) throw new ServiceException("参数不能为null");
        friendReqDao.save(friendReq);
        return true;
    }

    @Override
    public boolean updateFriendReq(FriendReq friendReq) {
        if (friendReq == null) throw new ServiceException("参数不能为null");
        if (friendReq.getId() == null) throw new ServiceException("更新的Id不能为null");
        FriendReq f = getFriendReqById(friendReq.getId().toString());
        if (f == null) throw new ServiceException("更新的数据不存在。");
        if (friendReq.getApplyContent() != null)
            f.setApplyContent(friendReq.getApplyContent());
        if (friendReq.getCreateTime() != 0)
            f.setCreateTime(friendReq.getCreateTime());
        f.setUpdateTime(System.currentTimeMillis());
        f.setStatus(friendReq.getStatus());
        friendReqDao.update(f);

        Map<String,String> opeartorLog = new HashMap<>();
        opeartorLog.put("operationType",OperationLogTypeDesc.DOCOTORLEVELCHANGE);
        //如果是医药代表邀请的需要修改用户的级别和过期时间
		if (Objects.equals(f.getUserReqType(), 1)) {
			User user = userService.getUser(f.getToUserId());
			if (Objects.nonNull(user) && Objects.equals(UserLevel.Tourist.getIndex(), user.getBaseUserLevel())
					&& Objects.equals(user.getUserType(), UserType.doctor.getIndex())) {
				// 医药代表加好友，如果是游客，需要变更游客为临时会员，并且，到期时间改为永久 by xuhuanjie
				userRepository.updateUserLevel(user.getUserId(), UserLevel.TemporaryUser.getIndex(),
						UserEnum.FOREVER_LIMITED_PERIOD);
				// 发送审核指令
				businessServiceMsg.sendEventForDoctor(user.getUserId(), user.getUserId(), user.getStatus(),
						user.getName(), UserEnum.UserLevel.TemporaryUser.getIndex(), UserEnum.FOREVER_LIMITED_PERIOD);
				opeartorLog.put("content",
						String.format("%1$s医药代表添加(%2$s)为好友升级为临时用户,有效期由(%3$s)变为(%4$s)", friendReq.getFromUserName(),
								user.getTelephone(), DateUtil.formatDate2Str(user.getLimitedPeriodTime(), null),
								DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD)));
			}
		}
		if (!CollectionUtils.isEmpty(opeartorLog) && Objects.equals(opeartorLog.size(), 2)) {
        	/*operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),opeartorLog.get("operationType"),opeartorLog.get("content"));*/
        	userService.userInfoChangeNotify(f.getToUserId());
        } 
        
        return true;
    }

    @Override
    public List<Integer> getFriendReqListByUserId(Integer userId) {

        List<Integer> doctorList = new ArrayList<Integer>();

        Set<String> set = new HashSet<String>();

        //获取我添加的搜有的医生好友
        List<FriendReq> fromList = friendReqDao.getFriendReqListByUserId(userId, "fromUserId");
        if (null != fromList && fromList.size() > 0) {
            clearRepeatFromUserList(fromList, set);
        }
        //获取加我的所有的医生好友
        List<FriendReq> toUserList = friendReqDao.getFriendReqListByUserId(userId, "toUserId");
        if (null != toUserList && toUserList.size() > 0) {
            clearRepeatToUserList(toUserList, set);
        }
        //转换成整形
        setDoctorUserList(set, doctorList);

        return doctorList;
    }

    public List<Integer> getMyDocIds(Integer userId) {
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("status", UserEnum.RelationStatus.normal.getIndex());
        query.put("setting.defriend", 1);

        //返回字段
        DBObject fields = new BasicDBObject();
        fields.put("toUserId", 1);
        fields.put("setting", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query, fields);

        List<Integer> relationId = new ArrayList<Integer>();//关系id列表
        Map<Integer, Object> objMap = new HashMap<Integer, Object>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            Integer toUserId = (Integer) obj.get("toUserId");
            relationId.add(toUserId);

            objMap.put(toUserId, (BasicDBObject) obj.get("setting"));
        }
        cursor.close();
        return relationId;
    }

    private void setDoctorUserList(Set<String> set, List<Integer> doctorList) {
        if (null != set && set.size() > 0) {
            Iterator<String> iterator = set.iterator();
            while (iterator.hasNext()) {
                String next = iterator.next();
                if (StringUtils.isNotEmpty(next)) {
                    doctorList.add(Integer.parseInt(next));
                }
            }
        }
    }

    private void clearRepeatFromUserList(List<FriendReq> fromList, Set<String> set) {
        if (null != fromList && fromList.size() > 0) {
            for (FriendReq req : fromList) {
                Integer toUserId = req.getToUserId();
                if (null != toUserId && toUserId.intValue() > 0) {
                    set.add(toUserId.toString());
                }
            }
        }
    }

    private void clearRepeatToUserList(List<FriendReq> toUserList, Set<String> set) {
        if (null != toUserList && toUserList.size() > 0) {
            for (FriendReq req : toUserList) {
                Integer fromUserId = req.getFromUserId();
                if (null != fromUserId && fromUserId.intValue() > 0) {
                    set.add(fromUserId.toString());
                }
            }
        }
    }

    @Override
    public Map getFriends(List<Integer> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return null;
        }

        Map<Integer, Map> firstMap = new HashMap<>();
        for (Integer userId : userIdList) {
            firstMap.put(userId, getSecondMap(userId));
        }

        return firstMap;
    }

    private Map<Integer, Map> getSecondMap(Integer userId) {
        List<Integer> toUserIdList = getToUserIdList(userId);
        Map<Integer, Map> secondMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(toUserIdList)) {
            for (Integer toUserId : toUserIdList) {
                secondMap.put(toUserId, getThirdMap(toUserId));
            }
        }
        return secondMap;
    }

    private Map<Integer, List> getThirdMap(Integer userId) {
        List<Integer> toUserIdList = getToUserIdList(userId);
        Map<Integer, List> thirdMap = new HashMap<>();
        if (!CollectionUtils.isEmpty(toUserIdList)) {
            for (Integer toUserId : toUserIdList) {
                thirdMap.put(toUserId, getToUserIdList(toUserId));
            }
        }
        return thirdMap;
    }

    private List<Integer> getToUserIdList(Integer userId) {
        return friendsRepository.getToUserIdList(userId);
    }

}
