package com.dachen.health.controller.user;

import com.dachen.health.commons.service.IQrCodeService;
import com.dachen.util.PropertiesUtil;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wangl
 * @desc
 * @date:2017/10/1011:18
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Controller
public class ShortUrlController {

    private Logger logger = LoggerFactory.getLogger(ShortUrlController.class);

    @Autowired
    IQrCodeService qrCodeService;

    @RequestMapping("/qr/p/nologin/{oid}")
    public void process(@PathVariable String oid, HttpServletResponse response, String access_token){
        logger.info("oid = {}, access_token = {}", oid, access_token);
        String url = qrCodeService.getUrlByOid(oid);
        if(StringUtils.isEmpty(url)){
            try {
                response.getWriter().write("二维码已失效,请重新生成");
                logger.info("二维码已失效,请重新生成");
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            return;
        }
        if(StringUtils.indexOf(access_token, ",") > 0){
            access_token = access_token.split(",")[0];
        }
        url = PropertiesUtil.addUrlParemeter(url,"access_token", access_token);
        logger.info("send redirect url = {}", url);
        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
    }
}
