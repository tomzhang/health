package com.dachen.health.activity.invite.api.credit;

import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.health.activity.invite.vo.CDeptIntegralVO;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.sdk.json.JSONMessage;
import com.dachen.sdk.spring.cloud.AbstractRemoteServiceClientProxy;
import com.dachen.util.StringUtil;
import com.google.common.collect.Maps;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/7 13:56 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Component
public class CreditApiProxy extends AbstractRemoteServiceClientProxy {

    @Autowired
    private RibbonManager ribbonManager;

    @Override
    public String getAppName() {
        return "credit";
    }

    /**
     * 更改个人学分
     *
     * @param userId
     * @param value
     * @param remark
     * @param reason
     * @return
     * @throws HttpApiException
     */
    public JSONMessage userIntegralChange(String userId, Long value,
        String remark, String reason, String orderId) throws HttpApiException {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(reason)
            ||  value == null) {
            return null;
        }

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("userId", userId);
        paramMap.put("value", value+"");
        paramMap.put("remark", remark);
        paramMap.put("reason", reason);
        paramMap.put("orderId", orderId);

        String url = "/user/change";
        return this.openRequest(url, paramMap, JSONMessage.class);
    }

    /**
     * 平台转学币给用户
     *
     * @param userId
     * @param updateValue
     * @param remark
     * @param reason
     * @param toUserReason
     * @return
     * @throws HttpApiException
     */
    public void userIntegralTransfer(String userId, Long updateValue,
        String remark, String reason, String toUserReason) throws HttpApiException {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(reason)
            ||  updateValue == null) {
            return;
        }

        Map<String, Object> paramMap = Maps.newHashMap();
        paramMap.put("userId", userId);
        paramMap.put("updateValue", updateValue);
        paramMap.put("remark", remark);
        paramMap.put("reason", reason);
        paramMap.put("toUserReason", toUserReason);

        String url = "http://CREDIT/platform/transfer";
        ribbonManager.post(url, paramMap);
    }

    public JSONMessage doctorChecked(String userId, String userName, Long value,
        String remark, String reason) throws HttpApiException {
        if (StringUtil.isBlank(userId) || StringUtil.isBlank(reason)
            ||  value == null) {
            return null;
        }

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("userId", userId);
        paramMap.put("userName", userName);
        paramMap.put("value", value+"");
        paramMap.put("remark", remark);
        paramMap.put("reason", reason);

        String url = "/user/doctorChecked";
        return this.openRequest(url, paramMap, JSONMessage.class);
    }

    /**
     * 获取科室学分余额
     *
     * @param deptId
     * @return
     * @throws HttpApiException
     */
    @RequestMapping("balance")
    public Long deptBalance(String deptId) throws HttpApiException {
        if (StringUtil.isBlank(deptId)){
            return null;
        }
        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put("deptId", deptId);

        String url = "/dept/balance";
        CDeptIntegralVO result = this.openRequest(url, paramMap, CDeptIntegralVO.class);
        return result.getBalance();
    }
}
