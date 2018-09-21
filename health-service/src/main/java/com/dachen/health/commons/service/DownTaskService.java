package com.dachen.health.commons.service;

import java.util.List;

import com.dachen.health.commons.entity.DownTask;

public interface DownTaskService {
	void save(DownTask dt);
	List<DownTask> getAllToDown(DownTask dt);
	
	DownTask getDownTaskByUrl(String url);
	
	/**
	 * 根据录音ID查找对应下载对象
	 * @param recordId
	 * @return
	 */
	DownTask getDownTaskByRecordId(String recordId);
	
	
	void downAndUpdate(DownTask task);
}
