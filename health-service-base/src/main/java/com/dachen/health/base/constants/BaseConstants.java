package com.dachen.health.base.constants;

import com.dachen.util.PropertiesUtil;

public class BaseConstants {
	
	//短信签名：玄关对应ext为1；博德嘉联对应的为3
	public static final String XG_SIGN = "【玄关健康】";
	public static final String BD_SIGN = "【博德嘉联】";
	
	//短信模板替换内容
	public static final String XG_DOC_APP = "玄关医生";
	public static final String XG_DOC_MEDICAL_CIRCLE = "玄关健康";
	public static final String XG_PLATFORM = "玄关健康";
	public static final String BD_DOC_APP = "博德嘉联医生集团掌上医院";
    public static final String XG_YSQ_APP = "医生圈";

    // 院校基础信息更新
    public static final String COLLEGE_INFO_CHANGE = "COLLEGE_INFO_CHANGE";
	
	//拉起医生端链接
//	public static final String XG_OPEN_DOC = PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/common/openApp/doc";
    public static final String XG_OPEN_DOC() {
        return PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/common/openApp/doc";
    }

//	public static final String BD_OPEN_DOC = PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/bdjl/openApp/doc";
    public static final String BD_OPEN_DOC() {
        return PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/bdjl/openApp/doc";
    }
	
	//拉起患者端链接
//	public static final String XG_OPEN_PAT = PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/common/openApp/pat";
    public static final String XG_OPEN_PAT() {
        return PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/common/openApp/pat";
    }

//	public static final String BD_OPEN_PAT = PropertiesUtil.getContextProperty("health.server")+"/health/web/mobile/#/bdjl/openApp/pat";
	
	//患者端下载地址
//	public static final String XG_DOWN_PAT = PropertiesUtil.getContextProperty("health.server")+"/health/web/app/downPatient.html";
    public static final String XG_DOWN_PAT() {
        return PropertiesUtil.getContextProperty("health.server")+"/health/web/app/downPatient.html";
    }

//	public static final String BD_DOWN_PAT = PropertiesUtil.getContextProperty("health.server")+"/health/web/bd_h5/app/downPatient.html";
    public static final String BD_DOWN_PAT() {
        return PropertiesUtil.getContextProperty("health.server")+"/health/web/bd_h5/app/downPatient.html";
    }
	
	//附近医生，搜索半径（单位KM）
//	public static final String GEO_NEAR_MAXDISTANCE = PropertiesUtil.getContextProperty("geonear.maxDistance");
    public static final String GEO_NEAR_MAXDISTANCE() {
        return PropertiesUtil.getContextProperty("geonear.maxDistance");
    }
}
