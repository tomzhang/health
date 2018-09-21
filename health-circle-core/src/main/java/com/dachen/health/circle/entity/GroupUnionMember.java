package com.dachen.health.circle.entity;

import com.dachen.health.circle.CircleEnum;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

@Entity(value = "c_group_union_member", noClassnameStored = true)
@Indexes({ @Index(fields = { @Field("unionId"),@Field("groupId") },options=@IndexOptions(unique=true)) })
public class GroupUnionMember {
    @Id
    private ObjectId id;

    private String unionId;

    private String groupId;
    private String groupType;

    /**
     * 是否主体
     */
    private Integer ifMain;

    private Integer createUserId;
    private Long createTime;

    /**
     * 来源类型，0：医联体创建，1：申请，2：邀请
     */
    private Integer fromType;
    /**
     * 来源id
     */
    private String fromId;

    @NotSaved
    private Group2 group;
    @NotSaved
    private GroupUnion union;
    @NotSaved
    private Boolean ifCanRemove;

    public Boolean getIfCanRemove() {
        return ifCanRemove;
    }

    public void setIfCanRemove(Boolean ifCanRemove) {
        this.ifCanRemove = ifCanRemove;
    }

    public GroupUnionMember() {
    }

    public GroupUnionMember(GroupUnion groupUnion, Group2 group, boolean ifMain, CircleEnum.GroupUnionMemberFromTypeEnum fromType, String fromId) {
        this.unionId = groupUnion.getId().toString();
        this.groupId = group.getId().toString();
        this.groupType = group.getType();
        this.ifMain = ifMain?1:0;
        this.fromType = fromType.getIndex();
        this.fromId = fromId;

        this.union = groupUnion;
        this.group = group;
    }

    public Integer getFromType() {
        return fromType;
    }

    public void setFromType(Integer fromType) {
        this.fromType = fromType;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public GroupUnion getUnion() {
        return union;
    }

    public void setUnion(GroupUnion union) {
        this.union = union;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getUnionId() {
        return unionId;
    }

    public void setUnionId(String unionId) {
        this.unionId = unionId;
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

    public Group2 getGroup() {
        return group;
    }

    public void setGroup(Group2 group) {
        this.group = group;
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

    public Integer getIfMain() {
        return ifMain;
    }

    public void setIfMain(Integer ifMain) {
        this.ifMain = ifMain;
    }
}
