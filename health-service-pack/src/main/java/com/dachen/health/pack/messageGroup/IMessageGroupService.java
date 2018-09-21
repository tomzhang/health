package com.dachen.health.pack.messageGroup;

import com.dachen.sdk.exception.HttpApiException;

import java.util.Map;

/**
 * Created by Administrator on 2017/3/1.
 */
public interface IMessageGroupService {

    Map<String, Object> getGroupParam(String gid) throws HttpApiException;

    void updateGroupBizState(String gid, Integer type) throws HttpApiException;

}
