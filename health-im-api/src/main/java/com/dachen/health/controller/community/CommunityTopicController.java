package com.dachen.health.controller.community;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.community.entity.param.TopicParam;
import com.dachen.health.community.entity.po.Files;
import com.dachen.health.community.entity.po.Video;
import com.dachen.health.community.service.ICommunityUsersService;
import com.dachen.health.community.service.ITopicService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value="community")
public class CommunityTopicController {
	@Autowired
	 private ITopicService topicService;
	@Autowired
	private ICommunityUsersService communityUserService;
	/**
     * @api {post} /community/publish 发表帖子
     * @apiVersion 1.0.0
     * @apiName publish
     * @apiGroup 医生交流社区
     * @apiDescription 发表帖子
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}       title             标题
     * @apiParam {String}   	type              帖子类型（0 普通图文 1图文混排 视频附件帖子）
     * @apiParam {String}       groupId           主集团id
     * @apiParam {String}   	text              纯文本
     * @apiParam {String}   	richText          富文本
	 * @apiParam {Integer}      richTextLength    富文本长度（文字）
	 * @apiParam {String}		digest            摘要（非必填）
	 * @apiParam {String}      digestImgUrls      题图数组(非必填)
     * @apiParam {String[]}   	label             集团标签名称数组
     * @apiParam {String}       circleId          圈子id       
     * @apiParam {String[]}     imgUrls           图片地址数组
     * @apiParam {String[]}     filesJson         附件数组{}[{"file_id":"dafdasdf","file_url":"www.baidu.com", "size":"123123","sizeStr":"12M"," file_name":"附件", "type":"1","suffix":"doc"}]
     * @apiParam {String[]}     videoJson         视频数组{}[{"play_url":"www.dfdf.com","play_first":"www.sdf.com","play_time":"123","size":"213123","suffix":"mp4"}]
     * @apiParam {Integer}     	saveType          保存模式(0 正常发布 1保存草稿 默认为0)
     *
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("/publish")
	public JSONMessage publish(TopicParam topicParam,String filesJson,String videoJson){
		
		if(StringUtils.isEmpty(topicParam.getGroupId())){
			throw new ServiceException("主集团id不能为空");
		}
		if(StringUtil.isEmpty(topicParam.getCircleId())){
			throw new ServiceException("圈子id为必填选项");
		}
		//处理视频数组和附件数组
		List<Files> listfiles=new ArrayList<>();
		listfiles=JSONArray.parseArray(filesJson, Files.class);
		List<Video> listVideo=new ArrayList<>();
		listVideo=JSONArray.parseArray(videoJson,Video.class);
		topicParam.setFiles(listfiles);
		topicParam.setVideo(listVideo);
		
		topicParam.setCreateUserId(ReqUtil.instance.getUserId());
		topicService.publish(topicParam);
		return JSONMessage.success();
	}

	/**
	 * @api {post} /community/editTopic 编辑帖子
	 * @apiVersion 1.0.0
	 * @apiName editTopic
	 * @apiGroup 医生交流社区
	 * @apiDescription 编辑帖子
	 *
	 * @apiParam {String}    	access_token      token
	 * @apiParam {String}       title             标题
	 * @apiParam {String}   	richText          富文本
	 * @apiParam {String}       circleId          圈子id
	 * @apiParam {String[]}     filesJson         附件数组{}[{"file_id":"dafdasdf","file_url":"www.baidu.com", "size":"123123","sizeStr":"12M"," file_name":"附件", "type":"1","suffix":"doc"}]
	 * @apiParam {String[]}     videoJson         视频数组{}[{"play_url":"www.dfdf.com","play_first":"www.sdf.com","play_time":"123","size":"213123","suffix":"mp4"}]
	 * @apiParam {String}       digest            摘要
	 * @apiParam {String[]}     digestImgUrls     题图数组
	 * @apiParam {Integer}      richTextLength   富文本长度
	 * @apiParam {Integer}     	saveType          保存模式(0 正常发布 1保存草稿 默认为0)
	 *
	 * @apiSuccess {Number} resultCode    返回状态吗
	 *
	 * @apiAuthor  李明
	 * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("/editTopic")
	public JSONMessage editTopic(TopicParam topicParam,String filesJson,String videoJson){
		if(StringUtils.isEmpty(topicParam.getId())){
			throw new ServiceException("帖子id不能为空");
		}
		if(StringUtils.isEmpty(topicParam.getGroupId())){
			throw new ServiceException("主集团id不能为空");
		}
		if(StringUtil.isEmpty(topicParam.getCircleId())){
			throw new ServiceException("圈子id为必填选项");
		}
		//处理视频数组和附件数组
		List<Files> listfiles=new ArrayList<>();
		listfiles=JSONArray.parseArray(filesJson, Files.class);
		List<Video> listVideo=new ArrayList<>();
		listVideo=JSONArray.parseArray(videoJson,Video.class);
		topicParam.setFiles(listfiles);
		topicParam.setVideo(listVideo);

		topicParam.setCreateUserId(ReqUtil.instance.getUserId());
		topicService.editTopic(topicParam);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /community/deleteTopic 删除帖子
     * @apiVersion 1.0.0
     * @apiName deleteTopic
     * @apiGroup 医生交流社区
     * @apiDescription 删除帖子
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}   	id                主帖id
     * @apiSuccess {Number} resultCode    返回状态吗
     *
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("/deleteTopic")
	public JSONMessage deleteTopic(String id){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		topicService.deleteTopic(id);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /community/likeTopic 点赞或者取消点赞帖子
     * @apiVersion 1.0.0
     * @apiName likeTopic
     * @apiGroup 医生交流社区
     * @apiDescription 点赞帖子
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}   	id                主帖id
     * @apiParam {String}   	type              默认为0 点赞  1取消点赞
     * @apiSuccess {Number}     resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("/likeTopic")
	public JSONMessage likeTopic(String id,String type){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("id不能为空");
		}
		topicService.likeTopic(id, ReqUtil.instance.getUserId(),type);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /community/findTopicList 获取帖子列表
     * @apiVersion 1.0.0
     * @apiName findTopicList
     * @apiGroup 医生交流社区
     * @apiDescription 获取帖子列表（按照最后更新时间排序）该医生所在的主集团的帖子
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}       groupId           主集团id
     * @apiParam {String}       circleId          圈子id
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
     * @apiSuccess {Object[]}   viedo           视频信息
     * @apiSuccess {Object[]}   files           附件信息
     * @apiSuccess {String}     circleId        圈子id
     * @apiSuccess {String}     circleName      圈子名称
     * @apiSuccess {String}     top             是否为置顶帖子(0 为置顶  1为非置顶)
	 * @apiSuccess {String}     digest          摘要
	 * @apiSuccess {Integer}    richTextLength   富文本长度
	 * @apiSuccess {String[]}   digestImgUrls   题图数组
     * @apiSuccess {Number}     resultCode     	返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("/findTopicList")
	public JSONMessage findTopicList(TopicParam param){
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException(" 主集团id不能为空");
		}
		return JSONMessage.success(null,topicService.findAllTopic(param,ReqUtil.instance.getUserId()));
	}

	/**
	 * @api {post} /community/findUpTopicList 获取置顶帖子列表
	 * @apiVersion 1.0.0
	 * @apiName findUpTopicList
	 * @apiGroup 医生交流社区
	 * @apiDescription 获取置顶帖子列表
	 *
	 * @apiParam {String}    	access_token      token
	 * @apiParam {String}       groupId           主集团id
	 * @apiParam {String}       circleId          圈子id（非必填）
	 *
	 * @apiSuccess {String}     id    			帖子id
	 * @apiSuccess {String}     title    	帖子标题
	 * @apiSuccess {String}     content    	帖子内容
	 * @apiSuccess {Number}     resultCode     	返回状态吗
	 * @apiAuthor  李明
	 * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findUpTopicList")
	public JSONMessage findUpTopicList(TopicParam param){
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException(" 主集团id不能为空");
		}
		return JSONMessage.success(topicService.findAllUpTopic(param));
	}

	/**
     * @api {post} /community/findByTopicDetail 查看帖子详情
     * @apiVersion 1.0.0
     * @apiName findByTopicDetail
     * @apiGroup 医生交流社区
     * @apiDescription 获取帖子详情
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}    	groupId      	主集团id	
     * @apiParam {String}    	id              帖子id
     * 
     * @apiSuccess {String}     id    			帖子id
     * @apiSuccess {String}     createTime    	帖子发表时间
     * @apiSuccess {String}     updateTime    	帖子最后更新时间
     * @apiSuccess {String}     title           标题
     * @apiSuccess {String}     createName      发帖人姓名
     * @apiSuccess {String}     delete          是否能删除(0代表不能 1代表可以)
     * @apiSuccess {String}     like            是否已点赞(0代表已点赞 1代表未点赞)
     * @apiSuccess {String}     likeCount       点赞数量
     * @apiSuccess {String}     pageView        浏览量
     * @apiSuccess {String}     replies         回复数量
     * @apiSuccess {String}     groupId         集团id
	 * @apiSuccess {String}     state           帖子状态(0-正常 2-草稿)
	 * @apiSuccess {Integer}    richTextLength   富文本长度
     * @apiSuccess {Number}     resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findByTopicDetail")
	public JSONMessage findByTopicDetail(String id,String groupId){
		if(StringUtils.isEmpty(id)){
			throw new ServiceException("主帖id不能为空");
		}
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("主集团id不能为空");
		}
		return JSONMessage.success(topicService.findbyTopicDetail(id, ReqUtil.instance.getUserId(),groupId));
	}
	
	
	/**
     * @api {post} /community/getPubListTopic 获取用户发表的帖子
     * @apiVersion 1.0.0
     * @apiName getPubListTopic
     * @apiGroup 医生交流社区
     * @apiDescription 获取我的帖子列表
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}       groupId           集团id
     * @apiParam {String}       userId            用户id(不传就查询当前登录用户发表的帖子)
	 * @apiParam {Integer}		saveType          是否为查询草稿（0 否 1是  默认为0）
     * @apiParam {String}    	pageIndex         页码（从0开始）
     * @apiParam {String}    	pageSize      	  每页多少条数据
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
	 * @apiSuccess {String}     digest          摘要
	 * @apiSuccess {String[]}   digestImgUrls   题图数组
     * @apiSuccess {Number}     resultCode     返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("getPubListTopic")
	public JSONMessage getPubListTopic(TopicParam param,Integer userId){
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException(" 主集团id不能为空");
		}
		if(userId!=null&&userId!=0){
			param.setCreateUserId(userId);
		}else{
			param.setCreateUserId(ReqUtil.instance.getUserId());
		}
		
		return JSONMessage.success(topicService.findTopicByUserId(param));
	}

	/**
	 * @api {post} /community/findTopicKeyWord 搜索帖子接口
	 * @apiVersion 1.0.0
	 * @apiName findTopicKeyWord
	 * @apiGroup 医生交流社区
	 * @apiDescription 搜索帖子接口
	 *
	 * @apiParam {String}    	access_token      token
	 * @apiParam {String}       groupId           主集团id（必填）
	 * @apiParam {String}       keyWord           搜索关键字（必填）
	 * @apiParam {String}       circleId          圈子id
	 * @apiParam {String}    	pageIndex         页码（从0开始）
	 * @apiParam {String}    	pageSize      	    每页多少条数据
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
	 * @apiSuccess {Number}     resultCode     返回状态吗
	 * @apiAuthor  李明
	 * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findTopicKeyWord")
	public JSONMessage findTopicKeyWord(TopicParam param){

		if(StringUtil.isEmpty(param.getKeyWord())&&StringUtils.isEmpty(param.getCircleId())){
			throw new ServiceException("关键字和圈子id不能同时为空");
		}
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("医生集团id不能为空");
		}
		if(StringUtil.isEmpty(param.getKeyWord())){
			param.setCreateUserId(ReqUtil.instance.getUserId());
			return JSONMessage.success(topicService.findAllTopic(param,ReqUtil.instance.getUserId()));
		}
		return JSONMessage.success(topicService.findTopicKeyWord(param));
	}


	/**
     * @api {post} /community/findGroupLabel 查找自己主集团的标签
     * @apiVersion 1.0.0
     * @apiName findGroupLabel
     * @apiGroup 医生交流社区
     * @apiDescription 点赞帖子
     *
     * @apiParam   {String}    	access_token  token
     * @apiParam   {String}     groupId       医生主集团id
     * @apiSuccess {String}     id            集团id
     * @apiSuccess {String}    	groupName     集团名称
     * @apiSuccess {String[]}   labels        标签数组
     * @apiSuccess {Number}     resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findGroupLabel")
	public JSONMessage findGroupLabel(String groupId){
//		String groupId="";
//		List<GroupDoctorVO> groupDoctorVo=groupDoctor.getByDoctorId(ReqUtil.instance.getUserId());
//		if(groupDoctorVo==null||groupDoctorVo.size()==0){
//			throw new ServiceException("该用户没有找到主集团");
//		}else{
//			groupId=groupDoctorVo.get(0).getGroupId();
//		}
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("医生主集团id不能为空");
		}
		return JSONMessage.success(communityUserService.findByGoupId(groupId));
	}
	/**
     * @api {post} /community/topTopic 置顶帖子
     * @apiVersion 1.0.0
     * @apiName topTopic
     * @apiGroup 医生交流社区
     * @apiDescription 置顶帖子
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}    	groupId      	主集团id	
     * @apiParam {String}    	id              帖子id
     * 
     * @apiSuccess {Number}     resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("topTopic")
	public JSONMessage topTopic(String id,String groupId){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("帖子id不能为空");
		}
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团id不能为空");
		}
		topicService.topTopic(id, groupId);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/moveTopic 上移帖子
     * @apiVersion 1.0.0
     * @apiName moveTopic
     * @apiGroup 医生交流社区
     * @apiDescription 上移帖子
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}    	groupId      	主集团id	
     * @apiParam {String}    	id              帖子id
     * 
     * @apiSuccess {Number}     resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("moveTopic")
	public JSONMessage moveTopic(String id,String groupId){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("帖子id不能为空");
		}
		if(StringUtils.isEmpty(groupId)){
			throw new ServiceException("集团id不能为空");
		}
		topicService.moveTopic(id, groupId);
		return JSONMessage.success();
	}
	/**
     * @api {post} /community/undoTopTopic 取消置顶
     * @apiVersion 1.0.0
     * @apiName undoTopTopic
     * @apiGroup 医生交流社区
     * @apiDescription 取消置顶
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}    	id              帖子id
     * 
     * @apiSuccess {Number}     resultCode    返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("undoTopTopic")
	public JSONMessage undoTopTopic(String id){
		if(StringUtil.isEmpty(id)){
			throw new ServiceException("帖子id不能为空");
		}
		topicService.undoTopTopic(id);
		return JSONMessage.success();
	}
	
	/**
     * @api {post} /community/findPcTopicList 获取所有帖子列表（PC端使用）
     * @apiVersion 1.0.0
     * @apiName findPcTopicList
     * @apiGroup 医生交流社区
     * @apiDescription 获取所有帖子列表（Pc端使用 按照时候置顶 发帖时间排序）
     *
     * @apiParam {String}    	access_token      token
     * @apiParam {String}       groupId           主集团id
     * @apiParam {String}       circleId          圈子id
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
     * @apiSuccess {Object[]}   viedo           视频信息
     * @apiSuccess {Object[]}   files           附件信息
     * @apiSuccess {String}     circleId        圈子id
     * @apiSuccess {String}     circleName      圈子名称
     * @apiSuccess {String}     top             是否为置顶帖子(0 为置顶  1为非置顶)
     * @apiSuccess {Number}     resultCode     	返回状态吗
     * @apiAuthor  李明
     * @date 2016年7月26日14:52:48
	 */
	@RequestMapping("findPcTopicList")
	public JSONMessage findPcTopicList(TopicParam param){
		if(StringUtils.isEmpty(param.getGroupId())){
			throw new ServiceException("集团id不能为空");
		}
		param.setCreateUserId(ReqUtil.instance.getUserId());
		
		return JSONMessage.success(topicService.findPcTopic(param));
	}
	
	
	
}
