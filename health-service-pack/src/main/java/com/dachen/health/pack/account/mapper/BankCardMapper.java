package com.dachen.health.pack.account.mapper;

import java.util.List;

import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.po.BankCard;
import com.dachen.health.pack.account.entity.vo.BankCardVO;
import com.dachen.health.pack.income.entity.param.SettleParam;

/**
 * ProjectName： health-service-pack<br>
 * ClassName： BankMapper<br>
 * Description： 银行卡mapper<br>
 * 
 * @author fanp
 * @createTime 2015年8月17日
 * @version 1.0.0
 */
public interface BankCardMapper {

    /**
     * </p>添加银行卡</p>
     * 
     * @param po
     * @author fanp
     * @date 2015年8月17日
     */
    void add(BankCard po);

    /**
     * </p>删除银行卡</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月17日
     */
    void delete(BankCardParam param);

    /**
     * </p>获取医生银行卡列表</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    List<BankCardVO> getAll(Integer userId);
    
    
    
    /**
     * 获取集团银行卡列表
     * @param groupId
     * @return
     */
    List<BankCardVO> getAllByGroupId(String groupId);

    /**
     * </p>根据卡号查找</p>
     * 
     * @param bankNo
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    List<BankCardVO> getByNo(String bankNo);

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
     * id为待设置
     * @param param
     */
    void updateStatus(BankCardParam param);
    /**
     * 设置useId或者groupId银行卡为非默认状态
     * @param param
     */
    void updateunDefault(BankCardParam param);
    
    /**
     * 根据ID获取银行卡
     * @param id
     * @return
     */
    BankCardVO getByID(Integer id);
    
    /**
     * 获取集团或者医生的默认银行卡
     * @param param
     * @return
     */
    List<BankCardVO> getDefaultCard(BankCardParam param);
    
    void updateSettleCard(SettleParam param);

	void deleteByGroupId(String groupId);
}
