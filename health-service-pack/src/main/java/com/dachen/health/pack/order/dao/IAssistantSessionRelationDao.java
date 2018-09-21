package com.dachen.health.pack.order.dao;

import java.util.List;

import com.dachen.health.auto.dao.BaseDao;
import com.dachen.health.pack.order.entity.po.AssistantSessionRelation;

/**
 * Created by qinyuan.chen
 * Date:2017/1/4
 * Time:19:47
 */
public interface IAssistantSessionRelationDao extends BaseDao<AssistantSessionRelation> {

    public AssistantSessionRelation add(AssistantSessionRelation asr);

    public AssistantSessionRelation queryByGId(String msgGroupId);

    public void update(AssistantSessionRelation asr);

    public void updateByGid(AssistantSessionRelation asr);

    /**
     * 
     * @param asr
     * @return
     */
    public List<AssistantSessionRelation> queryByConditions(AssistantSessionRelation asr);
    
    void deleteByIds(List<String> ids);
}
