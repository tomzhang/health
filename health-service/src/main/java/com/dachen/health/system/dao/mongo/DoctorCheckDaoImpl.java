package com.dachen.health.system.dao.mongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CSimpleUser;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.AreaVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.entity.vo.TitleVO;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.ICircleService;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.health.commons.service.impl.FaceRecognitionService;
import com.dachen.health.commons.vo.*;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.operationLog.constant.OperationLogTypeDesc;
import com.dachen.health.operationLog.mq.OperationLogMqProducer;
import com.dachen.health.system.dao.IDoctorCheckDao;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.health.system.entity.param.DoctorNameParam;
import com.dachen.health.system.entity.param.FindDoctorByAuthStatusParam;
import com.dachen.health.system.entity.vo.DoctorCheckVO;
import com.dachen.health.user.entity.po.NurseImage;
import com.dachen.health.user.service.INurseService;
import com.dachen.manager.RemoteServiceResult;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * ProjectName： health-service<br>
 * ClassName： DoctorCheckDaoImpl<br>
 * Description： 医生审核dao实现类<br>
 * 
 * @author fanp
 * @crateTime 2015年7月6日
 * @version 1.0.0
 */
@Repository
public class DoctorCheckDaoImpl extends NoSqlRepository implements IDoctorCheckDao {
    private static final Logger logger = LoggerFactory.getLogger(DoctorCheckDaoImpl.class);

    @Autowired
    private IBaseDataDao baseDataDao;
    
    @Autowired
    private INurseService nurseService;
    
    @Autowired
    private DiseaseTypeRepository diseaseTypeRepository;
    
	@Autowired
    private IQrCodeService qrCodeService;
	
	@Autowired
    private ICircleService circleService;

    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
	private OperationLogMqProducer operationLogMqProducer;

    @Autowired
    private Auth2Helper auth2Helper;

    @Autowired
    private RibbonManager ribbonManager;

    @Autowired
    private FaceRecognitionService faceRecognitionService;

    /**
     * </p>获取所有医生
     * 	update by wangl 
     *  扩大筛选类型将护士也选择出来
     * </p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public PageVO getDoctors(DoctorCheckParam param) {
        DBObject query = new BasicDBObject();
        Integer status = param.getStatus();
        if (StringUtil.isNotBlank(param.getName())) {
            //模糊查询，姓名、手机、医院
            Pattern pattern = Pattern.compile("^.*" + StringUtil.trim(param.getName()) + ".*$", Pattern.CASE_INSENSITIVE);
            BasicDBList or = new BasicDBList();
            or.add(new BasicDBObject("name", pattern));
            or.add(new BasicDBObject("telephone", pattern));
            or.add(new BasicDBObject("doctor.title", pattern));// 职称
            if (status != null && status == UserEnum.UserStatus.normal.getIndex()) {
                or.add(new BasicDBObject("doctor.check.hospital", pattern));
            } else {
                or.add(new BasicDBObject("doctor.hospital", pattern));
            }
            BasicDBList in = new BasicDBList();
            //模糊查询还要支持邀请人（2016-05-23傅永德）
            DBObject inviterQuery = new BasicDBObject();
            BasicDBList inviterOr = new BasicDBList();
            inviterOr.add(new BasicDBObject("userType", 3));
            inviterOr.add(new BasicDBObject("userType", 4));
            inviterQuery.put("name", pattern);
            inviterQuery.put("$or", inviterOr);
            DBCursor inviterCursor = dsForRW.getDB().getCollection("user").find(inviterQuery);
            while (inviterCursor.hasNext()) {
                DBObject obj = inviterCursor.next();
                in.add(obj.get("_id"));
            }
            if (in != null && in.size() > 0) {
                or.add(new BasicDBObject("source.inviterId", new BasicDBObject("$in", in)));
            }
            query.put("$or", or);
        } else if (StringUtil.isNotBlank(param.getQDoctorName()) || StringUtil.isNotBlank(param.getQHospital()) || StringUtil.isNotBlank(param.getQTelephone()) || StringUtil.isNotBlank(param.getQTitle()) || StringUtil.isNotBlank(param.getQInviteName())) {
            //模糊查询，姓名、手机、医院
            if (StringUtil.isNotBlank(param.getQDoctorName())) {
                query.put("name", param.getQDoctorName());
            } else if (StringUtil.isNotBlank(param.getQHospital())) {
                if (status != null && status == UserEnum.UserStatus.normal.getIndex()) {
                    query.put("doctor.check.hospital", param.getQHospital());
                } else {
                    query.put("doctor.hospital", param.getQHospital());
                }
            } else if (StringUtil.isNotBlank(param.getQTelephone())) {
                query.put("telephone", param.getQTelephone());
            } else if (StringUtil.isNotBlank(param.getQTitle())) {
                query.put("doctor.title", param.getQTitle());
            } else if (StringUtil.isNotBlank(param.getQInviteName())) {
                BasicDBList in = new BasicDBList();
                //模糊查询还要支持邀请人（2016-05-23傅永德）
                DBObject inviteQuery = new BasicDBObject();
                BasicDBList inviteOr = new BasicDBList();
                inviteOr.add(new BasicDBObject("userType", 3));
                inviteOr.add(new BasicDBObject("userType", 4));
                inviteQuery.put("name", param.getQInviteName());
                inviteQuery.put("$or", inviteOr);
                DBCursor inviteCursor = dsForRW.getDB().getCollection("user").find(inviteQuery);
                while (inviteCursor.hasNext()) {
                    DBObject obj = inviteCursor.next();
                    in.add(obj.get("_id"));
                }
                // 此处为空就没必要再走下去
                if (CollectionUtils.isEmpty(in)) {
                    PageVO page = new PageVO();
                    page.setPageData(Lists.newArrayList());
                    return page;
                }
                query.put("source.inviterId", new BasicDBObject("$in", in));
            }
        }
        
        //修改了排序（2016-05-20傅永德   待审核：按提交时间排序；已通过/未通过：按审核时间排序；未认证：按创建账号时间排序）
        DBObject sortField = new BasicDBObject();
        
        if (status != null) {
            query.put("status", status);// 审核状态
            // 不显示已挂起或禁用状态的用户
            query.put("suspend", UserConstant.SuspendStatus.normal.getIndex());
            //如果查未审核状态的，则同时增加医生的医院不为null
            if(status==UserStatus.uncheck.getIndex()) {
                //待审核，按照修改时间的降序
                sortField.put("modifyTime", -1);
            } else if (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex()) {
            	//已审核和审核未通过，按照审核时间的降序
            	sortField.put("doctor.check.checkTime", -1);
			} else if (status==UserStatus.Unautherized.getIndex()) {
				//未认证按照创建时间的降序
				sortField.put("createTime", -1);
			} else {
	        	//原来的排序规则
	        	sortField.put("modifyTime", -1);
	            sortField.put("createTime", -1);
	            sortField.put("_id", -1);
			}
        } else {
        	//原来的排序规则
        	sortField.put("modifyTime", -1);
            sortField.put("createTime", -1);
            sortField.put("_id", -1);
		}

        // 挂起或禁用列表
        if (Objects.nonNull(param.getSuspend())) {
            query.put("suspend", param.getSuspend());
        }

        /**
         * 修改时间查询条件
         * 输入参数中存在开始时间或结束时间才进行时间过滤
         * 如果开始时间和结束时间都为空，则不进行时间过滤
         */

        if(Objects.nonNull(param.getStartTime()) || Objects.nonNull(param.getEndTime())){
            DBObject timeQuery = new BasicDBObject();
            Long start = param.getStartTime();
            Long end = param.getEndTime();
            if(Objects.nonNull(start)){
                timeQuery.put("$gte", start);
            }

            if(Objects.nonNull(end)){
                timeQuery.put("$lt", end);
            }

            if (status == null){
                query.put("createTime", timeQuery);
            } else if(status == UserStatus.normal.getIndex() || status == UserStatus.fail.getIndex()) {
                query.put("doctor.check.checkTime", timeQuery);
            } else if (status == UserStatus.Unautherized.getIndex()) {
                query.put("createTime", timeQuery);
            } else if (status == UserStatus.uncheck.getIndex()) {
                query.put("submitTime", timeQuery);
            } else {
                query.put("createTime", timeQuery);
            }
        }

        /**
         * 添加医生科室查询
         */
        if(StringUtil.isNotBlank(param.getDeptId())){
            query.put("doctor.deptId", param.getDeptId().trim());
        }

        List<Integer> types = new ArrayList<Integer>();
        types.add(UserEnum.UserType.doctor.getIndex());
        types.add(UserEnum.UserType.nurse.getIndex());
        query.put("userType", new BasicDBObject("$in" , types));

        // 集团下的成员 查询（包含审核状态 ） add by tanyf 2016-06-02
        if(!CollectionUtils.isEmpty(param.getDoctorIds())){
        	query.put("_id", new BasicDBObject("$in" , param.getDoctorIds()));
        }

        DBCollection collection = dsForRW.getDB().getCollection("user");
        DBCursor cursor = collection.find(query).sort(sortField).skip(param.getStart()).limit(param.getPageSize());

        //查询用户学币
        Set<Object> userIdSet = new HashSet<Object>();
        for(DBObject user:cursor){
            userIdSet.add(user.get("_id"));
        }
        Map balanceMap = getUsersIntegral(userIdSet);


        List<DoctorCheckVO> list = new ArrayList<DoctorCheckVO>();
        
        List<Integer> inviterIds = Lists.newArrayList();
        List<String> groupIds = Lists.newArrayList();
        
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            DoctorCheckVO vo = new DoctorCheckVO(); 
            
            Integer userId = (Integer) obj.get("_id");

            //获取用户学币
            String idStr = String.valueOf(userId);
            Map integralMap = (Map)balanceMap.get(idStr);
            if(integralMap != null){
                Object oIntegral = integralMap.get("balance");
                Integer integral = oIntegral == null ? null:Integer.parseInt(String.valueOf(oIntegral));
                vo.setIntegral(integral);
            }
            vo.setUserId(userId);
            vo.setUserLevel((Integer) obj.get("userLevel"));
            vo.setLimitedPeriodTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "limitedPeriodTime"), null));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setTelephone(obj.get("telephone")==null ? "" : obj.get("telephone").toString());
            Long createTime = MongodbUtil.getLong(obj, "createTime");
            vo.setCreateTime(DateUtil.formatDate2Str(createTime, null));
            if (status!=null && status==UserStatus.Unautherized.getIndex()) {
            	vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "createTime"), null));
        	}
            if(null!= obj.get("modifyTime")){

				String modifyTimeStr = obj.get("modifyTime")==null ? "" : obj.get("modifyTime").toString();
            	Long modifyTime = Long.valueOf(modifyTimeStr);
            	
            	//未审核列表认证时间
            	if(status!=null && status==UserStatus.uncheck.getIndex()) {
            		vo.setOrderTime(DateUtil.formatDate2Str(modifyTime, null));
            		Long submitTime = MongodbUtil.getLong(obj, "submitTime");
            		if (null != submitTime) {
						vo.setCreateTime(DateUtil.formatDate2Str(submitTime, null));
					}
            	}
            }
            vo.setStatus((Integer) obj.get("status"));

            vo.setSuspend((Integer) obj.get("suspend"));
            
            int userType = MongodbUtil.getInteger(obj, "userType");
            
            vo.setUserType(userType);
            
            DBObject source = (BasicDBObject) obj.get("source");
            if (source!= null) {
                vo.setSubsystem(MongodbUtil.getInteger(source, "sourceType"));//来源子系统
				Integer inviterId = MongodbUtil.getInteger(source, "inviterId");
				if (inviterId != null) {
					vo.setInviterId(inviterId);
					inviterIds.add(inviterId);
				} else {
					vo.setInviterName("");
				}
				String groupId = MongodbUtil.getString(source, "groupId");
				vo.setSourceGroupId(groupId);
				groupIds.add(groupId);
				Integer sourceType = MongodbUtil.getInteger(source, "sourceType");
				vo.setSourceType(sourceType);
				if (sourceType == null) {
					vo.setSource("");
				} else {
                    //case里面的值参考 UserEnum.Source.app.getIndex();
                    UserEnum.Source sourceEnum = UserEnum.Source.getEnum(sourceType.intValue());
                    if (sourceEnum != null) {
                        vo.setSource(sourceEnum.getSource());
                    }
                }
            } else {
				vo.setSource("");
				vo.setInviterName("");
			}
            
            if(UserEnum.UserType.doctor.getIndex() == userType){
                 vo.setHeadPicFileName(UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"), vo.getUserId(), MongodbUtil.getInteger(obj, "sex"), userType));
                 DBObject doctor = (BasicDBObject) obj.get("doctor");
                 if (doctor != null) {
                     // 审核通过取审核信息，审核不通过取职业信息
                     if (vo.getStatus() != null && vo.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
                         // 审核通过
                         DBObject check = (BasicDBObject) doctor.get("check");
                         if (check != null) {
                             vo.setHospitalId(MongodbUtil.getString(check, "hospitalId"));
                             vo.setHospital(MongodbUtil.getString(check, "hospital"));
                             vo.setDepartments(MongodbUtil.getString(check, "departments"));
                             vo.setTitle(MongodbUtil.getString(check, "title"));
                             vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                             vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                             vo.setSkill(MongodbUtil.getString(doctor, "skill"));
                             if (status!=null && (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex())) {
                            	 vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                         	 }
                         }
                     } else {
                         // 审核不通过或未审核
                    	 DBObject check = (BasicDBObject) doctor.get("check");
                    	 vo.setHospitalId(MongodbUtil.getString(doctor, "hospitalId"));
                         vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                         vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                         vo.setTitle(MongodbUtil.getString(doctor, "title"));
                         vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                         if (check != null) {
                        	 vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                        	 if (status!=null && (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex())) {
                            	 vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                         	}
                         }
                     }
                 }
            }else if(UserEnum.UserType.nurse.getIndex() == userType){
            	  /**
            	   * 获取护士的图片数组
            	   */
            	  List<NurseImage> nurseImageList =  nurseService.getNurseImageList(userId);
            	  vo.setNurseImageList(nurseImageList);
            	  DBObject nurse = (BasicDBObject) obj.get("nurse");
            	  if(nurse != null){
            		  // 审核通过取审核信息，审核不通过取职业信息
                      if (vo.getStatus() != null && vo.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
                          // 审核通过
                          DBObject check = (BasicDBObject) nurse.get("check");
                          if (check != null) {
                              vo.setHospital(MongodbUtil.getString(check, "hospital"));
                              vo.setDepartments(MongodbUtil.getString(check, "departments"));
                              vo.setTitle(MongodbUtil.getString(check, "title"));
                              vo.setNurseNum(MongodbUtil.getString(nurse, "nurseNum"));
                              vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                              if (status!=null && (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex())) {
                             	 vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                          	  }
                          }
                      } else {
                          // 审核不通过或未审核
                    	  DBObject check = (BasicDBObject) nurse.get("check");
                          vo.setHospital(MongodbUtil.getString(nurse, "hospital"));
                          vo.setDepartments(MongodbUtil.getString(nurse, "departments"));
                          vo.setTitle(MongodbUtil.getString(nurse, "title"));
                          vo.setNurseNum(MongodbUtil.getString(nurse, "nurseNum"));
                          if (check != null) {
                        	  vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                        	  if (status!=null && (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex())) {
                             	 vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                          	  }
                          }
                      }
            	  }
            }
            list.add(vo);
        }
        
        //查询集团信息
        if (groupIds != null && groupIds.size() > 0) {
			BasicDBList gdbGroupIds = new BasicDBList();
			for (String groupId : groupIds) {
				if (StringUtils.isNoneEmpty(groupId)) {
					gdbGroupIds.add(new ObjectId(groupId));
				}
			}
			
			BasicDBObject in = new BasicDBObject();
			in.put(QueryOperators.IN, gdbGroupIds);
			DBObject groupQuery = new BasicDBObject();
			groupQuery.put("_id", in);
			DBCursor groupCursor = dsForRW.getDB().getCollection("c_group").find(groupQuery);
			while(groupCursor.hasNext()){
				DBObject obj = groupCursor.next();
				String name = obj.get("name")==null ? "" : obj.get("name").toString();

				String id = obj.get("_id").toString();
				
				if (list != null && list.size() > 0) {
					for (DoctorCheckVO doctorCheckVO : list) {
						String tempGroupId = doctorCheckVO.getSourceGroupId();
						if (StringUtils.equals(id, tempGroupId)) {
							switch (doctorCheckVO.getSourceType().intValue()) {
							case 2:
							case 3:
								doctorCheckVO.setSource(name + "邀请");
								break;
							case 4:
							case 6:
								doctorCheckVO.setSource(name + "新建");
								break;
							default:
								break;
							}
						}
					}
				}
			}
		}

        //查询邀请人的信息
        if (inviterIds != null && inviterIds.size() > 0) {
			BasicDBList userIds = new BasicDBList();
            Set<Integer> inviterIdSet = new HashSet<>();
			for (Integer id : inviterIds) {
				userIds.add(id);
                inviterIdSet.add(id);
			}

            //获取邀请人ID，查询inviter对应的token
            Map<Integer, AccessToken> tokenMap = null;
            if (!CollectionUtils.isEmpty(inviterIdSet)) {
                List<AccessToken> tokenList = auth2Helper.getOpenIdList(new ArrayList(inviterIdSet));
                tokenMap = tokenList.stream().collect(Collectors.toMap(AccessToken::getUserId, Function.identity()));
            }

			BasicDBObject in = new BasicDBObject();
			in.put("$in", userIds);
			DBObject inviterQuery = new BasicDBObject();
			inviterQuery.put("_id", in);
			DBCursor inviterCursor = dsForRW.getDB().getCollection("user").find(inviterQuery);
			while (inviterCursor.hasNext()) {
				DBObject obj = inviterCursor.next();
				String name = obj.get("name")==null ? "" : obj.get("name").toString();
				String id = obj.get("_id").toString();
				if (list != null && list.size() > 0) {
					for (DoctorCheckVO doctorCheckVO : list) {
						Integer tempInviterId = doctorCheckVO.getInviterId();
						if (tempInviterId != null && StringUtils.equals(id, String.valueOf(tempInviterId))) {
							doctorCheckVO.setInviterName(name);
						}

                        if (Objects.nonNull(tokenMap)) {
                            AccessToken token = tokenMap.get(tempInviterId);
                            if (Objects.nonNull(token)) {
                                doctorCheckVO.setOpenId(token.getOpenId());
                            }
                        }
					}
				}
			}
		}
        if (!CollectionUtils.isEmpty(list)) {
            List<Integer> drugorgUserIds = new ArrayList<Integer>();
            for (DoctorCheckVO vo : list) {
                if (vo == null || vo.getSubsystem() == null){
                    continue;
                }
                if (Source.drugOrg.getIndex() == vo.getSubsystem()) {
                    drugorgUserIds.add(vo.getInviterId());
                }
            }
            List<CSimpleUser> simpleUsers = null;
            try {
                simpleUsers = drugOrgApiClientProxy.getSimpleUser(drugorgUserIds);
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
            }
            if (!CollectionUtils.isEmpty(simpleUsers)) {
                Map<Integer, CSimpleUser> userMap = Maps.uniqueIndex(simpleUsers, (x) -> x.getId());
                for (DoctorCheckVO vo : list) {
                    if (vo == null || vo.getSubsystem() == null){
                        continue;
                    }
                    if (Source.drugOrg.getIndex() == vo.getSubsystem()) {
                        CSimpleUser inviter = userMap.get(vo.getInviterId());
                        if (inviter != null) {
                            vo.setInviterName(inviter.getName());
                        }
                    } else if (Source.doctorCircle.getIndex() == vo.getSubsystem()) {
                        User inviter = userRepository.getUser(vo.getInviterId());
                        if (inviter != null) {
                            vo.setInviterName(inviter.getName());
                        }
                    }
                }
            }
        }

        PageVO page = new PageVO();
        page.setPageData(list);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(collection.count(query));

        return page;
    }
    
    /**
     * </p>获取所有医生
     *  update by wangl 
     *  扩大筛选类型将护士也选择出来
     * </p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    public PageVO getDoctorsByName(DoctorNameParam param) {
        DBObject query = new BasicDBObject();
        Integer status = param.getStatus();
        if (StringUtil.isNotBlank(param.getName())) {
            //模糊查询，姓名、手机、医院
            
            Pattern pattern = Pattern.compile("^.*" + StringUtil.trim(param.getName()) + ".*$", Pattern.CASE_INSENSITIVE);
        
            BasicDBList or = new BasicDBList();
            
            or.add(new BasicDBObject("name", pattern));
            or.add(new BasicDBObject("telephone", pattern));
            
            query.put("$or",or);
        }
        
        //修改了排序（2016-05-20傅永德   待审核：按提交时间排序；已通过/未通过：按审核时间排序；未认证：按创建账号时间排序）
        DBObject sortField = new BasicDBObject();
        
        if (status != null && status != 0) {
            query.put("status", status);// 审核状态
            // 不显示已挂起或禁用状态的用户
            query.put("suspend", UserConstant.SuspendStatus.normal.getIndex());
        }
        
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBCollection collection = dsForRW.getDB().getCollection("user");
        DBCursor cursor = collection.find(query).sort(sortField).skip(param.getStart()).limit(param.getPageSize());

        List<DoctorCheckVO> list = new ArrayList<DoctorCheckVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            DoctorCheckVO vo = new DoctorCheckVO();
            Integer userId = (Integer) obj.get("_id");

            vo.setUserId(userId);
            vo.setUserLevel((Integer) obj.get("userLevel"));
            vo.setLimitedPeriodTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "limitedPeriodTime"), null));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setTelephone(obj.get("telephone")==null ? "" : obj.get("telephone").toString());
            Long createTime = MongodbUtil.getLong(obj, "createTime");
            vo.setCreateTime(DateUtil.formatDate2Str(createTime, null));
            if (status!=null && status==UserStatus.Unautherized.getIndex()) {
                vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "createTime"), null));
            }
            if(null!= obj.get("modifyTime")){

                String modifyTimeStr = obj.get("modifyTime")==null ? "" : obj.get("modifyTime").toString();
                Long modifyTime = Long.valueOf(modifyTimeStr);
                
                //未审核列表认证时间
                if(status!=null && status==UserStatus.uncheck.getIndex()) {
                    vo.setOrderTime(DateUtil.formatDate2Str(modifyTime, null));
                    Long submitTime = MongodbUtil.getLong(obj, "submitTime");
                    if (null != submitTime) {
                        vo.setCreateTime(DateUtil.formatDate2Str(submitTime, null));
                    }
                }
            }
            vo.setStatus((Integer) obj.get("status"));

            vo.setSuspend((Integer) obj.get("suspend"));
            
            int userType = MongodbUtil.getInteger(obj, "userType");
            
            vo.setUserType(userType);
            
            DBObject source = (BasicDBObject) obj.get("source");
            if (source!= null) {
                vo.setSubsystem(MongodbUtil.getInteger(source, "sourceType"));//来源子系统
                Integer inviterId = MongodbUtil.getInteger(source, "inviterId");
                if (inviterId != null) {
                    vo.setInviterId(inviterId);
                } else {
                    vo.setInviterName("");
                }
                String groupId = MongodbUtil.getString(source, "groupId");
                vo.setSourceGroupId(groupId);
                Integer sourceType = MongodbUtil.getInteger(source, "sourceType");
                vo.setSourceType(sourceType);
                if (sourceType == null) {
                    vo.setSource("");
                } else {
                    //case里面的值参考 UserEnum.Source.app.getIndex();
                    UserEnum.Source sourceEnum = UserEnum.Source.getEnum(sourceType.intValue());
                    if (sourceEnum != null) {
                        vo.setSource(sourceEnum.getSource());
                    }
                }
            } else {
                vo.setSource("");
                vo.setInviterName("");
            }
            
            if(UserEnum.UserType.doctor.getIndex() == userType){
                 vo.setHeadPicFileName(UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"), vo.getUserId(), MongodbUtil.getInteger(obj, "sex"), userType));
                 DBObject doctor = (BasicDBObject) obj.get("doctor");
                 if (doctor != null) {
                     // 审核通过取审核信息，审核不通过取职业信息
                     if (vo.getStatus() != null && vo.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
                         // 审核通过
                         DBObject check = (BasicDBObject) doctor.get("check");
                         if (check != null) {
                             vo.setHospitalId(MongodbUtil.getString(check, "hospitalId"));
                             vo.setHospital(MongodbUtil.getString(check, "hospital"));
                             vo.setDepartments(MongodbUtil.getString(check, "departments"));
                             vo.setTitle(MongodbUtil.getString(check, "title"));
                             vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                             vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                             vo.setSkill(MongodbUtil.getString(doctor, "skill"));
                             if (status!=null && (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex())) {
                                 vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                             }
                         }
                     } else {
                         // 审核不通过或未审核
                         DBObject check = (BasicDBObject) doctor.get("check");
                         vo.setHospitalId(MongodbUtil.getString(doctor, "hospitalId"));
                         vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                         vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                         vo.setTitle(MongodbUtil.getString(doctor, "title"));
                         vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                         if (check != null) {
                             vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                             if (status!=null && (status==UserStatus.normal.getIndex() || status==UserStatus.fail.getIndex())) {
                                 vo.setOrderTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                            }
                         }
                     }
                 }
            }
            list.add(vo);
        }
        
        PageVO page = new PageVO();
        page.setPageData(list);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(collection.count(query));

        return page;
    }

    /**
     * 批量查询用户学币
     * @param userIdSet
     * @return
     */
    private Map getUsersIntegral(Set userIdSet){
        String userIdStr = userIdSet.toString().replace("[","").replace("]","");
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("userIds", userIdStr);
        String url = "http://CREDIT/inner_api/user/findByUserIds";
        String responseStr = ribbonManager.post(url, paramMap);
        RemoteServiceResult response = JSON.parseObject(responseStr, RemoteServiceResult.class);
        if (response == null) {
            throw new ServiceException("远程接口调用失败！！！");
        }
        if (response.getResultCode() != 1) {
            logger.error("sendPost, 远程接口调用失败, url: {}, params: {}, response： {} ", url, JSON.toJSONString(paramMap), JSON.toJSONString(response));
            throw new ServiceException(response.getDetailMsg());
        }
        Map balanceMap = (JSONObject) response.getData();
        return balanceMap;
    }

    /**
     * 得到Query<?>
     * 
     * @param param
     * @return
     */
	public Query<User> findDoctorByAuthStatus(FindDoctorByAuthStatusParam param) {

		boolean isAuthStatus = false;
		if (param.isAuthStatus == 1) {
			isAuthStatus = true;
		} else {
			isAuthStatus = false;
		}

		DBObject query = new BasicDBObject();
		query.put("userType", new BasicDBObject("$eq", 3)); // == 3
		query.put("doctor.check", new BasicDBObject("$exists", isAuthStatus)); // 存在doctor

		if (StringUtil.isNotBlank(param.getKeyword())) {
			BasicDBList keyword = new BasicDBList();
			Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
			keyword.add(new BasicDBObject("name", pattern));
			keyword.add(new BasicDBObject("telephone", pattern));
			query.put(QueryOperators.OR, keyword);
		}
		Query<User> q = dsForRW.createQuery(User.class, query);
		return q;
	}

    /**
     * </p>获取医生详细信息</p>
     * 
     * @param id
     * @return
     * @author fanp
     * @date 2015年7月6日
     */
    @SuppressWarnings("unchecked")
    public DoctorCheckVO getDoctor(Integer id) {
        DBObject query = new BasicDBObject();
        query.put("_id", id);

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query);

        List<Integer> inviterIds = Lists.newArrayList();
        DoctorCheckVO vo = null;
        if (Objects.nonNull(obj)) {
            vo = new DoctorCheckVO();
            vo.setUserLevel((Integer) obj.get("userLevel"));
            vo.setLimitedPeriodTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "limitedPeriodTime"), null));
            int userId = (Integer) obj.get("_id");
            vo.setUserId(userId);
            vo.setSex(MongodbUtil.getInteger(obj, "sex"));
            vo.setBirthday(MongodbUtil.getLong(obj, "birthday"));
            vo.setIDNum(MongodbUtil.getString(obj, "IDNum"));
            vo.setWorkTime(MongodbUtil.getLong(obj, "workTime"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setTelephone(obj.get("telephone")==null ? "" : obj.get("telephone").toString());
            vo.setCreateTime(DateUtil.formatDate2Str((Long) obj.get("createTime"), null));
            vo.setStatus((Integer) obj.get("status"));
            if (Objects.nonNull(obj.get("submitTime"))) {
                vo.setCertTime(DateUtil.formatDate2Str((Long) obj.get("submitTime"), null));
            }
			vo.setModifyTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "modifyTime"), null));
            int userType = MongodbUtil.getInteger(obj, "userType");
            vo.setUserType(userType);
            vo.setSuspend((Integer) obj.get("suspend"));

            if(UserEnum.UserType.doctor.getIndex() == userType){

				//查找医生集团
				List<ObjectId> groupIds = new ArrayList<>();
				DBCollection groupDoctorCollection = dsForRW.getDB().getCollection("c_group_doctor");
				DBObject groupDoctorQuery = new BasicDBObject();
				groupDoctorQuery.put("doctorId", id);
				DBObject projection = new BasicDBObject("groupId", 1);
				DBCursor groupDoctorCursor = groupDoctorCollection.find(groupDoctorQuery, projection);
				while (groupDoctorCursor.hasNext()) {
					DBObject doctorObj = groupDoctorCursor.next();
					groupIds.add(new ObjectId(MongodbUtil.getString(doctorObj, "groupId")));
				}

				List<String> groupNames = new ArrayList<String>();
                if (!CollectionUtils.isEmpty(groupIds)) {
                    DBCollection groupCollection = dsForRW.getDB().getCollection("c_group");
                    DBObject groupQuery = new BasicDBObject();
                    groupQuery.put("_id", new BasicDBObject("$in", groupIds));
                    DBObject groupProjection = new BasicDBObject("name", 1);
                    DBCursor groupCursor = groupCollection.find(groupQuery, groupProjection);

                    while (groupCursor.hasNext()) {
                        DBObject groupObj = groupCursor.next();
                        groupNames.add(MongodbUtil.getString(groupObj, "name"));
                    }
                }
                vo.setGroupNames(groupNames);

            	vo.setHeadPicFileName(UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"), vo.getUserId(), MongodbUtil.getInteger(obj, "sex"), userType));
            	DBObject doctor = (BasicDBObject) obj.get("doctor");
                if (doctor != null) {
                    // 返回医生人身验证状态和验证头像
                    vo.setFaceRec(faceRecognitionService.passFaceRec(id));
                    FaceRecognition faceRecognition = faceRecognitionService.getFaceRecRecord(id);
                    vo.setFaceRecImage(faceRecognition == null ? "" : faceRecognition.getFaceImage());
                    // 审核通过去审核信息，审核不通过取职业信息
                    if (vo.getStatus() != null && vo.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
                        // 审核通过
                        DBObject check = (BasicDBObject) doctor.get("check");
                        if (check != null) {
                        	vo.setHospital(MongodbUtil.getString(check, "hospital"));
                        	vo.setHospitalId(MongodbUtil.getString(check, "hospitalId"));
                        	vo.setDepartments(MongodbUtil.getString(check, "departments"));
                        	vo.setDeptId(MongodbUtil.getString(check, "deptId"));
                            vo.setDeptPhone(MongodbUtil.getString(check, "deptPhone"));
                        	vo.setTitle(MongodbUtil.getString(check, "title"));
                        	vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));

                        	vo.setLicenseNum(MongodbUtil.getString(check, "licenseNum"));
                        	vo.setLicenseExpire(MongodbUtil.getString(check, "licenseExpire"));
                        	vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                        	vo.setChecker(MongodbUtil.getString(check, "checker"));
                        	vo.setRemark(MongodbUtil.getString(check, "remark"));
                        	String qrurl = qrCodeService.generateUserQr(String.valueOf(id), String.valueOf(vo.getUserType()));
                        	if (StringUtil.isNotEmpty(qrurl)) {
                        		vo.setQRUrl(qrurl);
							}
                        }
                    } else {
                        // 审核不通过或未审核
                        vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                        vo.setHospitalId(MongodbUtil.getString(doctor, "hospitalId"));
                        vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                        vo.setDeptId(MongodbUtil.getString(doctor, "deptId"));
                        vo.setDeptPhone(MongodbUtil.getString(doctor, "deptPhone"));
                        vo.setTitle(MongodbUtil.getString(doctor, "title"));
                        vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));

                        DBObject check = (BasicDBObject) doctor.get("check");
                        if (check != null) {
                            vo.setChecker(MongodbUtil.getString(check, "checker"));
                            vo.setRemark(MongodbUtil.getString(check, "remark"));
                        	vo.setCheckTime(DateUtil.formatDate2Str(MongodbUtil.getLong(check, "checkTime"), null));
                        }
                    }
                    // 禁用状态下取禁用原因和禁用时间
                    if (Objects.nonNull(vo.getSuspend()) && Objects.equals(vo.getSuspend(), UserConstant.SuspendStatus.tempForbid.getIndex())) {
                        DBObject suspendInfo = (BasicDBObject) obj.get("suspendInfo");
                        if (Objects.nonNull(suspendInfo)) {
                            vo.setDisableReason(MongodbUtil.getString(suspendInfo, "reason"));
                            vo.setDisableTime(MongodbUtil.getLong(suspendInfo, "createTime"));
                        }
                    }
                    vo.setSkill(MongodbUtil.getString(doctor, "skill"));
                    vo.setScholarship(MongodbUtil.getString(doctor, "scholarship"));
                    vo.setExperience(MongodbUtil.getString(doctor, "experience"));
                    vo.setIntroduction(MongodbUtil.getString(doctor, "introduction"));
                    ArrayList<String> expertises = (ArrayList<String>) doctor.get("expertise");
                    if (expertises != null && expertises.size() > 0) {
                    	removeBlankElement(expertises);
                    	DiseaseType[] vos = new DiseaseType[expertises.size()];
                    	List<DiseaseType> diseaseTypes = diseaseTypeRepository.findByIds(expertises); 
                    	if(diseaseTypes.size()>0)
                    	{
	                    	for (int i = 0; i < diseaseTypes.size(); i++) {
								vos[i] = diseaseTypes.get(i);
							}
                    	}
                    	vo.setExpertises(vos);
					}
                    Integer role = MongodbUtil.getInteger(doctor, "role");
                    vo.setRole(role == null ? 0 : role);

                    //查询医生助手
                    if(doctor.get("assistantId") != null){
                    	int assistantId = 0;
                    	try{
                    		assistantId = Integer.parseInt(doctor.get("assistantId").toString());
                    	}catch(Exception e){
                    		throw new ServiceException("医生助手id不正确");
                    	}

                    	if(assistantId != 0){
                    		Query<User> userQuery = dsForRW.createQuery(User.class);
                    		userQuery.filter("_id", assistantId);
                    		User assistant = userQuery.get();
                    		if(assistant != null){
                    			vo.setAssistantName(assistant.getName());
                    			vo.setAssistantId(assistant.getUserId());
                    		}
                    	}
                    }
                } else {
                	vo.setRole(0);
				}
            }else if(UserEnum.UserType.nurse.getIndex() == userType){
            	 /**
	          	   * 获取护士的图片数组
	          	   */
	          	  List<NurseImage> nurseImageList =  nurseService.getNurseImageList(userId);
	          	  vo.setNurseImageList(nurseImageList);
	          	  DBObject nurse = (BasicDBObject) obj.get("nurse");
	          	  if(nurse != null){
					// 审核通过取审核信息，审核不通过取职业信息
					if (vo.getStatus() != null && vo.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
						// 审核通过
						DBObject check = (BasicDBObject) nurse.get("check");
						if (check != null) {
							vo.setHospital(MongodbUtil.getString(check, "hospital"));
							vo.setHospitalId(MongodbUtil.getString(check, "hospitalId"));
							vo.setDepartments(MongodbUtil.getString(check, "departments"));
							vo.setTitle(MongodbUtil.getString(check, "title"));
							vo.setNurseNum(MongodbUtil.getString(nurse, "nurseNum"));
							vo.setChecker(MongodbUtil.getString(check, "checker"));
						}
					} else {
						// 审核不通过或未审核
						vo.setHospital(MongodbUtil.getString(nurse, "hospital"));
						vo.setHospitalId(MongodbUtil.getString(nurse, "hospitalId"));
						vo.setDepartments(MongodbUtil.getString(nurse, "departments"));
						vo.setTitle(MongodbUtil.getString(nurse, "title"));
						vo.setNurseNum(MongodbUtil.getString(nurse, "nurseNum"));

						DBObject check = (BasicDBObject) nurse.get("check");
						if (check != null) {
							vo.setChecker(MongodbUtil.getString(check, "checker"));
							vo.setRemark(MongodbUtil.getString(check, "remark"));
						}
					}
					Integer role = MongodbUtil.getInteger(nurse, "role");
					vo.setRole(role == null ? 0 : role);
	          	} else {
	          		vo.setRole(0);   
				}
            }
        }
        
        //设置邀请人信息
        DBObject source = (BasicDBObject) obj.get("source");
        if (source!= null) {
            vo.setSubsystem(MongodbUtil.getInteger(source, "sourceType"));//来源子系统
			Integer inviterId = MongodbUtil.getInteger(source, "inviterId");
			if (inviterId != null) {
				vo.setInviterId(inviterId);
				inviterIds.add(inviterId);
			} else {
				vo.setInviterName("");
			}
			
			//查询邀请的集团
			String inviteGroupName = null;
			String groupId = MongodbUtil.getString(source, "groupId");
			if(StringUtils.isNotEmpty(groupId)) {
				vo.setSourceGroupId(groupId);
				DBObject groupQuery = new BasicDBObject();
				groupQuery.put("_id", new ObjectId(groupId));
		        DBObject groupObj = dsForRW.getDB().getCollection("c_group").findOne(groupQuery);
		        if (groupObj != null) {
					inviteGroupName = groupObj.get("name").toString();
				}
			}
	        
			Integer sourceType = MongodbUtil.getInteger(source, "sourceType");
			if (sourceType == null) {
				vo.setSource("");
			} else {
				//case里面的值参考 UserEnum.Source.app.getIndex();
				if (StringUtils.isNotEmpty(inviteGroupName)) {
					switch (sourceType.intValue()) {
						case 2:
						case 3:
							vo.setSource(inviteGroupName + "邀请");
							break;
						case 4:
						case 6:
							vo.setSource(inviteGroupName + "新建");
							break;
						default:
							break;
					}
				} else {
                    UserEnum.Source sourceEnum = UserEnum.Source.getEnum(sourceType.intValue());
                    if (sourceEnum != null) {
                        vo.setSource(sourceEnum.getSource());
                    }
                }
			}
		} else {
			vo.setSource("");
			vo.setInviterName("");
		}
        
        //查询邀请人的信息(2016-05-23傅永德)
        if (inviterIds != null && inviterIds.size() > 0) {
			BasicDBList userIds = new BasicDBList();
			for (Integer tempId : inviterIds) {
				userIds.add(tempId);
			}
			
			BasicDBObject in = new BasicDBObject();
			in.put("$in", userIds);
			DBObject inviterQuery = new BasicDBObject();
			inviterQuery.put("_id", in);
			DBCursor inviterCursor = dsForRW.getDB().getCollection("user").find(inviterQuery);
			while (inviterCursor.hasNext()) {
				DBObject inviter = inviterCursor.next();
				String name = inviter.get("name").toString();
				String inviterId = inviter.get("_id").toString();

				Integer tempInviterId = vo.getInviterId();
				if (StringUtils.equals(inviterId, String.valueOf(tempInviterId))) {
					vo.setInviterName(name);
				}
			}
		}

        //根据子系统设置邀请人姓名
        if (vo != null && vo.getSubsystem() != null) {
            if (Source.drugOrg.getIndex() == vo.getSubsystem()) {
                List<Integer> _userIds = new ArrayList<>();
                _userIds.add(vo.getInviterId());
                try {
                    List<CSimpleUser> simpleUsers = drugOrgApiClientProxy.getSimpleUser(_userIds);
                    if (!CollectionUtils.isEmpty(simpleUsers)) {
                        CSimpleUser simpleUser = simpleUsers.get(0);
                        vo.setInviterName(simpleUser.getName());
                    }
                } catch (HttpApiException e) {
                    logger.error(e.getMessage(), e);
                }
            } else if (Source.doctorCircle.getIndex() == vo.getSubsystem()) {
                User inviter = userRepository.getUser(vo.getInviterId());
                if (inviter != null) {
                    vo.setInviterName(inviter.getName());
                }
            }
        }

        //2017/08/23 longjh
        try{
            Map<String, String[]> circleNames = new HashMap<String, String[]>();
            List<String> cricleName = new ArrayList<String>();
            List<String> deptName = new ArrayList<String>();
            List<CircleVO> circleList = circleService.getUserAllCircle(vo.getUserId().toString());
            if(!Objects.isNull(circleList)){
                for(CircleVO c : circleList){
                    if(CircleEnum.CircleType.CIRCLE.getIndex().equals(c.getType())
                            && StringUtils.isNotBlank(c.getName())){
                        cricleName.add(c.getName());
                    }else if(CircleEnum.CircleType.DEPT.getIndex().equals(c.getType())
                            && StringUtils.isNotBlank(c.getName())){
                        deptName.add(c.getName());
                    }
                }
                circleNames.put("dept", deptName.toArray(new String[0]));
                circleNames.put("circle", cricleName.toArray(new String[0]));
                vo.setCircleNames(circleNames);
            }
            
        }catch(Exception ex){
            
        }
        return vo;
    }

    /**
     * </p>获取医生状态</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年7月10日
     */
    public Integer getStatus(Integer doctorId) {
        DBObject query = new BasicDBObject();
        query.put("_id", doctorId);
        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query);
        if (obj != null) {
            return MongodbUtil.getInteger(obj, "status");
        }
        return null;
    }

    /**
     * </p>审核医生</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    public void checked(DoctorCheckParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());
        query.put("status", new BasicDBObject("$in", new Integer[] { UserEnum.UserStatus.uncheck.getIndex(), UserEnum.UserStatus.fail.getIndex(),UserEnum.UserStatus.Unautherized.getIndex() }));

        DBObject update = new BasicDBObject();
        update.put("status", UserEnum.UserStatus.normal.getIndex());
        //审核通过了就需要修改用户的级别和修改使用时间
        Map<String,String> opeartorLog = new HashMap<>();
        opeartorLog.put("operationType",OperationLogTypeDesc.DOCOTORLEVELCHANGE);
        if(Objects.equals(UserEnum.UserType.doctor.getIndex(), param.getUserType())){
        	User ccu = userRepository.getUser(param.getUserId());
        	update.put("userLevel", UserEnum.UserLevel.AuthenticatedUser.getIndex());
            update.put("limitedPeriodTime", UserEnum.FOREVER_LIMITED_PERIOD);
            opeartorLog.put("content",String.format("(%1$s)审核通过身份由(%2$s)变为(%3$s),有效期由(%4$s)变为(%5$s)",
            		Objects.nonNull(ccu)?ccu.getTelephone():"",UserEnum.UserLevel.getName(ccu.getUserLevel()),UserEnum.UserLevel.getName(UserEnum.UserLevel.AuthenticatedUser.getIndex()),DateUtil.formatDate2Str(ccu.getLimitedPeriodTime(), null),DateUtil.formatDate2Str(UserEnum.FOREVER_LIMITED_PERIOD, null)));
        }

        if (StringUtil.isNotBlank(param.getHeadPicFileName())) {
            if (!param.getHeadPicFileName().contains("default")) {
                update.put("headPicFileName", param.getHeadPicFileName());
            }
        }

        if (StringUtil.isNotBlank(param.getName())) {
            update.put("name", param.getName());
        }
        if (StringUtil.isNotBlank(param.getIDNum())) {
            update.put("IDNum", param.getIDNum());
        }
        if (Objects.nonNull(param.getWorkTime())) {
            update.put("workTime", param.getWorkTime());
        }
        if (Objects.nonNull(param.getBirthday())) {
            update.put("birthday", param.getBirthday());
        }
        if (Objects.nonNull(param.getSex()) && (param.getSex()== 1 || param.getSex()== 2 || param.getSex()== 3)) {
            update.put("sex", param.getSex());
        }
        update.put("doctor.hospital", param.getHospital());
        update.put("doctor.hospitalId", param.getHospitalId());
        if (param.getHospitalId() != null) {
			HospitalPO hospital = dsForRW.createQuery("b_hospital", HospitalPO.class).field("_id").equal(param.getHospitalId()).get();
			if (hospital != null) {
				update.put("doctor.provinceId", hospital.getProvince());
				Area area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
				if (area != null) {
					update.put("doctor.province", area.getName());
				}
				update.put("doctor.cityId", hospital.getCity());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
				if (area != null) {
					update.put("doctor.city", area.getName());
				}
				update.put("doctor.countryId", hospital.getCountry());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
				if (area != null) {
					update.put("doctor.country", area.getName());
				}
			} 
        }
        update.put("doctor.departments", param.getDepartments());
        update.put("doctor.deptId", param.getDeptId());
        update.put("doctor.deptPhone", param.getDeptPhone());
        update.put("doctor.title", param.getTitle());
        update.put("doctor.role", param.getRole());
        //add by zl@date 2016-07-26 医生助手id
        update.put("doctor.assistantId", param.getAssistantId());

        //查找职称排行
        if(StringUtil.isNotBlank(param.getTitle())){
            TitleVO titleVO = baseDataDao.getTitle(param.getTitle());
            update.put("doctor.titleRank", titleVO.getRank());
        }
        update.put("doctor.check.hospital", param.getHospital());
        update.put("doctor.check.hospitalId", param.getHospitalId());
        update.put("doctor.check.departments", param.getDepartments());
        update.put("doctor.check.deptPhone", param.getDeptPhone());
        update.put("doctor.check.deptId", param.getDeptId());
        update.put("doctor.check.title", param.getTitle());
        update.put("doctor.check.licenseNum", param.getLicenseNum());
        update.put("doctor.check.licenseExpire", param.getLicenseExpire());
        update.put("doctor.check.checkTime", param.getCheckTime());
        update.put("doctor.check.checker", param.getChecker());
        update.put("doctor.check.checkerId", param.getCheckerId());
        update.put("doctor.check.remark", param.getRemark());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
        
        if(!CollectionUtils.isEmpty(opeartorLog)&&Objects.equals(opeartorLog.size(),2)){
        	operationLogMqProducer.sendMsgToMq(ReqUtil.instance.getUserId(),opeartorLog.get("operationType"),opeartorLog.get("content"));
        }   
    }

    /**
     * </p>审核医生为通过</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月6日
     */
    public void fail(DoctorCheckParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());

        DBObject update = new BasicDBObject();
        update.put("status", UserEnum.UserStatus.fail.getIndex());
        update.put("doctor.check.checkTime", param.getCheckTime());
        update.put("doctor.check.checker", param.getChecker());
        update.put("doctor.check.checkerId", param.getCheckerId());
        update.put("doctor.check.remark", param.getRemark());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }

    /**
     * </p>设置执业区域</p>
     * 
     * @param hospitalId
     * @param userId
     * @author fanp
     * @date 2015年9月16日
     */
    public void setWorkArea(String userId, String hospitalId) {
        // 查找医院信息
        HospitalVO hvo = baseDataDao.getHospital(hospitalId);
        if (hvo == null) {
            return;
        }

        
        // 查找地区
        Integer provinceId = hvo.getProvince();
        Integer cityId = hvo.getCity();
        Integer countryId = hvo.getCountry();

        List<AreaVO> list = dsForRW.createQuery("b_area", AreaVO.class).filter("code in", new Integer[] { provinceId, cityId, countryId }).asList();

        String province = "";
        String city = "";
        String country = "";
        if (list != null && list.size() > 0) {
            for (AreaVO area : list) {
                if (area.getCode().equals(province)) {
                    province = area.getName();
                } else if (area.getCode().equals(city)) {
                    city = area.getName();
                } else if (area.getCode().equals(country)) {
                    country = area.getName();
                }
            }
        }
        
        
        DBObject query = new BasicDBObject();
        query.put("_id", userId);

        DBObject update = new BasicDBObject();
        update.put("doctor.province", province);
        update.put("doctor.city", city);
        update.put("doctor.country", country);
        update.put("doctor.provinceId", provinceId);
        update.put("doctor.cityId", cityId);
        update.put("doctor.countryId", countryId);

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }
    
    /**
     * 护士审核
     */
	@Override
	public void checkedNurse(DoctorCheckParam param) {
		DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());
        query.put("status", new BasicDBObject("$in", new Integer[] { UserEnum.UserStatus.uncheck.getIndex(), UserEnum.UserStatus.fail.getIndex() }));

        DBObject update = new BasicDBObject();
        update.put("status", UserEnum.UserStatus.normal.getIndex());
        update.put("nurse.hospital", param.getHospital());
        update.put("nurse.hospitalId", param.getHospitalId());
        if (param.getHospitalId() != null) {
			HospitalPO hospital = dsForRW.createQuery("b_hospital", HospitalPO.class).field("_id")
					.equal(param.getHospitalId()).get();
			if (hospital != null) {
				update.put("nurse.provinceId", hospital.getProvince());
				Area area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
				if (area != null) {
					update.put("nurse.province", area.getName());
				}
				update.put("nurse.cityId", hospital.getCity());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
				if (area != null) {
					update.put("nurse.city", area.getName());
				}
				update.put("nurse.countryId", hospital.getCountry());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
				if (area != null) {
					update.put("nurse.country", area.getName());
				}
			} 
        }
        update.put("nurse.departments", param.getDepartments());
        update.put("nurse.title", param.getTitle());
        //审核护士添加角色（2016-05-26傅永德）
        update.put("nurse.role", param.getRole());

        //查找职称排行
        if(StringUtil.isNotBlank(param.getTitle())){
            TitleVO titleVO = baseDataDao.getTitle(param.getTitle());
            update.put("nurse.titleRank", titleVO.getRank());
        }
        
        update.put("nurse.check.hospital", param.getHospital());
        update.put("nurse.check.hospitalId", param.getHospitalId());
        update.put("nurse.check.departments", param.getDepartments());
        update.put("nurse.check.title", param.getTitle());
        update.put("nurse.check.checkTime", param.getCheckTime());
        update.put("nurse.check.checker", param.getChecker());
        update.put("nurse.check.checkerId", param.getCheckerId());
        update.put("nurse.check.remark", param.getRemark());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
	}

	@Override
	public void failNurse(DoctorCheckParam param) {
		DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());

        DBObject update = new BasicDBObject();
        update.put("status", UserEnum.UserStatus.fail.getIndex());
        update.put("nurse.check.checkTime", param.getCheckTime());
        update.put("nurse.check.checker", param.getChecker());
        update.put("nurse.check.checkerId", param.getCheckerId());
        update.put("nurse.check.remark", param.getRemark());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
	}
	
	/**
     * </p>编辑医生</p>
     * 
     * @param param
     * @author 谭永芳
     * @date 2016年5月31日
     */
    public void edit(DoctorCheckParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());
        query.put("status", new BasicDBObject("$in", 
                new Integer[] {UserEnum.UserStatus.normal.getIndex(), 
                        UserEnum.UserStatus.fail.getIndex(),
                        UserEnum.UserStatus.Unautherized.getIndex()}));
        
        DBObject update = new BasicDBObject();
//        update.put("status", UserEnum.UserStatus.normal.getIndex());
        if(StringUtil.isNotBlank(param.getName())){
        	update.put("name", param.getName());// 姓名
        }
        if(StringUtil.isNotBlank(param.getHeadPicFileName())){
        	update.put("headPicFileName", param.getHeadPicFileName());// 头像
        }
        if (null != param.getSex() && (param.getSex()== 1 || param.getSex()== 2 || param.getSex()== 3)) {
        	update.put("sex", param.getSex());
		}
        
        if(Objects.nonNull(param.getUserLevel())){
        	update.put("userLevel", param.getUserLevel());// 用户级别
        }
        if(Objects.nonNull(param.getLimitedPeriodTime())){
        	update.put("limitedPeriodTime", param.getLimitedPeriodTime());//用户有效期
        }

        if (StringUtil.isNotBlank(param.getIDNum())) {
            update.put("IDNum", param.getIDNum());
        }
        if (Objects.nonNull(param.getWorkTime())) {
            update.put("workTime", param.getWorkTime());
        }
        if (Objects.nonNull(param.getBirthday())) {
            update.put("birthday", param.getBirthday());
        }

        if (StringUtil.isNotBlank(param.getIntroduction())) {
        	update.put("doctor.introduction", param.getIntroduction());
		}else {
			update.put("doctor.introduction", null);
		}
		
        if (StringUtil.isNotEmpty(param.getSkill()) && param.getSkill().length() > 4000) {
        	throw new ServiceException("补充擅长最多4000个字符");
		}
        if (StringUtil.isNotEmpty(param.getSkill())) {
        	update.put("doctor.skill", param.getSkill());
		}else {
			update.put("doctor.skill", null);
		}
        if (StringUtil.isNotEmpty(param.getScholarship())) {
            update.put("doctor.scholarship", param.getScholarship());
        }else {
            update.put("doctor.scholarship", null);
        }
        if (StringUtil.isNotEmpty(param.getExperience())) {
            update.put("doctor.experience", param.getExperience());
        }else {
            update.put("doctor.experience", null);
        }

        if (param.getExpertises() != null && param.getExpertises().length > 0) {
            List<String> expertises = Lists.newArrayList();
            for (int i = 0; i < param.getExpertises().length; i++) {
                if (StringUtil.isNotEmpty(param.getExpertises()[i])) {
                    expertises.add(param.getExpertises()[i]);
                }
            }
            update.put("doctor.expertise", expertises);
        } else {
            update.put("doctor.expertise", null);
        }
        
        //头像：点击原头像，可以选择新头像
        update.put("doctor.hospital", param.getHospital());//所属医院
        update.put("doctor.hospitalId", param.getHospitalId());//所属医院Id
        if (param.getHospitalId() != null) {
			HospitalPO hospital = dsForRW.createQuery("b_hospital", HospitalPO.class).field("_id").equal(param.getHospitalId()).get();
			if (hospital != null) {
				update.put("doctor.provinceId", hospital.getProvince());
				Area area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
				if (area != null) {
					update.put("doctor.province", area.getName());
				}
				update.put("doctor.cityId", hospital.getCity());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
				if (area != null) {
					update.put("doctor.city", area.getName());
				}
				update.put("doctor.countryId", hospital.getCountry());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
				if (area != null) {
					update.put("doctor.country", area.getName());
				}
			}
        }
        if (null != param.getDepartments()) {			
        	update.put("doctor.departments", param.getDepartments());
		}
        if (null != param.getDeptId()) {			
        	update.put("doctor.deptId", param.getDeptId());
		}
        if (null != param.getDeptPhone()) {
            update.put("doctor.deptPhone", param.getDeptPhone());
        }
        if (null != param.getTitle()) {			
        	update.put("doctor.title", param.getTitle());
		}
        if (null != param.getRole()) {			
        	update.put("doctor.role", param.getRole());
		}
        if (null != param.getAssistantId()) {			
        	update.put("doctor.assistantId", param.getAssistantId());
		}

        //查找职称排行
        if(StringUtil.isNotBlank(param.getTitle())){
            TitleVO titleVO = baseDataDao.getTitle(param.getTitle());
            update.put("doctor.titleRank", titleVO.getRank());
        }
        if (null != param.getHospital()) {			
        	update.put("doctor.check.hospital", param.getHospital());
		}
        if (null != param.getHospitalId()) {			
        	update.put("doctor.check.hospitalId", param.getHospitalId());
		}
        if (null != param.getDepartments()) {
        	update.put("doctor.check.departments", param.getDepartments());			
		}
        if (null != param.getDeptId()) {			
        	update.put("doctor.check.deptId", param.getDeptId());
		}
        if (null != param.getDeptPhone()) {
            update.put("doctor.check.deptPhone", param.getDeptPhone());
        }
        if (null != param.getTitle()) {			
        	update.put("doctor.check.title", param.getTitle());
		}
        if (null != param.getLicenseNum()) {			
        	update.put("doctor.check.licenseNum", param.getLicenseNum());//证书编号
		}
        if (null != param.getLicenseExpire()) {			
        	update.put("doctor.check.licenseExpire", param.getLicenseExpire());//证书到期时间
		}
        if (null != param.getCheckTime()) {			
        	update.put("doctor.check.checkTime", param.getCheckTime());
		}
        if (null != param.getChecker()) {			
        	update.put("doctor.check.checker", param.getChecker());
		}
        if (null != param.getCheckerId()) {			
        	update.put("doctor.check.checkerId", param.getCheckerId());
		}
        if (null != param.getRemark()) {			
        	update.put("doctor.check.remark", param.getRemark());
		}

		update.put("modifyTime", System.currentTimeMillis());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }
    
    
    
	
    /**
     * 护士审核
     */
	@Override
	public void editNurse(DoctorCheckParam param) {
		DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());
        query.put("status", new BasicDBObject("$in", new Integer[] { UserEnum.UserStatus.normal.getIndex()
																	,UserEnum.UserStatus.fail.getIndex() 
																	,UserEnum.UserStatus.Unautherized.getIndex() 
        											}));
        DBObject update = new BasicDBObject();
//        update.put("status", UserEnum.UserStatus.normal.getIndex());
        if(StringUtil.isNotBlank(param.getName())){
        	update.put("name", param.getName());// 姓名
        }
        if(StringUtil.isNotBlank(param.getHeadPicFileName())){
        	update.put("headPicFileName", param.getHeadPicFileName());// 头像
        }
        update.put("nurse.hospital", param.getHospital());
        update.put("nurse.hospitalId", param.getHospitalId());
        if (param.getHospitalId() != null) {
			HospitalPO hospital = dsForRW.createQuery("b_hospital", HospitalPO.class).field("_id")
					.equal(param.getHospitalId()).get();
			if (hospital != null) {
				update.put("nurse.provinceId", hospital.getProvince());
				Area area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getProvince()).get();
				if (area != null) {
					update.put("nurse.province", area.getName());
				}
				update.put("nurse.cityId", hospital.getCity());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCity()).get();
				if (area != null) {
					update.put("nurse.city", area.getName());
				}
				update.put("nurse.countryId", hospital.getCountry());
				area = dsForRW.createQuery("b_area", Area.class).field("code").equal(hospital.getCountry()).get();
				if (area != null) {
					update.put("nurse.country", area.getName());
				}
			} 
        }
        update.put("nurse.departments", param.getDepartments());
        update.put("nurse.title", param.getTitle());
        //审核护士添加角色（2016-05-26傅永德）
        update.put("nurse.role", param.getRole());

        //查找职称排行
        if(StringUtil.isNotBlank(param.getTitle())){
            TitleVO titleVO = baseDataDao.getTitle(param.getTitle());
            update.put("nurse.titleRank", titleVO.getRank());
        }
        
        update.put("nurse.check.hospital", param.getHospital());
        update.put("nurse.check.hospitalId", param.getHospitalId());
        update.put("nurse.check.departments", param.getDepartments());
        update.put("nurse.check.title", param.getTitle());
        update.put("nurse.check.checkTime", param.getCheckTime());
        update.put("nurse.check.checker", param.getChecker());
        update.put("nurse.check.checkerId", param.getCheckerId());
        update.put("nurse.check.remark", param.getRemark());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
	}

	@Override
	public List<String> getMyGroupIds(Integer userId) {
		List<String> myGroupIds = Lists.newArrayList();
		
		DBObject query = new BasicDBObject();
		query.put("doctorId", userId);
		DBCursor cursorGroupDoctors = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		
		while(cursorGroupDoctors.hasNext()) {
			DBObject groupDoctor = cursorGroupDoctors.next();
			String id = MongodbUtil.getString(groupDoctor, "groupId");
			myGroupIds.add(id);
		}
		
		return myGroupIds;
	}
	
	/**
	 * 对list里的空元素进行过滤
	 * @param list
	 */
	public void removeBlankElement(List<String> list) {
		
		if (CollectionUtils.isEmpty(list)) {
			return ;
		}
		
		Iterator<String> iterator = list.iterator();
		while (iterator.hasNext()) {
			String element = iterator.next();
			if (StringUtil.isEmpty(element)) {
				iterator.remove();
			}
		}
	}
}
