package com.dachen.line.stat.dao;

import com.dachen.line.stat.entity.vo.ServiceStatistic;


/**
 * 
 * @author liwei
 * @date 2015/8/26
 */
public interface IServiceStatisticDao {
    
    public  ServiceStatistic getServiceStatisticService();
    
    public  void updateTotalReceptionNum();
}
