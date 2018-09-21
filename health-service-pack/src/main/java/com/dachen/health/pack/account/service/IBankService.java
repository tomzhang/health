package com.dachen.health.pack.account.service;

import java.util.List;

import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.vo.BankVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IBankService<br>
 * Description： 银行service<br>
 * 
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
public interface IBankService {

    /**
     * </p>获取银行列表</p>
     * 
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    List<BankVO> getAll();

    /**
     * </p>根据id获取</p>
     * @param id
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    BankVO getOne(Integer id);
    
    /**
     * 根据关键字获取对应的银行名称列表
     * @param param
     * @return
     */
    List<String> getBankNameByKeyword(BankCardParam param);
    
}
