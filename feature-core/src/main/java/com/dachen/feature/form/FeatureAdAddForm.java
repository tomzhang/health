package com.dachen.feature.form;


import com.dachen.feature.FeatureEnum;
import com.dachen.feature.entity.FeatureAd;
import com.dachen.sdk.exception.ServiceException;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.util.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Scope("prototype")
public class FeatureAdAddForm {
    @NotEmpty(message = "title is blank")
    private String title;

    @NotEmpty(message = "picUrl is blank")
    private String picUrl;

    @NotNull(message = "isShowPicInText is null")
    private Boolean ifShowPicInText;

    @NotNull(message = "objectKindId is null")
    private Integer objectKindId;

    private String objectValue;
    private String objectTitle;

    private String httpUrl;
    private String content;

    /**
     * 0表示平台banner，1表示科室banner
     */
    private Integer bannerType;

    /**
     * 当bannerType = 1时，即为科室banner时，range保存科室列表
     */
    protected List<String> range;

    public FeatureAd toAdAndCheck() {
        FeatureAd tmp = new FeatureAd();
        tmp.setTitle(this.title);
        tmp.setPicUrl(this.picUrl);
        tmp.setIfShowPicInText(this.ifShowPicInText);

        tmp.setObjectKindId(this.objectKindId);
        tmp.setObjectValue(this.objectValue);
        tmp.setObjectTitle(this.objectTitle);

        tmp.setHttpUrl(this.httpUrl);
        tmp.setContent(this.content);
        tmp.setBannerType(this.bannerType);
        tmp.setRange(this.range);


        if (FeatureEnum.FeatureObjectKindIdEnum.RecArticle != FeatureEnum.FeatureObjectKindIdEnum.eval(tmp.getObjectKindId()) && tmp.getObjectKindId()<=3) {
            tmp.setObjectValue(null);
            tmp.setObjectTitle(null);
        }

        check(tmp);
        return tmp;
    }

    protected void check(FeatureAd tmp) {
        if(Objects.isNull(tmp.getBannerType())){
            throw new ServiceException("缺少bannerType参数");
        }
        if(tmp.getBannerType().equals(1)){
            if(CollectionUtils.isEmpty(tmp.getRange())){
                throw new ServiceException("缺少range参数");
            }
        }

        Integer objectKindId = tmp.getObjectKindId()>3?4:tmp.getObjectKindId();
        switch (FeatureEnum.FeatureObjectKindIdEnum.eval(objectKindId)) {
            case HttpUrl:
                if (com.dachen.util.StringUtils.isBlank(tmp.getHttpUrl())) {
                    throw new ServiceException("缺少httpUrl参数");
                }
                break;
            case RichText:
                if (com.dachen.util.StringUtils.isBlank(tmp.getContent())) {
                    throw new ServiceException("缺少content参数");
                }
                break;
            case RecArticle:
                if (com.dachen.util.StringUtils.isBlank(tmp.getObjectValue()) || com.dachen.util.StringUtils.isBlank(tmp.getObjectTitle())) {
                    throw new ServiceException("缺少objectValue或objectTitle参数");
                }
                break;
            case OtherType:
                if (com.dachen.util.StringUtils.isBlank(tmp.getObjectValue()) || com.dachen.util.StringUtils.isBlank(tmp.getObjectTitle())) {
                    throw new ServiceException("缺少objectValue或objectTitle参数");
                }
        }
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

    public Integer getObjectKindId() {
        return objectKindId;
    }

    public void setObjectKindId(Integer objectKindId) {
        this.objectKindId = objectKindId;
    }

    public String getObjectTitle() {
        return objectTitle;
    }

    public void setObjectTitle(String objectTitle) {
        this.objectTitle = objectTitle;
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

    @Override
    public String toString() {
        return "FeatureAdAddForm{" +
                "title='" + title + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", ifShowPicInText=" + ifShowPicInText +
                ", objectKindId=" + objectKindId +
                ", objectValue='" + objectValue + '\'' +
                ", objectTitle='" + objectTitle + '\'' +
                '}';
    }
}
