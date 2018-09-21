package com.dachen.health.controller.meeting;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.meeting.po.Meeting;
import com.dachen.health.meeting.service.MeetingService;

@RestController
@RequestMapping("/meeting")
public class MeetingController {

	@Autowired
	MeetingService meetingService;
	
	/**
	 * @api {get/post} /meeting/create 创建会议
	 * @apiVersion 1.0.0
	 * @apiName create
	 * @apiGroup 直播会议
	 * @apiDescription 使用场景：创建会议
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			company 			公司
	 * @apiParam {String} 			companyId 			公司id
	 * @apiParam {String} 			subject 			标题（必传）
	 * @apiParam {Long} 			startDate 			开始日期
	 * @apiParam {Long} 			startTime 			开始时间（必传）
	 * @apiParam {Long} 			endTime 			结束时间
	 * @apiParam {Integer} 			attendeesCount 			人数
	 * @apiParam {Integer} 			price 			    价格（分）
	 * @apiParam {String} 			organizerToken 			组织者加入口令（必传）
	 * @apiParam {String} 			panelistToken 			嘉宾加入口令（必传）
	 * @apiParam {String} 			attendeeToken 			普通参加者加入口令
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * @apiSuccess {String}         company              公司名称
	 * @apiSuccess {String}         subject              标题
	 * @apiSuccess {Long}         startDate              开始日期
	 * @apiSuccess {Long}         startTime             开始时间
	 * @apiSuccess {Long}         endTime              结束时间
	 * @apiSuccess {Integer}         attendeesCount             参加人数
	 * @apiSuccess {Integer}         price              		       价格
	 * @apiSuccess {String}         organizerToken             组织者加入口令
	 * @apiSuccess {String}         panelistToken              嘉宾加入口令
	 * @apiSuccess {String}         attendeeToken              普通参加者加入口令
	 * @apiSuccess {String}         organizerJoinUrl             组织者加入URL
	 * @apiSuccess {String}         panelistJoinUrl              嘉宾加入URL
	 * @apiSuccess {String}         attendeeJoinUrl              普通参加者加入URL
	 * @apiSuccess {String}         liveId              		 直播id
	 * @apiSuccess {String}         number              		直播编号
	 * 
     * @apiAuthor  wangl
     * @date 2016年3月9日
	 */
	@RequestMapping(value = "create")
	public JSONMessage create(Meeting meeting){
		return JSONMessage.success(meetingService.createMeeting(meeting));
	}
	
	
	/**
	 * @api {get/post} /meeting/update 修改会议
	 * @apiVersion 1.0.0
	 * @apiName update
	 * @apiGroup 直播会议
	 * @apiDescription 使用场景：修改会议
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			id 					会议记录id（必传）
	 * @apiParam {String} 			company 			公司
	 * @apiParam {String} 			subject 			标题（必传）
	 * @apiParam {Long} 			startDate 			开始日期
	 * @apiParam {Long} 			startTime 			开始时间（必传）
	 * @apiParam {Long} 			endTime 			结束时间
	 * @apiParam {Integer} 			attendeesCount 			人数
	 * @apiParam {Integer} 			price 			    价格（分）
	 * @apiParam {String} 			organizerToken 			组织者加入口令（必传）
	 * @apiParam {String} 			panelistToken 			嘉宾加入口令（必传）
	 * @apiParam {String} 			attendeeToken 			普通参加者加入口令
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * @apiSuccess {String}         company              公司名称
	 * @apiSuccess {String}         subject              标题
	 * @apiSuccess {Long}         startDate              开始日期
	 * @apiSuccess {Long}         startTime             开始时间
	 * @apiSuccess {Long}         endTime              结束时间
	 * @apiSuccess {Integer}         attendeesCount             参加人数
	 * @apiSuccess {Integer}         price              		       价格
	 * @apiSuccess {String}         organizerToken             组织者加入口令
	 * @apiSuccess {String}         panelistToken              嘉宾加入口令
	 * @apiSuccess {String}         attendeeToken              普通参加者加入口令
	 * @apiSuccess {String}         organizerJoinUrl             组织者加入URL
	 * @apiSuccess {String}         panelistJoinUrl              嘉宾加入URL
	 * @apiSuccess {String}         attendeeJoinUrl              普通参加者加入URL
	 * @apiSuccess {String}         liveId              		 直播id
	 * @apiSuccess {String}         number              		直播编号
	 * 
     * @apiAuthor  wangl
     * @date 2016年3月9日
	 */
	@RequestMapping(value = "update")
	public JSONMessage update(Meeting meeting){
		return JSONMessage.success(meetingService.updateMeeting(meeting));
	}
	
	
	/**
	 * @api {get/post} /meeting/stop 取消或完成会议
	 * @apiVersion 1.0.0
	 * @apiName stop
	 * @apiGroup 直播会议
	 * @apiDescription 使用场景：取消或完成会议
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {String} 			meetingId 			 	会议记录id（必传）
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * 
     * @apiAuthor  wangl
     * @date 2016年3月9日
	 */
	@RequestMapping(value = "stop")
	public JSONMessage stop(@RequestParam(required = true) String meetingId){
		meetingService.stopMeeting(meetingId);
		return JSONMessage.success();
	}
	
	
	/**
	 * @api {get/post} /meeting/list 会议列表
	 * @apiVersion 1.0.0
	 * @apiName list
	 * @apiGroup 直播会议
	 * @apiDescription 使用场景：会议列表
	 * @apiParam {String} 			access_token 			token
	 * @apiParam {Integer} 			pageIndex 				页数
	 * @apiParam {Integer} 			pageSize 				记录数
	 * @apiParam {String} 			companyId 				公司id
	 * 
	 * @apiSuccess {String}         resultCode              状态码
	 * @apiSuccess {String}         id              	 会议记录id
	 * @apiSuccess {String}         company              公司名称 
	 * @apiSuccess {String}         subject              标题
	 * @apiSuccess {Long}         startDate              开始日期
	 * @apiSuccess {Long}         startTime             开始时间
	 * @apiSuccess {Long}         endTime              结束时间
	 * @apiSuccess {Integer}         attendeesCount             参加人数
	 * @apiSuccess {Integer}         price              		       价格
	 * @apiSuccess {String}         organizerToken             组织者加入口令
	 * @apiSuccess {String}         panelistToken              嘉宾加入口令
	 * @apiSuccess {String}         attendeeToken              普通参加者加入口令
	 * @apiSuccess {String}         organizerJoinUrl             组织者加入URL
	 * @apiSuccess {String}         panelistJoinUrl              嘉宾加入URL
	 * @apiSuccess {String}         attendeeJoinUrl              普通参加者加入URL
	 * @apiSuccess {String}         liveId              		 直播id
	 * @apiSuccess {String}         number              		直播编号
	 * @apiSuccess {Integer}         status              		1:未开始，2：已开始
	 * @apiSuccess {String}         createUserName              创建者姓名
	 * @apiSuccess {String}         createUserId			    用户id
	 * @apiSuccess {String}         headPicFileName             头像
	 * @apiSuccess {String}         domain             			域名
	 * @apiSuccess {Integer}         isMyCreate             	1:是我创建，0：不是我创建的
	 * 
     * @apiAuthor  wangl
     * @date 2016年3月9日
	 */
	@RequestMapping(value = "list")
	public JSONMessage list(@RequestParam(required=true)String companyId, Integer pageIndex,Integer pageSize) throws HttpApiException {
		return JSONMessage.success(meetingService.listMeeting(companyId,pageIndex,pageSize));
	}
	
	
}
