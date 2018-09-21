package com.dachen.health.group.group.dao.impl;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.elasticsearch.handler.ElasticSearchFactory;
import com.dachen.elasticsearch.handler.param.AbstractSearchParam;
import com.dachen.elasticsearch.handler.param.BoolSearchParam;
import com.dachen.elasticsearch.handler.param.SearchParam;
import com.dachen.elasticsearch.util.TypeDefine.Constants;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.po.HospitalPO;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.GroupEnum.GroupDoctorStatus;
import com.dachen.health.commons.constants.GroupEnum.GroupType;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.User;
import com.dachen.health.disease.dao.DiseaseTypeRepository;
import com.dachen.health.disease.entity.DiseaseType;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.param.GroupSearchParam;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.GroupSearchVO;
import com.dachen.health.user.entity.po.Doctor;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

@Repository
public class GroupSearchDaoImp extends NoSqlRepository implements IGroupSearchDao {
	
	private static Logger logger = LoggerFactory.getLogger(GroupSearchDaoImp.class);

    @Autowired
    protected IBaseUserDao baseUserDao;
    
    @Autowired
    protected DiseaseTypeRepository diseaseTypeRepository;
    
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected IGroupDoctorDao groupDoctorDao;
    @Autowired
    protected IGroupDao groupDao;
    
    @Autowired
    protected IBaseDataDao baseDataDao;
    
    /**
     * </p>获取全部医生集团</p>
     * 
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findAllGroup(GroupSearchParam param) {
        List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();
        List<String> groupIds = new ArrayList<String>();
        DBObject query = new BasicDBObject();
		//query.put("certStatus", "P");
        UserSession user = ReqUtil.instance.getUser();
        if(UserType.patient.getIndex() == user.getUserType().intValue() || UserType.guest.getIndex() == user.getUserType().intValue()){
        	query.put("active", GroupEnum.GroupActive.active.getIndex());
//        	XGSF-5089  患者端不应该看到医院信息
        	query.put("type", "group");
        	//患者端获取全部集团时要将屏蔽的集团过滤掉（2016-7-7傅永德）
            query.put("skip", GroupEnum.GroupSkipStatus.normal.getIndex());
            //患者端获取全部集团时只返回已经审核通过的（2016-9-7傅永德）
            query.put("applyStatus", GroupEnum.GroupApplyStatus.pass.getIndex());
        }
        
        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query).sort(new BasicDBObject("weight", -1)).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            //平台不显示
            if (GroupUtil.PLATFORM_ID.equals(MongodbUtil.getString(obj, "_id")))
            	continue;
            
            GroupSearchVO vo = new GroupSearchVO();
            vo.setGroupId(MongodbUtil.getString(obj, "_id"));
            vo.setGroupName(MongodbUtil.getString(obj, "name"));
            vo.setIntroduction(MongodbUtil.getString(obj, "introduction"));
            vo.setCertPath(MongodbUtil.getString(obj, "logoUrl"));//(UserHelper.buildCertPath(vo.getGroupId(), MongodbUtil.getInteger(obj, "creator")));
            //vo.setCureNum(MongodbUtil.getInteger(obj, "cureNum"));
            vo.setCertStatus(MongodbUtil.getString(obj, "certStatus"));

            vo.setCureNum(getGroupCureNum(vo.getGroupId()));//不从数据库取就诊量,而是统计集团所有医生的就诊量

            DBObject configObj = (DBObject)obj.get("config");
            if(configObj != null){
                if(configObj.get("memberApply") != null){
                    vo.setMemberApply((Boolean)configObj.get("memberApply"));
                }
                if(configObj.get("memberInvite") != null){
                    vo.setMemberApply((Boolean)configObj.get("memberInvite"));
                }
            }
            
            //医生集团擅长病种
            BasicDBList dbList = (BasicDBList)obj.get("diseaselist");
            if(dbList!=null){
                vo.setDisease(this.getDisease(Arrays.asList(dbList.toArray(new String[]{}))));    
            }
            
            groupIds.add(vo.getGroupId());
            
            list.add(vo);
        }
        
        //查找专家数
        Map<String,Integer> map = getGroupExperNum(groupIds,1);
        for(GroupSearchVO vo:list){
            Integer expertNum = map.get(vo.getGroupId());
            vo.setExpertNum(expertNum == null ? 0 : expertNum);
        }

        return list;
    }
    
    public Map<String,Integer> getGroupExperNum(List<String> groupIds,Integer userStatus) {
    	
    	Map<String,Integer> map = new HashMap<String,Integer>();
    	for(String groupId :groupIds) {
    		DBObject query = new BasicDBObject();
    		query.put("groupId", groupId);
    		query.put("status", "C");
    		DBCursor cursors = dsForRW.getDB().getCollection("c_group_doctor").find(query);
    		List<Integer> docIds = new ArrayList<Integer>();
    		 while (cursors.hasNext()) {
    	         DBObject obj = cursors.next();
    	         docIds.add(Integer.valueOf(obj.get("doctorId").toString()));
    		 }
    		map.put(groupId, userRepository.countUsers2(docIds, userStatus));
    	}
    	
    	return map;
    }
    
    /**
     * </p>获取集团医生数</p>
     * @param groupIds
     * @return
     * @author fanp
     * @date 2015年10月9日
     */
    public Map<String,Integer> getGroupExperNum1(List<String> groupIds){
        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * db.c_group_doctor.aggregate([<br>
         *   { "$match" : { "groupId" : { "$in" : ['55d588fccb827c15fc9d47b3']} , "status":"C"}}, <br>
         *   { "$project" : { "_id" : 0 , "groupId" : 1}}, <br>
         *   { "$group" : { "_id" : "$groupId" , "value" : { "$sum" : 1}}}<br>
         * ])<br>
         * 2.返回结果如下 { "_id" : 216, "count" : 15 }<br>
         */
        
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("groupId", new BasicDBObject("$in",groupIds));
        matchFields.put("status", GroupEnum.GroupDoctorStatus.正在使用.getIndex());
        
        DBObject match = new BasicDBObject("$match", matchFields);

        // 返回字段
        BasicDBObject projectFields = new BasicDBObject();
        projectFields.put("_id", 0);
        projectFields.put("groupId", 1);
       
        DBObject project = new BasicDBObject("$project", projectFields);
        
        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$groupId");
        groupFields.put("value", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(group);
        
        Map<String,Integer> map = new HashMap<String,Integer>();
        
        DBCollection collection = dsForRW.getDB().getCollection("c_group_doctor");
        
        AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject obj = it.next();

            map.put(MongodbUtil.getString(obj, "_id"), MongodbUtil.getInteger(obj, "value"));
        }
        
        return map;
    }
    
    /**
     * </p>搜索医生集团（集团名／医生名／病种 ）</p>
     * 
     * @param param 关键字或病种查询
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findGroup(GroupSearchParam param) {
        List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();
        List<String> groupIds = new ArrayList<String>();
        
        DBObject query = new BasicDBObject();
        //query.put("certStatus", "P");
        UserSession user = ReqUtil.instance.getUser();
        if(UserType.patient.getIndex() == user.getUserType().intValue()){
        	query.put("active", GroupEnum.GroupActive.active.getIndex());
        }
        if(StringUtil.isNotBlank(param.getKeyword()) && StringUtil.isBlank(param.getDiseaseId())){
            //关键字模糊检索
            
            //查找病种
            List<String> diseaseIds = diseaseTypeRepository.findIdByName(param.getKeyword());
            Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
            
            if(diseaseIds!=null&&diseaseIds.size()>0){
                BasicDBList or = new BasicDBList();
                
                or.add(new BasicDBObject("name", pattern));
                or.add(new BasicDBObject("diseaselist", new BasicDBObject("$in",diseaseIds)));
                
                query.put("$or",or);
            }else{
                query.put("name", pattern);
            }
            
        }else if(StringUtil.isNotBlank(param.getDiseaseId()) && StringUtil.isBlank(param.getKeyword())){
            Query<DiseaseType> q = dsForRW.createQuery(DiseaseType.class);
            q.criteria("_id").equal(param.getDiseaseId());
            DiseaseType diseaseTypes=q.get();
            BasicDBList or = new BasicDBList();

            or.add(new BasicDBObject("diseaselist",diseaseTypes.getParent()));
            or.add(new BasicDBObject("diseaselist",param.getDiseaseId()));
            query.put("$or",or);

            //病种精确检索
            query.put("diseaselist", param.getDiseaseId());
        }

        DBObject sort = new BasicDBObject();
        sort.put("weight", -1);

        // 搜索集团匹配名称
        query.put("type", GroupType.group.getIndex());//只搜索集团，不搜索医院
        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            GroupSearchVO vo = new GroupSearchVO();
            vo.setGroupId(MongodbUtil.getString(obj, "_id"));
            vo.setGroupName(MongodbUtil.getString(obj, "name"));
            vo.setIntroduction(MongodbUtil.getString(obj, "introduction"));
            vo.setCertPath(MongodbUtil.getString(obj, "logoUrl"));//(UserHelper.buildCertPath(vo.getGroupId(), MongodbUtil.getInteger(obj, "creator")));
            //vo.setCureNum(MongodbUtil.getInteger(obj, "cureNum"));
            vo.setCertStatus(MongodbUtil.getString(obj, "certStatus"));

            vo.setCureNum(getGroupCureNum(vo.getGroupId()));//不从数据库取就诊量,而是统计集团所有医生的就诊量

            DBObject configObj = (DBObject)obj.get("config");
            if(configObj != null){
                if(configObj.get("memberApply") != null){
                    vo.setMemberApply((Boolean)configObj.get("memberApply"));
                }
                if(configObj.get("memberInvite") != null){
                    vo.setMemberApply((Boolean)configObj.get("memberInvite"));
                }
            }

            //医生集团擅长病种
            BasicDBList dbList = (BasicDBList)obj.get("diseaselist");
            if(dbList!=null){
                vo.setDisease(this.getDisease(Arrays.asList(dbList.toArray(new String[]{}))));    
            }
            
            groupIds.add(vo.getGroupId());
            
            list.add(vo);
        }

        //查找专家数
        Map<String,Integer> map = this.getGroupExperNum(groupIds,1);
        
        for(GroupSearchVO vo:list){
            Integer expertNum = map.get(vo.getGroupId());
            vo.setExpertNum(expertNum == null ? 0 : expertNum);
        }
        
        return list;

    }

    @Override
    public List<GroupSearchVO> findGroupIds(List<String> diseaseIds, int pageIndex, int pageSize) {
        List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();
        List<String> groupIds = new ArrayList<String>();

        Query<DiseaseType> q = dsForRW.createQuery(DiseaseType.class);
        q.criteria("_id").in(diseaseIds);

        List<DiseaseType> diseaseTypes=q.asList();
        for(DiseaseType diseaseType:diseaseTypes){
            diseaseIds.add(diseaseType.getParent());
        }

        DBObject query = new BasicDBObject();
        query.put("certStatus", "P");
        UserSession user = ReqUtil.instance.getUser();
        if(UserType.patient.getIndex() == user.getUserType().intValue()){
            query.put("active", GroupEnum.GroupActive.active.getIndex());
        }

            //病种精确检索
        BasicDBList or = new BasicDBList();

        for(int i=0;i<diseaseIds.size();i++){
            or.add(new BasicDBObject("diseaselist", diseaseIds.get(i)));
        }

        query.put("$or",or);
        //
        ////病种户内精确匹配
        //BasicDBList parseOr = new BasicDBList();
        //for(int i=0;i<parseIds.size();i++){
        //    parseOr.add(new BasicDBObject("diseaselist", parseIds.get(i)));
        //}
        //query.put("$or",or);


        DBObject sort = new BasicDBObject();
        sort.put("weight", -1);

        // 搜索集团匹配名称
        query.put("type", GroupType.group.getIndex());//只搜索集团，不搜索医院
        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query).sort(sort);
                //.skip(pageIndex*pageSize).limit(pageSize);
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            GroupSearchVO vo = new GroupSearchVO();
            vo.setGroupId(MongodbUtil.getString(obj, "_id"));
            vo.setGroupName(MongodbUtil.getString(obj, "name"));
            vo.setIntroduction(MongodbUtil.getString(obj, "introduction"));
            vo.setCertPath(MongodbUtil.getString(obj, "logoUrl"));//(UserHelper.buildCertPath(vo.getGroupId(), MongodbUtil.getInteger(obj, "creator")));
            //vo.setCureNum(MongodbUtil.getInteger(obj, "cureNum"));
            vo.setCertStatus(MongodbUtil.getString(obj, "certStatus"));

            vo.setCureNum(getGroupCureNum(vo.getGroupId()));//不从数据库取就诊量,而是统计集团所有医生的就诊量

            DBObject configObj = (DBObject)obj.get("config");
            if(configObj != null){
                if(configObj.get("memberApply") != null){
                    vo.setMemberApply((Boolean)configObj.get("memberApply"));
                }
                if(configObj.get("memberInvite") != null){
                    vo.setMemberApply((Boolean)configObj.get("memberInvite"));
                }
            }

            //医生集团擅长病种
            BasicDBList dbList = (BasicDBList)obj.get("diseaselist");
            if(dbList!=null){
                vo.setDisease(this.getDisease(Arrays.asList(dbList.toArray(new String[]{}))));
            }

            groupIds.add(vo.getGroupId());

            list.add(vo);
        }

        //查找专家数
        Map<String,Integer> map = this.getGroupExperNum(groupIds,1);

        for(GroupSearchVO vo:list){
            Integer expertNum = map.get(vo.getGroupId());
            vo.setExpertNum(expertNum == null ? 0 : expertNum);
        }

        return list;
    }

    /**
     * </p>搜索医生（集团名／医生名／病种 ）</p>
     * 
     * @param param 关键字或病种查询
     * @return
     * @author fanp
     * @date 2015年9月28日
     */
    public List<GroupSearchVO> findDoctor(GroupSearchParam param) {
        //由于需要按职称、患者数排序
        
        List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();

        DBObject query = new BasicDBObject();
        if(StringUtil.isNotBlank(param.getKeyword()) && StringUtil.isBlank(param.getDiseaseId())){
            //关键字模糊检索
            
            //查找病种
            List<String> diseaseIds = diseaseTypeRepository.findIdByName(param.getKeyword());
            Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
            
            if(diseaseIds!=null&&diseaseIds.size()>0) {
                BasicDBList or = new BasicDBList();
                or.add(new BasicDBObject("name", pattern));
                or.add(new BasicDBObject("doctor.expertise", new BasicDBObject("$in",diseaseIds)));
                query.put("$or",or);
            }else{
            	// 模糊找查医生名和模糊找查医生的擅长介绍
            	BasicDBList or = new BasicDBList();
            	or.add(new BasicDBObject("name", pattern));
            	or.add(new BasicDBObject("doctor.skill", pattern));
            	query.put("$or",or);
                // 病种精确检索
//              query.put("name", pattern);
            }
            
        }else if(StringUtil.isNotBlank(param.getDiseaseId()) && StringUtil.isBlank(param.getKeyword())){
            //病种查找
            query.put("doctor.expertise", param.getDiseaseId());
        }
        query.put("userType", UserEnum.UserType.doctor.getIndex());
        query.put("status", 1);
        DBObject sort = new BasicDBObject();
        sort.put("doctor.titleRank", 1);
        sort.put("doctor.cureNum", -1);

        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            GroupSearchVO vo = convert(obj);
            
            //获取所属集团
            BaseUserVO userVO = baseUserDao.getGroupByUserId(vo.getDoctorId());
            if(userVO!=null){
                vo.setGroupId(userVO.getGroupId());
                vo.setGroupName(userVO.getGroupName());
                vo.setCertStatus(userVO.getCertStatus());
            }
            
            list.add(vo);
        }

        return list;
    }
    
    public List<GroupSearchVO> findDoctor(String diseaseId, String deptId, List<Integer> doctorIds) {
    	List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();
    	DBObject query = new BasicDBObject();
    	if (StringUtil.isNotBlank(diseaseId)) {
    		//病种查找
    		Pattern pattern = Pattern.compile("^"+diseaseId+".*$");
    		query.put("doctor.expertise", pattern);
    	}
    	if (StringUtil.isNotBlank(deptId)) {
    		//科室查找
    		Pattern pattern = Pattern.compile("^"+deptId+".*$");
    		query.put("doctor.deptId", pattern);
    	}
        query.put("userType", UserEnum.UserType.doctor.getIndex());
        query.put("status", 1);
        if (doctorIds != null && !doctorIds.isEmpty()) {
        	DBObject inQuery = new BasicDBObject();
        	inQuery.put("$in", doctorIds);
        	query.put("_id", inQuery);
        }
        DBObject sort = new BasicDBObject();
        sort.put("doctor.titleRank", 1);
        sort.put("doctor.cureNum", -1);
        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).sort(sort);
        while (cursor.hasNext()) {
        	 DBObject obj = cursor.next();
             GroupSearchVO vo = convert(obj);
             list.add(vo);
        }
        return list;
    }

    /**
     * DBObject转换成GroupSearchVO
     * @param obj
     * @return
     */
	private GroupSearchVO convert(DBObject obj) {
		GroupSearchVO vo = new GroupSearchVO();
		vo.setDoctorId(MongodbUtil.getInteger(obj, "_id"));
		vo.setDoctorName(MongodbUtil.getString(obj, "name"));
		
		Integer userType = MongodbUtil.getInteger(obj, "userType");
		if (userType != null && userType == UserEnum.UserType.doctor.getIndex()) {
		    DBObject doctor = (BasicDBObject) obj.get("doctor");
		    if(doctor!=null){
		        vo.setHospital(MongodbUtil.getString(doctor, "hospital"));
		        vo.setDepartments(MongodbUtil.getString(doctor, "departments"));
		        vo.setTitle(MongodbUtil.getString(doctor, "title"));
		        vo.setSkill(MongodbUtil.getString(doctor, "skill"));
		        vo.setCureNum(MongodbUtil.getInteger(doctor, "cureNum"));
		        vo.setTroubleFree(MongodbUtil.getString(doctor, "troubleFree"));
		        Integer  role  = MongodbUtil.getInteger(doctor, "role");
		        
		        if(null==role || role.intValue()==0)
				{	
					role=UserEnum.DoctorRole.doctor.getIndex();	
				}
		        vo.setRole(role);
		        //设置擅长病种
		        BasicDBList dbList = (BasicDBList)doctor.get("expertise");
		        if(dbList!=null){
		            vo.setDisease(this.getDisease(Arrays.asList(dbList.toArray(new String[]{}))));    
		        }
		    }
		}
		vo.setHeadPicFileName(UserHelper.buildHeaderPicPath(MongodbUtil.getString(obj, "headPicFileName"), vo.getDoctorId()));
		return vo;
	}
    
    public String findDiseaseOnIds(List<String> diseaseIds) {
    	return getDisease(diseaseIds);
    }
    
    public String findDoctorGroupName(Integer doctorId) {
    	GroupDoctor groupDoctorParam = new GroupDoctor();
    	groupDoctorParam.setDoctorId(doctorId);
    	GroupDoctor groupDoctor = groupDoctorDao.getById(groupDoctorParam);
    	
    	if(groupDoctor!=null) {
    		Group group = groupDao.getById(groupDoctor.getGroupId());
    		if(group!=null){
    			return group.getName();
    		}
    	}
    	return null;
    }

    /**
     * </p>通过病种id获取病种name</p>
     * @param diseaseIds
     * @return
     * @author fanp
     * @date 2015年10月8日
     */
    public String getDisease(List<String> diseaseIds){
        if(diseaseIds==null || diseaseIds.size()==0){
            return "";
        }
        List<DiseaseType> diseases = diseaseTypeRepository.findByIds(diseaseIds);
        
        //医生集团擅长病种
        StringBuilder disease = new StringBuilder();
        if(diseases!=null){
            for(DiseaseType type:diseases){

                if(disease.length()==0){
                    disease.append(type.getName());
                }else{
                    disease.append("、");
                    disease.append(type.getName());
                }
            }
        }
        
        return disease.toString();
    }
    
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.group.dao.IGroupSearchDao#findGroupByName(com.dachen.health.group.group.entity.param.GroupSearchParam)
     */
    public List<GroupSearchVO> findGroupByName(GroupSearchParam param){
        List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();
        List<String> groupIds = new ArrayList<String>();
        DBObject query = new BasicDBObject();
        List<ObjectId> inUseGroupIds = new ArrayList<ObjectId>();
        
    	//参数校验
    	
    	//关键字判断
        if(param.getKeyword() != null && !StringUtils.isEmpty(param.getKeyword().trim())){
        	Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
        	
        	//匹配集团名称或者集团简介
        	BasicDBList keyMatch = new BasicDBList();
        	keyMatch.add(new BasicDBObject("name", pattern));
        	keyMatch.add(new BasicDBObject("introduction", pattern));
        	
    		query.put("$or", keyMatch);
        }
    	//过滤掉不允许加入的医生集团
    	if(param.isMemberApply()){
    		query.put("config.memberApply", new BasicDBObject("$ne", false));
    	}
        
    	//过滤已加入的集团
    	DBObject groupDoctorQuery = new BasicDBObject();
    	groupDoctorQuery.put("doctorId", param.getDoctorId());
    	groupDoctorQuery.put("status", GroupDoctorStatus.正在使用.getIndex());
    	DBCursor groupDoctorCursor = dsForRW.getDB().getCollection("c_group_doctor").find(groupDoctorQuery);
    	  	
    	while (groupDoctorCursor.hasNext()) {
    		  DBObject obj = groupDoctorCursor.next();
    		  inUseGroupIds.add(new ObjectId(MongodbUtil.getString(obj, "groupId")));
    	}
    	if(inUseGroupIds.size() >0){
//    		query.put(QueryOperators.NIN, inUseGroupIds);
    		query.put("_id", new BasicDBObject(QueryOperators.NIN, inUseGroupIds));
    	}



        //TODO  排序暂定
        DBObject sort = new BasicDBObject();
        sort.put("weight", -1);

        // 搜索集团匹配名称
        query.put("type", GroupType.group.getIndex());//只搜索集团，不搜索医院
        
        if(ReqUtil.instance.getUser().getUserType().intValue() == UserEnum.UserType.doctor.getIndex()){
        	query.put("applyStatus", 
            		new BasicDBObject(QueryOperators.NIN, 
            				new String[]{GroupEnum.GroupApplyStatus.notpass.getIndex(),GroupEnum.GroupApplyStatus.audit.getIndex()}));
        }else{
        	query.put("active", GroupEnum.GroupActive.active.getIndex());
        }
        //添加集团状态的过滤，只查询正常的集团（2016-6-4傅永德）
        query.put("skip", GroupEnum.GroupSkipStatus.normal.getIndex());
        DBCursor cursor = dsForRW.getDB().getCollection("c_group").find(query).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            GroupSearchVO vo = new GroupSearchVO();
            vo.setGroupId(MongodbUtil.getString(obj, "_id"));
            vo.setGroupName(MongodbUtil.getString(obj, "name"));
            vo.setIntroduction(MongodbUtil.getString(obj, "introduction"));
            //vo.setCertPath(UserHelper.buildCertPath(vo.getGroupId(), MongodbUtil.getInteger(obj, "creator")));
            vo.setCertPath(MongodbUtil.getString(obj, "logoUrl"));
            //vo.setCureNum(MongodbUtil.getInteger(obj, "cureNum"));
            vo.setCertStatus(MongodbUtil.getString(obj, "certStatus"));

            vo.setCureNum(getGroupCureNum(vo.getGroupId()));//不从数据库取就诊量,而是统计集团所有医生的就诊量

            //医生集团擅长病种
            BasicDBList dbList = (BasicDBList)obj.get("diseaselist");
            if(dbList!=null){
                vo.setDisease(this.getDisease(Arrays.asList(dbList.toArray(new String[]{}))));    
            }
            
            groupIds.add(vo.getGroupId());
            
            //查询医生申请加入集团的状态
            DBObject applyQuery = new BasicDBObject();
            applyQuery.put("doctorId", param.getDoctorId());
            applyQuery.put("groupId", vo.getGroupId());
            DBObject applyObject = dsForRW.getDB().getCollection("c_group_doctor").findOne(applyQuery);
            DBObject configObj = (DBObject)obj.get("config");
            boolean isApply = true;
            if(configObj != null && configObj.get("memberApply") != null){
            	isApply = (Boolean)configObj.get("memberApply");
            }
            
            if(applyObject != null){
            	String applyStatus = MongodbUtil.getString(applyObject, "status");
            	if(GroupDoctorStatus.正在使用.getIndex().equals(applyStatus)){
            		//已加入集团
            		vo.setApplyStatus(GroupDoctorStatus.正在使用.getIndex());
            	}else if(GroupDoctorStatus.申请待确认.getIndex().equals(applyStatus)){
            		//申请过 审核中
            		vo.setApplyStatus(GroupDoctorStatus.申请待确认.getIndex());
            	}else{
            		//其它状态，需要重新申请加入
            		if(isApply){
            			vo.setApplyStatus(GroupDoctorStatus.允许申请.getIndex());
            		}else{
            			vo.setApplyStatus(GroupDoctorStatus.不允许申请.getIndex());
            		}
            	}
            }else {            	
            	if(isApply){
            		vo.setApplyStatus(GroupDoctorStatus.允许申请.getIndex());
            	}else{
            		vo.setApplyStatus(GroupDoctorStatus.不允许申请.getIndex());
            	}
            }
            
            list.add(vo);
        }

        //查找专家数
        Map<String,Integer> map = this.getGroupExperNum(groupIds,1);
        
        for(GroupSearchVO vo:list){
            Integer expertNum = map.get(vo.getGroupId());
            vo.setExpertNum(expertNum == null ? 0 : expertNum);
        }
        
        return list;
    }
    
    /* (non-Javadoc)
     * @see com.dachen.health.group.group.dao.IGroupSearchDao#findGroupDoctorStatus(com.dachen.health.group.group.entity.param.GroupSearchParam)
     */
    public GroupSearchVO findGroupDoctorStatus(GroupSearchParam param){
    	GroupSearchVO retInfo  =new GroupSearchVO();
    	//查询医生与医生集团的关系
    	DBObject groupDoctorQuery = new BasicDBObject();
    	groupDoctorQuery.put("doctorId",param.getDoctorId());
    	groupDoctorQuery.put("groupId", param.getDocGroupId());
    	DBCursor cursor = dsForRW.getDB().getCollection("c_group_doctor").find(groupDoctorQuery);
    	if(cursor == null || cursor.size() ==0){
    		retInfo.setDoctorStatus(GroupEnum.GroupDoctorStatus.允许申请.getIndex());
    	}else{
    		DBObject obj = cursor.one();
    		if(GroupEnum.GroupDoctorStatus.申请待确认.getIndex().equals(MongodbUtil.getString(obj, "status"))){
    			retInfo.setDoctorStatus(GroupEnum.GroupDoctorStatus.申请待确认.getIndex());
    		}else if(GroupEnum.GroupDoctorStatus.正在使用.getIndex().equals(MongodbUtil.getString(obj, "status"))){
    			retInfo.setDoctorStatus(GroupEnum.GroupDoctorStatus.正在使用.getIndex());
    		}else{
    			retInfo.setDoctorStatus(GroupEnum.GroupDoctorStatus.允许申请.getIndex());
    		}
    	}
    	
    	//查询医生是否有集团管理员权限
    	DBObject groupUserQuery = new BasicDBObject();
    	groupUserQuery.put("doctorId",param.getDoctorId());
    	groupUserQuery.put("objectId", param.getDocGroupId());
    	groupUserQuery.put("status",GroupEnum.GroupDoctorStatus.正在使用.getIndex());
    	DBCursor groupUsercursor = dsForRW.getDB().getCollection("c_group_user").find(groupUserQuery);
    	if(groupUsercursor == null || groupUsercursor.size() ==0 ){
    		retInfo.setGroupAdmin(false);
    	}else{
    		retInfo.setGroupAdmin(true);
    	}

    	return retInfo;
    }
    
    @Override
	public List<User> searchDocByKeyword(GroupSearchParam param) {
        //由于需要按职称、患者数排序
        List<User> list = new ArrayList<User>();
        DBObject query = new BasicDBObject();
        if(StringUtil.isNotBlank(param.getKeyword()) && StringUtil.isBlank(param.getDiseaseId())){
            //查找病种
            List<String> diseaseIds = diseaseTypeRepository.findIdByName(param.getKeyword());
            Pattern pattern = Pattern.compile("^.*" + param.getKeyword() + ".*$", Pattern.CASE_INSENSITIVE);
            
            if(diseaseIds!=null&&diseaseIds.size()>0) {
                BasicDBList or = new BasicDBList();
                or.add(new BasicDBObject("name", pattern));
                or.add(new BasicDBObject("doctor.expertise", new BasicDBObject("$in",diseaseIds)));
                query.put("$or",or);
            }else{
            	// 模糊找查医生名和模糊找查医生的擅长介绍
            	BasicDBList or = new BasicDBList();
            	or.add(new BasicDBObject("name", pattern));
            	or.add(new BasicDBObject("doctor.skill", pattern));
            	query.put("$or",or);
                // 病种精确检索
//              query.put("name", pattern);
            }
            
        }else if(StringUtil.isNotBlank(param.getDiseaseId()) && StringUtil.isBlank(param.getKeyword())){
            //病种查找
            query.put("doctor.expertise", param.getDiseaseId());
        }
        query.put("userType", UserEnum.UserType.doctor.getIndex());
        query.put("doctor", new BasicDBObject("$ne", null));
        query.put("doctor.doctorNum", new BasicDBObject("$exists", true));
        query.put("doctor", new BasicDBObject("$exists",true));
        query.put("status", 1);
        DBObject sort = new BasicDBObject();
        sort.put("doctor.titleRank", 1);
        sort.put("doctor.cureNum", -1);
        
        DBCursor cursor = dsForRW.getDB().getCollection("user").find(query).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            User vo = setField(obj);
            list.add(vo);
        }
        cursor.close();
        return list;
	}

	private User setField(DBObject obj){
            if(obj == null){
            	return null;
            }
             User vo = new User();
             vo.setUserId(MongodbUtil.getInteger(obj, "_id"));
             vo.setName(MongodbUtil.getString(obj, "name"));
             vo.setTelephone(MongodbUtil.getString(obj, "telephone"));
             vo.setSex(MongodbUtil.getInteger(obj, "sex"));
             vo.setHeadPicFileName(MongodbUtil.getString(obj, "headPicFileName"));
             vo.setArea(MongodbUtil.getString(obj, "area"));
             vo.setStatus(MongodbUtil.getInteger(obj, "status"));
             Long birthday = MongodbUtil.getLong(obj, "birthday");
             if(birthday!=null){
                 vo.setAge(DateUtil.calcAge(birthday));
             }
             
           
             Doctor userDoctor = new Doctor();
           //医生信息
             DBObject doctor = (BasicDBObject)obj.get("doctor");
             if(doctor!=null) {
             	userDoctor.setHospital(MongodbUtil.getString(doctor, "hospital"));
             	userDoctor.setDepartments(MongodbUtil.getString(doctor, "departments"));
             	userDoctor.setTitle(MongodbUtil.getString(doctor, "title"));
             	userDoctor.setDoctorNum(MongodbUtil.getString(doctor, "doctorNum"));
             	userDoctor.setCureNum(MongodbUtil.getInteger(doctor, "cureNum"));
             	userDoctor.setHospitalId(MongodbUtil.getString(doctor, "hospitalId"));
             	List<String> udlist = new ArrayList<String>();
             	Object eList = doctor.get("expertise");
     			if (eList != null) {
     				List<Object> oList = (BasicDBList) eList;
     				for (Object temp : oList) {
     					udlist.add(temp.toString());
     				}
     			}
     			userDoctor.setExpertise(udlist);
     			vo.setDoctor(userDoctor);
             }
             return vo;
    }

	@Override
	public PageVO searchDocByGIdOrSId(GroupSearchParam param) {
		PageVO page = new PageVO();
		
		DBObject query = new BasicDBObject();
		query.put("status", "C");
		if(StringUtil.isNotBlank(param.getDocGroupId())) {
			query.put("groupId", param.getDocGroupId());
			if(StringUtil.isNoneBlank(param.getDeptName())){
				query.put("deptName", param.getDeptName());
			}
		}
		List<GroupDoctor> groupDoctorList = dsForRW.createQuery(GroupDoctor.class, query).asList();
		
		List<Integer> doctorIds = new ArrayList<Integer>();
		// 在线doctorId
		List<Integer> online_doctorIds = new ArrayList<Integer>();
		for (GroupDoctor gdoc : groupDoctorList) {
			doctorIds.add(gdoc.getDoctorId());
			if (gdoc.getOnLineState() != null && gdoc.getOnLineState().equalsIgnoreCase("1")) {
				online_doctorIds.add(gdoc.getDoctorId());
			}
		}
		
		Query<User> q = userRepository.findUserQuery(doctorIds,null);
		List<User> list = q.offset(param.getPageIndex()*param.getPageSize()).limit(param.getPageSize()).asList();
		Collections.sort(list, new Comparator<User>() {
			@Override
			public int compare(User o1, User o2) {
				if (online_doctorIds.contains(o1.getUserId()) && !online_doctorIds.contains(o2.getUserId())) {
					return -1;
				} else if (!online_doctorIds.contains(o1.getUserId()) && online_doctorIds.contains(o2.getUserId())) {
					return 1;
				}
				return 0;
			}
		});
		
		page.setPageData(list);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(q.countAll());
		return page;
	}
	
	/**
	 * 根据职称排序
	 * @param list
	 */
	private void sort(List<User> list){
		if(list == null || list.size() == 0){
			return;
		}
		Collections.sort(list, new Comparator<User>() {
			public int compare(User arg0, User arg1) {
				
				if (arg1.getDoctor().getTitleRank() == null) {
					arg1.getDoctor().setTitleRank("0");
				}
				if (arg0.getDoctor().getTitleRank() == null) {
					arg0.getDoctor().setTitleRank("0");
				}


				Integer _arg1 = Integer.valueOf(arg1.getDoctor().getTitleRank());
				Integer _arg0 = Integer.valueOf(arg0.getDoctor().getTitleRank());

				return _arg0.compareTo(_arg1);
			}
		});
	}

	@Override
	public List<GroupSearchVO> findGroupFromEs(GroupSearchParam param) {
        List<GroupSearchVO> list = new ArrayList<GroupSearchVO>();
        List<String> groupIds = new ArrayList<String>();
        SearchParam searchParam = 
				new SearchParam.Builder(Constants.INDEX_HEALTH)
								.searchKey(param.getKeyword())
								.type(new String[]{Constants.TYPE_GROUP})
								.from(param.getStart())
								.size(param.getPageSize())
								.build();
        Map<String,List<String>> map_es = ElasticSearchFactory.getInstance().searchAndReturnBizId(searchParam);
        if(null==map_es) return null;
        logger.info("map_es:"+map_es.get("group"));
        int size = map_es.get("group").size();
        //将返回的集团id转化为 
        BasicDBList basicList = new BasicDBList();
        List<String> sortGroupIds = Lists.newArrayList();
        for (int i = 0; i < size; i++) {
        	basicList.add(new ObjectId(map_es.get("group").get(i).toString()));
        	sortGroupIds.add(map_es.get("group").get(i).toString());
		}
        List<Group> group = dsForRW.createQuery("c_group",Group.class).field("_id").in(basicList).asList();
        
        if (sortGroupIds != null && sortGroupIds.size() > 0) {
			for(String sortGroupId : sortGroupIds) {
				for (Group group2 : group) {
					if (org.apache.commons.lang3.StringUtils.equals(sortGroupId, group2.getId())) {
						GroupSearchVO vo = new GroupSearchVO();
			        	vo.setGroupId(group2.getId());
			            vo.setGroupName(group2.getName());
			            vo.setIntroduction(group2.getIntroduction());
			            vo.setCertPath(group2.getLogoUrl());//(UserHelper.buildCertPath(vo.getGroupId(), MongodbUtil.getInteger(obj, "creator")));
			            //查询该集团所有状态为c的医生，并统计其就诊量
			            vo.setCureNum(Integer.valueOf(getGroupCureNum(group2.getId())));
			            vo.setCertStatus(group2.getCertStatus());
			            //医生集团擅长病种
			            vo.setDisease(this.getDisease(group2.getDiseaselist()));    
			            groupIds.add(vo.getGroupId());
			            list.add(vo);
					}
				}
			}
		}
        
        //查找专家数
        Map<String,Integer> map = this.getGroupExperNum(groupIds,1);
        for(GroupSearchVO vo:list){
            Integer expertNum = map.get(vo.getGroupId());
            vo.setExpertNum(expertNum == null ? 0 : expertNum);
        }
        return list;
    }
	
	public int getGroupCureNum(String gorupId) {
  		List<GroupDoctor> groupDoctors = groupDoctorDao.findDoctorsByGroupId(gorupId);
  		int cureNum = 0;
  		if (groupDoctors!=null && groupDoctors.size() > 0) {
  			List<Integer> userIds = Lists.newArrayList();
  			groupDoctors.forEach((groupDoctor)->{
  				if (groupDoctor.getDoctorId() != null) {					
  					userIds.add(groupDoctor.getDoctorId());
  				}
  			});
  			List<User> users = userRepository.findUsersByIds(userIds);
  			if (users != null && users.size() > 0) {
  				for(User user : users){
  					Doctor doctor = user.getDoctor();
  					if (doctor != null && doctor.getCureNum()!=null) {
  						cureNum += doctor.getCureNum();
  					}
  				}
  			}
  		}
  		return cureNum;
	}

	@Override
	public List<User> searchDocByKeywordFomEsServer(GroupSearchParam param) {
        List<User> list = new ArrayList<User>();
        List<User> sortList = new ArrayList<User>();//排序后的list
        if(StringUtil.isNotBlank(param.getKeyword()) && StringUtil.isBlank(param.getDiseaseId())){
        	AbstractSearchParam searchParam = null;
        	if(StringUtil.isNotBlank(param.getGroupId())){
        		searchParam = new BoolSearchParam.Builder(Constants.INDEX_HEALTH)
        				 	.type(new String[]{Constants.TYPE_DOCTOR})
        				 	.addFilter("groupId", param.getGroupId())
        					.searchKey(param.getKeyword())
        					.from(param.getStart())
        					.size(param.getPageSize())
        					.build();
        	}else{
        		 searchParam = new SearchParam.Builder(Constants.INDEX_HEALTH)
							.type(new String[]{Constants.TYPE_DOCTOR})
							.searchKey(param.getKeyword())
							.from(param.getStart())
							.size(param.getPageSize())
							.build();
        	}
    				
            Map<String,List<String>> map_es = ElasticSearchFactory.getInstance().searchAndReturnBizId(searchParam);
            if(null==map_es)return null;
            List<Integer> list_id = new ArrayList<Integer>();
            for (String id : map_es.get("doctor")) {
				list_id.add(Integer.valueOf(id));
			}
            list = dsForRW.createQuery("user",User.class).field("_id").in(list_id).asList();
            //按照ES服务器返回的权重顺序重新进行排序 保持原有权重不变
            for (Integer ids : list_id) {
            	for (User listUser : list) {
            		if(ids.equals(listUser.getUserId())){
            			sortList.add(listUser);
            			break;
            		}
            	}
            }
        }
		return sortList;
	}

	@Override
	public PageVO searchDocByGIdOrSIdWithoutOwnerId(GroupSearchParam param, Integer groupOwnerId, List<Integer> myDoctorIds) {
		PageVO page = new PageVO();
		
		DBObject query = new BasicDBObject();
		query.put("status", "C");
		if(StringUtil.isNotBlank(param.getDocGroupId())) {
			query.put("groupId", param.getDocGroupId());
			if(StringUtil.isNoneBlank(param.getDeptName())){
				query.put("deptName", param.getDeptName());
			}
		}
		List<GroupDoctor> groupDoctorList = dsForRW.createQuery(GroupDoctor.class, query).asList();
		
		List<Integer> doctorIds = new ArrayList<Integer>();
		// 在线doctorId
		List<Integer> online_doctorIds = new ArrayList<Integer>();
		for (GroupDoctor gdoc : groupDoctorList) {
//			if (!gdoc.getDoctorId().equals(groupOwnerId)) {
				doctorIds.add(gdoc.getDoctorId());
				if (gdoc.getOnLineState() != null && gdoc.getOnLineState().equalsIgnoreCase("1")) {
					online_doctorIds.add(gdoc.getDoctorId());
				}
//			}
		}
		List<HospitalPO> hpos = baseDataDao.getHospitalsCodeNull(param.getAreaCode(), false);
		if (param.getAreaCode() != null && hpos.isEmpty()) {
			return new PageVO();
		}
		
		//取得所有医院的Id
		List<String> hospitalIds = new ArrayList<String>();
		for (HospitalPO hpo : hpos) {
			hospitalIds.add(hpo.getId());
		}
		
		Query<User> q = null;
		if (param.getAreaCode() == null) {			
			q = userRepository.getDoctorQuery(doctorIds, null, param.getSpecialistId()).order("-doctor.cureNum,doctor.titleRank");
		} else {			
			q = userRepository.getDoctorQuery(doctorIds, hospitalIds, param.getSpecialistId()).order("-doctor.cureNum,doctor.titleRank");
		}
		
		List<User> list = q.asList();
		List<User> resultList = Lists.newArrayList();
		
		//1、先取出所有就诊过的医生和非就诊过的 2、针对就诊过的医生，在内存中按照doctor.cureNum和doctor.titleRank排序 3、合并成一个list，然后再进行分页
		List<User> visited = Lists.newArrayList();
		List<User> unVisited = Lists.newArrayList();
		List<User> ownered = Lists.newArrayList();
		
		if (list != null && list.size() > 0) {
			for(User user : list) {
				if(groupOwnerId!=null && user.getUserId().intValue() == groupOwnerId.intValue()) {
					ownered.add(user);	
				} else if (myDoctorIds != null && myDoctorIds.contains(user.getUserId())) {
					visited.add(user);
				} else {
					unVisited.add(user);
				}
			}			
			
			List<User> all = Lists.newArrayList();
			all.addAll(ownered);
			all.addAll(visited);
			all.addAll(unVisited);
			
			if (all != null) {
				//从list中的第几个元素开始取
				int start = param.getPageIndex() < 0 ? 0 : (param.getPageIndex() * param.getPageSize());
				//取到第几个元素
				int end = start + param.getPageSize() - 1;
				for (int i = start; i <= end; i++) {
					if (start > (all.size()-1)) {
						//当起始位大于all最大的一个元素，则什么都不做
					} else {
						if (end > ((all.size()-1))) {
							//若要取得结束的元素位置，大于all的最大长度,则取值取到all的最大元素即可
							if (i <= (all.size()-1)) {
								resultList.add(all.get(i));
							} else {
								//当i大于all的最大长度时，则什么都不做
							}
						} else {
							resultList.add(all.get(i));
						}
					}
				}
			}
			
		}
		
		
		//List<User> list = q.offset(param.getPageIndex()*param.getPageSize()).limit(param.getPageSize()).asList();
		
		page.setPageData(resultList);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(q.countAll());
		return page;
	}
	
	public PageVO searchDocByGIdOrSIdWithoutOwnerIdAll(GroupSearchParam param, Integer groupOwnerId, List<Integer> myDoctorIds) {
		PageVO page = new PageVO();
		
		DBObject query = new BasicDBObject();
		query.put("status", "C");
		if(StringUtil.isNotBlank(param.getDocGroupId())) {
			query.put("groupId", param.getDocGroupId());
			if(StringUtil.isNoneBlank(param.getDeptName())){
				query.put("deptName", param.getDeptName());
			}
		}
		List<GroupDoctor> groupDoctorList = dsForRW.createQuery(GroupDoctor.class, query).asList();
		
		List<Integer> doctorIds = new ArrayList<Integer>();
		// 在线doctorId
		List<Integer> online_doctorIds = new ArrayList<Integer>();
		for (GroupDoctor gdoc : groupDoctorList) {
//			if (!gdoc.getDoctorId().equals(groupOwnerId)) {
				doctorIds.add(gdoc.getDoctorId());
				if (gdoc.getOnLineState() != null && gdoc.getOnLineState().equalsIgnoreCase("1")) {
					online_doctorIds.add(gdoc.getDoctorId());
				}
//			}
		}
		List<HospitalPO> hpos = baseDataDao.getHospitalsCodeNull(param.getAreaCode(), false);
		if (param.getAreaCode() != null && hpos.isEmpty()) {
			return new PageVO();
		}
		
		//取得所有医院的Id
		List<String> hospitalIds = new ArrayList<String>();
		for (HospitalPO hpo : hpos) {
			hospitalIds.add(hpo.getId());
		}
		
		Query<User> q = null;
		if (param.getAreaCode() == null) {
			q = userRepository.getDoctorQuery(doctorIds, null, param.getSpecialistId()).order("-doctor.cureNum,doctor.titleRank");
		} else {			
			q = userRepository.getDoctorQuery(doctorIds, hospitalIds, param.getSpecialistId()).order("-doctor.cureNum,doctor.titleRank");
		}
		List<User> list = q.asList();
		
		//1、先取出所有就诊过的医生和非就诊过的 2、针对就诊过的医生，在内存中按照doctor.cureNum和doctor.titleRank排序 3、合并成一个list，然后再进行分页
		List<User> visited = Lists.newArrayList();
		List<User> unVisited = Lists.newArrayList();
		List<User> ownered = Lists.newArrayList();
		List<User> all = Lists.newArrayList();
		
		if (list != null && list.size() > 0) {
			for(User user : list) {
				if(groupOwnerId!=null && user.getUserId().intValue() == groupOwnerId.intValue()) {
					ownered.add(user);	
				} else if (myDoctorIds != null && myDoctorIds.contains(user.getUserId())) {
					visited.add(user);
				} else {
					unVisited.add(user);
				}
			}			
			
			
			all.addAll(ownered);
			all.addAll(visited);
			all.addAll(unVisited);	
		}
		
		page.setPageData(all);
		page.setPageIndex(param.getPageIndex());
		page.setPageSize(param.getPageSize());
		page.setTotal(q.countAll());
		return page;
	}
}