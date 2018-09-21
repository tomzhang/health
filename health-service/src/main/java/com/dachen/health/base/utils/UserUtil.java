package com.dachen.health.base.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.health.user.UserInfoNotify;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.RedisUtil;

public class UserUtil {

	//医生主页地址H5 + doctorId
//	public static final String DOCTOR_PAGE_URL = PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/doctorDetails/";
    public static final String DOCTOR_PAGE_URL() {
      return PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/doctorDetails/";
    }

    //集团主页地址H5 + groupId
//	public static final String GROUP_PAGE_URL = PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/groupDetails/";
    public static final String GROUP_PAGE_URL() {
      return PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/groupDetails/";
    }

    //医生注册地址：siginAndSigup/doctorId/packId/packType/code
//	public static final String DOC_SIGIN_URL = PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/siginAndSigup/{0}/0/0/0";
    public static final String DOC_SIGIN_URL() {
        return PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/siginAndSigup/{0}/0/0/0";
    }
	
//	//患者注册地址：singup/userType/code
//	public static final String PAT_SIGIN_URL = PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/siginAndSigup/1/0/patient009/0";
//    public static final String PAT_SIGIN_URL() {
//        return PropertiesUtil.getContextProperty("wechat.mp") + "/wechat/web/#/siginAndSigup/1/0/patient009/0";
//    }

	/**
	 * 清空用户所有token
	 * @param userId
	 * @author tan.yf
	 * @date 2016年6月4日
	 */
	public static void clearUserTokens(int userId) {
		String pattern = KeyBuilder.getCacheKey(Constants.CacheKeyPre.SessionUserId,userId+"_*");
		Set<String> userIdKeys =  RedisUtil.getKeysByPattern(pattern);
		List<String> tokenList = new ArrayList<String>();
		if(userIdKeys != null && userIdKeys.size() > 0){
			Iterator<String> ite = userIdKeys.iterator();
			while(ite.hasNext()){
				String cacheUserIdKey = ite.next();
				String access_token = RedisUtil.get(cacheUserIdKey);
				tokenList.add(access_token);
				String cacheKey = KeyBuilder.getCacheKey(Constants.CacheKeyPre.Session, access_token);
				RedisUtil.del(cacheUserIdKey , cacheKey);
			}
		}
		if (!tokenList.isEmpty()) {
			UserInfoNotify.clearUserToken(tokenList);
		}
	}
}
