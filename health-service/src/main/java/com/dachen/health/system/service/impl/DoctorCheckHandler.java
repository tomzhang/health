package com.dachen.health.system.service.impl;

import com.dachen.health.base.constants.BaseConstants;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.health.system.entity.param.DoctorCheckParam;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;
import com.dachen.pub.service.PubCustomerService;
import com.dachen.pub.service.PubGroupService;
import com.dachen.sdk.exception.HttpApiException;
import com.mobsms.sdk.MobSmsSdk;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sharp on 2018/3/1.
 */
@Service
public class DoctorCheckHandler {

    static Logger logger = LoggerFactory.getLogger(DoctorCheckHandler.class);

    @Resource
    protected MobSmsSdk mobSmsSdk;
    @Autowired
    private UserManager userManager;
    @Autowired
    protected IBaseDataService baseDataService;
    @Autowired
    protected IBusinessServiceMsg businessServiceMsg;
    @Autowired
    protected PubCustomerService pubCustomerService;
    @Autowired
    protected PubGroupService pubGroupService;

    @Async
    public void checkedNotify(User user, DoctorCheckParam param) throws HttpApiException {
        if (user == null || user.getDoctor() == null) {
            return;
        }
        //发到圈子页签-玄关健康团队的欢迎消息
        pubCustomerService.welcome(user.getUserId());

        //创建医生爱心宣教公众号，并且发送爱心宣教使用方法
        pubGroupService.createDoctorPub(user.getUserId(), user.getDoctor().getIntroduction(), param.getRole());

        //审核通过，分别给医生和助手发送IM通知
        if (user.getDoctor().getAssistantId() != null && user.getDoctor().getAssistantId() != 0) {
            User assistant = userManager.getUser(user.getDoctor().getAssistantId());
            sendIMNoticeToDoctor(user.getUserId()+"", assistant.getUserId(), assistant.getName());
            sendIMNoticeToFeldsher(assistant.getUserId()+"", user.getName());
        }
        //{0}您好，您的玄关平台医生资质已审核通过。您可以登录{1}APP，设置服务套餐，为患者服务。";
        if ((param.getSendSMS() != null) && param.getSendSMS()) {
            String tpl = baseDataService.toContent("0007", user.getName());
            mobSmsSdk.send(user.getTelephone(), tpl, BaseConstants.XG_YSQ_APP);
        }
    }

    /**
     * 审核医生通过，要给医生发与助手建立关系的IM通知
     *
     * @param toUserId IM通知发送给谁
     * @param feldsherId 医生助手姓名
     * @param feldsherName 医生助手姓名
     */
    private void sendIMNoticeToDoctor(String toUserId, Integer feldsherId, String feldsherName) throws HttpApiException {
        List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setStyle(7);
        imgTextMsg.setTitle("建立关系");
        imgTextMsg.setFooter("查看详情");
        String content = String.format("为方便您的在线咨询服务，系统已为您安排%s为您的专属医生助手。", feldsherName);
        imgTextMsg.setContent(content);
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", 27);
        param.put("bizId", feldsherId);
        imgTextMsg.setParam(param);
        mpt.add(imgTextMsg);
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY, mpt, null);
    }

    /**
     * 审核医生，要给医生助手发送建立关系的IM通知
     *
     * @param toUserId 发送用户
     * @param doctorName 医生姓名
     */
    private void sendIMNoticeToFeldsher(String toUserId, String doctorName) throws HttpApiException {
        List<ImgTextMsg> list = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        //  通知内容需要放到资源配置文件中，方便统一更新
        imgTextMsg.setTitle("建立关系");
        imgTextMsg.setTime(System.currentTimeMillis());
        imgTextMsg.setContent(String.format("您成为了%s医生的助手，后续该医生的订单需要您负责处理相关事务。", doctorName));
        list.add(imgTextMsg);
        //imgTextMsg.setFooter("查看详情");
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY, list, null);
    }

}
