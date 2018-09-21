package com.dachen.line.stat.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.alibaba.fastjson.JSON;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.data.request.PushMessageRequest;
import com.dachen.line.stat.entity.vo.NurseDutyTime;

/**
 * 工具类
 * 
 * @author weilit
 *
 */
public class Helper {

	private static Map<String, String> getValueMap(Object bean) {

		Map<String, String> map = new HashMap<String, String>();
		// System.out.println(obj.getClass());
		// 获取f对象对应类中的所有属性域
		Field[] fields = bean.getClass().getDeclaredFields();
		for (int i = 0, len = fields.length; i < len; i++) {
			String varName = fields[i].getName();
			try {
				// 获取原来的访问控制权限
				boolean accessFlag = fields[i].isAccessible();
				// 修改访问控制权限
				fields[i].setAccessible(true);
				// 获取在对象f中属性fields[i]对应的对象中的变量
				Object o = fields[i].get(bean);
				if (o.getClass().isArray()) {
					map.put(varName, Arrays.toString((Object[]) o));
				} else {
					map.put(varName, o.toString());
				}
				// System.out.println("传入的对象中包含一个如下的变量：" + varName + " = " + o);
				// 恢复访问控制权限
				fields[i].setAccessible(accessFlag);
			} catch (IllegalArgumentException ex) {
				ex.printStackTrace();
			} catch (IllegalAccessException ex) {
				ex.printStackTrace();
			}
		}
		return map;
	}

	/**
	 * 讲数值转换成map数组
	 * 
	 * @param bean
	 * @return
	 */
	public static List<Map<String, String>> getValueMapList(
			List<? extends Object> bean) {

		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		try {
			if (null != bean && bean.size() > 0) {
				for (Object object : bean) {
					Map<String, String> map = getValueMap(object);
					listMap.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listMap;
	}

	/**
	 * 讲数值转换成map数组
	 * 
	 * @param bean
	 * @return
	 */
	public static List<Map<String, Object>> getServiceTimeResultList(
			List<NurseDutyTime> dutyList) {

		List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
		try {
			if (null != dutyList && dutyList.size() > 0) {
				for (NurseDutyTime object : dutyList) {
					Map<String, Object> map = new HashMap<String, Object>();
					String day = object.getTime();
					map.put("week",
							DateUtils.getWeekOfDate(DateUtils.toDate(day)));
					map.put("day", DateUtils.formatDate2Str(
							DateUtils.toDate(day), DateUtils.FORMAT_MM_DD));
					map.put("status", object.getStatus());
					map.put("time", object.getTime());
					listMap.add(map);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return listMap;
	}

	/**
	 * 推送app消息
	 * 
	 * @param content
	 * @param userIds
	 */
	public static JSON push(String content, List<String> userIds) {
		PushMessageRequest msg = new PushMessageRequest();
		msg.setContent(content);
		Set<String>userSet = new HashSet<String>();
		userSet.addAll(userIds);
		msg.setPushUsers(userSet);
		msg.setBizType(0);
		JSON result = MsgHelper.push(msg);
		
		return result;
	}
}
