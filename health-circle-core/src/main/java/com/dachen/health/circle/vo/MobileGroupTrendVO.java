package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.*;
import com.dachen.sdk.util.SdkUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MobileGroupTrendVO implements Serializable {

    private String trendId;
    private String title;
    private Integer userId;
    private String picUrl;
    private String summary;
    private String content;
    private Long createTime;

    private Integer status;

    /**
     * 资料附件
     */
    private List<Attachment> attachments;
    /**
     * 资料视频说明
     */
    private List<Video> videos;

    private Integer totalComment;
    private Integer totalLike;
    private Integer totalCredit;

    private MobileDoctorVO doctor;
    private List<MobileGroupTrendLikeVO> recentLikeList;
//    private MobileGroupTrendLikeVO like;
    private Boolean ifLike = false;

    private List<MobileGroupTrendCreditVO> recentCreditList;
    private Boolean ifCredit = false;

    private MobileGroupVO group;

    public MobileGroupTrendVO() {
    }

    public MobileGroupTrendVO(GroupTrend groupTrend) {
        this.trendId = groupTrend.getId().toString();
        this.title = groupTrend.getTitle();
        this.userId = groupTrend.getUserId();
        this.picUrl = groupTrend.getPicUrl();
        this.summary = groupTrend.getSummary();
        this.content = groupTrend.getContent();
        this.createTime = groupTrend.getStatusTime();
        this.status = groupTrend.getStatusId();
        this.videos = groupTrend.getVideos();
        this.attachments = groupTrend.getAttachments();
        this.totalComment = groupTrend.getTotalComment();
        this.totalLike = groupTrend.getTotalLike();
        this.totalCredit = groupTrend.getTotalCredit();
        if (null != groupTrend.getGroup2()) {
            this.group = new MobileGroupVO(groupTrend.getGroup2());
        }
        if (null != groupTrend.getUser()) {
            this.doctor = new MobileDoctorVO(groupTrend.getUser());
        }
        if (SdkUtils.isNotEmpty(groupTrend.getRecentLikeList())) {
            this.recentLikeList = new ArrayList<>(groupTrend.getRecentLikeList().size());
            for (GroupTrendLike like:groupTrend.getRecentLikeList()) {
                this.recentLikeList.add(new MobileGroupTrendLikeVO(like));
            }
        }
        if (SdkUtils.isNotEmpty(groupTrend.getRecentCreditList())) {
            this.recentCreditList = new ArrayList<>(groupTrend.getRecentCreditList().size());
            for (GroupTrendCredit credit:groupTrend.getRecentCreditList()) {
                this.recentCreditList.add(new MobileGroupTrendCreditVO(credit));
            }
        }
        if (null != groupTrend.getLike()) {
//            this.like = new MobileGroupTrendLikeVO(groupTrend.getLike());
            this.ifLike = true;
        }
        if (null != groupTrend.getCredit()) {
//            this.like = new MobileGroupTrendLikeVO(groupTrend.getLike());
            this.ifCredit = true;
        }
    }

    public MobileGroupVO getGroup() {
        return group;
    }

    public void setGroup(MobileGroupVO group) {
        this.group = group;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Integer totalCredit) {
        this.totalCredit = totalCredit;
    }

    public List<MobileGroupTrendCreditVO> getRecentCreditList() {
        return recentCreditList;
    }

    public void setRecentCreditList(List<MobileGroupTrendCreditVO> recentCreditList) {
        this.recentCreditList = recentCreditList;
    }

    public Boolean getIfCredit() {
        return ifCredit;
    }

    public void setIfCredit(Boolean ifCredit) {
        this.ifCredit = ifCredit;
    }

    public Boolean getIfLike() {
        return ifLike;
    }

    public void setIfLike(Boolean ifLike) {
        this.ifLike = ifLike;
    }

//    public MobileGroupTrendLikeVO getLike() {
//        return like;
//    }
//
//    public void setLike(MobileGroupTrendLikeVO like) {
//        this.like = like;
//    }


    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(Integer totalLike) {
        this.totalLike = totalLike;
    }

    public List<MobileGroupTrendLikeVO> getRecentLikeList() {
        return recentLikeList;
    }

    public void setRecentLikeList(List<MobileGroupTrendLikeVO> recentLikeList) {
        this.recentLikeList = recentLikeList;
    }

    public String getTrendId() {
        return trendId;
    }

    public void setTrendId(String trendId) {
        this.trendId = trendId;
    }

    public Integer getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(Integer totalComment) {
        this.totalComment = totalComment;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public MobileDoctorVO getDoctor() {
        return doctor;
    }

    public void setDoctor(MobileDoctorVO doctor) {
        this.doctor = doctor;
    }
}
