package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.ILineRelateProductDao;
import com.dachen.line.stat.entity.vo.LineRelateProduct;
import com.dachen.line.stat.util.ConfigUtil;

@Repository
public class LineRelateProductDaoImpl  extends NoSqlRepository implements ILineRelateProductDao {

	@Override
	public List<LineRelateProduct> getLineRelateProductList(String column,
			Object sourceId) {
		List<LineRelateProduct> result = new ArrayList<LineRelateProduct>();
		Query<LineRelateProduct> uq = dsForRW.createQuery(LineRelateProduct.class).filter(column, sourceId);//查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public List<ObjectId> getLineRelateProductStringList(String column,
			Object sourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateLineRelateProduct(String id, String department) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertBatchLineRelateProduct(List<LineRelateProduct> departmentList) {
		if(ConfigUtil.checkCollectionIsEmpty(departmentList))
		{	
			for(LineRelateProduct depart:departmentList)
			{	
				insertLineRelateProduct(depart);
			}
			
		}
		
	}

	@Override
	public void insertLineRelateProduct(LineRelateProduct department) {
		dsForRW.insert(department);
		
	}
	
	

}
