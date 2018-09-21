package com.dachen.health.commons.dao;

import java.util.List;

import com.dachen.health.commons.vo.Tips;

public interface TipsRepository {

	List<Tips> findAll();

}
