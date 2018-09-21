package com.dachen.health.pack.account.service;

import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.po.Recharge;
import com.dachen.health.pack.account.entity.vo.RechargeVO;

public interface IRechargeService {

    /**
     * </p>生成第三方支付订单号</p>
     * 
     * @return
     * @author fanp
     * @date 2015年8月7日
     */
	String nextPayNo();

    /**
     * </p>添加充值记录</p>
     * 
     * @param param
     * @return 第三方支付订单编号
     * @author fanp
     * @date 2015年8月10日
     */
    Recharge addRecharge(RechargeParam param);
    
    
    /**
     * </p>充值成功</p>
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    boolean handleSuccess(RechargeParam param);
    
    /**
     * </p>订单号查询记录</p>
     * @param param PayNo
     * @author fanp
     * @date 2015年8月17日
     */
    RechargeVO findRechargeByPayNo(RechargeParam param);
    
    /**
     * </p>修改充值记录</p>
     * @param param PayNo
     * @author xiepei
     * @date 2015年8月17日
     */
    void updateRecharge(RechargeVO rechargeVo);
    
    
    /**
     * </p></p>
     * @param param PayNo
     * @author xiepei
     * @date 2015年8月17日
     */
    Recharge findRechargeByOrderId(RechargeParam param);
    
    /**
     * 获取已支付记录
     * @param param
     * @return
     */
    public RechargeVO findOneByOrderId(Integer orderId);
    
}
