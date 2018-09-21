package com.dachen.health.group.group.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dachen.health.group.common.util.GroupUtil;
import com.dachen.health.group.group.dao.IGroupDao;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IPlatformDoctorDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.entity.po.PlatformDoctor;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.health.group.group.service.IGroupQuartzService;
import com.dachen.health.group.group.service.IPlatformDoctorService;
import com.dachen.im.server.enums.EventEnum;
import com.dachen.util.StringUtil;

/**
 * 集团quartz服务类
 * 
 * @author pijingwei
 * @date 2015/8/7
 * 
 * @deprecated quartz调度全部改成了Jesque定时任务
 */
@Service
public class GroupQuartzServiceImpl implements IGroupQuartzService {
	Logger logger = LoggerFactory.getLogger(GroupQuartzServiceImpl.class);

	@Autowired
	protected IGroupDao groupDao;

	@Autowired
    protected IGroupDoctorDao gdocDao;
	
	@Autowired
    protected IPlatformDoctorDao pdocDao;
	
	@Autowired
    protected IPlatformDoctorService pdocService;

	@Autowired
    protected IGroupDoctorService groupDoctorService;
	
	
	private final long limitMillis = 2 * 60 * 60 * 1000; // 两小时

	/* 
	 * 检要所有集团的值班结束时间，当到了值班结束时间，则让该集团里的全部医生停止值班。
	 */
	@Override
	public void executeOffline() {

		// 获得所有集团，不是获得所有公司。
		List<Group> groupList = groupDao.findAll();
		
		if (groupList == null || groupList.isEmpty())
			return;

		for (Group group : groupList) {

			if (!isGroupDutyOnline(group)) {//两小时强制下线移至了JobTaskUtil.doctorOffline
				// 集团医生在线超过2小时强制下线
				groupDoctorOffline(group);
				// 平台医生在线超过2小时强制下线
				platformDoctorOffline(group);
			} else {
				// 当前时间不在集团值班时间内，则强制全部医生停止值班。
				groupOffline(group);
			}
		}
	}
	
	private void groupDoctorOffline(Group group) {
		
		List<GroupDoctor> gdocList = gdocDao.findGroupDoctorsByGroupId(group.getId());
		
		if (gdocList == null || gdocList.isEmpty())
			return;
		
		for (GroupDoctor gdoc : gdocList) {
			
			if (gdoc.getOnLineState() != null && "1".equals(gdoc.getOnLineState())) { // 判断医生是否线值班
				
				if (isOnlineTimeExceed(gdoc.getOnLineTime(), limitMillis)) { // 医生是否上线值班超过两小时？
					// 执行 - 医生值班结束。
					try {
						groupDoctorService.doctorOffline(gdoc, EventEnum.DOCTOR_OFFLINE_SYSTEM_FORCE);
					} catch (Exception e) {
						logger.error(e.getMessage());
						continue;
					}
				}
			}
		}
	}
	
	private void platformDoctorOffline(Group group) {
		if (!GroupUtil.PLATFORM_ID.equals(group.getId()))
			return;
		
		List<PlatformDoctor> pdocList = pdocDao.getByGroupId(group.getId());
		
		for (PlatformDoctor pdoc : pdocList) {
			
			if (pdoc.getOnLineState() != null && "1".equals(pdoc.getOnLineState())) {
				
				if (isOnlineTimeExceed(pdoc.getOnLineTime(), limitMillis)) { // 医生是否上线值班超过两小时？
					// 执行 - 医生值班结束。
					try {
						pdocService.doctorOffline(pdoc, EventEnum.DOCTOR_OFFLINE_SYSTEM_FORCE);
					} catch (Exception e) {
						logger.error(e.getMessage());
						continue;
					}
				}
			}
		}
	}
	
	private void groupOffline(Group group) {
		List<GroupDoctor> gdocList = gdocDao.findGroupDoctorsByGroupId(group.getId());

		if (gdocList == null || gdocList.isEmpty())
			return;

		for (GroupDoctor gdoc : gdocList) {
			// 执行 - 医生值班结束。
			try {
				groupDoctorService.doctorOffline(gdoc, EventEnum.DOCTOR_OFFLINE_SYSTEM_FORCE);
			} catch (Exception e) {
				logger.error(e.getMessage());
				continue;
			}
		}
	}
	
	/**
	 * 当前时间是否在集团可值班时间
	 * @param group
	 * @return
	 */
	private boolean isGroupDutyOnline(Group group) {

		String dutyStartTime = GroupUtil.GROUP_DUTY_START_TIME;
		String dutyEndTime = GroupUtil.GROUP_DUTY_END_TIME;
		if (GroupUtil.PLATFORM_ID.equals(group.getId())) {
			dutyStartTime = null;
			dutyEndTime = null;
		} else if (group.getConfig() != null) {
			if (StringUtil.isNoneBlank(group.getConfig().getDutyStartTime())) {
				dutyStartTime = group.getConfig().getDutyStartTime();
			}
			if (StringUtil.isNoneBlank(group.getConfig().getDutyEndTime())) {
				dutyEndTime = group.getConfig().getDutyEndTime();
			}
		}
		return GroupUtil.isCanOnDuty(dutyStartTime, dutyEndTime);
	}

	/**
	 * 医生是否上线值班超过两小时？
	 * 
	 * @param onlineTime  上线时间（单位：毫秒）
	 * @param limitMillis 医生上线时长（单位：毫秒）
	 * @return
	 */
	private boolean isOnlineTimeExceed(Long onlineTime, long limitMillis) {
		if (onlineTime == null) {
			return false;
		}
		long times = System.currentTimeMillis() - onlineTime;
		if (times > limitMillis) {
			return true;
		}
		return false;
	}
	
}
