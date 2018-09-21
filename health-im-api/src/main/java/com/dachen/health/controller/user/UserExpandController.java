package com.dachen.health.controller.user;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.constants.Constants;
import com.dachen.health.common.controller.AbstractController;
import com.dachen.health.commons.dao.UserExpandRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.PhotoVO;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.User.UserSettings;
import com.dachen.health.group.entity.po.Room;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * 
 * ProjectName： health-im-api<br>
 * ClassName： UserExpandController<br>
 * Description：用户扩展信息 <br>
 * @author limiaomiao
 * @createTime 2015年7月27日
 * @version 1.0.0
 */
@RestController
@RequestMapping("/user")
public class UserExpandController extends AbstractController {

	@Autowired
	private Datastore dsForRW;
	
	@Autowired
	private UserExpandRepository userExpandRepository;

	// @RequestMapping("/password/update")
	//
	// public JMessage updatePassword(@RequestParam("oldPassword") String
	// oldPassword, @RequestParam("newPassword") String newPassword) {
	// JMessage jMessage;
	//
	// if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword))
	// {
	// jMessage = Constants.Result.ParamsAuthFail;
	// } else {
	// jMessage = userService.updatePassword(UserUtils.getUserId(), oldPassword,
	// newPassword);
	// }
	//
	// return jMessage;
	// }
	//
	
	@RequestMapping("/password/reset")
	public JSONMessage resetPassword(@RequestParam(defaultValue = "") String telephone, @RequestParam(defaultValue = "") String randcode,
			@RequestParam(defaultValue = "") String newPassword) {
		JSONMessage jMessage;
	
		if (StringUtil.isEmpty(telephone) || (StringUtil.isEmpty(randcode)) || StringUtil.isEmpty(newPassword)) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			// jMessage = userService.resetPassword("", telephone, newPassword);
			jMessage = JSONMessage.success(null);
		}
	
		return jMessage;
	}

	@Autowired
	private UserManager userService;

	// @RequestMapping("/password/update")
	//
	// public JMessage updatePassword(@RequestParam("oldPassword") String
	// oldPassword, @RequestParam("newPassword") String newPassword) {
	// JMessage jMessage;
	//
	// if (StringUtils.isEmpty(oldPassword) || StringUtils.isEmpty(newPassword))
	// {
	// jMessage = Constants.Result.ParamsAuthFail;
	// } else {
	// jMessage = userService.updatePassword(UserUtils.getUserId(), oldPassword,
	// newPassword);
	// }
	//
	// return jMessage;
	// }
	//

	/**
	 * 
	 * </p>获取用户设置信息</p>
	 * @param userId
	 * @return {@link User.UserSettings}
	 * @author limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping(value = "/settings")
	public JSONMessage getSettings(@RequestParam int userId) {
		Object data = userService.getSettings(0 == userId ? ReqUtil.instance.getUserId() : userId);
		return JSONMessage.success(null, data);
	}

	/**
	 * 
	 * </p>这里用一句话描述这个方法的作用</p>
	 * @param userSettings {@link User.UserSettings}
	 * @return
	 * @author limiaomiao
	 * @date 2015年7月27日
	 */
	@RequestMapping(value = "/settings/update")
	public JSONMessage updateSettings(@ModelAttribute UserSettings userSettings) {
		int userId=ReqUtil.instance.getUserId();
		Object data = userService.updateSettings(userId,userSettings);

		return JSONMessage.success(null, data);
	}
	
	/**
	 * 
	 * @api {[get,post]} /settings/modifyPush  用户设置:新消息通知
	 * @apiVersion 1.0.0
	 * @apiName UserSetting
	 * @apiGroup 用户	
	 * @apiDescription 用户设置:设置是否接收新消息通知
	 * @apiParam  {String}    access_token          token
	 * @apiParam  {String}    serial          		客户端设备号,用于推送
	 * @apiParam  {Integer}    ispushflag             是否接收通知(1正常接收，2不接收)
	 * @apiAuthor chengwei
	 * @author chengwei
	 * @date 2015年8月28日
	 */
	@RequestMapping(value = "/settings/modifyPush")
	public JSONMessage updatePushSettings(@RequestParam String serial,@RequestParam Integer ispushflag) {
		int userId=ReqUtil.instance.getUserId();
		if(ispushflag==null || (ispushflag!=1 && ispushflag!=2))
		{
			return JSONMessage.failure("参数ispushflag错误:其值只能为1或者2");
		}
		Object data = userService.updateSettings(userId,serial,ispushflag);
		return JSONMessage.success(null, data);
	}
	

	@RequestMapping(value = "/avatar/set")
	public JSONMessage avatarSet(@RequestParam(value = "photoId") String photoId) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(photoId)) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			try {
				DBCollection photoCollection = dsForRW.getDB().getCollection("user_photo");

				DBObject q = new BasicDBObject();
				q.put("_id", ReqUtil.instance.getUserId());
				q.put("photos.avatar", 1);
				DBObject o = new BasicDBObject("$set", new BasicDBObject("photos.$.avatar", 0));

				photoCollection.update(q, o);

				q = new BasicDBObject();
				q.put("_id", ReqUtil.instance.getUserId());
				q.put("photos.photoId", photoId);
				o = new BasicDBObject("$set", new BasicDBObject("photos.$.avatar", 1));

				photoCollection.update(q, o);

				jMessage = JSONMessage.success("设置头像成功");
			} catch (Exception e) {
				e.printStackTrace();

				jMessage = JSONMessage.failure("设置头像失败");
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/photo/add")
	public JSONMessage photoAdd(@RequestParam(value = "photos") String photos) {
		JSONMessage jMessage;
		List<PhotoVO> photoList = null;

		try {
			photoList = JSON.parseArray(photos, PhotoVO.class);
		} catch (Exception e) {

		}

		if (null == photoList) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			try {
				DBCollection photoCollection = dsForRW.getDB().getCollection("user_photo");
				Integer userId = ReqUtil.instance.getUserId();

				photoList.forEach(photo -> {
					photo.setPhotoId(ObjectId.get().toString());
					photo.setCreateTime(new Date());
					photo.setAvatar(0);
				});

				DBObject q = new BasicDBObject("_id", userId);

				if (0 == photoCollection.find(q).count()) {
					DBObject o = new BasicDBObject();
					o.put("_id", userId);
					o.put("photos", photoList);
					o.put("photoCount", photoList.size());

					photoCollection.insert(o);
				} else {
					DBObject o = new BasicDBObject();
					o.put("$addToSet", new BasicDBObject("photos", new BasicDBObject("$each", photoList)));
					o.put("$inc", new BasicDBObject("photoCount", photoList.size()));

					photoCollection.update(q, o);
				}

				jMessage = JSONMessage.success(null, photoList);
			} catch (Exception e) {
				e.printStackTrace();

				jMessage = JSONMessage.failure("新增照片失败");
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/photo/delete")
	public JSONMessage photoDelete(@RequestParam(value = "photoId") String photoId) {
		JSONMessage jMessage;

		if (null == photoId) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			try {
				DBCollection photoCollection = dsForRW.getDB().getCollection("user_photo");

				DBObject q = new BasicDBObject("_id", ReqUtil.instance.getUserId());
				DBObject o = new BasicDBObject();
				o.put("$pull", new BasicDBObject("photos", new BasicDBObject("photoId", photoId)));
				o.put("$inc", new BasicDBObject("photoCount", -1));

				photoCollection.update(q, o);

				jMessage = JSONMessage.success("删除照片成功");
			} catch (Exception e) {
				e.printStackTrace();

				jMessage = JSONMessage.failure("删除照片失败");
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/photo/update")
	public JSONMessage photoUpdate(@ModelAttribute("photo") PhotoVO photo) {
		JSONMessage jMessage;

		if (StringUtil.isEmpty(photo.getPhotoId()) || StringUtil.isEmpty(photo.getOUrl()) || StringUtil.isEmpty(photo.getTUrl())) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			try {
				DBCollection photoCollection = dsForRW.getDB().getCollection("user_photo");

				DBObject q = new BasicDBObject("_id", ReqUtil.instance.getUserId());
				q.put("photos.photoId", photo.getPhotoId());

				DBObject set = new BasicDBObject();
				set.put("photos.$.oUrl", photo.getOUrl());
				set.put("photos.$.tUrl", photo.getTUrl());
				set.put("photos.$.createTime", new Date());

				DBObject o = new BasicDBObject("$set", set);

				photoCollection.update(q, o);

				jMessage = JSONMessage.success("更新照片成功");
			} catch (Exception e) {
				e.printStackTrace();

				jMessage = JSONMessage.failure("更新照片失败");
			}
		}

		return jMessage;
	}

	@RequestMapping(value = "/photo/list")
	public JSONMessage photoList(@RequestParam(value = "userId", defaultValue = "") Integer userId) {
		JSONMessage jMessage;

		try {
			DBCollection photoCollection = dsForRW.getDB().getCollection("user_photo");
			DBObject obj = photoCollection.findOne(new BasicDBObject("_id", null == userId ? ReqUtil.instance.getUserId() : userId));

			if (null == obj)
				jMessage = JSONMessage.success(null, new Object[] {});
			else {
				@SuppressWarnings("unchecked")
				List<BasicDBObject> photos = (List<BasicDBObject>) obj.get("photos");

				// 头像放在第一位
				Collections.sort(photos,
						(BasicDBObject o1, BasicDBObject o2) -> (o2.get("avatar").toString().compareTo(o1.get("avatar").toString())));

				jMessage = JSONMessage.success(null, photos);
			}
		} catch (Exception e) {
			e.printStackTrace();

			jMessage = JSONMessage.failure("获取照片列表失败");
		}

		return jMessage;
	}

	@RequestMapping(value = "/room/add")
	public JSONMessage roomAdd(@ModelAttribute("room") Room room) {
		JSONMessage jMessage;

		if (null == room) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			// jMessage = userExpandRepository.roomAdd(ReqUtil.instance.getUserId(),
			// room);
			return null;
		}

		return jMessage;
	}
	
	// @RequestMapping(value = "/room/update")
	// @ResponseBody
	// public JMessage roomUpdate() {
	// return null;
	// }

	@RequestMapping(value = "/room/delete")
	public JSONMessage roomDelete(@RequestParam("roomId") Integer roomId) {
		JSONMessage jMessage;

		if (null == roomId) {
			jMessage = Constants.Result.ParamsAuthFail;
		} else {
			jMessage = userExpandRepository.roomDelete(ReqUtil.instance.getUserId(), roomId);
		}

		return jMessage;
	}

	@RequestMapping(value = "/room/list")
	public JSONMessage roomList(@RequestParam(value = "userId", defaultValue = "") Integer userId) {
		return userExpandRepository.roomList(null == userId ? ReqUtil.instance.getUserId() : userId);
	}
}
