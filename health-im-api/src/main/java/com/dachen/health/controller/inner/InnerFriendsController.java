package com.dachen.health.controller.inner;

import com.alibaba.fastjson.JSON;
import com.dachen.sdk.exception.HttpApiException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;

import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.friend.entity.param.FriendReqQuery;
import com.dachen.health.friend.entity.po.FriendReq;
import com.dachen.health.friend.entity.vo.SalesGoodsFileVO;
import com.dachen.health.friend.service.FriendsManager;
import com.dachen.util.ReqUtil;

@RestController
@RequestMapping("inner_api/friends")
public class InnerFriendsController {

	@Autowired
	private FriendsManager friendsManager;

	/**
	 * 
	 * @api {[get,post]} inner_api/friends/getFriendReqById 获取好友请求
	 * @apiVersion 1.0.0
	 * @apiName getFriendReqById
	 * @apiGroup 内部api
	 * @apiDescription 获取好友请求
	 * @apiParam {String} access_token token
	 * @apiParam {String} id id
	 * 
	 * @apiSuccess {String} id 请求id
	 * @apiSuccess {Integer} fromUserId 发送者id
	 * @apiSuccess {Integer} toUserId 接收者id
	 * @apiSuccess {String} fromUserName 发送者名称
	 * @apiSuccess {String} toUserName 接收者名称
	 * @apiSuccess {String} fromHeadPicFileName 发送者头像
	 * @apiSuccess {String} toHeadPicFileName 接收者头像
	 * @apiSuccess {String} applyContent 请求内容
	 * @apiSuccess {int} status 状态：1:等待验证；2：已通过；3：已拒绝
	 * @apiSuccess {long} createTime 创建时间
	 * @apiSuccess {long} updateTime 更新时间
	 * @apiSuccess {Integer} userReqType 1 代表是医药代表的请求 0 不是
	 *
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
	 * @api {[get,post]} inner_api/friends/getFriendReq 获取好友请求
	 * @apiVersion 1.0.0
	 * @apiName getFriendReq
	 * @apiGroup 内部api
	 * @apiDescription 获取我发送的加好友请求+我收到的加好友请求
	 * @apiParam {String} access_token token
	 * @apiParam {Integer} pageIndex 查询的页码，从0开始
	 * @apiParam {Integer} pageSize 每页大小，默认15
	 * @apiParam {Integer} userReqType 用户申请类型（供医药代表使用，医药代码传1）
	 * 
	 * @apiSuccess {PageVO} pageVO 分页对象
	 * @apiSuccess {long} pageVO.total 总记录数
	 * @apiSuccess {int} pageVO.pageIndex 返回的页码
	 * @apiSuccess {int} pageVO.pageCount 总页数
	 * @apiSuccess {List} pageVO.pageData list中每个对象是一个FriendReq
	 * @apiSuccess {List} pageVO.pageData.friendReq 一个具体验证请求对象
	 * 
	 * @apiSuccess {String} pageVO.pageData.friendReq.id 请求id
	 * @apiSuccess {Integer} pageVO.pageData.friendReq.fromUserId 发送者id
	 * @apiSuccess {Integer} pageVO.pageData.friendReq.toUserId 接收者id
	 * @apiSuccess {String} pageVO.pageData.friendReq.fromUserName 发送者名称
	 * @apiSuccess {String} pageVO.pageData.friendReq.toUserName 接收者名称
	 * @apiSuccess {String} pageVO.pageData.friendReq.fromHeadPicFileName 发送者头像
	 * @apiSuccess {String} pageVO.pageData.friendReq.toHeadPicFileName 接收者头像
	 * @apiSuccess {String} pageVO.pageData.friendReq.applyContent 请求内容
	 * @apiSuccess {int} pageVO.pageData.friendReq.status 状态：1:等待验证；2：已通过；3：已拒绝
	 * @apiSuccess {long} pageVO.pageData.friendReq.createTime 创建时间
	 * @apiSuccess {long} pageVO.pageData.friendReq.updateTime 更新时间
	 * @apiSuccess {Integer} pageVO.pageData.friendReq.userReqType 1 代表是医药代表的请求
	 *             0 不是
	 *
	 * 
	 * 
	 * @apiAuthor 屈军利
	 * @date 2015年8月11日
	 */
	@RequestMapping("/getFriendReq")
	public JSONMessage getFriendReq(FriendReqQuery friendReqQuery) throws HttpApiException {
		friendReqQuery.setUserId(ReqUtil.instance.getUserId());
		Object data = friendsManager.getFriendReq(friendReqQuery);
		return JSONMessage.success(null, data);
	}

	// add by GengChao 2016/8/5 提供给drugorg使用的相关接口
	/**
	 * add by gengchao 2016/8/5 校验好友关系是否存在等待验证状态
	 * 
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	@RequestMapping("/getUnTreatedFriendReq")
	public JSONMessage getUnTreatedFriendReq(Integer userId, Integer toUserId) {
		return JSONMessage.success(null, friendsManager.getUnTreatedFriendReq(userId, toUserId));
	}
	
	/**
	 * add by gengchao 2016/8/5 获取用户好友关系
	 * @param userId
	 * @param toUserId
	 * @return
	 */
	@RequestMapping("/getFriendReqOfDrugOrg")
	public JSONMessage getFriendReqOfDrugOrg(Integer userId, Integer toUserId){
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

    @ApiOperation(value = "通过用户Id集合获取好友列表", notes = "通过用户Id集合获取好友列表", response = Map.class)
    @RequestMapping(value = "/getFriends")
    public JSONMessage getFriends(@ApiParam(value = "用户Id集合", required = true) @RequestParam List<Integer> userIdList){
        return JSONMessage.success(null, JSON.toJSON(friendsManager.getFriends(userIdList)));
    }

}
