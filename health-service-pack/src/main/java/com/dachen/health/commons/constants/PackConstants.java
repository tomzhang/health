package com.dachen.health.commons.constants;

import com.dachen.util.PropertiesUtil;

/**
 * Pack 参数配置
 * @author xiepei
 * @date 2015年8月17日
 * @version1.0.0
 */
public class PackConstants {
	
	/**
	 * 支付宝收款回调地址
	 */
	public static final String ALIPAY_CALL_BACK="alipay.callback.link";
	
	/**
	 * 支付宝有密退款回调地址
	 */
	public static final String ALIPAY_REFUND_CALL_BACK="alipay.refund.callback.link";
	
	/**
	 * 微信收款回调地址
	 */
	public static final String WXPAY_CALL_BACK="wxpay.callback.link";
	
	/**
	 * 交易成功
	 */
	public static final String AIL_TRADE_SUCCESS="TRADE_SUCCESS";
	
	/**
	 * 交易完成
	 */
	public static final String AIL_TRADE_FINISHED="TRADE_FINISHED";
	
	/**
	 * 交易等待付款
	 */
	public static final String AIL_WAIT_BUYER_PAY="WAIT_BUYER_PAY";
	/**
	 * 医生端跳转
	 */
	public static final String MESAGE_GO_APP_DOC_URL="msg.open.aocapp";
	/**
	 * 患者端跳转
	 */
	public static final String MESAGE_GO_APP_PAT_URL="msg.open.patapp";
	/**
	 * 医生助手端跳转
	 */
	public static final String MESAGE_GO_APP_DAS_URL="msg.open.dasapp";
	
	/**
	 * 医生端跳转（博德嘉联）
	 */
	public static final String MESAGE_GO_APP_DOC_URL_BD="msg.open.docapp.bdjl";
	/**
	 * 患者端跳转（博德嘉联）
	 */
	public static final String MESAGE_GO_APP_PAT_URL_BD="msg.open.patapp.bdjl";
	
	//msg.open.app=mobile/#/common/openApp/doc?
	//msg.open.app=mobile/#/common/openApp/pat?
	private static String PATH_URL="msg.open.url";
	

	/**
	 * url样例：http://120.24.94.126/mobile/#/common/openApp/doc?msgId=dc0e30a16fea40a28a20cca374b1efd8&msgType=1
	 * msgType=1，为拉起到app会话界面，此时msgId=会话id
	 * msgType=2，为拉起到app订单界面，此时msgId=订单id
	 * msgType=3，为拉起到app订单界面，此时msgId=订单id
	 */
	public static String greneartenURL(String msgType,String msgId,Integer userType) {
		//跳到会话聊天界面
		if (msgType.equals("1")) {
			return generateAppLink("1", msgId, userType);
		}
		// 跳到订单详情页面
		if (msgType.equals("2")) {
			return generateAppLink("2", msgId, userType);
		}
		// 跳到订单详情页面
		if (msgType.equals("3")) {
			return generateAppLink("3", msgId, userType);
		} else {
			return null;
		}
	}
	private static String generateAppLink(String msgType, String msgId, int userType) {
		String path = PropertiesUtil.getContextProperty(PATH_URL);
		String openAppUrl = null;
		if (UserEnum.UserType.doctor.getIndex() == userType) {
			openAppUrl = PropertiesUtil.getContextProperty(MESAGE_GO_APP_DOC_URL);
		} else if(UserEnum.UserType.patient.getIndex() == userType){
			openAppUrl = PropertiesUtil.getContextProperty(MESAGE_GO_APP_PAT_URL);
		} else{
			openAppUrl = PropertiesUtil.getContextProperty(MESAGE_GO_APP_DAS_URL);
		}
		path += openAppUrl;
		return path + "msgId=" + msgId + "&msgType=" + msgType;
	}
	
}


