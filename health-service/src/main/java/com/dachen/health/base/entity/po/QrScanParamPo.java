package com.dachen.health.base.entity.po;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

/**
 * @author wangl
 * @desc
 * @date:2017/9/2914:36
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Entity(value = "t_qr_scan_param", noClassnameStored = true)
public class QrScanParamPo {

    @Id
    public String id;
    public Long createTime;
    public String url;
}
