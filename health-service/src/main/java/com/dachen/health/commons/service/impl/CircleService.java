package com.dachen.health.commons.service.impl;

import com.dachen.health.cache.CircleCacheManager;
import com.dachen.health.commons.service.ICircleService;
import com.dachen.health.commons.vo.CircleVO;
import com.dachen.manager.RemoteSysManagerUtil;
import com.dachen.util.JSONUtil;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

@Service
public class CircleService implements ICircleService {
    static Logger logger = LoggerFactory.getLogger(CircleService.class);


    @Autowired
    private RemoteSysManagerUtil remoteSysManagerUtil;
    @Autowired
    private CircleCacheManager circleCacheManager;


    @Override
    public List<CircleVO> getUserAllCircle(String userId) {
        String result = remoteSysManagerUtil.postForObject("http://CIRCLE/doctorAllCircle/{userId}", userId);
        if(StringUtils.isBlank(result)){
            return new ArrayList<CircleVO>();
        }
        List<CircleVO> data = JSONUtil.parseList(CircleVO.class, result);
        return data;
    }

    /**
     * 获取用户加入的圈子信息
     * @param userId
     * @return
     */
    @Async
    public Future<String> findCircleByUserId(Integer userId) {
        Map<String, String> map = new HashedMap();
        map.put("userId", String.valueOf(userId));
        String res = remoteSysManagerUtil.send("circle", "inner/base/findLoginCircleByUserId", map);
        return new AsyncResult<>(res);
    }

    /**
     * 同步圈子ID信息
     * @param circles
     * @param userId
     */
    @Async
    public void syncCircleToTokenInfoWhenLogin(List<CircleVO> circles, Integer userId) {
		if (circles == null || circles.isEmpty()) {
			return;
		}
		Set<String> circleIds = circles.stream().map(o -> o.getId()).collect(Collectors.toSet());
		circleCacheManager.updateCircleCache(userId + "", circleIds);
		logger.info("同步圈子ID到cache中完成。。。");
    }

}
