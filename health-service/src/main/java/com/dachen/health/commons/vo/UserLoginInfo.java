package com.dachen.health.commons.vo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.utils.IndexDirection;

/**
 * @author cuizhiquan
 * @Description 记录用户登录时的手机信息
 * @date 2017/11/24 19:08
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
@Data
@Entity(value = "t_user_login_info", noClassnameStored = true)
public class UserLoginInfo {

    @Id
    private String id;

    /**
     * '
     * 登录时间
     */
    private Long loginTime;

    /**
     * 手机号
     */
    @Indexed(value = IndexDirection.DESC, name = "telephone_index")
    private String telephone;

    /**
     * 手机型号
     */
    private String phoneModel;

    /**
     * ROM版本
     */
    private String romVersion;

    /**
     * App版本
     */
    private String version;

    /**
     * 设备号
     */
    private String serial;

}
