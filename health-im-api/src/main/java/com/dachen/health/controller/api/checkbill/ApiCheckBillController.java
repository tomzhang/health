package com.dachen.health.controller.api.checkbill;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBill;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItem;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItemRet;
import com.dachen.health.checkbill.dao.CheckBillDao;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.util.JSONUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/checkBill")
public class ApiCheckBillController extends ApiBaseController {

	@Resource
	protected CheckBillService checkBillService;

	@Resource
	protected CheckBillDao checkBillDao;

	@Deprecated
	@RequestMapping(value = "addCheckBill", method = RequestMethod.POST)
	public JSONMessage addCheckBill(@RequestParam(required = true) String checkBillJson) {
		String tag = "addCheckBill";
		logger.info("{}. checkBillJson={}", tag, checkBillJson);
		
		CheckBill checkBill = JSONUtil.parseObject(CheckBill.class, checkBillJson);

		checkBill = checkBillService.addCheckBill(checkBill);
		return JSONMessage.success(null, checkBill);
	}
	
	@Deprecated
	@RequestMapping(value = "addCheckItem", method = RequestMethod.POST)
	public JSONMessage addCheckItem(@RequestParam(required = true) String checkItemJson) {
		String tag = "addCheckItem";
		logger.info("{}. checkItemJson={}", tag, checkItemJson);
		
		CheckItem checkItem = JSONUtil.parseObject(CheckItem.class, checkItemJson);

		checkItem = checkBillService.addCheckItem(checkItem);
		return JSONMessage.success(null, checkItem);
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "addCareItemCheckBillAndItemBatch", method = RequestMethod.POST)
	public JSONMessage addCareItemCheckBillAndItemBatch(@RequestParam(required = true) String careItemCheckBillsJson, 
			@RequestParam(required = true) String careItemCheckBillItemsJson) {
		String tag = "addCareItemCheckBillAndItemBatch";
		logger.info("{}. careItemCheckBillsJson={}, careItemCheckBillItemsJson={}", tag, careItemCheckBillsJson, careItemCheckBillItemsJson);
		
		List<Object> list = JSONUtil.parseObject(List.class, careItemCheckBillsJson);
		Integer count = list.size();
		List<CCareItemCheckBill> careItemCheckBillList  = new ArrayList<CCareItemCheckBill>(count);
		for (Object o:list) {
			CCareItemCheckBill careItemCheckBill = JSONUtil.parseObject(CCareItemCheckBill.class, o.toString());
			careItemCheckBillList.add(careItemCheckBill);
		}
		
		list = JSONUtil.parseObject(List.class, careItemCheckBillItemsJson);
		count = list.size();
		List<CCareItemCheckBillItem> careItemCheckBillItemList  = new ArrayList<CCareItemCheckBillItem>(count);
		for (Object o:list) {
			CCareItemCheckBillItem careItemCheckBillItem = JSONUtil.parseObject(CCareItemCheckBillItem.class, o.toString());
			careItemCheckBillItemList.add(careItemCheckBillItem);
		}
		
		List<CCareItemCheckBillItemRet> ret = checkBillService.addCareItemCheckBillAndItemBatch(careItemCheckBillList, careItemCheckBillItemList);
		if (CollectionUtils.isEmpty(ret)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, ret);
	}

	/**
	 * @see #getCheckItemsByCheckBillId(String)
	 * @param fromId
	 * @param checkupId
	 * @return
	 */
	@Deprecated
	@RequestMapping(value = "getCheckItemByFromIdAndCheckupId", method = RequestMethod.GET)
	public JSONMessage getCheckItemByFromIdAndCheckupId(String fromId, String checkupId) {
		String tag = "getCheckItemByFromIdAndCheckupId";
		logger.info("{}. fromId={}, checkupId={}", tag, fromId, checkupId);

		CheckItem item = checkBillDao.getCheckItemByFromIdAndCheckupId(fromId, checkupId);
		if (item.getImageList() != null && !item.getImageList().isEmpty()) { // 处理一下图片路径
			item.setImageList(addUrlPrefix(item.getImageList()));
		}
		return JSONMessage.success(null, item);
	}
	
	private void wrapImageList(List<CheckItem> list) {
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		for (CheckItem checkItem:list) {
			if (checkItem.getImageList() != null && !checkItem.getImageList().isEmpty()) { // 处理一下图片路径
				checkItem.setImageList(addUrlPrefix(checkItem.getImageList()));
			}
		}
	}

	private List<String> addUrlPrefix(List<String> images) {
		List<String> list = new ArrayList<String>();
		for (String image : images) {
			list.add(PropertiesUtil.addUrlPrefix(image));
		}
		return list;
	}

	@RequestMapping(value = "getCheckItemsByCheckBillId", method = RequestMethod.GET)
	public JSONMessage getCheckItemsByCheckBillId(@RequestParam String checkBillId) {
		String tag = "getCheckItemsByCheckBillId";
		logger.info("{}. checkBillId={}", tag, checkBillId);

		List<CheckItem> itemList = checkBillDao.getCheckItemsByFromId(checkBillId);
		if (CollectionUtils.isEmpty(itemList)) {
			return JSONMessage.success();
		}
		wrapImageList(itemList);
		return JSONMessage.success(null, itemList);
	}
	
	@RequestMapping(value = "getCheckItemsByCheckBillIds", method = RequestMethod.GET)
	public JSONMessage getCheckItemsByCheckBillIds(@RequestParam String[] checkBillIds) {
		String tag = "getCheckItemsByCheckBillId";
		logger.info("{}. checkBillIds={}", tag, checkBillIds);

		List<CheckItem> itemList = checkBillDao.getCheckItemsByFromIds(Arrays.asList(checkBillIds));
		if (CollectionUtils.isEmpty(itemList)) {
			return JSONMessage.success();
		}
		wrapImageList(itemList);
		return JSONMessage.success(null, itemList);
	}


}
