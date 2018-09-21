package com.dachen.health.msg.service.impl;

import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.constants.UserSession;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.health.msg.util.EventHelper;
import com.dachen.health.msg.util.IMUtils;
import com.dachen.health.msg.util.MsgHelper;
import com.dachen.im.server.constant.SysConstant;
import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.AuthTokenRequestMessage;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.im.server.enums.GroupTypeEnum;
import com.dachen.im.server.enums.RelationTypeEnum;
import com.dachen.pub.service.PubAccountService;
import com.dachen.util.JSONUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtils;

@Service(MsgServiceImpl.BEAN_ID)
public class MsgServiceImpl implements IMsgService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserManager userManager;

    @Autowired
    private PubAccountService pubAccountService;

//	@Resource(name = "jedisTemplate")
//	private JedisTemplate jedisTemplate;

    public static final String BEAN_ID = "MsgServiceImpl";

    public Object getGroupInfo(GroupInfoRequestMessage request) throws HttpApiException {
        if (StringUtils.isEmpty(request.getUserId())) {
            request.setUserId(String.valueOf(ReqUtil.instance.getUserId()));
        }
        GroupInfo group = null;
        if (SysConstant.isPubGroup(request.getGid())) {
            group = pubAccountService.getGroupInfo(request.getGid(), request.getUserId(), true);
            return group;
        }

        JSON json = MsgHelper.groupInfo(request);
        group = JSON.toJavaObject(json, GroupInfo.class);
        return group;
//		UserSession user = request.getUserId()==null?null:ReqUtil.instance.getUser(Integer.valueOf(request.getUserId()));
//		return IMUtils.buildGroupInfo(group,user);
    }

    /**
     * 创建导医和患者的会话
     * 1、如果不存在则新建（根据groupId判断）
     * 2、如果存在，则先删除该组的所有人（除创建者）；然后添加toUserId指定的人
     *
     * @param patientId 患者Id
     * @param guideId   导医Id
     * @return
     */
    public Object createGroup(String patientId, String guideId, Map<String, Object> groupParam) {
        CreateGroupRequestMessage quest = new CreateGroupRequestMessage();
        quest.setFromUserId(patientId);
        quest.setToUserId(guideId);
        quest.setGroupId(SysConstant.getGuideDocGroupId(patientId));
        quest.setGtype(RelationTypeEnum.DOC_PATIENT.getValue());
        quest.setType(GroupTypeEnum.CUSTOMER_SERVICE.getValue());
        quest.setParam(groupParam);
        JSON json = MsgHelper.createGroup2(quest);
        GroupInfo group = JSON.toJavaObject(json, GroupInfo.class);
        return group;
    }

    /**
     * 创建普通会话组( 客户端创建会话组的时候，有些端传了type=2,实际是创建的双人会话组。)
     * 客户端创建会话组的地方要改：
     * 1、type参数，要么不传，要么传0
     * 2、gtype参数一定要传
     */
    public Object createGroup(CreateGroupRequestMessage data) {
        UserSession user = ReqUtil.instance.getUser();
        if (StringUtils.isEmpty(data.getFromUserId())) {
            if (user == null) {
                //参数不正确;
                throw new ServiceException("参数错误：fromUserId不能为空");
            }
            data.setFromUserId(user.getUserId().toString());
        }
        if (StringUtils.isEmpty(data.getGtype())) {
            if (StringUtils.isEmpty(data.getGtype())) {
                try {
                    Integer toUserId = Integer.valueOf(data.getToUserId());
                    UserSession toUserSession = ReqUtil.instance.getUser(toUserId);
                    String gtype = IMUtils.getRtype(user.getUserType(), toUserSession.getUserType());
                    data.setGtype(gtype);
                } catch (Exception e) {
                    throw new ServiceException("gtype不能为空");
                }
            }
        }
        JSON json = MsgHelper.createGroup(data);
        GroupInfo group = JSON.toJavaObject(json, GroupInfo.class);
        return group;
    }

    /**
     * 修改群组信息
     *
     * @param data
     * @return
     */
    public Object updateGroup(UpdateGroupRequestMessage request) {
        if (StringUtils.isEmpty(request.getFromUserId())) {
            UserSession user = ReqUtil.instance.getUser();
            request.setFromUserId(user.getUserId().toString());
        }
        JSON json = MsgHelper.updateGroup(request);
        return json;
    }


    /**
     * 发送消息基本接口，供客户端和其他服务器端模块调用
     * 1、判断msg中是否有groupid，如果没有，则调用im的会话创建接口，创建一个会话，同时把会话id填充到msg中去
     * 2、将msg对象转化成im发送消息的参数，调用im的消息发送接口发送会话，同时把返回的信息组装成业务的返回值
     */
    public SendMsgResult baseSendMsg(MessageVO msg) throws HttpApiException {
        String tag = "baseSendMsg";
        UserSession userSession = ReqUtil.instance.getUser();
        String currentUserId = null;
        if (userSession != null) {
            currentUserId = String.valueOf(userSession.getUserId());
            if (StringUtils.isEmpty(msg.getFromUserId())) {
                msg.setFromUserId(currentUserId);
            }
            if (StringUtils.isEmpty(msg.getUserName())) {
                msg.setUserName(userSession.getName());
            }
        }
        if (StringUtils.isEmpty(msg.getFromUserId())) {
            throw new ServiceException("参数错误：发送者userId不能为空");
        }
        //避免userName总为null
        if (StringUtils.isEmpty(msg.getUserName())) {
            try {
                UserSession user = ReqUtil.instance.getUser(Integer.valueOf(msg.getFromUserId()));
                msg.setUserName(user.getName());
            } catch (Exception e) {
            }
        }

        IMUtils.setIsPush(msg, currentUserId);

        if (logger.isInfoEnabled()) {
            logger.info("{}. msg={}, pushUsers={}", tag, JSONUtil.toJSONString(msg), msg.getPushUsers());
        }

        String groupId = msg.getGid();
        SendMsgResult result = null;
        if (SysConstant.isPubGroup(groupId)) {
            //公共号
            result = pubAccountService.sendMsg(msg);
        } else {
            JSONObject json = (JSONObject) MsgHelper.sendMsg(msg);
            result = new SendMsgResult(json.getString("gid"), json.getString("mid"), json.getLongValue("time"));
        }
        return result;
    }

    /**
     * 发送指令消息
     *
     */
    public void sendEvent(EventVO event) {
        EventEnum eventType = EventEnum.getEnum(event.getEventType());
        Map<String, Object> param = event.getParam();
        EventHelper.checkEventParam(eventType, param);

        MsgHelper.sendEvent(event);
    }

    /**
     * 向IM注册设备信息
     *
     * @param deviceToken
     * @param client      ---客户端类型，ios/android
     * @param userId
     */
    public void registerDeviceToken(String deviceToken, String client, Integer userId, boolean invalid) {
        if (StringUtils.isEmpty(deviceToken)) {
            return;
        }
        AuthTokenRequestMessage request = new AuthTokenRequestMessage();
        request.setClient(client);
        request.setDeviceToken(deviceToken);
        request.setUid(String.valueOf(userId));
        request.setInvalid(invalid);
        UserSession user = userManager.getUserById(userId);
        if (user != null) {
            request.setUserType(user.getUserType());
        }
        MsgHelper.registerDeviceToken(request);
    }

    /**
     * 更新设备活跃时间
     *
     * @param deviceToken
     */
    public void updateDeviceToken(String deviceToken) {
        if (StringUtils.isEmpty(deviceToken)) {
            return;
        }
        AuthTokenRequestMessage request = new AuthTokenRequestMessage();
        request.setDeviceToken(deviceToken);
        MsgHelper.updateDeviceToken(request);
    }

    /**
     * 删除设备信息
     *
     * @param deviceToken
     */
    public void removeDeviceToken(String deviceToken) {
        if (StringUtils.isEmpty(deviceToken)) {
            return;
        }
        AuthTokenRequestMessage request = new AuthTokenRequestMessage();
        request.setDeviceToken(deviceToken);
        MsgHelper.removeDeviceToken(request);
    }

    /**
     * 更改设备推送标记
     *
     * @param deviceToken
     * @param invalid(true:无效;false:有效)
     */
    public void updatePushStatus(Integer userId, String deviceToken, boolean invalid) {
        if (StringUtils.isEmpty(deviceToken)) {
            return;
        }
        AuthTokenRequestMessage request = new AuthTokenRequestMessage();
        request.setDeviceToken(deviceToken);
        request.setInvalid(invalid);
        if (userId != null) {
            request.setUid(String.valueOf(userId));
        }
        MsgHelper.updatePushStatus(request);
    }


}
