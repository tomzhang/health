package com.dachen.health.circle.lightapp.service;

import com.alibaba.fastjson.JSONObject;
import com.dachen.common.auth.Auth2Helper;
import com.dachen.common.auth.data.AccessToken;
import com.dachen.commons.micro.Result;
import com.dachen.commons.micro.comsume.RibbonManager;
import com.dachen.health.circle.lightapp.dao.LightAppDao;
import com.dachen.health.circle.lightapp.entity.LightApp;
import com.dachen.health.circle.lightapp.entity.LightAppFilter;
import com.dachen.health.circle.lightapp.entity.OrgLightApp;
import com.dachen.health.circle.lightapp.entity.OrgLightApp.OrgType;
import com.dachen.health.circle.lightapp.entity.UserLightApp;
import com.dachen.health.circle.lightapp.vo.LightAppVO;
import com.dachen.health.circle.lightapp.vo.UserInfo;
import com.dachen.health.circle.service.GroupUser2Service;
import com.dachen.health.commons.service.impl.UserManagerImpl;
import com.dachen.health.commons.vo.User;
import com.dachen.util.BeanUtil;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author sharp
 * @desc
 * @date:2017/6/917:41 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Service
public class LightAppService {

    @Autowired
    protected LightAppDao lightAppDao;

    @Autowired
    protected Auth2Helper auth2Helper;

    @Autowired
    protected UserManagerImpl userManager;

    @Autowired
    protected RibbonManager ribbonManager;

    public <T> String insert(T lightApp) {
        return lightAppDao.insert(lightApp);
    }

    public List<LightAppVO> getLightApps() {

//        // 获取平台的app
//        List<OrgLightApp> platformApps = lightAppDao.getOrgApps(OrgType.platform.name(), null);
//        // 获取用户的app

//        List<UserLightApp> userApps = lightAppDao.getPersonApps(userId);
//        // 获取用户所属圈子的app
//        List<OrgLightApp> groupApps = lightAppDao.getOrgApps(OrgType.group.name(), groupUser2Service.findGroupIdByDoctor(userId));
//        // TODO 获取用户所属科室的app
//
//        // 获取轻应用信息
//        lightAppVOS.addAll(getLightApps(platformApps));
//        lightAppVOS.addAll(getLightApps(userApps));
//        lightAppVOS.addAll(getLightApps(groupApps));

        List<LightAppVO> lightAppVOS = getLightAppVO();

        filterVersion(lightAppVOS);

        sort(lightAppVOS);

        return lightAppVOS;
    }

    private List<LightAppVO> getLightAppVO() {
        List<LightAppVO> lightAppVOS = new ArrayList<LightAppVO>();
        List<LightApp> lightApps = lightAppDao.findByStatus(1);
        for (LightApp lightApp : lightApps) {
            lightAppVOS.add(po2vo(lightApp));
        }
        return lightAppVOS;
    }

    private void sort(List<LightAppVO> lightAppVOS) {
        Collections.sort(lightAppVOS, (x, y) -> {
            if (x.getSort() == null) {
                x.setSort(99);
            }
            if (y.getSort() == null) {
                y.setSort(99);
            }
            return Integer.compare(x.getSort(), y.getSort());
        });
    }

    protected void filterVersion(List<LightAppVO> lightAppVOS) {
        String version = ReqUtil.getVersion();
        for (Iterator<LightAppVO> it = lightAppVOS.iterator(); it.hasNext();) {
            LightAppVO lightApp = it.next();
            if (StringUtils.isNotBlank(version) && StringUtils.isNotBlank(lightApp.getMinVersion())
                && compareTo(StringUtils.split(version, "."), StringUtils.split(lightApp.getMinVersion(), ".")) < 0) {
                it.remove();
            }
        }
    }

    protected List<LightAppVO> getLightApps(List<? extends LightAppFilter> relations) {
        List<LightAppVO> result = new ArrayList<LightAppVO>();
        for (LightAppFilter relation : relations) {
            String lightAppId = relation.getLightAppId();
            LightApp lightApp = lightAppDao.findOne(lightAppId);
            if (lightApp != null && lightApp.getStatus() == 1) {
                result.add(po2vo(lightApp));
            }
        }
        return result;
    }

    //版本比较
    private int compareTo(String a[], String b[]) {
        int len1 = a.length;
        int len2 = b.length;
        int max = Math.max(len1, len2);
        int k = 0;
        while (k < max) {
            String str1 = k > (len1 - 1) ? "0" : a[k];
            String str2 = k > (len2 - 1) ? "0" : b[k];
            Integer v1, v2;
            try {
                v1 = Integer.parseInt(str1);
            } catch (Exception e) {
                v1 = 0;
            }
            try {
                v2 = Integer.parseInt(str2);
            } catch (Exception e) {
                v2 = 0;
            }
            int t = v1.compareTo(v2);
            if (t != 0) {
                return t;
            }
            k++;
        }
        return 0;
    }

    protected LightAppVO po2vo(LightApp lightApp) {
        LightAppVO lightAppVO = new LightAppVO();
        lightAppVO.setAppId(lightApp.getAppId());
        lightAppVO.setLightAppId(lightApp.getId());
        lightAppVO.setLightAppName(lightApp.getName());
        lightAppVO.setLightAppDesc(lightApp.getDesc());
        lightAppVO.setLightAppPic(lightApp.getPic());
        lightAppVO.setNewPic(lightApp.getNewPic());
        lightAppVO.setBgPic(lightApp.getBgPic());
        lightAppVO.setIosProtocol(lightApp.getIosProtocol());
        lightAppVO.setAndroidProtocol(lightApp.getAndroidProtocol());
        lightAppVO.setMinVersion(lightApp.getMinVersion());
        lightAppVO.setSort(lightApp.getSort());
        return lightAppVO;
    }

    public UserInfo getUserInfo(String openId) {
        Integer userId = auth2Helper.getUserIdByOpenId(openId);
        User user = userManager.getUser(userId);
        UserInfo userinfo = BeanUtil.copy(user, UserInfo.class);
        userinfo.setAvatar(user.getHeadPicFileName());
        userinfo.setSex(user.getSex() == 1 ? "男" : "女");
        if (user.getDoctor() != null) {
            userinfo.setHospital(user.getDoctor().getHospital());
            userinfo.setDepartments(user.getDoctor().getDepartments());
            userinfo.setTitle(user.getDoctor().getTitle());
            userinfo.setProvince(user.getDoctor().getProvince());
            userinfo.setIntroduction(user.getDoctor().getIntroduction());
            userinfo.setSkill(user.getDoctor().getSkill());
            Result result = ribbonManager.get("http://CREDIT/user/balance?userId={userId}", Result.class,user.getUserId());
            if (result.sucess()) {
                if (result.getData() instanceof JSONObject) {
                    JSONObject data = (JSONObject) result.getData();
                    userinfo.setBalance(data.getString("balance"));
                } else if (result.getData() instanceof Map<?, ?>) {
                    Map<String, Object> map = (Map<String, Object>) result.getData();
                    userinfo.setBalance(map.get("balance") != null ? map.get("balance").toString() : "0");
                }
            }
        }
        return userinfo;
    }

    public List<AccessToken> getOpenIdList(List<Integer> userIdList) {
        return auth2Helper.getOpenIdList(userIdList);
    }

    public List<AccessToken> getUserIdList(List<String> openIdList) {
        return auth2Helper.getUserIdList(openIdList);
    }


    /**
     * 发现页是否有显示
     * @param appId
     * @return
     */
    public boolean discovery(String appId) {
        LightApp lightApp = lightAppDao.findByAppId(appId);
        if (lightApp != null && lightApp.getStatus() == 1) {
            return true;
        }
        return false;
    }
}
