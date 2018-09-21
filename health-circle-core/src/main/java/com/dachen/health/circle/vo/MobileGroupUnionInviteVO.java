package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.BaseGroupUnionApplyOrInvite;

import java.util.Objects;

public class MobileGroupUnionInviteVO extends BaseMobileGroupUnionApplyOrInviteVO{

    private String inviteId;

    public MobileGroupUnionInviteVO() {
    }

    public MobileGroupUnionInviteVO(BaseGroupUnionApplyOrInvite base) {
        super(base);
        this.inviteId = Objects.toString(base.getId(), null);
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }


}
