package com.dachen.health.operationLog.dao.impl;

import com.dachen.commons.page.PageVO;
import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.auto.dao.impl.BaseDaoImpl;
import com.dachen.health.commons.constants.UserEnum.UserType;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.service.UserManager;
import com.dachen.health.commons.service.impl.UserManagerImpl;
import com.dachen.health.commons.vo.User;
import com.dachen.health.operationLog.dao.IOperationLogDao;
import com.dachen.health.operationLog.entity.param.OperationLogParam;
import com.dachen.health.operationLog.entity.po.OperationLog;
import com.dachen.util.StringUtils;
import com.google.common.collect.Lists;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

/**
 * @author 钟良
 * @desc
 * @date:2017/9/29 10:46 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Repository
public class OperationLogDaoImpl extends NoSqlRepository implements IOperationLogDao {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void save(OperationLog operationLog) {
        dsForRW.save(operationLog);
    }

    @Override
    public PageVO list(OperationLogParam param) {
        Query<OperationLog> query = dsForRW.createQuery(OperationLog.class);
        if (StringUtils.isNotEmpty(param.getKeywords())) {
            List<Integer> ids = userRepository.getDoctorIdsByName(param.getKeywords());
            if (CollectionUtils.isEmpty(ids)) {
                return new PageVO(null, 0L, param.getPageIndex(), param.getPageSize());
            }
            query.field("userId").in(ids);
        }
        long total = query.countAll();
        if (total < 1) {
            return new PageVO(null, 0L, param.getPageIndex(), param.getPageSize());
        }

        query.order("-date");
        query.limit(param.getPageSize()).offset(param.getOffset());
        List<OperationLog> logList = query.asList();
        if (!CollectionUtils.isEmpty(logList)) {
            List<Integer> doctorIds = Lists.transform(logList, o -> o.getUserId());
            List<User> userList = userRepository.getDoctorsByIds(doctorIds);
            if(!CollectionUtils.isEmpty(userList)){
                Map<Integer, User> userMap = new HashMap<>();
                for (User user : userList) {
                    userMap.put(user.getUserId(), user);
                }

                for (OperationLog operationLog : logList) {
                    User user = userMap.get(operationLog.getUserId());
                    if (Objects.nonNull(user)) {
                        operationLog.setUser(user.getName());
                        operationLog.setPhone(user.getTelephone());
                    }
                }
            }
        }
        return new PageVO(logList, total, param.getPageIndex(), param.getPageSize());
    }

    @Override
    public Long listCount(OperationLogParam param) {
        Query<OperationLog> query = dsForRW.createQuery(OperationLog.class);
        if (StringUtils.isNotEmpty(param.getKeywords())) {
            List<Integer> ids = userRepository.getDoctorIdsByName(param.getKeywords());
            if (CollectionUtils.isEmpty(ids)) {
                return 0L;
            }
            query.field("userId").in(ids);
        }
        return query.countAll();
    }

}
