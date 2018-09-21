package com.dachen.health.commons.service;

import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import com.dachen.health.commons.entity.BankBinNoData;

/**
 * 全国银行名称数据字典
 * @author Administrator
 *
 */
public interface BankBinDataService {
	
	/**
	 * 根据银行卡号获取银行名称
	 * @param bankNo
	 * @return
	 */
	public BankBinNoData findBankName(String bankCard);
	
	
	public void save() throws InvalidFormatException, IOException;

}
