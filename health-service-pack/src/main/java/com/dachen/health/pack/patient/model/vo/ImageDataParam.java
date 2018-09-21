package com.dachen.health.pack.patient.model.vo;

import java.util.List;

/**
 * Created by fuyongde on 2016/12/21.
 */
public class ImageDataParam {
    private List<Integer> cureRecordIds;
    private Integer type;

    public List<Integer> getCureRecordIds() {
        return cureRecordIds;
    }

    public void setCureRecordIds(List<Integer> cureRecordIds) {
        this.cureRecordIds = cureRecordIds;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
