package com.dachen.health.activity.invite.service;

import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.sdk.db.template.ServiceBase;
import java.util.List;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 17:25 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface ActivityService extends ServiceBase {
    List<Activity> findList(Integer type);
}
