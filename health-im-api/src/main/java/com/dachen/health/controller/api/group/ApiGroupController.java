package com.dachen.health.controller.api.group;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.group.group.entity.po.Group;
import com.dachen.health.group.group.service.IGroupService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/group")
public class ApiGroupController extends ApiBaseController {

	@Resource
	protected IGroupService groupService;

	@RequestMapping(value = "findById/{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable String id) {
		String tag = "findById/{id}";
		logger.info("{}. id={}", tag, id);

		Group group = groupService.getGroupById(id);
		if (null == group) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, group);
	}

}
