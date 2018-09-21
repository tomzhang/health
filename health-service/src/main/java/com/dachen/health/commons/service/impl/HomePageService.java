package com.dachen.health.commons.service.impl;

import com.dachen.commons.exception.ServiceException;
import com.dachen.health.commons.dao.HomePageModuleRepository;
import com.dachen.health.commons.vo.HomepageModuleConfigure;
import com.dachen.util.ReqUtil;
import com.dachen.util.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by wangjin on 2017/12/19.
 */
@Service
public class HomePageService {

    @Autowired
    HomePageModuleRepository homePageModuleRepository;

    public List<HomepageModuleConfigure> getModuleConfigures(String currentVersion) {
    	if(Objects.isNull(currentVersion)){
    		throw new ServiceException("版本号为空");
    	}
    	List<HomepageModuleConfigure> modules = homePageModuleRepository.getModuleConfigures();
    	filterVersion(modules,currentVersion);
        return modules;
    }

    
    private void filterVersion(List<HomepageModuleConfigure> modules,String currentVersion) {
        String version = ReqUtil.getVersion();
        for (Iterator<HomepageModuleConfigure> it = modules.iterator(); it.hasNext();) {
        	HomepageModuleConfigure module = it.next();
            if (StringUtils.isNotBlank(version) && StringUtils.isNotBlank(module.getMinmumVersion())
                && compareTo(StringUtils.split(version, "."), StringUtils.split(module.getMinmumVersion(), ".")) < 0) {
                it.remove();
            }
        }
    }
    
    
    //版本比较
    private int compareTo(String a[], String b[]) {
        int len1 = a.length;
        int len2 = b.length;
        int max = Math.max(len1, len2);
        int k = 0;
        while (k < max) {
            String str1 = k > (len1 - 1) ? "0" : a[k];
            String str2 = k > (len2 - 1) ? "0" : b[k];
            Integer v1, v2;
            try {
                v1 = Integer.parseInt(str1);
            } catch (Exception e) {
                v1 = 0;
            }
            try {
                v2 = Integer.parseInt(str2);
            } catch (Exception e) {
                v2 = 0;
            }
            int t = v1.compareTo(v2);
            if (t != 0) {
                return t;
            }
            k++;
        }
        return 0;
    }
}
