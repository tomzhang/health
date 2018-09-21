package com.dachen.health.controller.api.pack;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.dachen.commons.JSONMessage;
import com.dachen.health.pack.pack.entity.po.Pack;
import com.dachen.health.pack.pack.service.IPackService;
import com.dachen.web.ApiBaseController;

@RestController
@RequestMapping("/api/pack")
public class ApiPackController extends ApiBaseController {

	@Resource
	protected IPackService packService;

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public JSONMessage findById(@PathVariable Integer id) {
		String tag = "{id}";
		logger.info("{}. id={}", tag, id);

		Pack pack = packService.getPack(id);
		if (null == pack) {
			return JSONMessage.failure("套餐不存在");
		}

		return JSONMessage.success(null, pack);
	}

	@Deprecated
	@RequestMapping(value = "findByDoctorIdAndPackType", method = RequestMethod.GET)
	public JSONMessage findByDoctorIdAndPackType(Integer docId, Integer packType) {
		String tag = "findByDoctorIdAndPackType";
		logger.info("{}. docId={}, packType={}", tag, docId, packType);

		List<Pack> packs = packService.findByDoctorIdAndPackType(docId, packType);
		if (CollectionUtils.isEmpty(packs)) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, packs);
	}
	
	@RequestMapping(value = "findCarePlanIdListByDoctor", method = RequestMethod.GET)
	public JSONMessage findCarePlanIdListByDoctor(Integer doctorId) {
		String tag = "findCarePlanIdListByDoctor";
		logger.info("{}. doctorId={}", tag, doctorId);

		List<String> carePlanIdList = packService.findCarePlanIdListByDoctor(doctorId);
		if (CollectionUtils.isEmpty(carePlanIdList)) {
			return JSONMessage.success();
		}

		return JSONMessage.success(null, carePlanIdList);
	}
	
	@RequestMapping(value = "ifAdded", method = RequestMethod.GET)
	public JSONMessage ifAdded(@RequestParam(required=true)Integer doctorUserId, @RequestParam(required=true)String carePlanId, String carePlanSourceId) {
		String tag = "ifAdded";
		logger.info("{}. doctorUserId={}, carePlanId={}, carePlanSourceId={}", tag, doctorUserId, carePlanId, carePlanSourceId);

		boolean ret =packService.ifAdded(doctorUserId, carePlanId, carePlanSourceId);
		
		return JSONMessage.success(null, ret);
	}
}
