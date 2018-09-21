package com.dachen.health.controller.openapi;

import com.dachen.common.auth.Auth2Helper;
import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.constants.ImageDataEnum;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.Source;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.example.UserExample;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.health.commons.vo.UserSource;
import com.dachen.health.openApi.entity.OpenUserVO;
import com.dachen.health.openApi.entity.SimpleOpenUserVO;
import com.dachen.health.openApi.service.IOpenUserService;
import com.dachen.health.pack.order.service.IImageDataService;
import com.dachen.sdk.exception.HttpApiException;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author liangcs
 * @desc
 * @date:2017/5/2 16:58
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@RestController
@RequestMapping("/open/user")
public class OpenUserController {

    @Autowired
    private UserManager userManager;

    @Autowired
    private IOpenUserService openUserService;

    @Resource
    private IImageDataService imageDataService;
    
    @Autowired
    Auth2Helper auth2Helper;

    /** logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(OpenUserController.class);


    /**
     * @api {post} /open/user/register 注册
     * @apiVersion 1.0.0
     * @apiName register
     * @apiGroup 第三方集成
     * @apiDescription 第三方集成注册接口
     *
     * @apiParam {String}    	appId      appId
     * @apiParam {String}    	name      用户名
     * @apiParam {String}   	telephone              手机号
     * @apiParam {String}   	password       密码
     * @apiParam {String}   	userType       用户类型 医生3
     *
     * @apiSuccess {Integer} resultCode    返回状态吗
     * @apiSuccess {String} code    安全码
     * @apiSuccess {String} callbackUrl    回调地址
     *
     * @apiAuthor  liangcs
     * @date 2017年5月03日
     */
    @RequestMapping("register")
    public JSONMessage register(UserExample userExample, String appId) {

        if (Objects.isNull(userExample.getUserType()) || Objects.equals(userExample.getUserType(), UserType.customerService.getIndex())) {
            throw new ServiceException("用户类型异常");
        }

        if (!openUserService.checkAppId(appId)) {
            throw new ServiceException("appId非法");
        }

        UserSource userSource = new UserSource();
        userSource.setSourceType(Source.ThirdParty.getIndex());
        userSource.setTerminal(UserEnum.Terminal.xg.getIndex());
        userSource.setAppId(appId);
        userExample.setUserSource(userSource);
        userManager.registerIMUser(userExample);

        return JSONMessage.success(openUserService.login(appId, userExample.getTelephone(), userExample.getPassword(), userExample.getUserType()));
    }

    /**
     * @api {post} /open/user/login 登录
     * @apiVersion 1.0.0
     * @apiName login
     * @apiGroup 第三方集成
     * @apiDescription 第三方集成登录接口
     *
     * @apiParam {String}    	appId      appId
     * @apiParam {String}   	telephone              手机号
     * @apiParam {String}   	password       密码
     * @apiParam {String}   	userType       用户类型 医生3
     *
     * @apiSuccess {Integer} resultCode    返回状态吗
     * @apiSuccess {String} code    安全码
     * @apiSuccess {String} callbackUrl    回调地址
     *
     * @apiAuthor  liangcs
     * @date 2017年5月03日
     */
    @RequestMapping("login")
    public JSONMessage login(String appId, String telephone, String password, Integer userType) {
        return JSONMessage.success(openUserService.login(appId, telephone, password, userType));
    }

    @RequestMapping(value = "loginByCode", method = RequestMethod.POST)
    public JSONMessage loginByCode(String appId, String telephone, Integer userType) {
        return JSONMessage.success(openUserService.loginByCode(appId, telephone, userType));
    }


    /**
     * @api {post} /open/user/getUser 获取用户信息
     * @apiVersion 1.0.0
     * @apiName getUser
     * @apiGroup 第三方集成
     * @apiDescription 第三方集成获取用户信息接口
     *
     * @apiParam {String}    	appId      appId
     * @apiParam {String}   	code              手机号
     * @apiParam {String}   	sign       密码
     *
     * @apiSuccess {Integer} resultCode    返回状态吗
     * @apiSuccess {String} headPicFileName    头像地址
     * @apiSuccess {String} name    姓名
     * @apiSuccess {String} area    地域
     * @apiSuccess {String} hospital    医院
     * @apiSuccess {String} department    科室
     * @apiSuccess {String} title    职称
     * @apiSuccess {String} telephone    手机号
     * @apiSuccess {String} registerTime    注册时间
     * @apiSuccess {String} AuthenticateTime    认证时间
     * @apiSuccess {Integer} status    状态
     * @apiSuccess {String[]} images    证书地址
     * @apiSuccess  {String} provinceId 省份id
     * @apiSuccess   {String} cityId 城市id
     * @apiSuccess   {String} countryId 区县id
     * @apiSuccess   {String} province 省份名称
     * @apiSuccess   {String} city 城市名称
     * @apiSuccess  {String} country 区县名称
     *
     * @apiAuthor  liangcs
     * @date 2017年5月03日
     */
    @RequestMapping("getUser")
    public JSONMessage getUser(String appId, String code, String sign) {
        LOGGER.info("第三方集成获取用户信息接口----:{}", " " + "appId = [" + appId + "], code = [" + code + "], sign = [" + sign + "]");
        OpenUserVO openUserVO = openUserService.getUser(appId, code, sign);

        //认证图片(service 没有pack的依赖，获取证书的代码提到controller)
        List<Map<String, Object>> result = imageDataService.findDoctorImgData(5, openUserVO.getUserId());
        if (!CollectionUtils.isEmpty(result)) {
            List<String> images = new ArrayList<>();
            for (Map<String, Object> map : result) {
                images.add(map.get("url") + "");
            }
            openUserVO.setImages(images);
        }
        return JSONMessage.success(openUserVO);
    }

    /**
     * 通过openId获取用户信息
     * 这个接口走open-api加密校验
     *
     * @param openId
     * @return
     */
    @RequestMapping("/getUserByOpenId")
    public JSONMessage getUserByOpenId(@RequestParam String openId) {
        return JSONMessage.success(openUserService.getUserByOpenId(openId));
    }

    /**
     * 獲取用戶區域信息
     * @param appId
     * @param sign
     * @param userId
     * @return
     */
    @RequestMapping("getUserArea")
    public JSONMessage getUserArea(String timestamp, String appId, String sign, Integer[] userId) {
        if (userId == null || userId.length == 0) {
            return JSONMessage.success();
        }

        List<Integer> userIds = Arrays.asList(userId);
        return JSONMessage.success(openUserService.getUserArea(timestamp, appId, sign, userIds));
    }

    /**
     * @api {post} /open/user/update 更新用户信息
     * @apiVersion 1.0.0
     * @apiName update
     * @apiGroup 第三方集成
     * @apiDescription 第三方集成更新用户信息接口
     *
     * @apiParam {String}    	appId      appId
     * @apiParam {String}   	code              code
     * @apiParam {String} headPicFileName    头像地址
     * @apiParam {String} hospital    医院名称
     * @apiParam {String} hospitalId    医院id
     * @apiParam {String} dept    科室
     * @apiParam {String} deptId    科室id
     * @apiParam {String} title    职称
     * @apiParam {String[]} doctorsImage    认证图片地址
     *
     * @apiSuccess {Integer} resultCode    返回状态码
     * @apiAuthor  liangcs
     * @date 2017年5月03日
     */
    @RequestMapping("update")
    public JSONMessage update(String appId, String code, String headPicFileName, String hospital,
        String hospitalId, String dept, String deptId, String title, String... doctorsImage)
        throws HttpApiException {

        User user = openUserService.update(appId, code, headPicFileName, hospital, hospitalId, dept, deptId, title);

        //更新认证图片
        if ((doctorsImage != null) && (doctorsImage.length > 0)) {
            imageDataService.deleteImgData(ImageDataEnum.doctorCheckImage.getIndex(), user.getUserId());
            imageDataService.addDoctorImages(user.getUserId(), doctorsImage);
        }

        //医生状态改为待审核
        userManager.updateStatus(user.getUserId());

        return JSONMessage.success();
    }

    @RequestMapping("init")
    public JSONMessage initApp(String name, String callbackUrl, String notifyUrl, String key) {

        if (!Objects.equals(key, "dachen@2017")) {
            throw new ServiceException("验证失败");
        }

        return JSONMessage.success(openUserService.init(name, callbackUrl, notifyUrl));
    }
    
    
    
    @RequestMapping(value = "upgradeUserLevel", method = RequestMethod.GET)
    public JSONMessage upgradeUserLevel(@RequestParam(required = true) String openId,@RequestParam(required = true) String appKey) {
    	//根据openid获取医生的userId需要处理 诡异的问题
    	if(Objects.isNull(openId)){
    		return JSONMessage.failure("openId为空,更新用户身份失败");
    	}
    	Integer userId;
		try {
			userId = auth2Helper.getUserIdByOpenId(openId);
		} catch (Exception e) {
			return JSONMessage.failure("openId转userId失败,更新用户身份失败");
		}
    	if(Objects.isNull(userId)){
    		return JSONMessage.failure("openId转userId失败,更新用户身份失败");
    	}
        return JSONMessage.success(userManager.upgradeUserLevel(userId,"扫一扫签到"));
    }

    @ApiOperation(value = "通过openId获取简单用户信息(带圈子id)", response = SimpleOpenUserVO.class)
    @RequestMapping(value = "/getSimpleUserByOpenId", method = RequestMethod.GET)
    public JSONMessage getSimpleUserByOpenId(@RequestParam String openId) {
        return JSONMessage.success(openUserService.getSimpleUserByOpenId(openId));
    }

}
