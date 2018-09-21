package com.dachen.health.community.service.impl;

import CommonParam.CommonParam;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.health.commons.constants.CommunityEnum;
import com.dachen.health.commons.constants.CommunitySendEvent;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.community.dao.ICircleDao;
import com.dachen.health.community.dao.ICommunityUsersDao;
import com.dachen.health.community.dao.IReplyDao;
import com.dachen.health.community.dao.ITopicDao;
import com.dachen.health.community.entity.param.TopicParam;
import com.dachen.health.community.entity.po.*;
import com.dachen.health.community.entity.vo.ReplyVo;
import com.dachen.health.community.entity.vo.TopicVo;
import com.dachen.health.community.entity.vo.UpTopic;
import com.dachen.health.community.service.ITopicService;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.entity.vo.GroupVO;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.util.DateUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TopicServiceImpl implements ITopicService {
	@Autowired
	private ITopicDao topicDao;
	@Autowired
	private IReplyDao replyDao;
	@Autowired
	private ICommunityUsersDao communityUserDao;
	@Autowired
	private IGroupDoctorDao groupDoctorDao;
	@Autowired
	private ICircleDao circleDao;
	@Autowired
	private UserManager userManger;

	@Override
	public void publish(TopicParam topicParam) {

		// 对帖子进行一些初始化的操作
		Topic topic = convert(topicParam);
		topicDao.insert(topic);
		// 发送指令
		new Thread() {
			public void run() {

				List<String> doctorIds = groupDoctorDao.findGroupMainList(topicParam.getGroupId());
				GroupVO groupVo=groupDoctorDao.findGroupById(topicParam.getGroupId());
				
				Map map = new HashMap();
				map.put("type",1);
				map.put("groupId", topicParam.getGroupId());
				if(groupVo!=null){
					map.put("groupName",groupVo.getName());
				}
				CommunitySendEvent.sendEvent(EventEnum.COMMUNITY_NEW_DYNAMIC, doctorIds, map,
						topicParam.getCreateUserId().toString());

			}
		}.start();

	}

	/**
	 * 转换类
	 * 
	 * @author liming
	 */
	private Topic convert(TopicParam param) {
		Topic topic = new Topic();

		topic.setCreateUserId(param.getCreateUserId());
		topic.setType(param.getType());
		topic.setGroupId(param.getGroupId());
		TopicContent content = new TopicContent();
		content.setTitle(param.getTitle());
		content.setText(param.getText());
		content.setRichTextLength(param.getRichTextLength());//富文本的长度
		content.setRichText(param.getRichText());

		content.setLabel(param.getLabel());
		content.setDigest(param.getDigest());//摘要
		topic.setTopicContent(content);
		Accessory accessory = new Accessory();
		accessory.setImgUrls(param.getImgUrls());
		accessory.setDigestImgUrls(param.getDigestImgUrls());//题图数组
		// 处理附件和视频信息
		accessory.setFiles(param.getFiles());
		accessory.setVideos(param.getVideo());

		topic.setAccessory(accessory);
		// 增加圈子
		topic.setCircleId(param.getCircleId());

		// 初始化一些信息
		topic.setCreateTime(System.currentTimeMillis());
		topic.setUpdateTime(System.currentTimeMillis());
		//处理保存草稿的逻辑
		if(param.getSaveType().intValue()==1){
			topic.setState(CommunityEnum.TopicState.草稿.getIndex());
		}else{
			topic.setState(CommunityEnum.TopicState.正常.getIndex());
		}

		topic.setCommunityType(CommunityEnum.CommunityType.医生交流社区.getIndex());
		topic.setLikeCount(0L);
		topic.setPageView(0L);
		topic.setReplies(0L);
		return topic;
	}

	@Override
	public void deleteTopic(String id) {
		Topic topic = topicDao.getByPK(Topic.class, id);
		if (topic == null || topic.getState().equals(CommunityEnum.TopicState.已删除.getIndex())) {
			throw new ServiceException("该帖子已被删除");
		}
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("state", CommunityEnum.TopicState.已删除.getIndex());

		topicDao.update(Topic.class, id, map);

	}

	@Override
	public void likeTopic(String id, Integer userId, String type) {
		Topic topic = topicDao.getByPK(Topic.class, id);
		if (topic == null || topic.getState().equals(CommunityEnum.TopicState.已删除.getIndex())) {
			throw new ServiceException("改帖子不存在或已经被删除");
		}
		Set<Integer> users = topic.getLikeUsers();
		if (users == null) {
			users = new HashSet<>();
		}
		Map<String, Object> map = new HashMap<String, Object>();
		if (type == null || type.equals("0")) {
			if (users.contains(userId.intValue())) {
				throw new ServiceException("已点赞");
			}
			users.add(userId);
			map.put("likeCount", topic.getLikeCount() + 1);
			map.put("likeUsers", users);
		} else {
			if (!users.contains(userId.intValue())) {
				throw new ServiceException("已取消点赞");
			}
			users.remove(userId.intValue());
			map.put("likeCount", topic.getLikeCount() - 1);
			map.put("likeUsers", users);
		}
		topicDao.update(Topic.class, id, map);
	}

	@Override
	public PageVO findAllTopic(TopicParam params, Integer userId) {
		List<Topic> listTopic = topicDao.findNoUpPageTopic(params);
		List<TopicVo> vo = new ArrayList<TopicVo>();

		for (Topic topic : listTopic) {

			TopicVo topicVo = this.topicTotopicVo(topic, userId, params.getGroupId());
			List<Reply> listReply = replyDao.find2ReplyList(topic.getId());
			List<ReplyVo> listReplyVo = new ArrayList<>();
			for (Reply reply : listReply) {
				ReplyVo replyVo = new ReplyVo();
				replyVo.setContent(reply.getContent());
				UserSession replyUser = ReqUtil.instance.getUser(reply.getCreateUserId());
				replyVo.setReplyUserId(reply.getCreateUserId());
				replyVo.setReplyName(replyUser.getName());
				replyVo.setReplyHeadUrl(replyUser.getHeadImgPath());
				replyVo.setTopicId(reply.getTopicId());
				if (reply.getToUserId() != null && reply.getToUserId() != 0) {
					replyVo.setToUsesId(reply.getToUserId());
					UserSession toUser = ReqUtil.instance.getUser(reply.getToUserId());
					replyVo.setToUserName(toUser.getName());
					replyVo.setToUserHeadUrl(toUser.getHeadImgPath());
				}
				listReplyVo.add(replyVo);
			}

			topicVo.setReplys(listReplyVo);
			vo.add(topicVo);

		}
		// new Thread(){
		// public void run(){
		// List<String> userIds=new ArrayList<>();
		// userIds.add(userId.toString());
		// Map map=new HashMap();
		// map.put("type", 0);//清楚新消息提醒
		// sendEvent(EventEnum.NEW_DYNAMIC, userIds,map);
		//
		// }
		// }.start();
		return new PageVO(vo, topicDao.findNoUpPagePageCount(params), params.getPageIndex(), params.getPageSize());
	}

	/**
	 * 查询置顶帖子
	 * @param params
	 * @return
     */
	@Override
	public List<UpTopic> findAllUpTopic(TopicParam params) {
		List<Topic> listTopic = topicDao.findUpPageTopic(params);
		List<UpTopic> upTopic=new ArrayList<>();
		UpTopic up;
		for(Topic topic:listTopic){
			up=new UpTopic();
			up.setId(topic.getId());
			up.setTitle(topic.getTopicContent().getTitle());
			up.setContent(topic.getTopicContent().getText());
			upTopic.add(up);
		}


		return upTopic;
	}

	public TopicVo topicTotopicVo(Topic topic, Integer userId, String groupId) {
		UserSession user = ReqUtil.instance.getUser(topic.getCreateUserId());
		TopicVo topicVo = new TopicVo();
		topicVo.setCreateName(user.getName());
		topicVo.setCreateTime(DateUtil.toCommunityTime(topic.getCreateTime()));
		topicVo.setUpdateTime(DateUtil.toCommunityTime(topic.getUpdateTime()));
		topicVo.setCreateUserId(topic.getCreateUserId().toString());
		topicVo.setDelete(userId.equals(topic.getCreateUserId()) ? "1" : "0");// 判定是否能删除
		topicVo.setHeadUrl(user.getHeadImgPath());
		//这边判断时候点赞要使用当前登录用户的id
		topicVo.setLike(topic.getLikeUsers() == null ? "0" : (topic.getLikeUsers().contains(ReqUtil.instance.getUserId()) ? "1" : "0"));// 判断帖子是否已点赞
		topicVo.setId(topic.getId());
		topicVo.setLabel(topic.getTopicContent().getLabel());
		topicVo.setTitle(topic.getTopicContent().getTitle());
		topicVo.setDigest(topic.getTopicContent().getDigest());
		topicVo.setRichTextLength(topic.getTopicContent().getRichTextLength());
		topicVo.setPageView(topic.getPageView());
		topicVo.setReplies(topic.getReplies());
		topicVo.setLikeCount(topic.getLikeCount());
		if (topic.getTopicContent().getText() != null) {
			if (topic.getTopicContent().getText().length() > 100) {
				topicVo.setText(topic.getTopicContent().getText().substring(0, 100) + "……");
			} else {
				topicVo.setText(topic.getTopicContent().getText());
			}

		}
		// 处理圈子信息
		topicVo.setCircleId(topic.getCircleId());
		if (StringUtil.isNotEmpty(topic.getCircleId())) {
			//处理脏数据
			Circle circle=circleDao.getByPK(Circle.class,topic.getCircleId());
			if(circle!=null){
				topicVo.setCircleName(circle.getName());
			}

		}

		topicVo.setType(topic.getType());
		if (topic.getAccessory() != null) {
			topicVo.setImgUrls(topic.getAccessory().getImgUrls());// 图片信息
			topicVo.setFiles(topic.getAccessory().getFiles());// 附件信息
			topicVo.setVideo(topic.getAccessory().getVideos());// 视频信息
			topicVo.setDigestImgUrls(topic.getAccessory().getDigestImgUrls());
		}
		// 判断时候为置顶帖子
		topicVo.setTop(topic.getTop() == null ? "1" : "0");

		// 判断是否已收藏
		CommunityUsers communityUser = communityUserDao.getBygroupId(userId, groupId);
		if (communityUser == null || communityUser.getCollects() == null || communityUser.getCollects().size() == 0) {
			topicVo.setCollect("0");
		} else {
			boolean flag = false;

			for (CollectIds collect : communityUser.getCollects()) {
				if (collect.equals(topic.getId())) {
					flag = true;
				}
			}
			if (flag) {
				topicVo.setCollect("1");
			} else {
				topicVo.setCollect("0");
			}
		}

		return topicVo;
	}

	@Override
	public TopicVo findbyTopicDetail(String id, Integer userId, String groupId) {
		Topic topic = topicDao.getByPK(Topic.class, id);
		if (topic == null || topic.getState().equals(CommunityEnum.TopicState.已删除.getIndex())) {
			throw new ServiceException("该帖子不存在或已经被删除");
		}
		UserSession user = ReqUtil.instance.getUser(topic.getCreateUserId());
		TopicVo topicVo = new TopicVo();
		topicVo.setCreateName(user.getName());
		topicVo.setCreateTime(DateUtil.toCommunityTime(topic.getCreateTime()));
		topicVo.setUpdateTime(DateUtil.toCommunityTime(topic.getUpdateTime()));
		topicVo.setDelete(userId.equals(topic.getCreateUserId()) ? "1" : "0");
		topicVo.setHeadUrl(user.getHeadImgPath());
		topicVo.setLike(topic.getLikeUsers() == null ? "0" : (topic.getLikeUsers().contains(userId.intValue()) ? "1" : "0"));// 判断帖子是否已点赞
		topicVo.setId(topic.getId());
		topicVo.setLabel(topic.getTopicContent().getLabel());
		topicVo.setTitle(topic.getTopicContent().getTitle());
		topicVo.setPageView(topic.getPageView());
		topicVo.setReplies(topic.getReplies());
		topicVo.setLikeCount(topic.getLikeCount());
		topicVo.setText(topic.getTopicContent().getText());
		topicVo.setRichText(topic.getTopicContent().getRichText());
		topicVo.setRichTextLength(topic.getTopicContent().getRichTextLength());//设置摘要长度
		topicVo.setCreateUserId(topic.getCreateUserId().toString());
		topicVo.setState(topic.getState());//设置状态
		topicVo.setDigest(topic.getTopicContent().getDigest());//设置摘要
		//加入集团
		topicVo.setGroupId(topic.getGroupId());
		// 处理圈子信息
		topicVo.setCircleId(topic.getCircleId());
		if (StringUtil.isNotEmpty(topic.getCircleId())) {
			topicVo.setCircleName(circleDao.getByPK(Circle.class, topic.getCircleId()).getName());
		}
		//处理视频、附件、等信息
		topicVo.setType(topic.getType());
		if (topic.getAccessory() != null) {
			topicVo.setImgUrls(topic.getAccessory().getImgUrls());// 图片信息
			topicVo.setFiles(topic.getAccessory().getFiles());// 附件信息
			topicVo.setVideo(topic.getAccessory().getVideos());// 视频信息
			topicVo.setDigestImgUrls(topic.getAccessory().getDigestImgUrls());//题图数组
		}
		// 判断时候为置顶帖子
		topicVo.setTop(topic.getTop() == null ? "1" : "0");

		// 判断是否已收藏
		CommunityUsers communityUser = communityUserDao.getBygroupId(userId, groupId);
		if (communityUser == null || communityUser.getCollects() == null || communityUser.getCollects().size() == 0) {
			topicVo.setCollect("0");
		} else {
			boolean flag = false;
			for (CollectIds collect : communityUser.getCollects()) {
				if (collect.equals(topic.getId())) {
					flag = true;
				}
			}
			if (flag) {
				topicVo.setCollect("1");
			} else {
				topicVo.setCollect("0");
			}
		}
		// 每调用一次这个接口，浏览数量应该加1
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pageView", topic.getPageView() + 1);
		topicDao.update(Topic.class, id, map);
		return topicVo;
	}

	@Override
	public PageVO findTopicByUserId(TopicParam param) {
		List<Topic> listTopic = topicDao.findTopicByUser(param);
		List<TopicVo> vo = new ArrayList<TopicVo>();
		for (Topic topic : listTopic) {

			TopicVo topicVo = this.topicTotopicVo(topic, param.getCreateUserId(), param.getGroupId());

			/**
			 * 注入回复信息
			 */

			List<Reply> listReply = replyDao.find2ReplyList(topic.getId());
			List<ReplyVo> listReplyVo = new ArrayList<>();
			for (Reply reply : listReply) {
				ReplyVo replyVo = new ReplyVo();
				replyVo.setContent(reply.getContent());
				UserSession replyUser = ReqUtil.instance.getUser(reply.getCreateUserId());
				replyVo.setReplyUserId(reply.getCreateUserId());
				replyVo.setReplyName(replyUser.getName());
				replyVo.setReplyHeadUrl(replyUser.getHeadImgPath());
				replyVo.setTopicId(reply.getTopicId());
				if (reply.getToUserId() != null && reply.getToUserId() != 0) {
					replyVo.setToUsesId(reply.getToUserId());
					UserSession toUser = ReqUtil.instance.getUser(reply.getToUserId());
					replyVo.setToUserName(toUser.getName());
					replyVo.setToUserHeadUrl(toUser.getHeadImgPath());
				}
				listReplyVo.add(replyVo);
			}

			topicVo.setReplys(listReplyVo);
			vo.add(topicVo);

		}

		return new PageVO(vo, topicDao.findTopicByUserCount(param),
				param.getPageIndex(), param.getPageSize());
	}

	@Override
	public PageVO findMyCollectTopicList(CommonParam param) {
		CommunityUsers communityUser = communityUserDao.getBygroupId(param.getUserId(), param.getGroupId());
		if (communityUser == null || communityUser.getCollects() == null) {
			return new PageVO(null, 0L, param.getPageIndex(), param.getPageSize());

		}
		param.setCollects(communityUser.getCollects());
		List<Topic> listTopic = topicDao.findMyCollectTopicListAll(param);

		// 需要对list进行处理
		listTopic = this.topicSort(listTopic, communityUser.getCollects());
		// 对list进行分页处理
		List<Topic> listPage = new ArrayList<>();
		if (listTopic.size() < param.getStart()) {

		} else {
			if ((param.getPageIndex() + 1) * param.getPageSize() < listTopic.size()) {
				listPage = listTopic.subList(param.getStart(), (param.getPageIndex() + 1) * param.getPageSize());
			} else {
				listPage = listTopic.subList(param.getStart(), listTopic.size());
			}
		}

		List<TopicVo> vo = new ArrayList<TopicVo>();

		for (Topic topic : listPage) {

			TopicVo topicVo = this.topicTotopicVo(topic, ReqUtil.instance.getUserId(), param.getGroupId());

			/**
			 * 注入回复信息
			 */

			List<Reply> listReply = replyDao.find2ReplyList(topic.getId());
			List<ReplyVo> listReplyVo = new ArrayList<>();
			for (Reply reply : listReply) {
				ReplyVo replyVo = new ReplyVo();
				replyVo.setContent(reply.getContent());
				UserSession replyUser = ReqUtil.instance.getUser(reply.getCreateUserId());
				replyVo.setReplyUserId(reply.getCreateUserId());
				replyVo.setReplyName(replyUser.getName());
				replyVo.setReplyHeadUrl(replyUser.getHeadImgPath());
				replyVo.setTopicId(reply.getTopicId());
				if (reply.getToUserId() != null && reply.getToUserId() != 0) {
					replyVo.setToUsesId(reply.getToUserId());
					UserSession toUser = ReqUtil.instance.getUser(reply.getToUserId());
					replyVo.setToUserName(toUser.getName());
					replyVo.setToUserHeadUrl(toUser.getHeadImgPath());
				}
				listReplyVo.add(replyVo);
			}

			topicVo.setReplys(listReplyVo);
			vo.add(topicVo);

		}

		return new PageVO(vo, (long) listTopic.size(), param.getPageIndex(), param.getPageSize());

	}

	/**
	 * 处理排序问题
	 * 
	 * @author liming
	 */
	private List<Topic> topicSort(List<Topic> listTopic, Set<CollectIds> set) {
		for (Topic topic : listTopic) {
			for (CollectIds collect : set) {
				if (collect.equals(topic.getId())) {
					topic.setCollectTime(collect.getTime());
				}
			}
		}
		Collections.sort(listTopic, new Comparator<Topic>() {
			@Override
			public int compare(Topic t1, Topic t2) {
				if (t1.getCollectTime() == null) {
					return 1;
				}
				if (t2.getCollectTime() == null) {
					return -1;
				}
				return t2.getCollectTime().compareTo(t1.getCollectTime());
				// if(t1.getCollectTime().<t2.getCollectTime()){
				// return 1;
				// }else if(t1.getCollectTime()>t2.getCollectTime()){
				// return -1;
				// }else{
				// return 0;
				// }

			}

		});

		return listTopic;
	}

	@Override
	public void topTopic(String id, String groupId) {
		Topic topic = topicDao.getByPK(Topic.class, id);
		if (topic.getTop() != null) {
			throw new ServiceException("该帖子已经置顶");
		}
		Topic firstTopic = topicDao.getFirstTopTopic(groupId);
		Map map = new HashMap<>();

		map.put("top", firstTopic.getTop() == null ? 1 : firstTopic.getTop() + 1);
		topicDao.update(Topic.class, id, map);
	}

	@Override
	public void moveTopic(String id, String groupId) {
		Topic topic = topicDao.getByPK(Topic.class, id);

		Topic upTopic = topicDao.getUpTopTopic(groupId, topic.getTop());
		if (upTopic == null) {
			throw new ServiceException("已经是第一个了");
		}
		Map map = new HashMap<>();
		Map upMap = new HashMap<>();
		map.put("top", upTopic.getTop());
		upMap.put("top", topic.getTop());
		// 分辨保存上一条记录和本条记录
		topicDao.update(Topic.class, topic.getId(), map);
		topicDao.update(Topic.class, upTopic.getId(), upMap);

	}

	@Override
	public void undoTopTopic(String id) {

		topicDao.removeTopicAttr(id, "top");
	}

	@Override
	public PageVO findTopicKeyWord(TopicParam param) {

		// 先按照用户名，在按照标题 or 内容搜索
		List<Integer> userIds = userManger.getDoctorIds(param.getKeyWord());
		List<Topic> listPage = new ArrayList<>();
		List<Topic> listTopic = new ArrayList<>();
		// 优先按照用户名匹配出对应的帖子
		if (userIds != null && userIds.size() != 0) {
			Query<Topic> q = topicDao.getByUserTopic(userIds, param.getGroupId(), param.getCircleId());
			listTopic = q.asList();
		}
		// 在进行标题匹配
		List<Topic> listTopicTitle = new ArrayList<>();
		listTopicTitle = topicDao.findTitleTopic(param.getKeyWord(), param.getGroupId(), userIds,
				param.getCircleId());
		List<ObjectId> ids=new ArrayList<>();
		for(Topic topic:listTopicTitle){
			ObjectId id=new ObjectId(topic.getId());
			ids.add(id);
		}
		//进行内容匹配
		List<Topic>  listContentTitle=new ArrayList<>();
		listContentTitle=topicDao.findContentTopic(param.getKeyWord(), param.getGroupId(), userIds, ids, param.getCircleId());
		
		
		// 将三个集合进行合并
		listTopic.addAll(listTopicTitle);//标题匹配
		listTopic.addAll(listContentTitle);//内容匹配
		// 对list进行分页处理
		if (listTopic.size() < param.getStart()) {

		} else {
			if ((param.getPageIndex() + 1) * param.getPageSize() < listTopic.size()) {
				listPage = listTopic.subList(param.getStart(), (param.getPageIndex() + 1) * param.getPageSize());
			} else {
				listPage = listTopic.subList(param.getStart(), listTopic.size());
			}
		}

		List<TopicVo> vo = new ArrayList<TopicVo>();

		for (Topic topic : listPage) {

			TopicVo topicVo = this.topicTotopicVo(topic, ReqUtil.instance.getUserId(), param.getGroupId());

			/**
			 * 注入回复信息
			 */

			List<Reply> listReply = replyDao.find2ReplyList(topic.getId());
			List<ReplyVo> listReplyVo = new ArrayList<>();
			for (Reply reply : listReply) {
				ReplyVo replyVo = new ReplyVo();
				replyVo.setContent(reply.getContent());
				UserSession replyUser = ReqUtil.instance.getUser(reply.getCreateUserId());
				replyVo.setReplyUserId(reply.getCreateUserId());
				replyVo.setReplyName(replyUser.getName());
				replyVo.setReplyHeadUrl(replyUser.getHeadImgPath());
				replyVo.setTopicId(reply.getTopicId());
				if (reply.getToUserId() != null && reply.getToUserId() != 0) {
					replyVo.setToUsesId(reply.getToUserId());
					UserSession toUser = ReqUtil.instance.getUser(reply.getToUserId());
					replyVo.setToUserName(toUser.getName());
					replyVo.setToUserHeadUrl(toUser.getHeadImgPath());
				}
				listReplyVo.add(replyVo);
			}

			topicVo.setReplys(listReplyVo);
			vo.add(topicVo);

		}

		return new PageVO(vo, (long) listTopic.size(), param.getPageIndex(), param.getPageSize());

	}

	@Override
	public PageVO findPcTopic(TopicParam param) {
		List<Topic> listTopic = topicDao.findPageTopic(param);
		List<TopicVo> vo = new ArrayList<TopicVo>();

		for (Topic topic : listTopic) {

			TopicVo topicVo = this.topicTotopicVo(topic, param.getCreateUserId(), param.getGroupId());
			//时间进行特殊处理
			//topicVo.setCreateTime(DateUtil.formatDate2Str(topic.getCreateTime(),new SimpleDateFormat("yyyy-MM-dd HH:mm")));
			//List<Reply> listReply = replyDao.find2ReplyList(topic.getId());
			vo.add(topicVo);

		}
	
		return new PageVO(vo, topicDao.findPageCount(param), param.getPageIndex(), param.getPageSize());
	}

	@Override
	public void editTopic(TopicParam param) {
		Topic topic=topicDao.getByPK(Topic.class,param.getId());
		if(topic==null || topic.getState().equals(CommunityEnum.TopicState.已删除.getIndex())){
			throw new ServiceException("帖子已被删除或不存在");
		}
		Map<String,Object> map=new HashMap();
		//处理圈子
		map.put("circleId",param.getCircleId());

		if(param.getSaveType().intValue()==0){
			map.put("state", CommunityEnum.TopicState.正常.getIndex());
		}else{
			map.put("state",CommunityEnum.TopicState.草稿.getIndex());
		}
		TopicContent content=topic.getTopicContent();

		content.setText(param.getText());
		content.setDigest(param.getDigest());
		content.setRichTextLength(param.getRichTextLength());


		content.setRichText(param.getRichText());


		content.setTitle(param.getTitle());

		map.put("topicContent",content);
		//处理视频附件等信息,为空就职位空
		Accessory accessory=new Accessory();
		accessory.setFiles(param.getFiles());
		accessory.setVideos(param.getVideo());
		accessory.setDigestImgUrls(param.getDigestImgUrls());//处理题录信息
		//if(accessory!=null){
			map.put("accessory",accessory);
		//}
		map.put("updateTime",System.currentTimeMillis());//更新帖子最后更新时间
		topicDao.update(Topic.class,param.getId(),map);



	}
}
