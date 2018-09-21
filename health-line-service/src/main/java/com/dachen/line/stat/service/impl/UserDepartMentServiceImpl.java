package com.dachen.line.stat.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.line.stat.dao.IUserDepartMentDao;
import com.dachen.line.stat.entity.vo.UserDepartment;
import com.dachen.line.stat.service.IUserDepartMentService;
import com.dachen.line.stat.util.ConfigUtil;

/**
 * 短信
 * 
 * @author liwei
 * @date 2015/12/14
 */
@Service
public class UserDepartMentServiceImpl implements IUserDepartMentService {

	@Autowired
	private IUserDepartMentDao userDepartMentDao;

	public  String getUserDepartmentList(String productId)
	{
		StringBuffer buffer = new StringBuffer();
		
		List<UserDepartment> partList =   userDepartMentDao.getUserDepartmentList("sourceId", productId);
		if(ConfigUtil.checkCollectionIsEmpty(partList))
		{	
			int size = partList.size();
					
			for(int i =0;i<size;i++)
			{	
				if(i<size-1)
				{	
					buffer.append(partList.get(i).getDepartment()).append(",");
				}
				else
				{	
					buffer.append(partList.get(i).getDepartment());
				}
			}
		}
		
		return buffer.toString();
	}
}
