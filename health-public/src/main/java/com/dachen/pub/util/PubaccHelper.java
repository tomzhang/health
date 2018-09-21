package com.dachen.pub.util;

import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.response.GroupInfo;
import com.dachen.im.server.data.response.SendMsgResult;
import com.dachen.pub.model.param.PubMsgParam;
import com.dachen.pub.model.param.PubParam;
import com.dachen.pub.model.po.PubPO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.spring.cloud.AbstractRemoteServiceClientProxy;
import com.dachen.util.JSONUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PubaccHelper extends AbstractRemoteServiceClientProxy {

    public SendMsgResult sendMsgToPub(PubMsgParam pubMsg) throws HttpApiException {
        String tag = "sendMsgToPub";
        logger.info("{}. pubMsg={}", tag, pubMsg);
        if (null == pubMsg) {
            throw new HttpApiException("pubMsg is Null!");
        }
        logger.info("{}. pubMsg json={}", tag, JSONUtil.toJSONString(pubMsg));
        String url = "/pub/sendMsgToPub";
        SendMsgResult ret = this.postJson(url, pubMsg, SendMsgResult.class);
        return ret;
    }


    public SendMsgResult sendMsg(MessageVO msg) throws HttpApiException {
        if (null == msg) {
            throw new HttpApiException("msg is Null!");
        }
        String url = "/pub/sendMsg";
        SendMsgResult ret = this.postJson(url, msg, SendMsgResult.class);
        return ret;
    }

    public PubPO createPub(PubParam pubMsg) throws HttpApiException {
        String url = "/pub/create";
        PubPO ret = this.postJson(url, pubMsg, PubPO.class);
        return ret;
    }

    public void savePub(PubParam pubParam) throws HttpApiException {
        String url = "/pub/save";
        String ret = this.postJson(url, pubParam, String.class);
    }

    public PubPO getPub(String pid) throws HttpApiException {
        Map<String,String> param = new HashMap<String,String>();
        param.put("pubId", pid);
        String url = "/pub/get";
        PubPO ret = this.postJson(url, param, PubPO.class);
        return ret;
    }
    public GroupInfo getGroupInfo(String groupId, String currentUserId, boolean needUser) throws HttpApiException {
        Map<String,String> param = new HashMap<String,String>();
        param.put("pubId", groupId);
        param.put("userId", currentUserId);
        String url = "/pub/getGroupInfo";
        GroupInfo ret = this.postJson(url, param, GroupInfo.class);
        return ret;
    }

    public PubPO getCustomerPubInfo(Integer userType,String client) throws HttpApiException {
        Map<String,Object>param = new HashMap<String,Object>();
        param.put("userType", userType);
        param.put("client", client);
        String url = "/pub/getCustomePubInfo";
        PubPO ret = this.postJson(url, param, PubPO.class);
        return ret;
    }

    public void subscribe(String pid,String userId,UserRoleEnum role,boolean sendMsg) throws HttpApiException {
        String url = "/pub/subscribe";
        Map<String,Object>param = new HashMap<String,Object>();
        param.put("pid", pid);
        param.put("userId", userId);
        param.put("role", role.getValue());
        param.put("sendMsg", sendMsg);
        String ret = this.postJson(url, param, String.class);
    }

    public void unsubscribe(String pid,String userId,UserRoleEnum role) throws HttpApiException {
        Map<String,Object>param = new HashMap<String,Object>();
        param.put("pid", pid);
        param.put("userId", userId);
        param.put("role", role.getValue());
        String url = "/pub/unsubscribe";
        logger.info("取消关注 unsubscribe params pid:{} userId:{} role:{}", param.get("pid"),param.get("userId"),param.get("role"));
        String ret = this.postJson(url, param, String.class);
    }

    @Override
    public String getApiRequestDir() {
        return "inner";
    }

    @Override
    public String getAppName() {
        return "pubacc";
    }
}
