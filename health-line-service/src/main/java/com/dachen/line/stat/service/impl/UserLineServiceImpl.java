package com.dachen.line.stat.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.dachen.commons.exception.ServiceException;
import com.dachen.line.stat.comon.constant.NurseServiceOrderSetEnum;
import com.dachen.line.stat.dao.IUserLineServiceDao;
import com.dachen.line.stat.entity.vo.UserLineService;
import com.dachen.line.stat.service.IUserLineService;

/**
 * 护士订单服务
 * 
 * @author liwei
 * @date 2015/8/19
 */
@Service
public class UserLineServiceImpl implements IUserLineService {

	@Autowired
	private IUserLineServiceDao userLineServiceDao;

	@Override
	public List<UserLineService> getUserLineService(Integer userId) {
		// TODO Auto-generated method stub
		return userLineServiceDao.getUserLineService(userId);
	}

	@Override
	public void updateUserLineService(Integer userId, String serviceId,
			Integer status) {
		userLineServiceDao.updateUserLineService(userId, serviceId, status);

	}
	/**
	 * *  状态都是0 返回码为 100 
	 *    状态码都为 2 就返回 104 
	 *    100 用户没有设置服务套餐（判断依据用户没有关联套餐）
	 * 这里查询出来的状态都是 0 位设置 104：关闭了所有的服务，也没有时间（判断依据，所有的关联表的关闭状态）服务状态都是 2 关闭
	 */
	@Override
	public Map<String, Object> checkUserServiceSet(Integer userId) {
		String message = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer resultCode = NurseServiceOrderSetEnum.set_1.getIndex();
		int countZore = 0;
		int countTwo = 0;
		int countOne = 0;
		List<UserLineService> userServiceList = userLineServiceDao.getUserLineService(userId);
		if(null!=userServiceList && userServiceList.size()>0)
		{	
			for(UserLineService user:userServiceList)
			{	
				if(null!=user)
				{	
					if(user.getStatus()==0)
					{		
						countZore++;
					}
					else if(user.getStatus()==2)
					{		
						countTwo++;
					}
					else if(user.getStatus()==1)
					{		
						countOne++;
					}
				}
			}
			if(countZore==userServiceList.size())
			{	
				resultCode=NurseServiceOrderSetEnum.set_100.getIndex();
				message =NurseServiceOrderSetEnum.set_100.getTitle();
			}
			if(countTwo==userServiceList.size())
			{	
				resultCode=NurseServiceOrderSetEnum.set_104.getIndex();
				message =NurseServiceOrderSetEnum.set_104.getTitle();
			}
		}
		resultMap.put("resultCode", resultCode);
		resultMap.put("message", message);
		resultMap.put("data", userServiceList);
		return resultMap;
	}



}
