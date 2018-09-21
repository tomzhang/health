package com.dachen.health.controller.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.base.constant.UserChangeTypeEnum;
import com.dachen.health.msg.service.IBusinessServiceMsg;
import com.dachen.im.server.data.ImgTextMsg;
import com.dachen.im.server.enums.SysGroupEnum;

@RestController
@RequestMapping("/doctor/test")
public class DoctorTestController {
	@Autowired
    private IBusinessServiceMsg businessServiceMsg;
	
	@RequestMapping("/sendIMNotice")
    public JSONMessage checked(String toUserId) throws HttpApiException {
		sendIMNotice(toUserId);
        return JSONMessage.success();
    }
	
	/**
	 * 审核医生，设置了医生助手要给医生发IM通知
	 */
	private void sendIMNotice(String toUserId) throws HttpApiException {
		List<ImgTextMsg> mpt = new ArrayList<ImgTextMsg>();
        ImgTextMsg imgTextMsg = new ImgTextMsg();
        imgTextMsg.setStyle(7);
        imgTextMsg.setTime(System.currentTimeMillis());
//        imgTextMsg.setPic(GroupUtil.getInviteMemberImage());
        imgTextMsg.setTitle(UserChangeTypeEnum.DOCOTR_JOIN_CARE_PLAN.getAlias());
        final String content = "能发出去吗？";
        imgTextMsg.setContent(content);
        imgTextMsg.setUrl("http://");
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("bizType", 27);
        param.put("bizId", "");
        imgTextMsg.setParam(param);
        mpt.add(imgTextMsg);
        businessServiceMsg.sendTextMsg(toUserId, SysGroupEnum.TODO_NOTIFY, mpt, null);
	}
    
}
