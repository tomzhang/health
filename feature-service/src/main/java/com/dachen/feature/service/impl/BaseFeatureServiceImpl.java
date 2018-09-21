package com.dachen.feature.service.impl;

import com.dachen.feature.FeatureEnum;
import com.dachen.feature.FeatureEnum.FeatureStatusEnum;
import com.dachen.feature.FeatureEnum.FeatureKindEnum;
import com.dachen.feature.entity.BaseFeature;
import com.dachen.feature.entity.FeatureAd;
import com.dachen.feature.service.BaseFeatureService;
import com.dachen.feature.service.FeatureContentService;
import com.dachen.sdk.exception.ServiceException;
import com.dachen.sdk.page.Pagination;
import com.dachen.sdk.util.SdkUtils;
import com.dachen.util.StringUtils;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.regex.Pattern;

public abstract class BaseFeatureServiceImpl extends BaseServiceImpl implements BaseFeatureService {

    @Autowired
    protected FeatureContentService featureContentService;

    @Override
    public <T extends BaseFeature> T findNormalById(String id) {
        T o = this.findById(id);
        if(null==o){
            throw new ServiceException("记录不存在：" + id);
        } else if(FeatureStatusEnum.Deleted.getId().equals(o.getStatusId())) {
            throw new ServiceException("记录不存在：" + id);
        }
        return o;
    }

    @Override
    public <T> String saveEntity(T entity) {
        BaseFeature baseFeature = (BaseFeature) entity;
        baseFeature.checkData();
        return super.saveEntity(entity);
    }

    @Override
    public <T> T saveEntityAndFind(T entity) {
        BaseFeature baseFeature = (BaseFeature) entity;
        baseFeature.checkData();
        return super.saveEntityAndFind(entity);
    }

    @Override
    public <T> List<String> saveEntityBatch(List<T> entityList) {
        if (SdkUtils.isEmpty(entityList)) {
            return null;
        }
        for (T t:entityList) {
            BaseFeature baseFeature = (BaseFeature) t;
            baseFeature.checkData();
        }
        return super.saveEntityBatch(entityList);
    }

    @Override
    public <T> List<T> saveEntityBatchAndFind(List<T> entityList) {
        if (SdkUtils.isEmpty(entityList)) {
            return null;
        }
        for (T t:entityList) {
            BaseFeature baseFeature = (BaseFeature) t;
            baseFeature.checkData();
        }
        return super.saveEntityBatchAndFind(entityList);
    }

    @Override
    public <T extends BaseFeature> Pagination<T> findPage(String appName, String kw,
                                                          Integer bannerType, String deptId,
                                                          int pageIndex, int pageSize) {
        Query<T> query =  this.createQuery();
        query.field("appName").equal(appName).field("kindId").equal(getKindId().getId());
        if(Objects.nonNull(bannerType) && bannerType.equals(1)){
            query.field("statusId").equal(FeatureStatusEnum.Published.getId());
        }else {
            query.field("statusId").notEqual(FeatureStatusEnum.Deleted.getId());
        }
        query.field("bannerType").equal(bannerType);
        if(StringUtils.isNotBlank(kw)){
            Pattern pattern = Pattern.compile("^.*" + kw + ".*$", Pattern.CASE_INSENSITIVE);
            query.field("title").equal(pattern);
        }
        if(StringUtils.isNotBlank(deptId)){
            Set<String> deptIds = new HashSet<>();
            deptIds.add(deptId);
            query.field("range").hasAnyOf(deptIds);
        }
        query.order("-statusId, -weight, -createTime");
        query.offset(pageIndex*pageSize).limit(pageSize);

        Pagination<T> pagination = new Pagination<>(query.asList(), query.countAll(), pageIndex, pageSize);
        return pagination;
    }

    @Override
    public <T extends BaseFeature> Pagination<T> findPage(String appName, int pageIndex, int pageSize) {
        Query<T> query =  this.createQuery();
        query.field("appName").equal(appName).field("kindId").equal(getKindId().getId());
//        query.field("statusId").equal(statusEnum.getId());
        query.order("-weight, -createTime");
        query.offset(pageIndex*pageSize).limit(pageSize);

        Pagination<T> pagination = new Pagination<>(query.asList(), query.countAll(), pageIndex, pageSize);
        return pagination;
    }

    public <T extends BaseFeature> List<T> findList(String appName, Long ts) {
        Query<T> query = this.createQuery();
        query.field("appName").equal(appName).field("kindId").equal(getKindId().getId());
        if (0 == ts) {
            query.field("statusId").equal(FeatureStatusEnum.Published.getId());
            query.order("-weight, -createTime");
            query.limit(5);
        } else {
            query.field("updateTime").greaterThan(ts);
        }
        return query.asList();
    }

    @Override
    public <T extends BaseFeature> Integer nextPublishWeight(String appName) {
        Query<T> query =  this.createQuery();
        query.field("appName").equal(appName).field("kindId").equal(getKindId().getId());
        query.field("statusId").equal(FeatureStatusEnum.Published.getId());
        query.order("-weight");
        query.limit(1);
        query.retrievedFields(true, "weight");

        T t = query.get();
        if (null == t) {
            Integer prepareWeight = nextPrepareWeight(appName);
            return prepareWeight + 100; // publish的weight比prepare的weight大一个阀值（暂定100）
        }

        return t.getWeight()+1;
    }

    @Override
    public <T extends BaseFeature> Integer nextPrepareWeight(String appName) {
        Query<T> query =  this.createQuery();
        query.field("appName").equal(appName).field("kindId").equal(getKindId().getId());
        query.field("statusId").equal(FeatureStatusEnum.Prepared.getId());
        query.order("-weight");
        query.limit(1);
        query.retrievedFields(true, "weight");

        T t = query.get();
        if (null == t) {
            return 1;
        }

        return t.getWeight()+1;
    }

    @Override
    public <T extends BaseFeature> List<T> exchange(String appName, String id1, String id2) {
        List<String> idList = new ArrayList<>(2);
        idList.add(id1);
        idList.add(id2);

        List<T> list = this.findByIds(idList);
        // check data
        for (T t: list) {
            if (!t.getStatusId().equals(FeatureStatusEnum.Published.getId())) {
                throw new ServiceException("id1 not published!");
            }
            if (!t.getStatusId().equals(FeatureStatusEnum.Published.getId())) {
                throw new ServiceException("id2 not published!");
            }
        }

        // exchange
        int temp = list.get(0).getWeight();
        list.get(0).setWeight(list.get(1).getWeight());
        list.get(1).setWeight(temp);

        return this.saveEntityBatchAndFind(list);
    }

    @Override
    public <T extends BaseFeature> T publish(Integer currentUserId, String appName, String id,int bannerType) {
        T dbItem = this.findNormalById(id);
        if (dbItem.getStatusId().equals(FeatureStatusEnum.Published.getId())) {
            return dbItem;
        }
        dbItem.setWeight(this.nextPublishWeight(appName));
        dbItem.setStatusPublished(currentUserId);
        return this.saveEntityAndFind(dbItem);
    }

    @Override
    public <T extends BaseFeature> T cancelPublish(Integer currentUserId, String appName, String id,int bannerType) {
        T dbItem = this.findNormalById(id);
        if (dbItem.getStatusId().equals(FeatureStatusEnum.Prepared.getId())) {
            return dbItem;
        }
        dbItem.setWeight(this.nextPrepareWeight(appName));
        dbItem.setStatusCancelPublished(currentUserId);
        return this.saveEntityAndFind(dbItem);
    }

    @Override
    public <T extends BaseFeature> T deletePublish(String id,Integer currentUserId) {
        T dbItem = this.findNormalById(id);
        dbItem.setStatusId(FeatureStatusEnum.Deleted.getId());
        dbItem.setUpdateTime(System.currentTimeMillis());
        dbItem.setUpdateUserId(currentUserId);
        return super.saveEntityAndFind(dbItem);
    }
}
