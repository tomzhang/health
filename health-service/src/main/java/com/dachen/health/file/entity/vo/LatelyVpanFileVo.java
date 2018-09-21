package com.dachen.health.file.entity.vo;

/**
 * @author wangl
 * @desc
 * @date:2017/10/1710:46
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public class LatelyVpanFileVo {
    public String id;
    public String name;
    public String suffix;
    public String mimeType;
    public Long size;
    public String url;
    public String type;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSuffix() {
        return suffix;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "LatelyVpanFileVo{" +
            "id='" + id + '\'' +
            ", name='" + name + '\'' +
            ", suffix='" + suffix + '\'' +
            ", mimeType='" + mimeType + '\'' +
            ", size=" + size +
            ", url='" + url + '\'' +
            ", type='" + type + '\'' +
            '}';
    }
}
