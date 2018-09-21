package com.dachen.health.group.schedule.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.GroupEnum;
import com.dachen.health.commons.constants.ScheduleEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.vo.HospitalInfo;
import com.dachen.health.group.schedule.dao.IOfflineDao;
import com.dachen.health.group.schedule.entity.param.OfflineClinicDate;
import com.dachen.health.group.schedule.entity.param.OfflineParam;
import com.dachen.health.group.schedule.entity.po.Offline;
import com.dachen.health.group.schedule.entity.po.OfflineItem;
import com.dachen.health.group.schedule.entity.vo.DoctorOfflineVO;
import com.dachen.health.group.schedule.entity.vo.DoctorVO;
import com.dachen.health.group.schedule.entity.vo.OfflineVO;
import com.dachen.health.group.schedule.service.IOfflineService;
import com.dachen.health.group.schedule.service.IOftenAddrService;
import com.dachen.util.BeanUtil;
import com.dachen.util.DateUtil;
import com.dachen.util.MapDistance;
import com.dachen.util.MongodbUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;

@Service
public class OfflineServiceImpl implements IOfflineService {

    @Autowired
    protected IOfflineDao offlineDao;

    @Autowired
    protected IOftenAddrService oftenAddrService;
    @Autowired
    protected IGroupDao groupDao;
    
    @Autowired
    protected IGroupDoctorDao gdDao;
    
    @Autowired
    protected UserManager userManager;

    /**
     * </p>添加门诊信息</p>
     * 
     * @param param
     * @author fanp
     * @throws CloneNotSupportedException
     * @date 2015年8月11日
     */
    public void add(OfflineParam param) throws CloneNotSupportedException {
      //  if (param.getClinicType() == null || ScheduleEnum.ClinicType.getTitle(param.getClinicType()) == null) {
        //    throw new ServiceException("请选择门诊类型");
       // }
        //if (param.getPrice() == null) {
        //    throw new ServiceException("请填写门诊价格");
       // }
        if (StringUtil.isBlank(param.getHospital())) {
            throw new ServiceException("请填写出诊医院");
        }
        if(param.getClinicDate() == null || param.getClinicDate().isEmpty()){
//        	if (param.getClinicDate().size() == 0) {
                throw new ServiceException("请填写正确出诊时间");
//            }
        }

        Offline po = BeanUtil.copy(param, Offline.class);

        /**
         * 医生新增的预约信息数据
         */
        List<Offline> offlineList = new ArrayList<Offline>();
        for (OfflineClinicDate week : param.getClinicDate()) {
            for (Integer period : week.getPeriod()) {
                // 判断时间的正确性
                if (week.getWeek() >= 1 && week.getWeek() <= 7 && period >= 1 && period <= 3) {
                    Offline offline = (Offline) po.clone();
                    offline.setWeek(week.getWeek());
                    offline.setPeriod(period);
                    offline.setStartTime(week.getStartTime());
                    offline.setEndTime(week.getEndTime());
                    // 添加线下坐诊时间
                    offlineDao.add(offline);
                    if(week.getStartTime() != null && week .getEndTime() != null){
                    	offlineList.add(offline);
                    }
                }
            }
        }

        // 添加常用地址
        oftenAddrService.add(param.getHospital(), param.getDoctorId());
        
        /**
         * 设置医生的排班信息
         */
        if(offlineList != null && offlineList.size() > 0){
        	for (Offline o : offlineList) {
        		addDoctorOfflineItem(o,new ArrayList<Long>());
			}
        }
    }

    /**
     * 初始化12个礼拜的数据（从当前天算起）
     */
    private void addDoctorOfflineItem(Offline o,List<Long> startTimes) {
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(new Date());
    	int nowWeek = cal.get(Calendar.DAY_OF_WEEK) - 1;
    	if(nowWeek == 0){
    		nowWeek = 7;
    	}
		Long startTime = o.getStartTime();
		Long endTime = o.getEndTime();
		Calendar calStart = Calendar.getInstance();
		calStart.setTimeInMillis(startTime);
		
		calStart.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		calStart.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		calStart.set(Calendar.DATE, cal.get(Calendar.DATE));
		
		int startWeek = o.getWeek();
		Long itemSize = (endTime - startTime)/DateUtil.minute30millSeconds ;
		int tempWeek = nowWeek;
		while(tempWeek != startWeek){
			long temp = calStart.getTimeInMillis() + DateUtil.daymillSeconds;
			calStart.setTimeInMillis(temp);
			tempWeek = calStart.get(Calendar.DAY_OF_WEEK) - 1;
			if(tempWeek == 0){
				tempWeek = 7;
	    	}
		}
		setOfflineItem(itemSize.intValue(),calStart.getTimeInMillis(),o,startTimes);
	}

	private void setOfflineItem(int itemSize, long startTime,Offline o,List<Long> startTimes) {
		Long now = System.currentTimeMillis();
		for(int i=0 ; i<12 ; i++){
			long dateTime = DateUtil.getDayBegin(startTime);
			long tempTime = startTime;
			int tempSize = itemSize;
			while(tempSize-- > 0){
				if(tempTime >= now && !startTimes.contains(tempTime)){
					OfflineItem item = new OfflineItem();
					item.setCreateTime(now);
					item.setStartTime(tempTime);
					item.setEndTime(tempTime+DateUtil.minute30millSeconds);
					item.setDoctorId(o.getDoctorId());
					item.setHospitalId(o.getHospitalId());
					item.setWeek(o.getWeek());
					item.setPeriod(o.getPeriod());
					item.setStatus(ScheduleEnum.OfflineStatus.待预约.getIndex());
					item.setDateTime(dateTime);
					offlineDao.insertOfflineItem(item);
				}
				tempTime += DateUtil.minute30millSeconds;
			}
			startTime += DateUtil.weekmillSeconds;
		}
	}

	/**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param doctorId
     * @return List<OfflineVO> Set<hospital>
     * @author fanp
     * @date 2015年8月11日
     */
    public Map<String, Object> getAll(Integer doctorId,Integer week,String lat,String lng,Integer is_hospital_group) {
    	String groupId = groupDao.getAppointmentGroupId();
    	List<HospitalInfo> hospitals=new ArrayList<>();
    	
    	if(groupId!=null){
    		hospitals=groupDao.getById(groupId).getConfig().getHospitalInfo();
    	}
    	for(HospitalInfo hos:hospitals){
			if(hos.getLat()!=null&&hos.getLng()!=null&&lat!=null&&lng!=null){
				String distance=MapDistance.getDistance(lng, lat, hos.getLng(), hos.getLat());
				hos.setDistance(distance);
			}
		}
    	
    	
    	if(is_hospital_group==0){
    		Map<String, Object> map=offlineDao.getAll(doctorId,week);
    		if(lat!=null&&lng!=null){
    			map.put("hospital", offlineDao.getAllhospital(doctorId, week, lat, lng,hospitals));
    		}
    		return map;
    	}else if(is_hospital_group==1){
    		
    		//offlineDao.getAllhospital(doctorId, week, lat, lng);
    		if(lat!=null&&lng!=null){
    			return offlineDao.getAllHostialGroup(doctorId, week, lat, lng,hospitals);
    		}else{
    			return offlineDao.getAll(doctorId,week);
    		}
    		
    		
    	}else{
    		  throw new ServiceException("is_hospital_group参数设置错误");
    	}
		
       
    }

    /**
     * </p>查找医生线下坐诊时间表</p>
     * 
     * @param param
     * @return OfflineVO
     * @author fanp
     * @date 2015年8月11日
     */
    public OfflineVO getOne(OfflineParam param) {
        return offlineDao.getOne(param);
    }

    /**
     * </p>修改门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    public void update(OfflineParam param) {
    	OfflineVO oldOffline=offlineDao.getOne(param);//查询历史记录
        offlineDao.update(param);
        // 添加常用地址
        if(StringUtil.isNotBlank(param.getHospital())){
            oftenAddrService.add(param.getHospital().trim(), param.getDoctorId());
        }
        
        Offline po = BeanUtil.copy(param, Offline.class);
        
        if(param.getStartTime()==null){
        	if(oldOffline.getStartTime()==null){//兼容老接口
        		return;
        	}
        	po.setStartTime(oldOffline.getStartTime());
        }
        if(param.getEndTime()==null){
        	if(oldOffline.getEndTime()==null){//兼容老接口
        		return;
        	}
        	po.setEndTime(oldOffline.getEndTime());
        }
        if(param.getWeek()==null){
        	po.setWeek(oldOffline.getWeek());
        }
        if(param.getPeriod()==null){
        	po.setPeriod(oldOffline.getPeriod());
        }
        if(param.getHospital()==null){
        	po.setHospital(oldOffline.getHospital());
        }
        if(param.getHospitalId()==null){
        	po.setHospitalId(oldOffline.getHospitalId());
        }
        
        //根据修改的医生坐诊信息，删除对应的医生被预约信息记录数据
        offlineDao.removeOfflineItemList(oldOffline);
        //根据修改的医生坐诊信息，增加对应的医生被预约信息记录数据
        
        List<Long> list=offlineDao.getHasAppointmentOfflineItemList(oldOffline);
        addDoctorOfflineItem(po,list);
             
    }

    /**
     * </p>删除门诊坐诊信息</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    public void delete(OfflineParam param) {
    	OfflineVO oldOffline=offlineDao.getOne(param);//查询历史记录
        offlineDao.delete(param);
        //根据修改的医生坐诊信息，删除对应的医生被预约信息记录数据
        offlineDao.removeOfflineItemList(oldOffline);
    }

	@Override
	public Map<String,Map<String,Boolean>> getDoctor7DaysOfflineItems(Integer doctorId, String hospitalId , Long startTime){
		Collection<BasicDBObject> coll = offlineDao.getDoctor7DaysOfflineItems(doctorId,hospitalId,startTime);
		Map<String,Map<String,Boolean>> map = getInitDoctorOfflineItem(startTime);
		if(coll != null){
			Iterator<BasicDBObject> ite = coll.iterator();
			while(ite.hasNext()){
				BasicDBObject obj = ite.next();
				String dateTime = MongodbUtil.getString(obj, "dateTime");
				Map<String,Boolean> innerMap = map.get(dateTime);
				if(innerMap != null){
					//从mongodb 中取出数据之后就成了double
					Double period = MongodbUtil.getDouble(obj, "period");
					Boolean isAppoint = MongodbUtil.getBoolean(obj, "isAppoint");
					if(period != null){
						innerMap.put(String.valueOf(period.intValue()), isAppoint);
					}
				}
			}
		}
		return map;
	}
	
	private Map<String,Map<String,Boolean>> getInitDoctorOfflineItem(Long startTime){
		Map<String,Map<String,Boolean>> map = new TreeMap<String,Map<String,Boolean>>();
		long millSeconds = DateUtil.daymillSeconds;
		long base = DateUtil.getDayBegin(startTime);
		for(int i=0;i<7;i++){
			Map<String,Boolean> innerMap = new HashMap<String,Boolean>();
			long key = base + millSeconds * i;
			innerMap.put("1", null);
			innerMap.put("2", null);
			innerMap.put("3", null);
			map.put(String.valueOf(key), innerMap);
		}
		return map;
	}

	@Override
	public Boolean hasAppointment(OfflineParam param) {
		Boolean flag=false;
		OfflineVO ov=offlineDao.getOne(param);
		OfflineParam newParam=new OfflineParam();
		newParam.setHospitalId(ov.getHospitalId());
		newParam.setDoctorId(ov.getDoctorId());
		newParam.setPeriod(ov.getPeriod());
		newParam.setWeek(ov.getWeek());
		newParam.setStartTime(ov.getStartTime());
		newParam.setEndTime(ov.getEndTime());
		newParam.setDateTime(ov.getDateTime());
		List<OfflineItem> list=offlineDao.queryByCondition(newParam);
		for(OfflineItem oi : list){
			if(oi.getStatus()!=1){
				flag=true;
				break;
			}
		}
		return flag;
	}

	@Override
	public void addOneDayOfflineItem(Offline o, long startTime,int itemSize) {
		long now = System.currentTimeMillis();
		long dateTime = DateUtil.getDayBegin(startTime);
		while(itemSize-- > 0){
			OfflineItem item = new OfflineItem();
			item.setCreateTime(now);
			item.setStartTime(startTime);
			item.setEndTime(startTime+DateUtil.minute30millSeconds);
			item.setDoctorId(o.getDoctorId());
			item.setHospitalId(o.getHospital());
			item.setWeek(o.getWeek());
			item.setPeriod(o.getPeriod());
			item.setStatus(ScheduleEnum.OfflineStatus.待预约.getIndex());
			item.setDateTime(dateTime);
			offlineDao.insertOfflineItem(item);
			startTime += DateUtil.minute30millSeconds;
		}
	}

	@Override
	public List<OfflineItem> offlineItemDetail(Integer doctorId, String hospitalId, Integer period,Long dateTime) {
		return offlineDao.offlineItemDetail(doctorId,hospitalId,period,dateTime);
	}

	@Override
	public Collection<BasicDBObject> getDoctorOfflineAndCount(String hospitalId, Long dateTime, Integer period) {
		return offlineDao.getDoctorOfflineAndCount(hospitalId,dateTime,period);
	}

	@Override
	public void addOffline(OfflineParam param) {
		Offline offline = BeanUtil.copy(param, Offline.class);
		offline.setUpdateTime(System.currentTimeMillis());
		Calendar dateTime=Calendar.getInstance();
		dateTime.setTimeInMillis(offline.getDateTime());
		dateTime.set(Calendar.HOUR_OF_DAY, 0);
		dateTime.set(Calendar.MINUTE, 0);
		dateTime.set(Calendar.SECOND, 0);
		dateTime.set(Calendar.MILLISECOND, 0);
		offline.setDateTime(dateTime.getTimeInMillis());
		
		
		Calendar startTime=Calendar.getInstance();
		startTime.setTimeInMillis(offline.getStartTime());
		startTime.set(Calendar.YEAR, dateTime.get(Calendar.YEAR));
		startTime.set(Calendar.MONTH, dateTime.get(Calendar.MONTH));
		startTime.set(Calendar.DAY_OF_MONTH, dateTime.get(Calendar.DAY_OF_MONTH));
		offline.setStartTime(startTime.getTimeInMillis());
		
		Calendar endTime=Calendar.getInstance();
		endTime.setTimeInMillis(offline.getEndTime());
		endTime.set(Calendar.YEAR, dateTime.get(Calendar.YEAR));
		endTime.set(Calendar.MONTH, dateTime.get(Calendar.MONTH));
		endTime.set(Calendar.DAY_OF_MONTH, dateTime.get(Calendar.DAY_OF_MONTH));
		offline.setEndTime(endTime.getTimeInMillis());
		
		//针对WEB端，对时间片进行拆分
		//Long am_0600=offline.getDateTime()+6*60*60*1000;//排班日期早上6点
		Long am_1200=offline.getDateTime()+12*60*60*1000;//排班日期中午12点
		Long pm_1800=offline.getDateTime()+18*60*60*1000;//排班日期下午18点
		//时间片起始时间
		Long start=offline.getStartTime();
		Long end=offline.getEndTime();
			if(start<am_1200&&end>am_1200&&end<=pm_1800){
				offline.setEndTime(am_1200);
				offline.setPeriod(1);
				addOffline(offline);
				offline.setStartTime(am_1200);
				offline.setEndTime(end);
				offline.setPeriod(2);
				addOffline(offline);
			}else if(start<am_1200&&end>pm_1800){
				offline.setEndTime(am_1200);
				offline.setPeriod(1);
				addOffline(offline);
				offline.setStartTime(am_1200);
				offline.setEndTime(pm_1800);
				offline.setPeriod(2);
				addOffline(offline);
				offline.setStartTime(pm_1800);
				offline.setEndTime(end);
				offline.setPeriod(3);
				addOffline(offline);
			}else if(start>=am_1200&&start<pm_1800&&end>pm_1800){
				offline.setEndTime(pm_1800);
				offline.setPeriod(2);
				addOffline(offline);
				offline.setStartTime(pm_1800);
				offline.setEndTime(end);
				offline.setPeriod(3);
				addOffline(offline);
			}else{
				addOffline(offline);
			}
	}
	
	private void addOffline(Offline offline){
		//offlineDao.add(offline);
		offlineDao.addNew(offline);
		//生成对应的预约信息记录
		for(long time=offline.getStartTime(); time<offline.getEndTime();time+=DateUtil.minute30millSeconds){
			OfflineItem oi=new OfflineItem();
			oi.setStartTime(time);
			oi.setEndTime(time+DateUtil.minute30millSeconds);
			oi.setDateTime(offline.getDateTime());
			oi.setPeriod(offline.getPeriod());
			oi.setWeek(offline.getWeek());
			oi.setDoctorId(offline.getDoctorId());
			oi.setHospitalId(offline.getHospitalId());
			oi.setStatus(1);//设置为待预约状态
			oi.setDataFrom(1);//默认为医生添加
			oi.setCreateTime(System.currentTimeMillis());
			offlineDao.insertOfflineItem(oi);
		}
	}

	@Override
	public List<HospitalInfo> getHospitalList(UserSession us) {
		String groupId = getGroupId(us);
		if(groupId!=null){
			Group g = groupDao.getById(groupId);
			return g.getConfig().getHospitalInfo();
		}else{
			return Collections.emptyList();
		}
	}

	@Override
	public List<DoctorVO> getDoctorList(UserSession us) {
		String groupId = getGroupId(us);
		if(groupId!=null){
			//Group g = groupDao.getById(groupId);
			List<DoctorVO> doctorList=new ArrayList<DoctorVO>();
			List<GroupDoctor> gdList=gdDao.findDoctorsListByGroupId(groupId);
			for(GroupDoctor gd : gdList){
				if(!gd.getStatus().equalsIgnoreCase(GroupEnum.GroupDoctorStatus.正在使用.getIndex())){
					continue;
				}
				DoctorVO dv=new DoctorVO();
				dv.setDoctorId(gd.getDoctorId());
				dv.setGroupId(groupId);
				User user=userManager.getUser(gd.getDoctorId());
				dv.setDoctorName(user.getName());
				dv.setHospitalName(user.getDoctor().getHospital());
				dv.setHospitalId(user.getDoctor().getHospitalId());
				dv.setDepartments(user.getDoctor().getDepartments());
				dv.setDepartmentId(user.getDoctor().getDeptId());
				dv.setTitle(user.getDoctor().getTitle());
				doctorList.add(dv);
			}
			return doctorList;
		}else{
			return Collections.emptyList();
		}
	}
	
	
	public String getGroupId(UserSession us) {
		String groupId = groupDao.getAppointmentGroupId();
		if(StringUtils.isBlank(groupId)){
			return null;
		}
		Group g = groupDao.getById(groupId);
		if(g.getConfig() == null || !g.getConfig().isOpenSelfGuide()){
			return null;
		}
		if(UserType.doctor.getIndex() == us.getUserType()){
			List<GroupDoctor> gds = gdDao.findMainGroupByDoctorId(us.getUserId());
			if(gds != null && gds.size() > 0 && StringUtils.equals(groupId, gds.get(0).getGroupId())){
				return groupId;
			}
		}else if(UserType.DocGuide.getIndex() == us.getUserType()){
			User u = userManager.getUser(us.getUserId());
			if(u.getDoctorGuider() != null && StringUtils.equals(groupId, u.getDoctorGuider().getGroupId())){
				return groupId;
			}
		}
		return null;
	}
	
	
	public List<DoctorOfflineVO> getDoctorOfflineVOList(OfflineParam param){
		List<DoctorOfflineVO> list=new ArrayList<DoctorOfflineVO>();
		int index=1;
		for(long time=param.getStartTime();time<param.getEndTime();time+=7*24*60*60*1000){
			DoctorOfflineVO dv=new DoctorOfflineVO();
			Integer[] days=DateUtil.getWeekDays(new Date(time));
			dv.setIndex(index++);
			dv.setDays(days);
			dv.setOfflineList(new ArrayList<Offline>());
			Map<String,List<Offline>> offlineMap=new HashMap<String,List<Offline>>();
			for(int i=1;i<=7;i++){
				for(int j=1;j<=3;j++){
					offlineMap.put(i+""+j, new ArrayList<Offline>());
				}
			}
			dv.setOfflineMap(offlineMap);
			list.add(dv);
		}
		return list;
	}

	@Override
	public List<Offline> queryDoctorPeriodOfflines(OfflineParam param) {
		List<Offline> offlineList=offlineDao.queryByConditions(param);
		return offlineList;
	}

	@Override
	public List<DoctorOfflineVO> queryByConditionsForClient(OfflineParam param) {
		if(param.getStartTime()==null){
			Calendar cal= Calendar.getInstance();
			cal.setTimeInMillis(System.currentTimeMillis());
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND, 0);
	        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);//设置为本周周一
	        param.setStartTime(cal.getTimeInMillis());
	        param.setEndTime(cal.getTimeInMillis()+21*24*60*60*1000);//默认间隔为三周
		}
		List<Offline> allOfflineList=offlineDao.queryByConditions(param);
		
		//对每个医生的排班信息数据按周进行分组，默认为三周
		List<DoctorOfflineVO> doctorOfflineVOList=getDoctorOfflineVOList(param);
		for(Offline of : allOfflineList){
			for(DoctorOfflineVO dov : doctorOfflineVOList){
				if(of.getStartTime()>=param.getStartTime()&&of.getStartTime()<param.getStartTime()+7*24*60*60*1000&&dov.getIndex()==1){
					dov.getOfflineList().add(of);
					break;
				}
				if(of.getStartTime()>=param.getStartTime()&&of.getStartTime()<param.getStartTime()+7*24*60*60*1000*2&&dov.getIndex()==2){
					dov.getOfflineList().add(of);
					break;
				}
				if(of.getStartTime()>=param.getStartTime()&&of.getStartTime()<param.getEndTime()&&dov.getIndex()==3){
					dov.getOfflineList().add(of);
					break;
				}
			}
		}
		for(DoctorOfflineVO d : doctorOfflineVOList){
			List<Offline> list=d.getOfflineList();
			Map<String,List<Offline>> offlineMap=d.getOfflineMap();
			for(Offline o: list){
				String key=o.getWeek()+""+o.getPeriod();
				offlineMap.get(key).add(o);
			}
		}
		return doctorOfflineVOList;
	}

	@Override
	public List<Map<String,Object>> queryByConditionsForWeb(OfflineParam param) {

		List<Offline> allOfflineList=offlineDao.queryByConditionsForWeb(param);
		
		List<String> keyList=new ArrayList<>();
		//根据医生，对医生排班数据分组
		Map<String,List<Offline>> doctorOfflineMap=new TreeMap<String,List<Offline>>();
		for(Offline o : allOfflineList){
			User user=userManager.getUser(o.getDoctorId());
			String userKey=user.getUserId()+","+user.getName()+","+user.getDoctor().getHospital()+","+user.getDoctor().getDepartments()+","+user.getDoctor().getTitle();
			if(doctorOfflineMap.containsKey(userKey)){
				doctorOfflineMap.get(userKey).add(o);
			}else{
				List<Offline> doctorOfflineList=new ArrayList<Offline>();
				doctorOfflineList.add(o);
				doctorOfflineMap.put(userKey, doctorOfflineList);
				keyList.add(userKey);
			}
		}
		//每个医生的排班信息，按照排班开始时间进行排序
		for(String key:doctorOfflineMap.keySet()){
			List<Offline> list=doctorOfflineMap.get(key);
			list.sort(new Comparator<Offline>() {
				@Override
				public int compare(Offline o1, Offline o2) {
					return (int) (o1.getStartTime()-o2.getEndTime());
				}
				
			});
		}
		
		List<Map<String,Object>> dataList=new ArrayList<>();
		for(String str : keyList){
			Map<String,Object> map=new HashMap<>();
			map.put("doctor", str);
			map.put("offlineList", doctorOfflineMap.get(str));
			dataList.add(map);
		}
		//return doctorOfflineMap;
		return dataList;
	}

	@Override
	public Boolean hasOffline(OfflineParam param) {
		OfflineParam newParam=new OfflineParam();
		Calendar dateTime=Calendar.getInstance();
		dateTime.setTimeInMillis(param.getDateTime());
/*		dateTime.set(Calendar.HOUR_OF_DAY, 0);
		dateTime.set(Calendar.MINUTE, 0);
		dateTime.set(Calendar.SECOND, 0);
		dateTime.set(Calendar.MILLISECOND, 0);
		newParam.setDateTime(dateTime.getTimeInMillis());*/
		
		Calendar startTime=Calendar.getInstance();
		startTime.setTimeInMillis(param.getStartTime());
		startTime.set(Calendar.YEAR, dateTime.get(Calendar.YEAR));
		startTime.set(Calendar.MONTH, dateTime.get(Calendar.MONTH));
		startTime.set(Calendar.DAY_OF_MONTH, dateTime.get(Calendar.DAY_OF_MONTH));
		newParam.setStartTime(startTime.getTimeInMillis());
		
		Calendar endTime=Calendar.getInstance();
		endTime.setTimeInMillis(param.getEndTime());
		endTime.set(Calendar.YEAR, dateTime.get(Calendar.YEAR));
		endTime.set(Calendar.MONTH, dateTime.get(Calendar.MONTH));
		endTime.set(Calendar.DAY_OF_MONTH, dateTime.get(Calendar.DAY_OF_MONTH));
		newParam.setEndTime(endTime.getTimeInMillis());
		
		newParam.setDoctorId(param.getDoctorId());

		Long count=offlineDao.queryhasOffline(newParam);
		return count>0 ? true : false;
	}
	
	
	/*public static void main(String[] args){
		Calendar cal= Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND,0);
		cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);//设置为本周周一
        Date date=new Date(cal.getTimeInMillis());
        System.out.println(date);
	}*/
	
}
