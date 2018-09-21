package com.dachen.health.commons.service;

import java.util.List;

import com.mongodb.BasicDBObject;
import com.dachen.health.commons.vo.AreaVO;
import com.dachen.health.commons.vo.OptionVO;

public interface AdminManager {

	BasicDBObject getConfig();
}
