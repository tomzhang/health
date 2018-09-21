package com.dachen.health.pack.account.service.impl;

import java.text.ParseException;
import java.util.List;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.entity.vo.BaseUserVO;
import com.dachen.health.base.service.IBaseUserService;
import com.dachen.health.commons.entity.BankBinNoData;
import com.dachen.health.commons.service.BankBinDataService;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.entity.po.BankCard;
import com.dachen.health.pack.account.entity.vo.BankCardVO;
import com.dachen.health.pack.account.mapper.BankCardMapper;
import com.dachen.health.pack.account.service.IBankCardService;
import com.dachen.health.pack.account.service.IBankService;
import com.dachen.health.pack.account.util.IDCardUtil;
import com.dachen.health.pack.income.constant.IncomeEnum;
import com.dachen.health.pack.income.entity.param.SettleParam;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.dachen.util.StringUtils;

@Service
public class BankCardServiceImpl implements IBankCardService {
	

    @Autowired
    private BankCardMapper bankCardMapper;
    
    @Autowired
    private IBankService bankService;

    @Autowired
    private IBaseUserService baseUserService;
    
    @Autowired
    private IGroupService groupService;
    
    @Autowired
    private BankBinDataService bankBinDataService;

    /**
     * </p>添加银行卡</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月17日
     */
    public BankCard add(BankCardParam param) {
        if (StringUtil.isEmpty(param.getBankName())) {
            throw new ServiceException("请选择银行");
        }
        if (!this.checkBankCard(param.getBankNo())) {
            throw new ServiceException("请填写正确银行卡号");
        }
//        if (StringUtil.isBlank(param.getSubBank())) {
//            throw new ServiceException("请填写支行名称");
//        }
        List list = getAll(param.getUserId());
        if(list != null && list.size() >0){
        	throw new ServiceException("你已添加过银行卡，请勿重复添加");
        }
        // 判断医生是否认证通过
        BaseUserVO userVO = baseUserService.getUser(param.getUserId());
        if (userVO == null) {
            throw new ServiceException("用户不存在");
        }
//        if (userVO.getStatus() != UserEnum.UserStatus.normal.getIndex()) {
//            throw new ServiceException("认证未通过");
//        }
//        if (StringUtil.isBlank(userVO.getName())) {
//            throw new ServiceException("用户无姓名");
//        }

        //判断银行是否存在
//        BankVO bankVO = bankService.getOne(param.getBankId());
//        if(bankVO == null){
//            throw new ServiceException("请选择正确的银行");
//        }
        
        // 判断是否重复添加
//        List<BankCardVO> vo = bankCardMapper.getByNo(param.getBankNo().trim());
//        if (vo.size() > 0) {
//            throw new ServiceException("该银行卡已被添加");
//        }
//        if(param.getIsDefault()){
//        	updateunDefault(param);
//        }
        BankCard po = new BankCard();
        po.setUserId(param.getUserId());
        po.setUserRealName(userVO.getName());
        po.setBankId(param.getBankId());
        po.setBankName(param.getBankName());
        po.setBankNo(param.getBankNo().trim());
        po.setPersonNo(param.getPersonNo());
        po.setSubBank(param.getSubBank());
        po.setIsDelete(IncomeEnum.DeleteStatus.未删除.getIndex());
        po.setIsDefault(false);

        bankCardMapper.add(po);
        updateSettleBankCard(po);
        return po;
    }

    /**
     * </p>删除银行卡</p>
     * 1：删除 2：未删除
     * @param param
     * @author fanp 
     * @date 2015年8月17日
     */
    public void delete(BankCardParam param) {
    	if(param.getId() == null){
    		throw new ServiceException("Id不能为空");
    	}
    	BankCardVO vo = bankCardMapper.getByID(param.getId());
    	if(vo == null || vo.getIsDelete() == IncomeEnum.DeleteStatus.删除.getIndex()){
    		throw new ServiceException("没有找到对应的银行卡");
    	}
        param.setIsDelete(IncomeEnum.DeleteStatus.删除.getIndex());
        bankCardMapper.delete(param);
    }

    /**
     * </p>获取银行卡列表</p>
     * 
     * @param userId
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    public List<BankCardVO> getAll(Integer userId) {
        // 混淆银行卡号，显示末尾4wei
        List<BankCardVO> list = bankCardMapper.getAll(userId);
        
        StringBuffer sb=new StringBuffer();
		sb.append(PropertiesUtil.getContextProperty("fileserver.protocol")).append("://");
		sb.append(PropertiesUtil.getContextProperty("fileserver.host")).append(":").append(PropertiesUtil.getContextProperty("fileserver.port"));
		
        for (BankCardVO vo : list) {
            //vo.setBankNo(StringUtil.substring(vo.getBankNo(), -4));
            try {
            	BankBinNoData bankBinNoData = bankBinDataService.findBankName(vo.getBankNo());
            	if(bankBinNoData.getBankName().equals(vo.getBankName()))
            	{
                    vo.setBankIoc(bankBinNoData.getBankIoc());
            	}
            	else
            	{
            		vo.setBankIoc(sb.toString()+"/default/bank/defulat_bank.png");	
            	}
            }catch(Exception e) {
            	e.printStackTrace();
            	vo.setBankIoc(sb.toString()+"/default/bank/defulat_bank.png");
            }
        	
        }
        return list;
    }
    
    /**
     * </p>修改银行卡</p>
     * 
     * @param param
     * @author fanp
     * @date 2015年8月17日
     */
    public void update(BankCardParam param){
        if(param.getId()==null){
            throw new ServiceException("请选择需修改的银行卡");
        }
//        if(StringUtil.isBlank(param.getSubBank())){
//            throw new ServiceException("请填写支行");
//        }
        param.setSubBank(param.getSubBank().trim());
        bankCardMapper.update(param);
    }

    /**
     * </p>验证银行卡卡号</p>
     * 
     * @param bankNo
     * @return
     * @author fanp
     * @date 2015年8月17日
     */
    private boolean checkBankCard(String bankNo) {
        boolean flag = false;
        if (StringUtil.isNotBlank(bankNo)) {
            String str = bankNo.replaceAll("\\s", "");

            String regex1 = "^[\\d]{19}$";
            String regex2 = "^[\\d]{16}$";

            if (Pattern.matches(regex1, str) || Pattern.matches(regex2, str) ) {
                flag = true;
            } 
        }
//            else {
//                int s1 = 0, s2 = 0;
//                String reverse = new StringBuffer(str).reverse().toString();
//
//                for (int i = 0; i < reverse.length(); i++) {
//                    int digit = Character.digit(reverse.charAt(i), 10);
//                    if (i % 2 == 0) {
//                        s1 += digit;
//                    } else {
//                        s2 += 2 * digit;
//                        if (digit >= 5) {
//                            s2 -= 9;
//                        }
//                    }
//                }
//
//                flag = (s1 + s2) % 10 == 0;
//            }
//
//        }

        return flag;
    }

	@Override
	public void updateStatus(BankCardParam param) {
		if(param.getId() == null || param.getIsDefault() ==null){
			throw new ServiceException("状态或ID不能为空");
		}
		BankCardVO vo = bankCardMapper.getByID(param.getId());
		if(vo == null){
			return ;
		}
		param.setUserId(vo.getUserId());
		bankCardMapper.updateunDefault(param);
		bankCardMapper.updateStatus(param);
	}

	@Override
	public void updateunDefault(BankCardParam param) {
		if(param.getUserId() == null && StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("userId或者groupId不能为空");
		}
		bankCardMapper.updateunDefault(param);
	}

	@Override
	public List<BankCardVO> getAllByGroupId(String groupId) {
		if(StringUtil.isEmpty(groupId)){
			throw new ServiceException("groupId 不能为空");
		}
		return bankCardMapper.getAllByGroupId(groupId);
	}

	@Override
	public BankCard addGroupBankCard(BankCardParam param) {

		if(StringUtil.isBlank(param.getGroupId())){
			throw new ServiceException("集团id为空");
		}
	     
	     if(StringUtil.isBlank(param.getBankNo())){
				throw new ServiceException("银行卡卡号为空");
			}
		     
	     if(StringUtil.isBlank(param.getSubBank())){
				throw new ServiceException("银行卡开户行支行为空");
			}
		     
	     if(StringUtil.isBlank(param.getUserRealName())){
				throw new ServiceException("持卡人姓名为空");
			}
		     
	     if(StringUtil.isBlank(param.getPersonNo())){
				throw new ServiceException("身份证号为空");
			}
	     String result="信息有误";
		try {
			result = IDCardUtil.IDCardValidate(param.getPersonNo());
		} catch (ParseException e) {
			e.printStackTrace();
			result="转换异常";
		}
	    if(StringUtil.isNotEmpty(result)){
	    	throw new ServiceException(result);
	    }
		
        // 判断集团是否认证通过
        Group group = groupService.getGroupById(param.getGroupId());
        if (group == null) {
            throw new ServiceException("集团不存在");
        }
        if (StringUtil.isBlank(group.getName())) {
            throw new ServiceException("集团无名称");
        }

        //判断银行是否存在
//        BankVO bankVO = bankService.getOne(param.getBankId());
//        if(bankVO == null){
//            throw new ServiceException("请选择正确的银行");
//        }
        
        // 判断是否重复添加
//        List<BankCardVO> vo = bankCardMapper.getByNo(param.getBankNo().trim());
//        if (vo.size() > 0) {
//            throw new ServiceException("该银行卡已被添加");
//        }
        
//        if(param.getIsDefault()){
//        	param.setUserId(null);
//        	updateunDefault(param);
//        }
        
        bankCardMapper.deleteByGroupId(param.getGroupId());
        
        /*List<BankCardVO> list = getAllByGroupId(param.getGroupId());
        if(list != null && list.size() >0){
        	//update t_bank_card set is_delete = #{isDelete} where id=#{id} 
        	for (BankCardVO bankCardVO : list) {
        		BankCardParam  bcp = new BankCardParam();
            	bcp.setId(bankCardVO.getId());
            	bcp.setIsDelete(IncomeEnum.DeleteStatus.删除.getIndex());
            	bankCardMapper.delete(param);
			}
        	//throw new ServiceException("你已添加过银行卡，请勿重复添加");
        }*/
        
        BankCard po = new BankCard();
        po.setUserId(0);
        po.setGroupId(param.getGroupId());
        po.setUserRealName(param.getUserRealName());
        po.setBankId(param.getBankId());
        po.setBankName(param.getBankName());
        po.setBankNo(param.getBankNo().trim());
        po.setPersonNo(param.getPersonNo());
        po.setSubBank(param.getSubBank());
        po.setIsDelete(IncomeEnum.DeleteStatus.未删除.getIndex());
        po.setIsDefault(false);
        bankCardMapper.add(po);
        //updateSettleBankCard(po);
        return po;
	}

	@Override
	public BankCardVO getDocDefaultCard(BankCardParam param) {
		if(param.getUserId() == null){
			throw new ServiceException("用户Id不能为空");
		}
		param.setGroupId(null);
		List<BankCardVO> list= bankCardMapper.getDefaultCard(param);
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

	@Override
	public BankCardVO getGroupDefaultCard(BankCardParam param) {
		if(StringUtil.isEmpty(param.getGroupId())){
			throw new ServiceException("集团Id不能为空");
		}
		param.setUserId(null);
		List<BankCardVO> list= bankCardMapper.getDefaultCard(param);
		if(list.isEmpty()){
			return null;
		}
		return list.get(0);
	}

	@Override
	public BankCardVO getBankCardById(Integer id) {
		if(id == null){
			return null;
		}
		return bankCardMapper.getByID(id);
	}
	/**
	 * 更新结算表里用户绑定银行卡为0的银行卡信息
	 * 
	 */
    private void updateSettleBankCard(BankCard bc){
    	if(bc == null || bc.getId() == null || bc.getId() == 0){
    		return ;
    	}
    	SettleParam param = new SettleParam();
    	param.setUserBankId(bc.getId());
    	if(StringUtils.isEmpty(bc.getGroupId()) && bc.getUserId() != null){
    		param.setUserId(bc.getUserId()+"");
    	}else if(bc.getUserId() == 0 && !StringUtils.isEmpty(bc.getGroupId())) {
    		param.setUserId(bc.getGroupId());
    	}
    	bankCardMapper.updateSettleCard(param);
    }
    public static void main(String[] args) {
		try {
			IDCardUtil.IDCardValidate("44023198703142001");
		} catch (ParseException e) {
			throw new ServiceException(e.toString());
		}
 		System.err.println("00");
	}
}
