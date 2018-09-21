package com.dachen.health.pack.order.dao.impl;

import com.dachen.health.auto.dao.impl.BaseDaoImpl;
import com.dachen.health.pack.order.dao.IPendingOrderStatusDao;
import com.dachen.health.pack.order.entity.po.PendingOrderStatus;
import com.dachen.util.ConvertUtil;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by qinyuan.chen
 * Date:2016/12/28
 * Time:20:17
 */
@Repository
public class PendingOrderStatusDao extends BaseDaoImpl<PendingOrderStatus> implements IPendingOrderStatusDao{


    @Override
    public PendingOrderStatus add(PendingOrderStatus pendingOrderStatus) {
        pendingOrderStatus.setCreateTime(System.currentTimeMillis());
        pendingOrderStatus.setUpdateTime(System.currentTimeMillis());
        save(pendingOrderStatus);
        return pendingOrderStatus;
    }

    @Override
    public void updateById(PendingOrderStatus pendingOrderStatus) {
        pendingOrderStatus.setUpdateTime(System.currentTimeMillis());
        Map<String,Object> map= ConvertUtil.objectToMap(pendingOrderStatus);
        map.remove("id");
        update(PendingOrderStatus.class,pendingOrderStatus.getId(),map);
    }

    @Override
    public void updateByOrderId(PendingOrderStatus pendingOrderStatus) {
        pendingOrderStatus.setUpdateTime(System.currentTimeMillis());
        Map<String,Object> map= ConvertUtil.objectToMap(pendingOrderStatus);
        map.remove("orderId");
        Query<PendingOrderStatus> query = dsForRW.createQuery(PendingOrderStatus.class).filter("orderId",pendingOrderStatus.getOrderId());
        UpdateOperations<PendingOrderStatus> ops = dsForRW.createUpdateOperations(PendingOrderStatus.class);
        for( Map.Entry<String, Object> eachObj:map.entrySet()){
            ops.set(eachObj.getKey(), eachObj.getValue());
        }
        dsForRW.findAndModify(query, ops);
    }

    @Override
    public void deleteByOrderId(Integer orderId) {
        Query<PendingOrderStatus> query = dsForRW.createQuery(PendingOrderStatus.class);
        query.filter("orderId",orderId);
        dsForRW.delete(query);
    }

    @Override
    public PendingOrderStatus queryByOrderId(Integer orderId) {
        Query<PendingOrderStatus> query = dsForRW.createQuery(PendingOrderStatus.class);
        query.filter("orderId",orderId);
        return query.get();
    }

    @Override
    public List<Integer> queryAllOrderIds() {
        Query<PendingOrderStatus> query = dsForRW.createQuery(PendingOrderStatus.class);
        query.filter("orderStatus",1);
        List<PendingOrderStatus> list=query.asList();
        if(list!=null&&list.size()>0){
            List<Integer> orderIds=new ArrayList<>();
            for(PendingOrderStatus pos : list){
                orderIds.add(pos.getOrderId());
            }
            return orderIds;
        }else{
            return Collections.emptyList();
        }
    }
}
