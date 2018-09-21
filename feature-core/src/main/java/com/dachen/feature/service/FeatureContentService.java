package com.dachen.feature.service;

import com.dachen.feature.entity.BaseFeature;
import com.dachen.feature.entity.FeatureContent;
import com.dachen.sdk.db.template.ServiceBase;

public interface FeatureContentService extends ServiceBase {

    String findContentById(String id);
    FeatureContent add(BaseFeature baseFeature, String content, String contentHash, String httpUrl);
    FeatureContent update(String id, String content, String contentHash, String httpUrl,
                          String appName, Integer createUserId);

    String hash(String content);
}
