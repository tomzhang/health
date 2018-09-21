package com.dachen.line.stat.util;

import com.dachen.util.PropertiesUtil;

public class Constant {

    public static String totalReceptionNum() {
        return PropertiesUtil
                .getContextProperty("nurse.total.reception.num");
    }
	
	//多少加入的随机数目
	public static String[] TODAY_ADDIN_NUM = new String[] { "29", "29", "29",
			"29", "29", "29" };
    
	/**
	 * 姓氏
	 */
	public static String[] NAME_XIN = new String[] { "赵", "钱", "孙", "李", "周",
			"吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦",
			"尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶",
			"姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章" };
    
	/**
	 * 称呼后缀
	 */
	public static String[] NAME_CHENHU = new String[] { "先生", "女士" };
    
	/**
	 * 服务类型
	 */
	public static String[] SERVICE_TYPE = new String[] { "就医直通车", "专家直通车", "检查直通车" };
    
	/**
	 * 随机服务时间
	 */
	public static String[] SERVICE_TIME = new String[] { "2", "10", "8", "5" };

    /**
     * 随机城市
     */
	public static String[] CITY = new String[] { "北京", "天津", "上海", "重庆", "北京",
			"天津", "沈阳", "大连", "济南", "青岛", "南京", "上海", "杭州", "武汉", "广州", "深圳",
			"香港", "澳门", "重庆", "成都", "西安", "哈尔滨" };
	
	/**
	 * 系统短信
	 */
	public static Integer SYSTEM_MESSAGE = 1000;

	// 患者支付成功
//	public static String MESSAGE_PATIENT_PAY_ORDER_SUCCESS = PropertiesUtil
//			.getContextProperty("message.patient.pay.order.success");
    public static String MESSAGE_PATIENT_PAY_ORDER_SUCCESS() {
      return PropertiesUtil
                .getContextProperty("message.patient.pay.order.success");
    }

	// 患者下单之后系统推送给护士的短信末班
//	public static String MESSAGE_PATIENT_PLACE_ORDER_SUCCESS = PropertiesUtil
//			.getContextProperty("message.patient.place.order.success");
    public static String MESSAGE_PATIENT_PLACE_ORDER_SUCCESS() {
        return PropertiesUtil
              .getContextProperty("message.patient.place.order.success");
    }

	// 护士抢单成功之后，给患者推送的短信末班
//	public static String MESSAGE_NURSE_GRAP_ORDER_SUCCESS = PropertiesUtil
//			.getContextProperty("message.nurse.grap.order.success");
    public static String MESSAGE_NURSE_GRAP_ORDER_SUCCESS() {
        return  PropertiesUtil
                .getContextProperty("message.nurse.grap.order.success");
    }

	// 护士开始服务给护着发送的短信的末班
//	public static String MESSAGE_NURSE_START_THE_SERVICE = PropertiesUtil
//			.getContextProperty("message.nurse.start.the.service");
    public static String MESSAGE_NURSE_START_THE_SERVICE() {
        return PropertiesUtil
                .getContextProperty("message.nurse.start.the.service");
    }

	// 护士上传了检查结果之后给患者发送的短信
//	public static String MESSAGE_NURSE_UPLOAD_CHECK_RESULT = PropertiesUtil
//			.getContextProperty("message.nurse.upload.check.result");
    public static String MESSAGE_NURSE_UPLOAD_CHECK_RESULT() {
        return PropertiesUtil
                .getContextProperty("message.nurse.upload.check.result");
    }

	// 患者结束评价之后，给护士的短信提醒
//	public static String MESSAGE_PATIENT_END_APPRAISE_THE_SERVICE = PropertiesUtil
//			.getContextProperty("message.patient.end.appraise.the.service");
    public static String MESSAGE_PATIENT_END_APPRAISE_THE_SERVICE() {
      return PropertiesUtil
              .getContextProperty("message.patient.end.appraise.the.service");
    }

	// 患者取消订单接口末班
//	public static String MESSAGE_PATIENT_CANCLE_ORDER_SUCCESS = PropertiesUtil
//			.getContextProperty("message.patient.cancle.order.success");
    public static String MESSAGE_PATIENT_CANCLE_ORDER_SUCCESS() {
      return PropertiesUtil
              .getContextProperty("message.patient.cancle.order.success");
    }

	// 客服电话
//	public static final String CUSTOMER_SERVICE_TELEPHONE = PropertiesUtil
//			.getContextProperty("nurse.customer.service.telephone");
    public static final String CUSTOMER_SERVICE_TELEPHONE() {
        return PropertiesUtil
                .getContextProperty("nurse.customer.service.telephone");
    }


	// 上传结果之后的app链接 护士端app链接 患者端短信链接 修改成短连接

	// 客服电话
//	public static final String MESSAGE_PATIENT_TIME_OVERTOP = PropertiesUtil
//			.getContextProperty("message.patient.time.overtop");
    public static final String MESSAGE_PATIENT_TIME_OVERTOP() {
        return PropertiesUtil
                .getContextProperty("message.patient.time.overtop");
    }

	//超时时间
	public static final int MESSAGE_PATIENT_TIME = 2;

	// 订单兜底时间
//	public static final String PATIENT_ORDER_EXCEPTION_TIME = PropertiesUtil
//			.getContextProperty("patient.order.exception.time");
    public static final String PATIENT_ORDER_EXCEPTION_TIME() {
      return PropertiesUtil
              .getContextProperty("patient.order.exception.time");
    }

	// 订单兜底 提示信息
//	public static final String PATIENT_ORDER_EXCEPTION_CANCEL = PropertiesUtil
//			.getContextProperty("patient.order.exception.cancel");
    public static final String PATIENT_ORDER_EXCEPTION_CANCEL() {
      return PropertiesUtil
              .getContextProperty("patient.order.exception.cancel");
    }

//	// 设置关闭护士订单的天数（前提是此次服务已经结束但是患者没有对护士的服务进行评价）
//	public static final String NURSE_ORDER_NO_EVALUATE_DAY = PropertiesUtil
//			.getContextProperty("nurse.order.no.evaluate.day");
//    public static final String NURSE_ORDER_NO_EVALUATE_DAY() {
//      return PropertiesUtil
//              .getContextProperty("nurse.order.no.evaluate.day");
//    }

	// 护士抢单之后未给患者打电话 您已成功
//	public static final String NURSE_ORDER_NO_SENDMESSAGE = PropertiesUtil
//			.getContextProperty("nurse.order.no.sendmessage");
    public static final String NURSE_ORDER_NO_SENDMESSAGE() {
        return PropertiesUtil
                .getContextProperty("nurse.order.no.sendmessage");
    }

//	// 上传结果之后的app链接
//	public static String APP_UPLOAD_CHECK_RESULT_LINK = PropertiesUtil
//			.getContextProperty("app.upload.check.result.link");
//    public static String APP_UPLOAD_CHECK_RESULT_LINK() {
//        return PropertiesUtil
//                .getContextProperty("app.upload.check.result.link");
//    }

	// 医患平台服务器地址
//	public static final String OUT_SERVER_URL = PropertiesUtil
//			.getContextProperty("out.server.url");
    public static final String OUT_SERVER_URL() {
      return PropertiesUtil
              .getContextProperty("out.server.url");
    }

	// public static final String OUT_SERVER_URL ="http://120.24.94.126:8091/";

	// 检查直通车
//	public static final String CHECK_UP_PRODUCT_ID = PropertiesUtil
//			.getContextProperty("check.up.product.id");
    public static final String CHECK_UP_PRODUCT_ID() {
      return PropertiesUtil
              .getContextProperty("check.up.product.id");
    }

	// 给患者发短信的前缀   护士发来信息:
//	public static final String MESSAGE_NURSE_TO_PATIENT = PropertiesUtil
//				.getContextProperty("message.nurse.to.patient");
    public static final String MESSAGE_NURSE_TO_PATIENT() {
      return PropertiesUtil
              .getContextProperty("message.nurse.to.patient");
    }

	
}
