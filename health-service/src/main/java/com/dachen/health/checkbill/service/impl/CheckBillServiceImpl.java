package com.dachen.health.checkbill.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.page.PageVO;
import com.dachen.commons.user.UserSessionService;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBill;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItem;
import com.dachen.health.api.client.checkbill.entity.CCareItemCheckBillItemRet;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.health.base.entity.po.CheckSuggestItem;
import com.dachen.health.base.entity.vo.CheckSuggestItemVo;
import com.dachen.health.base.service.IBaseDataService;
import com.dachen.health.checkbill.dao.CheckBillDao;
import com.dachen.health.checkbill.entity.po.CheckBill;
import com.dachen.health.checkbill.entity.po.CheckItem;
import com.dachen.health.checkbill.entity.po.CheckupItem;
import com.dachen.health.checkbill.entity.vo.CheckBillPageVo;
import com.dachen.health.checkbill.entity.vo.CheckItemCount;
import com.dachen.health.checkbill.entity.vo.CheckItemSearchParam;
import com.dachen.health.checkbill.entity.vo.CheckbillSearchParam;
import com.dachen.health.checkbill.service.CheckBillService;
import com.dachen.util.BeanUtil;
import com.dachen.util.PropertiesUtil;
import com.dachen.util.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Service
public class CheckBillServiceImpl implements CheckBillService {

	@Autowired
	private CheckBillDao checkBillDao;
	
	@Autowired
	IBaseDataService baseDataServiceImpl;
	
	@Autowired
	UserSessionService userSessionService;
	
	@Autowired
	private IBaseDataDao baseDataDao;

	@Override
	public CheckBill addCheckBill(CheckBill checkBill) {
		return checkBillDao.insertCheckBill(checkBill);
	}
	
	public List<CheckBill> addCheckBillList(List<CheckBill> checkBillList) {
		if (CollectionUtils.isEmpty(checkBillList)) {
			return null;
		}
		List<CheckBill> ret = new ArrayList<CheckBill>(checkBillList.size());
		for (CheckBill checkBill:checkBillList) {
			CheckBill newItem = this.addCheckBill(checkBill);
			ret.add(newItem);
		}
		return ret;
	}

	@Override
	public int updateCheckItem(CheckItem checkItem) {
		if(StringUtil.isNullOrEmpty(checkItem.getId())){
			throw new ServiceException("检查项参数Id为空");
		}
		CheckBill cb = getCheckBillDetail(checkItem.getFromId());
		if(cb == null){
			throw new ServiceException("找不到对应的检查单数据");
		}
		CheckItem rtnCi = checkBillDao.updateCheckItem(checkItem);
		return rtnCi != null ? 1 : 0;
	}
	
	/**
	 * 
	 */
	@Override
	public int updateCheckbill(CheckBill checkBill) {
		if(StringUtil.isNullOrEmpty(checkBill.getId())){
			throw new ServiceException("检查项参数Id为空");
		}
		CheckBill rtncb = checkBillDao.updateCheckbill(checkBill);
		return rtncb != null ? 1 : 0;
	}

	@Override
	public CheckItem getCheckItemById(String checkItemId) {
		if(StringUtil.isNullOrEmpty(checkItemId)){
			throw new ServiceException("参数为空");
		}
		CheckItem checkItem = checkBillDao.getCheckItemById(checkItemId);
		if(checkItem == null){
			throw new ServiceException("当前Id={"+checkItemId+"}查询不到结果");
		}
		List<String> imageUrl = checkItem.getImageList();
		List<String> images = new ArrayList<String>();
		if(imageUrl != null && imageUrl.size() > 0){
			for (String imagePath : imageUrl) {
				imagePath = PropertiesUtil.addUrlPrefix(imagePath);
				images.add(imagePath);
			}
		}
		checkItem.setImageList(images);
		return checkItem;
	}
	
	@Override
	public CheckItem addCheckItem(CheckItem checkItem) {
		List<String> imageUrl = checkItem.getImageList();
		if(imageUrl != null && imageUrl.size() > 0){
			List<String> images = new ArrayList<String>();
			for (String imagePath : imageUrl) {
				imagePath = PropertiesUtil.removeUrlPrefix(imagePath);
				images.add(imagePath);
			}
			checkItem.setImageList(images);
		}
		if(checkItem.getCreateTime() == null)
			checkItem.setCreateTime(System.currentTimeMillis());
		return checkBillDao.addCheckItem(checkItem);
	}
	
	@Override
	public List<CCareItemCheckBillItemRet> addCareItemCheckBillAndItemBatch(List<CCareItemCheckBill> careItemCheckBillList, 
			List<CCareItemCheckBillItem> careItemCheckBillItemList) {
		if (CollectionUtils.isEmpty(careItemCheckBillList) ||
				CollectionUtils.isEmpty(careItemCheckBillItemList)) {
			throw new ServiceException("列表数据为空");
		}
		
		List<CheckBill> checkBillList = this.constructCheckBill(careItemCheckBillList);
		checkBillList = this.addCheckBillList(checkBillList);	// step1：先批量创建出checkBill出来
		
		Integer count = careItemCheckBillItemList.size();
		List<CheckItem> checkItemList  = new ArrayList<CheckItem>(count);
		for (CCareItemCheckBillItem careItemCheckBillItem:careItemCheckBillItemList) {
			checkItemList.add(constructCheckBillItem(checkBillList, careItemCheckBillItem));
		}
		checkItemList = this.addCheckItemList(checkItemList);	// step2：再批量创建出checkItem出来
		
		// step3：组装结果
		List<CCareItemCheckBillItemRet> ret = constructRet(checkBillList, checkItemList);
		
		return ret;
	}
	
	private List<CCareItemCheckBillItemRet> constructRet(List<CheckBill> checkBillList, List<CheckItem> checkItemList) {
		List<CCareItemCheckBillItemRet> ret = new ArrayList<CCareItemCheckBillItemRet>(checkItemList.size());
		for (CheckItem checkItem:checkItemList) {
			CheckBill checkBill = selectById(checkBillList, checkItem.getFromId());
			
			CCareItemCheckBillItemRet retItem = new CCareItemCheckBillItemRet();
			retItem.setCheckBillId(checkItem.getFromId());
			retItem.setCheckItemId(checkItem.getId());
			retItem.setCheckUpId(checkItem.getCheckUpId());
			retItem.setCareItemId(checkBill.getCareItemId());
			ret.add(retItem);
		}
		return ret;
	}

	public List<CheckItem> addCheckItemList(List<CheckItem> checkItemList) {
		if (CollectionUtils.isEmpty(checkItemList)) {
			return null;
		}
		
		List<CheckItem> ret = new ArrayList<CheckItem>();
		for (CheckItem checkItem:checkItemList) {
			checkItem = this.addCheckItem(checkItem);
			ret.add(checkItem);
		}
		return ret;
	}
	
	private List<CheckBill> constructCheckBill(List<CCareItemCheckBill> careItemCheckBillList) {
		if (CollectionUtils.isEmpty(careItemCheckBillList)) {
			return null;
		}
		List<CheckBill> checkBillList = new ArrayList<CheckBill>(careItemCheckBillList.size());
		for (CCareItemCheckBill careItemCheckBill:careItemCheckBillList) {
			checkBillList.add(constructCheckBill(careItemCheckBill));
		}
		return checkBillList;
	}
	private CheckBill constructCheckBill(CCareItemCheckBill careItemCheckBill) {
        CheckBill checkBill = new CheckBill();
        checkBill.setOrderId(careItemCheckBill.getOrderId());
        checkBill.setPatientId(careItemCheckBill.getPatientId());
        checkBill.setCheckBillStatus(2); // 1：未下订单，2：已经下订单，3：已接单，4:已上传结果
        checkBill.setCreateTime(System.currentTimeMillis());
        checkBill.setSuggestCheckTime(careItemCheckBill.getSuggestCheckTime());
        checkBill.setAttention(careItemCheckBill.getAttention());
        checkBill.setCareItemId(careItemCheckBill.getCareItemId());
        return checkBill;
    }
	
	private CheckItem constructCheckBillItem(List<CheckBill> checkBillList, CCareItemCheckBillItem careItemCheckBillItem) {
		CheckBill checkBill = selectByCareItemId(checkBillList, careItemCheckBillItem.getCareItemId());
		return this.constructCheckBillItem(checkBill, careItemCheckBillItem);
	}
	
	private CheckBill selectByCareItemId(List<CheckBill> checkBillList, String careItemId) {
		for (CheckBill checkBill:checkBillList) {
			if (checkBill.getCareItemId().equals(careItemId)) {
				return checkBill;
			}
		}
		return null;
	}
	
	private CheckBill selectById(List<CheckBill> checkBillList, String checkBillId) {
		for (CheckBill checkBill:checkBillList) {
			if (checkBill.getId().equals(checkBillId)) {
				return checkBill;
			}
		}
		return null;
	}

	private CheckItem constructCheckBillItem(CheckBill checkBill, CCareItemCheckBillItem careItemCheckBillItem) {
		CheckItem checkItem = new CheckItem();
        checkItem.setCheckUpId(careItemCheckBillItem.getCheckUpId());
        checkItem.setItemName(careItemCheckBillItem.getCheckUpName());
        checkItem.setFrom(1);
        checkItem.setFromId(checkBill.getId());
        checkItem.setCreateTime(System.currentTimeMillis());
        checkItem.setUpdateTime(System.currentTimeMillis());
        checkItem.setIndicatorIds(careItemCheckBillItem.getConcernedItemIds());
        return checkItem;
    }
	
	@Override
	public List<String> addCheckupItemBatch(List<CheckupItem> list) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		
		return checkBillDao.addCheckupItemBatch(list);
	}

	@Override
	public int batchAddOrUpdateCheckItem(String checkItemString) {
		List<CheckItem> items = null;
		try{
			items = JSONArray.parseArray(checkItemString, CheckItem.class);
		}catch(Exception e){
			throw new ServiceException("检查项列表序列化参数错误");
		}
		long time = System.currentTimeMillis();
		if(items != null && items.size() > 0){
			for (CheckItem checkItem : items) {
				boolean exists = checkBillDao.existsCheckItem(checkItem.getCheckUpId(),checkItem.getFromId());
				setImageUrlToPath(checkItem);
				if(exists){
					checkItem.setUpdateTime(time);
					checkBillDao.updateCheckItemByFromIdAndCheckUpId(checkItem);
				}else{
					checkItem.setFrom(2);
					checkItem.setCreateTime(time);
					addCheckItem(checkItem);
				}
			}
			return 1; 
		}else{
			throw new ServiceException("检查项列表序列化参数错误");
		}
		
	}


	private void setImageUrlToPath(CheckItem checkItem) {
		List<String> imageUrl = checkItem.getImageList();
		List<String> imagePaths = new ArrayList<String>();
		if(imageUrl != null && imageUrl.size() > 0){
			for (String url : imageUrl) {
				String path = PropertiesUtil.removeUrlPrefix(url);
				imagePaths.add(path);
			}
			checkItem.setImageList(imagePaths);
		}
	}

	private void setImagePathToUrl(CheckItem checkItem) {
		List<String> imageUrl = checkItem.getImageList();
		List<String> imagePaths = new ArrayList<String>();
		if(imageUrl != null && imageUrl.size() > 0){
			for (String url : imageUrl) {
				String path = PropertiesUtil.addUrlPrefix(url);
				imagePaths.add(path);
			}
		}
		checkItem.setImageList(imagePaths);
	}
	
	
	@Override
	public PageVO getCheckItemListByFromId(CheckItemSearchParam scp) {
		
		String fromId = scp.getFromId();
		if(StringUtil.isNullOrEmpty(fromId)){
			throw new ServiceException("参数为空");
		}
		PageVO pageVo = checkBillDao.getCheckItemListByFromId(scp); 
		@SuppressWarnings("unchecked")
		List<CheckItem> checkItems = (List<CheckItem>) pageVo.getPageData();
		if(checkItems == null ||  checkItems.size() < 1){
			throw new ServiceException("当前来源fromId={"+fromId+"}查询不到结果");
		}
		if(checkItems != null && checkItems.size() > 0){
			for (CheckItem checkItem : checkItems) {
				setImagePathToUrl(checkItem);
			}
		}
		return pageVo;
	}
	
	@Deprecated
	@Override
	public boolean saveCheckBill(Integer orderId, String attention) {
		if(orderId == null){
			throw new ServiceException("当前对应的订单Id为空");
		}
		
		
		List<CheckSuggest> css = baseDataServiceImpl.getCheckSuggestByIds(attention.split(","));
		long createTime = System.currentTimeMillis();
		if(css != null && css.size() > 0){
			for (CheckSuggest cs : css) {
				CheckItem item = new CheckItem();
				item.setCreateTime(createTime);
				item.setFrom(1);
				item.setFromId("");
				item.setItemName(cs.getName());
			}
		}
		return false;
	}

	@Override
	public PageVO getCheckBillPageVo(CheckbillSearchParam csp) {
		return checkBillDao.getCheckBillPageVo(csp);
	}

	@Override
	public CheckBill getCheckBillDetail(String checkbillId) {
		CheckBill cb = checkBillDao.getCheckBillDetail(checkbillId);
		if(cb == null){
			throw new ServiceException("当前检查单Id={"+checkbillId+"}未找到记录");
		}
		return cb;
	}

	@Override
	public List<CheckItemCount> getCheckItemCount(Integer patientId) {
		patientId = patientId != null && patientId > 0 ?  patientId : Integer.MIN_VALUE;
		List<String> checkBillIds = checkBillDao.getCheckbillIds(patientId);
		return (checkBillIds == null || checkBillIds.size() < 1) ? null :
			   checkBillDao.getCheckItemCount(checkBillIds);
	}

	@Override
	public List<CheckItem> getCheckItemByClassify(Integer patientId , String checkUpId) {
		patientId = patientId != null && patientId > 0 ?  patientId : Integer.MIN_VALUE;
		List<String> checkBillIds = checkBillDao.getCheckbillIds(patientId);
		List<CheckItem> list =	(checkBillIds == null || checkBillIds.size() < 1 || StringUtil.isNullOrEmpty(checkUpId)) ? null :
			checkBillDao.getCheckItemByClassify(checkBillIds,checkUpId);
		if(list != null && list.size() > 0){
			Iterator<CheckItem> ite = list.iterator();
			while (ite.hasNext()) {
				CheckItem checkItem = ite.next();
				setImagePathToUrl(checkItem);
				if(checkItem.getVisitingTime() == null 
						&& StringUtils.isBlank(checkItem.getResults()) 
						&& (checkItem.getImageList() == null || checkItem.getImageList().size() < 1)){
					ite.remove();
				}
			}
		}
		return list; 
	}

	@Override
	public List<CheckBillPageVo> getCheckBillList(Set<Integer> orderIds, Integer patientId) {
		
		List<CheckBillPageVo> rtnItem = null;
		/**
		 * 1、获取所有已上传了结果的检查单（checkBill）
		 * 2、对每个检查单获取所有的检查项（checkItem list）
		 */
		List<CheckBill> checkBills = checkBillDao.getCheckBillList(orderIds,patientId);
		
		if(checkBills != null && checkBills.size() > 0){
			rtnItem = new ArrayList<CheckBillPageVo>();
			for (CheckBill cb : checkBills) {
				CheckBillPageVo cbpv = new CheckBillPageVo();
				cbpv.setCreateTime(cb.getCreateTime());
				String cbId = cb.getId();
				List<CheckItem> cis = checkBillDao.getItemList(cbId);
				cbpv.setCheckItemList(cis);
				rtnItem.add(cbpv);
			}
		}
		return rtnItem;
	}

	@Override
	public CheckBill getCheckBillByOrderId(Integer orderId) {
		return checkBillDao.getCheckBillByOrderId(orderId);
	}

	@Override
	public void removeCheckItemByCheckBillId(String checkbillId) {
		checkBillDao.removeCheckItemByCheckBillId(checkbillId);
	}
	

	@Override
	public Map<String, Object> getCheckItem(String checkItemId) {
	    CheckItem checkItem = checkBillDao.getCheckItemById(checkItemId);
	    if(checkItem == null) {
	        throw new ServiceException("检查项为空");
        }

		CheckBill checkBill = checkBillDao.getCheckBillById(checkItem.getFromId());
		if (checkBill == null) {
			//检查单为空
			throw new ServiceException("检查单为空");
		}
		String checkupId = checkItem.getCheckUpId();
		CheckSuggest checkSuggest = baseDataDao.getCheckSuggestById(checkupId);
		if (checkSuggest == null) {
			throw new ServiceException("检查项为空");
		}
		Map<String, Object> result = Maps.newHashMap();
		result.put("checkupName", checkSuggest.getName());
		result.put("checkupId", checkSuggest.getId());
		result.put("suggestCheckTime", checkBill.getSuggestCheckTime());
		result.put("attention", checkBill.getAttention());

		result.put("indicatorIds", checkItem.getIndicatorIds());

        List<CheckSuggestItem> checkSuggestItems = baseDataDao.getCheckSuggestItemListByCheckupId(checkupId);
        List<CheckSuggestItemVo> checkSuggestItemVos = Lists.newArrayList();
        if (checkSuggestItems != null && checkSuggestItems.size() > 0) {
            for (CheckSuggestItem checkSuggestItem : checkSuggestItems) {
                CheckSuggestItemVo checkSuggestItemVo = BeanUtil.copy(checkSuggestItem, CheckSuggestItemVo.class);
                if (checkItem.getIndicatorIds() != null && checkItem.getIndicatorIds().contains(checkSuggestItem.getId().toString())) {
                    checkSuggestItemVo.setDoctorCare(true);
                } else {
                    checkSuggestItemVo.setDoctorCare(false);
                }
                checkSuggestItemVos.add(checkSuggestItemVo);
            }
        }

        result.put("checkSuggestItems", checkSuggestItemVos);

		return result;
	}

    @Override
    public Map<String, Object> isCheckItemFinish(String checkItemId) {
	    Map<String, Object> result = Maps.newHashMap();
	    CheckItem checkItem = checkBillDao.getCheckItemById(checkItemId);
		if (checkItem == null) {
			throw new ServiceException("检查单为空");
		}

		CheckBill checkBill = checkBillDao.getCheckBillById(checkItem.getFromId());
		Integer patientId = checkBill.getPatientId();
		result.put("patientId", patientId);
		result.put("checkUpId", checkItem.getCheckUpId());
        result.put("checkItemId", checkItemId);

        Integer status = checkBill.getCheckBillStatus();

        if (StringUtils.isNotBlank(checkItem.getResults()) || (checkItem.getImageList() != null && checkItem.getImageList().size() > 0) || checkItem.getVisitingTime()!=null) {
			result.put("isCheckItemFinish", true);
		} else {
			result.put("isCheckItemFinish", false);
		}

        return result;
    }

}
