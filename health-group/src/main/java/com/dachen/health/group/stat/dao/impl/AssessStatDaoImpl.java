package com.dachen.health.group.stat.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.dao.IBaseUserDao;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.helper.UserHelper;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserStatus;
import com.dachen.health.group.stat.dao.IAssessStatDao;
import com.dachen.health.group.stat.entity.param.StatParam;
import com.dachen.health.group.stat.entity.vo.StatVO;
import com.dachen.util.DateUtil;
import com.dachen.util.MongodbUtil;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * ProjectName： health-group<br>
 * ClassName： AssessStatDaoImpl<br>
 * Description： 考核统计dao实现类<br>
 * 
 * @author fanp
 * @createTime 2015年9月17日
 * @version 1.0.0
 */
@Repository
public class AssessStatDaoImpl extends NoSqlRepository implements IAssessStatDao {

    @Autowired
    protected IBaseUserDao baseUserDao;
    
    /**
     * </p>统计邀请医生数</p>
     * 
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO inviteDoctorByGroup(StatParam param) {
        //查找集团所有医生
        List<Integer> doctorIds = baseUserDao.getDoctorIdByGroup(param.getGroupId(), param.getStatuses(),param.getKeyword());
        
        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * db.c_group_doctor.aggregate([ <br>
         *  { $match: { "groupId":"55d588fccb827c15fc9d47b3"} }, <br>
         *  { $project: {_id: 0, doctorId: 1,referenceId:1 } }, <br>
         *  { $group: { _id:"$referenceId", value : { $sum : 1 }}}, <br>
         *  { $sort : { value : -1} }, <br>
         *  { $limit : 10}, <br>
         *  { $skip : 10} <br>
         * ]) <br>
         * 2.返回结果如下 { "_id" : 216, "count" : 15 }
         */
        
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("groupId", param.getGroupId());
        matchFields.put("status", new BasicDBObject("$in",param.getStatuses()));
        matchFields.put("referenceId", new BasicDBObject("$in",doctorIds));
        
        //时间查询条件
        if(param.getStartTime()!=null || param.getEndTime()!=null){
            DBObject matchTime = new BasicDBObject();
            if(param.getStartTime()!=null){
                matchTime.put("$gte", param.getStartTime());
            }
            if(param.getEndTime()!=null){
                matchTime.put("$lte", param.getEndTime());
            }
            matchFields.put("creatorDate", matchTime);
        }
        
        DBObject match = new BasicDBObject("$match", matchFields);

        // 返回字段
        BasicDBObject projectFields = new BasicDBObject();
        projectFields.put("_id", 0);
        projectFields.put("doctorId", 1);
        projectFields.put("referenceId", 1);
        DBObject project = new BasicDBObject("$project", projectFields);
        
        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$referenceId");
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
        
        DBCollection collection = dsForRW.getDB().getCollection("c_group_doctor");
        
        AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject obj = it.next();

            StatVO vo = new StatVO();
            vo.setId(MongodbUtil.getInteger(obj, "_id"));
            vo.setValue(MongodbUtil.getInteger(obj, "value")); 
            if(vo.getId()!=0)
            {
            list.add(vo);
            }
        }
        
        //手动构造分页
        this.buildPage(doctorIds, list);
        List<Integer> userIds = new ArrayList<Integer>();//需返回的数据的id
        List<StatVO> newList = new ArrayList<StatVO>();//需返回的数据
        
        int start = param.getStart();
        int end = param.getPageSize()+param.getStart();
        if(start<=end){
            if(end>list.size()){
                end = list.size();
            }
            for(int i = start;i < end;i++){
                newList.add(list.get(i));
                userIds.add(list.get(i).getId());
            }
        }

        //查询医生姓名
        if(userIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(userIds.toArray(new Integer[]{}));
            for(StatVO stat:newList){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setHospital(user.getHospital());
                        stat.setDepartments(user.getDepartments());
                        stat.setTitle(user.getTitle());
                        stat.setHeadPicFileName(UserHelper.buildHeaderPicPath(user.getHeadPicFileName(),stat.getId()));
                        break;
                    }
                }
            }
        }
        
        //构造分页
        PageVO page = new PageVO();
        page.setPageData(newList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.valueOf(doctorIds.size()));   
        
        return page;
        
    }

    /**
     * </p>统计医生邀请医生数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO inviteDoctorByDoctor(StatParam param){
        DBObject query = new BasicDBObject();
        query.put("groupId", param.getGroupId());
        query.put("referenceId", param.getDoctorId());
        query.put("status", new BasicDBObject("$in",param.getStatuses()));
        
        //时间查询条件
        if(param.getStartTime()!=null || param.getEndTime()!=null){
            DBObject matchTime = new BasicDBObject();
            if(param.getStartTime()!=null){
                matchTime.put("$gte", param.getStartTime());
            }
            if(param.getEndTime()!=null){
                matchTime.put("$lte", param.getEndTime());
            }
            query.put("creatorDate", matchTime);
        }
        
        DBObject project = new BasicDBObject();
        project.put("_id", 0);
        project.put("doctorId", 1);
        project.put("creatorDate", 1);
        
        DBObject sort = new BasicDBObject();
        sort.put("creatorDate", -1);
        
        DBCollection collection = dsForRW.getDB().getCollection("c_group_doctor");
        
        List<StatVO> list = new ArrayList<StatVO>();
        List<Integer> userIds = new ArrayList<Integer>();
        
        //查询邀请的医生
        DBCursor cursor = collection.find(query,project).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            
            StatVO vo = new StatVO();
            vo.setId(MongodbUtil.getInteger(obj, "doctorId"));
            vo.setTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "creatorDate"), null));

            userIds.add(vo.getId());

            list.add(vo);
        }
        
        //查询医生姓名
        if(userIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(userIds.toArray(new Integer[]{}));
            for(StatVO stat:list){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setTelephone(user.getTelephone());
                        stat.setHospital(user.getHospital());
                        stat.setDepartments(user.getDepartments());
                        stat.setTitle(user.getTitle());
                        stat.setHeadPicFileName(UserHelper.buildHeaderPicPath(user.getHeadPicFileName(),stat.getId()));
                        stat.setStatus(user.getStatus());// 状态
                        UserStatus userStatus = UserEnum.UserStatus.getEnum(user.getStatus());
						stat.setStatusName(userStatus != null ? userStatus.getTitle() : ""); // 状态名称
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
    
    /**
     * </p>统计集团添加患者数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO addPatientByGroup(StatParam param){
        //查找集团所有医生
        List<Integer> doctorIds = baseUserDao.getDoctorIdByGroup(param.getGroupId(), param.getStatuses(),param.getKeyword());
        
        
      //查询所有医生相关的患者id
    	List<Integer> patientIdList = baseUserDao.getUserPatientIdByDoctorIds(doctorIds);
    	//再过滤掉患者id中的未激活患者
    	patientIdList = baseUserDao.filterInactivePatientIds(patientIdList);
        
        //查找邀请患者
        /**
         * 1.构造pipeline,最终pipeline如下 <br>
         * db.u_doctor_patient.aggregate([ <br>
         *  { $match: { "userId":{$in:[216,542,550,540,410,545,512]}} }, <br>
         *  { $project: {_id: 0, userId: 1} }, <br>
         *  { $group: { _id:"$userId", value : { $sum : 1 }}}, <br>
         *  { $sort : { value : -1} }, <br>
         *  { $limit : 10}, <br>
         *  { $skip : 10} <br>
         * ]) <br>
         * 2.返回结果如下 { "_id" : 216, "value" : 15 }
         */
        
        // 匹配条件
        DBObject matchFields = new BasicDBObject();
        matchFields.put("userId", new BasicDBObject("$in",doctorIds));
        matchFields.put("toUserId", new BasicDBObject("$in",patientIdList));
        DBObject match = new BasicDBObject("$match", matchFields);

        // 返回字段
        BasicDBObject projectFields = new BasicDBObject();
        projectFields.put("_id", 0);
        projectFields.put("userId", 1);
        DBObject project = new BasicDBObject("$project", projectFields);
        
        // 分组条件
        DBObject groupFields = new BasicDBObject("_id", "$userId");
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
        
        DBCollection collection = dsForRW.getDB().getCollection("u_doctor_patient");
        
        AggregationOutput output = collection.aggregate(pipeline);

        Iterator<DBObject> it = output.results().iterator();
        while (it.hasNext()) {
            DBObject obj = it.next();

            StatVO vo = new StatVO();
            vo.setId(MongodbUtil.getInteger(obj, "_id"));
            vo.setValue(MongodbUtil.getInteger(obj, "value")); 
            
            list.add(vo);
        }
        
        //手动构造分页
        this.buildPage(doctorIds, list);
        List<Integer> userIds = new ArrayList<Integer>();//需返回的数据的id
        List<StatVO> newList = new ArrayList<StatVO>();//需返回的数据
        
        int start = param.getStart();
        int end = param.getPageSize()+param.getStart();
        if(start<=end){
            if(end>list.size()){
                end = list.size();
            }
            for(int i = start;i < end;i++){
                newList.add(list.get(i));
                userIds.add(list.get(i).getId());
            }
        }

        //查询医生姓名
        if(userIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(userIds.toArray(new Integer[]{}));
            for(StatVO stat:newList){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setHospital(user.getHospital());
                        stat.setDepartments(user.getDepartments());
                        stat.setTitle(user.getTitle());
                        stat.setAge(user.getAge());
                        stat.setAgeStr(user.getAgeStr());
                        stat.setSex(user.getSex());
                        stat.setHeadPicFileName(user.getHeadPicFileName());
                        break;
                    }
                }
            }
            
        }
        
        //构造分页
        PageVO page = new PageVO();
        page.setPageData(newList);
        page.setPageIndex(param.getPageIndex());
        page.setPageSize(param.getPageSize());
        page.setTotal(Long.valueOf(doctorIds.size())); 
        
        return page;
    }
    
    /**
     * </p>统计集团医生添加患者数</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月17日
     */
    public PageVO addPatientByDoctor(StatParam param){
    	//先查询该医生相关的患者id
    	List<Integer> patientIdList = baseUserDao.getUserPatientIdByDoctorId(param.getDoctorId() );
    	//再过滤掉患者id中的未激活患者
    	patientIdList = baseUserDao.filterInactivePatientIds(patientIdList);
 
        DBObject query = new BasicDBObject();
        query.put("userId", param.getDoctorId());
        query.put("toUserId", new BasicDBObject("$in",patientIdList));
        
        
        //时间查询条件
        if(param.getStartTime()!=null || param.getEndTime()!=null){
            DBObject matchTime = new BasicDBObject();
            if(param.getStartTime()!=null){
                matchTime.put("$gte", param.getStartTime());
            }
            if(param.getEndTime()!=null){
                matchTime.put("$lte", param.getEndTime());
            }
            query.put("createTime", matchTime);
        }
        
        DBObject project = new BasicDBObject();
        project.put("_id", 0);
        project.put("toUserId", 1);
        project.put("createTime", 1);
        
        DBObject sort = new BasicDBObject();
        sort.put("createTime", -1);
        
        DBCollection collection = dsForRW.getDB().getCollection("u_doctor_patient");
        
        List<StatVO> list = new ArrayList<StatVO>();
        List<Integer> userIds = new ArrayList<Integer>();
        
        DBCursor cursor = collection.find(query,project).sort(sort).skip(param.getStart()).limit(param.getPageSize());
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            
            StatVO vo = new StatVO();
            vo.setId(MongodbUtil.getInteger(obj, "toUserId"));
            vo.setTime(DateUtil.formatDate2Str(MongodbUtil.getLong(obj, "createTime"), null));

            userIds.add(vo.getId());

            list.add(vo);
        }
        
        //查询医生姓名
        if(userIds.size()>0){
            List<BaseUserVO> userList = baseUserDao.getByIds(userIds.toArray(new Integer[]{}));
            for(StatVO stat:list){
                for(BaseUserVO user:userList){
                    if(stat.getId().equals(user.getUserId())){
                        stat.setName(user.getName());
                        stat.setTelephone(user.getTelephone());
                        stat.setSex(user.getSex());
                        stat.setAge(user.getAge());
                        stat.setAgeStr(user.getAgeStr());
                        stat.setHeadPicFileName(user.getHeadPicFileName());
                        stat.setStatus(user.getStatus());// 状态
                        UserStatus userStatus = UserEnum.UserStatus.getEnum(user.getStatus());
						stat.setStatusName(userStatus != null ? userStatus.getTitle() : ""); // 状态名称
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

    /**
     * </p>手动构造分页</p>
     * @param doctorIds 所有医生id
     * @param list 待构造的分页
     * @author fanp
     * @date 2015年9月23日
     */
    private void buildPage(List<Integer> doctorIds,List<StatVO> list){
        List<Integer> listIds = new ArrayList<Integer>();
        List<StatVO> remainList = new ArrayList<StatVO>();
        for(StatVO vo : list){
            listIds.add(vo.getId());
        }
        //查找出list中不存在的id
        for(Integer ids:doctorIds){
            int place = this.binSearch(listIds, ids);
            if(place == -1){
                StatVO vo = new StatVO();
                vo.setId(ids);
                vo.setValue(0);
                remainList.add(vo);
                
            }
        }
        list.addAll(remainList);
    }
    
    // 二分法查找id所在位置，list经过排序
    private int binSearch(List<Integer> listIds, int ids) {
        Integer[] sortIds = listIds.toArray(new Integer[]{});
        Arrays.sort(sortIds);
        
        int mid = sortIds.length / 2;

        int start = 0;
        int end = sortIds.length - 1;
        while (start <= end) {
            mid = (end - start) / 2 + start;
            Integer id = sortIds[mid];
            if (ids == id) {
                return mid;
            } else if (ids <= id) {
                end = mid - 1;
            } else if (ids >= id) {
                start = mid + 1;
            }
        }
        return -1;
    }

	@Override
	public DBCollection getDBCollection(String collectionName) {
		return dsForRW.getDB().getCollection(collectionName);
	}
    
}
