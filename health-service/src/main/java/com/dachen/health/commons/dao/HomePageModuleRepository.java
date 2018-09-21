package com.dachen.health.commons.dao;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.commons.vo.HomepageModuleConfigure;

import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wangjin on 2017/12/19.
 */
@Repository
public class HomePageModuleRepository extends NoSqlRepository {

	public List<HomepageModuleConfigure> getModuleConfigures(){
        Query<HomepageModuleConfigure> query = dsForRW.createQuery("homepage_module_configure",HomepageModuleConfigure.class).filter("isShow", 1);
        query.order("sort");
        return query.asList();
    }

   
}
