package com.dachen.health.msg.service;

import java.util.Map;

import com.dachen.im.server.data.EventVO;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.CreateGroupRequestMessage;
import com.dachen.im.server.data.request.GroupInfoRequestMessage;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.sdk.exception.HttpApiException;

public interface IMsgService {

    Object createGroup(CreateGroupRequestMessage createGroupParam);

    Object updateGroup(UpdateGroupRequestMessage request);

    Object getGroupInfo(GroupInfoRequestMessage request) throws HttpApiException;

    /**
     * 发送消息
     *
     * @param msg
     * @return
     */
    SendMsgResult baseSendMsg(MessageVO msg) throws HttpApiException;

    /**
     * 注册手机设备信息
     *
     * @param deviceToken
     * @param client      ---客户端类型，ios/android
     * @param userId
     */
    void registerDeviceToken(String deviceToken, String client, Integer userId, boolean invalid);

    void updateDeviceToken(String deviceToken);

    /**
     * 删除设备信息
     *
     * @param deviceToken
     * @param userId
     */
    void removeDeviceToken(String deviceToken);

    /**
     * 更改设备推送标记
     *
     * @param deviceToken
     * @param invalid(true:无效;false:有效)
     */
    void updatePushStatus(Integer userId, String deviceToken, boolean invalid);


    /**
     * 获取指令消息
     *
     * @param buisnessParam
     */
    void sendEvent(EventVO eventVO);

    Object createGroup(String patientId, String userId, Map<String, Object> groupParam);

}
