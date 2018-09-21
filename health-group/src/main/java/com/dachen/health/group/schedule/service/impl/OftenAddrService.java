package com.dachen.health.group.schedule.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.group.schedule.dao.IOftenAddrDao;
import com.dachen.health.group.schedule.entity.po.OftenAddr;
import com.dachen.health.group.schedule.service.IOftenAddrService;

@Service
public class OftenAddrService implements IOftenAddrService {

    @Autowired
    protected IOftenAddrDao oftenAddrDao;
    
    /**
     * </p>添加常用地址</p>
     * 
     * @param hospital
     * @param doctorId
     * @author fanp
     * @date 2015年8月14日
     */
    public void add(String hospital, Integer doctorId){
        oftenAddrDao.add(hospital, doctorId);
    }

    /**
     * </p>查询常用地址</p>
     * 
     * @param doctorId
     * @return
     * @author fanp
     * @date 2015年8月14日
     */
    public List<OftenAddr> getAll(Integer doctorId){
        return oftenAddrDao.getAll(doctorId);
    }

    /**
     * </p>删除常用地址</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月14日
     */
    public void delete(OftenAddr po){
        oftenAddrDao.delete(po);
    }
    
}
