package com.dachen.util;

import com.alibaba.fastjson.JSON;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.KeyBuilder;
import com.dachen.commons.constants.Constants;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.support.spring.SpringBeansUtils;
import com.dachen.commons.user.UserSessionService;
import com.dachen.medice.vo.AccessConfig;

import java.util.Objects;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class ReqUtil implements InitializingBean {

    @Autowired
    private Auth2Helper auth2Helper;

    private final static String IPHONE = "iPhone";
    private final static String IOS = "iOS";
    private final static String ANDROID = "android";
    
    public static ReqUtil instance;
    private ReqUtil() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        instance = this;
    }
    public AccessToken getTokenInfo() {
        AccessToken accessToken = JSON.parseObject(getRequest().getHeader("token"), AccessToken.class);
        return accessToken;
    }

    /**
     * 获取当前登录用户id
     *
     * @author 李淼淼
     * @date 2015年9月6日
     */
    public int getUserId() {
        AccessToken accessToken = getTokenInfo();
        return accessToken == null ? 0 : accessToken.getUserId();
    }

    public static Integer getCurrentUserId() {
        HttpServletRequest request = getRequest();
        if (request != null && request.getHeader("userID") != null) {
            return Integer.valueOf(request.getHeader("userID"));
        }
        return null;
    }

    /**
     * 获取当前用户类型
     * @return
     */
    public int getUserType() {
        return getTokenInfo().getUserType();
    }

    public int getUserId(String token) {
        return this.getUserIdFromAuth(token);
    }
    /**
     * 根据token 直接从auth服务中获取用户id
     * @author 钟良
     * @date 2017年10月10日
     */
    public int getUserIdFromAuth(String token) {
        if (StringUtil.isBlank(token) || token.startsWith("guest_")) {
            return 0;
        }
        AccessToken accessToken = auth2Helper.getTokenInfo(token);
        return accessToken == null ? 0 : accessToken.getUserId();
    }

    /**
     * @author 李淼淼
     * @date 2015年9月7日
     */
    public UserSession getUser() {
        UserSession session = null;
        try {
            session = getUser(getUserId());
        } catch (Exception e) {
            // TODO: handle exception
        }
        return session;
    }

    /**
     * @author 李淼淼
     * @date 2015年9月7日
     */
    public UserSession getUser(Integer userId) {
        UserSessionService userSessionService = SpringBeansUtils.getBean(UserSessionService.BEAN_ID);
        return userSessionService.getUserSession(userId);
    }

    /**
     * @author 李淼淼
     * @date 2015年9月1日
     */
    public static HttpServletRequest getRequest() {
        if (RequestContextHolder.getRequestAttributes() == null) {
            return null;
        }
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * @author wangl
     * @date 2016年6月8日11:35:40
     */
    public static boolean isBDJL() {
        boolean isBDJL = false;
        HttpServletRequest req = getRequest();
        if (req != null) {
            String header = req.getHeader("User-Agent");
            isBDJL = StringUtils.containsIgnoreCase(header, "bdjl");
            if (!isBDJL) {
                String webAgent = req.getHeader("Web-Agent");
                isBDJL = StringUtils.containsIgnoreCase(webAgent, "bdjl");
            }
        }
        return isBDJL;
    }

    public static String getVersion() {
        HttpServletRequest req = getRequest();
        if (req != null) {
            String header = req.getHeader("User-Agent");
            if (header != null) {
                String[] agent = StringUtils.split(header, "/");
                if (agent != null && agent.length > 1 && StringUtil.isNotBlank(agent[1])) {
                    return agent[1];
                }
            }
        }
        return null;
    }

    /**
     * @author wangl
     * @date 2016年6月8日11:35:40
     */
    public static boolean isMedicalCircle() {
        boolean isMedicalCircle = false;
        HttpServletRequest req = getRequest();
        if (Objects.nonNull(req)) {
            String header = req.getHeader("User-Agent");

            if (StringUtils.containsIgnoreCase(header, "MedicalCircle") || StringUtils.containsIgnoreCase(header, "medicalcircle")) {
                isMedicalCircle = true;
            }

            if (!isMedicalCircle) {
                String webAgent = req.getHeader("Web-Agent");
                if (StringUtils.containsIgnoreCase(webAgent, "MedicalCircle") || StringUtils.containsIgnoreCase(webAgent, "medicalcircle")) {
                    isMedicalCircle = true;
                }
            }
        }
        return isMedicalCircle;
    }

    /**
     * 是否来自微信服务号H5调用
     *
     * @return
     * @author xieping
     */
    public static boolean isFromWechat() {
        HttpServletRequest req = getRequest();
        if (req != null) {
            String webAgent = req.getHeader("Web-Agent");
            return StringUtils.containsIgnoreCase(webAgent, "Wechat");
        }
        return false;
    }


    /**
     * 请求 token 获取
     *
     * @author 李淼淼
     * @date 2015年9月1日
     */
    public String getToken() {
        if (getRequest() == null) {
            return "";
        }
        return getTokenInfo().getToken();
    }

    /**
     * 获取ip
     *
     * @author 李淼淼
     * @date 2015年9月8日
     */
    public static String getRequestIp() {
        HttpServletRequest request = getRequest();
        String requestIp = request.getHeader("x-forwarded-for");

        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getHeader("X-Real-IP");
        }
        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getHeader("Proxy-Client-IP");
        }
        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getRemoteAddr();
        }
        if (requestIp == null || requestIp.isEmpty() || "unknown".equalsIgnoreCase(requestIp)) {
            requestIp = request.getRemoteHost();
        }

        return requestIp;
    }

    /**
     * 获取游客的id
     *
     * @return
     * @author liming
     */
    public static int getGuestUserId(String guest_token) {
        String userId = RedisUtil.get(KeyBuilder.getCacheKey(Constants.CacheKeyPre.GuestSession, guest_token));

        return StringUtils.isEmpty(userId) ? 0 : 1;
    }

    public static boolean isAndroidAccess() {
        return StringUtils.containsIgnoreCase(getRequest().getHeader("User-Agent"), ANDROID);
    }

    public static boolean isIOSAccess() {
        return StringUtils.containsIgnoreCase(getRequest().getHeader("User-Agent"), IOS)
                || StringUtils.containsIgnoreCase(getRequest().getHeader("User-Agent"), IPHONE);
    }

    public static String getAccessFlagString() {
        return isIOSAccess() ? "ios" : isAndroidAccess() ? "android" : "";
    }

    /**
     * 获取账号登录方式
     * tel ：移动端电话登录
     * wechat : 移动端微信登录
     * web_account ： 非移动端的操作
     */
    public static String getAccountLoginType() {
        String accountType = getRequest().getHeader("account-type");
        if (StringUtils.isBlank(accountType))
            accountType = "web_account";
        return accountType;
    }

    /**
     * 移动端登录时候
     * 会有设备的序列号
     * 非移动端操作 统一使用 "web_serial_num"
     *
     * @return
     */
    public static String getSerialNumber() {
        String serialNum = getRequest().getHeader("serial-number");
        if (StringUtils.isBlank(serialNum))
            serialNum = "web_serial_num";
        return serialNum;
    }
    
    public HeaderInfo getHeaderInfo() {
        HttpServletRequest req = getRequest();
        HeaderInfo info = new HeaderInfo();
        if(req == null)
            return info;
        String mobileAgent = req.getHeader("User-Agent");
        String webAgent = req.getHeader("Web-Agent");
        if(StringUtil.isNotBlank(webAgent)){
            String[] arr = webAgent.split("/");
            if(arr.length > 3){
                info.setAppName(arr[2]);
                info.setDeviceType(arr[3]);
            }else{
//                throw new ServiceException(ErrorCode.Agent_error.getIndex(), ErrorCode.Agent_error.getTitle());
            }
            return info;
        }
        if(StringUtil.isNotBlank(mobileAgent)){
            String[] arr = mobileAgent.split("/");
            if(arr.length > 3){
                info.setAppName(arr[2]);
                info.setDeviceType(arr[3]);
            }else{
//                throw new ServiceException(ErrorCode.Agent_error.getIndex(), ErrorCode.Agent_error.getTitle());
            }
            return info;
        }
        return info;
    }

    /**
     * 是否允许接入
     */
    public static boolean isAllowAccess(AccessConfig accessConfig) {
    	if(accessConfig == null) {
    		//无配置默认跳出
    		return true;
    	}
    	
    	HttpServletRequest req = getRequest();
    	
        if ( Objects.nonNull(req) ) {
        	String userAgent = req.getHeader("User-Agent");
            String webAgent = req.getHeader("Web-Agent");
            String headerOrigin = req.getHeader("Origin");
            
            //判断agent是否合规
            if(StringUtil.isNotEmpty(accessConfig.getAgent())) {
            	String [] agentArray = accessConfig.getAgent().split(",");
            	 for(String agent : agentArray) {
                	 if (StringUtils.containsIgnoreCase(userAgent, agent) || StringUtils.containsIgnoreCase(userAgent, agent.toLowerCase())) {
                         return true;
                     }else if (StringUtils.containsIgnoreCase(webAgent, agent) || StringUtils.containsIgnoreCase(webAgent, agent.toLowerCase())) {
                		 return true;
                     }
                }
            }
            
            //判断origin 是否合规
            if(StringUtil.isNotEmpty(accessConfig.getOrigin())) {
            	String [] originArray = accessConfig.getOrigin().split(",");
            	for(String origin : originArray) {
                  	 if (StringUtils.containsIgnoreCase(headerOrigin, origin)) {
                           return true;
                       }
                  }
            }
            
        }
        return false;
    }

    /**
     * 发送验证码验证签名
     * @param phone 电话号码
     * @param sign 签名
     * @param accessConfig 接入配置
     * @return
     */
    public static boolean valiSMSSign(String phone, String sign, AccessConfig accessConfig) {
    	if(accessConfig == null || StringUtil.isEmpty(accessConfig.getSignPrefix())) {
    		//无配置默认报错
    		return false;
    	}
    	
    	if(StringUtil.isEmpty(phone) || StringUtil.isEmpty(sign)) {
    		//参数有误
    		return false;
    	}
    	
    	String signPrefix = accessConfig.getSignPrefix();
    	String expectSign = DigestUtils.md5Hex(signPrefix+phone);
    	//验证签名
    	if(expectSign.equals(sign.toLowerCase())) { //全小写MD5
    		return true;
    	}
    	
        return false;
    }
}
