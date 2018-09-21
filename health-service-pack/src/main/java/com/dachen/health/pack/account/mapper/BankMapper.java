package com.dachen.health.pack.account.mapper;

import java.util.List;

import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.vo.BankVO;

public interface BankMapper {

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
     * 
     * @param id
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    BankVO getOne(Integer id);
    
    List<String> getAllByNameKeyWord(BankCardParam param);

}