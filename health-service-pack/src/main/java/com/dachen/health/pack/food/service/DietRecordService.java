package com.dachen.health.pack.food.service;

import com.dachen.commons.page.PageVO;

/**
 * Created by fuyongde on 2017/2/23.
 */
public interface DietRecordService {

    /**
     * 添加一条饮食记录
     * @param foodName  食物名称
     * @param reactions 不良反应
     * @param dietTime  进食时间
     * @param patientId 患者id
     */
    void save(String foodName, String reactions, Long dietTime, Integer patientId);

    /**
     * 分页查询患者的饮食记录
     * @param patientId 患者id
     * @param pageIndex
     * @param pageSize
     * @return
     */
    PageVO findByPatientId(Integer patientId, Integer pageIndex, Integer pageSize);
}
