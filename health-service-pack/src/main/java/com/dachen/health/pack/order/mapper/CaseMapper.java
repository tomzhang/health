package com.dachen.health.pack.order.mapper;

import com.dachen.health.pack.order.entity.param.CheckInParam;
import com.dachen.health.pack.order.entity.po.Case;
import com.dachen.health.pack.order.entity.vo.CheckInVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： CaseMapper<br>
 * Description： 病例表mapper<br>
 * 
 * @author fanp
 * @createTime 2015年9月7日
 * @version 1.0.0
 */
public interface CaseMapper {

    /**
     * </p>添加报到病例</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年9月7日
     */
    void add(Case po);
    
    /**
     * </p>根据报到查找病例</p>
     * @param param
     * @return
     * @author fanp
     * @date 2015年9月8日
     */
    CheckInVO getByCheckIn(CheckInParam param);
    
    void updateCase(Case po);
    
    void updateAllCase(Case po);
    /**
     * 根据报道id查询病情信息  add by  liwei 
     * @param param
     * @return
     */
    CheckInVO selectCaseByCheckInId(CheckInParam param);

}