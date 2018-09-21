package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.line.stat.dao.IUserDepartMentDao;
import com.dachen.line.stat.entity.vo.UserDepartment;
import com.dachen.line.stat.util.ConfigUtil;

@Repository
public class UserDepartMentDaoImpl  extends NoSqlRepository implements IUserDepartMentDao {

	

	@Override
	public List<UserDepartment> getUserDepartmentList(String column,
			Object sourceId) {
		List<UserDepartment> result = new ArrayList<UserDepartment>();
		Query<UserDepartment> uq = dsForRW.createQuery(UserDepartment.class).filter(column, sourceId);//查询搜有的数据
		result = uq.asList();
		return result;
	}

	@Override
	public List<ObjectId> getUserDepartmentStringList(String column,
			Object sourceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateUserDepartment(String id, String department) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void insertBatchUserDepartment(List<UserDepartment> departmentList) {
		if(ConfigUtil.checkCollectionIsEmpty(departmentList))
		{	
			for(UserDepartment depart:departmentList)
			{	
				insertUserDepartment(depart);
			}
			
		}
		
	}

	@Override
	public void insertUserDepartment(UserDepartment department) {
		dsForRW.insert(department);
		
	}
	
	

}
