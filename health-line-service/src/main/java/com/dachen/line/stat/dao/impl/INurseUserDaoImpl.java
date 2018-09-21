package com.dachen.line.stat.dao.impl;

import java.util.List;

import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.dachen.commons.exception.ServiceException;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.constants.UserEnum;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.constants.UserNurseEnum;
import com.dachen.health.commons.vo.User;
import com.dachen.line.stat.dao.INurseUserDao;
import com.dachen.line.stat.util.ConfigUtil;

@Repository
public class INurseUserDaoImpl  extends NoSqlRepository implements INurseUserDao {
	@Override
	public List<User> getUserList(List<String> hospital) {
		Query<User> uq = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.nurse.getIndex()).filter("status", UserEnum.UserStatus.normal.getIndex()).filter("nurse.hospitalId in", hospital);
		List<User> users =uq.asList();
		return users;
	
	}

	@Override
	public boolean checkUserCertStatus(Integer userId) {
		Query<User> uq = dsForRW.createQuery(User.class).filter("userType", UserEnum.UserType.nurse.getIndex()).filter("status", UserEnum.UserStatus.normal.getIndex());
		List<User> users =uq.asList(); 
		if(ConfigUtil.checkCollectionIsEmpty(users))
		{	
			return true;
		}
		return false;
	}

	@Override
	public User updateUserGuide(Integer userId) {

		Query<User> q = dsForRW.createQuery(User.class).field("_id")
				.equal(userId);
		User oldUser = q.get();
		if(null==oldUser)
		{	
			throw new ServiceException("用户不存在！");
		}
		UpdateOperations<User> ops = dsForRW.createUpdateOperations(User.class);

		if (UserType.nurse.getIndex() == oldUser.getUserType()) {
			
			ops.set("nurse.opreatorGuide", true);
		}

		User user = dsForRW.findAndModify(q, ops);
		
		return  user;
	}

}
