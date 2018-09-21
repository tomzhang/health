package com.dachen.health.pack.order.service;

import java.util.List;

import com.dachen.health.pack.order.entity.po.AssistantSessionRelation;

/**
 * Created by qinyuan.chen
 * Date:2017/1/4
 * Time:19:51
 */
public interface IAssistantSessionRelationService {

    public AssistantSessionRelation add(AssistantSessionRelation asr,Integer type);

    public void update(AssistantSessionRelation asr);

}
