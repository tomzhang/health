package com.dachen.health.group;

import java.util.List;

import com.dachen.commons.page.PageVO;
import com.dachen.health.base.entity.po.Area;
import com.dachen.health.base.entity.po.HospitalLevelPo;
import com.dachen.health.base.entity.vo.HospitalVO;
import com.dachen.health.group.group.entity.param.HospitalBaseParam;

public interface IHospitalBaseService {
    
    /**
     * 获取所有的医院等级信息
     */
    List<HospitalLevelPo> findAllHospitalLevel();
    
    /**
     * 获取行政区域信息
     */
    List<Area> findArea(Area area);
    
    /**
     * 获取医院信息
     */
    PageVO findHospital(HospitalBaseParam hospitalBaseParam);

    /**
     * 根据医院名称集合查询医院等级
     */
    String findLevelByHospitalName(List<String> listName);

    List<HospitalVO> pageQueryHospital();

    List<HospitalVO> getByIds(List<String> ids);
}
