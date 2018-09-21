package com.dachen.feature.service;

import com.dachen.feature.entity.FeatureAd;
import com.dachen.feature.entity.FeatureAdPreview;
import com.dachen.feature.form.FeatureAdAddForm;
import com.dachen.feature.vo.MobileFeatureAdVO;
import com.dachen.health.base.entity.vo.DeptVO;
import com.dachen.sdk.page.Pagination;

import java.util.List;
import java.util.Map;

public interface FeatureAdService extends BaseFeatureService {

    FeatureAd add(Integer currentUserId, String appName, FeatureAdAddForm form) throws Exception;
    FeatureAd update(Integer currentUserId, String appName, String id, FeatureAdAddForm form) throws Exception;

    void updateStaticHtmlAsync(String id);

    void reGenerateStaticAll();

    Pagination<FeatureAd> findPage(Integer currentUserId, String appName,
                                   String kw, Integer bannerType,String deptId,
                                   Integer pageIndex, Integer pageSize);

    FeatureAd findById(Integer currentUserId, String appName, String id);

    List<MobileFeatureAdVO> findListAndVO(Integer currentUserId, String appName, Long ts);
    MobileFeatureAdVO viewItemAndVO(Integer currentUserId, String appName, String id);

    MobileFeatureAdVO viewItemAndVOInfo(Integer currentUserId, String appName, String id);

    Map<String, String> preview(Integer currentUserId, String appName, FeatureAdAddForm form);

    FeatureAdPreview previewDetail(String id);

    /**
     * 检查当前科室是否已有banner
     * @param deptIds
     * @return
     */
    List<DeptVO> ifDeptHasBanner(List<String> deptIds);

    /**
     * 科室banner添加显示
     * @param currentUserId
     * @param appName
     * @param kw
     * @param deptId
     * @param pageIndex
     * @param pageSize
     * @return
     */
    Pagination<FeatureAd> addDeptDisplay(Integer currentUserId, String appName, String kw,String deptId,
                                   Integer pageIndex, Integer pageSize);

    /**
     * （faq调用）广告系统查看是否引用banner，若引用则不可下架
     * @param value
     * @return
     */
    public FeatureAd getByObjectValue(String value,String appName);

}
