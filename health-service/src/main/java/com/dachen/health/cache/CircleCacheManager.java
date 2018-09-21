package com.dachen.health.cache;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.dachen.cache.core.ICache;
import com.dachen.cache.impl.DoctorCircleCache;

/**
 * 缓存医生圈子信息
 * @author tanyulin
 *
 */
@Component
public class CircleCacheManager {
	
	/**
	 * 更新用户圈子缓存
	 * @param userId 用户Id
	 * @param circleIdSet 圈子idset
	 */
	public void updateCircleCache(String userId, Set<String> circleIdSet) {
		ICache<String, Set<String>> cache = DoctorCircleCache.getInstance(); 
        cache.save(userId,circleIdSet); 
	}
	
	/**
	 * 获取用户圈子缓存
	 * @param userId 用户Id
	 * @return
	 */
	public List<String> getCircleCache(String userId) {
		ICache<String, Set<String>> cache = DoctorCircleCache.getInstance();
		Set<String> circleIdSet = (Set<String>) cache.get(userId);
	    if(circleIdSet != null) {
	    	return circleIdSet.stream().collect(Collectors.toList());
	    }
	    return null;
	}
}