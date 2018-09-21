package com.dachen.line.stat.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.line.stat.comon.constant.NurseServiceOrderSetEnum;
import com.dachen.line.stat.dao.IUserServiceTimeDao;
import com.dachen.line.stat.entity.vo.NurseDutyTime;
import com.dachen.line.stat.service.IUserServiceTimeService;

/**
 * 护士订单服务
 * 
 * @author liwei
 * @date 2015/8/19
 */
@Service
public class UserServiceTimeServiceImpl implements IUserServiceTimeService {

	@Autowired
	private IUserServiceTimeDao userServiceTimeDao;

	@Override
	public List<NurseDutyTime> getUserServiceTimeList(Integer userId) {
		return userServiceTimeDao.getUserServiceTimeList(userId);
	}

	@Override
	public void updateUserServiceTime(Integer userId, String time,
			Integer status) {
		userServiceTimeDao.updateUserServiceTime(userId, time, status);

	}

	/**
	 * 校验护士服务时间设置
	 * 
	 * @param status
	 *            如果查询用户的时间没空，就返回101 如果用户的时间记录数目小于等于2 就返回 102 否则 转改都为成功
	 *            101：用户没有设置时间（判断依据用户从未设置过服务时间，或是当前时间超过最大预约时间）
	 *            102：用户需要扩大设置时间范围（判断依据用户设置的最大的时间，距离当前的时间小于2天，则提醒设置扩大服务时间）
	 */
	@Override
	public Map<String, Object> checkUserServiceTimeSet(Integer userId) {
        String message = null;
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Integer code = NurseServiceOrderSetEnum.set_1.getIndex();
		List<NurseDutyTime> result = userServiceTimeDao
				.getUserServiceTime(userId);
		if (null == result || result.size() == 0) {
			code = NurseServiceOrderSetEnum.set_101.getIndex();
			message=NurseServiceOrderSetEnum.set_101.getTitle();
		} else if (result.size() < 2) {
			code = NurseServiceOrderSetEnum.set_102.getIndex();
			message=NurseServiceOrderSetEnum.set_102.getTitle();
		}
		resultMap.put("data", result);
		resultMap.put("resultCode", code);
		resultMap.put("message", message);
		return resultMap;
	}

}
