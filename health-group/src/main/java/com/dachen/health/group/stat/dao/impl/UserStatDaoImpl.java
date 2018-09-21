package com.dachen.health.group.stat.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.entity.vo.DiseaseTypeVO;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.stat.dao.IUserStatDao;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * ProjectName： health-group<br>
 * ClassName： UserStatDaoImpl<br>
 * Description： 用户统计dao实现类<br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
@Repository
public class UserStatDaoImpl extends NoSqlRepository implements IUserStatDao {

    @Autowired
    protected IBaseUserDao baseUserDao;
    
    @Autowired
    protected IBaseDataService baseDataService;
    
    /**
     * </p>统计医生职称</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public List<StatVO> statTitle(StatParam param){
        List<Integer> userIds = getAllDoctors(param.getGroupId(),param.isShowOnJob());
        
        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * db.c_group_doctor.aggregate([ <br>
         *  { $match: { "_id":{$in:[216,542,550,540,410,545,512]}} }, <br>
         *  { $project: {_id: 0, doctor.check.title: 1} }, <br>
         *  { $group: { _id:"$doctor.check.title", value : { $sum : 1 }}}, <br>
         *  { $sort : { value : -1} }, <br>
         * ]) <br>
         * 2.返回结果如下 { "_id" : "主任医生", "value" : 15 }
         */
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("_id", new BasicDBObject("$in",userIds));
        DBObject match = new BasicDBObject("$match", matchFields);

        // 返回字段
        BasicDBObject projectFields = new BasicDBObject();
        projectFields.put("_id", 0);
        projectFields.put("doctor.check.title", 1);
        DBObject project = new BasicDBObject("$project", projectFields);
        
        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$doctor.check.title");
        groupFields.put("value", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        // 排序条件
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("value",-1));
        
        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(group);
        pipeline.add(sort);
        
        List<StatVO> list = new ArrayList<StatVO>();
        
        DBCollection collection = dsForRW.getDB().getCollection("user");
        
        AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        
        List<StatVO> tempList = new ArrayList<StatVO>();
        while (it.hasNext()) {
            DBObject obj = it.next();

            StatVO vo = new StatVO();
            vo.setName(MongodbUtil.getString(obj, "_id"));
            vo.setValue(MongodbUtil.getInteger(obj, "value")); 
            //id 为空放到最后
            if (StringUtil.isEmpty(vo.getName())) {
            	tempList.add(vo);
            	continue;
            }
            list.add(vo);
        }
        list.addAll(tempList);
        return list;
    }

    /**
     * </p>统计医生分布区域</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public List<StatVO> statDoctorArea(StatParam param){
        List<Integer> userIds = getAllDoctors(param.getGroupId(),param.isShowOnJob());
        
        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * db.c_group_doctor.aggregate([ <br>
         *  { $match: { "_id":{$in:[216,542,550,540,410,545,512]}} }, <br>
         *  { $project: {_id: 0, doctor: 1} }, <br>
         *  { $group: { "_id" : "$doctor.provinceId" ,"area":{"$first":"$doctor.province"}, value : { $sum : 1 }}}, <br>
         *  { $sort : { value : -1} }, <br>
         * ]) <br>
         * 2.返回结果如下 { "_id" : "主任医生", "value" : 15 }
         */
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("_id", new BasicDBObject("$in",userIds));
        
        //查询省市县
        if(param.getAreaId()!=null && param.getAreaId()!=0){
            if(param.getAreaId()%10000 == 0){
                //查询省
                matchFields.put("doctor.provinceId", param.getAreaId());
            }else if(param.getAreaId()%100 == 0){
                //查询市
                matchFields.put("doctor.cityId", param.getAreaId());
            }
        }
        
        DBObject match = new BasicDBObject("$match", matchFields);

        // 返回字段
        BasicDBObject projectFields = new BasicDBObject();
        projectFields.put("_id", 0);
        projectFields.put("doctor", 1);
        DBObject project = new BasicDBObject("$project", projectFields);
        
        // 分组条件
        DBObject groupFields = new BasicDBObject();
        //查询省市县
        if(param.getAreaId()!=null){
            if(param.getAreaId()%10000 == 0){
                //查询省
                groupFields.put("_id", "$doctor.cityId");
                groupFields.put("area", new BasicDBObject("$first","$doctor.city"));
            }else if(param.getAreaId()%100 == 0){
                //查询市
                groupFields.put("_id", "$doctor.countryId");
                groupFields.put("area", new BasicDBObject("$first","$doctor.country"));
            }
        }else{
            //查询全国
            groupFields.put("_id", "$doctor.provinceId");
            groupFields.put("area", new BasicDBObject("$first","$doctor.province"));
        }
        
        groupFields.put("value", new BasicDBObject("$sum", 1));
        DBObject group = new BasicDBObject("$group", groupFields);
        
        // 排序条件
        DBObject sort = new BasicDBObject("$sort", new BasicDBObject("value",-1));
        
        List<DBObject> pipeline = new ArrayList<DBObject>();
        pipeline.add(match);
        pipeline.add(project);
        pipeline.add(group);
        pipeline.add(sort);
        
        List<StatVO> list = new ArrayList<StatVO>();
        
        DBCollection collection = dsForRW.getDB().getCollection("user");
        
        AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        
        List<StatVO> tempList = new ArrayList<StatVO>();
        while (it.hasNext()) {
            DBObject obj = it.next();

            StatVO vo = new StatVO();
            vo.setId(MongodbUtil.getInteger(obj, "_id"));
            vo.setName(MongodbUtil.getString(obj, "area"));
            vo.setValue(MongodbUtil.getInteger(obj, "value")); 
            //id 为空放到最后
            if (vo.getId() == null) {
            	tempList.add(vo);
            	continue;
            }
            list.add(vo);
        }
        list.addAll(tempList);
        return list;
    }
    
    private List<Integer> getAllDoctors(String groupId,boolean isShowOnJob) {
		//查找集团下所有医生
		List<Integer> userIds = baseUserDao.getDoctorIdByGroup(groupId,
					isShowOnJob?new String[] { GroupEnum.GroupDoctorStatus.正在使用.getIndex()} 
							   : new String[] {
										GroupEnum.GroupDoctorStatus.正在使用.getIndex(), 
										GroupEnum.GroupDoctorStatus.离职.getIndex(),
										GroupEnum.GroupDoctorStatus.踢出.getIndex() 
								});
		return userIds;
	}
    
    /**
     * </p>统计医生分布病种</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public List<StatVO> statDoctorDisease(StatParam param){
        List<StatVO> list = new ArrayList<StatVO>();
        
    	List<DiseaseTypeVO> diseaseTypes = baseDataService.getDiseaseByParent(param.getDiseaseId());
    	for (DiseaseTypeVO diseaseType : diseaseTypes) {
    		long count = getDoctorQuery(param.getGroupId(), 1, diseaseType.getId(),param.isShowOnJob()).countAll();
    		if (count == 0)
    			continue;
    		StatVO vo = new StatVO();
        	vo.setDiseaseId(diseaseType.getId());
        	vo.setName(diseaseType.getName());
        	vo.setValue((int)count);
        	list.add(vo);
    	}
        
        Collections.sort(list, new Comparator<StatVO>() {
			@Override
			public int compare(StatVO o1, StatVO o2) {
				if (o1 == null || o2 == null)
					return 0;
				return o1.getValue() > o2.getValue() ? -1 : 1;
			}
		});
        
        return list;
    }
   
    private Query<User> getDoctorQuery(String groupId, Integer type, String typeId,boolean isShowOnJob) {
    	Query<User> q = dsForRW.createQuery("user", User.class).filter("_id in", getAllDoctors(groupId,isShowOnJob));
    	if (1 == type) {//病种统计
    		Pattern pattern = Pattern.compile("^.*" + typeId + ".*$");
    		q.filter("doctor.expertise", pattern);
    	} else if (2 == type) {//职称统计
    		if (StringUtil.isBlank(typeId)) {
    			q.or(q.criteria("doctor.check.title").doesNotExist(),
       				 q.criteria("doctor.check.title").equal(null));
    		} else {
    			q.filter("doctor.check.title", typeId);
    		}
    	} else if (3 == type) {//区域统计
    		if (StringUtil.isBlank(typeId)) {
    			q.or(q.criteria("doctor.provinceId").doesNotExist(),
    				 q.criteria("doctor.provinceId").equal(null));
    		} else {
    			Integer areaId = Integer.valueOf(typeId);
    			if (areaId % 10000 == 0) {//省
    				q.filter("doctor.provinceId", areaId);
    			} else if (areaId % 100 == 0) {//市
    				q.filter("doctor.cityId", areaId);
    			} else {//区
    				q.filter("doctor.countryId", areaId);
    			}
    		}
    	}
    	return q;
    }
    
    @Override
    public PageVO statDoctor(StatParam param) {
    	Query<User> q = getDoctorQuery(param.getGroupId(), param.getType(), param.getTypeId(),param.isShowOnJob());
    	
    	List<User> users = q.offset(param.getStart()).limit(param.getPageSize()).asList();
    	List<Integer> userIds = Lists.newArrayList();
    	
    	if (users != null && users.size() > 0) {
    		for(User user : users) {
        		userIds.add(user.getUserId());
        	}
        	Query<GroupDoctor> groupDoctorQuery = dsForRW.createQuery("c_group_doctor", GroupDoctor.class).filter("doctorId in", userIds).filter("groupId", param.getGroupId());
        	List<GroupDoctor> groupDoctors = groupDoctorQuery.asList();
        	for (User user : users) {
    			for (GroupDoctor groupDoctor : groupDoctors) {
    				if (user.getUserId().equals(groupDoctor.getDoctorId())) {
    					user.setRemarks(groupDoctor.getRemarks());
    				}
    			}
    		}
        	return new PageVO(convert(users), q.countAll(), param.getPageIndex(), param.getPageSize());
		} else {
			return new PageVO(new ArrayList<>(), q.countAll() , param.getPageIndex(), param.getPageSize());
		}
    	
    	
    }
    
    /**
     * DBCursor转换成BaseUserVO
     * @return
     */
    private List<BaseUserVO> convert(List<User> users) {
    	List<BaseUserVO> vos = new ArrayList<BaseUserVO>();
    	for (User user : users) {
    		BaseUserVO vo = new BaseUserVO();
    		vo.setUserId(user.getUserId());
    		vo.setName(user.getName());
    		vo.setAge(user.getAge());
    		vo.setSex(user.getSex());
    		vo.setTelephone(user.getTelephone());
    		vo.setHeadPicFileName(user.getHeadPicFileName());
    		vo.setRemarks(user.getRemarks());
			vo.setTitle(user.getDoctor() != null ? user.getDoctor().getTitle() : "");
			vo.setDoctorNum(user.getDoctor() != null ? user.getDoctor().getDoctorNum() : null);
    		vos.add(vo);
    	}
    	return vos;
    }
    
    /**
     * </p>统计集团患者</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO statPatientByDoctor(StatParam param,List<Integer> doctorIds){
        
        //查找医生邀请的患者
        DBObject query = new BasicDBObject();
        query.put("userId", new BasicDBObject("$in",doctorIds));
        
        DBObject project = new BasicDBObject();
        project.put("_id", 0);
        project.put("toUserId", 1);
        project.put("createTime", 1);
        
        DBObject sort = new BasicDBObject();
        sort.put("createTime", -1);
        
        DBCollection collection = dsForRW.getDB().getCollection("u_doctor_patient");
        
        List<StatVO> list = new ArrayList<StatVO>();
        List<Integer> patientIds = new ArrayList<Integer>();
        
        DBCursor cursor = collection.find(query,project).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            
            Integer patientId = MongodbUtil.getInteger(obj, "toUserId");
            //查询集团下的患者时，存在A、B两位医生都和患者C有好友关系，此时会查出了两条记录，需去重 add by xieping
            if (patientIds.contains(patientId))
            	continue;
            
            StatVO vo = new StatVO();
            vo.setId(patientId);
            vo.setTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "createTime"), null));

            patientIds.add(vo.getId());

            list.add(vo);
        }
        
        //查询患者姓名
        if(patientIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(patientIds.toArray(new Integer[]{}));
            for(StatVO stat:list){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setAge(user.getAge());
                        stat.setSex(user.getSex());
                        stat.setTelephone(user.getTelephone());
                        stat.setHeadPicFileName(user.getHeadPicFileName());
                        break;
                    }
                }
            }
            
        }
        
        //构造分页
        PageVO page = new PageVO();
        page.setPageData(list);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(collection.count(query));
        
        return page;
    }
    
}
