package com.dachen.health.user.listener;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dachen.commons.asyn.event.EcEvent;
import com.dachen.commons.asyn.event.EventType;
import com.dachen.commons.asyn.event.annotation.EcEventListener;
import com.dachen.commons.asyn.event.annotation.EcEventMapping;
import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.util.QiniuUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.util.StringMap;
import com.qiniu.util.UrlSafeBase64;

/**
 * @deprecated by chengwei 2017-10-25：目前不需要生成缩略图，7牛提供的样式可以满足需求。
 * @author Administrator
 */
//@Component("UserUpdateListener")
//@EcEventListener
public class UserUpdateListener {
	private static final Logger logger = LoggerFactory.getLogger(UserUpdateListener.class);
	
	@Resource
	private IQrCodeService qrCodeService;

//    @EcEventMapping(type = {EventType.UserPicUpdated})
    public void fireUserPicUpdatedEvent(EcEvent event) throws Exception {
    	logger.debug("send event {},{}",event.getType().getQueueName(),event.toJSONString());
    	// 1、生成用户二维码，使用try...catch...，避免失败影响生成缩略图
//    	try {
//    		Integer userId = event.param("userId");
//        	Integer userType = event.param("userType");
//    		String url = qrCodeService.modifyUserQr(userId+"", userType+"");
//    		logger.info("用户二维码url：{}", url);
//    	} catch (Exception e) {
//    		logger.error("修改头像时重新生成二维码失败", e);
//    	}
    	
    	// 2、生成头像缩略图
    	String bucket = event.param("bucket");
    	String srcKey = event.param("key");
    	
    	String newKey = srcKey +"_small";
		String fops = "imageView2/5/h/100";
		OperationManager operater = QiniuUtil.getOperationManager();
		/**
		 * 处理结果通知接收URL，七牛将会向你设置的URL发起 Content-Type: application/json的POST请求。
		 * 请参考http://developer.qiniu.com/docs/v6/api/reference/fop/pfop/pfop.html#pfop-notification
		 */
		String notifyURL = "";
		
		/**
		 * 强制执行数据处理。
		 * 当服务端发现fops指定的数据处理结果已经存在，那就认为已经处理成功，避免重复处理浪费资源。加上本字段并设为1，则可强制执行数据处理并覆盖原结果。
		 */
		boolean force = true;
		
		StringMap params = new StringMap().putNotEmpty("notifyURL", notifyURL)
		        .putWhen("force", 1, force).putNotEmpty("pipeline", "thumbnail");

		fops += "|saveas/" + UrlSafeBase64.encodeToString(bucket+ ":" + newKey);

	    try {
	    	operater.pfop(bucket, srcKey, fops, params);
	    } catch (QiniuException e) {
	        Response r = e.response;
	        // 请求失败时简单状态信息
			for (int i = 0; i < 3; i++) {
				try {
					String id = operater.pfop(bucket, srcKey, fops, params);
					if (id.contains("persistentId")) {
						break;
					}
				} catch (QiniuException ex) {
					logger.error("UserUpdateListener: for{}" + ex.response.toString());
					continue;
				}
	        }
	        logger.error("UserUpdateListener: " + r.toString());
	    }
    }
}
