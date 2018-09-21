package com.dachen.health.pack.account.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.dao.IdxRepository;
import com.dachen.health.commons.constants.AccountEnum;
import com.dachen.health.pack.account.entity.param.RechargeParam;
import com.dachen.health.pack.account.entity.po.Recharge;
import com.dachen.health.pack.account.entity.vo.RechargeVO;
import com.dachen.health.pack.account.mapper.RechargeMapper;
import com.dachen.health.pack.account.service.IRechargeService;
import com.dachen.util.StringUtil;

@Service
public class RechargeServieImpl implements IRechargeService {

    @Autowired
    private IdxRepository idxRepository;
    
    @Autowired
    private RechargeMapper rechargeMapper;
    
    /**
     * </p>生成第三方支付订单号</p>
     * 
     * @return
     * @author fanp
     * @date 2015年8月7日
     */
	public String nextPayNo() {
		// 生成订单号，规则：给定默认起始值（15080700+自增序列）*10+0，最后一位为用户id%10，现默认为0
//		Integer payNo = idxRepository.nextPayNoIdx(idxType.PayNo);
//		if (payNo == null) {
//			throw new ServiceException("支付订单id获取失败");
//		}
//		System.out.println(idxRepository.nextDoctorNum(idxType.doctorNum));
//		return (15080700 + payNo) * 10;
		return StringUtil.randomUUID();
	}
    
    /**
     * </p>添加充值记录</p>
     * 
     * @param param
     * @return 第三方支付订单编号
     * @author fanp
     * @date 2015年8月10日
     */
    public Recharge addRecharge(RechargeParam param){
        Recharge recharge = new Recharge();
        recharge.setUserId(param.getUserId());
        recharge.setRechargeMoney(param.getMoney());
        recharge.setPayType(param.getPayType());
        recharge.setPayNo(this.nextPayNo());
        recharge.setCreateTime(System.currentTimeMillis());
        recharge.setRechargeStatus(AccountEnum.RechargeStatus.初始.getIndex());
        recharge.setSourceType(param.getSourceType());
        recharge.setSourceId(param.getSourceId());
        rechargeMapper.addRecharge(recharge);
        
        return recharge;
    }
    
    /**
     * </p>充值成功</p>
     * @param param
     * @author fanp
     * @date 2015年8月13日
     */
    public boolean handleSuccess(RechargeParam param){
        boolean flag = false;
        /* 
         * 查询充值是否成功，
         * 如果成功则更新状态，生成账户记录，
         * 如果状态初始，则主动调用支付查询接口
         */
        
        RechargeVO vo = rechargeMapper.getOne(param);
        if(vo == null){
            throw new ServiceException("支付异常");
        }
        
        return flag;
    }
    
    /**
     * 查询一条充值记录
     */
	public RechargeVO findRechargeByPayNo(RechargeParam param) {
		return rechargeMapper.getOne(param);
	}
	
	/**
	 * 修改状态
	 */
	public void updateRecharge(RechargeVO rechargeVo) {
		 rechargeMapper.update(rechargeVo);
	}

	/**
	 * 根据订单查询充值记录
	 */
	public Recharge findRechargeByOrderId(RechargeParam param) {
		return rechargeMapper.getRechargeByOrder(param);
	}

	public RechargeVO findOneByOrderId(Integer orderId) {
		RechargeParam param = new RechargeParam();
		param.setSourceId(orderId);
		return rechargeMapper.getOneByOrderId(param);
	}
}
