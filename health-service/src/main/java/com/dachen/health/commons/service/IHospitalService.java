package com.dachen.health.commons.service;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.vo.HospitalDeptVO;
import java.util.List;

/**
 * 医院信息服务接口
 * @author longjh
 *      date:2017/08/22
 */
public interface IHospitalService {
    
    /**
     * 通过更新时间获取医院信息
     * @param updateTime
     *      如果值为null则获取所有医院信息，如果值不为null则获取大于等于updateTime的医院信息
     */
    PageVO getHospitalByUpdate(String appKey, String orgId, Long updateTime, Integer pageIndex, Integer pageSize, String hospital, String hospitalId);

    List<HospitalDeptVO> getDepartments(String appKey, String orgId);
}
