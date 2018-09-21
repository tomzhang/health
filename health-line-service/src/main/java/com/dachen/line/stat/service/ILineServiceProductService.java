package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.line.stat.entity.vo.LineServiceProduct;

public interface ILineServiceProductService {
	
	public List<LineServiceProduct> getSystemLineServiceProduct();
	/**
	 * 根据套餐id查询套餐信息
	 * @param productId
	 * @return
	 */
	public LineServiceProduct getSystemLineServiceProductById(String productId);
	
	public List<Map<String, Object>> getCheckItemListById(String productId,int type);
	
	public List<Map<String, Object>> getCertificatedHospitalList(Integer status);
	
}
