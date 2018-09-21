package com.dachen.health.common.controller;

import java.util.List;

import javax.annotation.Resource;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.group.entity.param.MemberParam;
import com.dachen.health.group.entity.param.RoomParam;
import com.dachen.health.group.entity.vo.RoomVO;
import com.dachen.health.group.service.RoomManager;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

@RestController
@RequestMapping("/room")
public class RoomController {

	@Resource(name = RoomManager.BEAN_ID)
	private RoomManager roomManager;
	@Autowired
	private UserManager userManager;

	/**
	 * 新增房间
	 * 
	 * @param room
	 * @param text
	 * @return
	 */
	@RequestMapping("/add")
	public JSONMessage add(@ModelAttribute RoomParam room,
			@RequestParam(defaultValue = "") String text) {
		List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON
				.parseArray(text, Integer.class);
		Object data = roomManager.add(userManager.getUser(ReqUtil.instance.getUserId()),
				room, idList);
		return JSONMessage.success(null, data);
	}

	/**
	 * 删除房间
	 * 
	 * @param roomId
	 * @return
	 */
	@RequestMapping("/delete")
	public JSONMessage delete(@RequestParam String roomId) {
		roomManager.delete(userManager.getUser(ReqUtil.instance.getUserId()),
				new ObjectId(roomId));
		return JSONMessage.success();
	}

	/**
	 * 更新房间
	 * 
	 * @param roomId
	 * @param roomName
	 * @param notice
	 * @param desc
	 * @return
	 */
	@RequestMapping("/update")
	public JSONMessage update(@RequestParam String roomId,
			@RequestParam(defaultValue = "") String roomName,
			@RequestParam(defaultValue = "") String notice,
			@RequestParam(defaultValue = "") String desc) {
		// if (StringUtil.isEmpty(roomName) && StringUtil.isEmpty(notice)) {
		//
		// } else {
		// User user = userManager.getUser(ReqUtil.instance.getUserId());
		// roomManager.update(user, new ObjectId(roomId), roomName, notice,
		// desc);
		// }
		User user = userManager.getUser(ReqUtil.instance.getUserId());
		roomManager.update(user, new ObjectId(roomId), roomName, notice, desc);
		
		return JSONMessage.success();
	}

	/**
	 * 根据房间Id获取房间
	 * 
	 * @param roomId
	 * @return
	 */
	@RequestMapping("/get")
	public JSONMessage get(@RequestParam String roomId) {
		Object data = roomManager.get(new ObjectId(roomId));
		return JSONMessage.success(null, data);
	}

	/**
	 * 获取房间列表（按创建时间排序）
	 * 
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
//	@RequestMapping("/list")
//	public JSONMessage list(@RequestParam(defaultValue = "0") int pageIndex,
//			@RequestParam(defaultValue = "10") int pageSize) {
//		Object data = roomManager.selectList(pageIndex, pageSize);
//		return JSONMessage.success(null, data);
//	}

	@RequestMapping("/member/update")
	public JSONMessage updateMember(@RequestParam String roomId,
			@ModelAttribute MemberParam member,
			@RequestParam(defaultValue = "") String text) {
		List<Integer> idList = StringUtil.isEmpty(text) ? null : JSON
				.parseArray(text, Integer.class);
		User user = userManager.getUser(ReqUtil.instance.getUserId());
		
		ObjectId roomObjId = new ObjectId(roomId);
		if (null == idList)
		{
			roomManager.updateMember(user,roomObjId, member);
		}
		else
		{
			roomManager.updateMember(user, roomObjId, idList);
		}

		RoomVO room = roomManager.get(roomObjId);
		return JSONMessage.success(null,room);
	}

	@RequestMapping("/member/delete")
	public JSONMessage deleteMember(@RequestParam String roomId,
			@RequestParam int userId) {
		User user = userManager.getUser(ReqUtil.instance.getUserId());
		roomManager.deleteMember(user, new ObjectId(roomId), userId);
		return JSONMessage.success();
	}

	@RequestMapping("/member/get")
	public JSONMessage getMember(@RequestParam String roomId,
			@RequestParam int userId) {
		Object data = roomManager.getMember(new ObjectId(roomId), userId);
		return JSONMessage.success(null, data);
	}

	@RequestMapping("/member/list")
	public JSONMessage getMemberList(@RequestParam String roomId) {
		Object data = roomManager.getMemberList(new ObjectId(roomId));
		return JSONMessage.success(null, data);
	}
	
	@Deprecated
	@RequestMapping("/join")
	public JSONMessage join(@RequestParam String roomId,
			@RequestParam(defaultValue = "2") int type) {
		roomManager.join(ReqUtil.instance.getUserId(), new ObjectId(roomId), type);
		return JSONMessage.success();
	}

	@Deprecated
	@RequestMapping("/leave")
	public JSONMessage leave(@RequestParam String roomId) {
		roomManager.leave(ReqUtil.instance.getUserId(), new ObjectId(roomId));
		return JSONMessage.success();
	}

	@RequestMapping("/list/his")
	public JSONMessage historyList(@RequestParam(defaultValue = "0") int roomType) {
		Object data = roomManager.selectHistoryList(ReqUtil.instance.getUserId(), roomType);
		// Object data = roomManager.selectHistoryList(ReqUtil.instance.getUserId(),
		// type,
		// pageIndex, pageSize);
		return JSONMessage.success(null, data);
	}
	
	/**
	 * adminFlag:1表示设置管理员，0表示取消管理员
	 * @param roomId
	 * @param userId
	 * @param adminFlag
	 * @return
	 */
	@RequestMapping("/member/setadmin")
	public JSONMessage setAdmin(@RequestParam String roomId,@RequestParam int userId,int adminFlag) {
		int currentUerId = ReqUtil.instance.getUserId();
		boolean sucess = false;
		if(adminFlag==0)
		{
			sucess = roomManager.cancalAdministator(new ObjectId(roomId),userId, currentUerId);
		}
		else if(adminFlag==1)
		{
			sucess = roomManager.setAdministator(new ObjectId(roomId),userId, currentUerId);
		}
		else
		{
			return JSONMessage.failure("参数错误");
		}
		
		if(sucess)
		{
			return JSONMessage.success();
		}
		return JSONMessage.failure("设置管理员失败");
	}
	
	/**
	 * 用户设置是否接收群消息
	 * @param roomId
	 * @param userId
	 * @param isReceive（0表示屏蔽消息，1表示接收消息）
	 * @return
	 */
	@RequestMapping("/member/refuseMessage")
	public JSONMessage refuseMessage (@RequestParam String roomId,@RequestParam int userId,int isReceive) {
		roomManager.refuseMessage(userId, new ObjectId(roomId), isReceive);
		return JSONMessage.success();
	}

}
