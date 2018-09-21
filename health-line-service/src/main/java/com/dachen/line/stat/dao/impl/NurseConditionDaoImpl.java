package com.dachen.line.stat.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.vo.User;
import com.dachen.line.stat.dao.INurseOrderConditionDao;

@Repository
public class NurseConditionDaoImpl extends NoSqlRepository implements
		INurseOrderConditionDao {
	 @Autowired
	  private UserManager userManager;

	private static User user;

	@Override
	public List<String> getNurseBelongHospital(Integer userId) {
		List<String> hospitalList = new ArrayList<String>();
		user = userManager.getUser(userId);
		String nurse = user.getNurse().getHospitalId();
		hospitalList.add(nurse);

		return hospitalList;
	}

	@Override
	public List<String> getNurseBelongDepartment(Integer userId) {
		List<String> departList = new ArrayList<String>();
		String depart = user.getNurse().getDepartments();
		departList.add(depart);
		return departList;
	}

}
