package com.dachen.health.pack.account.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.vo.BankVO;
import com.dachen.health.pack.account.mapper.BankMapper;
import com.dachen.health.pack.account.service.IBankService;

@Service
public class BankServiceImpl implements IBankService {

    @Autowired
    private BankMapper bankMapper;

    /**
     * </p>获取银行列表</p>
     * 
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    public List<BankVO> getAll() {
        return bankMapper.getAll(); 
    }


    /**
     * </p>根据id获取</p>
     * @param id
     * @return
     * @author fanp
     * @date 2015年8月18日
     */
    public BankVO getOne(Integer id){
        return bankMapper.getOne(id); 
    }


	@Override
	public List<String> getBankNameByKeyword(BankCardParam param) {
		return bankMapper.getAllByNameKeyWord(param);
	}
    
    
}
