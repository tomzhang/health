package com.dachen.health.group.group.service;


/**
 * 集团quartz服务类
 * 
 * @author pijingwei
 * @date 2015/8/7
 */
public interface IGroupQuartzService {

	/**
	 * 检要所有集团的值班结束时间，当到了值班结束时间，则让该集团里的全部医生停止值班。
	 * 
	 */
	public void executeOffline();

}
