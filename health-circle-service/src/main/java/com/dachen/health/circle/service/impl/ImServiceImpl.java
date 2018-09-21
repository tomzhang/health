package com.dachen.health.circle.service.impl;

import com.dachen.health.circle.service.ImService;
import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.msg.service.IMsgService;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.data.MessageVO;
import com.dachen.im.server.data.request.UpdateGroupRequestMessage;
import com.dachen.im.server.enums.MsgTypeEnum;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.StringUtil;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImServiceImpl implements ImService{

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    protected IMsgService msgService;

    @Autowired
    protected IBusinessServiceMsg businessMsgService;

    @Autowired
    protected IMsgService iMsgService;

    @Override
    public void joinGroupIM(String gid, Integer operator, Integer doctorId, String doctorName) {
        String tag = "joinGroupIM";
        UpdateGroupRequestMessage updateGroupRequestMessage = new UpdateGroupRequestMessage();
        updateGroupRequestMessage.setGid(gid);
        updateGroupRequestMessage.setAct(1);
        updateGroupRequestMessage.setRole(1);
        updateGroupRequestMessage.setFromUserId(String.valueOf(operator));
        updateGroupRequestMessage.setToUserId(String.valueOf(doctorId));
        try {
            Object o = iMsgService.updateGroup(updateGroupRequestMessage);
            logger.debug("{}. o={}", tag, o);
        } catch (Exception e) {
            logger.info("{}加入Group时未成功加入到Group会话组", doctorName);
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendTextMsg(Integer fromUserId, String gid, String content) throws HttpApiException {
        MessageVO msg = new MessageVO();
        msg.setType(MsgTypeEnum.TEXT_IMG.getValue());
        msg.setFromUserId(fromUserId.toString());
        msg.setGid(gid);
        msg.setContent(content);
        msgService.baseSendMsg(msg);
    }

    @Override
    public void sendRemind(Integer fromUserId, String gid, String content) throws HttpApiException {
        /*
        2017-05-26 10:07:57,368 [http-nio-8101-exec-10] INFO  com.dachen.health.msg.service.impl.MsgServiceImpl - baseSendMsg. msg={"content":"感谢你关注中山大学附属第一医院血液病科组1","fromUserId":"0","gid":"pub_dept_590fe2694ba6054a3b37da29","isPush":"true","type":12,"userName":"黄登品"}, pushUsers=null
2017-05-26 10:07:57,368 [http-nio-8101-exec-10] INFO  com.dachen.sdk.component.RemoteInvokeComponent - executePostMethod. by RibbonManager. serviceUrl=http://pubacc/inner/pub/sendMsg
2017-05-26 10:07:57,371 [http-nio-8101-exec-10] INFO  com.dachen.sdk.component.RemoteInvokeComponent - executePostMethod. http://pubacc/inner/pub/sendMsg execute spent 3 ms, ret={"detailMsg":"发送者不能为系统用户","resultCode":100,"resultMsg":"发送者不能为系统用户"}
         */
        MessageVO msg = new MessageVO();
        msg.setType(MsgTypeEnum.REMIND.getValue());
//        msg.setFromUserId(SysConstant.SysUserEnum.SYS_001.getUserId());
        msg.setFromUserId(fromUserId.toString());
        msg.setGid(gid);
        msg.setContent(content);
        msgService.baseSendMsg(msg);
    }

    @Override
    public void sendTodoNotifyMsg(Integer toUserId, String title, String content, String url) {
        try {
            List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setStyle(6);
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setPic(GroupUtil.getInviteMemberImage());

            imgTextMsg.setTitle(title);
            imgTextMsg.setContent(content);
            imgTextMsg.setUrl(url);

            mpt.add(imgTextMsg);

            businessMsgService.sendTextMsg(toUserId.toString(), SysGroupEnum.TODO_NOTIFY, mpt, null);
            businessMsgService.sendTextMsg(toUserId.toString(), SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendTodoNotifyMsg(Integer toUserId, String title, String content, String url, Map<String, Object> params) {
        try {
            List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            imgTextMsg.setStyle(6);
            imgTextMsg.setTime(System.currentTimeMillis());
            imgTextMsg.setPic(GroupUtil.getInviteMemberImage());

            imgTextMsg.setTitle(title);
            imgTextMsg.setContent(content);
            imgTextMsg.setUrl(url);

            imgTextMsg.setParam(params);

            mpt.add(imgTextMsg);

//            businessMsgService.sendTextMsg(toUserId.toString(), SysGroupEnum.TODO_NOTIFY, mpt, null);
            businessMsgService.sendTextMsg(toUserId.toString(), SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public void sendTodoNotifyMsg(Integer toUserId, String title, String content, String url, Map<String, Object> params,ImgTextMsg itm) {
        try {
            List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>(1);
            ImgTextMsg imgTextMsg = new ImgTextMsg();
            if(null == itm.getStyle()) {
                imgTextMsg.setStyle(6);
            }else {
                imgTextMsg.setStyle(itm.getStyle());
            }
            imgTextMsg.setTime(System.currentTimeMillis());
            if(StringUtil.isEmpty(itm.getPic())) {
                imgTextMsg.setPic(GroupUtil.getInviteMemberImage());
            }else {
                imgTextMsg.setPic(itm.getPic());
            }
            if(StringUtil.isNotEmpty(itm.getFooter())){
                imgTextMsg.setFooter(itm.getFooter());
            }
            imgTextMsg.setTitle(title);
            imgTextMsg.setContent(content);
            imgTextMsg.setUrl(url);

            imgTextMsg.setParam(params);

            mpt.add(imgTextMsg);

//            businessMsgService.sendTextMsg(toUserId.toString(), SysGroupEnum.TODO_NOTIFY, mpt, null);
            businessMsgService.sendTextMsg(toUserId.toString(), SysGroupEnum.TODO_NOTIFY_DOC, mpt, null);
            logger.info("发送通知参数:{}",ToStringBuilder.reflectionToString(imgTextMsg));
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
