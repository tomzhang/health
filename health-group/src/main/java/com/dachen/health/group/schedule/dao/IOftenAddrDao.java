package com.dachen.health.group.schedule.dao;

import java.util.List;

import com.dachen.health.group.schedule.entity.po.OftenAddr;

public interface IOftenAddrDao {

    /**
     * </p>添加常用地址</p>
     * 
     * @param hospital
     * @param doctorId
     * @author fanp
     * @date 2015年8月14日
     */
    void add(String hospital, Integer doctorId);

    /**
     * </p>查询常用地址</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月14日
     */
    List<OftenAddr> getAll(Integer doctorId);

    /**
     * </p>删除常用地址</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月14日
     */
    void delete(OftenAddr po);
    
}
