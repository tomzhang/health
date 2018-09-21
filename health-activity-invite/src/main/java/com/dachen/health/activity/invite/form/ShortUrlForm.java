package com.dachen.health.activity.invite.form;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.activity.invite.enums.InviteEnum.QrCodeTypeEnum;
import com.dachen.util.StringUtil;
import org.springframework.context.annotation.Scope;

/**
 * @author 钟良
 * @desc
 * @date:2017/5/31 14:09 Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Scope("prototype")
public class ShortUrlForm {
    /**
     * 类型, dept表示科室，doctor表示医生
     * @see com.dachen.health.activity.invite.enums.InviteEnum.QrCodeTypeEnum
     */
    private String type;
    //类型id，dept时为科室id，doctor时为医生Id
    private String typeId;

    private String inviteActivityId;//邀请活动Id
    private String registerActivityId;//注册活动Id
    /**
     * 邀请方式（短信-sms，微信-wechat，二维码-qrcode）
     * @see com.dachen.health.commons.constants.UserEnum.InviteWayEnum
     */
    private String way;

    private Integer doctorId;//医生Id

    private String groupId;//集团Id

    public void check() {
        if (StringUtil.isBlank(type)) {
            throw new ServiceException("类型不能为空");
        }
        if (StringUtil.isBlank(typeId)) {
            throw new ServiceException("类型Id不能为空");
        }
        if (StringUtil.isBlank(way)) {
            throw new ServiceException("邀请方式不能为空");
        }
        if (QrCodeTypeEnum.Doctor.getId().equals(type)) {//type为医生
            if (StringUtil.isBlank(inviteActivityId)) {
                throw new ServiceException("邀请活动Id不能为空");
            }
            if (StringUtil.isBlank(registerActivityId)) {
                throw new ServiceException("注册活动Id不能为空");
            }
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
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

    public String getWay() {
        return way;
    }

    public void setWay(String way) {
        this.way = way;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
