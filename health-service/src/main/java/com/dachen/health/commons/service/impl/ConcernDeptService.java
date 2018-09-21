package com.dachen.health.commons.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.base.dao.IBaseDataDao;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.health.commons.dao.ConcernDeptRepository;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.ConcernDept;
import com.dachen.health.commons.vo.User;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by sharp on 2017/11/28.
 */
@Service
public class ConcernDeptService {

    static Logger logger = LoggerFactory.getLogger(ConcernDeptService.class);

    @Autowired
    ConcernDeptRepository concernDeptRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    IBaseDataDao baseDataDao;

    /**
     *
     * @param userId
     * @return
     */
    public List<DeptVO> findDepts(Integer userId) {
        long time1 = System.currentTimeMillis();
        List<String> deptIds =  concernDeptRepository.findDeptIds(userId);
        long time2 = System.currentTimeMillis();
        logger.info("########.findDeptIds cost {}ms", time2-time1);
        if (deptIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<DeptVO> deptVOS = baseDataDao.getDeptByIds(deptIds);
        long time3 = System.currentTimeMillis();
        logger.info("########.getDeptByIds cost {}ms", time3-time2);

        this.sort(deptVOS, deptIds);
        logger.info("########.sort cost {}ms", System.currentTimeMillis()-time3);
        return deptVOS;
    }

    /**
     * 排序，按照科室添加的顺序返回
     * @param deptVOS
     * @param deptIds
     */
    private void sort(List<DeptVO> deptVOS, List<String> deptIds) {
        Map<String, Integer> sortMap = new HashMap<>();
        for (int i = 0; i < deptIds.size(); i++) {
            sortMap.put(deptIds.get(i), i);
        }
        Collections.sort(deptVOS, Comparator.comparing(x -> sortMap.get(x.getId())));
    }

    public List<DeptVO> findAllDepts(Integer userId) {
        List<DeptVO> list = new ArrayList<>();
        User user = userRepository.getUser(userId);
        if (user != null && user.getDoctor() != null && user.getDoctor().getDeptId() != null) {
            list.add(baseDataDao.getDeptById(user.getDoctor().getDeptId()));
        }
        List<String> deptIds =  concernDeptRepository.findDeptIds(userId);
        List<DeptVO> concernDepts = baseDataDao.getDeptByIds(deptIds);
        if (concernDepts != null && concernDepts.size() > 0) {
            list.addAll(concernDepts);
        }
        return list;
    }

    public ConcernDept setDepts(Integer userId, List<String> deptIds) {
        if (deptIds == null) {
            deptIds = new ArrayList<>();
        }
        if (deptIds.size() > 10) {
            throw new ServiceException("最多选择10个");
        }
        return concernDeptRepository.setDepts(userId, deptIds);
    }
    
    public List<String> findConcernDeptUserIds(String deptId, int pageIndex, int pageSize){
        if(StringUtils.isBlank(deptId)){
            return new ArrayList<>();
        }
        pageSize = (pageSize == 0) ? 15 : pageSize;
        
        List<String> userIds = concernDeptRepository.findUserIds(deptId, pageIndex, pageSize);
        if(Objects.isNull(userIds)){
            userIds = new ArrayList<>();
        }
        return userIds;
    }

}
