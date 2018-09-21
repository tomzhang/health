package com.dachen.health.controller.system;

import org.springframework.web.bind.annotation.RequestMapping;

import com.dachen.commons.JSONMessage;

/**
 * 该类没有实际业务代码，只用于api的辅助描述
 * 
 * @author Administrator
 *
 */
public class DocHelpController {
	
	/**
	 * 
	 * @api {[get,post]} / 短信拉起app url
	 * @apiVersion 1.0.0
	 * @apiName getGreneartenURL
	 * @apiGroup help
	 * @apiDescription 短信拉起app参数描述(PackConstants.java)
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *             url样例：医生端：http://120.24.94.126/mobile/#/common/openApp/doc
	 *                             ?msgId=dc0e30a16fea40a28a20cca374b1efd8&msgType=1
	 *                      患者端：http://120.24.94.126/mobile/#/common/openApp/pat
	 *                             ?msgId=dc0e30a16fea40a28a20cca374b1efd8&msgType=1
	 *			   msgType=1，为拉起到app会话界面，此时msgId=会话id
	 *             msgType=2，为拉起到app订单界面，此时msgId=订单id
	 *             msgType=3，为拉起到app订单界面，此时msgId=订单id
	 * 
	 * @apiAuthor 屈军利
	 */
	@RequestMapping(value = "/getGreneartenURL")
	public JSONMessage getGreneartenURL() {
		return new JSONMessage();
	}

	/**
	 * 
	 * @api {[get,post]} / 通知描述
	 * @apiVersion 1.0.0
	 * @apiName getNotification
	 * @apiGroup help
	 * @apiDescription 通知组和类型的描述
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *             所有通知会在大轮询方法中获取通知个数，然后进入某个通知会话组后，调用获取消息接口获取未读通知
	 * 
	 *             目前通知组有以下： FRIEND_DOCTOR("GROUP_0001","医生好友"),
	 *             FRIEND_PATIENT("GROUP_0003","患者好友"),
	 *             TODO_NOTIFY("GROUP_002","待办列表");
	 * 
	 *             具体通知类型用通知中的param中的type区分，目前有以下type：
	 * 
	 *             ADD_FRIEND("1","新的好友"), DEL_FRIEND("2","删好友"),
	 *             PROFILE_CHANGE("3","个人资料变化"),
	 *             PROFILE_INVITE_COMPANY("4","公司管理员邀请函"),
	 *             PROFILE_INVITE_GROUP("5","集团管理员邀请函"),
	 *             PROFILE_INVITE_DOCTOR("6","加入集团邀请函"),
	 *             PROFILE_INVITE_CREATE("7","创建集团邀请函"),
	 *             FRIEND_REQ_CHANGE("8","新的好友");
	 * 
	 * @apiAuthor 屈军利
	 */
	@RequestMapping(value = "/getNotification")
	public JSONMessage getNotification() {
		return new JSONMessage();
	}

	/**
	 * 
	 * @api {[get,post]} / 指令描述
	 * @apiVersion 1.0.0
	 * @apiName getEvent
	 * @apiGroup help
	 * @apiDescription 指令类型的描述
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *             所有指令会在轮询方法中获取，指令类型如下:
	 * 
	 *             ADD_FRIEND("1","新的好友"), 
	 *             DEL_FRIEND("2","删好友"),
	 *             PROFILE_CHANGE("3","个人资料变化"), 
	 *             GROUP_ADD_DOCTOR("4","加入医生集团"),
	 *             GROUP_DELETE_DOCTOR("5","离开医生集团"),
	 *             GROUP_CHANGEDEPT_DOCTOR("6","医生集团医生科室变动"),
	 * 			   DOCTOR_ONLINE("7","医生上线"),
	 *			   DOCOTR_OFFLINE("8","医生下线"),
	 *			   DOCTOR_OFFLINE_SYSTEM_FORCE("9","医生被系统强制下线"),
	 *
	 *             GROUP_ADDUSER("10","增加群组成员"), 
	 *             GROUP_DELUSER("11","删除群组成员"),
	 *             GROUP_QUIT("12","退出群聊"), 
	 *             GROUP_CHANGE_NAME("13","修改群组名称"),
	 *             GROUP_CHANGE_PIC("14","修改群组头像"),
	 * 
	 *             ORDER_CHANGE_STATUS("15","订单状态修改");
	 *             
	 *				DOCTOR_DISTURB_ON("16","医生开启免打扰"),
	 *				DOCTOR_DISTURB_OFF("17","医生关闭免打扰"),
	 *             GROUP_DUTY_TIME_CHANGE("18","集团值班时间改变"),
	 *             NEW_CHECKIN("19","新的患者报到")
	 *              INVITE_JOIN_ROOM("20","邀请加入视频/电话会议"),
 	 *               REFUSE_JOIN_ROOM("21","拒绝加入视频/电话会议"),
 	 *               CANCEL_MEETING("22","取消视频/电话会议"),
	 *            	GUIDE_ORDER_HANDLE("23","导医订单处理"),
	 *            	CONSULTATION_FRIEND_APPLY("24","会诊好友申请"),
	 *            	VIDEO_PHONE_IS_BUSY("25","视频电话占线");客户端使用
	 *             DRUG_REMIND("26","用药提醒"),
	 *  			DOCTOR_STATUS_CHANGE("27","医生认证状态变化"),
	 *             GROUP_SKIP_CHANGE("28","医生集团屏蔽状态变化"),
     * 				IM_V_CHAT_INVITE("30","发起视频聊天邀请"),
     * 				IM_V_CHAT_REJECT("31","拒绝视频"),
     * 				IM_V_CHAT_BUSY("32","忙线中"),
     * 				IM_V_CHAT_CALLER_CANCEL("33","发起人取消视频"),
     * 				IM_V_CHAT_ADD_USER("34","添加用户到视频聊天中"),
     * 				IM_MESSAGE_WITHDRAW ("36","IM消息撤回");
     *              SYNERG_VISIT_F5 ("37","选择协同组刷新页面"),
	 *				SYNERG_VISIT_ADD ("38","协同拜访组新成员加入"),
	 *              SYNERG_VISIT_CONFIRM ("39","协同拜访组确认"),
	 *              SYNERG_VISIT_CANCEL("40","协同拜访组被删除"),
	 *              SYNERG_VISIT_CANCELUSER("41","协同组成员退出"),
	 *              NEW_DYNAMIC("42","新的动态"),
	 *              COMMUNITY_NEW_DYNAMIC("43","医生社区新动态"),
	 *              MESSAGE_REPLY_COUNT_CHANGE("44","消息回复次数变化");
	 *              MY_CARD_ADD("29","我的卡券增加");
	 * 
	 * @apiAuthor 屈军利
	 */
	@RequestMapping(value = "/getEvent")
	public JSONMessage getEvent() {
		return null;
	}

	/**
	 * 
	 * @api {[get,post]} /CommonUploadServlet 通用文件上传
	 * @apiVersion 1.0.0
	 * @apiName CommonUploadServlet
	 * @apiGroup 文件
	 * @apiDescription 通用文件上传接口
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *             通用上传文件 URL：/upload/ CommonUploadServlet，支持多文件
	 * 
	 *             实际端口为9000，而不是8091
	 *             请求参数： 参数名 参数说明 是否必填 path 
	 *             路径： 
	 *             checkin 患者报到 
	 *             order 订单生成
	 *             curerecord 诊疗记录 
	 *             avatar 患者头像
	 *             article 文章封面图片
	 *             groupcert 集团认证图片
	 *             checkbill 检查单图片
	 *             casepatient 电子病历图片
	 *             
	 * 
	 *             注意：此接口是调用文件服务器接口 请求参数： 参数名 参数说明 是否必填 path 路径 Y 接口返回： 成功： {
	 *             "data": {
	 * 
	 *             " datas ": [ { "oFileName":
	 *             "c710fce990f45176620fe8e22e395040.jpg", "oUrl":
	 *             "http://192.168.3.7:8081/u/123/100123/201507/o/c710fce990f45176620fe8e22e395040.jpg"
	 *             , "status": 1, "tUrl":
	 *             "http://192.168.3.7:8081/u/123/100123/201507/t/c710fce990f45176620fe8e22e395040.jpg"
	 *             , "height": 62, "width": 99 } ] }, "failure": 0,
	 *             "resultCode": 1, "success": 1, "total": 1 }
	 * 
	 *             失败：{ "resultCode": 1010101, "resultMsg": "缺少上传文件" }
	 * 
	 * 
	 * @apiAuthor 李淼淼
	 */
	public JSONMessage CommonUploadServlet() {
		return null;
	}

	/**
	 * 
	 * @api {[get,post]} /commonDelFile 通用文件删除
	 * @apiVersion 1.0.0
	 * @apiName commonDelFile
	 * @apiGroup 文件
	 * @apiDescription 通用文件删除接口
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *             通用文件删除接口 URL：/upload/ commonDelFile 请求参数： 参数名 参数说明 是否必填
	 *             fileName 文件名称(字符串)—需带文件类型，全路径去除协议 主机 basepath后的全路径
	 * 
	 * 
	 * 
	 *             如 全路径为 http://192.168.3.7:8081/af/201509/
	 *             c0ffa1941c5040c3822e7dcaa75d330b.jpg
	 *             则传af/201509/c0ffa1941c5040c3822e7dcaa75d330b.jpg Y 接口返回： 成功：
	 *             { "resultCode": 1, " resultMsg ":"文件删除成功" } 失败：{
	 *             "resultCode": "1020106", "resultMsg": "文件不存在" }
	 * 
	 * 
	 * 
	 * @apiAuthor 李淼淼
	 */
	public JSONMessage commonDelFile() {
		return null;
	}
	
	/**
	 * 
	 * @api {[get,post]} /getBizType 通知业务类型
	 * @apiVersion 1.0.0
	 * @apiName getBizType
	 * @apiGroup help
	 * @apiDescription 通知业务类型
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *			bizType：  表示业务类型（50有的为前端定义）
	 *				  7: 健康关怀计划问题内容
	 *				  8: 随访表
	 *				  9: 患教资料
	 *				 10: 健康关怀计划待支付订单卡片
	 *				 11: 分成设置改变通知
	 *				 12: 随访计划待支付订单卡片
	 *				 13: 48小时未填写咨询记录
	 *				 14: 医生填写咨询记录
	 *				 15: 三方通话成功结束
	 *				 16: 评价通知
	 *				 17: 加入集团会诊
	 *				 18: 绑定银行卡
	 *				 19: 申请加入集团通知
	 *				 20: 导医发给患者不带预约时间医生卡片
	 *				 21：  主诊医生邀请会诊医生加入电话视频会议
	 *				 22: 患者发给导医的卡片详情不带预约
	 *				 23： 申请创建医生集团
	 *				 24: 医生加入医院发给管理员的通知
	 *				 25：查看医生详情页面
	 *				 26：查看旧患者病历资料页面
	 *				 27：查看医生助手资料页面
	 *
	 *
	 *				 31：帖子详情（先判断帖子是否存在）
	 *				 32：查看评论详情（先判断评论是否存在）
	 *				 33：查看社区个人主页
	 *
	 *				 41：查看新患者病历资料页面
	 *
	 * 				 51：就医知识的卡片类型（前端定义）
	 *	
	 * @apiAuthor wangl
	 */
	@RequestMapping(value = "/getBizType")
	public JSONMessage getBizType() {
		return null;
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /userAgent userAgent 类型
	 * @apiVersion 1.0.0
	 * @apiName userAgent
	 * @apiGroup help
	 * @apiDescription userAgent 类型
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 * 			userAgent ：客户端代理 
	 * 			iOS    :  DGroupPatient/1.1/iPhone; iOS 9.3 ，DGroupPatient/1.1/(iPhone; iOS 9.3; Scale/2.00)
	 * 			android:  DGroupDoctor/1.050603.214/Dalvik/2.1.0(Linux;U;Android 5.0;PLK-TL01H Build/HONORPLK-TL01H)
	 *			DGroupDoctor: 包名
	 *			1.050603.214：  版本号
	 *			description : 目前只解析了版本号 使用"/"截取split字符串并解析第二段数据
	 *			获取客户端类型：ThreadData.clientVersionType.get();
	 *				标准版： standard
	 *             博德嘉联版 ：bdjl
	 * @apiAuthor wangl
	 */
	@RequestMapping(value = "/userAgent")
	public JSONMessage getUserAgent() {
		return null;
	}
	
	/**
	 * @api {[get,post]} /sessionStatus 轮询sesssionstatus
	 * @apiVersion 1.0.0
	 * @apiName sessionStatus
	 * @apiGroup help
	 * @apiDescription sessionStatus 类型
	 * 
	 * @apiExample {javascript} Example usage:
	 * 
	 *			sesssionstatus：  订单会话中的状态
	 *				  15: 服务中           
	 *				  16: 服务超时
	 *				  17: 人工取消
	 *				 172: 医生助手取消
	 *				 18:  后台自动取消
	 *				 19:  等待队列中
	 *				 20:  咨询队列中
	 *				 21:  等待接单
	 *				 22:  服务结束
	 *				 23:  未激活
	 * @apiAuthor zhangy
	 */
	@RequestMapping(value = "/sessionStatus")
	public JSONMessage getSessionStatus(){
		return null;
	}
}
