package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.ILineServiceProductDao;
import com.dachen.line.stat.dao.IOrderRelatedCheckItemDao;
import com.dachen.line.stat.entity.vo.OrderRelatedCheckItem;
import com.dachen.line.stat.util.ConfigUtil;

@Repository
public class OrderRelatedCheckItemDaoImpl  extends NoSqlRepository implements IOrderRelatedCheckItemDao {

	@Autowired
	private ILineServiceProductDao lineServiceProductDao;
	
	@Override
	public List<OrderRelatedCheckItem> getOrderRelatedCheckItemList(String column,
			Object sourceId) {
		List<OrderRelatedCheckItem> result = new ArrayList<OrderRelatedCheckItem>();
		Query<OrderRelatedCheckItem> uq = dsForRW.createQuery(OrderRelatedCheckItem.class).filter(column, sourceId);//查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public List<ObjectId> getOrderRelatedCheckItemStringList(String column,
			Object sourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateOrderRelatedCheckItem(String id, String department) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertBatchOrderRelatedCheckItem(List<OrderRelatedCheckItem> departmentList) {
		if(ConfigUtil.checkCollectionIsEmpty(departmentList))
		{	
			for(OrderRelatedCheckItem depart:departmentList)
			{	
				insertOrderRelatedCheckItem(depart);
			}
			
		}
		
	}

	@Override
	public void insertOrderRelatedCheckItem(OrderRelatedCheckItem department) {
		dsForRW.insert(department);
		
	}
	
	/**
	 * 产品列表
	 * @param productId
	 * @return
	 */
	public  String getOrderCheckItemList(String orderId)
	{   
		Map<String,Object> checkItem = new HashMap<String, Object>();
		
		StringBuffer buffer = new StringBuffer();
		List<OrderRelatedCheckItem> lineServiceList =  getOrderRelatedCheckItemList("sourceId",orderId);
		if(ConfigUtil.checkCollectionIsEmpty(lineServiceList))
		{	
			int size = lineServiceList.size();
					
			for(int i =0;i<size;i++)
			{	
				if(i<size-1)
				{	
					buffer.append(lineServiceList.get(i).getCheckItem()).append(",");
				}
				else
				{	
					buffer.append(lineServiceList.get(i).getCheckItem());

				}
			}
		}
		
		return buffer.toString();
	}

}
