package com.dachen.health.commons.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jsoup.helper.StringUtil;

import com.dachen.health.pack.order.entity.vo.ServiceItemVO;

public class ServiceItemUtil {

	public static String toString(List<String> serviceItemIds) {
		if (serviceItemIds == null || serviceItemIds.isEmpty()) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (String serviceItemId : serviceItemIds) {
			sb.append(serviceItemId).append(",");
		}
		sb.delete(sb.length()-1, sb.length());
		return sb.toString();
	}
	
	public static List<String> toList(String serviceItemId) {
		if (StringUtil.isBlank(serviceItemId)) {
			return null;
		}
		return Arrays.asList(serviceItemId.split(","));
	}
	
	public static List<ServiceItemVO> parseString(String serviceItemId) {
		if (StringUtil.isBlank(serviceItemId)) {
			return null;
		}
		
		List<ServiceItemVO> vos = new ArrayList<ServiceItemVO>();
		String[] itemIds = serviceItemId.split(",");
		for (String itemId : itemIds) {
			ServiceItemVO vo = new ServiceItemVO();
			if (itemId.indexOf("#") != -1) {
				vo.setId(itemId.split("#")[0]);
				vo.setCount(Integer.valueOf(itemId.split("#")[1]));
			} else {
				vo.setId(itemId);
				vo.setCount(1);
			}
			vos.add(vo);
		}
		return vos;
	}
}

