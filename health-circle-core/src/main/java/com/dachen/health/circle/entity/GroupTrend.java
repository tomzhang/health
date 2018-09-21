package com.dachen.health.circle.entity;

import com.dachen.health.circle.CircleEnum;
import com.dachen.health.circle.form.GroupTrendAddForm;
import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;

import java.util.ArrayList;
import java.util.List;

@Entity(value = "c_group_trend", noClassnameStored = true)
public class GroupTrend {

    @Id
    private ObjectId id;

    /**
     * 所属的group
     */
    @Indexed
    private String groupId;
    /**
     * 所属的group type（冗余）
     */
    private String groupType;

    private String title;
    private String picUrl;
    private String summary;
    private String content;

    /**
     * 资料附件
     */
    private List<Attachment> attachments;
    /**
     * 资料视频说明
     */
    private List<Video> videos;

    @Indexed
    private Integer statusId;
    private Long statusTime;

    /**
     * 显示的医生
     */
    private Integer userId;

    private Long createTime;
    private Integer createUserId;
    private Long updateTime;
    private Integer updateUserId;

    private Integer totalComment = 0;
    private Integer totalLike = 0;
    /**
     * 打赏总数
     */
    private Integer totalCredit = 0;
    /**
     * 最近点赞ids
     */
    private List<String> recentLikeIdList;

    /**
     * 最近打赏ids
     */
    private List<String> recentCreditIdList;
    // 包装时的参数 end
    @NotSaved
    private User user;
    @NotSaved
    private List<GroupTrendLike> recentLikeList;
    @NotSaved
    private GroupTrendLike like;

    @NotSaved
    private List<GroupTrendCredit> recentCreditList; //最近打赏列表
    @NotSaved
    private GroupTrendCredit credit; //是否打赏
    @NotSaved
    private Group2 group2;
    // 包装时的参数 start

    public GroupTrend() {
    }

    public GroupTrend(GroupTrendAddForm form, List<Video> videos, List<Attachment> attachments) {
        this.groupId =  form.getGroupId();
        this.title = form.getTitle();
        this.picUrl = form.getPicUrl();
        this.summary = form.getSummary();
        this.content = form.getContent();

        this.videos = videos;
        this.attachments = attachments;
    }

    public Group2 getGroup2() {
        return group2;
    }

    public void setGroup2(Group2 group2) {
        this.group2 = group2;
    }

    public Integer getTotalCredit() {
        return totalCredit;
    }

    public void setTotalCredit(Integer totalCredit) {
        this.totalCredit = totalCredit;
    }

    public List<String> getRecentCreditIdList() {
        return recentCreditIdList;
    }

    public void setRecentCreditIdList(List<String> recentCreditIdList) {
        this.recentCreditIdList = recentCreditIdList;
    }

    public GroupTrendLike getLike() {
        return like;
    }

    public void setLike(GroupTrendLike like) {
        this.like = like;
    }

    public List<String> getRecentLikeIdList() {
        return recentLikeIdList;
    }

    public void setRecentLikeIdList(List<String> recentLikeIdList) {
        this.recentLikeIdList = recentLikeIdList;
    }


    public List<GroupTrendLike> getRecentLikeList() {
        return recentLikeList;
    }

    public void setRecentLikeList(List<GroupTrendLike> recentLikeList) {
        this.recentLikeList = recentLikeList;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Long getStatusTime() {
        return statusTime;
    }

    public void setStatusTime(Long statusTime) {
        this.statusTime = statusTime;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
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

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
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

    public Integer getTotalComment() {
        return totalComment;
    }

    public void setTotalComment(Integer totalComment) {
        this.totalComment = totalComment;
    }

    public Integer getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(Integer totalLike) {
        this.totalLike = totalLike;
    }

    public void setStatus(CircleEnum.GroupTrendStatusEnum status) {
        this.statusId = status.getId();
        this.statusTime = System.currentTimeMillis();
    }

    public List<GroupTrendCredit> getRecentCreditList() {
        return recentCreditList;
    }

    public void setRecentCreditList(List<GroupTrendCredit> recentCreditList) {
        this.recentCreditList = recentCreditList;
    }

    public GroupTrendCredit getCredit() {
        return credit;
    }

    public void setCredit(GroupTrendCredit credit) {
        this.credit = credit;
    }

    @Override
    public String toString() {
        return "GroupTrend{" +
                "id=" + id +
                ", groupId='" + groupId + '\'' +
                ", groupType='" + groupType + '\'' +
                ", title='" + title + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", attachments=" + attachments +
                ", videos=" + videos +
                ", statusId=" + statusId +
                ", statusTime=" + statusTime +
                ", userId=" + userId +
                ", createTime=" + createTime +
                ", createUserId=" + createUserId +
                ", updateTime=" + updateTime +
                ", updateUserId=" + updateUserId +
                ", totalComment=" + totalComment +
                ", totalLike=" + totalLike +
                ", totalCredit=" + totalCredit +
                ", recentLikeIdList=" + recentLikeIdList +
                ", recentCreditIdList=" + recentCreditIdList +
                ", user=" + user +
                ", recentLikeList=" + recentLikeList +
                ", like=" + like +
                ", recentCreditList=" + recentCreditList +
                ", credit=" + credit +
                '}';
    }
}
