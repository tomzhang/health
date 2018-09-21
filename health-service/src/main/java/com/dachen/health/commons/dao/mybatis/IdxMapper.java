package com.dachen.health.commons.dao.mybatis;

import com.dachen.health.commons.vo.IdxVO;

public interface IdxMapper {

	int insert(IdxVO idxVO);

	int insertDoctor(IdxVO idxVO);
	
	int insertDoctorPrefix(IdxVO idxVO);
	
	void resetDoctor();
	
	IdxVO selectDoctorPrefix();
}
