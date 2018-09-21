package com.dachen.health.controller.homepage;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.service.impl.HomePageService;
import com.dachen.util.ReqUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * Created by wangjin on 2017/12/19.
 */
@RestController
@RequestMapping("/home/page")
@Api(value = "医生圈首页",description = "医生圈首页")
public class HomePageController{

	@Autowired
	HomePageService homePageService;
   
    @ApiOperation(value = "业务模块")
    @RequestMapping(value = "/businessModules", method = RequestMethod.GET)
    public JSONMessage businessModules() {
        String currentVersion = ReqUtil.getVersion();
        return JSONMessage.success(homePageService.getModuleConfigures(currentVersion));
    }
}
