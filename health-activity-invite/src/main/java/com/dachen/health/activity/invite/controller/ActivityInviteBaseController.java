package com.dachen.health.activity.invite.controller;

import com.dachen.util.ReqUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ActivityInviteBaseController {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Integer getCurrentUserId() {
        Integer currentUserId = ReqUtil.instance.getUserId();
        if (null == currentUserId || 0 == currentUserId) {
            return null;
        }
        return currentUserId;
    }
}
