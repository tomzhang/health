package com.dachen.health.commons.dao;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.vo.ConcernDept;
import com.dachen.health.commons.vo.User;
import com.mongodb.BasicDBObject;

import org.apache.commons.collections.CollectionUtils;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by sharp on 2017/11/28.
 */
@Repository
public class ConcernDeptRepository extends NoSqlRepository {

    static final Logger logger = LoggerFactory.getLogger(ConcernDeptRepository.class);
    @Autowired
    private UserRepository userRepository;

    public List<String> findDeptIds(Integer userId) {
        long time1 = System.currentTimeMillis();
        ConcernDept userConcernDept = dsForRW.createQuery(ConcernDept.class).filter("_id", userId).get();

        List<String> deptIds = new ArrayList<>();
        if (userConcernDept == null) {
            // 初始时关注 心血管内科，消化内科，神经内科，眼科
            deptIds.addAll(Arrays.asList("NKAA", "NKAC", "NKAB", "YK"));
        } else {
            deptIds.addAll(userConcernDept.getDeptIds());
        }
        boolean remove = false;

        long time2 = System.currentTimeMillis();
        logger.info("######.findConcernDept cost {}ms", time2-time1);

        User user = userRepository.getUserWithFields(userId, "doctor.deptId");
        long time3 = System.currentTimeMillis();
        logger.info("######.findUser cost {}ms", time3-time2);

        if (user != null && user.getDoctor() != null && user.getDoctor().getDeptId() != null) {
            if (deptIds.contains(user.getDoctor().getDeptId())) {
                deptIds.remove(user.getDoctor().getDeptId());
                logger.info("删除本身科室：{}", user.getDoctor().getDeptId());
                remove = true;
            }
        }
        if (remove || userConcernDept == null) {
            this.setDepts(userId, deptIds);
            logger.info("######.findAndModify...");
        }
        logger.info("######.findAndModify cost {}ms", System.currentTimeMillis()-time3);
        return deptIds;
    }

    public ConcernDept setDepts(Integer userId, List<String> deptIds) {
        Query<ConcernDept> query = dsForRW.createQuery(ConcernDept.class).filter("_id", userId);
        UpdateOperations<ConcernDept> ops = dsForRW.createUpdateOperations(ConcernDept.class);
        ops.set("deptIds", deptIds);
        return dsForRW.findAndModify(query, ops, false, true);
    }
    
    public List<String> findUserIds(String deptId, int pageIndex, int pageSize) {
        BasicDBObject filter = new BasicDBObject("deptIds", new BasicDBObject("$in", new String[]{deptId}));
        
        List<ConcernDept> concernDepts = dsForRW.createQuery(ConcernDept.class, filter)
                                                .offset(pageIndex * pageSize).limit(pageSize).asList();
        
        if(CollectionUtils.isEmpty(concernDepts)){
            concernDepts = new ArrayList<>();
        }
        
        Set<String> resultIds = new HashSet<>();
        if(concernDepts.size() != pageSize){
            long concernDeptCount = dsForRW.createQuery(ConcernDept.class, filter).countAll();
            // 分页换算，通过ConcernDept的记录数换算出user中的分页信息，进分页
            int pageIndexCount = (int) (concernDeptCount / pageSize);
            int remainderCount = (int) (concernDeptCount % pageSize);
            //页数从0开始因此(pageIndexCount - 1)
            int pIndex = (concernDeptCount < pageSize && pageIndex == 0) ? 0 : pageIndex - pageIndexCount;
            int pSize = pageSize - concernDepts.size();
            int offset = (pSize < pageSize) ? 0 : 0 - remainderCount;

            List<Long> userIds = userRepository.findUserIdsByDeptId(deptId, pIndex, pSize, offset);
            
            if(CollectionUtils.isNotEmpty(userIds)){
                for(Long id : userIds){
                    resultIds.add(String.valueOf(id));
                }
            }
        }
        
        for(ConcernDept user : concernDepts){
            resultIds.add(String.valueOf(user.getUserId()));
        }
        
        return new ArrayList<>(resultIds);
    }

}
