package com.dachen.health.circle.service;

import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.sdk.exception.HttpApiException;

import java.util.Map;

public interface ImService {
    void joinGroupIM(String gid, Integer operator, Integer doctorId, String doctorName);

    void sendTextMsg(Integer fromUserId, String gid, String content) throws HttpApiException;

    void sendRemind(Integer fromUserId, String gid, String content) throws HttpApiException;

    void sendTodoNotifyMsg(Integer toUserId, String title, String content, String url);

    void sendTodoNotifyMsg(Integer toUserId, String title, String content, String url, Map<String, Object> params);

    void sendTodoNotifyMsg(Integer toUserId, String title, String content, String url, Map<String, Object> params,ImgTextMsg itm);

    }
