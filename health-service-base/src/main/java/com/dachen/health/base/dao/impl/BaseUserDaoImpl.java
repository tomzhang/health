package com.dachen.health.base.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class BaseUserDaoImpl extends NoSqlRepository implements IBaseUserDao {

    /**
     * </p>获取医生真实信息</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    public BaseUserVO getUser(Integer userId) {
        DBObject query = new BasicDBObject();
        query.put("_id", userId);

        DBObject project = new BasicDBObject();
        project.put("_id", 1);
        project.put("status", 1);
        project.put("userType", 1);
        project.put("name", 1);
        project.put("telephone", 1);
        project.put("birthday", 1);
        project.put("headPicFileName", 1);
        project.put("doctor", 1);
        project.put("settings", 1);
        project.put("sex", 1);

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query, project);

        BaseUserVO vo = null;
        if (obj != null) {
            vo = new BaseUserVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setSex(MongodbUtil.getInteger(obj, "sex"));
            vo.setStatus(MongodbUtil.getInteger(obj, "status"));
            vo.setUserType(MongodbUtil.getInteger(obj, "userType"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            vo.setAge(DateUtil.calcAge(MongodbUtil.getLong(obj, "birthday")));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));

            if (vo.getUserType() != null && vo.getUserType() == UserEnum.UserType.doctor.getIndex()) {
                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if(doctor!=null){
                    vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setHospitalId(MongodbUtil.getString(doctor, "hospitalId"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                    vo.setCheckInGive(MongodbUtil.getInteger(doctor, "checkInGive") == null? 1:MongodbUtil.getInteger(doctor, "checkInGive"));
                    DBObject check = (BasicDBObject) doctor.get("check");
                    if (check != null) {						
                    	vo.setCheckRemark(MongodbUtil.getString(check, "remark"));
					}
                }
            }
            if (obj.get("settings") != null) {
            	DBObject settings = (BasicDBObject) obj.get("settings");
            	Map<String, Object> settingsMap = new HashMap<String, Object>();
            	settingsMap.put("doctorVerify", settings.get("doctorVerify"));
            	vo.setSettings(settingsMap);
            }
        
        }

        return vo;
    }

    /**
     * </p>通过id查询用户</p>
     * 
     * @param userIds
     * @return
     * @author fanp
     * @date 2015年8月19日
     */
    public List<BaseUserVO> getByIds(Integer[] userIds,Integer status) {
        DBObject query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", userIds));
        if (status != null){
        	query.put("status", status);
        }

        DBObject project = new BasicDBObject();
        project.put("_id", 1);
        project.put("status", 1);
        project.put("userType", 1);
        project.put("name", 1);
        project.put("telephone", 1);
        project.put("birthday", 1);
        project.put("age", 1);
        project.put("sex", 1);
        project.put("headPicFileName", 1);
        project.put("doctor", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, project).sort(new BasicDBObject("_id", 1));

        List<BaseUserVO> list = new ArrayList<BaseUserVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            BaseUserVO vo = new BaseUserVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setStatus(MongodbUtil.getInteger(obj, "status"));
            vo.setUserType(MongodbUtil.getInteger(obj, "userType"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            vo.setBirthday(MongodbUtil.getLong(obj, "birthday"));
            if (MongodbUtil.getLong(obj, "birthday") != null) {
            	vo.setAge(DateUtil.calcAge(MongodbUtil.getLong(obj, "birthday")));
            } else if (MongodbUtil.getInteger(obj, "age") != null) {
            	vo.setAge(MongodbUtil.getInteger(obj, "age"));
            }
            vo.setSex(MongodbUtil.getInteger(obj, "sex"));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
            

            if (vo.getUserType() != null && vo.getUserType() == UserEnum.UserType.doctor.getIndex()) {
                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if(doctor!=null){
                    vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                }
            }
            list.add(vo);
        }

        return list;
    }
    
    /**
     * </p>通过id查询用户</p>
     * 
     * @param userIds
     * @return
     * @author tanyf
     * @date 2016年6月28日
     */
    public List<BaseUserVO> getByIds(Integer[] userIds){
    	return this.getByIds(userIds, null);
    }
    
    /**
     * </p>查找集团下所有医生</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    public List<Integer> getDoctorIdByGroup(String groupId) {
        DBObject query = new BasicDBObject();
        query.put("groupId", groupId);
        query.put("status", "C");

        DBObject project = new BasicDBObject();
        project.put("doctorId", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query, project);

        List<Integer> list = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        return list;
    }
    
    public List<Integer> getDoctorIdByGroup(String groupId,String[] statuses){
    	return this.getDoctorIdByGroup(groupId, statuses, null);
    }
    
    /**
     * </p>查找集团下所有医生</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月1日
     */
    public List<Integer> getDoctorIdByGroup(String groupId,String[] statuses,String keyword) {
        DBObject query = new BasicDBObject();
        query.put("groupId", groupId);
        query.put("status", new BasicDBObject("$in",statuses));

        DBObject project = new BasicDBObject();
        project.put("doctorId", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(query, project);

        List<Integer> list = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "doctorId"));
        }
        
        if (StringUtils.isNotBlank(keyword)){
        	// 根据关键词 过滤 医生
            Pattern pattern = Pattern.compile("^.*" + StringUtil.trim(keyword) + ".*$", Pattern.CASE_INSENSITIVE);
            BasicDBList or = new BasicDBList();
            
            or.add(new BasicDBObject("name", pattern));
            or.add(new BasicDBObject("telephone", pattern));
            DBObject userQuery = new BasicDBObject();
            userQuery.put("_id", new BasicDBObject("$in",list));
            userQuery.put("$or", or);
            DBObject projection = new BasicDBObject();
            projection.put("_id", 1);

            DBCursor userCursor = dsForRW.getDB().getCollection("user").find(userQuery, project);

            List<Integer> reList = new ArrayList<Integer>();
            while (userCursor.hasNext()) {
                DBObject obj = userCursor.next();
                reList.add(MongodbUtil.getInteger(obj, "_id"));
            }
            return reList;
        }else{
        	return list;
        }
    }

    /**
     * </p>设置医患关系</p>
     * 
     * @param doctorId
     * @param patientId
     * @author fanp
     * @date 2015年9月9日
     */
    public void setDoctorPatient(Integer doctorId, Integer patientId){
        //添加双向医患关系
        int userId=0;
        int toUserId=0;
        for(int i = 0;i<2;i++){
            if(i==0){
                userId=doctorId;
                toUserId=patientId;
            }else{
                userId=patientId;
                toUserId=doctorId;
            }
            
            DBObject query = new BasicDBObject();
            query.put("userId", userId);
            query.put("toUserId", toUserId);
            
            DBObject update = new BasicDBObject();
            update.put("userId", userId);
            update.put("toUserId", toUserId);
            update.put("createTime", System.currentTimeMillis());
            update.put("status", UserEnum.RelationStatus.normal.getIndex());
            update.put("setting.defriend",1);
            update.put("setting.topNews",1);
            update.put("setting.messageMasking",1);
            update.put("setting.collection",1);
            
            dsForRW.getDB().getCollection("u_doctor_patient").update(query, new BasicDBObject("$set",update), true, false);
        }
    }

    /**
     * </p>设置医患关系</p>
     *
     * @param doctorId
     * @param patientId
     * @author fuyongde
     * @date 2015年9月9日
     */
    public void addDoctorPatient(Integer doctorId, Integer patientId, Integer userId, String remarkName, String remark){
        //添加双向医患关系

        //1、先查询userId为医生，toUserId为用户，patientId为患者id的记录存在
        DBObject queryForDoctor = new BasicDBObject();
        queryForDoctor.put("userId", doctorId);
        queryForDoctor.put("toUserId", userId);
        queryForDoctor.put("patientId", patientId);
        DBCursor cursorForDoctor = dsForRW.getDB().getCollection("u_doctor_patient").find(queryForDoctor);
        if (cursorForDoctor.hasNext()) {
            //存在这样的记录，则什么都不做
        } else {

            DBObject update = new BasicDBObject();
            update.put("userId", doctorId);
            update.put("toUserId", userId);
            update.put("patientId", patientId);
            update.put("createTime", System.currentTimeMillis());
            if (StringUtils.isNotBlank(remarkName)) {
                update.put("remarkName", remarkName);
            }
            if (StringUtils.isNotBlank(remark)) {
                update.put("remark", remark);
            }
            update.put("status", UserEnum.RelationStatus.normal.getIndex());
            update.put("setting.defriend",1);
            update.put("setting.topNews",1);
            update.put("setting.messageMasking",1);
            update.put("setting.collection",1);
            dsForRW.getDB().getCollection("u_doctor_patient").update(queryForDoctor, new BasicDBObject("$set",update), true, false);
        }

        //2、再查询userId为用户，toUserId为医生 的记录
        DBObject queryForPatient = new BasicDBObject();
        queryForPatient.put("userId", userId);
        queryForPatient.put("toUserId", doctorId);
        DBCursor cursorForPatient = dsForRW.getDB().getCollection("u_doctor_patient").find(queryForPatient);

        if (cursorForDoctor.hasNext()) {
            //存在这样的记录，则什么都不做
        } else {
            //患者端反向存储的时候，没有patientId字段
            DBObject update = new BasicDBObject();
            update.put("userId", userId);
            update.put("toUserId", doctorId);
            update.put("createTime", System.currentTimeMillis());
            update.put("status", UserEnum.RelationStatus.normal.getIndex());
            update.put("setting.defriend",1);
            update.put("setting.topNews",1);
            update.put("setting.messageMasking",1);
            update.put("setting.collection",1);

            dsForRW.getDB().getCollection("u_doctor_patient").update(queryForPatient, new BasicDBObject("$set",update), true, false);
        }
    }

    
    /**
     * </p>通过用户id查找集团信息</p>
     * @param userId
     * @return
     * @author fanp
     * @date 2015年9月15日
     */
    public BaseUserVO getGroupById(Integer userId, String groupId) {
        BaseUserVO vo = null;
        
        //查找医生所在集团
        DBObject gdQuery = new BasicDBObject();
        gdQuery.put("doctorId", userId);
        gdQuery.put("groupId", groupId);
        gdQuery.put("status", "C");
        
        DBObject gdProject = new BasicDBObject();
        gdProject.put("contactWay", 1);
        
        DBObject gdObj = dsForRW.getDB().getCollection("c_group_doctor").findOne(gdQuery, gdProject);
		if (gdObj != null) {
			vo = new BaseUserVO();
            setBaseUserInfo(vo, gdObj, groupId);
        }
        return vo;
    }
    
    public BaseUserVO getGroupByUserId(Integer userId) {
        BaseUserVO vo = null;
        
        //查找医生所在集团
        DBObject gdQuery = new BasicDBObject();
        gdQuery.put("doctorId", userId);
        gdQuery.put("status", "C");
        
        DBObject gdProject = new BasicDBObject();
        gdProject.put("contactWay", 1);
        gdProject.put("groupId", 1);
        
        DBObject orderBy = new BasicDBObject();
		orderBy.put("type", 1);
		orderBy.put("isMain", -1);
		
		DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(gdQuery, gdProject).sort(orderBy).limit(1);
		while (cursor.hasNext()) {
			DBObject gdObj = cursor.next();
			vo = new BaseUserVO();
			String groupId = MongodbUtil.getString(gdObj,"groupId");
            setBaseUserInfo(vo, gdObj, groupId);
        }
        return vo;
    }

	private void setBaseUserInfo(BaseUserVO vo, DBObject gdObj, String groupId) {
		vo.setContactWay(MongodbUtil.getString(gdObj,"contactWay"));
		//查找集团名称
		if (StringUtil.isNotBlank(groupId)) {
			// 获取集团信息
		    DBObject gQuery = new BasicDBObject();
		    gQuery.put("_id", new ObjectId(groupId));
		    
		    DBObject gProject = new BasicDBObject();
		    gProject.put("name", 1);
		    gProject.put("certStatus", 1);
		    DBObject gObj = dsForRW.getDB().getCollection("c_group").findOne(gQuery, gProject);
		    
			if (gObj != null) {
		        vo.setGroupId(groupId);
		        vo.setGroupName(MongodbUtil.getString(gObj, "name"));
		        vo.setCertStatus(MongodbUtil.getString(gObj, "certStatus"));
		    }
		}
	}
    
    /**
     * 通过用户id、集团id查找组织信息
     * @param userId
     * @param groupId
     * @return
     */
    public BaseUserVO getDepartment(Integer userId, String groupId){
        BaseUserVO vo = new BaseUserVO();
        
        if(StringUtil.isNotBlank(groupId)){
			// 获取组织信息
        	BasicDBObject query = new BasicDBObject();
            query.put("groupId", groupId);
            query.put("doctorId", userId);
            
            BasicDBObject project = new BasicDBObject();
            project.put("departmentId", 1);
            DBObject obj = dsForRW.getDB().getCollection("c_department_doctor").findOne(query, project);
            
            if (obj != null) {
            	String departmentId = MongodbUtil.getString(obj,"departmentId");
            	vo.setDepartmentId(departmentId);
            	
            	query = new BasicDBObject();
                query.put("_id", new ObjectId(departmentId));
                
                project = new BasicDBObject();
                project.put("name", 1);
                obj = dsForRW.getDB().getCollection("c_department").findOne(query, project);
                
                if (obj != null) {
                	vo.setDepartmentName(MongodbUtil.getString(obj, "name"));
                }
            }
        }
        return vo;
    }

	@Override
	public BaseUserVO getUserByTelephoneAndType(Integer type,String telephone) {
        DBObject query = new BasicDBObject();
        query.put("telephone", telephone);
        query.put("userType", type);

        DBObject project = new BasicDBObject();
        project.put("_id", 1);
        project.put("status", 1);
        project.put("userType", 1);
        project.put("name", 1);
        project.put("telephone", 1);
        project.put("birthday", 1);
        project.put("headPicFileName", 1);
        project.put("doctor", 1);
        

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query, project);

        BaseUserVO vo = null;
        if (obj != null) {
            vo = new BaseUserVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setStatus(MongodbUtil.getInteger(obj, "status"));
            vo.setUserType(MongodbUtil.getInteger(obj, "userType"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            vo.setAge(DateUtil.calcAge(MongodbUtil.getLong(obj, "birthday")));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));

            if (vo.getUserType() != null && vo.getUserType() == UserEnum.UserType.doctor.getIndex()) {
                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if(doctor!=null){
                    vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                }
            }
        
        }

        return vo;
    }

	@Override
	public BaseUserVO getUserByDoctorNum(String doctorNum) {
        DBObject query = new BasicDBObject();
        query.put("doctor.doctorNum", doctorNum);

        DBObject project = new BasicDBObject();
        project.put("_id", 1);
        project.put("status", 1);
        project.put("userType", 1);
        project.put("name", 1);
        project.put("telephone", 1);
        project.put("birthday", 1);
        project.put("headPicFileName", 1);
        project.put("doctor", 1);
        

        DBObject obj = dsForRW.getDB().getCollection("user").findOne(query, project);

        BaseUserVO vo = null;
        if (obj != null) {
            vo = new BaseUserVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setStatus(MongodbUtil.getInteger(obj, "status"));
            vo.setUserType(MongodbUtil.getInteger(obj, "userType"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            vo.setAge(DateUtil.calcAge(MongodbUtil.getLong(obj, "birthday")));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));

            if (vo.getUserType() != null && vo.getUserType() == UserEnum.UserType.doctor.getIndex()) {
                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if(doctor!=null){
                    vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                }
            }
        
        }

        return vo;
    }
    
	

    /* (non-Javadoc)
     * @see com.dachen.health.base.dao.IBaseUserDao#getUserPatientIdByGroup(java.lang.String)
     */
    public List<Integer> getUserPatientIdByGroup(String groupId) {
    	List<Integer> doctorIds= getDoctorIdByGroup(groupId);
        
        DBObject query = new BasicDBObject();
        query.put("userId", new BasicDBObject("$in",doctorIds));
        
        DBObject project = new BasicDBObject();
        project.put("toUserId", 1);
        
        DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query,project);
        List<Integer> list = new ArrayList<Integer>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            list.add(MongodbUtil.getInteger(obj, "toUserId"));
        }
        return list;
    }
    
    

	  /**
   * </p>查找医生的所有患者</p>
   * 
   * @param doctorId
   * @return
   * @author qujunli
   * @date 2015年9月1日
   */
  public List<Integer> getUserPatientIdByDoctorId(Integer doctorId) {
      
      DBObject query = new BasicDBObject();
      query.put("userId",doctorId);
      
      DBObject project = new BasicDBObject();
      project.put("toUserId", 1);
      
      DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query,project);
      List<Integer> list = new ArrayList<Integer>();
      while (cursor.hasNext()) {
          DBObject obj = cursor.next();
          list.add(MongodbUtil.getInteger(obj, "toUserId"));
      }
      return list;
  }
  
  /* (non-Javadoc)
 * @see com.dachen.health.base.dao.IBaseUserDao#getUserPatientIdByDoctorId(java.util.List)
 */
  @Override
public List<Integer> getUserPatientIdByDoctorIds(List<Integer> doctorIdList){
      DBObject query = new BasicDBObject();
      query.put("userId",new BasicDBObject("$in",doctorIdList));
      
      DBObject project = new BasicDBObject();
      project.put("toUserId", 1);
      
      DBCursor cursor = dsForRW.getDB().getCollection("u_doctor_patient").find(query,project);
      List<Integer> list = new ArrayList<Integer>();
      while (cursor.hasNext()) {
          DBObject obj = cursor.next();
          list.add(MongodbUtil.getInteger(obj, "toUserId"));
      }
      return list;
  }
  
  /* (non-Javadoc)
 * @see com.dachen.health.base.dao.IBaseUserDao#filterInactivePatientId(java.util.List)
 */
  @Override
public List<Integer> filterInactivePatientIds(List<Integer> PatientIdList){
      DBObject query = new BasicDBObject();
      query.put("_id",new BasicDBObject("$in",PatientIdList));
      query.put("status",new BasicDBObject("$ne",UserStatus.inactive.getIndex()) );
      
      
      DBObject project = new BasicDBObject();
      project.put("_id", 1);
      
      DBCursor cursor = dsForRW.getDB().getCollection("user").find(query,project);
      List<Integer> list = new ArrayList<Integer>();
      while (cursor.hasNext()) {
          DBObject obj = cursor.next();
          list.add(MongodbUtil.getInteger(obj, "_id"));
      }
      return list;
  }
    
  
  public BaseUserVO getGroupMsgByUserId(Integer userId) {
      BaseUserVO vo = null;
      
      //查找医生所在集团
      DBObject gdQuery = new BasicDBObject();
      gdQuery.put("doctorId", userId);
      gdQuery.put("status", "C");
      gdQuery.put("isMain", true);
      
      DBObject gdProject = new BasicDBObject();
      gdProject.put("deptName", 1);
      gdProject.put("groupId", 1);
      
      gdProject.put("name", 1);
      
      
      DBObject gdObj = dsForRW.getDB().getCollection("c_group_doctor").findOne(gdQuery, gdProject);
		if (gdObj != null) {
			vo = new BaseUserVO();
			String groupId = MongodbUtil.getString(gdObj,"groupId");
			String deptName = MongodbUtil.getString(gdObj,"deptName");
			String groupName = MongodbUtil.getString(gdObj,"name");
			vo.setGroupId(groupId);
			vo.setGroupName(groupName);
			vo.setDepartmentName(deptName);
      } else {
      	gdQuery.removeField("isMain");
      	DBCursor gdCursor = dsForRW.getDB().getCollection("c_group_doctor").find(gdQuery, gdProject);
      	vo = new BaseUserVO();
      	if (gdCursor.count() == 1) {
      		vo = new BaseUserVO();
			String groupId = MongodbUtil.getString(gdObj,"groupId");
			String deptName = MongodbUtil.getString(gdObj,"deptName");
			String groupName = MongodbUtil.getString(gdObj,"name");
			vo.setGroupId(groupId);
			vo.setGroupName(groupName);
			vo.setDepartmentName(deptName);
      	} 
      	
      }
      return vo;
  }

	@Override
	public List<BaseUserVO> getTopLevelByIds(Integer[] userIds) {
		DBObject query = new BasicDBObject();
        query.put("_id", new BasicDBObject("$in", userIds));
        query.put("doctor.titleRank", new BasicDBObject("$in", new Integer[]{1,2}));
        query.put("status", UserEnum.UserStatus.normal.getIndex());

        DBObject project = new BasicDBObject();
        project.put("_id", 1);
        project.put("status", 1);
        project.put("userType", 1);
        project.put("name", 1);
        project.put("telephone", 1);
        project.put("birthday", 1);
        project.put("age", 1);
        project.put("sex", 1);
        project.put("headPicFileName", 1);
        project.put("doctor", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query, project).sort(new BasicDBObject("_id", 1));

        List<BaseUserVO> list = new ArrayList<BaseUserVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            BaseUserVO vo = new BaseUserVO();
            vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
            vo.setStatus(MongodbUtil.getInteger(obj, "status"));
            vo.setUserType(MongodbUtil.getInteger(obj, "userType"));
            vo.setName(MongodbUtil.getString(obj, "name"));
            vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
            if (MongodbUtil.getLong(obj, "birthday") != null) {
            	vo.setAge(DateUtil.calcAge(MongodbUtil.getLong(obj, "birthday")));
            } else if (MongodbUtil.getInteger(obj, "age") != null) {
            	vo.setAge(MongodbUtil.getInteger(obj, "age"));
            }
            vo.setSex(MongodbUtil.getInteger(obj, "sex"));
            vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
            

            if (vo.getUserType() != null && vo.getUserType() == UserEnum.UserType.doctor.getIndex()) {
                DBObject doctor = (BasicDBObject) obj.get("doctor");
                if(doctor!=null){
                    vo.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
                    vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
                    vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
                    vo.setTitle(MongodbUtil.getString(doctor, "title"));
                }
            }
            list.add(vo);
        }

        return list;
	}

}
