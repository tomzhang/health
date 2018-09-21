package com.dachen.health.app.service;

import com.dachen.health.app.entity.po.App;

public interface IAppService {
	public App getAppVersion(String appCode);
}
