package com.dachen.health.controller.api.user;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.commons.dao.UserRepository;
import com.dachen.health.commons.vo.CarePlanDoctorVO;
import com.dachen.health.commons.vo.User;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/user")
public class ApiUserController extends ApiBaseController {

	@Resource
	protected UserRepository userRepository;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable Integer id) {
		String tag = "{id}";
		logger.info("{}. id={}", tag, id);
		User user = userRepository.getUser(id);
		if (null == user) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, user);
	}
	
	@RequestMapping(value = "findByIds", method = RequestMethod.GET)
	public JSONMessage findByIds(Integer[] ids) {
		String tag = "findByIds";
		logger.info("{}. ids={}", tag, ids);
		if (null == ids || 0 == ids.length) {
			return JSONMessage.success();
		}
		
		List<User> list = userRepository.getUsers(Arrays.asList(ids));
		if (null == list || 0 == list.size()) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, list);
	}

	@RequestMapping(value = "findUserByDocs", method = RequestMethod.GET)
	public JSONMessage findUserByDocs(Integer[] doctorIds, Integer status) {
		String tag = "findUserByDocs";
		logger.info("{}. doctorIds={}, status={}", tag, doctorIds, status);

		List<CarePlanDoctorVO> carePlanDoc = userRepository.findUserByDocs(Arrays.asList(doctorIds), status);
		if (CollectionUtils.isEmpty(carePlanDoc)) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, carePlanDoc);
	}
}
