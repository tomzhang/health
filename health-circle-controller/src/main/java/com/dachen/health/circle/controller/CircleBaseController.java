package com.dachen.health.circle.controller;

import com.dachen.util.ReqUtil;

public abstract class CircleBaseController {
    protected Integer getCurrentUserId() {
        Integer currentUserId = ReqUtil.instance.getUserId();
        if (null == currentUserId || 0 == currentUserId) {
            return null;
        }
        return currentUserId;
    }
}
