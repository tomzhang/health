package com.dachen.util;

public class BusinessUtil {

	public static String getSexName(Integer sex) {
		if (sex == null) {
			return "";
		}
		if (sex == 1)
			return "男";
		else if (sex == 2)
			return "女";
		else
			return "保密";
	}

	/**
	 * 判断医生是否免费,true:收费，false：免费
	 * 
	 * @param data
	 * @param groupDoctor
	 */
	public static boolean getDoctorIsFee(Long dutyDuration, Long taskDuration, Integer outpatientPrice) {
		if (outpatientPrice == null || outpatientPrice == 0) {
			return false;
		} else {
			if (taskDuration != null && dutyDuration != null) {
				// 如果已值班时长>=任务时长，则收费
				if (dutyDuration >= taskDuration) {
					return true;
				} else {
					return false;
				}
			} else {
				if (dutyDuration == null && taskDuration != null) {
					return false;
				} else {
					return true;
				}
			}
		}
	}
}
