package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.NotSaved;

@Entity(value = "c_group_user", noClassnameStored = true)
public class GroupUser2 {
    /**
     * Id
     */
    @Id
    private ObjectId id;

    /**
     * 用户Id
     */
    private Integer doctorId;

    /**
     * 集团Id或公司Id
     */
    private String objectId;

    /**
     * 账户类型    1：公司用户   2：集团用户
     */
    private Integer type;

    /**
     * 状态	I：邀请待通过，C：正常使用， S：已离职，N：拒绝邀请
     */
    private String status;

    /**
     * 创建人
     */
    private Integer creator;

    /**
     * 创建时间
     */
    private Long creatorDate;

    /**
     * 更新人
     */
    private Integer updator;

    /**
     * 更新时间
     */
    private Long updatorDate;

    /**
     * root=超级管理员，admin=普通管理员
     */
    private String rootAdmin;

    /**
     * 姓名
     */
    private @NotSaved String name;
    /**
     * 头像
     */
    private @NotSaved String headPicFileName;

    @NotSaved
    private User user;
    @NotSaved
    private Group2 group;

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getCreator() {
        return creator;
    }

    public void setCreator(Integer creator) {
        this.creator = creator;
    }

    public Long getCreatorDate() {
        return creatorDate;
    }

    public void setCreatorDate(Long creatorDate) {
        this.creatorDate = creatorDate;
    }

    public Integer getUpdator() {
        return updator;
    }

    public void setUpdator(Integer updator) {
        this.updator = updator;
    }

    public Long getUpdatorDate() {
        return updatorDate;
    }

    public void setUpdatorDate(Long updatorDate) {
        this.updatorDate = updatorDate;
    }

    public String getRootAdmin() {
        return rootAdmin;
    }

    public void setRootAdmin(String rootAdmin) {
        this.rootAdmin = rootAdmin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadPicFileName() {
        return headPicFileName;
    }

    public void setHeadPicFileName(String headPicFileName) {
        this.headPicFileName = headPicFileName;
    }
}
