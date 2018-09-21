package com.dachen.health.group.schedule.dao.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.schedule.dao.IOftenAddrDao;
import com.dachen.health.group.schedule.entity.po.OftenAddr;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

@Repository
public class OftenDaoImpl extends NoSqlRepository implements IOftenAddrDao {

    /**
     * </p>添加常用地址</p>
     * 
     * @param hospital
     * @param doctorId
     * @author fanp
     * @date 2015年8月14日
     */
    public void add(String hospital, Integer doctorId) {
        DBObject query = new BasicDBObject();
        query.put("doctorId", doctorId);
        query.put("hospital", hospital);

        dsForRW.getDB().getCollection("c_often_addr").update(query, query, true, true);
    }

    /**
     * </p>查询常用地址</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月14日
     */
    public List<OftenAddr> getAll(Integer doctorId) {
        return dsForRW.createQuery("c_often_addr", OftenAddr.class).retrievedFields(false, "doctorId").field("doctorId").equal(doctorId).asList();
    }

    /**
     * </p>删除常用地址</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月14日
     */
    public void delete(OftenAddr po) {
        DBObject query = new BasicDBObject();
        query.put("doctorId", po.getDoctorId());
        query.put("_id", po.getId());

        dsForRW.getDB().getCollection("c_often_addr").remove(query);
    }

}
