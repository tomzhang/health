package com.dachen.pub.util;


public class PubUtils {
//	public static final String PUB_DOCTOR_TEAM="pub_001";
//	public static final String PUB_PATIENT_TEAM="pub_002";
//	public static final String PUB_NURSE_TEAM="pub_003";
//	public static final String PUB_COMPANY_TEAM="pub_010";
	public static final int NEWS_MSG_MAX_COUNT=10;//医生动态
	public static final String PUB_NEWS_DOCTOR="pub_news_001";//医生动态
	public static final String PUB_NEWS_PATIENT="pub_news_002";//患者动态
	
	public static final String PUB_DOC_PREFIX="pub_doctor_";//医生公众号
	
	public static final String PUB_GROUP_NEWS="pub_pub_";//集团新闻
	
	public static final String PUB_PATIENT_VOICE="pub_voice_";//患者之声
	
	public static final String BDJL="bdjl";//博德嘉联

	public static final String PUB_DEPT = "pub_dept_";//科室公众号

	public static boolean isPubGroupNews(String groupId)
	{
		return groupId!=null && groupId.startsWith(PUB_GROUP_NEWS);
	}
	
	public static boolean isPubPatientVoice(String groupId)
	{
		return groupId!=null && groupId.startsWith(PUB_PATIENT_VOICE);
	}
	
	public static boolean isDoctorPub(String pubId)
	{
		return pubId!=null && pubId.startsWith(PUB_DOC_PREFIX);
	}
	
	public static String getPubIdByMid(int userType,String mid){
		String pid = null;
		if(userType==3){
			pid=PUB_GROUP_NEWS+mid;
		}
		else if(userType==1){
			pid=PUB_PATIENT_VOICE+mid;
		}
		return pid;
	}
	/**
	 * 
	 * @param terminal see {@link UserEnum.Terminal}}
	 * @return
	 */
	public static String getClient(Integer terminal){
		if(terminal!=null && terminal==2){
			return BDJL;
		}
		return "";
	}
	
	public static Integer getTerminal(String client){
		if(BDJL.equalsIgnoreCase(client)){
			return 2;
		}
		return 1;
	}
}
