package com.dachen.health.checkbill.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dachen.commons.page.PageVO;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBill;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItem;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItemRet;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.entity.po.CheckupItem;
import com.dachen.health.checkbill.entity.vo.CheckBillPageVo;
import com.dachen.health.checkbill.entity.vo.CheckItemCount;
import com.dachen.health.checkbill.entity.vo.CheckItemSearchParam;
import com.dachen.health.checkbill.entity.vo.CheckbillSearchParam;

public interface CheckBillService {

	CheckBill addCheckBill(CheckBill checkBill);

	int updateCheckItem(CheckItem checkItem);

	CheckItem getCheckItemById(String checkItemId);

	CheckItem addCheckItem(CheckItem checkItem);
	List<CCareItemCheckBillItemRet> addCareItemCheckBillAndItemBatch(List<CCareItemCheckBill> careItemCheckBillList, 
			List<CCareItemCheckBillItem> CCareItemCheckBillItemList);
	
	List<String> addCheckupItemBatch(List<CheckupItem> list);

	int batchAddOrUpdateCheckItem(String checkItemString);

	PageVO getCheckItemListByFromId(CheckItemSearchParam scp);

	boolean saveCheckBill(Integer id, String attention);

	PageVO getCheckBillPageVo(CheckbillSearchParam csp);

	CheckBill getCheckBillDetail(String checkbillId);

	int updateCheckbill(CheckBill checkBill);

	List<CheckItemCount> getCheckItemCount(Integer patientId);

	List<CheckItem> getCheckItemByClassify(Integer patientId , String checkUpId);

	List<CheckBillPageVo> getCheckBillList(Set<Integer> orderIds, Integer patientId);

	CheckBill getCheckBillByOrderId(Integer orderId);

	void removeCheckItemByCheckBillId(String checkbillId);
	
	/**
	 * 根据CheckBillId获取CheckBill
	 * @param checkItemId
	 */
	Map<String, Object> getCheckItem(String checkItemId);

	Map<String, Object> isCheckItemFinish(String checkItemId);
}
