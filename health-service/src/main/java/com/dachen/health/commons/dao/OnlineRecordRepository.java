package com.dachen.health.commons.dao;

import com.dachen.health.commons.entity.OnlineRecord;

public interface OnlineRecordRepository extends BaseRepository<OnlineRecord,String>{

	/**
	 * 根据医生集团查找最后一条上线记录
	 * @param groupDoctorId
	 * @return
	 */
	public OnlineRecord findLastOneByGroupDoctor(String groupDoctorId);
	
	
	public OnlineRecord update(OnlineRecord entity);
	
	/**
	 * 根据医生集团查询当月已值班时间
	 * @param groupDoctorId
	 * @return
	 */
	public Long findDurationOfOnDuty(String groupDoctorId);
	
}
