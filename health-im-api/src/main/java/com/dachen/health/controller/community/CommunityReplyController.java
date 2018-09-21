package com.dachen.health.controller.community;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.community.entity.param.ReplyParam;
import com.dachen.health.community.service.IReplyService;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping(value="community")
public class CommunityReplyController {
	@Autowired
	private IReplyService replyService;
	/**
     * @api {post} /community/replytoTopic 回复主帖
     * @apiVersion 1.0.0
     * @apiName replytoTopic
     * @apiGroup 医生交流社区
     * @apiDescription 回复主帖
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}   	topicId             主帖id(必填)
     * @apiParam {String}   	content           回复内容(必填)
     * @apiParam {String[]}     imgUrls   		     图片数组
     * @apiParam {String}       groupId           医生主集团id(必填)
     * @apiSuccess {Number}     resultCode        返回状态码
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("replytoTopic")
	public JSONMessage replytoTopic(ReplyParam param){
		if(StringUtils.isEmpty(param.getTopicId())
			||StringUtils.isEmpty(param.getContent())
			||StringUtils.isEmpty(param.getGroupId())){
			
			throw new ServiceException("缺少必填参数");
		}
		param.setCreateUserId(ReqUtil.instance.getUserId());
		replyService.replyTips(param);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/replytoUser 回复评论
     * @apiVersion 1.0.0
     * @apiName replytoUser
     * @apiGroup 医生交流社区
     * @apiDescription 回复评论
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}   	topicId             主帖id（必填）
     * @apiParam {String}   	content           回复内容（必填）
     * @apiParam {String[]}    	imgUrls   		     图片数组
     * @apiParam {Integer}      toUserId          回复评论人的用户id（必填）
     * @apiParam {String}       toReplyId         回复评论的id（必填）
     * @apiParam {String}       groupId           医生主集团id（必填）
     * @apiSuccess {Number}     resultCode        返回状态码
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("replytoUser")
	public JSONMessage replytoUser(ReplyParam param){
		if(StringUtils.isEmpty(param.getTopicId())
				||StringUtils.isEmpty(param.getContent())
				||StringUtils.isEmpty(param.getGroupId())
				||param.getToUserId()==null
				||StringUtils.isEmpty(param.getToReplyId())){
				
				throw new ServiceException("缺少必填参数");
			}
		param.setCreateUserId(ReqUtil.instance.getUserId());
		replyService.replyUsers(param);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /community/deleteReplyId 删除评论或回帖
     * @apiVersion 1.0.0
     * @apiName 删除评论或回帖
     * @apiGroup 医生交流社区
     * @apiDescription 回复评论
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}   	id             	  id
     * @apiSuccess {Number}     resultCode        返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("deleteReplyId")
	public JSONMessage deleteReplyId(String id){
		if(StringUtils.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		replyService.delete(id);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/findByReplyList 获取帖子评论详情列表
     * @apiVersion 1.0.0
     * @apiName findByReplyList
     * @apiGroup 医生交流社区
     * @apiDescription 获取帖子评论详情列表
     *
     * @apiParam {String}    	access_token    token
     * @apiParam  {String}      id              主帖id（必填）
     * @apiParam {String}    	pageIndex       页码（从0开始）
     * @apiParam {String}    	pageSize      	 每页多少条数据
     * 
     * @apiSuccess {String}   content        回复内容
     * @apiSuccess {String}   replyHeadUrl   回复人的头像地址
     * @apiSuccess {String}   replyUserId    回复人的id
     * @apiSuccess {String}   replyName      回复人名称
     * @apiSuccess {String}   id             回复id
     * @apiSuccess {String}   topicId        主帖id
     * @apiSuccess {String}   imgUrls        回复的头像
     * @apiSuccess {String}   toUserHeadUrl  评论人的头像地址
     * @apiSuccess {String}   toUserId       评论人的id
     * @apiSuccess {String}   toUserName     评论人姓名
     * @apiSuccess {String}   floor          楼层
     * @apiSuccess {Number}   resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findByReplyList")
	public JSONMessage findByReplyList(ReplyParam param){
		if(StringUtils.isEmpty(param.getTopicId())){
			throw new ServiceException("主帖id不能为空");
		}
		
		param.setCreateUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(replyService.findReplyList(param));
	}
	
	
}
