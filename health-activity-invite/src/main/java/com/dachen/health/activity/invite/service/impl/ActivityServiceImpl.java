package com.dachen.health.activity.invite.service.impl;

import com.dachen.health.activity.invite.entity.Activity;
import com.dachen.health.activity.invite.service.ActivityService;
import com.dachen.sdk.annotation.Model;
import java.util.List;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/22 17:27 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Model(Activity.class)
@Service
public class ActivityServiceImpl extends BaseServiceImpl implements ActivityService {

    @Override
    public List<Activity> findList(Integer type) {
        Query<Activity> query = this.createQuery();
        query.filter("type", type);
        return query.asList();
    }
}
