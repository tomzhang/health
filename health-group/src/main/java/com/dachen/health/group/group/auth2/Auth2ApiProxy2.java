package com.dachen.health.group.group.auth2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.constants.Constants.ResultCode;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.manager.RemoteServiceResult;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/18 15:07 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Component
public class Auth2ApiProxy2 {

    private static final Logger logger = LoggerFactory.getLogger(Auth2ApiProxy2.class);

    @Autowired
    private RibbonManager ribbonManager;

    public List<AuthSimpleUser2> getSimpleUserList(List<Integer> userIdList) {
        if (CollectionUtils.isEmpty(userIdList)) {
            return null;
        }

        Map<String, List<Integer>> idMap = Maps.newHashMap();
        idMap.put("userId", userIdList);

        String response = ribbonManager.post("http://AUTH2/v2/user/getSimpleUser", idMap);

        RemoteServiceResult result = JSON.parseObject(response, RemoteServiceResult.class);
        if (!Objects.equals(result.getResultCode(), ResultCode.Success)) {
            throw new ServiceException(result.getDetailMsg());
        }

        return JSONArray.parseArray(JSON.toJSONString(result.getData()), AuthSimpleUser2.class);
    }
}
