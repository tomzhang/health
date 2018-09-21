package com.dachen.health.group.common.dao.impl;

import org.springframework.stereotype.Repository;

import com.dachen.commons.support.nosql.NoSqlRepository;
import com.dachen.health.group.common.dao.ICommonDao;

/**
 * 医生基本信息dao 实现类
 * @author wangqiao 重构
 * @date 2016年4月22日
 */
@Repository
@Deprecated
public class CommonDaoImpl extends NoSqlRepository implements ICommonDao {
}
