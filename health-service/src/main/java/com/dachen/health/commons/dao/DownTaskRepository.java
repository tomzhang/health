package com.dachen.health.commons.dao;

import java.util.List;

import com.dachen.health.commons.entity.DownTask;

public interface DownTaskRepository {
	
	String sava(DownTask dt);
	
	/**
	 * 获取所有小于上传失败状态的情况
	 * @param dt
	 * @return
	 */
	List<DownTask> findAllTaskToDown(DownTask dt);
	
	boolean updateDownTask(DownTask dt);
	
	DownTask getDownTaskByUrl(String url);
	
	DownTask getDownTaskByRecordId(String recordId);
	
}
