package com.dachen.health.controller.pack.account;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.service.BankBinDataService;
import com.dachen.health.pack.account.entity.param.BankCardParam;
import com.dachen.health.pack.account.service.IBankCardService;
import com.dachen.health.pack.account.service.IBankService;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtil;

@RestController
@RequestMapping("/pack/bank")
public class BankController {

    @Autowired
    private IBankCardService bankCardService;
    
    @Autowired
    private IBankService bankService;
    @Autowired
    BankBinDataService bankBinDataService;
    
    /**
     * @api {get} /pack/bank/addBankCard 添加银行卡
     * @apiVersion 1.0.0
     * @apiName addBankCard
     * @apiGroup 银行卡
     * @apiDescription 添加银行卡
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    bankNo                银行卡卡号
     * @apiParam  {String}    subBank               银行卡开户行支行
     * @apiParam  {String}    personNo              身份证号
     * @apiParam  {String}    userRealName          姓名
     * 
     * @apiSuccess {Number=1} resultCode            返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("addBankCard")
    public JSONMessage addBankCard(BankCardParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        return JSONMessage.success(null, bankCardService.add(param));
    }
    
    
    
    /**
     * @api {get} /pack/bank/deleteBankCard 删除银行卡
     * @apiVersion 1.0.0
     * @apiName deleteBankCard
     * @apiGroup 银行卡
     * @apiDescription 删除银行卡
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    id                    id
     * 
     * @apiSuccess {Number=1} resultCode            返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("deleteBankCard")
    public JSONMessage deleteBankCard(BankCardParam param) {
//        param.setUserId(ReqUtil.instance.getUserId());
        bankCardService.delete(param);
        return JSONMessage.success();
    }
    
    /**
     * @api {get} /pack/bank/getBankCards 获取银行卡列表
     * @apiVersion 1.0.0
     * @apiName getBankCards
     * @apiGroup 银行卡
     * @apiDescription 获取银行卡列表
     *
     * @apiParam  {String}    access_token            token
     * 
     * @apiSuccess  {Integer}    id                   id
     * @apiSuccess  {Integer}    bankId               银行卡开户行id
     * @apiSuccess  {String}     bankName             银行卡开户行
     * @apiSuccess  {String}     subBank              开户支行
     * @apiSuccess  {String}     personNo             身份证号
     * @apiSuccess  {String}     userRealName         姓名
     * @apiSuccess  {String}     bankNo               银行卡完整卡号，混淆显示客户端处理
     * @apiSuccess  {String}     bankIoc              银行小图标
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("getBankCards")
    public JSONMessage getBankCards(BankCardParam param) {
        return JSONMessage.success(null,bankCardService.getAll(ReqUtil.instance.getUserId()));
    }
    
    /**
     * @api {post} /pack/bank/updateBankCard 修改银行卡
     * @apiVersion 1.0.0
     * @apiName updateBankCard
     * @apiGroup 银行卡
     * @apiDescription 修改银行卡
     *
     * @apiParam  {String}    access_token            token
     * @apiParam  {Integer}   id                      id
     * @apiParam  {String}    subBank                 支行
     * 
     * @apiSuccess {Number=1} resultCode              返回状态吗
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("updateBankCard")
    public JSONMessage updateBankCard(BankCardParam param) {
        param.setUserId(ReqUtil.instance.getUserId());
        bankCardService.update(param);
        return JSONMessage.success();
    }
    
    
    /**
     * @api {get} /pack/bank/getBanks 获取银行列表
     * @apiVersion 1.0.0
     * @apiName getBanks
     * @apiGroup 银行卡
     * @apiDescription 获取银行列表
     *
     * @apiParam  {String}    access_token            token
     * 
     * @apiSuccess  {Integer}    id                   银行id
     * @apiSuccess  {String}     bankName             银行名称
     *
     * @apiAuthor  范鹏
     * @date 2015年8月17日
     */
    @RequestMapping("getBanks")
    public JSONMessage getBanks() {
        return JSONMessage.success(null,bankService.getAll());
    }
    
    /**
     * @api {get} /pack/bank/setBankStatus 设置银行卡是否为默认
     * @apiVersion 1.0.0
     * @apiName setBankStatus
     * @apiGroup 银行卡
     * @apiDescription 设置指定银行卡的状态
     *
     * @apiParam  {String}     access_token           token
     * @apiParam  {Integer}   id                     银行卡ID
     * @apiParam  {boolean}   isDefault              0否；1是
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("setBankStatus")
    public JSONMessage setBankStatus(BankCardParam param){
    	bankCardService.updateStatus(param);
    	return JSONMessage.success(null,null);
    }
    
    
    /**
     * @api {get} /pack/bank/addGroupBankCard 添加集团银行卡
     * @apiVersion 1.0.0
     * @apiName addGroupBankCard
     * @apiGroup 银行卡
     * @apiDescription 添加集团银行卡
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    groupId          		集团ID
     * @apiParam  {String}    bankNo                银行卡卡号
     * @apiParam  {String}    bankId                银行卡开户行id
     * @apiParam  {String}    subBank               银行卡开户行支行
     * @apiParam  {String}    userRealName          持卡人姓名
     * @apiParam  {String}     personNo             身份证号
     * 
     * @apiSuccess {Number=1} resultCode            返回状态吗
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("addGroupBankCard")
    public JSONMessage addGroupBankCard(BankCardParam param){
    	return JSONMessage.success(null, bankCardService.addGroupBankCard(param));
    }
    
    /**
     * @api {get} /pack/bank/getGroupBanks 获取集团所有银行卡
     * @apiVersion 1.0.0
     * @apiName getGroupBanks
     * @apiGroup 银行卡
     * @apiDescription  获取集团所有银行卡
     *
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    groupId          		集团ID
     * 
     * @apiSuccess  {Integer}    id                   银行id
     * @apiSuccess  {String}     bankName             银行名称
     * 
     * 
     * @apiAuthor  张垠
     * @date 2016年1月8日
     */
    @RequestMapping("getGroupBanks")
    public JSONMessage getAllGoupBankCard(BankCardParam param){
    	if(StringUtil.isEmpty(param.getGroupId())){
    		throw new ServiceException("集团Id不能为空");
    	}
    	return JSONMessage.success(null,bankCardService.getAllByGroupId(param.getGroupId()));
    }
    
    
    /**
     * @api {get} /pack/bank/findBankName 获取银行卡名称
     * @apiVersion 1.0.0
     * @apiName findBankName
     * @apiGroup 银行卡
     * @apiDescription  获取获取银行卡名称
     *
     * @apiParam  {String}       access_token        token
     * @apiParam  {String}    	 bankCard            银行卡号
     * 
     * @apiSuccess  {String}     bankName            银行名称
     * @apiSuccess  {String}     bankIoc             银行卡图标
     * @apiSuccess  {String}     bankCode            开户行代码
     * @apiSuccess  {String}     bankNoType          银行卡类型，显示类型名称：借记卡
     * @apiAuthor  谢佩
     * @date 2016年1月26日
     */
    @RequestMapping("findBankName")
    public JSONMessage getFindBankName(BankCardParam param){
    	return JSONMessage.success(null,bankBinDataService.findBankName(param.getBankCard()));
    }
    
    
    @RequestMapping("saveBankBinData")
    public JSONMessage saveBankBinDataService(){
    	try {
			bankBinDataService.save();
		} catch (InvalidFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return JSONMessage.success();
    }
    
    
    /**
     * @api  {get} /pack/bank/searchNameByKeyword 获取银行卡名称
     * @apiVersion 1.0.0
     * @apiName bankNameList
     * @apiGroup 银行卡
     * @apiDescription 根据银行卡关键字分页获取银行卡名称列表
     * 
     * @apiParam  {String}    access_token          token
     * @apiParam  {String}    bankName         	            关键字
     * @apiParam  {Integer}   pageIndex         	页码
     * @apiParam  {Integer}   pageSize        		页面大小
     * 
     * @apiSuccess {List}       data            	列表
     * 
     * @apiAuthor  张垠
     * @date 2016年4月15日
     */
    @RequestMapping("searchNameByKeyword")
    public JSONMessage getBankNameList(BankCardParam param){
    	return JSONMessage.success(null, bankService.getBankNameByKeyword(param));
    	
    }
    
    
}
