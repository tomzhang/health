package com.dachen.feature.service.impl;

import com.dachen.feature.entity.BaseFeature;
import com.dachen.feature.entity.FeatureContent;
import com.dachen.feature.service.FeatureContentService;
import com.dachen.sdk.annotation.Model;
import org.apache.commons.codec.digest.DigestUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

@Model(FeatureContent.class)
@Service
public class FeatureContentServiceImpl extends BaseServiceImpl implements FeatureContentService {

    @Override
    public String findContentById(String id) {
        Query<FeatureContent> query = this.createQueryByPK(id);
        query.retrievedFields(true, "content");
        FeatureContent featureContent = query.get();
        if (null == featureContent) {
            return null;
        }
        return featureContent.getContent();
    }

    @Override
    public FeatureContent add(BaseFeature baseFeature, String content, String contentHash, String httpUrl) {
        FeatureContent tmp = new FeatureContent();
        tmp.setFeatureId(baseFeature.getId());
        tmp.setAppName(baseFeature.getAppName());

        tmp.setContent(content);
        tmp.setContentHash(contentHash);
        tmp.setHttpUrl(httpUrl);

        tmp.setCreateTime(System.currentTimeMillis());
        tmp.setCreateUserId(baseFeature.getCreateUserId());
        FeatureContent featureContent = this.saveEntityAndFind(tmp);
        return featureContent;
    }

    @Override
    public FeatureContent update(String id, String content, String contentHash, String httpUrl,
                                 String appName, Integer createUserId) {
        FeatureContent dbItem = this.findById(id);
        if (dbItem == null) {//从其他类型改成“富文本”
            dbItem = new FeatureContent();
            dbItem.setFeatureId(new ObjectId(id));
            dbItem.setAppName(appName);
            dbItem.setCreateTime(System.currentTimeMillis());
            dbItem.setCreateUserId(createUserId);
        }
        dbItem.setContent(content);
        dbItem.setContentHash(contentHash);
        dbItem.setHttpUrl(httpUrl);
        return this.saveEntityAndFind(dbItem);
    }

    @Override
    public String hash(String content) {
        return DigestUtils.md5Hex(content);
    }



}
