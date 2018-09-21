package com.dachen.feature.service;

import com.dachen.feature.FeatureEnum.FeatureKindEnum;
import com.dachen.feature.FeatureEnum.FeatureStatusEnum;
import com.dachen.feature.entity.BaseFeature;
import com.dachen.sdk.db.template.ServiceBase;
import com.dachen.sdk.page.Pagination;

import java.util.List;

public interface BaseFeatureService extends ServiceBase {
    FeatureKindEnum getKindId();

    <T extends BaseFeature> T findNormalById(String id);

    <T extends BaseFeature> Pagination<T> findPage(String appName, String kw,
                                                   Integer bannerType,String deptId,
                                                   int pageIndex, int pageSize);

    <T extends BaseFeature> Pagination<T> findPage(String appName,
                                                   int pageIndex, int pageSize);

    <T extends BaseFeature> Integer nextPublishWeight(String appName);
    <T extends BaseFeature> Integer nextPrepareWeight(String appName);

    <T extends BaseFeature> List<T> exchange(String appName, String id1, String id2);

    <T extends BaseFeature> T publish(Integer currentUserId, String appName, String id,int bannerType);

    <T extends BaseFeature> T cancelPublish(Integer currentUserId, String appName, String id,int bannerTypr);

    <T extends BaseFeature> T deletePublish(String id,Integer currentUserId);

    /**
     * 发布科室banner
     * @param currentUserId
     * @param appName
     * @param id
     * @param deptId
     * @param <T>
     * @return
     */
    <T extends BaseFeature> T publishDeptBanner(Integer currentUserId, String appName, String id,String deptId);

    /**
     * 取消科室banner
     * @param currentUserId
     * @param appName
     * @param id
     * @param deptId
     * @param <T>
     * @return
     */
    <T extends BaseFeature> T cancelDeptBanner(Integer currentUserId, String appName, String id,String deptId);

    /**
     * 替换科室banner
     * @param currentUserId
     * @param appName
     * @param deptId
     * @param <T>
     * @return
     */
    <T extends BaseFeature> T replaceDeptBanner(Integer currentUserId, String appName,
                                                String oldId,String newId,String deptId);

}
