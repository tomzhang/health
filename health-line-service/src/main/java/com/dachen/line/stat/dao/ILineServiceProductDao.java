package com.dachen.line.stat.dao;

import java.util.List;

import com.dachen.line.stat.entity.vo.LineService;
import com.dachen.line.stat.entity.vo.LineServiceProduct;

/**
 * 获取系统产品列表
 * 
 * @author weilit
 *
 */
public interface ILineServiceProductDao {
	
	public List<LineServiceProduct> getSystemLineServiceProduct();

	public LineServiceProduct getSystemLineServiceBean(String id);

	public List<LineService> getProductServiceItemList(String productId);
	
	public LineService getLineServiceItem(String lsId);

}
