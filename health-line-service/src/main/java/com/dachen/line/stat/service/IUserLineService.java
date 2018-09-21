package com.dachen.line.stat.service;

import java.util.List;
import java.util.Map;

import com.dachen.line.stat.entity.vo.UserLineService;

public interface IUserLineService {
	
	/**
	 * 获取用户的服务器列表
	 * @param userId
	 * @return
	 */
	public List<UserLineService> getUserLineService(Integer userId);

	/**
	 * 更新用户服务设置
	 * @param userId
	 * @param serviceId
	 * @param status
	 */
	public void updateUserLineService(Integer userId, String serviceId,
			Integer status);
	
	
	/**
	 * 校验护士是否开启服务
	 * @param status   
	 * 如果 状态都是0  返回码为 100   状态码都为 2    就返回 104  
	100  用户没有设置服务套餐（判断依据用户没有关联套餐）                                这里查询出来的状态都是 0  位设置
	104：关闭了所有的服务，也没有时间（判断依据，所有的关联表的关闭状态）服务状态都是 2 关闭
	 */
	public Map<String,Object> checkUserServiceSet(Integer userId);
	
	
	
}
