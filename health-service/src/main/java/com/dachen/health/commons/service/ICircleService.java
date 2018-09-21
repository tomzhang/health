package com.dachen.health.commons.service;

import java.util.List;

import com.dachen.health.commons.vo.CircleVO;

public interface ICircleService {
    
    /**
     * 获取用户的所有圈子和科室
     */
    List<CircleVO> getUserAllCircle(String userId);
}
