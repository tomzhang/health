package com.dachen.health.circle.form;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.circle.entity.GroupUnion;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

@Scope("prototype")
public class GroupUnionAddForm {

    // 名称：必填，没有填写名称点击创建，提示“请输入名称”。名称最多输入15字符。
    @NotEmpty(message = "name is empty")
    @Length(min = 1, max = 15, message = "name too long")
    private String name;

    // 简介：选填项
    private String intro;

    // 默认logo（UI提供），选填项
    private String logoPicUrl;

    // 选择创建组织：显示我是管理员的科室和圈子。单选，只有一个默认勾选；有多个默认不勾选。没有勾选组织点击“创建”提示：“请选择创建组织“
    @NotEmpty(message = "groupId is empty")
    private String groupId;

    public void check() {
        // TODO: 使用validate
        if (StringUtils.isBlank(groupId)) {
            throw new ServiceException("groupId is Null!");
        }
        if (StringUtils.isBlank(name)) {
            throw new ServiceException("name is Null!");
        }
    }

    public GroupUnion toGroupUnion() {
//        this.check();

        GroupUnion groupUnion = new GroupUnion();
        groupUnion.setGroupId(this.groupId);
        groupUnion.setName(this.name);
        groupUnion.setIntro(this.intro);
        groupUnion.setLogoPicUrl(this.logoPicUrl);
        return groupUnion;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getLogoPicUrl() {
        return logoPicUrl;
    }

    public void setLogoPicUrl(String logoPicUrl) {
        this.logoPicUrl = logoPicUrl;
    }
}
