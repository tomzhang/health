package com.dachen.feature.entity;


import com.dachen.feature.FeatureEnum;
import com.dachen.feature.form.FeatureAdAddForm;
import com.dachen.health.base.entity.vo.DeptVO;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.List;

@Entity(value = "p_app_feature", noClassnameStored = true)
public class FeatureAd extends BaseFeature {

    private String picUrl;
    /**
     * 是否在正文显示图片
     */
    private Boolean ifShowPicInText;

    private Integer objectKindId;
    private String objectValue;
    private String objectTitle;

    private String httpUrl;
    @NotSaved
    private String content;
    private String contentHash;

    /**
     * 0表示平台banner，1表示科室banner
     */
    private Integer bannerType;

    /**
     * 当bannerType = 1时，即为科室banner时，range保存科室列表
     */
    private List<String> range;

    /**
     * 科室的详细信息
     */
    private List<DeptVO> deptsInfo;

    /**
     * 分享地址，内嵌iframe
     */
    private String shareUrl;

    public FeatureAd() {
        setKind(FeatureEnum.FeatureKindEnum.Advertisement);
    }

    public FeatureAd(FeatureAdAddForm form) {
        this.setTitle(form.getTitle());
        this.picUrl = form.getPicUrl();
        this.ifShowPicInText = form.getIfShowPicInText();
        this.objectKindId = form.getObjectKindId();
        this.objectValue = form.getObjectValue();
        this.objectTitle = form.getObjectTitle();
    }

    public String getContentHash() {
        return contentHash;
    }

    public void setContentHash(String contentHash) {
        this.contentHash = contentHash;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getObjectTitle() {
        return objectTitle;
    }

    public void setObjectTitle(String objectTitle) {
        this.objectTitle = objectTitle;
    }

    public Integer getObjectKindId() {
        return objectKindId;
    }

    public void setObjectKindId(Integer objectKindId) {
        this.objectKindId = objectKindId;
    }

    public Boolean getIfShowPicInText() {
        return ifShowPicInText;
    }

    public void setIfShowPicInText(Boolean ifShowPicInText) {
        this.ifShowPicInText = ifShowPicInText;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    public Integer getBannerType() {
        return bannerType;
    }

    public void setBannerType(Integer bannerType) {
        this.bannerType = bannerType;
    }

    public List<String> getRange() {
        return range;
    }

    public void setRange(List<String> range) {
        this.range = range;
    }

    public List<DeptVO> getDeptsInfo() {
        return deptsInfo;
    }

    public void setDeptsInfo(List<DeptVO> deptsInfo) {
        this.deptsInfo = deptsInfo;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
