package com.dachen.health.activity.invite.form;

import com.dachen.commons.exception.ServiceException;
import com.dachen.util.StringUtil;
import java.util.Objects;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/23 20:16 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope(value = "prototype")
public class UserRegisterForm {
    //注册手机号
    @NotBlank(message = "注册手机号不能为空")
    private String telephone;
    //注册姓名
    @NotBlank(message = "注册姓名不能为空")
    private String name;
    //注册密码
    @NotBlank(message = "注册密码不能为空")
    private String password;
    //邀请活动Id
    @NotBlank(message = "邀请活动Id不能为空")
    private String inviteActivityId;
    //注册活动Id
    @NotBlank(message = "注册活动Id不能为空")
    private String registerActivityId;
    //邀请人Id
    private Integer inviterId;
    //来源子系统（医生圈-17、药企圈-16）
    private Integer subsystem;
    //邀请方式（短信-sms，微信-wechat，二维码-qrcode）
    @NotBlank(message = "邀请方式不能为空")
    private String way;
    //集团Id
    private String groupId;
    /**
     * 是否是科室邀请注册的医生
     */
    private Boolean deptInvitation;

    public void check() {
        if (StringUtil.isBlank(telephone)){
            throw new ServiceException("注册手机号不能为空");
        }
        if (StringUtil.isBlank(name)){
            throw new ServiceException("注册姓名不能为空");
        }
        if (StringUtil.isBlank(password)){
            throw new ServiceException("注册密码不能为空");
        }
        if (Objects.isNull(inviterId)){
            throw new ServiceException("邀请人Id不能为空");
        }
        if (Objects.isNull(subsystem)){
            throw new ServiceException("来源子系统不能为空");
        }
        if (StringUtil.isBlank(way)){
            throw new ServiceException("邀请方式不能为空");
        }
    }

    public void checkForJoinDept() {
        if (StringUtil.isBlank(telephone)){
            throw new ServiceException("注册手机号不能为空");
        }
        if (Objects.isNull(inviterId)){
            throw new ServiceException("邀请人Id不能为空");
        }
        if (Objects.isNull(subsystem)){
            throw new ServiceException("来源子系统不能为空");
        }
        if (StringUtil.isBlank(way)){
            throw new ServiceException("邀请方式不能为空");
        }
        if (StringUtil.isBlank(groupId)){
            throw new ServiceException("科室Id不能为空");
        }
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getInviteActivityId() {
        return inviteActivityId;
    }

    public void setInviteActivityId(String inviteActivityId) {
        this.inviteActivityId = inviteActivityId;
    }

    public String getRegisterActivityId() {
        return registerActivityId;
    }

    public void setRegisterActivityId(String registerActivityId) {
        this.registerActivityId = registerActivityId;
    }

    public Integer getInviterId() {
        return inviterId;
    }

    public void setInviterId(Integer inviterId) {
        this.inviterId = inviterId;
    }

    public Integer getSubsystem() {
        return subsystem;
    }

    public void setSubsystem(Integer subsystem) {
        this.subsystem = subsystem;
    }

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Boolean getDeptInvitation() {
        return deptInvitation;
    }

    public void setDeptInvitation(Boolean deptInvitation) {
        this.deptInvitation = deptInvitation;
    }
}
