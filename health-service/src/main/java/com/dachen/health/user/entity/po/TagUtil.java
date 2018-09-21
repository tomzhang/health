package com.dachen.health.user.entity.po;

import java.util.Arrays;
import java.util.List;

import org.dom4j.IllegalAddException;

public class TagUtil {

	public static final String CHECK_IN = "患者报到";
	public static final String MESSAGE = "图文咨询";
	public static final String PHONE = "电话咨询";
	public static final String HEALTH_CARE = "健康关怀";
	public static final String OUTPATIENT = "在线门诊";
	public static final String CONSULTATION = "会诊套餐";
	public static final String INACTION = "未激活";
	public static final String appointment = "预约订单";
	public static final String feeBill = "收费单";
	public static final String INTEGRAL = "积分问诊";

	public static final List<String> SYS_TAG = Arrays.asList(new String[] {CHECK_IN,MESSAGE,PHONE,HEALTH_CARE,OUTPATIENT,INACTION,CONSULTATION,appointment,feeBill, INTEGRAL});
	
	public static String getSysTagName(Integer packType) {
    	String tagName = null;
    	switch (packType) {
    	case 0:
    		tagName = CHECK_IN;
    		break;
    	case 1:
    		tagName = MESSAGE;
    		break;
    	case 2:
    		tagName = PHONE;
    		break;
    	case 3:
    		tagName = HEALTH_CARE;
    		break;
    	case 4:
    		tagName = OUTPATIENT;
    		break;
    	case 8:
    		tagName = CONSULTATION;
    		break;
    	case 9:
    		tagName = appointment;
    		break;
    	case 10:
    		tagName = feeBill;
    	case 12:
    		tagName = INTEGRAL;
    		break;
    	default:
    		throw new IllegalAddException("argument invalid");
    	}
    	return tagName;
    }
    
}
