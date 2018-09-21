package com.dachen.health.pack.order.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.dachen.commons.exception.ServiceException;
import com.dachen.drug.api.client.DrugApiClientProxy;
import com.dachen.drug.api.entity.CGoodsView;
import com.dachen.health.pack.order.entity.param.OrderDrugParam;
import com.dachen.health.pack.order.entity.po.*;
import com.dachen.health.pack.order.entity.vo.OrderDrugRecipeVO;
import com.dachen.health.pack.order.mapper.OrderDrugMapper;
import com.dachen.health.pack.order.mapper.OrderMapper;
import com.dachen.health.pack.order.mapper.OrderRecipeMapper;
import com.dachen.health.pack.order.service.IOrderDrugService;
import com.dachen.health.pack.pack.entity.po.PackDrug;
import com.dachen.health.pack.pack.entity.po.PackDrugExample;
import com.dachen.health.pack.pack.mapper.PackDrugMapper;
import com.dachen.health.pack.patient.model.OrderSession;
import com.dachen.health.pack.patient.service.IOrderSessionService;
import com.dachen.medice.vo.Drug;
import com.dachen.medice.vo.DrugRecipe;
import com.dachen.medice.vo.PatientDrugSuggest;
import com.dachen.medice.vo.PatientDrugSuggestList;
import com.dachen.sdk.exception.HttpApiException;
import com.dachen.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDrugServiceImpl implements IOrderDrugService{

	@Autowired
	OrderDrugMapper orderDrugMapper;

	@Autowired
	OrderRecipeMapper orderRecipeMapper;
	
	@Autowired
	OrderMapper orderMapper;
	
	@Autowired
	IOrderSessionService orderSessionService;
	
	@Autowired
	PackDrugMapper packDrugMapper;
	
	
	public void deleteOrderDrug(OrderDrugParam param) {
		OrderDrugExample orderDrugExample = new OrderDrugExample();
		orderDrugExample.createCriteria().andOrderIdEqualTo(param.getOrderId());
		orderDrugMapper.deleteByExample(orderDrugExample);
	}
	
	/**
	 * 用药信息从关联套餐中导入到订单用药中
	 * @param packId
	 * @param orderId
	 */
	public void saveOrderDrug(Integer packId,Integer orderId) {
		
		PackDrugExample packDrugExample = new PackDrugExample();
		packDrugExample.createCriteria().andPackIdEqualTo(packId);
		List<PackDrug> packDrugs = packDrugMapper.selectByExample(packDrugExample);
		
		for(PackDrug packDrug : packDrugs) {
			OrderDrug orderDrug = new OrderDrug();
			orderDrug.setId(StringUtil.randomUUID());
			orderDrug.setDrugId(packDrug.getDrugId());
			orderDrug.setOrderId(orderId);
			orderDrugMapper.insert(orderDrug);
		}
	}

	public void saveOrderDrug(OrderDrugParam param) throws HttpApiException {
		
		if(param.getOrderId()==null) {
			throw new ServiceException("订单信息不能为空！"); 
		}
		if(StringUtil.isBlank(param.getDrugReiceJson())) {
			throw new ServiceException("药品信息不能为空！"); 
		}
		
		deleteOrderDrug(param);
		
		OrderDrugRecipeVO orderDrugRecipeVO = new OrderDrugRecipeVO();
		//JSONUtil.parseObject(OrderDrugRecipeVO.class,param.getDrugReiceJson());
		List<DrugRecipe> drugRecipes = JSONArray.parseArray(param.getDrugReiceJson(),DrugRecipe.class);
		
		orderDrugRecipeVO.setDrugRecipes(drugRecipes);
		orderDrugRecipeVO.setOrderId(param.getOrderId());
		
		//保存到订单药品中
		save(orderDrugRecipeVO);
		
		OrderSession orderSession = orderSessionService.findOneByOrderId(param.getOrderId());
		if (orderSession == null || orderSession.getServiceBeginTime() == null || orderSession.getServiceBeginTime() == 0) {
			return;//未开始服务，无需更新OrderRecipe
		}
		
		Order order = orderMapper.getOne(orderDrugRecipeVO.getOrderId());
		//生成药方保存
//		SCareTemplate careTemplate = null;//scareTemplateMapper.selectByPrimaryKey(order.getCareTemplateId());
		
		OrderRecipeExample orderRecipeExample = new OrderRecipeExample();
		orderRecipeExample.createCriteria().andOrderIdEqualTo(order.getId());
		List<OrderRecipe> orderRecipes = orderRecipeMapper.selectByExample(orderRecipeExample);
		if(orderRecipes!=null && orderRecipes.size()>0) {
			OrderRecipe orderRecipe = orderRecipes.get(0);
			String result = drugApiClientProxy.deleteDrugRecipe(orderRecipe.getRecipeId());
			/*if(result == null) {
				throw new ServiceException("删除药方失败!"); 
			}*/
		}
		
		String recipeId = null;//mediceHelper.saveDrugRecipe(param.getAccess_tonke(), order.getDoctorId(),order.getUserId(), order.getPatientId(), careTemplate.getGroupId(), param.getDrugReiceJson());
		
		if(StringUtil.isBlank(recipeId)) {
			throw new ServiceException("生成药方失败!"); 
		}
		
		if(orderRecipes==null || orderRecipes.size()==0) {
			OrderRecipe orderRecipe = new OrderRecipe();
			orderRecipe.setCreateTime(System.currentTimeMillis());
			orderRecipe.setId(StringUtil.randomUUID());
			orderRecipe.setOrderId(order.getId());
			orderRecipe.setRecipeId(recipeId);
			orderRecipeMapper.insert(orderRecipe);
		}else {
			OrderRecipe orderRecipe = orderRecipes.get(0);
			orderRecipe.setRecipeId(recipeId);
			orderRecipe.setCreateTime(System.currentTimeMillis());
			orderRecipeMapper.updateByPrimaryKey(orderRecipe);
		}
	}
	
	public void save(OrderDrugRecipeVO orderDrugRecipe) {
		
		for(DrugRecipe drugRecipe : orderDrugRecipe.getDrugRecipes()) {
			if(StringUtil.isEmpty(drugRecipe.getDrug())) {
				continue;
			}
			OrderDrug orderDrug = new OrderDrug();
			orderDrug.setId(StringUtil.randomUUID());
			orderDrug.setDrugId(drugRecipe.getDrug());
			orderDrug.setOrderId(orderDrugRecipe.getOrderId());
			orderDrugMapper.insert(orderDrug);
		}
	}

	/**
	 * 有OrderRecipe记录的调用此接口获取药品建议
	 */
	public PatientDrugSuggestList findDrugInfo(OrderDrugParam param) throws HttpApiException {
		
		if(param.getOrderId()==null) {
			throw new ServiceException("订单ID不能为空！"); 
		}
		
		OrderRecipeExample orderRecipeExample = new OrderRecipeExample();
		orderRecipeExample.createCriteria().andOrderIdEqualTo(param.getOrderId());
		List<OrderRecipe> orderRecipes = orderRecipeMapper.selectByExample(orderRecipeExample);
		
		if(orderRecipes == null || orderRecipes.size()==0) {
//			throw new ServiceException("您没有药方信息");
			return findDrugInfo(param.getAccess_tonke(), param.getOrderId());
		}
			
		PatientDrugSuggestList patientDrugSuggests = null;//mediceHelper.getDrugRecipe(param.getAccess_tonke(), orderRecipes.get(0).getRecipeId());
		
		return patientDrugSuggests;
	}

	@Autowired
	protected DrugApiClientProxy drugApiClientProxy;
	
	/**
	 * 无OrderRecipe记录的调用此接口获取药品建议
	 */
	public PatientDrugSuggestList findDrugInfo(String access_tonke, Integer orderId) throws HttpApiException {
		PatientDrugSuggestList list = new PatientDrugSuggestList();
		if (orderId == null) {
			throw new ServiceException("订单ID不能为空！");
		}
		List<String> drugIds = getDrugIdList(orderId);
		if (drugIds.size() == 0) {
			return list;
		}
		
		List<PatientDrugSuggest> patientDrugSuggests = new ArrayList<PatientDrugSuggest>();
		for (String drugId : drugIds) {
			PatientDrugSuggest patientDrugSuggest = new PatientDrugSuggest();
			CGoodsView goodsView = drugApiClientProxy.getDrugUsage(drugId);
			if (goodsView == null)
				continue;
			patientDrugSuggest.setC_drug_usage_list(goodsView.getDrugUsegeList());
			Drug drug = new Drug();
			drug.setId(drugId);
			patientDrugSuggest.setDrug(drug);
			patientDrugSuggest.setGeneral_name(goodsView.getGeneralName());
			patientDrugSuggest.setManufacturer(goodsView.getManufacturer());
			patientDrugSuggest.setPack_specification(goodsView.getPackSpecification());
//			patientDrugSuggest.setRequires_quantity(requires_quantity);
			patientDrugSuggests.add(patientDrugSuggest);
		}
		list.setC_patient_drug_suggest_list(patientDrugSuggests);	
		return list;
	}
	
	
	public Integer countOrderDrug(Integer orderId) {
		OrderDrugExample orderDrugExample = new OrderDrugExample();
		orderDrugExample.createCriteria().andOrderIdEqualTo(orderId);
		return orderDrugMapper.countByExample(orderDrugExample);
	}
	
	public List<String> getDrugIdList(Integer orderId) {
		OrderDrugExample example = new OrderDrugExample();
		example.createCriteria().andOrderIdEqualTo(orderId);
		List<OrderDrug> orderDrugs = orderDrugMapper.selectByExample(example);
		
		List<String> drugIds = new ArrayList<String>();
		for (OrderDrug orderDrug : orderDrugs) {
			drugIds.add(orderDrug.getDrugId());
		}
		return drugIds;
	}
	
	
	
}
