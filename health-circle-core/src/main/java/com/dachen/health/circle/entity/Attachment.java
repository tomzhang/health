package com.dachen.health.circle.entity;

public class Attachment {
    /**
     * 名称
     */
    private String name;

    /**
     * 附件类别
     */
    private Integer type;
    /**
     * 说明
     */
    private String explain;
    /**
     * 链接地址
     */
    private String link;

    private Files file;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Files getFile() {
        return file;
    }

    public void setFile(Files file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
