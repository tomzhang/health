package com.dachen.commons;

import com.dachen.commons.constants.Constants;
import com.dachen.commons.lock.RedisLock.LockType;
import com.dachen.util.ReqUtil;

public class KeyBuilder {
    public static final String HotMsgIdListTemplate = "msg.id.hot.list:%1$s";

    public static final String HotMsgListTemplate = "msg.hot.list:%1$s";

    public static final String LatestMsgIdListTemplate = "msg.id.latest.list:%1$s";

    public static final String LatestMsgListTemplate = "msg.latest.list:%1$s";

    public static final String UserHotMsgIdTemplate = "user.msg.hot:%1$s";

    public static final String UserLatestMsgIdTemplate = "user.msg.latest:%1$s";

    public static final String SMSRandCodePrefix = "password_reset_rand_code:%1$s";
    
    public static final String LockPrefix = "lock:%1$s:%2$s";
    
    public static final String GROUP_DOCTOR_ONLINE = "group_doctor_online:%1$s";
    
    public static final String GROUP_DOCTOR_ONLINE_COUNT= "group_doctor_online_count:%1$s";

    public static final String isNewAccountToken = "is_new_account_token:%1$s";

    public static final int isNewAccountTokenActiveTime = 5 * 60; //seconds

    /**
	 * 电话咨询订单缓存 id:{map{订单服务状态，开发服务时间，接单导医Id}}
	 */
	public final static String H_ORDER_CACHE ="h_consult_order";
	
	/**
	 * 导医-患者会话对应的当前订单（电话咨询服务订单）
	 * key:会话Id（类似于guide_患者Id）；value：订单Id（电话咨询服务订单）
	 * groupId:id
	 */
	public final static String S_ORDER_GROUP ="s_order_group";
	
	/**
	 * 导医接单池(缓存所有的待接单数据{目前有 电话订单、预约名医订单}：state = 未开始服务)；score：订单创建时间（患者下单时间）
	 */
	public final static String Z_ORDER_POOL ="z_order_pool"; 
	
	public final static String Z_PATIENT_MESSAGE_TO_GUIDE_POOL ="z_patient_message_to_guide_pool:";
	
	/**
	 * 缓存订单预约时间
	 * 1、当患者支付成功的时候，往此缓存增加数据：key：z_schedule_pool:guide；score：预约时间；member：guideId+":"+订单Id
	 * 2、当导医结束服务的时候，删除对应的数据：key：z_schedule_pool:guide；score：预约时间；member：guideId+":"+订单Id
	 */
	public final static String Z_SCHEDULE_POOL ="z_schedule_pool";

	/**
	 * 图文咨询消息条数控制
	 */
	public static final String MESSAGE_REPLY_COUNT = "h_message_reply_count";
	
    // user:%1$s
    // user:id:%1$s
    // user:access_token:%1$s
    /**
     * 根据telephone ,userType生成获取accessToken的键
     * 
     * @param telephone
     * @return uk_${telephone}_${userType}
     */
    public static String atKey(String telephone, Integer userType) {
        return String.format("uk_%1$s_%2$d", telephone, userType);
    }

    /**
     * 根据accessToken生成获取userId的键
     * 
     * @param accessToken
     * @return at_${accessToken}
     */
    public static String userIdKey(String accessToken) {
        return String.format("at_%1$s", accessToken);
    }

    /**
     * 根据userId生成获取用户数据的键
     * 
     * @param userId
     *            用户Id
     * @return
     */
    public static String userKey(Integer userId) {
        return String.format("u_%1$s", userId);
    }

    /**
     * </p>获取缓存可以</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月4日
     */
    public static String getCacheKey(String cacheKeyPre, String token) {
        return String.format("%1$s:%2$s", cacheKeyPre, token);
    }

    /**
     * </p>获取缓存可以</p>
     * 
     * @return
     * @author fanp
     * @date 2015年7月4日
     */
    public static String getCacheKey(String cacheKeyPre) {
        return getCacheKey(cacheKeyPre, ReqUtil.instance.getRequest().getParameter("access_token"));
    }
    

    public static String getSMSRandCodeKey(String id) {
        return String.format(SMSRandCodePrefix, id);
    }
    
    /**
     * 根据user_id生成获取userId的键
     * @param userId
     * @return user_id_${userId}
     */
    public static String userIdKey(Integer userId) {
        return String.format("user:%1$s", userId);
    }
    
    /**
     * 根据type生成获取lock的键
     * @param type
     * @param pk
     * @return lock:{type}:{pk}
     */
    public static String buildLockKey(LockType type,String pk) {
        return String.format(LockPrefix, type.name(),pk);
    }
    
    
    /**
     * 根据groupId生成获取OnLineKey的键
     * @param groupId
     * @return group_doctor_online:{groupId}
     */
    public static String buildGroupOnLineKey(String groupId) {
        return String.format(GROUP_DOCTOR_ONLINE,groupId);
    }
    
    /**
     * 根据groupId生成获取OnLineKey的键
     * @param groupId
     * @return group_doctor_online:{groupId}
     */
    public static String buildGroupOnLineCountKey(String groupId) {
        return String.format(GROUP_DOCTOR_ONLINE_COUNT,groupId);
    }

    public static String userIdTokenKey(Integer userId , String serialNum , String accountType){
    	return Constants.CacheKeyPre.USERID_TOKEN+userId+":"+serialNum+":"+accountType;
    }

    public static String getIsNewAccountTokenKey(String key) {
        return String.format(isNewAccountToken, key);
    }

    public static void main(String[] args) {
		System.out.println(userIdKey(999999999));
	}

}
