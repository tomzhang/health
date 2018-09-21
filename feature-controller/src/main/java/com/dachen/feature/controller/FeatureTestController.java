package com.dachen.feature.controller;

import com.dachen.sdk.component.FreemarkerComponent;
import com.dachen.sdk.json.JSONMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/feature/test")
public class FeatureTestController extends FeatureBaseController {

    @Autowired
    protected FreemarkerComponent freemarkerComponent;

    @RequestMapping
    public String index(String templateFile, String title, String content) throws Exception {
        Map<String,Object> params = new HashMap<>();
        params.put("title", title);
        params.put("content", content);
        String ret = freemarkerComponent.render(templateFile, params);
        logger.debug("ret={}", ret);
        return ret;
    }

}
