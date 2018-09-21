package com.dachen.health.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.dachen.commons.JSONMessage;
import com.dachen.drugorg.api.client.DrugOrgApiClientProxy;
import com.dachen.drugorg.api.entity.CEnterpriseUser;
import com.dachen.mq.producer.BasicProducer;
import com.dachen.sdk.async.task.AsyncTaskPool;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.ReqUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/28 14:41 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Api(value = "业务助理", description = "业务助理", produces = MediaType.APPLICATION_JSON_VALUE, protocols = "http")
@RestController
@RequestMapping("business/assistant")
public class BusinessAssistantController {

    private static final Logger logger = LoggerFactory.getLogger(BusinessAssistantController.class);

    @Resource
    private AsyncTaskPool asyncTaskPool;
    @Autowired
    private DrugOrgApiClientProxy drugOrgApiClientProxy;

    @ApiOperation(value = "添加业务助理为好友", notes = "添加业务助理为好友", response = JSONMessage.class)
    @ApiImplicitParam(name = "businessAssistantId", value = "业务助理Id", required = true, dataType = "int", paramType = "path")
    @RequestMapping(value = "addFriend/{businessAssistantId}", method = RequestMethod.POST)
    public JSONMessage addBusinessAssistantToBeFriend(@PathVariable Integer businessAssistantId) {
        notifyDrugOrgAsync(businessAssistantId, ReqUtil.instance.getUserId());
        return JSONMessage.success();
    }

    private void notifyDrugOrgAsync(Integer inviterId, Integer doctorId) {
        this.asyncTaskPool.getPool().submit(() -> {
            try {
                // 往mq中写入注册成功信息，药企圈消费此信息
                sendMsgToMq(inviterId, doctorId);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
    }

    private void sendMsgToMq(Integer inviterId, Integer doctorId) {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("representUserId", inviterId);
        dataMap.put("doctorUserId", doctorId);
        BasicProducer.sendMessage("EXCHANGE-INVITE-DOCTOR-REGIST", JSONObject.toJSONString(dataMap));
    }

    @ApiOperation(value = "获取业务助理详情", notes = "获取业务助理详情", response = CEnterpriseUser.class)
    @RequestMapping(value = {"/{id}"}, method = RequestMethod.GET)
    public JSONMessage getBusinessAssistant(@PathVariable Integer id) {
        List<Integer> businessAssistantIds = new ArrayList<>();
        businessAssistantIds.add(id);
        List<CEnterpriseUser> cEnterpriseUsers;
        CEnterpriseUser cEnterpriseUser = null;
        try {
            cEnterpriseUsers = drugOrgApiClientProxy.getByUserId(businessAssistantIds);
        } catch (HttpApiException e) {
            logger.error(e.getMessage(), e);
            return JSONMessage.error(e);
        }

        Map<String, Object> result = new HashMap<>();
        boolean isCurUserFriend = false;

        if (!CollectionUtils.isEmpty(cEnterpriseUsers)) {
            cEnterpriseUser = cEnterpriseUsers.get(0);

            try {
                List<Integer> userIds = drugOrgApiClientProxy.doctorSaleFriend(ReqUtil.instance.getUserId());
                if (!CollectionUtils.isEmpty(userIds)) {
                    if (userIds.contains(cEnterpriseUser.getUserId())) {
                        isCurUserFriend = true;
                    }
                }
            } catch (HttpApiException e) {
                logger.error(e.getMessage(), e);
                return JSONMessage.error(e);
            }
        }

        result.put("isCurUserFriend", isCurUserFriend);
        result.put("cEnterpriseUser", cEnterpriseUser);
        return JSONMessage.success(result);
    }
}
