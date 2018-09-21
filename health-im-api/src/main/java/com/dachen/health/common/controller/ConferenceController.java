package com.dachen.health.common.controller;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dachen.sdk.exception.HttpApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.logger.LoggerUtils;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.pack.conference.entity.param.CallRecordParam;
import com.dachen.health.pack.conference.service.ICallRecordService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.util.MongodbUtil;
import com.dachen.util.XmlToDbObject;
import com.mobsms.sdk.MobSmsSdk;
import com.mongodb.DBObject;

@RestController
@RequestMapping("conference")
public class ConferenceController {
	private static Logger logger= LoggerFactory.getLogger(ConferenceController.class);
	
	@Autowired
	private ICallRecordService conferenceService;
	@Resource
    private MobSmsSdk mobSmsSdk;
	@Autowired
	private IBusinessServiceMsg sendMsgService;
	
	/**
     * @api  {get，post} /conference/createConference 创建会议
     * @apiVersion 1.0.0
     * @apiName createConference
     * @apiGroup 电话会议
     * @apiDescription 由导医创建会议 ,然后会邀请导医和患者加入会议
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}     orderId               订单ID
     * 
     * @apiSuccess {String}     respCode             请求状态码；0：成功；：非0：，失败
     * @apiSuccess {String}     confId               会议标识ID，成功时由8为纯数字组成。否则为0
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/createConference")
	public JSONMessage createConference( @RequestParam Integer orderId){
		return JSONMessage.success(null,conferenceService.createConference(orderId)); 
	}
	
	/**
     * @api  {get，post} /conference/inviteMember 邀请加入
     * @apiVersion 1.0.0
     * @apiName inviteMember
     * @apiGroup 电话会议
     * @apiDescription 邀请成员加入会议（拨打与重拔与会者电话）
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}     callRecordId         电话纪录ID
     * @apiParam  {String}     userId         	           与会者ID
     * 
     * @apiSuccess {String}     code                请求状态码 0：成功；非0：失败
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/inviteMember")
	public JSONMessage inviteMember(@RequestParam String callRecordId,@RequestParam String userId){
		return JSONMessage.success(null,conferenceService.inviteMember(callRecordId,userId));
	}
	
	/**
     * @api  {get，post} /conference/removeConference 退出会议即移出与会者
     * @apiVersion 1.0.0
     * @apiName removeConference
     * @apiGroup 电话会议
     * @apiDescription 退出会议即挂断电话
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    callRecordId         电话纪录ID
     * @apiParam  {String}    userId         	           与会者ID
     * 
     * @apiSuccess {String}     code                请求状态码 0：成功；非0：失败
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/removeConference")
	public JSONMessage removeConference(@RequestParam String callRecordId,@RequestParam String userId){
		return JSONMessage.success(null,conferenceService.removeConference(callRecordId, userId));
	}
	
	/**
     * @api  {get，post} /conference/deafConference 禁听与会者
     * @apiVersion 1.0.0
     * @apiName deafConference
     * @apiGroup 电话会议
     * @apiDescription 被禁的与会者在会议中不能听与不能说
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    callRecordId         电话纪录ID
     * @apiParam  {String}    userId         	           与会者ID
     * 
     * @apiSuccess {String}     code                请求状态码 0：成功；非0：失败
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/deafConference")
	public JSONMessage deafConference(@RequestParam String callRecordId,@RequestParam String userId){
		return JSONMessage.success(null,conferenceService.deafConference(callRecordId, userId));
	}
	
	/**
     * @api  {get，post} /conference/unDeafConference 取消与会者禁听
     * @apiVersion 1.0.0
     * @apiName unDeafConference
     * @apiGroup 电话会议
     * @apiDescription 取消与会者禁听，与禁听相对
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    callRecordId         电话纪录ID
     * @apiParam  {String}    userId         	           与会者ID
     * 
     * @apiSuccess {String}     code                请求状态码 0：成功；非0：失败
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/unDeafConference")
	public JSONMessage unDeafConference(@RequestParam String callRecordId,@RequestParam String userId){
		return JSONMessage.success(null,conferenceService.unDeafConference(callRecordId, userId));
	}
	
	/**
     * @api  {get，post} /conference/dismissConference 解散会议
     * @apiVersion 1.0.0
     * @apiName dismissConference
     * @apiGroup 电话会议
     * @apiDescription 解散会议，会议里所有成员电话会被挂断
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    callRecordId         电话纪录ID
     * 
     * @apiSuccess {String}     code                请求状态码 0：成功；非0：失败
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/dismissConference")
	public JSONMessage dismissConference(@RequestParam String callRecordId){
		return JSONMessage.success(null,conferenceService.dismissConference(callRecordId));
	}
	
	/**
     * @api  {get，post} /conference/getStatus 轮询获取状态
     * @apiVersion 1.0.0
     * @apiName getStatus
     * @apiGroup 电话会议 
     * @apiDescription 轮询获取状态，当会议还在创建时，抛出“会议创建中”信息，如果失败则抛出对应的“余额不足创建三方通话失败”或者“未知原因创建三方通话失败”信息。其它正常 返回
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    confId         电话纪录ID
     * 
     * @apiSuccess {String}   memberId             与会者ID                  
     * @apiSuccess {String}   telephone            与会者电话号码                 
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/getStatus")
	public JSONMessage getStatus(String confId){
		return JSONMessage.success(null,conferenceService.getStatus(confId));
	}
	
	@RequestMapping("/endCall")
	public JSONMessage endCall(HttpServletRequest request,HttpServletResponse response) {
		Map<String ,String > map = new HashMap<String ,String >();
		try {
  			InputStream inStream = request.getInputStream();
			ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while ((len = inStream.read(buffer)) != -1) {
				outSteam.write(buffer, 0, len);
			}
			String resultxml = new String(outSteam.toByteArray(), "utf-8");
			logger.info("callresult: "+resultxml);
			inStream.close();
			outSteam.close();
			
			List<DBObject> list = XmlToDbObject.getDBObjects(new ByteArrayInputStream(resultxml.getBytes("UTF-8")));
			if(list == null || list.isEmpty()){
				throw new ServiceException("请求参数有误");
			}
			DBObject obj = list.get(0);
			String event = MongodbUtil.getString(obj, "event");
			if(event.equals("confJoin")){
				conferenceService.endJoin(obj);
			}else if(event.equals("confUnjoin")){
				conferenceService.endUnJoin(obj);
			}else if(event.equals("userMediaCtrRet")){
				conferenceService.endMedia(obj);
			}else if(event.equals("confRecordCtrRet")){
				conferenceService.endStopRecord(obj);
			}else if(event.equals("confDismiss")){
				conferenceService.endDismiss(obj);
			}else if(event.equals("confQueryRet")){
				conferenceService.endQuerry(obj);
			}
			map.put("retCode", "0");
			map.put("reason", resultxml);
		}catch(Exception e) {
			    LoggerUtils.printExceptionLog(e.getMessage(), e.fillInStackTrace());
				 
		}
		return JSONMessage.success(null,map);
	}
	
	/**
     * @api  {get，post} /conference/getCallRecord 获取电话纪录
     * @apiVersion 1.0.0
     * @apiName getCallRecord
     * @apiGroup 电话会议
     * @apiDescription 根据订单获取电话纪录包括（会议与非会议）
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}    orderId              订单ID
     * 
     * @apiSuccess {String}   memberId             与会者ID                  
     * @apiSuccess {String}   telephone            与会者电话号码                 
     * 
     * @apiAuthor  张垠
     * @date 2015年11月12日
     */
	@RequestMapping("/getCallRecord")
	public JSONMessage getCallRecord(CallRecordParam param){
		return JSONMessage.success(null,conferenceService.getCallRecordByParam(param));
	}
	
	/**
     * @api  {get，post} /conference/getCommentByConfId 获取电话会议开始时间
     * @apiVersion 1.0.0
     * @apiName getCommentByConfId
     * @apiGroup 电话会议
     * @apiDescription 根据会议ID获取会议的时间
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {Integer}    confId               会议ID
     * 
     * @apiSuccess {String}   message               会议备注语                 
     * 
     * @apiAuthor  张垠
     * @date 2015年11月24日
     */
	@RequestMapping("/getCommentByConfId")
	public JSONMessage getCommentByConfId(String confId){
		return JSONMessage.success(null,conferenceService.getMessage(confId));
	}
	
	
	/**
     * @api  {get，post} /conference/getLastConference 根据订单获取当前导医尚未结束的会议
     * @apiVersion 1.0.0
     * @apiName getLastConference
     * @apiGroup 电话会议
     * @apiDescription 根据订单查询当前导医尚未结束的会议
     * 
     * @apiParam  {String}     access_token          token
     * @apiParam  {String}     orderId              订单ID
     * 
     * @apiSuccess {String}      id               		电话纪录ID                 
     * @apiSuccess {ConfCall}   confCall              
     * @apiSuccess {String}     confCall.confId	          会议ID    
     *                
     * @apiAuthor  张垠
     * @date 2015年11月26日
     */
	@RequestMapping("/getLastConference")
	public JSONMessage getLastConference(CallRecordParam param){
		return JSONMessage.success(null,conferenceService.getLastConference(param));
	}
	
	/**
     * @api  {get，post} /conference/getConferenceRecordStatus 根据订单查询所有会议状态与否
     * @apiVersion 1.0.0
     * @apiName getConferenceRecordStatus
     * @apiGroup 电话会议
     * @apiDescription 根据订单查询所有会议状态与否
     * 
     * @apiParam  {String}     access_token          token
     * @apiParam  {String}     orderId              订单ID
     * 
     * @apiSuccess {String}    confId 	         	 会议ID    
     * @apiSuccess {boolean}   status 	                                    状态
     *                
     * @apiAuthor  张垠
     * @date 2015年11月26日
     */
	@RequestMapping("/getConferenceRecordStatus")
	public JSONMessage getConferenceRecordStatus(CallRecordParam param){
		return JSONMessage.success(null, conferenceService.getRecordStatus(param));
	}
	
	/**
     * @api  {get，post} /conference/testSendNotice 测试发送通知
     * @apiVersion 1.0.0
     * @apiName testSendNotice
     * @apiGroup 电话会议
     * @apiDescription 测试发送通知(临时测试)
     * 
     * @apiParam  {String}     access_token          token
     * @apiParam  {String}     phone                 收短信ID
     * @apiParam  {String}     userId                收通UserID
     * 
     *                
     * @apiAuthor  张垠
     * @date 2015年11月26日
     */
	@RequestMapping("/testSendNotice")
	public JSONMessage testSendNotice(@RequestParam String phone,@RequestParam String userId) throws HttpApiException {
		 //发短信
//		 String doctorContent = "您与患者"+ patient.getUserName() +"的电话咨询订单已结束，请您尽快填写本次订单咨询记录，咨询记录结束后订单款项才会结算，感谢您本次服务。";
		 String patientContent = "您与医生"+ "xxxx"+"的电话咨询订单已结束，如果对本次订单有疑问，请拨打平台客服电话：400-0000-0000。医生之后会为您填写本次订单咨询记录，请稍后查看。";
		 mobSmsSdk.send(phone, patientContent);
//		 mobSmsSdk.send(patientNum, patientContent);
		 //发通知
//		   医生推送：您与患者***的电话咨询已结束，点击填写咨询记录。
		 List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
		 ImgTextMsg imgTextMsg = new ImgTextMsg();
		 imgTextMsg.setTime(System.currentTimeMillis());
		 imgTextMsg.setStyle(7);
		 imgTextMsg.setTitle(UserChangeTypeEnum.DOCOTR_OUT_CONFERENCE.getAlias());
		 imgTextMsg.setContent("您与患者"+ "XXX"+"的电话咨询已结束，点击填写咨询记录。");
		 Map<String, Object> imParam = new HashMap<String, Object>();
		 imParam.put("bizType",13);
		 imParam.put("bizId",2731);
         imgTextMsg.setParam(imParam);
		 mpt.add(imgTextMsg);
		 Map<String, Object> msg = new HashMap<String, Object>();
		 msg.put("pushTip", "您与患者"+ "XXX"+"的电话咨询已结束，点击填写咨询记录。");
		 sendMsgService.sendTextMsg(userId, SysGroupEnum.TODO_NOTIFY, mpt, msg);
		return JSONMessage.success(null,null);
	}
	
	@RequestMapping("/queryConference")
	public JSONMessage queryConference(CallRecordParam param ){
		return JSONMessage.success(null, conferenceService.queryConference(param.getId()));
		
	}
}
