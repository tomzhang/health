package com.dachen.health.user.dao.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.base.entity.vo.TitleVO;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.DoctorInfoChangeEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.doctor.service.ICommonGroupDoctorService;
import com.dachen.health.user.dao.IDoctorDao;
import com.dachen.health.user.entity.param.DoctorParam;
import com.dachen.health.user.entity.param.GetRecheckInfo;
import com.dachen.health.user.entity.param.ResetDoctorInfo;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.health.user.entity.po.DoctorCheckInfoChange;
import com.dachen.health.user.entity.vo.DoctorVO;
import com.dachen.util.BeanUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.VerificationUtil;
import com.google.common.collect.Lists;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Pattern;

@Repository
public class DoctorDaoImpl extends NoSqlRepository implements IDoctorDao {

    @Autowired
    private IBaseDataDao baseDataDao;
    @Autowired
    private ICommonGroupDoctorService commonGroupDoctorService;
    
    @Resource
    private DiseaseTypeRepository diseaseTypeRepository;
    
    @Autowired
    private IBaseDataService baseDataService;
    
    /**
     * 获取个人介绍和擅长领域
     * @param userId
     * @return
     * @author fanp
     * @date 2015年7月7日
     */
    public Map<String, Object> getIntro(Integer userId) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query);

        if (Objects.nonNull(obj)) {
            
            DBObject doctor = (BasicDBObject) obj.get("doctor");
            
            if (Objects.nonNull(doctor)) {
                
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("doctorName", MongodbUtil.getString(obj, "name"));
                map.put("introduction", MongodbUtil.getString(doctor, "introduction"));
                map.put("scholarship", MongodbUtil.getString(doctor, "scholarship"));
                map.put("experience", MongodbUtil.getString(doctor, "experience"));
                map.put("cureNum", MongodbUtil.getInteger(doctor, "cureNum"));
                
                //增加护士角色
                Integer  role = MongodbUtil.getInteger(doctor, "role");
                if(null==role || role.intValue()==0) {	
                	role =UserEnum.DoctorRole.doctor.getIndex();
                }
                map.put("role", role);
                
                //skill与expertise都不为空 则优先显示expertise
                map.put("skill", MongodbUtil.getString(doctor, "skill"));
                
                BasicDBList expertise=  (BasicDBList)doctor.get("expertise");
                if(Objects.nonNull(expertise)) {
                    
	                List<String> diseaseTypeIds=new ArrayList<String>();
	                for (Object object : expertise) {
	                	diseaseTypeIds.add((String)object);
					}
	                
	                if(!diseaseTypeIds.isEmpty()){
		                List<DiseaseType> diseaseTypes=diseaseTypeRepository.findByIds(diseaseTypeIds);
		                map.put("expertise",diseaseTypes);
	                }
                }
               
                return map;
            }
        }
        return null;
    }

    /**
     * </p>设置医生个人介绍</p>
     * 
     * @param userId
     *            用户id
     * @param introduction
     *            个人介绍
     * @author fanp
     * @date 2015年7月7日
     */
    public void updateIntro(Integer userId, String introduction) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject update = new BasicDBObject();
        update.put("doctor.introduction", introduction.trim());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }

    /**
     * </p>设置医生擅长领域</p>
     * 
     * @param userId
     *            用户id
     * @param skill
     *            擅长
     * @author fanp
     * @date 2015年7月7日
     */
    public void updateSkill(Integer userId, String skill) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject update = new BasicDBObject();
        update.put("doctor.skill", skill.trim());

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }

    @Override
    public void updateScholarship(Integer userId, String scholarship) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject update = new BasicDBObject();
        update.put("doctor.scholarship", scholarship);

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }

    @Override
    public void updateExperience(Integer userId, String experience) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject update = new BasicDBObject();
        update.put("doctor.experience", experience);

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }

    /**
     * </p>修改职业信息</p>
     * 
     * @author fanp
     * @date 2015年7月7日
     */
    public DoctorVO getWork(Integer userId) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject field = new BasicDBObject();
        field.put("_id", 0);
        field.put("doctor.hospital", 1);
        field.put("doctor.departments", 1);
        field.put("doctor.title", 1);

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query, field);

        if (obj != null) {
            DBObject doctor = (BasicDBObject) obj.get("doctor");
            if (doctor != null) {
                DoctorVO vo = new DoctorVO();
                vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                vo.setTitle(MongodbUtil.getString(doctor, "title"));

                return vo;
            }
        }
        return null;
    }

    /**
     * </p>修改医生职业信息，未认证是修改认证信息也是修改职业信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月13日
     */
    public void updateWork(DoctorParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        if (param.getStatuses() != null && param.getStatuses().length > 0) {
            query.put("status", new BasicDBObject("$in", param.getStatuses()));
        }

        BasicDBObject update = new BasicDBObject();
        if (StringUtil.isNotBlank(param.getName())) {
            update.put("name", param.getName());
        }
        if (StringUtil.isNotBlank(param.getHospital()) && StringUtil.isNotBlank(param.getHospitalId())) {
            HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
            if(hvo == null || !StringUtil.equals(hvo.getName(), param.getHospital())){
                throw new ServiceException("医院有误，请重新选择");
            }
            
            update.put("doctor.hospital", StringUtil.trim(param.getHospital()));
            update.put("doctor.hospitalId", StringUtil.trim(param.getHospitalId()));
        }
        if (StringUtil.isNotBlank(param.getHospital()) && StringUtil.isBlank(param.getHospitalId())) {
            update.put("doctor.hospital", StringUtil.trim(param.getHospital()));
            List<HospitalVO> hospitals = baseDataService.getHospitals(param.getHospital());
			if (hospitals != null && hospitals.size() == 1) {
				update.put("doctor.hospitalId", hospitals.get(0).getId());
			} else {
				update.put("doctor.hospitalId", "");
			}
        }
        if (StringUtil.isNotBlank(param.getDepartments())) {
            DeptVO dvo = baseDataDao.getDept(param.getDepartments());
            if(dvo == null){
                throw new ServiceException("科室有误，请重新选择");
            }
            update.put("doctor.departments", StringUtil.trim(param.getDepartments()));
            DeptVO dept = baseDataDao.getDept(StringUtil.trim(param.getDepartments()));
            if (dept != null) {
            	update.put("doctor.deptId", dept.getId());
            }
        }
        if (StringUtil.isNotBlank(param.getDeptId())) {
        	update.put("doctor.deptId", StringUtil.trim(param.getDepartments()));
        }
        if (StringUtil.isNotBlank(param.getTitle())) {
            TitleVO tvo = baseDataDao.getTitle(param.getTitle());
            if(tvo == null){
                throw new ServiceException("职称有误，请重新选择");
            }
            
            update.put("doctor.title", StringUtil.trim(param.getTitle()));
            
            //查找职称排行
            TitleVO titleVO = baseDataDao.getTitle(param.getTitle());
            update.put("doctor.titleRank", titleVO.getRank());
        }
        
        /*
    private Integer provinceId;
    private Integer cityId;
    private Integer countryId;
    private String province;
    private String city;
    private String country;*/
        if (param.provinceId > 0 && StringUtil.isNotBlank(param.provinceName)) {
        	// update
        	update.put("doctor.provinceId", param.provinceId);
        	update.put("doctor.province", param.provinceName);
        }
        
        if (param.cityId > 0 && StringUtil.isNotBlank(param.cityName)) {
        	// update
        	update.put("doctor.cityId", param.cityId);
        	update.put("doctor.city", param.cityName);
        }
        
        if (param.countyId > 0 && StringUtil.isNotBlank(param.countyName)) {
        	// update
        	update.put("doctor.countryId", param.countyId);
        	update.put("doctor.country", param.countyName);
        }
        
        //医生重新提交审核信息时需修改
        if(param.getStatus() != null && param.getStatus() == UserEnum.UserStatus.uncheck.getIndex()){
            update.put("status", param.getStatus());
        }
        
        if(!update.isEmpty()){
            dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
        }
    }

    /**
     * </p>医生获取认证信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年7月15日
     */
    public DoctorVO getCheckInfo(DoctorParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getUserId());
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject field = new BasicDBObject();
        field.put("_id", 0);
        field.put("name", 1);
        field.put("status", 1);
        field.put("userLevel", 1);
        field.put("limitedPeriodTime", 1);
        field.put("headPicFileName", 1);
        field.put("telephone", 1);
        field.put("doctor", 1);

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query, field);
        if (obj != null) {
            DoctorVO vo = new DoctorVO();
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setStatus(MongodbUtil.getInteger(obj, "status"));
            vo.setUserLevel(MongodbUtil.getInteger(obj, "userLevel"));
            vo.setLimitedPeriodTime(MongodbUtil.getLong(obj, "limitedPeriodTime"));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));

            DBObject doctor = (BasicDBObject) obj.get("doctor");
            if (doctor != null) {
                if (vo.getStatus() != null && vo.getStatus() == UserEnum.UserStatus.normal.getIndex()) {
                    // 医生审核通过，查询审核信息
                    DBObject check = (BasicDBObject) doctor.get("check");
                    if (check != null) {
                        vo.setHospital(MongodbUtil.getString(check, "hospital"));
                        vo.setDepartments(MongodbUtil.getString(check, "departments"));
                        vo.setTitle(MongodbUtil.getString(check, "title"));
                        vo.setDeptPhone(MongodbUtil.getString(check, "deptPhone"));
                    }
                } else {
                    // 医生未审核通过，查找职业信息
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                    vo.setDeptPhone(MongodbUtil.getString(doctor, "deptPhone"));
                    DBObject check = (BasicDBObject) doctor.get("check");
                    if (check != null) {
                        vo.setRemark(MongodbUtil.getString(check, "remark"));
                    }

                }

                return vo;
            }
        }

        return null;
    }

	@Override
	public Object search(DoctorParam param) {
        DBObject query = new BasicDBObject();
        if(!StringUtil.isEmpty(param.getDoctorNum())){
        	query.put("doctor.doctorNum", param.getDoctorNum());
        }
        if(!StringUtil.isEmpty(param.getTelephone())){
        	query.put("telephone", param.getTelephone());
        }
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject field = new BasicDBObject();
        field.put("password", 0);
   /*     field.put("_id", 0);
        field.put("name", 1);
        field.put("status", 1);
        field.put("doctor", 1);*/
        
        

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query, field);
        if(obj!=null){
        	 Map<Object, Object> data = user2Map(param,obj);
        	return data;
        }else{
        	throw new ServiceException(20005, "找不到对应用户");
        }
       
    }
	
	
	@Override
	public Object searchs(DoctorParam param) {
        DBObject query = new BasicDBObject();
        if(!StringUtil.isEmpty(param.getKeyWord())){
        	DBObject[] subquery=new DBObject[]{new BasicDBObject("doctor.doctorNum", param.getKeyWord()),new BasicDBObject("telephone", param.getKeyWord())};
        	query.put("$or",subquery);
        }
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject field = new BasicDBObject();
        field.put("password", 0);
   /*     field.put("_id", 0);
        field.put("name", 1);
        field.put("status", 1);
        field.put("doctor", 1);*/
        
        

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query,field);
        List retdata=new ArrayList();
        while(cursor.hasNext()){
        	DBObject obj=cursor.next();
        	 Map<Object, Object> data = user2Map(param,obj);
        	retdata.add(data);
        }
        
        return retdata;
       
    }

	private Map<Object, Object> user2Map(DoctorParam param,DBObject obj) {
	    Map<String,Object> map = commonGroupDoctorService.getContactBySameGroup(param.getUserId(), MongodbUtil.getInteger(obj, "_id"));
        
        Integer ret = (Integer)map.get("hasGroup");
        
        Map<Object,Object> data=new HashMap<Object,Object>();
        data.putAll(obj.toMap());
        data.put("headPicFileName", UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj,"headPicFileName"), MongodbUtil.getInteger(obj, "_id")));
        data.putAll(map);
        int hasGroup=(ret>0)?1:2;
        data.put("hasGroup",(hasGroup));
        
		return data;
	}
	
	
	/**
	 * 设置专长

	 * @author 			李淼淼
	 * @date 2015年9月22日
	 */
	@Override
	public void updateExpertise(Integer userId,String[] diseaseTypeIds){
		DBObject query=new BasicDBObject();
		query.put("_id", userId);
		DBObject update=new BasicDBObject();
		DBObject diseaseType=new BasicDBObject();
		//Map [] dts=new Map[diseaseTypeIds.length];
	/*	List<Map> dts=new ArrayList<Map>();
		for (int i=0;i<diseaseTypeIds.length;i++) {
			String diseaseTypeId=diseaseTypeIds[i];
			
			DiseaseType dt=diseaseTypeRepository.findOne("id", diseaseTypeId) ;
			if(dt!=null){
				dts.add(BeanUtils.toMap(dt));
			}
		}*/
		
		if (diseaseTypeIds != null && diseaseTypeIds.length > 0) {
			List<String> ids = Lists.newArrayList();
			for (int i = 0; i < diseaseTypeIds.length; i++) {
				if(StringUtil.isNotEmpty(diseaseTypeIds[i])) {
					ids.add(diseaseTypeIds[i]);
				}
			}
			diseaseType.put("doctor.expertise", ids);
		}else {
			diseaseType.put("doctor.expertise", null);
		}
		update.put("$set", diseaseType);
		dsForRW.getDB().getCollection("user").update(query, update);
	}
	
	
	/**
	 * 删除专长

	 * @author 			李淼淼
	 * @date 2015年9月22日
	 */
	@Override
	public void deleteExpertise(Integer userId,String[] diseaseTypeIds){
		DBObject query=new BasicDBObject();
		query.put("_id", userId);
		DBObject update=new BasicDBObject();
		DBObject diseaseType=new BasicDBObject();
		diseaseType.put("doctor.expertise",  new BasicDBObject("$in", diseaseTypeIds));
		update.put("$pull", diseaseType);
		dsForRW.getDB().getCollection("user").update(query, update,false,true);
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	@Override
	public Object getExpertise(Integer userId) {
		DBObject query=new BasicDBObject();
		query.put("_id", userId);
		DBObject projection=new BasicDBObject();
		projection.put("doctor.expertise", 1);
	
		DBObject ret=dsForRW.getDB().getCollection("user").findOne(query, projection);
		Map doctor=(Map)ret.get("doctor");
		List<DiseaseTypeVO> retData = new ArrayList<DiseaseTypeVO>();
		if(doctor!=null){
			BasicDBList expreriseIdList=(BasicDBList)doctor.get("expertise");
			if(expreriseIdList!=null)
			{
			List<String> ids = Arrays.asList(expreriseIdList.toArray(new String[]{}));
			retData = baseDataService.getDiseaseType(ids);
			}
		}
		return retData;
	}

	@Override
	public void updateCureNum(Integer userId, Integer cureNum) {
	 	DBObject query = new BasicDBObject();
        query.put("_id", userId);
        query.put("userType", UserEnum.UserType.doctor.getIndex());

        DBObject update = new BasicDBObject();
        update.put("doctor.cureNum", cureNum);

        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
	}
	
	 /**
     * </p>设置医生的免打扰</p>
     * 
     * @param userIdByDoctor
     *            用户id
     * @param troubleFree
     *            个人介绍
     * @author fanp
     * @date 2015年7月7日
     */
    public void updateMsgDisturb(int userIdByDoctor, String troubleFree) {
    	// 查询语句
        DBObject query = new BasicDBObject();
        query.put("_id", userIdByDoctor);
        query.put("userType", UserEnum.UserType.doctor.getIndex());
        // 更新语句
        DBObject update = new BasicDBObject();
        update.put("doctor.troubleFree", troubleFree);
        // 执行DB更新方法
        dsForRW.getDB().getCollection("user").update(query, new BasicDBObject("$set", update));
    }

    @Override
	public Map<String, Object> researchDoctors(String doctorName,
			String hospitalId, int pageIndex, int pageSize) {

		Map<String,Object> map = new HashMap<String,Object>();
		int limit =pageSize;
//		Pattern pattern = Pattern.compile("^.*王.*$", Pattern.CASE_INSENSITIVE);
		
		Pattern pattern = Pattern.compile("^.*" + doctorName + ".*$", Pattern.CASE_INSENSITIVE);
		DBObject query = new BasicDBObject();
		query.put("userType", UserEnum.UserType.doctor.getIndex());
		query.put("doctor.hospitalId", hospitalId);
		query.put("name", pattern);
//		DBObject sort = new BasicDBObject();
//		sort.put("weight", 1);
//		sort.put("createTime",-1);
		int skip = (pageIndex-1)*pageSize;
		skip = skip < 0 ? 0 : skip;
		List<User> list = new ArrayList<User>();
		DBCollection collection = dsForRW.getDB().getCollection("user");
		
//		DBCursor cursor = collection.find(query).sort(sort).skip(skip).limit(limit);
		DBCursor cursor = collection.find(query).skip(skip).limit(limit);
		User vo = null;
		while(cursor.hasNext()){
//			vo = new User();
			DBObject obj = cursor.next();
			vo =setField( obj);
			list.add(vo);
		}
		cursor.close();
		int  count = collection.find(query).count();
		map.put("count", count);
		map.put("list", list);
		return map;
	
	}

    @Override
    public Integer getCheckInfoStatus(int userId) {
        DBObject query = new BasicDBObject();
        query.put("userId", userId);
        query.put("infoStatus", DoctorInfoChangeEnum.InfoStatus.uncheck.getIndex());
        long count = dsForRW.getDB().getCollection("doctor_reset_check_info").count(query);
        if ((count == 1)) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public void resetCheckInfo(ResetDoctorInfo param) {
        DBObject query = new BasicDBObject();
        query.put("userId", param.getUserId());
        query.put("infoStatus", DoctorInfoChangeEnum.InfoStatus.uncheck.getIndex());
        BasicDBObject update = new BasicDBObject();
        update.put("modifyTime", System.currentTimeMillis());
        if (StringUtil.isNotBlank(param.getName())) {
            update.put("name", param.getName());
        }
        if (StringUtil.isNotBlank(param.getHeadPicFileName())) {
            update.put("headPicFileName", param.getHeadPicFileName());
        }
        if (!CollectionUtils.isEmpty(param.getCheckImage())) {
            update.put("checkImage", param.getCheckImage());
        }
        if (StringUtil.isNotBlank(param.getDeptPhone())) {
            if (!VerificationUtil.checkTelephone(param.getDeptPhone())) {
                throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
            }
            update.put("deptPhone", param.getDeptPhone());
        }
        if (StringUtil.isNotBlank(param.getHospital()) && StringUtil.isNotBlank(param.getHospitalId())) {
            HospitalVO hvo = baseDataDao.getHospital(param.getHospitalId());
            if (hvo == null || !StringUtil.equals(hvo.getName(), param.getHospital())) {
                throw new ServiceException("医院有误，请重新选择");
            }
            update.put("hospital", StringUtil.trim(param.getHospital()));
            update.put("hospitalId", StringUtil.trim(param.getHospitalId()));
        }
        if (StringUtil.isNotBlank(param.getDepartments())) {
            DeptVO dvo = baseDataDao.getDept(param.getDepartments());
            if (dvo == null) {
                throw new ServiceException("科室有误，请重新选择");
            }
            update.put("departments", StringUtil.trim(param.getDepartments()));
            DeptVO dept = baseDataDao.getDept(StringUtil.trim(param.getDepartments()));
            if (dept != null) {
                update.put("deptId", dept.getId());
            }
        }
        if (StringUtil.isNotBlank(param.getTitle())) {
            TitleVO tvo = baseDataDao.getTitle(param.getTitle());
            if (tvo == null) {
                throw new ServiceException("职称有误，请重新选择");
            }
            update.put("title", StringUtil.trim(param.getTitle()));
        }
        if (!update.isEmpty()) {
            dsForRW.getDB().getCollection("doctor_reset_check_info").update(query, new BasicDBObject("$set", update));
        }
    }

    @Override
    public void addCheckInfo(ResetDoctorInfo param) {
        DoctorCheckInfoChange copy = BeanUtil.copy(param, DoctorCheckInfoChange.class);
        copy.setInfoStatus(DoctorInfoChangeEnum.InfoStatus.uncheck.getIndex());
        copy.setCreateTime(System.currentTimeMillis());
        copy.setModifyTime(System.currentTimeMillis());
        this.dsForRW.insert(copy);
    }

    @Override
    public PageVO getAfterCheckInfo(GetRecheckInfo getRecheckInfo) {
        if (Objects.isNull(getRecheckInfo.getPageSize())) {
            getRecheckInfo.setPageSize(15);
        }
        if (Objects.isNull(getRecheckInfo.getPageIndex())) {
            getRecheckInfo.setPageIndex(0);
        }
        Query<DoctorCheckInfoChange> query = dsForRW.createQuery(DoctorCheckInfoChange.class);
        if (StringUtil.isNoneBlank(getRecheckInfo.getName())) {
            query.field("name").containsIgnoreCase(getRecheckInfo.getName());
        }
        query.filter("infoStatus", getRecheckInfo.getInfoStatus());
        query.retrievedFields(true, "userId", "name", "infoStatus", "verifyResult", "verifyTime", "createTime");
        query.order("-createTime");
        query.offset(getRecheckInfo.getPageIndex() * getRecheckInfo.getPageSize());
        query.limit(getRecheckInfo.getPageSize());
        PageVO pageVO = new PageVO();
        pageVO.setTotal(query.countAll());
        pageVO.setPageData(query.asList());
        pageVO.setPageIndex(getRecheckInfo.getPageIndex());
        pageVO.setPageSize(getRecheckInfo.getPageSize());
        return pageVO;
    }

    @Override
    public DoctorCheckInfoChange getCheckInfoDetail(String id) {
        Query<DoctorCheckInfoChange> query = dsForRW.createQuery(DoctorCheckInfoChange.class);
        query.filter("_id", new ObjectId(id));
        return query.get();
    }

    @Override
    public void handleCheckInfo(ResetDoctorInfo resetDoctorInfo) {
        Query<DoctorCheckInfoChange> query = dsForRW.createQuery(DoctorCheckInfoChange.class);
        UpdateOperations<DoctorCheckInfoChange> ops = dsForRW.createUpdateOperations(DoctorCheckInfoChange.class);
        query.field("_id").equal(new ObjectId(resetDoctorInfo.getId()));
        ops.set("verifyResult", resetDoctorInfo.getVerifyResult());
        if (StringUtil.isNoneBlank(resetDoctorInfo.getName())) {
            ops.set("name", resetDoctorInfo.getName());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getHeadPicFileName())) {
            ops.set("headPicFileName", resetDoctorInfo.getHeadPicFileName());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getHospital())) {
            ops.set("hospital", resetDoctorInfo.getHospital());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getHospitalId())) {
            ops.set("hospitalId", resetDoctorInfo.getHospitalId());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getDepartments())) {
            ops.set("departments", resetDoctorInfo.getDepartments());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getDeptId())) {
            ops.set("deptId", resetDoctorInfo.getDeptId());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getTitle())) {
            ops.set("title", resetDoctorInfo.getTitle());
        }
        if (StringUtil.isNoneBlank(resetDoctorInfo.getDeptPhone())) {
            if (!VerificationUtil.checkTelephone(resetDoctorInfo.getDeptPhone())) {
                throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
            }
            ops.set("deptPhone", resetDoctorInfo.getDeptPhone());
        }
        ops.set("infoStatus", DoctorInfoChangeEnum.InfoStatus.check.getIndex());
        ops.set("modifyTime", System.currentTimeMillis());
        ops.set("verifyTime", System.currentTimeMillis());
        dsForRW.update(query, ops).getUpdatedCount();
    }

    @Override
    public User updateUserCheckInfo(ResetDoctorInfo resetDoctorInfo) {
        Query<User> query = dsForRW.createQuery(User.class);
        query.field("_id").equal(resetDoctorInfo.getUserId());
        UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);
        ops.set("modifyTime", System.currentTimeMillis());
        if (StringUtil.isNotBlank(resetDoctorInfo.getName())) {
            ops.set("name", resetDoctorInfo.getName());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getHeadPicFileName())) {
            ops.set("headPicFileName", resetDoctorInfo.getHeadPicFileName());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getHospital())) {
            ops.set("doctor.hospital", resetDoctorInfo.getHospital());
            ops.set("doctor.check.hospital", resetDoctorInfo.getHospital());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getHospitalId())) {
            ops.set("doctor.hospitalId", resetDoctorInfo.getHospitalId());
            ops.set("doctor.check.hospitalId", resetDoctorInfo.getHospitalId());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getDepartments())) {
            ops.set("doctor.departments", resetDoctorInfo.getDepartments());
            ops.set("doctor.check.departments", resetDoctorInfo.getDepartments());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getDeptId())) {
            ops.set("doctor.deptId", resetDoctorInfo.getDeptId());
            ops.set("doctor.check.deptId", resetDoctorInfo.getDeptId());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getTitle())) {
            ops.set("doctor.title", resetDoctorInfo.getTitle());
            ops.set("doctor.check.title", resetDoctorInfo.getTitle());
        }
        if (StringUtil.isNotBlank(resetDoctorInfo.getDeptPhone())) {
            if (!VerificationUtil.checkTelephone(resetDoctorInfo.getDeptPhone())) {
                throw new ServiceException(VerificationUtil.CheckRegexEnum.固话.getPrompt());
            }
            ops.set("doctor.deptPhone", resetDoctorInfo.getDeptPhone());
            ops.set("doctor.check.deptPhone", resetDoctorInfo.getDeptPhone());
        }
        return dsForRW.findAndModify(query, ops);
    }

    @Override
    public DoctorCheckInfoChange getDealingInfo(int userId, int infoStatus) {
        Query<DoctorCheckInfoChange> query = dsForRW.createQuery(DoctorCheckInfoChange.class);
        query.filter("userId", userId);
        query.filter("infoStatus", infoStatus);
        return query.get();
    }

    @Override
    public Long getUncheckInfoCount() {
        Query<DoctorCheckInfoChange> query = dsForRW.createQuery(DoctorCheckInfoChange.class);
        query.filter("infoStatus", DoctorInfoChangeEnum.InfoStatus.uncheck.getIndex());
        return query.countAll();
    }

    private User setField(DBObject obj){
		if(obj == null){
			return null;
		}
		User vo = new User();
		vo.setName(MongodbUtil.getString(obj, "name") );
		Integer userId =MongodbUtil.getInteger(obj, "_id");
		vo.setUserId(userId );
		String headPic=MongodbUtil.getString(obj,"headPicFileName");
		vo.setHeadPicFileName(headPic);
	    if(null!=	obj.get("doctor"))
	    {	
	    	Doctor doctor = new Doctor();
	    	DBObject doc = (BasicDBObject) obj.get("doctor");
			if(null != doc) {
				doctor.setTitle(MongodbUtil.getString(doc, "title"));
				doctor.setHospital(MongodbUtil.getString(doc, "hospital"));
				doctor.setHospitalId(MongodbUtil.getString(doc, "hospitalId"));
				doctor.setDeptId(MongodbUtil.getString(doc, "deptId"));
				doctor.setDepartments(MongodbUtil.getString(doc, "departments"));
				vo.setDoctor(doctor);
			}
	    }
		return vo;
	}

}
