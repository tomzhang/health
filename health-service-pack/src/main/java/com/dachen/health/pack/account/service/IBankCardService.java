package com.dachen.health.pack.account.service;

import java.util.List;

import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.po.BankCard;
import com.dachen.health.pack.account.entity.vo.BankCardVO;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： IBankService<br>
 * Description： 银行卡绑定service<br>
 * 
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
public interface IBankCardService {

    /**
     * </p>添加银行卡</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月17日
     */
	BankCard add(BankCardParam param);
    
    /**
     * 添加集团银行卡
     * @param param
     */
	BankCard addGroupBankCard(BankCardParam param);

    /**
     * </p>删除银行卡</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月17日
     */
    void delete(BankCardParam param);
    
    /**
     * 根据ID获取银行卡
     * @param id
     * @return
     */
    BankCardVO getBankCardById(Integer id);

    /**
     * </p>获取银行卡列表</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    List<BankCardVO> getAll(Integer userId);
    
    /**
     * 获取集团所有银行卡ID
     * @param groupId
     * @return
     */
    List<BankCardVO> getAllByGroupId(String groupId);

    /**
     * </p>修改银行卡</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月17日
     */
    void update(BankCardParam param);
    
    /**
     * 设置状态
     * id为是待设银行卡ID
     * @param param
     */
    void updateStatus(BankCardParam param);
    
    /**
     * 清空userId的银行卡所有默认状态
     * @param param
     */
    void updateunDefault(BankCardParam param);
    
    
    /**
     * 获取医生默认的银行卡
     * @param param
     * @return
     */
    BankCardVO getDocDefaultCard(BankCardParam param);
    
    /**
     * 获取集团默认的银行卡
     * @param param
     * @return
     */
    BankCardVO getGroupDefaultCard(BankCardParam param);
    
}
