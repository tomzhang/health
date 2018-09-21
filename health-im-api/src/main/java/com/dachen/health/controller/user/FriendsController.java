package com.dachen.health.controller.user;

import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import com.dachen.sdk.exception.HttpApiException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.friend.entity.param.FriendReqQuery;
import com.dachen.health.friend.entity.po.FriendReq;
import com.dachen.health.friend.entity.po.FriendSetting;
import com.dachen.health.friend.entity.vo.SalesGoodsFileVO;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.health.user.entity.vo.UserDetailVO;
import com.dachen.util.ReqUtil;
/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： FriendsController<br>
 * Description：好友相关 <br>
 * @author limiaomiao
 * @crateTime 2015年7月27日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/friends")
public class FriendsController {

	@Autowired
	private FriendsManager friendsManager;

 

 

	/**
	 * 
	 * </p>好友添加</p>
	 * @param toUserId
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping("/add")
	public JSONMessage addFriends(
			@RequestParam(value = "toUserId") Integer toUserId) {
		friendsManager.addFriends(ReqUtil.instance.getUserId(), toUserId);

		return JSONMessage.success("加好友成功");
	}

	/**
	 * 
	 * </p>好友添加(批量)</p>
	 * @param toUserids
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping("/adds")
	public JSONMessage addFriends1(Integer[] toUserids) {
		for (Integer toUserId : toUserids) {
			friendsManager.addFriends(ReqUtil.instance.getUserId(), toUserId);
		}
		return JSONMessage.success("加好友成功");
	}
	 

	/**
	 * 
	 * @api {[get,post]} /friends/delete 删除好友
	 * @apiVersion 1.0.0
	 * @apiName delete
	 * @apiGroup 用户好友	
	 * @apiDescription 删除好友
	 * @apiParam  	{String}    access_token	token
	 * @apiParam	{Integer}	toUserId		对方用户Id
	 * 
	 * @apiAuthor limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping("/delete")
	public JSONMessage deleteFriends(@RequestParam Integer toUserId) throws HttpApiException {
		friendsManager.deleteFriends(ReqUtil.instance.getUserId(), toUserId);
		return JSONMessage.success("删除好友成功");
	}

 
 
	/**
	 * 
	 * </p>黑名单列表</p>
	 * @return List<{@link UserDetailVO}>
	 * @author limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping("/blacklist") 
	public JSONMessage queryBlacklist() { 
		List<Object> data = friendsManager.queryBlacklist(ReqUtil.instance.getUserId());

		return JSONMessage.success(null, data);
	}

	 
 
	/**
	 * 
	 * </p>好友设置</p>
	 * @param toUserId
	 * @param settings {@link FriendSetting}
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping("/setProfile")
	public JSONMessage setFriends(@RequestParam Integer toUserId,FriendSetting settings) {
		int userId = ReqUtil.instance.getUserId();
		boolean flag = friendsManager.setFriends(userId, toUserId,settings);
		return JSONMessage.success(null, flag);
	}
	
	/**
	 * 
	 * @api {[get,post]}  /friends/sessionList  会话列表
	 * @apiVersion 1.0.0
	 * @apiName sessionList	
	 * @apiGroup 用户好友	
	 * @apiDescription 获取用户会话列表
	 * @apiParam  {String}    access_token          token
	 * @apiSuccess {User}  msgs 				 用户消息
	 * @apiSuccess {User}  confirmMsg 				 用户认真消息 
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年7月31日
	 */
	@RequestMapping("/sessionList")
	public JSONMessage getSessionList(){
		int userId=ReqUtil.instance.getUserId();
		Object data=friendsManager.getSessionList(userId,0,0);
		return JSONMessage.success(null,data);
	}
	
	/**
	 * 
	 * @api {[get,post]} /friends/userMsgs 用户消息
	 * @apiVersion 1.0.0
	 * @apiName userMsgs
	 * @apiGroup 用户好友	
	 * @apiDescription 好友消息
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String} 	 toUserId			         好友id
	 * @apiParam 		{long} 	 lastTime			    最后修改时间(13位 时间戳)
	 * @apiSuccess 	{User} 			msg 			消息
	 * @apiAuthor 李淼淼
	 * @author 李淼淼
	 * @date 2015年7月31日
	 */
	@RequestMapping("/userMsgs")
	public JSONMessage getMsgByUser(@RequestParam int toUserId,long lastTime){
		int userId=ReqUtil.instance.getUserId();
		Object data=friendsManager.getMsgsByUser(userId, toUserId,lastTime);
		return JSONMessage.success(null,data);
				
	}
	
	/**
	 * 
	 * @api {[get,post]} /friends/sendApply 发送加好友申请
	 * @apiVersion 1.0.0
	 * @apiName sendApply
	 * @apiGroup 用户好友	
	 * @apiDescription 如果对方需要验证，用此接口发送加好友申请
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String} 	 toUserId			        好友id
	 * @apiParam 	 {String} 	 applyContent			申请备注
	 * 
	 * 

	 * @apiAuthor 屈军利
	 * @date 2015年8月26日
	 */
	@RequestMapping("/sendApply")
	public JSONMessage sendApply(@RequestParam Integer toUserId,@RequestParam String applyContent) throws HttpApiException {
		 int userId=ReqUtil.instance.getUserId();
		 friendsManager.sendApplyAddFriend(userId,toUserId, applyContent);
		 return JSONMessage.success("发送成功");
				
	}
	 
	
	/**
	 * 
	 * @api {[get,post]} /friends/applyAdd 加用户为好友
	 * @apiVersion 1.0.0
	 * @apiName applyAdd
	 * @apiGroup 用户好友	
	 * @apiDescription 如果对方不需要验证，则直接用此接口加好友
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String} 	 toUserId			        好友id
	 * 
	 * @apiSuccess {User}   user 				添加的好友详细信息，数据结构为user
	 * 

	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/applyAdd")
	public JSONMessage applyAddFriend(@RequestParam Integer toUserId) throws HttpApiException {
		 int userId=ReqUtil.instance.getUserId();
		 Object data= friendsManager.applyAddFriend(userId, toUserId,true);
		 return JSONMessage.success("添加成功",data);
				
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /friends/replyAdd 处理加好友申请
	 * @apiVersion 1.0.0
	 * @apiName replyAdd
	 * @apiGroup 用户好友	
	 * @apiDescription 处理加好友申请，（此接口暂不需要，用户加好友不需验证）
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String} 	 id			        请求Id
	 * @apiParam 	 {int} 	 result			         处理结果：2：接受；

	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/replyAdd")
	public JSONMessage replyAddFriend(@RequestParam String id, @RequestParam int result) throws HttpApiException {
		 friendsManager.replyAddFriend(id, result);
		return JSONMessage.success();
				
	}
	
	/**
	 * 
	 * @api {[get,post]} /friends/getFriendReqById 获取好友请求
	 * @apiVersion 1.0.0
	 * @apiName getFriendReqById
	 * @apiGroup 用户好友	
	 * @apiDescription 获取好友请求
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String}    id          id
     * 
     * @apiSuccess {String}  	 id       请求id
     * @apiSuccess {Integer}  	 fromUserId    发送者id
     * @apiSuccess {Integer}   	toUserId      接收者id
     * @apiSuccess {String}  	 fromUserName    发送者名称
     * @apiSuccess {String}   	toUserName     接收者名称
     * @apiSuccess {String}  	fromHeadPicFileName    发送者头像
     * @apiSuccess {String}   	toHeadPicFileName     接收者头像
     * @apiSuccess {String}    applyContent   请求内容
     * @apiSuccess {int}   	  status    状态：1:等待验证；2：已通过；3：已拒绝
     * @apiSuccess {long}      createTime    创建时间
     * @apiSuccess {long}      updateTime   更新时间
     * @apiSuccess {Integer}   userReqType   1 代表是医药代表的请求      0  不是

     *
     * 
	 * @apiAuthor xieping
	 * @date 2015年8月11日
	 */
	@RequestMapping("/getFriendReqById")
	public JSONMessage getFriendReqById(String id) {
		return JSONMessage.success(friendsManager.getFriendReqById(id));
	}
	
	
	/**
	 * 
	 * @api {[get,post]} /friends/getFriendReq 获取好友请求
	 * @apiVersion 1.0.0
	 * @apiName getFriendReq
	 * @apiGroup 用户好友	
	 * @apiDescription 获取我发送的加好友请求+我收到的加好友请求
	 * @apiParam 	 {String}     access_token      token
	 * @apiParam 	 {Integer}    pageIndex         查询的页码，从0开始
	 * @apiParam 	 {Integer}    pageSize          每页大小，默认15
	 * @apiParam 	 {Integer}    userReqType       用户申请类型（供医药代表使用，医药代码传1）
	 * 
	 * @apiSuccess {PageVO}     pageVO             分页对象
     * @apiSuccess {long}      pageVO.total             总记录数
     * @apiSuccess {int}   pageVO.pageIndex            返回的页码
     * @apiSuccess {int}   pageVO.pageCount            总页数
     * @apiSuccess {List}   pageVO.pageData            list中每个对象是一个FriendReq
     * @apiSuccess {List}   pageVO.pageData.friendReq       一个具体验证请求对象
     * 
     * @apiSuccess {String}  	 pageVO.pageData.friendReq.id       请求id
     * @apiSuccess {Integer}  	 pageVO.pageData.friendReq.fromUserId    发送者id
     * @apiSuccess {Integer}   	pageVO.pageData.friendReq.toUserId      接收者id
     * @apiSuccess {String}  	 pageVO.pageData.friendReq.fromUserName    发送者名称
     * @apiSuccess {String}   	pageVO.pageData.friendReq.toUserName     接收者名称
     * @apiSuccess {String}  	 pageVO.pageData.friendReq.fromHeadPicFileName    发送者头像
     * @apiSuccess {String}   	pageVO.pageData.friendReq.toHeadPicFileName     接收者头像
     * @apiSuccess {String}     pageVO.pageData.friendReq.applyContent   请求内容
     * @apiSuccess {int}   	   pageVO.pageData.friendReq.status    状态：1:等待验证；2：已通过；3：已拒绝
     * @apiSuccess {long}      pageVO.pageData.friendReq.createTime    创建时间
     * @apiSuccess {long}      pageVO.pageData.friendReq.updateTime   更新时间
     * @apiSuccess {Integer}   pageVO.pageData.friendReq.userReqType   1 代表是医药代表的请求      0  不是

     *
     * 
	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/getFriendReq")
	public JSONMessage getFriendReq(FriendReqQuery friendReqQuery) throws HttpApiException {
		friendReqQuery.setUserId(ReqUtil.instance.getUserId());
		Object data=friendsManager.getFriendReq(friendReqQuery);
		return JSONMessage.success(null,data);
	}
	
	/**
	 * 
	 * @api {[get,post]} /friends/addFriendReq 添加好友请求
	 * @apiVersion 1.0.0
	 * @apiName addFriendReq
	 * @apiGroup 用户好友	
	 * @apiDescription 添加好友请求
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {Integer}    fromUserId           用户id
	 * @apiParam 	 {Integer}    toUserId             被添加好友的用户id
	 * @apiParam 	 {List}    saleGoodFileList        品种组文件列表
	 * @apiParam 	 {String}    applyContent          添加好友备注
	 * 
	 * @apiSuccess {String}     resultCode          1：正常
     * 
	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/addFriendReq")
	public JSONMessage addFriendReq(FriendReq friendReq){
		Object data=friendsManager.addFriendReq(friendReq);
		return JSONMessage.success(null,data);
	}
	
	/**
	 * 
	 * @api {[get,post]} /friends/addPhoneFriend 加手机联系人为好友
	 * @apiVersion 1.0.0
	 * @apiName addPhoneFriend
	 * @apiGroup 用户好友	
	 * @apiDescription 加手机联系人为好友，如果state=1:代表添加成功，从map中取对方user对象和msg；如果state=2:对方需要验证，从map中取对方userId和msg；如果state=3:对方不是平台医生，从map中取msg
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String} 	 phone			        对方号码
	 * 
	 * @apiSuccess {map}  map  state、msg、user、userId
	 * 

	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/addPhoneFriend")
	public Map addPhoneFriend(@RequestParam String phone) throws HttpApiException {
		int userId = ReqUtil.instance.getUserId();
		Object data = friendsManager.addPhoneFriend(userId, phone);
		return JSONMessage.success("处理成功", data);

	}
	

	/**
	 * 
	 * @api {[get,post]} /friends/sendInviteMsg 发送短信邀请
	 * @apiVersion 1.0.0
	 * @apiName sendInviteMsg
	 * @apiGroup 用户好友	
	 * @apiDescription 邀请对方加入平台
	 * @apiParam 	 {String}    access_token          token
	 * @apiParam 	 {String} 	 phone			        对方号码
	 *
	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/sendInviteMsg")
	public JSONMessage sendInviteMsg(@RequestParam String phone){
		 int userId=ReqUtil.instance.getUserId();
		 friendsManager.sendInviteMsgAsync(userId, phone);
		 return JSONMessage.success("处理成功");
				
	}
	
	/**
	 * 
	 * @api {[get,post]} /friends/addGroupFriend 添加同集团好友
	 * @apiVersion 1.0.0
	 * @apiName addGroupFriend
	 * @apiGroup 用户好友	
	 * @apiDescription 添加同集团好友
	 * @apiParam 	 {Integer}	userId		用户Id
	 * @apiParam 	 {Integer}	toUserId	对方用户Id
	 * 
	 * @apiSuccess	 {User}   user 			添加的好友详细信息，数据结构为user
	 * 
	 * @apiAuthor 谢平
	 * @date 2015年11月4日
	 */
	@RequestMapping("/addGroupFriend")
	public JSONMessage addGroupFriend(Integer userId, Integer toUserId) {
		if (userId == null) {
			userId = ReqUtil.instance.getUserId();
		}
		Object data = friendsManager.addGroupFriend(userId, toUserId);
		return JSONMessage.success("success", data);

	}
	
	/**
	 * 不公开，统一使用deleteFriends
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	@RequestMapping("/delGroupFriend")
	public JSONMessage delGroupFriend(Integer userId, Integer toUserId) {
		if (userId == null) {
			userId = ReqUtil.instance.getUserId();
		}
		friendsManager.delGroupFriend(userId, toUserId);
		return JSONMessage.success("success");

	}
	
	// add by GengChao 2016/8/5 提供给drugorg使用的相关接口
	/**
	 * add by gengchao 2016/8/5  校验好友关系是否存在等待验证状态
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	@RequestMapping("/getUnTreatedFriendReq")
	public JSONMessage getUnTreatedFriendReq(Integer userId, Integer toUserId){
		return JSONMessage.success(null, friendsManager.getUnTreatedFriendReq(userId, toUserId));
	}
	
	/**
	 * add by gengchao 2016/8/5 获取用户好友关系
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	@RequestMapping("/getFriendReqOfDrugOrg")
	public JSONMessage getFriendReq(Integer userId, Integer toUserId){
		return JSONMessage.success(null, friendsManager.getFriendReq(userId, toUserId));
	}
	
	/**
	 * add by gengchao 2016/8/5 删除用户好友关系
	 * @param id
	 * @return
	 */
	@RequestMapping("/deleteFriendReqById")
	public JSONMessage deleteFriendReqById(String id){
		return JSONMessage.success(null, friendsManager.deleteFriendReqById(id));
	}
	
	/**
	 * add by gengchao 2016/8/5 添加好友关系
	 * @param friendReq
	 * @return
	 */
	@RequestMapping("/addFriendReqOfDrugOrg")
	public JSONMessage addFriendReqOfDrugOrg(FriendReq friendReq,String saleGoodFileListJson){
		if(friendReq==null) throw new ServiceException("参数不能为null");
		if(StringUtils.isNotBlank(saleGoodFileListJson)){
			List<SalesGoodsFileVO> fileListReq = JSONArray.parseArray(saleGoodFileListJson, SalesGoodsFileVO.class);
			friendReq.setSaleGoodFileList(fileListReq);
		}
		return JSONMessage.success(null, friendsManager.addFriendReqOfDrugOrg(friendReq));
	}
	
	/**
	 * add by gengchao 2016/8/5 更新好友关系 
	 * @param friendReq
	 * @return
	 */
	@RequestMapping("/updateFriendReq")
	public JSONMessage updateFriendReq(FriendReq friendReq){
		if(friendReq==null) throw new ServiceException("参数不能为null");
		return JSONMessage.success(null, friendsManager.updateFriendReq(friendReq));
	}
	// $
	
}
