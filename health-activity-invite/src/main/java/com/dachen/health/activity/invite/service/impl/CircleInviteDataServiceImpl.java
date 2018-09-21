package com.dachen.health.activity.invite.service.impl;

import com.dachen.health.activity.invite.entity.CircleInvite;
import com.dachen.health.activity.invite.form.InvitationReportForm;
import com.dachen.health.activity.invite.service.CircleInviteDataService;
import com.dachen.health.activity.invite.vo.InvitationReportVO;
import com.dachen.sdk.annotation.Model;
import com.dachen.sdk.page.Pagination;
import java.util.Iterator;
import org.mongodb.morphia.aggregation.Accumulator;
import org.mongodb.morphia.aggregation.AggregationPipeline;
import org.mongodb.morphia.aggregation.Group;
import org.mongodb.morphia.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @author 钟良
 * @desc
 * @date:2017/6/2 11:19 Copyright (c) 2017, DaChen All Rights Reserved.
 */

@Model(value = CircleInvite.class)
@Service
public class CircleInviteDataServiceImpl extends BaseServiceImpl implements CircleInviteDataService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Pagination<InvitationReportVO> listInvitations(InvitationReportForm form, Integer pageIndex, Integer pageSize) {
        Query<CircleInvite> query =  this.createQuery();

        setMatchQuery(query, form);
        AggregationPipeline pipeline = dsForRW.createAggregation(CircleInvite.class)
            .match(query) //条件查询
            .group(
                Group.id(Group.grouping("userId"), Group.grouping("way")),//按邀请人，邀请方式分组
                Group.grouping("userId", Group.first("userId")),
                Group.grouping("way", Group.first("way")),
                Group.grouping("count", new Accumulator("$sum", 1)));//分组后统计数量

        Iterator<CircleInvite> iterator = pipeline.aggregate(CircleInvite.class);
        while (iterator.hasNext()) {
            CircleInvite info = iterator.next();
            logger.info(info.toString());
        }
        //Pagination<InvitationReportVO> page = new Pagination<>(list, total, pageIndex, pageSize);
        return null;
    }

    private void setMatchQuery(Query<CircleInvite> query, InvitationReportForm form) {
        /*if (StringUtil.isNotBlank(form.getInviteActivityId())) {
            query.filter("activityId", form.getInviteActivityId());
        }
        if (!Objects.isNull(form.getSubsystem())) {
            query.filter("subsystem", form.getSubsystem());
        }
        if (StringUtils.isNotBlank(form.getInviterName())) {
            query.filter("userName", createPattern(form.getInviterName()));
        }
        if (!Objects.isNull(form.getStartTime())) {
            query.filter("createTime >=", form.getStartTime());
        }
        if (!Objects.isNull(form.getEndTime())) {
            query.filter("createTime <=", form.getEndTime());
        }*/
    }


}
