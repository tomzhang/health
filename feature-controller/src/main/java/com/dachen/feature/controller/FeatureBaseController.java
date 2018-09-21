package com.dachen.feature.controller;

import com.dachen.sdk.component.ReqComponent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class FeatureBaseController {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public Integer getCurrentUserId() {
        Integer currentUserId = ReqComponent.instance.getUserId();
        return currentUserId;
    }
}
