/**
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
package com.dachen.health.system.entity.param;

import com.dachen.commons.page.PageVO;

/**
 * @author longjh
 *     date:2018年4月12日
 * @desc
 * 
 */
public class DoctorNameParam extends PageVO {
    /* 姓名 */
    private String name;
    /* 是否审核 */
    private Integer status;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
}
