package com.dachen.health.openApi.dao;

import com.dachen.health.openApi.entity.ThirdApp;

/**
 * @author liangcs
 * @desc
 * @date:2017/5/2 21:09
 * @Copyright (c) 2017, DaChen All Rights Reserved.
 */
public interface IThirdAppDAO {

    ThirdApp findByAppId(String appId);

    ThirdApp save(ThirdApp thirdApp);

}
