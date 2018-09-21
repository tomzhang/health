package com.dachen.health.circle.vo;

import com.dachen.health.circle.entity.BaseGroupUnionApplyOrInvite;
import com.dachen.health.circle.entity.GroupUnionApply;

import java.util.Objects;

public class MobileGroupUnionApplyVO extends BaseMobileGroupUnionApplyOrInviteVO {

    private String applyId;

    public MobileGroupUnionApplyVO() {
    }

    public MobileGroupUnionApplyVO(BaseGroupUnionApplyOrInvite base) {
        super(base);
        this.applyId = Objects.toString(base.getId(), null);
        GroupUnionApply apply = (GroupUnionApply) base;
    }

    public String getApplyId() {
        return applyId;
    }

    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

}
