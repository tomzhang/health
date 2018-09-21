package com.dachen.health.circle.controller;

import com.dachen.careplan.api.client.CarePlanApiClientProxy;
import com.dachen.careplan.api.entity.CCarePlan;
import com.dachen.commons.JSONMessage;
import com.dachen.health.circle.service.ImService;
import com.dachen.sdk.exception.HttpApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/circle/test/im")
@Deprecated
public class CircleTestImController extends CircleBaseController {

    @Autowired
    protected ImService imService;

//    @Autowired
//    protected CarePlanApiClientProxy carePlanApiClientProxy;

    @RequestMapping(value = "/sendText")
    public JSONMessage remove(Integer fromUserId, String gid, String content) throws HttpApiException {
        Integer currentUserId = this.getCurrentUserId();
        imService.sendTextMsg(fromUserId, gid, content);
        return JSONMessage.success();
    }

//    @RequestMapping(value = "carePlan/findById")
//    public JSONMessage findById(String id) throws HttpApiException {
//        Integer currentUserId = this.getCurrentUserId();
//        CCarePlan cCarePlan = carePlanApiClientProxy.findById(id);
//        return JSONMessage.success(null, cCarePlan);
//    }
//
//    @RequestMapping(value = "carePlan/findById2")
//    public JSONMessage findById2(String id) throws HttpApiException {
//        Integer currentUserId = this.getCurrentUserId();
//        CCarePlan cCarePlan = carePlanApiClientProxy.findById2(id);
//        return JSONMessage.success(null, cCarePlan);
//    }
//
//    @RequestMapping(value = "carePlan/findById3")
//    public JSONMessage findById3(String id) throws HttpApiException {
//        Integer currentUserId = this.getCurrentUserId();
//        CCarePlan cCarePlan = carePlanApiClientProxy.findById3(id);
//        return JSONMessage.success(null, cCarePlan);
//    }
//
//    @RequestMapping(value = "carePlan/findById4")
//    public JSONMessage findById4(String id) throws HttpApiException {
//        Integer currentUserId = this.getCurrentUserId();
//        CCarePlan cCarePlan = carePlanApiClientProxy.findById4(id, "otherMsg=" + new Date());
//        return JSONMessage.success(null, cCarePlan);
//    }

}
