package com.dachen.health.group.hospital.dao;

import java.util.Collection;
import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalLevelPo;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.group.group.entity.param.HospitalBaseParam;

/**
 * 
 * @author longjh
 *      data:2017/08/18
 */
public interface IHospitalBaseDao {
    
    /**
     * 获取所有医院等级信息
     */
    List<HospitalLevelPo> findAllHospitalLevel();
    
    /**
     * 获取行政区域信息
     * @param area
     *      查询参数
     */
    List<Area> findArea(Area area);
    
    /**
     * 获取行政区域信息
     * @param area
     *      查询参数
     */
    List<Area> findArea(Collection<Integer> coll);
    
    /**
     * 查询医院信息
     * @param hospitalBaseParam
     *      查询参数
     */
    PageVO findHospital(HospitalBaseParam hospitalBaseParam);

    /**
     * 根据医院名称集合查询医院等级
     */
    String findLevelByHospitalName(List<String> listName);

    List<HospitalVO> pageQueryHospital();

    List<HospitalVO> getByIds(List<String> ids);
}
