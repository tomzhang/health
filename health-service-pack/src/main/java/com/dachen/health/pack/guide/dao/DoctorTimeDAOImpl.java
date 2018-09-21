package com.dachen.health.pack.guide.dao;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO.Remark;
import com.dachen.health.pack.guide.entity.po.DoctorTimePO.Time;

@Repository
public class DoctorTimeDAOImpl extends NoSqlRepository implements IDoctorTimeDAO{
	
	public DoctorTimePO getDoctorTime(Integer doctorId)
	{
		Query<DoctorTimePO> uq=dsForRW.createQuery(DoctorTimePO.class)
				.filter("_id", doctorId);
		DoctorTimePO info = uq.get();
		if(info!=null && info.getTimeList()!=null)
		{
			List<Time>timeList = info.getTimeList();
			if(timeList.size()>0)
			{
				long currentTime = System.currentTimeMillis();
				Iterator<Time>iter = timeList.iterator();
				while(iter.hasNext())
				{
					Time time = iter.next();
					if(time.getEnd()<currentTime)
					{
						iter.remove();
					}
				}
			}
		}
		sortTimeList(info);
		return info;
	}
	
	public DoctorTimePO removeDoctorTime(Integer doctorId,Long start,Long end)
	{
		long currentTime = System.currentTimeMillis();
		Query<DoctorTimePO> uq=dsForRW.createQuery(DoctorTimePO.class)
				.filter("_id", doctorId);
		DoctorTimePO info = uq.get();
		if(info==null)
		{
			return null;
		}
		List<Time>timeList = null;
		if(info!=null && info.getTimeList()!=null)
		{
			timeList = info.getTimeList();
			if(timeList.size()>0)
			{
				
				Iterator<Time>iter = timeList.iterator();
				while(iter.hasNext())
				{
					Time time = iter.next();
					if(start.longValue() == time.getStart())
					{
						iter.remove();
					}
					else if(time.getEnd()<currentTime)
					{
						iter.remove();
					}
				}
				dsForRW.save(info);
			}
		}
		sortTimeList(info);
		return info;
	}
	
	public DoctorTimePO addDoctorTime(Integer doctorId,Long start,Long end)
	{
		long currentTime = System.currentTimeMillis();
		if(start < currentTime)
		{
			throw new ServiceException("预约的开始时间已经过时。");
		}
		if(end==null)
		{
			end = start+30*60*1000L;//默认结束时间在开始时间上加30分钟
		}
		Query<DoctorTimePO> uq=dsForRW.createQuery(DoctorTimePO.class)
				.filter("_id", doctorId);
		DoctorTimePO info = uq.get();
		List<Time>timeList = null;
		if(info==null)
		{
			info = new DoctorTimePO();
			info.setDoctorId(doctorId);
			timeList = new ArrayList<Time>();
			timeList.add(new Time(start,end,0));
			info.setTimeList(timeList);
			dsForRW.insert(info);
			return info;
		}
		if(info!=null && info.getTimeList()!=null)
		{
			timeList = info.getTimeList();
			if(timeList.size()>0)
			{
				
				Iterator<Time>iter = timeList.iterator();
				while(iter.hasNext())
				{
					Time time = iter.next();
					if(start.longValue() == time.getStart())
					{
						throw new ServiceException("时间重复。");
					}
					else if(time.getEnd()<currentTime)
					{
						iter.remove();
					}
				}
			}
		}
		else
		{
			timeList = new ArrayList<Time>();
		}
		timeList.add(new Time(start,end,0));
		info.setTimeList(timeList);
		dsForRW.save(info);
		sortTimeList(info);
		return info;
	}
	
	public int addCount(Integer doctorId,Long start,Long end)
	{
		Query<DoctorTimePO> uq=dsForRW.createQuery(DoctorTimePO.class)
				.filter("_id", doctorId);
		DoctorTimePO info = uq.get();
		List<Time>timeList = null;
		int count = 0;
		if(info!=null && info.getTimeList()!=null)
		{
			timeList = info.getTimeList();
			if(timeList.size()>0)
			{
				
				Iterator<Time>iter = timeList.iterator();
				while(iter.hasNext())
				{
					Time time = iter.next();
					if(start.longValue() == time.getStart())
					{
						count = time.getCount()+1;
						time.setCount(count);
						dsForRW.save(info);
						return count;
					}
				}
			}
		}
		return count;
	}
	
	private void sortTimeList(DoctorTimePO info)
	{
		if(info==null || info.getTimeList()==null)
		{
			return;
		}
		List<Time>timeList = info.getTimeList();
		if(timeList.isEmpty())
		{
			return;
		}
		//按照日期倒叙排序
		timeList.sort(new Comparator<Time>(){
			@Override
			public int compare(Time o1, Time o2) {
				return o1.getStart().compareTo(o2.getStart());
			}
			
		});
	}

	@Override
	public DoctorTimePO addDoctorRemark(Integer doctorId, String remark,String guideId,String guideName) {
		long currentTime = System.currentTimeMillis();
		Query<DoctorTimePO> uq=dsForRW.createQuery(DoctorTimePO.class)
				.filter("_id", doctorId);
		DoctorTimePO info = uq.get();
		Remark mark = new Remark();
		List<Remark> remarks =null;
		if(null==info){
			info = new DoctorTimePO();
			remarks = new ArrayList<Remark>();
			mark.setCreateTime(currentTime);
			mark.setGuideId(guideId);
			mark.setGuideName(guideName);
			mark.setRemark(remark);
			remarks.add(mark);
			info.setRemarkList(remarks);
			info.setDoctorId(doctorId);
			dsForRW.insert(info);
			return info;
		}
		remarks = info.getRemarkList();
		if(remarks==null){
			remarks = new ArrayList<Remark>();
		}
		mark.setCreateTime(currentTime);
		mark.setGuideId(guideId);
		mark.setGuideName(guideName);
		mark.setRemark(remark);
		remarks.add(mark);
		info.setRemarkList(remarks);
		dsForRW.save(info);
		sortTimeList(info);
		return info;
	}

	@Override
	public DoctorTimePO getDoctorRemark(Integer doctorId) {
		Query<DoctorTimePO> uq=dsForRW.createQuery(DoctorTimePO.class)
				.filter("_id", doctorId);
		DoctorTimePO info = uq.get();
		sortTimeList(info);
		return info;
	}
}
