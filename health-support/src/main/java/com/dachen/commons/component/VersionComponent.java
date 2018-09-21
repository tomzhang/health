package com.dachen.commons.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dachen.commons.constants.ErrorCode;
import com.dachen.commons.exception.ServiceException;
import com.dachen.im.server.enums.AppEnum;
import com.dachen.util.DateUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;

@Component
public class VersionComponent {

	private static final Logger logger = LoggerFactory.getLogger(VersionComponent.class);
	private static String iphone = "iPhone";
	private static String ios = "iOS";
	private static String android = "android";
	private static String iOS_minVersion ;
	private static String android_minVersion ;
	private static String versionRegex = "^\\d{1,}(\\.\\d{1,})*";
	private static Long bdjlPatientTimeEnd ;
	private static Long bdjlDoctorTimeEnd ;
	private static boolean needTimeLimit ;
	
	private static Map<String, String> iOS_VersionMap = new HashMap<String, String>();
	private static Map<String, String> android_VersionMap = new HashMap<String, String>();
	
	static {
        String tag = "static";
		String patinetTimeStr = PropertiesUtil.getContextProperty("bdjl.beta1.patient.endtime");
		String doctorTimeStr = PropertiesUtil.getContextProperty("bdjl.beta1.doctor.endtime");
        logger.info("{}. patinetTimeStr={}, doctorTimeStr={}", tag, patinetTimeStr, doctorTimeStr);

		bdjlPatientTimeEnd = DateUtil.parseStringToTimestamp(patinetTimeStr);
		bdjlDoctorTimeEnd = DateUtil.parseStringToTimestamp(doctorTimeStr);
		needTimeLimit = bdjlPatientTimeEnd > 0 || bdjlDoctorTimeEnd > 0;
        logger.info("{}. needTimeLimit={}", tag, needTimeLimit);
		
		iOS_minVersion = PropertiesUtil.getContextProperty("iOS.minVersion");
		android_minVersion = PropertiesUtil.getContextProperty("android.minVersion");
        logger.info("{}. iOS_minVersion={}, android_minVersion={}", tag, iOS_minVersion, android_minVersion);
		
		initVersionMap(iOS_minVersion, iOS_VersionMap);
		initVersionMap(android_minVersion, android_VersionMap);
	}
	
	private static void initVersionMap(String versionStr, Map<String, String> versionMap) {
		if (StringUtil.isNotBlank(versionStr)) {
			String[] versions = versionStr.split(",");
			if (versions == null || versions.length == 0)
				return;
			for (String version : versions) {
				if (version.contains(":")) {
					String[] packageVersion = version.split(":");
					if (packageVersion != null && packageVersion.length == 2) {
						versionMap.put(packageVersion[0].toLowerCase(), packageVersion[1]);
					}
				}
			}
		}
	}
	
	
	public boolean requestProcess(HttpServletRequest request) throws Exception {
        String tag = "requestProcess";
		String header = request.getHeader("User-Agent");
        logger.info("{}. header={}", tag, header);
		
		/**
		 * 时限控制
		 */
		if(needTimeLimit && (StringUtils.contains(header, "_BDJL_beta1") || StringUtils.contains(header, "_BDJL_beta"))){
			timeLimitHandler(header);
		}
		
		/**
		 * appName控制
		 */
//		validateAppName(header);
		/**
		 * 版本控制
		 */
		if (StringUtils.containsIgnoreCase(header, iphone) || StringUtils.containsIgnoreCase(header, ios)
				|| StringUtils.containsIgnoreCase(header, android)) {
			return versionHandler(header);
		}
		return true;
	}
	
	
	private void validateAppName(String header) {
	    String appName = parseHeaderString(header , "appName");
	    List<String> list = new ArrayList<>();
	    for(AppEnum item : AppEnum.values()){
	        list.add(item.value());
	    }
	    if(!list.contains(appName))
	        throw new ServiceException(ErrorCode.appName_error.getIndex(), ErrorCode.appName_error.getTitle());
	}


    private boolean timeLimitHandler(String header) {
		String packageName = parseHeaderString(header , "package");
		logger.info("timeLimitHandler. header={}, packageName={}" + packageName);
		long now = System.currentTimeMillis();
		if(bdjlDoctorTimeEnd > 0 && StringUtils.startsWith(packageName, "DGroupDoctor") && bdjlDoctorTimeEnd < now){
			throw new ServiceException(ErrorCode.time_over.getIndex(), ErrorCode.time_over.getTitle());
		}
		if(bdjlPatientTimeEnd > 0 && StringUtils.startsWith(packageName, "DGroupPatient") && bdjlPatientTimeEnd < now){
			throw new ServiceException(ErrorCode.time_over.getIndex(), ErrorCode.time_over.getTitle());
		}
		return true;
	}

	private boolean versionHandler(String header) {
        // User-Agent：DGroupPatient/1.9/010/ios/iphone
		String versionStr = parseHeaderString(header, "version");
		logger.info("versionHandler. header={}, version={}", header, versionStr);
		if (StringUtils.isNotBlank(versionStr) && versionStr.matches(versionRegex)) {
			String packageStr = parseHeaderString(header, "package");
			if (StringUtils.containsIgnoreCase(header, android)) {
				versionHandler(android_VersionMap, versionStr, packageStr);
			} else {
				versionHandler(iOS_VersionMap, versionStr, packageStr);
			}
		}
		return true;
	}

	private void versionHandler(Map<String, String> versionMap, String versionStr, String packageStr) {
		if (versionMap.containsKey(packageStr.toLowerCase())) {
			String[] minVersionArr = versionMap.get(packageStr.toLowerCase()).split("\\.");
			String[] clientVersionArr = versionStr.split("\\.");
			int conpareValue = compareTo(minVersionArr, clientVersionArr);
			if (conpareValue > 0) {
				throw new ServiceException(ErrorCode.version_no_match.getIndex(), ErrorCode.version_no_match.getTitle());
			}
		}
	}

	private String parseHeaderString(String header , String type) {
		String str = null;
		if(header != null){
			String[] arr = header.split("/");
			if(StringUtils.equals(type, "version")){
				if(arr.length > 1)
					str = arr[1].trim();
			}
			if(StringUtils.equals(type, "package")){
				str = arr[0].trim();
			}
			if(StringUtils.equals(type, "appName")){
			    if(arr.length > 2)
                    str = arr[2].trim();
            }
		}
		return str;
	}
	
	private int compareTo(String a[] , String b[]) {
		int len1 = a.length;
        int len2 = b.length;
        int max = Math.max(len1, len2);
        int k = 0;
        while (k < max){
        	String str1 = k > (len1-1) ? "0" : a[k];
        	String str2 = k > (len2-1) ? "0" : b[k];
        	Integer v1 , v2;
        	try{
        		v1 = Integer.parseInt(str1);
        	}catch(Exception e){
        		v1 = 0;
        	}
        	try{
        		v2 = Integer.parseInt(str2);
        	}catch(Exception e){
        		v2 = 0;
        	}
//        	int t = compareTo(str1,str2);
        	int t = v1.compareTo(v2);
        	if( t != 0){
        		return t ;
        	}
        	k++;
        }
		return 0;
	}
	
	private int compareTo(String a , String b) {
        int len1 = a.length();
        int len2 = b.length();
        int max = Math.max(len1, len2);
        char v1[] = a.toCharArray();
        char v2[] = b.toCharArray();
        int k = 0;
        while (k < max) {
            char c1 = k > (len1-1) ? '0' : v1[k];
            char c2 = k > (len2-1) ? '0' : v2[k];
            if (c1 != c2) {
                return c1 - c2;
            }
            k++;
        }
        return 0;
    }
	
	
}
