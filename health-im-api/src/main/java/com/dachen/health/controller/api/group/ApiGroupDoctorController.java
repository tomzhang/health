package com.dachen.health.controller.api.group;

import java.util.List;

import javax.annotation.Resource;

import com.dachen.health.api.client.group.entity.CGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.group.dao.IGroupDoctorDao;
import com.dachen.health.group.group.dao.IGroupSearchDao;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.entity.po.GroupDoctor;
import com.dachen.health.group.group.service.IGroupDoctorService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/group/doctor")
public class ApiGroupDoctorController extends ApiBaseController {

	@Resource
	protected IGroupDoctorDao groupDoctorDao;
	
	@Resource
	protected IGroupDoctorService groupDoctorService;
	
	@Autowired
	protected IGroupSearchDao groupSearchDao;

	@RequestMapping(value = "findByUK", method = RequestMethod.GET)
	public JSONMessage findByUK(String groupId, Integer doctorId) {
		String tag = "findByUK";
		logger.info("{}. groupId={}, doctorId={}", tag, groupId, doctorId);

		GroupDoctor gdoc = new GroupDoctor();
		gdoc.setGroupId(groupId);
		gdoc.setDoctorId(doctorId);
		GroupDoctor dbGd = groupDoctorDao.getById(gdoc);
		if (null == dbGd) {
			return JSONMessage.success();
		}
		return JSONMessage.success(null, dbGd);
	}
	
	@RequestMapping(value = "exists", method = RequestMethod.GET)
	public JSONMessage exists(String groupId, Integer doctorId) {
		String tag = "exists";
		logger.info("{}. groupId={}, doctorId={}", tag, groupId, doctorId);

		GroupDoctor gdoc = new GroupDoctor();
		gdoc.setGroupId(groupId);
		gdoc.setDoctorId(doctorId);
		GroupDoctor dbGd = groupDoctorDao.getById(gdoc);
		if (null == dbGd) {
			return JSONMessage.success(null, false);
		}
		return JSONMessage.success(null, true);
	}
	
	@RequestMapping(value = "findDoctorGroupName/{doctorId}", method = RequestMethod.GET)
	public JSONMessage findDoctorGroupName(@PathVariable Integer doctorId) {
		String tag = "findDoctorGroupName/{doctorId}";
		logger.info("{}. doctorId={}", tag, doctorId);
		
		String name = groupSearchDao.findDoctorGroupName(doctorId);
		
		return JSONMessage.success(null, name);
	}
	
	/**
	 * 获取医生所在的活跃集团的ids
	 * @param doctorId
	 * @return
	 */
	@RequestMapping(value = "getActiveGroupIdListByDoctor/{doctorId}", method = RequestMethod.GET)
	public JSONMessage getActiveGroupIdListByDoctor(@PathVariable Integer doctorId) {
		String tag = "getActiveGroupIdListByDoctor/{doctorId}";
		logger.info("{}. doctorId={}", tag, doctorId);
		
		List<String> groupIds = groupDoctorService.getActiveGroupIdListByDoctor(doctorId);
		if (CollectionUtils.isEmpty(groupIds)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, groupIds);
	}
	
	/**
	 * 获取医生所在的活跃集团的列表
	 * @param doctorId
	 * @return
	 */
	@RequestMapping(value = "getActiveGroupListByDoctor/{doctorId}", method = RequestMethod.GET)
	public JSONMessage getActiveGroupListByDoctor(@PathVariable Integer doctorId) {
		String tag = "getActiveGroupListByDoctor/{doctorId}";
		logger.info("{}. doctorId={}", tag, doctorId);
		
		List<CGroup> groups = groupDoctorService.getActiveCGroupListByDoctor(doctorId);
		if (CollectionUtils.isEmpty(groups)) {
			return JSONMessage.success();
		}
		
		return JSONMessage.success(null, groups);
	}
}
