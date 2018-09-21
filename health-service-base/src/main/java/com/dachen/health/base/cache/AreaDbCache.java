package com.dachen.health.base.cache;

import com.alibaba.fastjson.JSON;
import com.dachen.health.base.entity.vo.AreaVO;
import com.dachen.util.RedisUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * @author 钟良
 * @desc
 * @date:2017/10/25 10:56 Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class AreaDbCache {

    private static final Logger logger = LoggerFactory.getLogger(AreaDbCache.class);

    private static final String AREA_INFO_KEY = "health:base:b_area";

    public static List<AreaVO> getAllAreaInfoList() {
        List<String> jsonList = RedisUtil.lrange(AREA_INFO_KEY, 0, -1);

        if (CollectionUtils.isEmpty(jsonList)) {
            return Collections.emptyList();
        }
        List<AreaVO> list = new ArrayList<>(jsonList.size() + 1);
        for (String s : jsonList) {
            AreaVO obj = JSON.parseObject(s, AreaVO.class);
            if (obj == null) {
                continue;
            }
            list.add(obj);
        }
        return list;
    }

    public static void saveAreaList(List<AreaVO> areaList) {
        if(CollectionUtils.isEmpty(areaList)){
            return;
        }
        List<String> jsonList = new ArrayList<>(areaList.size());
        for (AreaVO area : areaList) {
            String s = JSON.toJSONString(area);
            jsonList.add(s);
        }
        logger.info("saveAreaList:{}",jsonList);
        RedisUtil.rpush(AREA_INFO_KEY, jsonList.toArray(new String[jsonList.size()]));
    }

    public static void clearAreaListDbCache() {
        List<String> jsonList = RedisUtil.lrange(AREA_INFO_KEY, 0, -1);

        if (!CollectionUtils.isEmpty(jsonList)) {
            for (String s: jsonList) {
                RedisUtil.removeVal(AREA_INFO_KEY, s);
            }
        }
    }
}
