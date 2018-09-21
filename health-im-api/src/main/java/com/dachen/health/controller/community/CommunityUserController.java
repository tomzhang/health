package com.dachen.health.controller.community;

import CommonParam.CommonParam;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.community.entity.param.ReplyParam;
import com.dachen.health.community.entity.vo.CommunityUserVo;
import com.dachen.health.community.service.ICircleService;
import com.dachen.health.community.service.ICommunityUsersService;
import com.dachen.health.community.service.IReplyService;
import com.dachen.health.community.service.ITopicService;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value="community")
public class CommunityUserController {
	@Autowired
	private ICommunityUsersService communityUserService;
	@Autowired
	private ITopicService topicService;
	@Autowired
	private IReplyService replyService;
	@Autowired
	private IGroupDoctorService groupDoctor;
	@Autowired
	private ICircleService circleService;
	
	
	/**
     * @api {post} /community/collectTopic 收藏帖子
     * @apiVersion 1.0.0
     * @apiName collectTopic
     * @apiGroup 医生交流社区
     * @apiDescription 收藏帖子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiParam   {String}     topicId       主帖id
     * @apiSuucess {String}     resultCode    返回状态码
     * 
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("collectTopic")
	public JSONMessage collectTopic(String groupId,String topicId){
		if(StringUtil.isEmpty(groupId)){
			throw new ServiceException("主集团id不能为空");

		}
		if(StringUtil.isEmpty(topicId)){
			throw new ServiceException("主帖id不能为空");
		}
	
		
		communityUserService.collectTopic(topicId, ReqUtil.instance.getUserId(), groupId);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /community/deleteCollectTopic 取消收藏的帖子
     * @apiVersion 1.0.0
     * @apiName deleteCollectTopic
     * @apiGroup 医生交流社区
     * @apiDescription 取消收藏的帖子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiParam   {String}     topicId       主帖id
     * @apiSuucess {String}     resultCode    返回状态码
     * 
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("deleteCollectTopic")
	public JSONMessage deleteCollectTopic(String groupId,String topicId){
		if(StringUtil.isEmpty(groupId)){
			throw new ServiceException("主集团id不能为空");
		}
		if(StringUtil.isEmpty(topicId)){
			throw new ServiceException("主帖id不能为空");
		}
		communityUserService.deleteCollectTopic(topicId, ReqUtil.instance.getUserId(), groupId);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/findMyCollectTopicList 获取我收藏的帖子列表
     * @apiVersion 1.0.0
     * @apiName findMyCollectTopicList
     * @apiGroup 医生交流社区
     * @apiDescription 获取我收藏的帖子列表
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}       groupId           主集团id
     * @apiParam {String}    	pageIndex         页码（从0开始）
     * @apiParam {String}    	pageSize      	     每页多少条数据
     * 
     * @apiSuccess {String}     id    			帖子id
     * @apiSuccess {String}     createTime    	帖子发表时间
     * @apiSuccess {String}     updateTime    	帖子最后更新时间
     * @apiSuccess {String}     title           标题
     * @apiSuccess {String}     imgUrls         图片地址
     * @apiSuccess {String}     createName      发帖人姓名
     * @apiSuccess {String}     headUrl         发帖人头像地址
     * @apiSuccess {String}     createUserId    发帖人id
     * @apiSuccess {String}     delete          是否能删除(0代表不能  1代表可以)
     * @apiSuccess {String}     like            是否已点赞(0代表未点赞 1代表未点赞)
     * @apiSuccess {String}     cellect         是否已收藏(0代表未收藏 1代表已收藏)
     * @apiSuccess {String}     likeCount       点赞数量
     * @apiSuccess {String}     pageView        浏览量
     * @apiSuccess {String}     replies         回复数量
     * @apiSuccess {replys[]}   content         回复内容
     * @apiSuccess {replys[]}   replyHeadUrl    回复人的头像地址
     * @apiSuccess {replys[]}   replyId         回复人的id
     * @apiSuccess {replys[]}   replyName       回复人名称
     * @apiSuccess {replys[]}   topicId         主帖id
     * @apiSuccess {replys[]}   toUserHeadUrl   评论人的头像地址
     * @apiSuccess {replys[]}   toUserId        评论人的id
     * @apiSuccess {replys[]}   toUserName      评论人姓名
     * @apiSuccess {Number}     resultCode     返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findMyCollectTopicList")
	public JSONMessage findMyCollectTopicList(CommonParam param){
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException(" 主集团id不能为空");
		}
		param.setUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null,topicService.findMyCollectTopicList(param));
	}
	/**
     * @api {post} /community/findMyReplyList 我的回复列表
     * @apiVersion 1.0.0
     * @apiName findMyReplyList
     * @apiGroup 医生交流社区
     * @apiDescription 我的回复列表
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiParam   {int}        pageIndex     当前页
     * @apiParam   {int}        pageSize      每页多少条数据
     * @apiSuccess {String}     userId        用户id
     * @apiSuccess {String}    	userName      用户姓名
     * @apiSuccess {String}     headUrl         用户的头像地址
     * @apiSuccess {String}       topicId         主帖id
     * @apiSuccess {String}       content          回复内容
     * @apiSuccess {String}       time             回复的时间
     * @apiSuccess {String}     toUserName      被回复人的姓名
     * @apiSuccess {String}     toContent       被回复人的标题或内容
     * 
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findMyReplyList")
	public JSONMessage findMyReplyList(ReplyParam param){
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("主集团id不能为空");
		}
		param.setCreateUserId(ReqUtil.instance.getUserId());
		return JSONMessage.success(null,replyService.findMyReplyList(param));
	}
	
	/**
     * @api {post} /community/findToMyReplyList 我的收到的回复列表
     * @apiVersion 1.0.0
     * @apiName findToMyReplyList
     * @apiGroup 医生交流社区
     * @apiDescription 我的收到的回复列表
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiSuccess {String}     content        我的内容
     * @apiSuccess {String}     userName      我的姓名
     * @apiSuccess {String}    	time           回复的时间
     * @apiSuccess {String}     toContent         回复人的内容
     * @apiSuccess {String}     toUserHeadUrl      回复人的头像
     * @apiSuccess {String}     toUserName         回复人的姓名
     * @apiSuccess {String}     topicId            回复的id
     * 
     * 
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findToMyReplyList")
	public JSONMessage findToMyReplyList(ReplyParam param){
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("主集团id不能为空");
		}
		return JSONMessage.success(null,replyService.findMyToReplyList(param));
	}
	
	/**
     * @api {post} /community/findUserInfo 获取用户信息
     * @apiVersion 1.0.0
     * @apiName findUserInfo
     * @apiGroup 医生交流社区
     * @apiDescription 获取用户信息
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       集团id（不传默认返回主集团id）
     * @apiSuccess {String}     groupId       医生主集团id
     * @apiSuccess {String}     userId        用户id
     * @apiSuccess {String}    	userName      用户姓名
     * @apiSuccess {String}    headUrl         用户的头像地址
     * @apiSuccess {Long}      collectAmount    收藏数
     * @apiSuccess {Long}     replyAmount      回复数目
     * @apiSuccess {Long}     topicAmount      发表帖子数量
     * @apiSuccess {String}   message          是否有未读消息 0-无  1-有
     * @apiSuccess {String}   likeCount        累计点赞数
     * 
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findUserInfo")
	public JSONMessage findUserInfo(String groupId){
		
		//集团为空，就去获取该用户的主集团id
		
		if(StringUtils.isEmpty(groupId)){
			List<String> groupDoctorVo=groupDoctor.getGroupIsMain(ReqUtil.instance.getUserId());
			if(groupDoctorVo==null||groupDoctorVo.size()==0){
				throw new ServiceException("该医生没有设置主集团");
			}else if(groupDoctorVo.size()>1){
				throw new ServiceException("主集团设置异常：大于两个主集团");
			}else{
				groupId=groupDoctorVo.get(0);
			}
			
		}
		CommunityUserVo userVo=communityUserService.findUserInfo(groupId, ReqUtil.instance.getUserId());
		userVo.setGroupId(groupId);
		return JSONMessage.success(userVo);
	}
	
	
	/**
     * @api {post} /community/getByGroupCircle 获取圈子列表
     * @apiVersion 1.0.0
     * @apiName getByGroupCircle
     * @apiGroup 医生交流社区
     * @apiDescription 获取圈子列表
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiSuccess {String}     id            圈子id
     * @apiSuccess {String}    	name          圈子名称
     * @apiAuthor  李明
     * @date 2016年8月18日18:23:10
	 */
	@RequestMapping("getByGroupCircle")
	public JSONMessage getByGroupCircle(String groupId){
		if(StringUtil.isEmpty(groupId)){
			throw new ServiceException("集团id为空，请重新输入");
		}
		//List<Circle> circle=circleService.getByGroupCircle(groupId);
		return JSONMessage.success(circleService.getByGroupCircle(groupId));
	}
	/**
     * @api {post} /community/addCircle 新增圈子
     * @apiVersion 1.0.0
     * @apiName addCircle
     * @apiGroup 医生交流社区
     * @apiDescription 新增圈子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiParam   {String}     name          圈子名称
     * @apiParam   {String}     main          默认圈子
     * @apiSuccess {String}    	resultCode    成功标识码
     * @apiAuthor  李明
     * @date 2016年8月18日18:26:21
	 */
	@RequestMapping("addCircle")
	public JSONMessage addCircle(String groupId,String name){
		if(StringUtil.isEmpty(groupId)){
			throw new ServiceException("集团id为空，请重新输入");
		}
		if(StringUtil.isEmpty(name)){
			throw new ServiceException
			("圈子名称为空");
		}
		circleService.addCircle(name, groupId);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/topCircle 上移圈子
     * @apiVersion 1.0.0
     * @apiName topCircle
     * @apiGroup 医生交流社区
     * @apiDescription 上移圈子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiParam   {String}     id            圈子id
     * @apiSuccess {String}    	resultCode    成功标识码
     * @apiAuthor  李明
     * @date 2016年8月18日18:26:21
	 */
	@RequestMapping("topCircle")
	public JSONMessage topCircle(String id,String groupId,@RequestParam(required=true,defaultValue="0")String type){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		circleService.topCircle(id,type,groupId);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/deleteCircle 删除圈子
     * @apiVersion 1.0.0
     * @apiName deleteCircle
     * @apiGroup 医生交流社区
     * @apiDescription 上去圈子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     id            圈子id
     * @apiSuccess {String}    	resultCode    成功标识码
     * @apiAuthor  李明
     * @date 2016年8月18日18:26:21
	 */
	@RequestMapping("deleteCircle")
	public JSONMessage deleteCircle(String id){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		circleService.deleteCircle(id);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/updateCircle 修改圈子
     * @apiVersion 1.0.0
     * @apiName updateCircle
     * @apiGroup 医生交流社区
     * @apiDescription 修改圈子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     name       圈子名称
	 * @apiParam   {String}     id            圈子id
     * @apiSuccess {String}    	resultCode    成功标识码
     * @apiAuthor  李明
     * @date 2016年8月18日18:26:21
	 */
	@RequestMapping("updateCircle")
	public JSONMessage updateCircle(String id,String name){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		if(StringUtils.isEmpty(name)){
			throw new ServiceException("圈子名称不能为空");
		}
		circleService.updateCircle(id, name);
		return JSONMessage.success();
	}
	

	
	/**
     * @api {post} /community/findByUserIdInfo 获取用户发表的帖子
     * @apiVersion 1.0.0
     * @apiName findByUserIdInfo
     * @apiGroup 医生交流社区
     * @apiDescription 修改圈子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       集团id
     * @apiParam   {String}     userId        用户id
     * @apiSuccess {String}     likeCount     累计点赞数
     * @apiSuccess {String}		topicAmount   发表帖子数目
     * @apiSuccess {String}    	resultCode    成功标识码
     * @apiAuthor  李明
     * @date 2016年8月18日18:26:21
	 */
	@RequestMapping("findByUserIdInfo")
	public JSONMessage findByUserIdInfo(String groupId,Integer userId){
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团id不能为空");
		}
		if(userId==null||userId==0){
			throw new ServiceException("用户id不能为空");
		}
		return JSONMessage.success(communityUserService.findByUserIdInfo(groupId, userId));
	}



}
