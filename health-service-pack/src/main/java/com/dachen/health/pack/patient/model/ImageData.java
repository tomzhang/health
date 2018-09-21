package com.dachen.health.pack.patient.model;

/**
 * 病例图像关联表
 * @author 李淼淼
 * @version 1.0 2015-09-22
 */
public class ImageData {
    /**
     * 主键
     */
    private Integer id;

    /**
     * 关联id
     */
    private Integer relationId;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 图像路径
     */
    private String imageUrl;

    /**
     * 图像类型
     * @see com.dachen.health.commons.constants.ImageDataEnum
     */
    private Integer imageType;

    /**
     * 时长
     */
    private Long timeLong;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取关联id
     *
     * @return relation_id - 关联id
     */
    public Integer getRelationId() {
        return relationId;
    }

    /**
     * 设置关联id
     *
     * @param relationId 关联id
     */
    public void setRelationId(Integer relationId) {
        this.relationId = relationId;
    }

    /**
     * 获取用户id
     *
     * @return user_id - 用户id
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     *
     * @param userId 用户id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取图像路径
     *
     * @return image_url - 图像路径
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * 设置图像路径
     *
     * @param imageUrl 图像路径
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl == null ? null : imageUrl.trim();
    }

    /**
     * 获取图像类型
     *
     * @return image_type - 图像类型
     */
    public Integer getImageType() {
        return imageType;
    }

    /**
     * 设置图像类型
     *
     * @param imageType 图像类型
     */
    public void setImageType(Integer imageType) {
        this.imageType = imageType;
    }

    /**
     * 获取时长
     *
     * @return time_long - 时长
     */
    public Long getTimeLong() {
        return timeLong;
    }

    /**
     * 设置时长
     *
     * @param timeLong 时长
     */
    public void setTimeLong(Long timeLong) {
        this.timeLong = timeLong;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        ImageData other = (ImageData) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getRelationId() == null ? other.getRelationId() == null : this.getRelationId().equals(other.getRelationId()))
            && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
            && (this.getImageUrl() == null ? other.getImageUrl() == null : this.getImageUrl().equals(other.getImageUrl()))
            && (this.getImageType() == null ? other.getImageType() == null : this.getImageType().equals(other.getImageType()))
            && (this.getTimeLong() == null ? other.getTimeLong() == null : this.getTimeLong().equals(other.getTimeLong()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getRelationId() == null) ? 0 : getRelationId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getImageUrl() == null) ? 0 : getImageUrl().hashCode());
        result = prime * result + ((getImageType() == null) ? 0 : getImageType().hashCode());
        result = prime * result + ((getTimeLong() == null) ? 0 : getTimeLong().hashCode());
        return result;
    }
}