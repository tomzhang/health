package com.dachen.health.group.schedule.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.schedule.dao.IOnlineDao;
import com.dachen.health.group.schedule.entity.param.OnlineClinicDate;
import com.dachen.health.group.schedule.entity.param.OnlineParam;
import com.dachen.health.group.schedule.entity.po.Online;
import com.dachen.health.group.schedule.entity.po.OnlineDoctorInfo;
import com.dachen.health.group.schedule.entity.vo.OnlineVO;
import com.dachen.util.MongodbUtil;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class OnlineDaoImpl extends NoSqlRepository implements IOnlineDao {

    @Autowired
    protected IGroupDao groupDao;
    
    /**
     * </p>添加门诊信息,通过更新来判断存不存在</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月11日
     */
    public void add(Online po) {
        List<OnlineDoctorInfo> list = po.getDoctors();
        if(list == null || list.size()==0){
            return;
        }
        Integer[] ids = new Integer[list.size()];
        for(int i = 0,j=list.size();i<j;i++){
            ids[i] = list.get(i).getDoctorId();
        }
        
        //先删除再添加
        DBCollection collection = dsForRW.getDB().getCollection("c_online");
        
        DBObject query = new BasicDBObject();
        query.put("groupId", po.getGroupId());
        query.put("week", po.getWeek());
        query.put("period", po.getPeriod());
        
        DBObject pull = new BasicDBObject();
        pull.put("$pull", new BasicDBObject("doctors",new BasicDBObject("doctorId",new BasicDBObject("$in",ids))));
        
        collection.update(query, pull,false ,true);

        query.put("departmentId", po.getDepartmentId());
        //添加
        DBObject set = new BasicDBObject();
        set.put("department", po.getDepartment());
        
        DBObject[] objArra = new BasicDBObject[list.size()];
        for(int i = 0,j=list.size();i<j;i++){
            DBObject pushList = new BasicDBObject();
            pushList.put("doctorId", list.get(i).getDoctorId());
            pushList.put("doctorName", list.get(i).getDoctorName());
            pushList.put("startTime", list.get(i).getStartTime());
            pushList.put("endTime", list.get(i).getEndTime());
            
            objArra[i] = pushList;
        }
        
        DBObject update = new BasicDBObject();
        
        update.put("$addToSet", new BasicDBObject("doctors",new BasicDBObject("$each",objArra)));
        update.put("$set", set);
        
        collection.update(query, update, true, true);
    }

    /**
     * </p>按科室查找在线值班时间表</p>
     * 
     * @param doctorId
     * @return List<OfflineVO> Set<hospital>
     * @author fanp
     * @date 2015年8月11日
     */
    public OnlineVO getAllByDept(List<String> deptIds) {
        DBObject query = new BasicDBObject();
        query.put("departmentId", new BasicDBObject("$in",deptIds.toArray()));

        DBObject sort = new BasicDBObject();
        sort.put("week", 1);
        sort.put("period", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_online").find(query).sort(sort);

        List<OnlineClinicDate> list = new ArrayList<OnlineClinicDate>();
        OnlineVO onlineVO = new OnlineVO();
        int week=0,period=0;
        
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            OnlineClinicDate vo = new OnlineClinicDate();
            
            vo.setWeek(MongodbUtil.getInteger(obj, "week"));
            vo.setPeriod(MongodbUtil.getInteger(obj, "period"));

            BasicDBList dbList = (BasicDBList) obj.get("doctors");
            
            List<OnlineDoctorInfo> doctors = new ArrayList<OnlineDoctorInfo>();
            if(dbList.size()>0){
                for(int m = 0,n=dbList.size();m<n;m++){
                    DBObject dobj = (BasicDBObject)dbList.get(m);
                    
                    OnlineDoctorInfo info =new OnlineDoctorInfo();
                    info.setDoctorId(MongodbUtil.getInteger(dobj, "doctorId"));
                    info.setDoctorName(MongodbUtil.getString(dobj, "doctorName"));
                    info.setStartTime(MongodbUtil.getString(dobj, "startTime"));
                    info.setEndTime(MongodbUtil.getString(dobj, "endTime"));
                    
                    doctors.add(info);
                }
            }
            
            if(vo.getWeek() == week && vo.getPeriod()==period){
                list.get(list.size()-1).getDoctors().addAll(doctors);
                continue;
            }else{
                week=vo.getWeek();
                period=vo.getPeriod();
            }
            
            vo.setDoctors(doctors);
            
            list.add(vo);
        }
        onlineVO.setClinicDate(list);
        return onlineVO;
    }
    
    /**
     * </p>查找分配的医生</p>
     * @param doctorIds
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年8月26日
     */
    public List<OnlineClinicDate> getOnlineByDoctorIds(List<Integer> doctorIds,String groupId){
        DBObject query = new BasicDBObject();
        query.put("groupId",groupId);

        DBObject project = new BasicDBObject();
        project.put("_id", 0);
        project.put("week", 1);
        project.put("period", 1);
        project.put("doctors", 1);
        
        DBObject sort = new BasicDBObject();
        sort.put("week", 1);
        sort.put("period", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_online").find(query,project).sort(sort);
        List<OnlineClinicDate> list = new ArrayList<OnlineClinicDate>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            OnlineClinicDate vo = new OnlineClinicDate();
            
            vo.setWeek(MongodbUtil.getInteger(obj, "week"));
            vo.setPeriod(MongodbUtil.getInteger(obj, "period"));

            BasicDBList dbList = (BasicDBList) obj.get("doctors");
            
            List<OnlineDoctorInfo> doctors = new ArrayList<OnlineDoctorInfo>();
            if(dbList.size()>0){
                for(int m = 0,n=dbList.size();m<n;m++){
                    DBObject dobj = (BasicDBObject)dbList.get(m);
                    
                    Integer doctorId = MongodbUtil.getInteger(dobj, "doctorId");
                    //判断是否包含doctorIds
                    if(doctorIds.contains(doctorId)){
                        OnlineDoctorInfo info =new OnlineDoctorInfo();
                        info.setDoctorId(doctorId);
                        info.setDoctorName(MongodbUtil.getString(dobj, "doctorName"));
                        info.setStartTime(MongodbUtil.getString(dobj, "startTime"));
                        info.setEndTime(MongodbUtil.getString(dobj, "endTime"));
                        
                        doctors.add(info);
                    }
                }
            }
            
            if(doctors.size()>0){
                //判断和上一条数据是否为同一时间段，如果是则添加到上一条数据中如果不是则追加
                int lsize = list.size();
                if(lsize>0){
                    OnlineClinicDate onlineClinicDate = list.get(lsize-1);
                    if(onlineClinicDate.getWeek().equals(vo.getWeek()) 
                            && onlineClinicDate.getPeriod().equals(vo.getPeriod())){
                        onlineClinicDate.getDoctors().addAll(doctors);
                    }else{
                        vo.setDoctors(doctors);
                        list.add(vo);
                    }
                }else{
                    vo.setDoctors(doctors);
                    list.add(vo);
                }
            }
        }
        
        return list;
    }
    
    /**
     * </p>按医生查找在线值班时间表</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    public Map<String, Object> getAllByDoctor(Integer doctorId){
        DBObject query = new BasicDBObject();
        query.put("doctors.doctorId", doctorId);

        DBObject project = new BasicDBObject();
        project.put("_id", 0);
        project.put("groupId", 1);
        project.put("department", 1);
        project.put("week", 1);
        project.put("period", 1);
        project.put("doctors.$", 1);
        
        DBObject sort = new BasicDBObject();
        sort.put("week", 1);
        sort.put("period", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_online").find(query,project).sort(sort);
        
        
        Set<String> set = new HashSet<String>();
        Map<String,String> map = new HashMap<String, String>();
        List<OnlineVO> list = new ArrayList<OnlineVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            OnlineVO vo = new OnlineVO();
            
            vo.setDepartment(MongodbUtil.getString(obj, "department"));
            vo.setWeek(MongodbUtil.getInteger(obj, "week"));
            vo.setPeriod(MongodbUtil.getInteger(obj, "period"));

            BasicDBList doctor =  (BasicDBList)obj.get("doctors");
            vo.setStartTime(MongodbUtil.getString((BasicDBObject)doctor.get(0), "startTime"));
            vo.setEndTime(MongodbUtil.getString((BasicDBObject)doctor.get(0), "endTime"));
            
            list.add(vo);
            
            //查找集团名称
            String groupId = MongodbUtil.getString(obj, "groupId");
            String group = map.get(groupId);
            if(group != null ){
                vo.setGroup(group);
            }else{
                Group groupVO = groupDao.getById(groupId, null);
                if(groupVO!=null){
                    vo.setGroup(groupVO.getName());
                    
                    set.add(vo.getGroup());
                    map.put("groupId", vo.getGroup());
                }
            }
        }
        
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("online", list);
        dataMap.put("group", set);

        return dataMap;
    }
    
    /**
     * </p>删除在线值班信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    public void delete(OnlineParam param){
        
        DBObject query = new BasicDBObject();
        query.put("_id", param.getDepartmentId());
        
        DBObject obj = dsForRW.getDB().getCollection("c_department").findOne(query);
        
        if(obj!=null){
            DBObject updateQuery = new BasicDBObject();
            updateQuery.put("groupId", MongodbUtil.getString(obj, "groupId"));
            updateQuery.put("week", param.getWeek());
            updateQuery.put("period", param.getPeriod());
            
            DBObject update = new BasicDBObject();
            update.put("$pull", new BasicDBObject("doctors",new BasicDBObject("doctorId",param.getDoctorId())));
            
            dsForRW.getDB().getCollection("c_online").update(updateQuery,update,false,true);
        }
    }
    
    public void deleteAllByDoctorData(String groupId, Integer doctorId) {
	    DBObject updateQuery = new BasicDBObject();
	    updateQuery.put("groupId", groupId);
	    DBObject update = new BasicDBObject();
	    update.put("$pull", new BasicDBObject("doctors",new BasicDBObject("doctorId", doctorId)));
	    
	    dsForRW.getDB().getCollection("c_online").update(updateQuery,update,false,true);
    }
    
}
