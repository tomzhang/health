package com.dachen.health.circle.form;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

import java.io.Serializable;

@Scope("prototype")
public class GroupTrendUpdateForm implements Serializable {

    @NotEmpty(message = "title is empty")
    @Length(max = 64, message = "title too long")
    private String title;

    @NotEmpty(message = "picUrl is empty")
    private String picUrl;

    @NotEmpty(message = "summary is empty")
    private String summary;

    @NotEmpty(message = "content is empty")
    private String content;

    private String videosJson;

    private String attachmentsJson;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getVideosJson() {
        return videosJson;
    }

    public void setVideosJson(String videosJson) {
        this.videosJson = videosJson;
    }

    public String getAttachmentsJson() {
        return attachmentsJson;
    }

    public void setAttachmentsJson(String attachmentsJson) {
        this.attachmentsJson = attachmentsJson;
    }
}
