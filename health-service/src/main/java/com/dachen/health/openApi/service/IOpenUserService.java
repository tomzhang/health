package com.dachen.health.openApi.service;

import com.dachen.health.commons.vo.User;
import com.dachen.health.openApi.entity.OpenUserVO;
import com.dachen.health.openApi.entity.SimpleOpenUserVO;
import com.dachen.health.openApi.entity.ThirdApp;

import java.util.List;
import java.util.Map;

/**
 * @author liangcs
 * @desc
 * @date:2017/5/2 18:07
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IOpenUserService {

    Boolean checkAppId(String appId);

    /**
     * 第三方登录接口
     * @param appId
     * @param telephone
     * @param password
     * @param userType
     * @return
     */
    Map<String, String> login(String appId, String telephone, String password, Integer userType);

    /**
     * 获取用户信息
     * @param appId
     * @param code
     * @param sign
     * @return
     */
    OpenUserVO getUser(String appId, String code, String sign);

    /**
     * 通过openId获取用户信息
     * @param openId
     * @return
     */
    OpenUserVO getUserByOpenId(String openId);
    
    List<OpenUserVO> getUserArea(String timestamp, String appId, String sign, List<Integer> userIds);

    Integer getUserId(String appId, String code);

    void sendNotify(String appId, Integer userId, Integer status, String remark);

    ThirdApp init(String name, String callbackUrl, String notifyUrl);

    User update(String appId, String code, String headPicFileName, String hospital,
        String hospitalId, String dept, String deptId, String title);

    SimpleOpenUserVO getSimpleUserByOpenId(String openId);

    /**
     * 第三方验证码登录接口
     * @param appId
     * @param telephone
     * @param userType
     * @return
     */
    Map<String, String> loginByCode(String appId, String telephone, Integer userType);
}
