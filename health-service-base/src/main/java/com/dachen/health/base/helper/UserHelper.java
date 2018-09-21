package com.dachen.health.base.helper;

import com.dachen.health.commons.constants.UserEnum;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.QiniuUtil;
import com.dachen.util.StringUtil;

public class UserHelper {
	/**
	 * 
	 * @author 李淼淼
	 * @date 2015年8月25日
	 */
	public static String buildHeaderPicPath(String headPicFileName, Integer userId) {
//		if (StringUtil.isEmpty(headPicFileName)) {
//			headPicFileName = "/avatar/" + PropertiesUtil.getContextProperty("defalut.headerfile");
//		} else {
//			headPicFileName = addAvatarPrefix(userId, headPicFileName);
//		}
//		return PropertiesUtil.addUrlPrefix(headPicFileName);
		if (StringUtil.isEmpty(headPicFileName)) {
			// 改成七牛默认地址
			return QiniuUtil.DEFAULT_AVATAR();
		} else {
			if (headPicFileName.contains("http://")) {
				return headPicFileName;
			} else if (headPicFileName.contains("/")) {
				return PropertiesUtil.addUrlPrefix(headPicFileName);
			} else {
				return PropertiesUtil.addUrlPrefix("/avatar/o/" + (userId % 10000) + "/"+ headPicFileName);
			}
		}
	}

	/**
	 * 
	 * @param userId
	 * @param headPicFileName
	 * @return
	 */
	public static String addAvatarPrefix(Integer userId, String headPicFileName) {
		if (StringUtil.isEmpty(headPicFileName))
			return headPicFileName;
		//用户头像有两个方式更新：1、更新用户的个人资料；2、更新关系为本人的患者头像
		//第1中方式中headPicFileName存放的数据格式为14532882703916.png
		//第2中方式中headPicFileName存放的数据格式为af/201601/avatar/5dc75c05fcff45aa853d03fc538ec9ae.png
		if (!headPicFileName.contains(PropertiesUtil.getHeaderPrefix()) && !headPicFileName.contains("/")) {
			headPicFileName = "/avatar/o/" + (userId % 10000) + "/" + headPicFileName;
		}
		return headPicFileName;
	}
	
	/**
	 * </p>获取集团头像</p>
	 * @param groupId
	 * @param userId
	 * @return
	 * @author fanp
	 * @date 2015年9月28日
	 */
	public static String buildCertPath(String groupId, Integer userId) {
	    String baseName = String.valueOf(userId % 10000);
        String subName = String.valueOf(userId);

        //cert/683/100683/55e46911b522252fc2083c78/groupLogo.jpg
        String path="/cert/"+baseName+"/"+subName+"/"+groupId+"/groupLogo.jpg";
        
        return PropertiesUtil.addUrlPrefix(path);
    }
	
	public static String buildHeaderPicPath(String headPicFileName, Integer userId, Integer sex, Integer userType) {
		if (StringUtil.isEmpty(headPicFileName)) {
			// 改成七牛默认地址
			
			if ((null != userType) && (userType.intValue() == UserEnum.UserType.doctor.getIndex())) {
				if (null != sex) {
					return (2 == sex) ? QiniuUtil.DEFAULT_AVATAR_FEMALE() : QiniuUtil.DEFAULT_AVATAR_MEN();
				}else {
					return QiniuUtil.DEFAULT_AVATAR_MEN();
				}
			}
			return QiniuUtil.DEFAULT_AVATAR();
		} else {
			if (headPicFileName.contains("http://")) {
				return headPicFileName;
			} else if (headPicFileName.contains("/")) {
				return PropertiesUtil.addUrlPrefix(headPicFileName);
			} else {
				return PropertiesUtil.addUrlPrefix("/avatar/o/" + (userId % 10000) + "/"+ headPicFileName);
			}
		}
	}
	
}
