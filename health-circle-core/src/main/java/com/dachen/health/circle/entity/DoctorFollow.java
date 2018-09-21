package com.dachen.health.circle.entity;

import com.dachen.health.commons.vo.User;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

/**
 * 我关注的医生
 * Created By lim
 * Date: 2017/7/10
 * Time: 10:37
 * Index: 同一个人，同一个医生 只有一条记录
 */
@Entity(value = "c_doctor_follow", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field("doctorId"),@Field("userId") },options=@IndexOptions(unique=true)) })
public class DoctorFollow {
    @Id
    private ObjectId id;
    /**
     * 关注人
     */
    private Integer userId;
    /**
     * 我关注医生id       被关注人
     */
    private Integer doctorId;


    private Long createTime;

    @NotSaved
    private Group2 group;

    public DoctorFollow() {
    }

    public DoctorFollow(User user) {
        this.userId = user.getUserId();
        this.createTime = System.currentTimeMillis();
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "DoctorFollow{" +
                "id=" + id +
                ", userId=" + userId +
                ", doctorId=" + doctorId +
                ", createTime=" + createTime +
                ", group=" + group +
                '}';
    }
}
