package com.dachen.health.group.fee.service;

import com.dachen.health.group.fee.entity.param.FeeParam;
import com.dachen.health.group.fee.entity.vo.FeeVO;

public interface IFeeService {

    /**
     * </p>获取收费设置</p>
     * 
     * @param groupId
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    FeeVO get(String groupId);

    /**
     * </p>收费设置</p>
     * 
     * @return
     * @author fanp
     * @date 2015年9月21日
     */
    void save(FeeParam param);
    
    /**
     * 检查计划关怀价格范围 
     * @param groupId
     * @param price
     * @return
     */
    public Boolean checkFeeIsCarePlan(String groupId,String price);
    
    /**
     * 通过登录用户的医生id，查询该医生所有集团的收费  （取所有集团中的最高和最低 做为收费设置）
     * @param doctorId
     * @return
     * @author wangqiao
     * @date 2015年12月28日
     * 此接口不适用健康关怀，因为健康关怀属于集团，它的价格只能由隶属集团决定 add by xieping
     */
	public FeeVO getByDoctorId(Integer doctorId);
    
    /**
     * 获取健康关怀fee区间
     * @param doctorId
     * @param groupId
     * @return
     */
    public FeeVO get(Integer doctorId, String groupId);
    
}
