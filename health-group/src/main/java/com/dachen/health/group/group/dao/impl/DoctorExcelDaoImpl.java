package com.dachen.health.group.group.dao.impl;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.AreaVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.commons.entity.UserDiseaseLaber;
import com.dachen.health.group.group.auth2.Auth2ApiProxy2;
import com.dachen.health.group.group.auth2.AuthSimpleUser2;
import com.dachen.health.group.group.dao.DoctroExcelDao;
import com.dachen.health.group.group.entity.vo.*;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.*;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class DoctorExcelDaoImpl extends NoSqlRepository implements DoctroExcelDao {
    private static final Logger logger = LoggerFactory.getLogger(DoctorExcelDaoImpl.class);

    @Autowired
    protected IBaseDataDao baseDataDao;

    @Autowired
    private Auth2ApiProxy2 auth2ApiProxy;

	@Override
	public List<DoctorExcelVo> getDoctorExcel(Integer status) {
		DBObject query = new BasicDBObject();
		query.put("userType", 3);
		query.put("status", status);
		DBCursor cursor = dsForRW.getDB().getCollection("user").find(query);
		
		List<DoctorExcelVo> doctorExcelVos = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			DoctorExcelVo doctorExcelVo = new DoctorExcelVo();
			doctorExcelVo.setId(obj.get("_id").toString());
			String registerTimeStr = obj.get("createTime") == null ? "" : obj.get("createTime").toString();
			String phone = obj.get("telephone").toString() == null ? "" : obj.get("telephone").toString();
			doctorExcelVo.setPhone(phone);
			DBObject doctor = (BasicDBObject) obj.get("doctor");
			
			String authTimeStr = null;
			String hospital = null;
			String department = null;
			String title = null;
			
			if (doctor != null) {
				DBObject check =  (BasicDBObject) doctor.get("check");
				if (check != null) {
					authTimeStr = check.get("checkTime")== null ? "" : check.get("checkTime").toString();
				}
				
				hospital = doctor.get("hospital") == null ? "" : doctor.get("hospital").toString();
				department = doctor.get("departments") == null ? "" :doctor.get("departments").toString();
				title = doctor.get("title") == null ? "" : doctor.get("title").toString();
			}
			
			
			if (StringUtils.isNotEmpty(registerTimeStr)) {
				Long registerTime = Long.valueOf(registerTimeStr);
				Date registerTimeDate = new Date(registerTime);
				doctorExcelVo.setRegisterTime(registerTime);
				doctorExcelVo.setRegisterTimeStr(simpleDateFormat.format(registerTimeDate));
			}
			if (StringUtils.isNotEmpty(authTimeStr)) {
				Long authTime = Long.valueOf(authTimeStr);
				Date authTimeDate = new Date(authTime);
				doctorExcelVo.setAuthTime(authTime);
				doctorExcelVo.setAuthTimeStr(simpleDateFormat.format(authTimeDate));
			}
			
			
			if (StringUtils.isNotEmpty(hospital)) {
				doctorExcelVo.setIsFull("是");
			} else {
				doctorExcelVo.setIsFull("否");	
			}
			doctorExcelVo.setHospital(hospital);
			doctorExcelVo.setDepartment(department);
			doctorExcelVo.setTitle(title);
			String name = MongodbUtil.getString(obj, "name");
			doctorExcelVo.setName(name);
			doctorExcelVos.add(doctorExcelVo);
		}
		
		return doctorExcelVos;
	}
	
	@Override
	public List<DoctorExcelVo> getDoctorExcel(Integer status, Long start, Long end) {
		
		BasicDBObject query = new BasicDBObject();
		query.put("userType", 3);
		query.put("status", status);
		DBObject object = new BasicDBObject(QueryOperators.GTE, start);
		object.put(QueryOperators.LTE, end);
		
		DBObject orderBy = new BasicDBObject();
		if (status == UserStatus.normal.getIndex() || status == UserStatus.fail.getIndex()) {
			//当为3时需要使用审核的字段
			query.put("doctor.check.checkTime", object);
			orderBy.put("doctor.check.checkTime", -1);
		} else {
			query.put("createTime", object);			
			orderBy.put("createTime", -1);
		}
		DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).sort(orderBy);

		List<DoctorExcelVo> doctorExcelVos = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<Integer> inviterIds = Lists.newArrayList();
		Set<String> hospitalIdSet = new HashSet<>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			DoctorExcelVo doctorExcelVo = new DoctorExcelVo();
			doctorExcelVo.setId(obj.get("_id").toString());
			String registerTimeStr = obj.get("createTime") == null ? "" : obj.get("createTime").toString();
			String phone = obj.get("telephone").toString() == null ? "" : obj.get("telephone").toString();
			doctorExcelVo.setPhone(phone);
			DBObject doctor = (BasicDBObject) obj.get("doctor");
			DBObject source = (BasicDBObject) obj.get("source");

			Long lastLoginTime = MongodbUtil.getLong(obj, "lastLoginTime");
			if (lastLoginTime != null) {
			    doctorExcelVo.setLastLoginTime(lastLoginTime);
			    doctorExcelVo.setLastLoginTimeStr(simpleDateFormat.format(lastLoginTime));
            }
			
			String authTimeStr = null;
			String hospital = null;
			String hospitalId = null;
			String department = null;
			String title = null;
			String checkRemark = null;
			
			String province = null;
			String city = null;
			String country = null;
			
			if (doctor != null) {
				DBObject check =  (BasicDBObject) doctor.get("check");
				if (check != null) {
					authTimeStr = check.get("checkTime") == null ? "" : check.get("checkTime").toString();
					checkRemark = check.get("remark") == null ? "" : check.get("remark").toString();
				}
				
				hospital = doctor.get("hospital") == null ? "" : doctor.get("hospital").toString();
				hospitalId = MongodbUtil.getString(doctor, "hospitalId");
				department = doctor.get("departments") == null ? "" :doctor.get("departments").toString();
				title = doctor.get("title") == null ? "" : doctor.get("title").toString();
				
				province = MongodbUtil.getString(doctor, "province");
				city = MongodbUtil.getString(doctor, "city");
				country = MongodbUtil.getString(doctor, "country");
			}
			
			Integer inviterId = null;
			Integer sourceType = null;
			if (source != null) {
				inviterId = MongodbUtil.getInteger(source, "inviterId");
				doctorExcelVo.setInviterId(Objects.isNull(inviterId)?"":inviterId+"");
				sourceType = MongodbUtil.getInteger(source, "sourceType");
			}
			
			if (sourceType != null) {
				switch (sourceType) {
					case 1:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.app.getSource());
						break;
					case 2:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.group.getSource());
						break;
					case 3:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.hospital.getSource());
						break;
					case 4:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.groupAdmin.getSource());
						break;
					case 5:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.checkAdmin.getSource());
						break;
					case 6:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.hospitalAdmin.getSource());
						break;
					case 7:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.bdjlApp.getSource());
						break;
					case 8:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorInvite.getSource());
						break;
					case 9:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.wechatRegister.getSource());
						break;
					case 10:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.guideInvite.getSource());
						break;
					case 11:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.farmAdminLot.getSource());
						break;
					case 12:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.checkAdminLot.getSource());
						break;
                    case 13:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.share.getSource());
                        break;
                    case 14:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.drugStore.getSource());
                        break;
                    case 15:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.ThirdParty.getSource());
                        break;
                    case 16:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.drugOrg.getSource());
                        break;
                    case 17:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorCircle.getSource());
                        break;
					case 18:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorCircleInviteJoin.getSource());
						break;
					case 19:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorCircleInviteJoin.getSource());
						break;
					default:
						break;
				}
			}
			
			if (inviterId != null) {
				inviterIds.add(inviterId);
			}

			if(Objects.nonNull(hospitalId)){
				hospitalIdSet.add(hospitalId);
			}

			doctorExcelVo.setInviterIdInt(inviterId);
			doctorExcelVo.setSourceType(sourceType);
			
			if (StringUtils.isNotEmpty(registerTimeStr)) {
				Long registerTime = Long.valueOf(registerTimeStr);
				Date registerTimeDate = new Date(registerTime);
				doctorExcelVo.setRegisterTime(registerTime);
				doctorExcelVo.setRegisterTimeStr(simpleDateFormat.format(registerTimeDate));
			}
			if (StringUtils.isNotEmpty(authTimeStr)) {
				Long authTime = Long.valueOf(authTimeStr);
				Date authTimeDate = new Date(authTime);
				doctorExcelVo.setAuthTime(authTime);
				doctorExcelVo.setAuthTimeStr(simpleDateFormat.format(authTimeDate));
			}
			
			if (StringUtils.isNotEmpty(hospital)) {
				doctorExcelVo.setIsFull("是");
			} else {
				doctorExcelVo.setIsFull("否");	
			}
			doctorExcelVo.setHospital(hospital);
			doctorExcelVo.setHospitalId(hospitalId);
			doctorExcelVo.setProvince(province);
			doctorExcelVo.setCity(city);
			doctorExcelVo.setCountry(country);
			doctorExcelVo.setDepartment(department);
			doctorExcelVo.setTitle(title);
			String name = MongodbUtil.getString(obj, "name");
			doctorExcelVo.setName(name);
			doctorExcelVo.setCheckRemark(checkRemark);
            doctorExcelVo.setUserLevel(MongodbUtil.getInteger(obj, "userLevel"));
            doctorExcelVo.setLimitedPeriodTime(MongodbUtil.getLong(obj, "limitedPeriodTime"));
            doctorExcelVos.add(doctorExcelVo);
		}
		
		//查询邀请人信息
		Map<Integer, AuthSimpleUser2> userMap = null;
        if (!CollectionUtils.isEmpty(inviterIds)) {
            Set<Integer> inviterIdSet = new HashSet<>(inviterIds);
            List<AuthSimpleUser2> userList = auth2ApiProxy.getSimpleUserList(new ArrayList<>(inviterIdSet));
            userMap = userList.stream().collect(Collectors.toMap(AuthSimpleUser2::getId, Function.identity()));
        }

		//在待审核、未认证、未通过三种状态下，省市区信息取医生注册时填写医院所在的省市区信息
		Map<String,HospitalVO> hospitalMap = null;
		if(status != UserEnum.UserStatus.normal.getIndex()){
        	//获取所有医院信息
			if(!CollectionUtils.isEmpty(hospitalIdSet)) {
				List<String> hospitalIds = new ArrayList<>(hospitalIdSet);
				List<HospitalVO> hospitals = dsForRW.createQuery("b_hospital", HospitalVO.class).field("_id").in(hospitalIds).asList();
				Set<Integer> areaCodeSet = new HashSet<>();
				hospitals.forEach(hospital -> {
					areaCodeSet.add(hospital.getProvince());
					areaCodeSet.add(hospital.getCity());
					areaCodeSet.add(hospital.getCountry());

				});

				//获取所有地区信息
				if(!CollectionUtils.isEmpty(areaCodeSet)) {
					List<Integer> areaCodes = new ArrayList<>(areaCodeSet);
					List<AreaVO> areas = dsForRW.createQuery("b_area", AreaVO.class).field("code").in(areaCodes).asList();
					Map<Integer, String> areaMap = areas.stream().collect(Collectors.toMap(AreaVO::getCode, AreaVO::getName));

					//向医院信息中填入对应的省市区的名字
					hospitals.forEach(hospital -> {
						hospital.setProvinceName(areaMap.get(hospital.getProvince()));
						hospital.setCityName(areaMap.get(hospital.getCity()));
						hospital.setCountryName(areaMap.get(hospital.getCountry()));
					});

					hospitalMap = hospitals.stream().collect(Collectors.toMap(HospitalVO::getId, Function.identity()));
				}
			}
		}

		for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
			//填入邀请人信息
			if (Objects.nonNull(userMap)) {
				AuthSimpleUser2 user = userMap.get(doctorExcelVo.getInviterIdInt());
				if (Objects.nonNull(user)) {
					doctorExcelVo.setInviterName(user.getName());
				}
			}

			//填入非审核状态下填入医院对应的省市区信息
			if(Objects.nonNull(hospitalMap)){
				String hospitalId = doctorExcelVo.getHospitalId();
				HospitalVO hospital = hospitalMap.get(hospitalId);
				if(Objects.nonNull(hospital)) {
					doctorExcelVo.setProvince(hospital.getProvinceName());
					doctorExcelVo.setCity(hospital.getCityName());
					doctorExcelVo.setCountry(hospital.getCountryName());
				}
			}
		}

		return doctorExcelVos;
	}

	@Override
	public List<GroupDoctorReVo> getGroupDoctorReVo(List<Integer> doctorIds) {
		DBObject query = new BasicDBObject();
		query.put("status", "C");
		BasicDBList ids = new BasicDBList();
		for (Integer doctorId : doctorIds) {
			ids.add(doctorId);			
		}
		BasicDBObject in = new BasicDBObject();
		in.put(QueryOperators.IN, doctorIds);
		query.put("doctorId", in);
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query);
		List<GroupDoctorReVo> groupDoctorReVos = new ArrayList<>();
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			GroupDoctorReVo groupDoctorReVo = new GroupDoctorReVo();
			groupDoctorReVo.setDoctorId(obj.get("doctorId").toString());
			groupDoctorReVo.setGroupId(obj.get("groupId").toString());
			groupDoctorReVo.setInviterId(obj.get("creator").toString());
			groupDoctorReVos.add(groupDoctorReVo);
		}
		return groupDoctorReVos;
	}

	@Override
	public List<GroupDoctorReVo> setGroupName(List<GroupDoctorReVo> groupDoctorReVos) {
		if (groupDoctorReVos != null && groupDoctorReVos.size() > 0) {
			BasicDBList groupIds = new BasicDBList();
			for (GroupDoctorReVo groupDoctorReVo : groupDoctorReVos) {
				ObjectId objectId = new ObjectId(groupDoctorReVo.getGroupId());
				groupIds.add(objectId);
			}
			
			BasicDBObject in = new BasicDBObject();
			in.put(QueryOperators.IN, groupIds);
			DBObject query = new BasicDBObject();
			query.put("_id", in);
			DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query);
			
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String groupName = Objects.isNull(obj.get("name"))?"":obj.get("name").toString();
				String groupId = obj.get("_id").toString();
				for (GroupDoctorReVo groupDoctorReVo : groupDoctorReVos) {
					String tempGroupId = groupDoctorReVo.getGroupId();
					if (StringUtils.equals(groupId, tempGroupId)) {
						groupDoctorReVo.setGroupName(groupName);
					}
				}
			}
			
			return groupDoctorReVos;
		} else {
			return null;
		}
	}

	@Override
	public List<GroupDoctorReVo> setInviterName(List<GroupDoctorReVo> groupDoctorReVos) {
		if (groupDoctorReVos != null && groupDoctorReVos.size() > 0) {
			BasicDBList userIds = new BasicDBList();
			for (GroupDoctorReVo groupDoctorReVo : groupDoctorReVos) {
				Integer userId = Integer.valueOf(groupDoctorReVo.getInviterId());
				userIds.add(userId);
			}
			
			BasicDBObject in = new BasicDBObject();
			in.put(QueryOperators.IN, userIds);
			DBObject query = new BasicDBObject();
			query.put("_id", in);
			DBCursor cursor = dsForRW.getDB().getCollection("user").find(query);
			
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String groupName = MongodbUtil.getString(obj, "name");
				String userId = obj.get("_id").toString();
				for (GroupDoctorReVo groupDoctorReVo : groupDoctorReVos) {
					String tempInviterId = groupDoctorReVo.getInviterId();
					if (StringUtils.equals(userId, tempInviterId)) {
						groupDoctorReVo.setInviterName(groupName);
					}
				}
			}
			
			return groupDoctorReVos;
		}
		return null;
	}

	@Override
	public List<PatientExcelVo> getPatientExcelVo(Integer status, Long start, Long end) {
		DBObject query = new BasicDBObject();
		query.put("userType", 1);
		query.put("status", status);
		
		DBObject object = new BasicDBObject(QueryOperators.GTE, start);
		object.put(QueryOperators.LTE, end);
		
		if (status == 1) {
			//当为1时需要使用审核的字段
			query.put("createTime", object);			
		} else {
			
		}
		DBObject orderBy = new BasicDBObject();
		orderBy.put("createTime", -1);
		DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).sort(orderBy);
		
		List<PatientExcelVo> patientExcelVos = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd");

		List<Integer> userIds = Lists.newArrayList();

		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			PatientExcelVo patientExcelVo = new PatientExcelVo();
			patientExcelVo.setId(obj.get("_id").toString());

			userIds.add(MongodbUtil.getInteger(obj, "_id"));

			String registerTimeStr = obj.get("createTime") == null ? "" : obj.get("createTime").toString();
			if (StringUtils.isNotEmpty(registerTimeStr)) {
				Long registerTime = Long.valueOf(registerTimeStr);
				Date registerTimeDate = new Date(registerTime);
				patientExcelVo.setRegisterTime(registerTime);
				patientExcelVo.setRegisterTimeStr(simpleDateFormat.format(registerTimeDate));
			}

            Long lastLoginTime = MongodbUtil.getLong(obj, "lastLoginTime");
            if (lastLoginTime != null) {
                patientExcelVo.setLastLoginTime(lastLoginTime);
                patientExcelVo.setLastLoginTimeStr(simpleDateFormat.format(lastLoginTime));
            }

			String phone = obj.get("telephone").toString() == null ? "" : obj.get("telephone").toString();
			patientExcelVo.setPhone(phone);
			String name = MongodbUtil.getString(obj, "name");
			patientExcelVo.setName(name);
			String sex = obj.get("sex") == null ? "" : obj.get("sex").toString();
			if (StringUtils.isEmpty(sex)) {
				patientExcelVo.setSex("未填写");
			} else if(StringUtils.equals("1", sex)) {
				patientExcelVo.setSex("男");
			} else if(StringUtils.equals("2", sex)) {
				patientExcelVo.setSex("女");
			} else if (StringUtils.equals("3", sex)) {
				patientExcelVo.setSex("保密");
			} else {
				patientExcelVo.setSex("未知");
			}
			
			String birthdayStr = obj.get("birthday") == null ? "" : obj.get("birthday").toString();
			if (StringUtils.isNotEmpty(birthdayStr)) {
				Long birthday = Long.valueOf(birthdayStr);
				Date birthdayDate = new Date(birthday);
				
				patientExcelVo.setBirthday(simpleDateFormat2.format(birthdayDate));
				
				String age = null;
			
				if(birthday!=null) {
		    		int ages=DateUtil.calcAge(birthday);
		    		if (ages == 0 || ages == -1) {
		    			age = DateUtil.calcMonth(birthday)<=0?"1个月":DateUtil.calcMonth(birthday)+"个月";
		    		}
		    		age = ages + "岁";
		    	}
				
				patientExcelVo.setAgeStr(age);
			}
			String area = obj.get("area") == null ? "" : obj.get("area").toString();
			patientExcelVo.setAddress(area);
			patientExcelVos.add(patientExcelVo);
		}

		//处理labers
        if (!CollectionUtils.isEmpty(userIds)) {
            List<UserDiseaseLaber> userDiseaseLabers = dsForRW.createQuery(UserDiseaseLaber.class).field("userId").in(userIds).asList();
            //查询出全部的疾病
            List<DiseaseTypeVO> diseaseTypes = baseDataDao.getAllDiseaseType();
            Map<String, String> diseaseTypeMap = Maps.newHashMap();
            if (diseaseTypes != null && diseaseTypes.size() > 0) {
                for (DiseaseTypeVO diseaseTypeVo : diseaseTypes) {
                    diseaseTypeMap.put(diseaseTypeVo.getId(), diseaseTypeVo.getName());
                }
            }
            if (!CollectionUtils.isEmpty(userDiseaseLabers) && !CollectionUtils.isEmpty(patientExcelVos)) {
                userDiseaseLabers.forEach(userDiseaseLaber -> {
                    patientExcelVos.forEach(patientExcelVo -> {
                        if (StringUtils.equals(patientExcelVo.getId(), String.valueOf(userDiseaseLaber.getUserId()))
                                && StringUtils.isNotBlank(diseaseTypeMap.get(userDiseaseLaber.getDiseaseId()))) {
                            if (CollectionUtils.isEmpty(patientExcelVo.getLabers())) {
                                List<String> labers = Lists.newArrayList();
                                labers.add(diseaseTypeMap.get(userDiseaseLaber.getDiseaseId()));
                                patientExcelVo.setLabers(labers);
                            } else {
                                patientExcelVo.getLabers().add(diseaseTypeMap.get(userDiseaseLaber.getDiseaseId()));
                            }
                        }
                    });
                });
            }
        }
		
		return patientExcelVos;
	}

	@Override
	public List<UserExcelVo> getByIds(List<Integer> ids) {
		if (ids != null && ids.size() > 0) {
			BasicDBList userIds = new BasicDBList();
			for (Integer id : ids) {
				userIds.add(id);
			}
			
			BasicDBObject in = new BasicDBObject();
			in.put(QueryOperators.IN, userIds);
			DBObject query = new BasicDBObject();
			query.put("_id", in);
			DBCursor cursor = dsForRW.getDB().getCollection("user").find(query);
			List<UserExcelVo> userExcelVos = new ArrayList<>();
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String name = MongodbUtil.getString(obj, "name");
				String id = obj.get("_id").toString();
				String phone = obj.get("telephone").toString();
				UserExcelVo userExcelVo = new UserExcelVo();
				userExcelVo.setId(id);
				userExcelVo.setName(name);
				userExcelVo.setPhone(phone);
				userExcelVos.add(userExcelVo);
			}
			
			return userExcelVos;
		}
		return null;
	}

	@Override
	public List<GroupExcelVo> getGroupByIds(List<String> ids) {
		if (ids != null && ids.size() > 0) {
			BasicDBList groupIds = new BasicDBList();
			for (String groupId : ids) {
				if (StringUtils.isNotEmpty(groupId)) {
					ObjectId objectId = new ObjectId(groupId);
					groupIds.add(objectId);
				}
			}
			
			BasicDBObject in = new BasicDBObject();
			in.put(QueryOperators.IN, groupIds);
			DBObject query = new BasicDBObject();
			query.put("_id", in);
			DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query);
			
			List<GroupExcelVo> groupDoctorReVos = new ArrayList<>();
			
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String groupName = obj.get("name").toString();
				String groupId = obj.get("_id").toString();
				GroupExcelVo groupExcelVo = new GroupExcelVo();
				groupExcelVo.setGroupId(groupId);
				groupExcelVo.setGroupName(groupName);
				groupDoctorReVos.add(groupExcelVo);
			}
			
			return groupDoctorReVos;
		}
		return null;
	}

	@Override
	public List<DoctorExcelVo> getDoctorExcelByIds(List<Integer> ids) {

		BasicDBObject query = new BasicDBObject();
		query.put("userType", 3);
		
		BasicDBList userIds = new BasicDBList();
		if (ids != null && ids.size() > 0) {
			for (Integer id : ids) {
				userIds.add(id);
			}
		}
		
		BasicDBObject in = new BasicDBObject();
		in.put(QueryOperators.IN, userIds);
		query.put("_id", in);
		DBObject orderBy = new BasicDBObject();
		orderBy.put("createTime", -1);
		DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).sort(orderBy);
		
		List<DoctorExcelVo> doctorExcelVos = new ArrayList<>();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		List<Integer> inviterIds = Lists.newArrayList();
		
		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			DoctorExcelVo doctorExcelVo = new DoctorExcelVo();
			doctorExcelVo.setId(obj.get("_id").toString());
			String registerTimeStr = obj.get("createTime") == null ? "" : obj.get("createTime").toString();
			String phone = obj.get("telephone").toString() == null ? "" : obj.get("telephone").toString();
			doctorExcelVo.setPhone(phone);
			DBObject doctor = (BasicDBObject) obj.get("doctor");
			
			String authTimeStr = null;
			String hospital = null;
			String department = null;
			String title = null;
			String checkRemark = null;
			
			if (doctor != null) {
				DBObject check =  (BasicDBObject) doctor.get("check");
				if (check != null) {
					authTimeStr = check.get("checkTime") == null ? "" : check.get("checkTime").toString();
					checkRemark = check.get("remark") == null ? "" : check.get("remark").toString();
				}
				
				hospital = doctor.get("hospital") == null ? "" : doctor.get("hospital").toString();
				department = doctor.get("departments") == null ? "" :doctor.get("departments").toString();
				title = doctor.get("title") == null ? "" : doctor.get("title").toString();
			}
			
			
			if (StringUtils.isNotEmpty(registerTimeStr)) {
				Long registerTime = Long.valueOf(registerTimeStr);
				Date registerTimeDate = new Date(registerTime);
				doctorExcelVo.setRegisterTime(registerTime);
				doctorExcelVo.setRegisterTimeStr(simpleDateFormat.format(registerTimeDate));
			}
			if (StringUtils.isNotEmpty(authTimeStr)) {
				Long authTime = Long.valueOf(authTimeStr);
				Date authTimeDate = new Date(authTime);
				doctorExcelVo.setAuthTime(authTime);
				doctorExcelVo.setAuthTimeStr(simpleDateFormat.format(authTimeDate));
			}
			
			
			
			if (StringUtils.isNotEmpty(hospital)) {
				doctorExcelVo.setIsFull("是");
			} else {
				doctorExcelVo.setIsFull("否");	
			}
			doctorExcelVo.setHospital(hospital);
			doctorExcelVo.setDepartment(department);
			doctorExcelVo.setTitle(title);
			String name = MongodbUtil.getString(obj, "name");
			doctorExcelVo.setName(name);
			doctorExcelVo.setCheckRemark(checkRemark);
			
			DBObject source = (BasicDBObject) obj.get("source");
			
			Integer sourceType = null;
			Integer inviterId = null;
			if (source != null) {
				inviterId = MongodbUtil.getInteger(source, "inviterId");
				sourceType = MongodbUtil.getInteger(source, "sourceType");
			}
			
			if (sourceType != null) {
				switch (sourceType) {
					case 1:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.app.getSource());
						break;
					case 2:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.group.getSource());
						break;
					case 3:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.hospital.getSource());
						break;
					case 4:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.groupAdmin.getSource());
						break;
					case 5:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.checkAdmin.getSource());
						break;
					case 6:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.hospitalAdmin.getSource());
						break;
					case 7:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.bdjlApp.getSource());
						break;
					case 8:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorInvite.getSource());
						break;
					case 9:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.wechatRegister.getSource());
						break;
					case 10:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.guideInvite.getSource());
						break;
					case 11:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.farmAdminLot.getSource());
						break;
					case 12:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.checkAdminLot.getSource());
						break;
                    case 13:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.share.getSource());
                        break;
                    case 14:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.drugStore.getSource());
                        break;
                    case 15:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.ThirdParty.getSource());
                        break;
                    case 16:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.drugOrg.getSource());
                        break;
                    case 17:
                        doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorCircle.getSource());
                        break;
					case 18:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorCircleInviteJoin.getSource());
						break;
					case 19:
						doctorExcelVo.setSourceTypeName(UserEnum.Source.doctorCircleInviteJoin.getSource());
						break;
					default:
						break;
				}
			}
			
			doctorExcelVo.setInviterIdInt(inviterId);
			doctorExcelVo.setSourceType(sourceType);
			
			if (inviterId != null) {
				inviterIds.add(inviterId);
			}
			
			doctorExcelVos.add(doctorExcelVo);
		}
		
		//查询邀请人信息

        if (!CollectionUtils.isEmpty(inviterIds)) {
            List<AuthSimpleUser2> userList = auth2ApiProxy.getSimpleUserList(new ArrayList<>(inviterIds));
            Map<Integer, AuthSimpleUser2> userMap = userList.stream().collect(Collectors.toMap(
                AuthSimpleUser2::getId, Function.identity()));

            for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
                if (Objects.nonNull(userMap)) {
                    AuthSimpleUser2 user = userMap.get(doctorExcelVo.getInviterIdInt());
                    if (Objects.nonNull(user)) {
                        doctorExcelVo.setInviterName(user.getName());
                    }
                }
            }
        }
		/*BasicDBObject inviterInQuery = new BasicDBObject();
		inviterInQuery.put(QueryOperators.IN, inviterIds);
		DBObject queryInviter = new BasicDBObject();
		queryInviter.put("_id", inviterInQuery);
		DBCursor cursorInviter = dsForRW.getDB().getCollection("user").find(queryInviter);
		
		while (cursorInviter.hasNext()) {
			DBObject obj = cursorInviter.next();
			String name = MongodbUtil.getString(obj, "name");
			String userId = obj.get("_id").toString();
			for (DoctorExcelVo doctorExcelVo : doctorExcelVos) {
				if (StringUtils.equals(userId, String.valueOf(doctorExcelVo.getInviterIdInt()))) {
					doctorExcelVo.setInviterName(name);
				}
			}
		}*/
		
		return doctorExcelVos;
	}

	@Override
	public List<OrderExcelVo> setCareItemIdAndAnswerTimes(List<OrderExcelVo> orderExcelVos) {
		if (orderExcelVos!= null && orderExcelVos.size() > 0) {
			BasicDBObject queryCarePlan = new BasicDBObject();
			BasicDBList queryCarePlanIds = new BasicDBList();
			orderExcelVos.forEach((orderExcelVo)->{
				if (orderExcelVo != null && StringUtils.isNotEmpty(orderExcelVo.getCareTemplateId())) {					
					queryCarePlanIds.add(orderExcelVo.getCareTemplateId());
				}
			});
			queryCarePlan.put(QueryOperators.IN, queryCarePlanIds);
			
			DBObject query = new BasicDBObject();
			query.put("carePlanId", queryCarePlan);
			DBCursor cursor = dsForRW.getDB().getCollection("p_care_item").find(query);
			
			DBObject queryItems = new BasicDBObject();
			BasicDBList queryItemsIds = new BasicDBList();
			
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String id = MongodbUtil.getString(obj, "_id");
				String carePlanId = MongodbUtil.getString(obj, "carePlanId");
				
				queryItemsIds.add(id);
				
				orderExcelVos.forEach((orderExcelVo)->{
					if (orderExcelVo != null && StringUtils.equals(orderExcelVo.getCareTemplateId(), carePlanId)) {					
						if (orderExcelVo.getCareItemIds() == null) {
							List<String> careItemIds = Lists.newArrayList();
							careItemIds.add(id);
							orderExcelVo.setCareItemIds(careItemIds);
						} else {
							orderExcelVo.getCareItemIds().add(id);
						}
					}
				});
			}
			
			queryItems.put(QueryOperators.IN, queryItemsIds);
			DBObject queryTiems = new BasicDBObject();
			queryTiems.put("careItemId", queryItems);
			
			//去p_life_answer\p_track_answer\p_survey_answer表查询
			DBCursor cursorLife = dsForRW.getDB().getCollection("p_life_answer").find(queryTiems);
			while (cursorLife.hasNext()) {
				DBObject obj = cursorLife.next();
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				if (StringUtils.isNotEmpty(careItemId) && createTime != null) {
					orderExcelVos.forEach((orderExcelVo)->{
						if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {		
							if (orderExcelVo.getAnswerTimes() == null) {
								List<Long> answerTimes = Lists.newArrayList();
								answerTimes.add(createTime);
								orderExcelVo.setAnswerTimes(answerTimes);
							} else {
								orderExcelVo.getAnswerTimes().add(createTime);
							}
						}
					});
				}
			}
			
			DBCursor cursorTrack = dsForRW.getDB().getCollection("p_track_answer").find(queryTiems);
			while (cursorTrack.hasNext()) {
				DBObject obj = cursorTrack.next();
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				if (StringUtils.isNotEmpty(careItemId) && createTime != null) {
					orderExcelVos.forEach((orderExcelVo)->{
						if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {		
							if (orderExcelVo.getAnswerTimes() == null) {
								List<Long> answerTimes = Lists.newArrayList();
								answerTimes.add(createTime);
								orderExcelVo.setAnswerTimes(answerTimes);
							} else {
								orderExcelVo.getAnswerTimes().add(createTime);
							}
						}
					});
				}
			}
			
			DBCursor cursorSurvey = dsForRW.getDB().getCollection("p_survey_answer").find(queryTiems);
			while (cursorSurvey.hasNext()) {
				DBObject obj = cursorSurvey.next();
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				if (StringUtils.isNotEmpty(careItemId) && createTime != null) {
					orderExcelVos.forEach((orderExcelVo)->{
						if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {		
							if (orderExcelVo.getAnswerTimes() == null) {
								List<Long> answerTimes = Lists.newArrayList();
								answerTimes.add(createTime);
								orderExcelVo.setAnswerTimes(answerTimes);
							} else {
								orderExcelVo.getAnswerTimes().add(createTime);
							}
						}
					});
				}
			}
		}
		return orderExcelVos;
	}
	
	
	public List<OrderExcelVo> setItemAnswerInfo(List<OrderExcelVo> orderExcelVos) {
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		if (orderExcelVos!= null && orderExcelVos.size() > 0) {
			BasicDBObject queryCarePlan = new BasicDBObject();
			BasicDBList queryCarePlanIds = new BasicDBList();
			orderExcelVos.forEach((orderExcelVo)->{
				if (orderExcelVo != null && StringUtils.isNotEmpty(orderExcelVo.getCareTemplateId())) {					
					queryCarePlanIds.add(orderExcelVo.getCareTemplateId());
				}
			});
			queryCarePlan.put(QueryOperators.IN, queryCarePlanIds);
			
			DBObject query = new BasicDBObject();
			query.put("carePlanId", queryCarePlan);
			BasicDBObject timeQuery = new BasicDBObject();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new java.util.Date());
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			timeQuery.put(QueryOperators.LTE, calendar.getTime().getTime());
			query.put("fullSendTime", timeQuery);
			//查询当天以前的需要发送的各种表
			DBCursor cursor = dsForRW.getDB().getCollection("p_care_item").find(query);
			
			DBObject queryItems = new BasicDBObject();
			BasicDBList queryItemsIds = new BasicDBList();
			
			while (cursor.hasNext()) {
				DBObject obj = cursor.next();
				String id = MongodbUtil.getString(obj, "_id");
				String carePlanId = MongodbUtil.getString(obj, "carePlanId");
				Long planTime = MongodbUtil.getLong(obj, "fullSendTime");
				String planTimeStr = null;
				if (planTime != null) {
					planTimeStr = simpleDateFormat.format(planTime);
				}
				
				Integer status = MongodbUtil.getInteger(obj, "status");
				Integer type = MongodbUtil.getInteger(obj, "type");
				queryItemsIds.add(id);
				
				ItemAnswerInfo info = new ItemAnswerInfo();
				info.setItemId(id);
				info.setType(type);
				info.setPlanTime(planTime);
				info.setPlanTimeStr(planTimeStr);
				info.setStatus(status);
				if (status != null && status.intValue() == 3) {
					info.setStatusDesc("是");
				} else {
					info.setStatusDesc("否");
				}
				
				orderExcelVos.forEach((orderExcelVo)->{
					if (orderExcelVo != null && StringUtils.equals(orderExcelVo.getCareTemplateId(), carePlanId) && type != null) {					
						if (type.intValue() == 10) {
							//病情跟踪
							if(orderExcelVo.getTrack() != null) {
								orderExcelVo.getTrack().add(info);
							} else {
								List<ItemAnswerInfo> track = Lists.newArrayList();
								track.add(info);
								orderExcelVo.setTrack(track);
							}
						} else if (type.intValue() == 30) {
							//生活量表
							if(orderExcelVo.getLife() != null) {
								orderExcelVo.getLife().add(info);
							} else {
								List<ItemAnswerInfo> life = Lists.newArrayList();
								life.add(info);
								orderExcelVo.setLife(life);
							}
						} else if (type.intValue() == 40) {
							//调查表
							if(orderExcelVo.getSurvey() != null) {
								orderExcelVo.getSurvey().add(info);
							} else {
								List<ItemAnswerInfo> survey = Lists.newArrayList();
								survey.add(info);
								orderExcelVo.setSurvey(survey);
							}
						}
					}
				});
			}
			
			queryItems.put(QueryOperators.IN, queryItemsIds);
			DBObject queryTiems = new BasicDBObject();
			queryTiems.put("careItemId", queryItems);
			DBObject sort = new BasicDBObject();
			sort.put("createTime", 1);
			
			//去p_life_answer\p_track_answer\p_survey_answer表查询
			DBCursor cursorLife = dsForRW.getDB().getCollection("p_life_answer").find(queryTiems).sort(sort);
			while (cursorLife.hasNext()) {
				DBObject obj = cursorLife.next();
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String answerTimeStr = null;
				if (createTime != null) {
					answerTimeStr = simpleDateFormat.format(createTime);
				}
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				if (StringUtils.isNotEmpty(careItemId) && createTime != null) {
					for(OrderExcelVo orderExcelVo : orderExcelVos) {
						if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {	
							List<ItemAnswerInfo> track = orderExcelVo.getTrack();
							if (track != null && track.size() > 0) {
								for(ItemAnswerInfo info : track) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
							List<ItemAnswerInfo> life = orderExcelVo.getLife();
							if (life != null && life.size() > 0) {
								for(ItemAnswerInfo info : orderExcelVo.getLife()) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
							List<ItemAnswerInfo> survey = orderExcelVo.getSurvey();
							if (survey != null && survey.size() > 0) {
								for(ItemAnswerInfo info : orderExcelVo.getSurvey()) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
						}
					};
				}
			}
			
			DBCursor cursorTrack = dsForRW.getDB().getCollection("p_track_answer").find(queryTiems).sort(sort);
			while (cursorTrack.hasNext()) {
				DBObject obj = cursorTrack.next();
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String answerTimeStr = null;
				if (createTime != null) {
					answerTimeStr = simpleDateFormat.format(createTime);
				}
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				if (StringUtils.isNotEmpty(careItemId) && createTime != null) {
					for(OrderExcelVo orderExcelVo : orderExcelVos) {
						if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {		
							List<ItemAnswerInfo> track = orderExcelVo.getTrack();
							if (track != null && track.size() > 0) {
								for(ItemAnswerInfo info : track) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
							List<ItemAnswerInfo> life = orderExcelVo.getLife();
							if (life != null && life.size() > 0) {
								for(ItemAnswerInfo info : orderExcelVo.getLife()) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
							List<ItemAnswerInfo> survey = orderExcelVo.getSurvey();
							if (survey != null && survey.size() > 0) {
								for(ItemAnswerInfo info : orderExcelVo.getSurvey()) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
						}
					};
				}
			}
			
			DBCursor cursorSurvey = dsForRW.getDB().getCollection("p_survey_answer").find(queryTiems).sort(sort);
			while (cursorSurvey.hasNext()) {
				DBObject obj = cursorSurvey.next();
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String answerTimeStr = null;
				if (createTime != null) {
					answerTimeStr = simpleDateFormat.format(createTime);
				}
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				if (StringUtils.isNotEmpty(careItemId) && createTime != null) {
					for(OrderExcelVo orderExcelVo : orderExcelVos) {
						if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {
							List<ItemAnswerInfo> track = orderExcelVo.getTrack();
							if (track != null && track.size() > 0) {
								for(ItemAnswerInfo info : track) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
							List<ItemAnswerInfo> life = orderExcelVo.getLife();
							if (life != null && life.size() > 0) {
								for(ItemAnswerInfo info : orderExcelVo.getLife()) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
							List<ItemAnswerInfo> survey = orderExcelVo.getSurvey();
							if (survey != null && survey.size() > 0) {
								for(ItemAnswerInfo info : orderExcelVo.getSurvey()) {
									if (StringUtils.equals(info.getItemId(), careItemId)) {
										info.setAnswerTime(createTime);
										info.setAnswerTimeStr(answerTimeStr);
									}
								}
							}
						}
					};
				}
			}
			
			DBCursor leaveMessage = dsForRW.getDB().getCollection("p_leave_message").find(queryTiems).sort(sort);
			List<Integer> userIds = Lists.newArrayList();
			while (leaveMessage.hasNext()) {
				DBObject obj = leaveMessage.next();
				String id = MongodbUtil.getString(obj, "_id");
				String careItemId = MongodbUtil.getString(obj, "careItemId");
				String message = MongodbUtil.getString(obj, "message");
				Long createTime = MongodbUtil.getLong(obj, "createTime");
				String voice = MongodbUtil.getString(obj, "voice");
				List<String> pics = (List<String>) obj.get("images");
				Integer userId = MongodbUtil.getInteger(obj, "userId");
				userIds.add(userId);
				for (OrderExcelVo orderExcelVo : orderExcelVos) {
					if (orderExcelVo != null && orderExcelVo.getCareItemIds()!=null && orderExcelVo.getCareItemIds().contains(careItemId)) {
						MessageExcelVo messageExcelVo = new MessageExcelVo();
						messageExcelVo.setId(id);
						if (StringUtils.isNotEmpty(message)) {							
							messageExcelVo.setMessage(message);
						}
						messageExcelVo.setCreateTime(createTime);
						if (StringUtils.isNotEmpty(voice)) {							
							messageExcelVo.setVoice(voice);
						}
						if (pics != null && pics.size() > 0) {							
							messageExcelVo.setPics(pics);
						}
						messageExcelVo.setUserId(userId);
						if (orderExcelVo.getCareMessage() == null) {
							List<MessageExcelVo> messageExcelVos = Lists.newArrayList();
							messageExcelVos.add(messageExcelVo);
							orderExcelVo.setCareMessage(messageExcelVos);
						} else {
							orderExcelVo.getCareMessage().add(messageExcelVo);
						}
					}
				}
			}
			
			//对message进行排序
			for(OrderExcelVo orderExcelVo : orderExcelVos) {
				if (orderExcelVo.getCareMessage() != null) {
					orderExcelVo.getCareMessage().sort((p, o) -> p.getCreateTime().compareTo(o.getCreateTime()));
				}
			}
			
			
			if (userIds != null && userIds.size() > 0) {
				//查询user表
				DBObject userQuery = new BasicDBObject();
				BasicDBObject in = new BasicDBObject();
				in.put(QueryOperators.IN, userIds);
				userQuery.put("_id", in);
				Map<Integer, Integer> userTypeMap = Maps.newHashMap();
				DBCursor userCursor = dsForRW.getDB().getCollection("user").find(userQuery);
				while (userCursor.hasNext()) {
					DBObject obj = userCursor.next();
					Integer id = MongodbUtil.getInteger(obj, "_id");
					Integer userType = MongodbUtil.getInteger(obj, "userType");
					userTypeMap.put(id, userType);
				}
				
				for(OrderExcelVo orderExcelVo : orderExcelVos) {
					if (orderExcelVo != null && orderExcelVo.getCareMessage() != null && orderExcelVo.getCareMessage().size() > 0) {
						StringBuffer messageBurrer = new StringBuffer();
						for (MessageExcelVo messageExcelVo : orderExcelVo.getCareMessage()) {
							Integer userId = messageExcelVo.getUserId();
							Long createTime = messageExcelVo.getCreateTime();
							String createTimeStr = null;
							if (createTime != null) {							
								createTimeStr = simpleDateFormat.format(createTime);
							}
							if (Objects.nonNull(userTypeMap) && Objects.nonNull(userTypeMap.get(userId)) && userTypeMap.get(userId).intValue() == UserEnum.UserType.patient.getIndex()) {
								//患者
								messageBurrer.append("患者留言（").append(createTimeStr).append("）：");
							} else {
								//医生
								messageBurrer.append("医生回复（").append(createTimeStr).append("）：");
							}
							
							if (StringUtils.isNotEmpty(messageExcelVo.getMessage())) {
								messageBurrer.append(messageExcelVo.getMessage());								
							}
							if (messageExcelVo.getPics() != null && messageExcelVo.getPics().size() > 0) {
								messageBurrer.append("。图片：");
								for (String pic : messageExcelVo.getPics()) {
									messageBurrer.append("[图片URL]").append(",");
								}
								if (messageBurrer != null &&messageBurrer.length() > 0) {
									messageBurrer.setLength(messageBurrer.length() - 1);
								}
								messageBurrer.append("。");
							}
							if (StringUtils.isNotEmpty(messageExcelVo.getVoice())) {
								messageBurrer.append("。声音：");
								messageBurrer.append("[声音URL]");
								messageBurrer.append("。").append("\n");
							}
						}
						orderExcelVo.setMessage(messageBurrer.toString());
					}
					
				}
			}
			
		}
		return orderExcelVos;
	}

}
