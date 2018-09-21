package com.dachen.feature.entity;


import com.dachen.feature.FeatureEnum;
import com.dachen.sdk.exception.ServiceException;
import org.apache.commons.lang.StringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Id;

public abstract class BaseFeature {

    @Id
    private ObjectId id;

    private String appName;

    private String title;

    private Integer kindId;

    /**
     * 0就绪中，2发布，9删除
     */
    private Integer statusId;

    private Integer weight;

    private Integer ifTop;
    private Long topTime;
    private Integer topUserId;

    private Long createTime;
    private Integer createUserId;
    private Long updateTime;
    private Integer updateUserId;
    private Long publishTime;
    private Integer publishUserId;
    private Long deleteTime;
    private Integer deleteUserId;

    private Integer totalView = 0;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getKindId() {
        return kindId;
    }

    public void setKindId(Integer kindId) {
        this.kindId = kindId;
    }

    public Integer getIfTop() {
        return ifTop;
    }

    public void setIfTop(Integer ifTop) {
        this.ifTop = ifTop;
    }

    public Long getTopTime() {
        return topTime;
    }

    public void setTopTime(Long topTime) {
        this.topTime = topTime;
    }

    public Integer getTopUserId() {
        return topUserId;
    }

    public void setTopUserId(Integer topUserId) {
        this.topUserId = topUserId;
    }

    public Long getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Long deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Integer getDeleteUserId() {
        return deleteUserId;
    }

    public void setDeleteUserId(Integer deleteUserId) {
        this.deleteUserId = deleteUserId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(Integer updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
    }

    public Integer getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(Integer publishUserId) {
        this.publishUserId = publishUserId;
    }

    public void setStatusPrepared(Integer userId) {
        this.statusId = FeatureEnum.FeatureStatusEnum.Prepared.getId();
        this.createTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.updateUserId = userId;
        this.createUserId = userId;
    }

    public void setStatusPublished(Integer userId) {
        this.statusId = FeatureEnum.FeatureStatusEnum.Published.getId();
        this.createTime = System.currentTimeMillis();
        this.publishTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.createUserId = userId;
        this.updateUserId = userId;
        this.publishUserId = userId;
    }

    public void setStatusCancelPublished(Integer userId) {
        this.statusId = FeatureEnum.FeatureStatusEnum.Prepared.getId();
        this.publishTime = System.currentTimeMillis();
        this.updateTime = System.currentTimeMillis();
        this.updateUserId = userId;
        this.publishUserId = userId;
    }

    public void setKind(FeatureEnum.FeatureKindEnum kind) {
        this.kindId = kind.getId();
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getTotalView() {
        return totalView;
    }

    public void setTotalView(Integer totalView) {
        this.totalView = totalView;
    }

    public void checkData() {
        if (StringUtils.isBlank(this.appName)) {
            throw new ServiceException("缺少appName");
        }
        if (null == this.kindId) {
            throw new ServiceException("缺少kindId");
        }
        if (StringUtils.isBlank(this.title)) {
            throw new ServiceException("缺少title");
        }
        if (null == this.createUserId) {
            throw new ServiceException("缺少createUserId");
        }
        if (null == this.weight) {
            throw new ServiceException("缺少weight");
        }
    }
}
