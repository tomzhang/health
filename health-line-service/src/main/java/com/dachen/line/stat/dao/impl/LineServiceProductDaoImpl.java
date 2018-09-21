package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.base.entity.po.CheckSuggest;
import com.dachen.line.stat.dao.ILineRelateProductDao;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.entity.vo.LineRelateProduct;
import com.dachen.line.stat.entity.vo.LineService;
import com.dachen.line.stat.entity.vo.LineServiceProduct;
import com.dachen.line.stat.util.ConfigUtil;

@Repository
public class LineServiceProductDaoImpl extends NoSqlRepository implements
		ILineServiceProductDao {

	@Autowired
	private ILineRelateProductDao lineRelateProductDao;
	/**
	 * 获取系统产品列表
	 */
	 public List<LineServiceProduct> getSystemLineServiceProduct() {
		 List<LineServiceProduct> result = new ArrayList<LineServiceProduct>();
			Query<LineServiceProduct> uq = dsForRW.createQuery(LineServiceProduct.class);//查询搜有的数据
			result = uq.asList();
			
			return result;
	}

	 public LineServiceProduct getSystemLineServiceBean(String id)
	 {
		 LineServiceProduct result =null;
		 List<LineServiceProduct> beans = getSystemLineServiceProduct();
		 for(LineServiceProduct bean :beans)
		 {	 
			 if(id.equals(bean.getId()))
			 {	 
				 result =bean;
				 break;
			 }
			 
		 }
		 return  result;
	 }


	@Override
	public List<LineService> getProductServiceItemList(String productId) {
		
		List<LineRelateProduct> raleteIds = lineRelateProductDao.getLineRelateProductList("sourceId", productId);
		List<ObjectId> lsIds = new ArrayList<ObjectId>();
		if(null!=raleteIds && raleteIds.size()>0)
		{	
			
			for(LineRelateProduct product :raleteIds)
			{	
				lsIds.add(new ObjectId(product.getLsId()));
			}
			
		}
		Query<LineService> uq = dsForRW.createQuery(LineService.class).filter("_id in", lsIds);//查询搜有的数据
		return uq.asList();
	}
    //566e76a8b472651ca459a262
	@Override
	public LineService getLineServiceItem(String lsId) {
		LineService  query = null;
		
		List<LineService> list = dsForRW.createQuery(LineService.class).filter("_id", new ObjectId(lsId)).asList();
		if(ConfigUtil.checkCollectionIsEmpty(list))
		{	
			query=list.get(0);
		}
		
		
		return query;
	}
	
	/**
	 * 查询基础业务id
	 * @param checkId
	 * @return
	 */
	public CheckSuggest getCheckSuggestItem(String checkId) {
		
		Query<CheckSuggest> query = dsForRW.createQuery(CheckSuggest.class)
				.field("_id").equal(new ObjectId(checkId));
		return query.get();
	}
	
	
}
