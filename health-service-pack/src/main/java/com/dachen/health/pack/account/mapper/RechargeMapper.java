package com.dachen.health.pack.account.mapper;

import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.po.Recharge;
import com.dachen.health.pack.account.entity.vo.RechargeVO;

public interface RechargeMapper {

    /**
     * </p>添加充值记录</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月7日
     */
    void addRecharge(Recharge recharge);
    
    
    /**
     * </p>查询充值信息</p>
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    RechargeVO getOne(RechargeParam param);
    
    /**
     * </p>获取已支付的支付数据</p>
     * @param param
     * @author peiX
     * @date 2015年8月13日
     */
    RechargeVO getOneByOrderId(RechargeParam param);
    /**
     * </p>查询充值支付信息</p>
     * @param param
     * @author peiX
     * @date 2015年8月17日
     */
    void update(RechargeVO rechargeVo);
    
    /**
     * </p>依据订单ID查询充值记录</p>
     * @param param
     * @return
     */
    Recharge getRechargeByOrder(RechargeParam param);
    
    
}
