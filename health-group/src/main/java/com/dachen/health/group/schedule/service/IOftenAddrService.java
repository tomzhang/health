package com.dachen.health.group.schedule.service;

import java.util.List;

import com.dachen.health.group.schedule.entity.po.OftenAddr;

/**
 * ProjectName： health-group<br>
 * ClassName： IOftenAddrService<br>
 * Description： 常用地址service<br>
 * 
 * @author fanp
 * @createTime 2015年8月14日
 * @version 1.0.0
 */
public interface IOftenAddrService {

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
