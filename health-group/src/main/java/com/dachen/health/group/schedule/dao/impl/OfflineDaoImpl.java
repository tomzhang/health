
package com.dachen.health.group.schedule.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.ScheduleEnum;
import com.dachen.health.commons.constants.ScheduleEnum.OfflineDateFrom;
import com.dachen.health.commons.constants.ScheduleEnum.OfflineStatus;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.group.schedule.entity.vo.OfflineVO;
import com.dachen.util.DateUtil;
import com.dachen.util.MapDistance;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@Repository
public class OfflineDaoImpl extends NoSqlRepository implements IOfflineDao {

    /**
     * </p>添加门诊信息,通过更新来判断存不存在</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月11日
     */
    public void add(Offline po) {
        DBObject query = new BasicDBObject();
        query.put("doctorId", po.getDoctorId());
        query.put("hospitalId", po.getHospitalId());
        query.put("week", po.getWeek());
        query.put("period", po.getPeriod());

        DBObject update = new BasicDBObject();
        update.put("doctorId", po.getDoctorId());
        update.put("hospital", po.getHospital().trim());
        update.put("hospitalId", po.getHospitalId());
        if(po.getClinicType()!=null){
        	update.put("clinicType", po.getClinicType());
        }
        if(po.getPrice()!=null){
        	update.put("price", po.getPrice());
        }
        update.put("week", po.getWeek());
        update.put("period", po.getPeriod());
        update.put("startTime", po.getStartTime());
        update.put("endTime", po.getEndTime());
        if(po.getDateTime()!=null){
        	update.put("dateTime", po.getDateTime());
        }
        if(po.getUpdateTime()!=null){
        	update.put("updateTime", po.getUpdateTime());
        }
        dsForRW.getDB().getCollection("c_offline").update(query, update, true, true);
    }

    /**
     * </p>获取门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月11日
     */
    public OfflineVO getOne(OfflineParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getId());
        if(param.getDoctorId()!=null){
        	query.put("doctorId", param.getDoctorId());
        }
        DBObject obj = dsForRW.getDB().getCollection("c_offline").findOne(query);

        OfflineVO vo = new OfflineVO();
        if (obj != null) {
            vo.setId(MongodbUtil.getString(obj, "_id"));
            vo.setHospital(MongodbUtil.getString(obj, "hospital"));
            vo.setHospitalId(MongodbUtil.getString(obj, "hospitalId"));
            vo.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
            vo.setClinicType(MongodbUtil.getInteger(obj, "clinicType"));
            vo.setPrice(MongodbUtil.getLong(obj, "price"));
            vo.setWeek(MongodbUtil.getInteger(obj, "week"));
            vo.setPeriod(MongodbUtil.getInteger(obj, "period"));
            if(MongodbUtil.getLong(obj, "startTime")!=null&&MongodbUtil.getLong(obj, "endTime")!=null){//兼容老版本
	            vo.setStartTime(MongodbUtil.getLong(obj, "startTime"));
	            vo.setStartTimeString(DateUtil.getMinuteTimeByLong(MongodbUtil.getLong(obj, "startTime")));
	            vo.setEndTime(MongodbUtil.getLong(obj, "endTime"));
	            vo.setEndTimeString(DateUtil.getMinuteTimeByLong(MongodbUtil.getLong(obj, "endTime")));
            }
            if(MongodbUtil.getLong(obj, "dateTime")!=null){
            	vo.setDateTime(MongodbUtil.getLong(obj, "dateTime"));
	            vo.setDateTimeString(DateUtil.formatDate(MongodbUtil.getLong(obj, "dateTime")));
            }
        }
        return vo;
    }
    
    
    

    /**
     * </p>删除医生排班信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月11日
     */
    public void delete(OfflineParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getId());
        dsForRW.getDB().getCollection("c_offline").remove(query);
    }
    
    
  //根据医生的历史值班信息，删除对应的被预约信息记录表中状态为1（待预约）的记录
    public void removeOfflineItemList(OfflineVO oldOffline){
        Query<OfflineItem> q = dsForRW.createQuery(OfflineItem.class);
        q.field("doctorId").equal(oldOffline.getDoctorId());
        q.field("hospitalId").equal(oldOffline.getHospitalId());
        q.field("week").equal(oldOffline.getWeek());
        q.field("period").equal(oldOffline.getPeriod());
        q.field("status").equal(1);
        q.field("dataFrom").equal(1);
        q.field("startTime").greaterThan(System.currentTimeMillis());
        dsForRW.delete(q);
    }
    
    //获取未被删除的预约信息记录
    public List<Long> getHasAppointmentOfflineItemList(OfflineVO oldOffline){
    	 Query<OfflineItem> q = dsForRW.createQuery(OfflineItem.class);
         q.field("doctorId").equal(oldOffline.getDoctorId());
         q.field("hospitalId").equal(oldOffline.getHospitalId());
         q.field("week").equal(oldOffline.getWeek());
         q.field("period").equal(oldOffline.getPeriod());
         q.field("status").notEqual(1);
         q.field("startTime").greaterThan(System.currentTimeMillis());
         q.retrievedFields(true,"startTime");
         List<Long> list=new ArrayList<Long>();
         for(OfflineItem oi : q.asList()){
        	 list.add(oi.getStartTime());
         }
         return list;
    }
    

    /**
     * </p>更新门诊信息</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月12日
     */
    public void update(OfflineParam param) {
        DBObject query = new BasicDBObject();
        query.put("_id", param.getId());
        query.put("doctorId", param.getDoctorId());

        BasicDBObject update = new BasicDBObject();

        if (param.getClinicType() != null && ScheduleEnum.ClinicType.getTitle(param.getClinicType()) != null) {
            update.put("clinicType", param.getClinicType());
        }
        if (param.getPrice() != null) {
            update.put("price", param.getPrice());
        }
        if (StringUtil.isNotBlank(param.getHospital())) {
            update.put("hospital", param.getHospital().trim());
        }
        if(param.getHospitalId()!=null){
        	 update.put("hospitalId", param.getHospitalId());
        }
        if (param.getWeek() != null && param.getPeriod() != null) {
            update.put("week", param.getWeek());
            update.put("period", param.getPeriod());
        }
        if(param.getStartTime()!=null&&param.getEndTime()!=null){
        	update.put("startTime", param.getStartTime());
        	update.put("endTime", param.getEndTime());
        }
        if (!update.isEmpty()) {
        	//OfflineVO oldOffline=getOne(param);//查询历史记录
            dsForRW.getDB().getCollection("c_offline").update(query, new BasicDBObject("$set", update));
            
            //根据修改的医生坐诊信息，删除对应的医生被预约信息记录数据
           // removeOfflineItemList(oldOffline);
            //根据修改的医生坐诊信息，增加对应的医生被预约信息记录数据
            
            
        }
    }

    /**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月11日
     */
    public Map<String, Object> getAll(Integer doctorId,Integer week) {
        DBObject query = new BasicDBObject();
        query.put("doctorId", doctorId);
        if(week!=0) {
        	query.put("week", week);
        }
       
        DBObject sort = new BasicDBObject();
        sort.put("week", 1);
        sort.put("period", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_offline").find(query).sort(sort);

        Set<String> set = new HashSet<String>();
        List<OfflineVO> list = new ArrayList<OfflineVO>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            OfflineVO vo = new OfflineVO();
            vo.setId(MongodbUtil.getString(obj, "_id"));
            vo.setHospital(MongodbUtil.getString(obj, "hospital"));
            vo.setHospitalId(MongodbUtil.getString(obj, "hospitalId"));
            vo.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
            vo.setClinicType(MongodbUtil.getInteger(obj, "clinicType"));
            vo.setPrice(MongodbUtil.getLong(obj, "price"));
            vo.setWeek(MongodbUtil.getInteger(obj, "week"));
            vo.setPeriod(MongodbUtil.getInteger(obj, "period"));
            if(MongodbUtil.getLong(obj, "startTime")!=null&&MongodbUtil.getLong(obj, "endTime")!=null){//兼容老版本
	            vo.setStartTime(MongodbUtil.getLong(obj, "startTime"));
	            vo.setStartTimeString(DateUtil.getMinuteTimeByLong(MongodbUtil.getLong(obj, "startTime")));
	            vo.setEndTime(MongodbUtil.getLong(obj, "endTime"));
	            vo.setEndTimeString(DateUtil.getMinuteTimeByLong(MongodbUtil.getLong(obj, "endTime")));
            }
            list.add(vo);
            set.add(vo.getHospital());
        }

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("offline", list);
        map.put("hospital", set);

        return map;
    }
    
    /**
     * </p>查找线下医生坐诊表</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月11日
     */
    public Map<String,List<OfflineParam>> getDoctor(List<Integer> doctorIds,List <String> hospital) {
        DBObject query = new BasicDBObject();
        
        query.put("doctorId", new BasicDBObject("$in", doctorIds));
        query.put("hospital", new BasicDBObject("$in", hospital));

        DBCursor cursor = dsForRW.getDB().getCollection("c_offline").find(query);

        Map<String,List<OfflineParam>> map=new HashMap<>();
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
           
            OfflineParam param=new OfflineParam();
            
           param.setId((ObjectId) obj.get("_id"));
           param.setDoctorId(MongodbUtil.getInteger(obj, "doctorId"));
           param.setHospital(MongodbUtil.getString(obj, "hospital"));

           /**
            * 删除对应医生的坐诊诊信息
            */
           delete(param);
         
           if(map.containsKey(param.getHospital())){
        	   map.get(param.getHospital()).add(param);
           }else{
        	   List<OfflineParam>  list=new ArrayList<OfflineParam>();
        	   list.add(param);
        	   map.put(param.getHospital(), list);
           }
        }

       

        return map;
    }
    
    @Override
	public Map<String, Object> getAllHostialGroup(Integer doctorId, Integer week,String lat,String lng,List<HospitalInfo> hospitals) {
    	 DBObject query = new BasicDBObject();
         query.put("doctorId", doctorId);
         if(week!=0) {
         	query.put("week", week);
         }
         DBObject sort = new BasicDBObject();
         sort.put("week", 1);
         sort.put("period", 1);

         DBCursor cursor = dsForRW.getDB().getCollection("c_offline").find(query).sort(sort);
         Map<String, OfflineVO> map = new HashMap<String, OfflineVO>();
         List<OfflineVO> listoff = new ArrayList<OfflineVO>();//所有的信息
         while (cursor.hasNext()) {
             DBObject obj = cursor.next();

             OfflineVO vo = new OfflineVO();
             vo.setId(MongodbUtil.getString(obj, "_id"));
             vo.setHospital(MongodbUtil.getString(obj, "hospital"));
             vo.setClinicType(MongodbUtil.getInteger(obj, "clinicType"));
             vo.setPrice(MongodbUtil.getLong(obj, "price"));
             vo.setWeek(MongodbUtil.getInteger(obj, "week"));
             vo.setPeriod(MongodbUtil.getInteger(obj, "period"));
             //计算离当前位置的距离
             listoff.add(vo);
             OfflineVO vos = new OfflineVO();
             vos.setId(MongodbUtil.getString(obj, "_id"));
             vos.setHospital(MongodbUtil.getString(obj, "hospital"));
             vos.setHospitalId(MongodbUtil.getString(obj, "hospitalId"));
             //vo.setDistance(MapDistance.getDistance(lat, lng, "117.00999000000002", "36.66123"));
             if(hospitals!=null&&hospitals.size()>0){
             	for(HospitalInfo hos:hospitals){
             		if(vo.getHospital().equals(hos.getName())){
             			if(hos.getLat()!=null&&hos.getLng()!=null){
             				vos.setDistance(MapDistance.getDistance(lng, lat, hos.getLng(), hos.getLat()));
             				break;
             			}
             		}
             	}
             	
             	
             }
             
             map.put(vo.getHospital(), vos);
           
         }
         List<OfflineVO> list=new ArrayList<OfflineVO>();
         
         Iterator<String> keys = map.keySet().iterator(); 

         while(keys.hasNext()) { 
         String key = (String) keys.next(); 
         list.add(map.get(key));

      } 
         

        list=sort(list);
        
         //进行处理
         for(OfflineVO temp:list){
        	 List<OfflineVO> lists = new ArrayList<OfflineVO>();
        	 for(OfflineVO vo:listoff){
        		 if(vo.getHospital().equals(temp.getHospital())){
        			 lists.add(vo);
        		 }
        	 }
        	 temp.setOfflins(lists);
        	 lists=null;
        	 
         }
         Map<String,Object> offmap=new HashMap<>();
         offmap.put("hospital", list);
         return offmap;
	}
    
    /**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param doctorId
     * @return
     * @author Ming
     * @date 2016年5月17日11:42:03
     */
    public List<OfflineVO> getAllhospital(Integer doctorId,Integer week,String lat,String lng,List<HospitalInfo> hospitals) {
       
    	DBObject query = new BasicDBObject();
        query.put("doctorId", doctorId);
        if(week!=0) {
        	query.put("week", week);
        }
        DBObject sort = new BasicDBObject();
        sort.put("week", 1);
        sort.put("period", 1);

        DBCursor cursor = dsForRW.getDB().getCollection("c_offline").find(query).sort(sort);
        Map<String, OfflineVO> map = new HashMap<String, OfflineVO>();
       
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();

            OfflineVO vo = new OfflineVO();
            vo.setId(MongodbUtil.getString(obj, "_id"));
            vo.setHospital(MongodbUtil.getString(obj, "hospital"));
            //vo.setClinicType(MongodbUtil.getInteger(obj, "clinicType"));
            //vo.setPrice(MongodbUtil.getLong(obj, "price"));
            //vo.setWeek(MongodbUtil.getInteger(obj, "week"));
            //vo.setPeriod(MongodbUtil.getInteger(obj, "period"));
            //计算离当前位置的距离
            
            //vo.setDistance(MapDistance.getDistance(lat, lng, "117.00999000000002", "36.66123"));
            if(hospitals!=null&&hospitals.size()>0){
            	for(HospitalInfo hos:hospitals){
            		if(vo.getHospital().equals(hos.getName())){
            			vo.setDistance(hos.getDistance());
            			break;
//            			if(hos.getLat()!=null&&hos.getLng()!=null){
//            				vo.setDistance(MapDistance.getDistance(lat, lng, hos.getLat(), hos.getLng()));
//            				break;
//            			}else{
//            				//vo.setDistance("99999");
//            			}
            		}
            	}
            	
            	
            }else{
            	//vo.setDistance("99999");
            }
            
            map.put(vo.getHospital(), vo);
          
        }
        List<OfflineVO> list=new ArrayList<OfflineVO>();
        
        Iterator<String> keys = map.keySet().iterator(); 

        while(keys.hasNext()) { 
        String key = (String) keys.next(); 
        list.add(map.get(key));

     } 
        

      
        list=sort(list);
        return list;
    }
    /**
     * 按照距离对医院进行排序
     * @param list
     * @return
     * @author 李明
     */
    private  List<OfflineVO> sort( List<OfflineVO> list){
    	Collections.sort(list, new Comparator<OfflineVO>(){  
      	  
            public int compare(OfflineVO o1, OfflineVO o2) {  
            	if(o1.getDistance()==null){
             		return 1;
             	}
             	if(o2.getDistance()==null){
             		return -1;
             	}
                //按照距离的远近，进行排序
                if( Double.parseDouble(o1.getDistance())>  Double.parseDouble(o2.getDistance())){  
                    return 1;  
                }else  if(Double.parseDouble(o1.getDistance()) == Double.parseDouble(o2.getDistance())){  
                    return 0;  
                } else{
                	
                	return -1;  
                } 
            }  
        });   
    	return list;
    }
    
    public static void main(String[] args) {
    	//实验list排序是否有效
    	OfflineVO v1=new OfflineVO();
    	v1.setDistance(null);
    	OfflineVO v2=new OfflineVO();
    	v2.setDistance("15");
    	OfflineVO v3=new OfflineVO();
    	v3.setDistance(null);
    	OfflineVO v4=new OfflineVO();
    	v4.setDistance("52");
    	 List<OfflineVO> list=new ArrayList<OfflineVO>();
    	 list.add(v1);
    	 list.add(v2);
    	 list.add(v3);
    	 list.add(v4);
    	for(OfflineVO v:list){
    		System.out.println(v.getDistance());
    	}
    	
//   	list=sort(list);
//    	Collections.sort(list, new Comparator<OfflineVO>(){  
//      	  
//            /*  
//             * int compare(Student o1, Student o2) 返回一个基本类型的整型，  
//             * 返回负数表示：o1 小于o2，  
//             * 返回0 表示：o1和o2相等，  
//             * 返回正数表示：o1大于o2。  
//             */  
//            public int compare(OfflineVO o1, OfflineVO o2) {  
//            	
//                //按照学生的年龄进行升序排列  
//                if( Double.parseDouble(o1.getDistance())>  Double.parseDouble(o2.getDistance())){  
//                    return 1;  
//                }  
//                if(Double.parseDouble(o1.getDistance())==  Double.parseDouble(o2.getDistance())){  
//                    return 0;  
//                }  
//                return -1;  
//            }  
//        });   
    	
    	for(OfflineVO v:list){
    		System.out.println(v.getDistance());
    	}
    	
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<BasicDBObject> getDoctor7DaysOfflineItems(Integer doctorId, String hospitalId,Long startTime) {
		long begin = DateUtil.getDayBegin(startTime);
		long end = DateUtil.getDayBegin(startTime+DateUtil.weekmillSeconds);
		Query<OfflineItem> q = dsForRW.createQuery(OfflineItem.class);
		q.field("startTime").greaterThanOrEq(begin);
		q.field("endTime").lessThan(end);
		q.field("doctorId").equal(doctorId);
		q.field("hospitalId").equal(hospitalId);
		q.field("dataFrom").notEqual(OfflineDateFrom.导医添加.getIndex());
		
		String reduce = "function (doc,aggr){"
				 +	      "aggr.isAppoint = aggr.isAppoint && (doc.status != 1) ;"
				 +      "}";
		DBObject keys = new BasicDBObject();
		keys.put("period", 1);
		keys.put("dateTime", 1);
		DBObject result = dsForRW.getCollection(OfflineItem.class)
				.group(keys,
						q.getQueryObject(),
						new BasicDBObject("isAppoint",true), 
						reduce);
		
		Map<String,BasicDBObject> map = result.toMap();
		if(map.size() > 0){
			return map.values();
		}
		return null;
	}

	@Override
	public OfflineItem insertOfflineItem(OfflineItem item) {
		String id = dsForRW.insert(item).getId().toString();
		return dsForRW.createQuery(OfflineItem.class).field("_id").equal(new ObjectId(id)).get();
	}
	
	public List<OfflineItem> queryByCondition(OfflineParam param) {
		
	    Query<OfflineItem> query = dsForRW.createQuery(OfflineItem.class);
	    /*if(param.getId()!=null){
	    	query.field("offlineId").equal(param.getId());
	    }*/
        if(param.getHospitalId()!=null){
        	query.field("hospitalId").equal(param.getHospitalId());
        }
        if(param.getDoctorId()!=null){
        	query.field("doctorId").equal(param.getDoctorId());
        }
        if (param.getPeriod() != null) {
    	    query.field("period").equal(param.getPeriod());
        }
        if (param.getWeek() != null){
        	query.field("week").equal(param.getWeek());
        }
        if(param.getStartTime()!=null&&param.getEndTime()!=null){
        	query.field("startTime").greaterThanOrEq(param.getStartTime());
    	    query.field("endTime").lessThanOrEq(param.getEndTime());
        }
	    
		return query.asList();
	}

	@Override
	public List<Offline> getByWeek(int week) {

		return dsForRW.createQuery(Offline.class).field("week").equal(week).asList();
	}

	@Override
	public List<OfflineItem> offlineItemDetail(Integer doctorId, String hospitalId, Integer period,Long dateTime) {
		dateTime = DateUtil.getDayBegin(dateTime);
		return dsForRW.createQuery(OfflineItem.class)
					.field("doctorId").equal(doctorId)
					.field("hospitalId").equal(hospitalId)
					.field("period").equal(period)
					.field("dateTime").equal(dateTime)
					.field("dataFrom").notEqual(OfflineDateFrom.导医添加.getIndex())
					.field("status").equal(OfflineStatus.待预约.getIndex())
					.asList();
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection<BasicDBObject> getDoctorOfflineAndCount(String hospitalId, Long dateTime, Integer period) {
		Query<OfflineItem> q = dsForRW.createQuery(OfflineItem.class);
			q.field("dateTime").equal(dateTime);
			q.field("period").equal(period);
			q.field("hospitalId").equal(hospitalId);
			q.order("startTime");
		
		String reduce = "function (doc,aggr){"
				 +	      "aggr.totalCount += 1 ;"
				 +        "if(doc.status != 1){"
				 +	      		"aggr.realCount += 1 ;"
				 +		   "}"
				 +		  "if(doc.endTime > aggr.overTime){"
				 +          "aggr.overTime = doc.endTime;"
				 + 		   "}"
				 +		  "if(doc.startTime < aggr.beginTime){"
				 +          "aggr.beginTime = doc.startTime;"
				 + 		   "}"
				 +      "}";
		DBObject keys = new BasicDBObject("doctorId", 1);
		keys.put("week", 1);
		
		DBObject group = new BasicDBObject();
		group.put("totalCount", 0);
		group.put("realCount", 0);
		group.put("beginTime", Long.MAX_VALUE);
		group.put("overTime", 0);
		
		DBObject result = dsForRW.getCollection(OfflineItem.class)
						.group(keys,
								q.getQueryObject(),
								group,
								reduce);
		Map<String,BasicDBObject> map = result.toMap();
		if(map.size() > 0){
			return map.values();
		}
		return null;
	}

	@Override
	public Long searchAppointmentOrder4GuideCount(List<Integer> patientIds) {
		return dsForRW.createQuery(OfflineItem.class)
					.field("patientId").in(patientIds)
					.countAll();
	}

	@Override
	public List<OfflineItem> searchAppointmentOrder4Guide(List<Integer> patientIds, Integer pageIndex , Integer pageSize) {
		return dsForRW.createQuery(OfflineItem.class)
					.field("patientId").in(patientIds)
					.order("-startTime")
					.offset(pageIndex*pageSize)
					.limit(pageSize)
					.asList();
	}

	@Override
	public OfflineItem findOfflineItemById(String offlineItemId) {
		return  dsForRW.createQuery(OfflineItem.class).field("_id").equal(new ObjectId(offlineItemId)).get();
	}

	@Override
	public void updateOfflineItemStatus(String offlineItemId, int status) {
		Query<OfflineItem> q = 
				dsForRW.createQuery(OfflineItem.class)
				.field("_id").equal(new ObjectId(offlineItemId));
		UpdateOperations<OfflineItem> ops = dsForRW.createUpdateOperations(OfflineItem.class);
		ops.set("status", status);
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public void updateOfflineItemOrderInfo(String offlineItemId, Integer orderId, Integer patientId) {
		Query<OfflineItem> q = 
				dsForRW.createQuery(OfflineItem.class)
				.field("_id").equal(new ObjectId(offlineItemId));
		UpdateOperations<OfflineItem> ops = dsForRW.createUpdateOperations(OfflineItem.class);
		ops.set("orderId", orderId);
		ops.set("patientId", patientId);
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public void updateOfflineItemStatusByOrderId(Integer orderId,Integer doctorId ,  Integer status) {
		Query<OfflineItem> q = 
				dsForRW.createQuery(OfflineItem.class)
				.field("orderId").equal(orderId)
				.field("doctorId").equal(doctorId)
				.field("status").notEqual(ScheduleEnum.OfflineStatus.待预约.getIndex());
		UpdateOperations<OfflineItem> ops = dsForRW.createUpdateOperations(OfflineItem.class);
		ops.set("status", status);
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public OfflineItem findOfflineItemByOrderId(Integer orderId , Integer doctorId) {
		return dsForRW.createQuery(OfflineItem.class)
					.field("orderId").equal(orderId)
					.field("doctorId").equal(doctorId)
					.field("status").notEqual(ScheduleEnum.OfflineStatus.待预约.getIndex()).get();
	}

	@Override
	public void cancelOfflineItem(Integer orderId) {
		Query<OfflineItem> q = dsForRW.createQuery(OfflineItem.class)
				.field("orderId").equal(orderId)
				.field("status").notEqual(ScheduleEnum.OfflineStatus.待预约.getIndex());
		UpdateOperations<OfflineItem> ops = dsForRW.createUpdateOperations(OfflineItem.class);
		ops.unset("orderId");
		ops.unset("patientId");
		ops.set("status", ScheduleEnum.OfflineStatus.待预约.getIndex());
		dsForRW.updateFirst(q, ops);
	}

	@Override
	public List<OfflineItem> getPatientAppointmentByCondition(String hospitalId, Integer doctorId, Long oppointTime) {
		Query<OfflineItem> query = dsForRW.createQuery(OfflineItem.class);
        query.field("hospitalId").equal(hospitalId);
        query.field("doctorId").equal(doctorId);
        query.field("startTime").greaterThanOrEq(oppointTime);
    	query.field("startTime").lessThanOrEq(oppointTime+86400000);
    	return query.asList();
	}

	@Override
	public List<Offline> findOfflineByDoctorPeriod(String hospitalId , Integer doctorId, Integer week, Integer period) {
		Query<Offline> q = dsForRW.createQuery(Offline.class);
	        q.field("hospitalId").equal(hospitalId);
	        q.field("doctorId").equal(doctorId);
	        q.field("week").equal(week);
	        q.field("period").equal(period);
        return q.asList();
	}

	@Override
	public List<OfflineItem> getDoctorOneDayOffline(String hospitalId, long dateTime, Integer doctorId) {
		return dsForRW.createQuery(OfflineItem.class)
				.field("hospitalId").equal(hospitalId)
				.field("dateTime").equal(dateTime)
				.field("doctorId").equal(doctorId)
				.order("startTime")
				.asList();
	}

	@Override
	public OfflineItem getOfflineItem(Long startTime, Integer doctorId) {
		return dsForRW.createQuery(OfflineItem.class)
					.field("startTime").equal(startTime)
					.field("doctorId").equal(doctorId)
					.get();
	}

	@Override
	public List<Offline> queryByConditions(OfflineParam param) {
		Calendar cal=Calendar.getInstance();
		
		Query<Offline> query = dsForRW.createQuery(Offline.class);
		if(param.getHospitalId()!=null){
			query.field("hospitalId").equal(param.getHospitalId());
		}
		if(param.getDoctorId()!=null){
			query.field("doctorId").equal(param.getDoctorId());
		}
		if(param.getPeriod()!=null){
			query.field("period").equal(param.getPeriod());
		}
		if(param.getWeek()!=null){
			query.field("week").equal(param.getWeek());
		}
		if(param.getDateTime()!=null){
			cal.setTimeInMillis(param.getDateTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			param.setDateTime(cal.getTimeInMillis());
			query.field("dateTime").greaterThanOrEq(param.getDateTime());
			query.field("dateTime").lessThan(param.getDateTime()+86400000);
		}
		if(param.getStartTime()!=null){
			query.field("startTime").greaterThanOrEq(param.getStartTime());
			query.field("endTime").lessThanOrEq(param.getEndTime());
		}
		query.order("startTime");//按照开始时间顺序排序
		List<Offline> list=query.asList();
		for(Offline o : list){
			if(o.getStartTime()!=null){
				o.setStartTimeString(DateUtil.getMinuteTimeByLong(o.getStartTime()));
			}
			if(o.getEndTime()!=null){
				o.setEndTimeString(DateUtil.getMinuteTimeByLong(o.getEndTime()));
			}
			if(o.getDateTime()!=null){
				o.setDateTimeString(DateUtil.formatDate(o.getDateTime()));
			}
		}
        return list;
	}

	@Override
	public List<Integer> getDoctorIdsInHospitalIds(List<String> hospitalIds) {
		Query<OfflineItem> q = dsForRW.createQuery(OfflineItem.class);
		q.field("hospitalId").in(hospitalIds);
		q.retrievedFields(true, "doctorId");
		List<OfflineItem> list = q.asList();
		List<Integer> doctorIds = new ArrayList<Integer>();
		if(list != null && list.size() > 0){
			for (OfflineItem o : list) {
				doctorIds.add(o.getDoctorId());
			}
		}
		return doctorIds;
	}

	@Override
	public Long queryhasOffline(OfflineParam param) {
		Query<Offline> query = dsForRW.createQuery(Offline.class);
		query.field("doctorId").equal(param.getDoctorId());
		/*query.or(query.criteria("startTime").greaterThanOrEq(param.getStartTime()).criteria("startTime").lessThan(param.getEndTime()) , 
				query.criteria("endTime").greaterThan(param.getStartTime()).criteria("endTime").lessThanOrEq(param.getEndTime()),
				
				query.criteria("startTime").lessThanOrEq(param.getStartTime()).criteria("endTime").greaterThan(param.getStartTime()) , 
				query.criteria("startTime").lessThan(param.getEndTime()).criteria("endTime").greaterThanOrEq(param.getEndTime())
				);*/
		query.field("startTime").lessThan(param.getEndTime());
		query.field("endTime").greaterThan(param.getStartTime());
        return query.countAll();
	}

	@Override
	public void addNew(Offline offline) {
		  DBObject dbo = new BasicDBObject();
	      dbo.put("doctorId", offline.getDoctorId());
	      dbo.put("hospital", offline.getHospital().trim());
	      dbo.put("hospitalId", offline.getHospitalId());
	      if(offline.getClinicType()!=null){
	       dbo.put("clinicType",offline.getClinicType());
	      }
	      if(offline.getPrice()!=null){
	       dbo.put("price", offline.getPrice());
	      }
	      dbo.put("week", offline.getWeek());
	      dbo.put("period", offline.getPeriod());
	      dbo.put("startTime", offline.getStartTime());
	      dbo.put("endTime", offline.getEndTime());
	      if(offline.getDateTime()!=null){
	        dbo.put("dateTime", offline.getDateTime());
	      }
	      if(offline.getUpdateTime()!=null){
	        dbo.put("updateTime", offline.getUpdateTime());
	      }
	      dsForRW.getDB().getCollection("c_offline").insert(dbo);
	}

	@Override
	public List<Offline> queryByConditionsForWeb(OfflineParam param) {
Calendar cal=Calendar.getInstance();
		
		Query<Offline> query = dsForRW.createQuery(Offline.class);
		if(param.getHospitalId()!=null){
			query.field("hospitalId").equal(param.getHospitalId());
		}
		if(param.getDoctorId()!=null){
			query.field("doctorId").equal(param.getDoctorId());
		}
		if(param.getPeriod()!=null){
			query.field("period").equal(param.getPeriod());
		}
		if(param.getWeek()!=null){
			query.field("week").equal(param.getWeek());
		}
		if(param.getDateTime()!=null){
			cal.setTimeInMillis(param.getDateTime());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			param.setDateTime(cal.getTimeInMillis());
			query.field("dateTime").greaterThanOrEq(param.getDateTime());
			query.field("dateTime").lessThan(param.getDateTime()+86400000);
		}
		if(param.getStartTime()!=null){
			query.field("startTime").greaterThanOrEq(param.getStartTime());
			query.field("endTime").lessThanOrEq(param.getEndTime());
		}
		query.order("-updateTime");//按照更新时间顺序排序
		List<Offline> list=query.asList();
		for(Offline o : list){
			if(o.getStartTime()!=null){
				o.setStartTimeString(DateUtil.getMinuteTimeByLong(o.getStartTime()));
			}
			if(o.getEndTime()!=null){
				o.setEndTimeString(DateUtil.getMinuteTimeByLong(o.getEndTime()));
			}
			if(o.getDateTime()!=null){
				o.setDateTimeString(DateUtil.formatDate(o.getDateTime()));
			}
		}
        return list;
	}

}
