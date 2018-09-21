package com.dachen.health.app.service.impl;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import com.alibaba.fastjson.JSON;
import com.dachen.commons.exception.ServiceException;
import com.dachen.health.app.entity.po.App;
import com.dachen.health.app.service.IAppService;
import com.dachen.util.ReqUtil;

@Service
public class AppServiceImpl implements IAppService {
	private static final String PROPERTIES_APP_VERSION_JSON = "/properties/app_version.json";
	private String content = null;
	@Override
	public App getAppVersion(String appCode){
		try {
			getFileContent();
		} catch (IOException e) {
			throw new ServiceException("读取版本文件失败");
		}
		List<App> appList = JSON.parseArray(content, App.class);
		for(App app : appList){
			if (StringUtils.equalsIgnoreCase(app.getCode(), appCode) && 
					StringUtils.equalsIgnoreCase(app.getDevice(), ReqUtil.instance.getAccessFlagString())){
				return app;
			}
		}
		return null;
	}
	
	public void getFileContent() throws IOException {
		Resource r = (Resource) new ClassPathResource(PROPERTIES_APP_VERSION_JSON);
        EncodedResource enc = new EncodedResource(r);
        content = FileCopyUtils.copyToString(enc.getReader());
	}
}
