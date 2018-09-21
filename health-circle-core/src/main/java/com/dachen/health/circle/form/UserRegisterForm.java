package com.dachen.health.circle.form;

import com.dachen.commons.exception.ServiceException;
import com.dachen.util.StringUtil;
import java.util.Objects;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.context.annotation.Scope;

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
    //运营活动Id
    @NotBlank(message = "运营活动Id不能为空")
    private String activityId;
    //邀请人Id
    private Integer inviterId;
    //来源子系统（医生圈-17、药企圈-16）
    private Integer subsystem;
    //邀请方式（短信-sms，微信-wechat，二维码-qrcode）
    @NotBlank(message = "邀请方式不能为空")
    private String way;

    public void check() {
        if (Objects.isNull(inviterId)){
            throw new ServiceException("邀请人Id不能为空");
        }
        if (Objects.isNull(subsystem)){
            throw new ServiceException("来源子系统不能为空");
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

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
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
}
