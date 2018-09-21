package com.dachen.health.circle.form;

import com.dachen.health.circle.entity.Attachment;
import com.dachen.health.circle.entity.GroupTrend;
import com.dachen.health.circle.entity.Video;
import com.dachen.sdk.util.SdkJsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

import java.io.Serializable;
import java.util.List;

@Scope("prototype")
public class GroupTrendAddForm implements Serializable {

    @NotEmpty(message = "groupId is empty")
    private String groupId;

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

    public GroupTrend toGroupTrend() {
        List<Video> videoList = null;
        if (StringUtils.isNotBlank(videosJson)) {
            videoList = SdkJsonUtils.parseList(Video.class, this.videosJson);
        }
        List<Attachment> attachmentList = null;
        if (StringUtils.isNotBlank(attachmentsJson)) {
            attachmentList = SdkJsonUtils.parseList(Attachment.class, this.attachmentsJson);
        }
        GroupTrend groupTrendTemp = new GroupTrend(this, videoList, attachmentList);
        return groupTrendTemp;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
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
