package com.dachen.health.controller.user;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.common.controller.AbstractController;

@RestController
@RequestMapping("/base")
public class TestController extends AbstractController {

    @RequestMapping("/testEncoding")
    public JSONMessage testEncoding(@RequestHeader("Accept-Encoding") String encoding) {
    	System.out.println(encoding);
		return JSONMessage.success(encoding + "============================");
    }    
}






