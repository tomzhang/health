package com.dachen.health.api.client.checkbill.proxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.dachen.health.api.HealthApiClientProxy;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBill;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItem;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItemRet;
import com.dachen.health.api.client.checkbill.entity.CCheckBill;
import com.dachen.health.api.client.checkbill.entity.CCheckBillItem;
import com.dachen.sdk.exception.HttpApiException;

@Component
public class CheckBillApiClientProxy extends HealthApiClientProxy {

	/**
	 * 添加检查单
	 * @param checkBill 检查单实体
	 * @return 新创建的检查单实体，含id值
	 * @throws HttpApiException 
	 * @deprecated 不推荐使用
	 */
	@Deprecated
	public CCheckBill addCheckBill(CCheckBill checkBill) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		this.putJsonStrIfNotBlank(params, "checkBillJson", checkBill);
		try {
			String url = "checkBill/addCheckBill";
			CCheckBill c = this.postRequest(url, params, CCheckBill.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 添加检查项
	 * @param checkItem 检查项实体
	 * @return 新创建的检查项实体，含id值
	 * @throws HttpApiException 
	 * @deprecated 不推荐使用
	 */
	@Deprecated
	public CCheckBillItem addCheckItem(CCheckBillItem checkItem) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		this.putJsonStrIfNotBlank(params, "checkItemJson", checkItem);
		try {
			String url = "checkBill/addCheckItem";
			CCheckBillItem c = this.postRequest(url, params, CCheckBillItem.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 添加关怀项的检查单
	 * @param careItemCheckBills 检查单列表
	 * @param careItemCheckBillItems 检查单检查项列表
	 * @return 
	 * @throws HttpApiException 
	 */
	public List<CCareItemCheckBillItemRet> addCareItemCheckBillAndItemBatch(List<CCareItemCheckBill> careItemCheckBills,
			List<CCareItemCheckBillItem> careItemCheckBillItems) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		this.putJsonStrIfNotBlank(params, "careItemCheckBillsJson", careItemCheckBills);
		this.putJsonStrIfNotBlank(params, "careItemCheckBillItemsJson", careItemCheckBillItems);
		try {
			String url = "checkBill/addCareItemCheckBillAndItemBatch";
			List<CCareItemCheckBillItemRet> c = this.postRequestList(url, params, CCareItemCheckBillItemRet.class);
			return c;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 获取检查项
	 * @param fromId
	 * @param checkupId
	 * @return CCheckBillItem
	 * @throws HttpApiException 
	 * @see #getCheckItemListByFromIdAndCheckupIdList
	 */
	@Deprecated
	public CCheckBillItem getCheckItemByFromIdAndCheckupId(String fromId, String checkupId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(2);
		params.put("fromId", fromId.toString());
		params.put("checkupId", checkupId.toString());
		try {
			String url = "checkBill/getCheckItemByFromIdAndCheckupId";
			CCheckBillItem citem = this.openRequest(url, params, CCheckBillItem.class);
			return citem;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据checkBillId获取检查项
	 * @param checkBillId
	 * @return
	 * @throws HttpApiException
	 */
	public List<CCheckBillItem> getCheckItemsByCheckBillId(String checkBillId) throws HttpApiException {
		Map<String, String> params = new HashMap<String, String>(1);
		params.put("checkBillId", checkBillId.toString());
		try {
			String url = "checkBill/getCheckItemsByCheckBillId";
			List<CCheckBillItem> ret = this.openRequestList(url, params, CCheckBillItem.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
	
	/**
	 * 根据checkBillId获取检查项
	 * @param checkBillId
	 * @return
	 * @throws HttpApiException
	 */
	public List<CCheckBillItem> getCheckItemsByCheckBillIds(List<String> checkBillIds) throws HttpApiException {
		if (null == checkBillIds || 0 == checkBillIds.size()) {
			return null;
		}
		Map<String, String> params = new HashMap<String, String>(1);
		this.putArrayIfNotBlank(params, "checkBillIds", checkBillIds);
		try {
			String url = "checkBill/getCheckItemsByCheckBillIds";
			List<CCheckBillItem> ret = this.openRequestList(url, params, CCheckBillItem.class);
			return ret;
		} catch (HttpApiException e) {
			throw new HttpApiException(e.getMessage(), e);
		}
	}
}
