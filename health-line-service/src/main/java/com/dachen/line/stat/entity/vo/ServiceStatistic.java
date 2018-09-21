package com.dachen.line.stat.entity.vo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.mongodb.morphia.annotations.NotSaved;

import com.dachen.line.stat.util.ConfigUtil;
import com.dachen.line.stat.util.Constant;
import com.dachen.util.PropertiesUtil;

/**
 * 
 * @author pijingwei
 * @date 2015/8/13
 */
public class ServiceStatistic {

	/**
	 * 今天加入人数
	 */
	private @NotSaved String todayAddNum;

	/**
	 * 共接待人数 这里需要做配置定时任务 每一个小时增加10
	 */
	private int totalReceptionNum=0;

	/**
	 * 标签描述
	 */
	private @NotSaved List<Map<String,Object>> content = new ArrayList<Map<String,Object>>();

	public String getTodayAddNum() {
		todayAddNum = ConfigUtil.getRandomArrayItem(Constant.TODAY_ADDIN_NUM);
		return todayAddNum;
	}

	public void setTodayAddNum(String todayAddNum) {
		this.todayAddNum = todayAddNum;
	}


	public int getTotalReceptionNum() {
		String tep = Constant.totalReceptionNum();
		if(StringUtils.isNotEmpty(tep))
		{	
			totalReceptionNum = Integer.parseInt(tep);
		}
		return totalReceptionNum;
	}

	public void setTotalReceptionNum(int totalReceptionNum) {
		this.totalReceptionNum = totalReceptionNum;
	}

	public List<Map<String,Object>> getContent() {
		List<NurseStatistic> contentList = new ArrayList<NurseStatistic>();
		for (int i = 0; i < 20; i++) {
			NurseStatistic object = new NurseStatistic();
			object.setArea(ConfigUtil.getRandomArrayItem(Constant.CITY));
			object.setName(ConfigUtil.getRandomArrayItem(Constant.NAME_XIN)+ ConfigUtil.getRandomArrayItem(Constant.NAME_CHENHU));
			object.setService(ConfigUtil.getRandomArrayItem(Constant.SERVICE_TYPE));
			object.setTime( ConfigUtil.getRandomArrayItem(Constant.SERVICE_TIME));
			object.setUserImage(ConfigUtil.getRandomArrayItem(getUserImage()));
			contentList.add(object);
		}
		System.out.println(contentList);
		// 按照时间排序
		Collections.sort(contentList, new Comparator<NurseStatistic>() {
			public int compare(NurseStatistic arg0, NurseStatistic arg1) {
				int result = 0;
				int time1 = Integer.parseInt(arg0.getTime());
				int time2 = Integer.parseInt(arg1.getTime());
				if (time1 < time2) {
					result = -1;
				}
				return result;
			}
		});
		for (NurseStatistic object : contentList) {
			Map<String,Object> map = new HashMap<String, Object>();
			map.put("image", object.getUserImage());
			map.put("setContent", object.getLineService());
			content.add(map);
		}
		return content;
	}
	
	public String[] getUserImage()
	{
			 String[] USERIMAGES = new String[6];
			 
			 for(int i =0;i<6;i++)
			 {	 
				 String userIamge=PropertiesUtil.getHeaderPrefix() + "/default/nurse/nurse_head0"+i+".png";
				 USERIMAGES[i]=userIamge;
			 }
			
			return USERIMAGES;
	}
	
	
	public void setContent(List<Map<String, Object>> content) {
		this.content = content;
	}

	public static void main(String[] args) {
		ServiceStatistic s = new ServiceStatistic();
		System.out.println(s.getTodayAddNum()+","+s.getTotalReceptionNum()+","+s.getContent());
	}
}
