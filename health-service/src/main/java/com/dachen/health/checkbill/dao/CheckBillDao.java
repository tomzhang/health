package com.dachen.health.checkbill.dao;

import java.util.List;
import java.util.Set;

import com.dachen.commons.page.PageVO;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.entity.po.CheckupItem;
import com.dachen.health.checkbill.entity.vo.CheckItemCount;
import com.dachen.health.checkbill.entity.vo.CheckItemSearchParam;
import com.dachen.health.checkbill.entity.vo.CheckbillSearchParam;

public interface CheckBillDao {

	CheckBill insertCheckBill(CheckBill checkBill);

	CheckItem updateCheckItem(CheckItem checkItem);

	CheckItem getCheckItemById(String checkItemId);

	CheckItem addCheckItem(CheckItem checkItem);
	
	List<String> addCheckupItemBatch(List<CheckupItem> list);

	int batchAddCheckItem(List<CheckItem> items);

	PageVO getCheckItemListByFromId(CheckItemSearchParam scp);

	PageVO getCheckBillPageVo(CheckbillSearchParam csp);

	CheckBill getCheckBillDetail(String checkbillId);

	CheckBill updateCheckbill(CheckBill checkBill);

	List<String> getCheckbillIds(int userId);

	List<CheckItemCount> getCheckItemCount(List<String> checkBillIds);

	List<CheckItem> getCheckItemByClassify(List<String> checkBillIds, String checkUpId);
	
	boolean existsCheckItem(String checkUpId, String fromId);

	void updateCheckItemByFromIdAndCheckUpId(CheckItem checkItem);
	
	/**
	 * @param fromId
	 * @param checkupId
	 * @return
	 */
	@Deprecated
	CheckItem getCheckItemByFromIdAndCheckupId(String fromId, String checkupId);
	
	List<CheckItem> getCheckItemsByFromId(String fromId);
	List<CheckItem> getCheckItemsByFromIds(List<String> fromIds);

	List<CheckBill> getCheckBillList(Set<Integer> orderIds, Integer patientId);

	List<CheckItem> getItemList(String checkBill);

	CheckBill getCheckBillByOrderId(Integer orderId);

	void removeCheckItemByCheckBillId(String checkbillId);

	List<CheckItem> findByIds(List<String> ids);
	
	CheckBill getCheckBillById(String checkBillId);

	boolean updateCareItemId(String checkBillId, String careItemId);

}
