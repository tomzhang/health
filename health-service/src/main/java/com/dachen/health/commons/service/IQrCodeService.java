package com.dachen.health.commons.service;

import com.dachen.health.base.entity.po.QrScanParamPo;
import java.util.Map;

import com.dachen.health.commons.entity.QrCodeParam;

public interface IQrCodeService {

	String generateUserQr(String userId, String userType);

	String generateQr(String id, String type);
	
	String generateQr(String id, String type, Integer doctorId);
	
	/**
	 * 生产圈子/科室二维码
	 */
	String generateQr(String circleId, String logo, String inviteUrl);
	
	String modifyUserQr(String userId, String userType);

    String generateSignUpImage(String call_back_url, String call_back_param);
    
    /**
     * 二维码生成接口
     * @param param
     *      生成参数
     * @return 二维码的图片地址
     */
    String generateQrCode(QrCodeParam param);
    
    /**
     * 解析二维码扫描数据
     * @param param
     *      解析参数
     */
    Map<String, Object> scanQrCode(QrCodeParam param);

    QrScanParamPo getQrScanParam(String paramId);

    String getUrlByOid(String oid);
    
    /**
	 * 生成会议二维码
	 */
	String generateMeetingActivityQr(String circleId, String logo,String type);
}
