package com.dachen.line.stat.service;

import com.dachen.line.stat.entity.UserRequestParam;

import java.util.HashMap;
import java.util.Map;

public interface IAdapterService {

    /**
     * 获取token 和是否有菜单显示权限
     * @param userId
     * @param communityId
     * @return
     */
    HashMap<String,Object> getAuthenticationInfo(String userId, String communityId);

    /**
     * 调用病例库修改用户信息接口
     * @param userRequestParam
     * @return
     */
    Map<String,Object> updateUser(UserRequestParam userRequestParam);
}
