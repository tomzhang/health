package com.dachen.health.group.common.service;

import java.util.Map;

import com.dachen.health.group.common.entity.vo.QrCodeInfo;

/**
 * 
 * @author pijingwei
 * @date 2015/8/19
 * 二维码登录接口
 */
public interface QrCodeLoginService {
	
	/**
     * </p>根据uuid获取是否登录成功的信息</p>
     * @param uuid	
     * @return Map<String, Object>
     * @author pijingwei
     * @date 2015年8月19日
     */
	Map<String, Object> getDoctorInfoByCheck(String uuid);
	
	/**
     * </p>验证用户信息</p>
     * @param doctorId
     * @return 
     * @author pijingwei
     * @date 2015年8月19日
     */
	@Deprecated //业务逻辑不正确,暂时废弃，add by wangqiao
	void verifyLogin(QrCodeInfo codeInfo);
	
}
