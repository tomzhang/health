package com.dachen.feature.vo;

import com.dachen.feature.entity.FeatureAd;

public class MobileFeatureAdVO {

    private String id;
    private String title;
    private String picUrl;
    private Boolean ifShowPicInText;
    private Long publishTime;
    private Integer totalView = 0;

    private Integer objectKindId;
    private String objectValue;
    private String objectTitle;

    private String httpUrl;
    private String content;

    private String shareUrl;

    public MobileFeatureAdVO() {
    }

    public MobileFeatureAdVO(FeatureAd ad) {
        this.id = ad.getId().toString();
        this.title = ad.getTitle();
        this.picUrl = ad.getPicUrl();
        this.ifShowPicInText = ad.getIfShowPicInText();
        this.publishTime = ad.getPublishTime();
        this.totalView = ad.getTotalView();
        this.objectKindId = ad.getObjectKindId();
        this.objectValue = ad.getObjectValue();
        this.objectTitle = ad.getObjectTitle();

        this.httpUrl = ad.getHttpUrl();
        this.content = ad.getContent();
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public Boolean getIfShowPicInText() {
        return ifShowPicInText;
    }

    public void setIfShowPicInText(Boolean ifShowPicInText) {
        this.ifShowPicInText = ifShowPicInText;
    }

    public Integer getObjectKindId() {
        return objectKindId;
    }

    public void setObjectKindId(Integer objectKindId) {
        this.objectKindId = objectKindId;
    }

    public String getObjectValue() {
        return objectValue;
    }

    public void setObjectValue(String objectValue) {
        this.objectValue = objectValue;
    }

    public String getObjectTitle() {
        return objectTitle;
    }

    public void setObjectTitle(String objectTitle) {
        this.objectTitle = objectTitle;
    }

    public Integer getTotalView() {
        return totalView;
    }

    public void setTotalView(Integer totalView) {
        this.totalView = totalView;
    }

    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }
}
